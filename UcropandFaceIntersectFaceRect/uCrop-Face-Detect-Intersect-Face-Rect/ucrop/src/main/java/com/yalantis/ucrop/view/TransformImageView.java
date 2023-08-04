package com.yalantis.ucrop.view;

import static com.yalantis.ucrop.UCropActivity.uCropActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.yalantis.ucrop.util.ColorFilterGenerator;
import com.yalantis.ucrop.util.FastBitmapDrawable;
import com.yalantis.ucrop.util.RectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 * <p/>
 * This class provides base logic to setup the image, transform it with matrix (move, scale, rotate),
 * and methods to get current matrix state.
 */
public class TransformImageView extends AppCompatImageView {

    private static final String TAG = "TransformImageView";

    private static final int RECT_CORNER_POINTS_COORDS = 8;
    private static final int RECT_CENTER_POINT_COORDS = 2;
    private static final int MATRIX_VALUES_COUNT = 9;

    protected final float[] mCurrentImageCorners = new float[RECT_CORNER_POINTS_COORDS];
    protected final float[] mCurrentImageCenter = new float[RECT_CENTER_POINT_COORDS];

    private final float[] mMatrixValues = new float[MATRIX_VALUES_COUNT];

    public static Matrix mCurrentImageMatrix = new Matrix();
    public static Matrix mCurrentSqureImageMatrix = new Matrix();
    protected int mThisWidth, mThisHeight;

    protected TransformImageListener mTransformImageListener;

    private float[] mInitialImageCorners;
    private float[] mInitialImageCenter;

    protected boolean mBitmapDecoded = false;
    protected boolean mBitmapLaidOut = false;

    private int mMaxBitmapSize = 0;

    private float mBrightness = 0;
    private float mContrast = 0;
    private float mSaturation = 0;
    public static ArrayList<Rect> faceRectsNew = new ArrayList<>(); // List to store RectF bounds of each face
    public static ArrayList<Rect> faceRectsActual = new ArrayList<>(); // List to store RectF bounds of each face
    public static ArrayList<Rect> faceRectsDummy = new ArrayList<>(); // List to store RectF bounds of each face
    public static ArrayList<Rect> faceRectsTop = new ArrayList<>(); // List to store RectF bounds of each face


    public static Rect faceRectFocus = new Rect();
    public static Rect faceRectFocusAfterPadding = new Rect();

    private Allocation mInAllocation;
    private Allocation mOutAllocation;
    private ScriptIntrinsicConvolve3x3 mSharpnessScript;
    private SharpnessScriptTask mSharpnessScriptTask;

    private float mSharpness = 0;
    public static Rect faceRect = new Rect();
    private String mImageInputPath, mImageOutputPath;
    private ExifInfo mExifInfo;
    Bitmap resultBitmap;
    public static Face faceCenetered;
    int area1;
    int area2;

    /**
     * Interface for rotation and scale change notifying.
     */
    public interface TransformImageListener {

        void onLoadComplete();

        void onLoadFailure(@NonNull Exception e);

        void onRotate(float currentAngle);

        void onScale(float currentScale);

        void onBrightness(float currentBrightness);

        void onContrast(float currentContrast);

        void onSaturation(float currentSaturation);

        void onSharpness(float currentSharpness);
    }

    public TransformImageView(Context context) {
        this(context, null);
    }

    public TransformImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransformImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setTransformImageListener(TransformImageListener transformImageListener) {
        mTransformImageListener = transformImageListener;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType);
        } else {
            Log.w(TAG, "Invalid ScaleType. Only ScaleType.MATRIX can be used");
        }
    }

    /**
     * Setter for {@link #mMaxBitmapSize} value.
     * Be sure to call it before {@link #setImageURI(Uri)} or other image setters.
     *
     * @param maxBitmapSize - max size for both width and height of bitmap that will be used in the view.
     */
    public void setMaxBitmapSize(int maxBitmapSize) {
        mMaxBitmapSize = maxBitmapSize;
    }

    public int getMaxBitmapSize() {
        if (mMaxBitmapSize <= 0) {
            mMaxBitmapSize = BitmapLoadUtils.calculateMaxBitmapSize(getContext());
        }
        return mMaxBitmapSize;
    }

    @Override
    public void setImageBitmap(final Bitmap bitmap) {

        setImageDrawable(new FastBitmapDrawable(bitmap));

    }


    public void autoScaleToFaceRect(Rect faceRect, Bitmap bitmap) {
        // Get the dimensions of the ImageView and the faceRect
        int imageViewWidth = getWidth();
        int imageViewHeight = getHeight();
        int faceRectWidth = faceRect.width();
        int faceRectHeight = faceRect.height();

        // Calculate the scaling factor required to fit the faceRect within the ImageView bounds
        float scaleX = (float) imageViewWidth / faceRectWidth;
        float scaleY = (float) imageViewHeight / faceRectHeight;
        float scale = Math.min(scaleX, scaleY);

        // Calculate the translation required to center the faceRect within the ImageView bounds
        float dx = (imageViewWidth - faceRectWidth * scale) / 2.0f - faceRect.left * scale;
        float dy = (imageViewHeight - faceRectHeight * scale) / 2.0f - faceRect.top * scale;

        // Set the image scale and translation to fit the faceRect within the ImageView bounds
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        mCurrentImageMatrix.set(matrix);
    }

    public String getImageInputPath() {
        return mImageInputPath;
    }

    public String getImageOutputPath() {
        return mImageOutputPath;
    }

    public ExifInfo getExifInfo() {
        return mExifInfo;
    }

    /**
     * This method takes an Uri as a parameter, then calls method to decode it into Bitmap with specified size.
     *
     * @param imageUri - image Uri
     * @throws Exception - can throw exception if having problems with decoding Uri or OOM.
     */
    public void setImageUri(@NonNull Uri imageUri, @Nullable Uri outputUri) throws Exception {
        int maxBitmapSize = getMaxBitmapSize();

        BitmapLoadUtils.decodeBitmapInBackground(getContext(), imageUri, outputUri, maxBitmapSize, maxBitmapSize, new BitmapLoadCallback() {

            @Override
            public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
                mImageInputPath = imageInputPath;
                mImageOutputPath = imageOutputPath;
                mExifInfo = exifInfo;

                mBitmapDecoded = true;
                createScript(bitmap);
                Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

                // Create a new canvas with the resultBitmap
                Canvas canvas = new Canvas(resultBitmap);

                // Draw the original bitmap onto the canvas
                canvas.drawBitmap(bitmap, 0, 0, null);

                // Create a Paint object for drawing the rectangle
                Paint rectPaint = new Paint();
                rectPaint.setColor(Color.YELLOW);
                rectPaint.setStyle(Paint.Style.STROKE);
                rectPaint.setStrokeWidth(15);

                // Define the coordinates of the top-left corner and the bottom-right corner of the rectangle
                int left = 20;
                int top = 20;
                int right = left + 400;
                int bottom = top + 400;

                // Draw the rectangle on the canvas
//                        canvas.drawRect(left, top, right, bottom, rectPaint);
                faceRect = new Rect(left, top, right, bottom);
                RectF faceRectFnew = new RectF(faceRect.left, faceRect.top, faceRect.right, faceRect.bottom);

//                        Toast.makeText(getContext().getApplicationContext(), mCurrentImageMatrix.toString(), Toast.LENGTH_SHORT).show();
                identifyTheFaces(bitmap);
//                        setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(@NonNull Exception bitmapWorkerException) {
                Log.e(TAG, "onFailure: setImageUri", bitmapWorkerException);
                if (mTransformImageListener != null) {
                    mTransformImageListener.onLoadFailure(bitmapWorkerException);
                }
            }
        });
    }

    public Rect addPaddingFromCenter(Rect faceRect, int padding) {
        int centerX = (faceRect.left + faceRect.right) / 2;
        int centerY = (faceRect.top + faceRect.bottom) / 2;

        int newLeft = centerX - faceRect.width() / 2 - padding;
        int newTop = centerY - faceRect.height() / 2 - padding;
        int newRight = centerX + faceRect.width() / 2 + padding;
        int newBottom = centerY + faceRect.height() / 2 + padding;

        System.out.println("uCrop _____" + "centerX" + centerX);
        System.out.println("uCrop _____" + "centerY" + centerY);
        System.out.println("uCrop _____" + "newLeft" + newLeft);
        System.out.println("uCrop _____" + "newTop" + newTop);
        System.out.println("uCrop _____" + "newRight" + newRight);
        System.out.println("uCrop _____" + "newBottom" + newBottom);

        return new Rect(newLeft, newTop, newRight, newBottom);
    }

    public Rect addPaddingFromCenter(Rect faceRect, int paddingInScreenPixels, float bitmapDensity) {
        // Assuming you have a method to update the zoomLevel based on user interaction
        float zoomLevel = 1.0f;

// Step 1: Get the screen density
        float screenDensity = getResources().getDisplayMetrics().density;

// Step 2: Calculate the distance in pixels from the center to the edges of the rectangle
        int distanceInPixels = (int) (paddingInScreenPixels * screenDensity / zoomLevel);

// Step 3: Calculate the new coordinates of the rectangle
        int centerX = (faceRect.left + faceRect.right) / 2;
        int centerY = (faceRect.top + faceRect.bottom) / 2;

        int newLeft = centerX - distanceInPixels;
        int newTop = centerY - distanceInPixels;
        int newRight = centerX + distanceInPixels;
        int newBottom = centerY + distanceInPixels;

        System.out.println("uCrop _____" + "centerX" + centerX);
        System.out.println("uCrop _____" + "centerY" + centerY);
        System.out.println("uCrop _____" + "newLeft" + newLeft);
        System.out.println("uCrop _____" + "newTop" + newTop);
        System.out.println("uCrop _____" + "newRight" + newRight);
        System.out.println("uCrop _____" + "newBottom" + newBottom);

        return new Rect(newLeft, newTop, newRight, newBottom);
    }

    private void identifyTheFaces(final Bitmap faceBitmap) {
        faceRectsNew.clear();
        faceRectsActual.clear();
        faceRectsDummy.clear();
        faceRectsTop.clear();

        int width = faceBitmap.getWidth();
        int height = faceBitmap.getHeight();

        System.out.println("uCrop" + "---faceBitmap--width--" + width);
        System.out.println("uCrop" + "---faceBitmap--height--" + height);


        resultBitmap = Bitmap.createBitmap(faceBitmap.getWidth(), faceBitmap.getHeight(), faceBitmap.getConfig());

        InputImage image = InputImage.fromBitmap(faceBitmap, 0);
        FaceDetector detector = FaceDetection.getClient();

        final Task<List<Face>> result = detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(List<Face> faces) {

                        if (faces.size() > 0) {
                            faceCenetered = faces.get(0);

                            Rect boundsFirstFace = faces.get(0).getBoundingBox();
                            faceRectFocus = new Rect(boundsFirstFace);

                            int padding = 200; // Set the desired padding value here
                            faceRectFocusAfterPadding = addPaddingFromCenter(faceRectFocus, padding);
                            System.out.println("uCrop " + " First faceRectFocusAfterPadding width " + faceRectFocusAfterPadding.width());
                            System.out.println("uCrop " + " First faceRectFocusAfterPadding Height" + faceRectFocusAfterPadding.height());

                        }
                        System.out.println(faces);
                        if (faces.size() == 0) {
                            uCropActivity.showPopDialog("No Face Found ");
                        } else {

                            if (faces.size() <= 5) {
                                for (Face face : faces) {

                                    // Get the bounding box of the face
                                    Rect bounds = face.getBoundingBox();
                                    // Calculate the center point of the face
                                    float centerX = bounds.centerX();
                                    float centerY = bounds.centerY();

                                    // Calculate the width and height of the rectangle
                                    float rectWidth = bounds.width() * 1.5f;
                                    float rectHeight = bounds.height() * 1.5f;

                                    // Calculate the left, top, right, and bottom coordinates of the rectangle
                                    int left = (int) (centerX - (rectWidth / 6));
                                    int top = (int) (centerY - (rectHeight / 5));
                                    int right = (int) (centerX + (rectWidth / 6));
                                    int bottom = (int) (centerY + (rectHeight / 4));

                                    int left_top = (int) (centerX - (rectWidth / 10));
                                    int top_top = (int) (centerY - (rectHeight / 3));
                                    int right_top = (int) (centerX + (rectWidth / 10));
                                    int bottom_top = (int) (centerY - (rectHeight) / 6);

                                    // Create a new Rect containing the rectangle at the center of the face
                                    Rect centerRect = new Rect(left, top, right, bottom);
                                    Rect centerRect_top = new Rect(left_top, top_top, right_top, bottom_top);
                                    faceRectsNew.add(centerRect);
                                    faceRectsTop.add(centerRect_top);
                                    if (bounds.height() > 120) {
                                        faceRectsActual.add(bounds);
                                    }
                                    System.out.println("uCrop " + "bounds size  faceRectsNew----" + bounds.height());
                                    System.out.println("uCrop " + "faceBitmap size  faceRectsNew----" + faceBitmap.getHeight() * 0.08);

                                    // Create a new canvas with the resultBitmap
                                    Canvas canvas = new Canvas(resultBitmap);

                                    // Draw the original bitmap onto the canvas
                                    canvas.drawBitmap(faceBitmap, 0, 0, null);

                                    // Create a Paint object for drawing the rectangle
                                    Paint rectPaint = new Paint();
                                    Paint rectPaintTop = new Paint();
                                    rectPaint.setColor(Color.BLUE);
                                    rectPaint.setStyle(Paint.Style.STROKE);
                                    rectPaint.setStrokeWidth(15);

                                    // Draw the rectangle   Paint rectPaint = new Paint();
                                    rectPaintTop.setColor(Color.RED);
                                    rectPaintTop.setStyle(Paint.Style.STROKE);
                                    rectPaintTop.setStrokeWidth(15);

                                    // Draw the rectangle at the center of the face
//                            canvas.drawRect(faceRectFocusAfterPadding, rectPaint);
                                    System.out.println("uCrop " + " size  faceRectsNew----" + faceRectsNew.size());
                                    System.out.println("uCrop " + " size  faceRectsNew----" + faceRectsTop.size());
                                    System.out.println("uCrop " + "size   faceRectsActual----" + faceRectsActual.size());
                                    System.out.println("uCrop " + "bounds.height   bounds.height----" + bounds.height());
//                                    canvas.drawRect(faceRectsNew.get(0), rectPaint);
//                                    canvas.drawRect(faceRectsTop.get(0), rectPaintTop);
                                }
                            }
                        }
                        System.out.println("uCrop " + " First faceRectFocusAfterPadding" + faceRectFocusAfterPadding);
                        //to show the dialog when the face size is too small to crop
                        if (faceRectsActual.size() == 0 && faces.size() > 0) {
                            uCropActivity.showPopDialog("Face are too Small to Crop");
                            System.out.println("uCrop " + "   faceRectsActual size" + faceRectsActual.size());

                        }
                        if (faces.size() == 1) {
                            if (faceRectsActual.size() > 0) {
                                System.out.println("uCrop " + "faceRectsActual.get(0).width()------)" + faceRectsActual.get(0).width());
                                System.out.println("uCrop " + "faceRectsActual.get(0).height()-----)" + faceRectsActual.get(0).height());
                                if (faceRectsActual.get(0).height() < 60) {
                                    uCropActivity.showPopDialog("Face are too Small to Crop");
                                }
                            }
                            setImageBitmap(resultBitmap);
                        } else {
                            if (faces.size() > 5) {
                                uCropActivity.showPopDialog("There are more Faces , Upload Another Image");
                            }
                        }

                        if (faceRectsActual.size() > 1) {
                            System.out.println("Rects before sorting:");
                            for (Rect rect : faceRectsActual) {
                                System.out.println("Rects before sorting:" + rect);
                            }
                            Collections.sort(faceRectsActual, new Comparator<Rect>() {
                                @Override
                                public int compare(Rect rect1, Rect rect2) {
                                    // Calculate the area of the two Rects
                                    area1 = rect1.width() * rect1.height();
                                    area2 = rect2.width() * rect2.height();
                                    System.out.println("uCrop" + rect2 + "  Rects Area2 :" + area2);
                                    System.out.println("uCrop" + rect1 + "  Rects Area1 :" + area1);
                                    System.out.println("uCrop" + rect2 + "----rect1.width():----)" + rect1.width());
                                    System.out.println("uCrop" + rect2 + "----rect2.width():----)" + rect2.width());

                                    System.out.println("uCrop" + rect2 + "---rect1.height():----)" + rect1.height());
                                    System.out.println("uCrop" + rect2 + "---rect2.height():----)" + rect2.height());

                                    // Compare the areas in descending order (bigger first, smaller last)
                                    return Integer.compare(area2, area1);
                                }
                            });
                            System.out.println("Rects before  adding  faceRectFocusAfterPadding: -------  )" + faceRectFocusAfterPadding);

                            System.out.println("Rects after adding  faceRectFocusAfterPadding:   )" + faceRectFocusAfterPadding);

                            System.out.println("Rects after sorting:");
                            for (Rect rect : faceRectsActual) {
                                System.out.println("Rects After sorting:" + rect);
                            }
                            faceRectsDummy.addAll(faceRectsActual);
                        } else {


                        }


                        setImageBitmap(resultBitmap);
                    }

                }).

                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setImageBitmap(resultBitmap);

                        Toast.makeText(getContext().getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                    }
                });
        System.out.println(faceRectsNew);
    }


    /**
     * @return - current image scale value.
     * [1.0f - for original image, 2.0f - for 200% scaled image, etc.]
     */
    public float getCurrentScale() {
        return getMatrixScale(mCurrentImageMatrix);
    }

    /**
     * This method calculates scale value for given Matrix object.
     */
    public float getMatrixScale(@NonNull Matrix matrix) {
        return (float) Math.sqrt(Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X), 2) + Math.pow(getMatrixValue(matrix, Matrix.MSKEW_Y), 2));
    }

    /**
     * @return - current image rotation angle.
     */
    public float getCurrentAngle() {
        return getMatrixAngle(mCurrentImageMatrix);
    }

    /**
     * This method calculates rotation angle for given Matrix object.
     */
    public float getMatrixAngle(@NonNull Matrix matrix) {
        return (float) -(Math.atan2(getMatrixValue(matrix, Matrix.MSKEW_X), getMatrixValue(matrix, Matrix.MSCALE_X)) * (180 / Math.PI));
    }

    /**
     * @return - current image brightness.
     */
    public float getCurrentBrightness() {
        return mBrightness;
    }

    /**
     * @return - current image contrast.
     */
    public float getCurrentContrast() {
        return mContrast;
    }

    /**
     * @return - current image saturation.
     */
    public float getCurrentSaturation() {
        return mSaturation;
    }

    /**
     * @return - current image sharpness.
     */
    public float getCurrentSharpness() {
        return mSharpness;
    }

    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
        mCurrentImageMatrix.set(matrix);
        mCurrentSqureImageMatrix.set(matrix);
//        Toast.makeText(getContext().getApplicationContext(), mCurrentSqureImageMatrix.toString(), Toast.LENGTH_SHORT).show();
        updateCurrentImagePoints();
    }

    @Nullable
    public Bitmap getViewBitmap() {
        if (getDrawable() == null || !(getDrawable() instanceof FastBitmapDrawable)) {
            return null;
        } else {
            System.out.println(((FastBitmapDrawable) getDrawable()).getBitmap());
            return ((FastBitmapDrawable) getDrawable()).getBitmap();
        }
    }

    /**
     * This method translates current image.
     *
     * @param deltaX - horizontal shift
     * @param deltaY - vertical shift
     */
    public void postTranslate(float deltaX, float deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            mCurrentImageMatrix.postTranslate(deltaX, deltaY);
            setImageMatrix(mCurrentImageMatrix);


        }
    }

    /**
     * This method scales current image.
     *
     * @param deltaScale - scale value
     * @param px         - scale center X
     * @param py         - scale center Y
     */
    public void postScale(float deltaScale, float px, float py) {
        if (deltaScale != 0) {
            mCurrentImageMatrix.postScale(deltaScale, deltaScale, px, py);
            setImageMatrix(mCurrentImageMatrix);
            if (mTransformImageListener != null) {
                mTransformImageListener.onScale(getMatrixScale(mCurrentImageMatrix));
            }
        }
    }

    /**
     * This method rotates current image.
     *
     * @param deltaAngle - rotation angle
     * @param px         - rotation center X
     * @param py         - rotation center Y
     */
    public void postRotate(float deltaAngle, float px, float py) {
        if (deltaAngle != 0) {
            mCurrentImageMatrix.postRotate(deltaAngle, px, py);
            setImageMatrix(mCurrentImageMatrix);
            if (mTransformImageListener != null) {
                mTransformImageListener.onRotate(getMatrixAngle(mCurrentImageMatrix));
            }
        }
    }

    /**
     * This method changes image brightness.
     *
     * @param brightness - brightness
     */
    public void postBrightness(float brightness) {
        mBrightness += brightness;

        setColorFilters();
        mTransformImageListener.onBrightness(mBrightness);
    }

    /**
     * This method changes image contrast.
     *
     * @param contrast - contrast
     */
    public void postContrast(float contrast) {
        mContrast += contrast;

        setColorFilters();
        mTransformImageListener.onContrast(mContrast);
    }

    /**
     * This method changes image saturation.
     *
     * @param saturation - saturation
     */
    public void postSaturation(float saturation) {
        mSaturation += saturation;

        setColorFilters();
        mTransformImageListener.onSaturation(mSaturation);
    }

    private void setColorFilters() {
        ColorMatrix cm = new ColorMatrix();
        mBrightness = ColorFilterGenerator.adjustBrightness(cm, mBrightness);
        mContrast = ColorFilterGenerator.adjustContrast(cm, mContrast);
        mSaturation = ColorFilterGenerator.adjustSaturation(cm, mSaturation);
        setColorFilter(new ColorMatrixColorFilter(cm));
    }

    /**
     * This method changes image sharpness.
     *
     * @param sharpness - sharpness
     */
    public void postSharpness(float sharpness) {
        mSharpness += sharpness;
        mSharpness = Math.min(5, Math.max(0, mSharpness));

        if (mSharpnessScriptTask != null) {
            mSharpnessScriptTask.cancel(false);
        }
        mSharpnessScriptTask = new SharpnessScriptTask();
        mSharpnessScriptTask.execute(mSharpness);

        mTransformImageListener.onSharpness(mSharpness * 10);
    }

    /*
     * In the AsyncTask, it invokes RenderScript intrinsics to do a filtering.
     * After the filtering is done, an operation blocks at Allocation.copyTo() in AsyncTask thread.
     * Once all operation is finished at onPostExecute() in UI thread, it can invalidate and update
     * ImageView UI.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private class SharpnessScriptTask extends AsyncTask<Float, Void, Boolean> {
        Boolean issued = false;

        protected Boolean doInBackground(Float... values) {
            if (!isCancelled()) {
                issued = true;

                float value = values[0];
                float[] coefficients = {0, -value, 0, -value, 1 + (4 * value), -value, 0, -value, 0};

                mSharpnessScript.setCoefficients(coefficients);
                mSharpnessScript.forEach(mOutAllocation);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                updateView();
            }
        }

        @Override
        protected void onCancelled(Boolean result) {
            if (issued) {
                updateView();
            }
        }

        private void updateView() {
            Bitmap sourceBitmap = getViewBitmap();
            Bitmap alteredBitmap = sourceBitmap.copy(sourceBitmap.getConfig(), false);

            mOutAllocation.copyTo(alteredBitmap);

            setImageBitmap(alteredBitmap);
        }

    }

    protected void init() {
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed || (mBitmapDecoded && !mBitmapLaidOut)) {

            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth() - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();
            mThisWidth = right - left;
            mThisHeight = bottom - top;

            onImageLaidOut();
        }
    }

    /**
     * When image is laid out {@link #mInitialImageCenter} and {@link #mInitialImageCenter}
     * must be set.
     */
    protected void onImageLaidOut() {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();

        Log.d(TAG, String.format("Image size: [%d:%d]", (int) w, (int) h));

        RectF initialImageRect = new RectF(0, 0, w, h);
        mInitialImageCorners = RectUtils.getCornersFromRect(initialImageRect);
        mInitialImageCenter = RectUtils.getCenterFromRect(initialImageRect);

        mBitmapLaidOut = true;

        if (mTransformImageListener != null) {
            mTransformImageListener.onLoadComplete();
        }
    }

    /**
     * This method returns Matrix value for given index.
     *
     * @param matrix     - valid Matrix object
     * @param valueIndex - index of needed value. See {@link Matrix#MSCALE_X} and others.
     * @return - matrix value for index
     */
    protected float getMatrixValue(@NonNull Matrix matrix, @IntRange(from = 0, to = MATRIX_VALUES_COUNT) int valueIndex) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[valueIndex];
    }

    /**
     * This method logs given matrix X, Y, scale, and angle values.
     * Can be used for debug.
     */
    @SuppressWarnings("unused")
    protected void printMatrix(@NonNull String logPrefix, @NonNull Matrix matrix) {
        float x = getMatrixValue(matrix, Matrix.MTRANS_X);
        float y = getMatrixValue(matrix, Matrix.MTRANS_Y);
        float rScale = getMatrixScale(matrix);
        float rAngle = getMatrixAngle(matrix);
        Log.d(TAG, logPrefix + ": matrix: { x: " + x + ", y: " + y + ", scale: " + rScale + ", angle: " + rAngle + " }");
    }

    /**
     * This method updates current image corners and center points that are stored in
     * {@link #mCurrentImageCorners} and {@link #mCurrentImageCenter} arrays.
     * Those are used for several calculations.
     */
    private void updateCurrentImagePoints() {
        mCurrentImageMatrix.mapPoints(mCurrentImageCorners, mInitialImageCorners);
        mCurrentImageMatrix.mapPoints(mCurrentImageCenter, mInitialImageCenter);
    }

    /**
     * Initialize RenderScript.
     * <p>
     * <p>Creates RenderScript kernel that performs sharpness manipulation.</p>
     */
    private void createScript(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return;
        }

        // Initialize RS
        RenderScript rs = RenderScript.create(getContext());

        // Allocate buffers
        mInAllocation = Allocation.createFromBitmap(rs, bitmap.copy(bitmap.getConfig(), false));
        mOutAllocation = Allocation.createFromBitmap(rs, bitmap);

        // Load script
        mSharpnessScript = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        mSharpnessScript.setInput(mInAllocation);

        rs.destroy();
    }
}
