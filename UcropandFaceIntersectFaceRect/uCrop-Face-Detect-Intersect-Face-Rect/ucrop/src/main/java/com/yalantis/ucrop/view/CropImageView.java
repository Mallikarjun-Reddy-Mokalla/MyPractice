package com.yalantis.ucrop.view;

import static com.yalantis.ucrop.UCropActivity.faceRects;
import static com.yalantis.ucrop.UCropActivity.uCropActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceLandmark;
import com.yalantis.ucrop.R;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.callback.CropBoundsChangeListener;
import com.yalantis.ucrop.model.CropParameters;
import com.yalantis.ucrop.model.ImageState;
import com.yalantis.ucrop.task.BitmapCropTask;
import com.yalantis.ucrop.util.CubicEasing;
import com.yalantis.ucrop.util.RectUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 * <p/>
 * This class adds crop feature, methods to draw crop guidelines, and keep image in correct state.
 * Also it extends parent class methods to add checks for scale; animating zoom in/out.
 */
public class CropImageView extends TransformImageView {
    public static final int DEFAULT_MAX_BITMAP_SIZE = 0;
    public static final int DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION = 500;
    public static final float DEFAULT_MAX_SCALE_MULTIPLIER = 10.0f;
    public static final float SOURCE_IMAGE_ASPECT_RATIO = 0f;
    public static final float DEFAULT_ASPECT_RATIO = SOURCE_IMAGE_ASPECT_RATIO;
    private static final int TARGET_SIZE = 400;
    private RectF faceBoxRectF = new RectF();
    private RectF faceBoxRectFTop = new RectF();

    private RectF imageBoxRectFOnSet = new RectF();
    private RectF imageBoxRectFTopOnSet = new RectF();
  private   RectF imageBoxInViewOnSet = new RectF();
  private   RectF imageBoxInViewTopOnset = new RectF();

    public static RectF mCropRect = new RectF();

    private final Matrix mTempMatrix = new Matrix();
    public static ArrayList<RectF> updatedFaceRects = new ArrayList<>();

    private float mTargetAspectRatio;
    private float mMaxScaleMultiplier = DEFAULT_MAX_SCALE_MULTIPLIER;

    private CropBoundsChangeListener mCropBoundsChangeListener;

    private Runnable mWrapCropBoundsRunnable, mZoomImageToPositionRunnable = null;
    OverlayView overlayView = new OverlayView(getContext().getApplicationContext(), null);
    private float mMaxScale, mMinScale;
    private int mMaxResultImageSizeX = 0, mMaxResultImageSizeY = 0;
    private long mImageToWrapCropBoundsAnimDuration = DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION;
    TransformImageView transformImageView = new TransformImageView(getContext().getApplicationContext());

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Cancels all current animations and sets image to fill crop area (without animation).
     * Then creates and executes {@link BitmapCropTask} with proper parameters.
     */
    public void cropAndSaveImage(@NonNull Bitmap.CompressFormat compressFormat, int compressQuality, @Nullable BitmapCropCallback cropCallback) {
        cancelAllAnimations();
        setImageToWrapCropBounds(false);

        final ImageState imageState = new ImageState(mCropRect, RectUtils.trapToRect(mCurrentImageCorners), getCurrentScale(), getCurrentAngle());

        final CropParameters cropParameters = new CropParameters(mMaxResultImageSizeX, mMaxResultImageSizeY, compressFormat, compressQuality, getImageInputPath(), getImageOutputPath(), getExifInfo(), getCurrentBrightness(), getCurrentContrast(), getCurrentSaturation(), getCurrentSharpness());

        new BitmapCropTask(getContext(), getViewBitmap(), imageState, cropParameters, cropCallback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * @return - maximum scale value for current image and crop ratio
     */
    public float getMaxScale() {
        return mMaxScale;
    }

    /**
     * @return - minimum scale value for current image and crop ratio
     */
    public float getMinScale() {
        return mMinScale;
    }

    /**
     * @return - aspect ratio for crop bounds
     */
    public float getTargetAspectRatio() {
        return mTargetAspectRatio;
    }

    /**
     * Updates current crop rectangle with given. Also recalculates image properties and position
     * to fit new crop rectangle.
     *
     * @param cropRect - new crop rectangle
     */
    public void setCropRect(RectF cropRect) {
        mTargetAspectRatio = cropRect.width() / cropRect.height();
        mCropRect.set(cropRect.left - getPaddingLeft(), cropRect.top - getPaddingTop(), cropRect.right - getPaddingRight(), cropRect.bottom - getPaddingBottom());
//        mCropRect.set(cropRect);
        calculateImageScaleBounds();
        setImageToWrapCropBounds();
    }

    /**
     * This method sets aspect ratio for crop bounds.
     * If {@link #SOURCE_IMAGE_ASPECT_RATIO} value is passed - aspect ratio is calculated
     * based on current image width and height.
     *
     * @param targetAspectRatio - aspect ratio for image crop (e.g. 1.77(7) for 16:9)
     */
    public void setTargetAspectRatio(float targetAspectRatio) {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            mTargetAspectRatio = targetAspectRatio;
            return;
        }

        if (targetAspectRatio == SOURCE_IMAGE_ASPECT_RATIO) {
            mTargetAspectRatio = drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight();
//            updateTheFaceRectFWithAspectRatio(mTargetAspectRatio);

        } else {
            mTargetAspectRatio = targetAspectRatio;
//            updateTheFaceRectFWithAspectRatio(mTargetAspectRatio);

        }

        if (mCropBoundsChangeListener != null) {
            mCropBoundsChangeListener.onCropAspectRatioChanged(mTargetAspectRatio);
        }
    }

    public void updateTheFaceRectFWithAspectRatio(float faceAspectRatio) {
        // Calculate the current aspect ratio of the faceBitmap
        float currentAspectRatio = (float) uCropActivity.inputBitmap.getWidth() / uCropActivity.inputBitmap.getHeight();

// Iterate through the faceRects and adjust their coordinates based on the aspect ratio
        for (Rect faceRectF : faceRectsNew) {
            float faceRectWidth = faceRectF.width();
            float faceRectHeight = faceRectF.height();

            if (currentAspectRatio < faceAspectRatio) {
                // The current image is narrower than the target aspect ratio, so adjust the width
                float newWidth = faceRectHeight * faceAspectRatio;
                float widthOffset = (newWidth - faceRectWidth) / 2f;
                faceRectF.left -= widthOffset;
                faceRectF.right += widthOffset;
//                Toast.makeText(getContext().getApplicationContext(), "Aspect Ratio changed ", Toast.LENGTH_SHORT).show();
            } else {
                // The current image is wider than or equal to the target aspect ratio, so adjust the height
                float newHeight = faceRectWidth / faceAspectRatio;
                float heightOffset = (newHeight - faceRectHeight) / 2f;
                faceRectF.top -= heightOffset;
                faceRectF.bottom += heightOffset;
//                Toast.makeText(getContext().getApplicationContext(), "Aspect Ratio  Altered ", Toast.LENGTH_SHORT).show();
            }
//            updatedFaceRects.add(faceRectF);
            System.out.println(updatedFaceRects);
//            Toast.makeText(getContext().getApplicationContext() , updatedFaceRects.get(0).toString(), Toast.LENGTH_LONG).show();
            // Now, the faceRectF is updated with the new aspect ratio
        }

    }

    @Nullable
    public CropBoundsChangeListener getCropBoundsChangeListener() {
        return mCropBoundsChangeListener;
    }

    public void setCropBoundsChangeListener(@Nullable CropBoundsChangeListener cropBoundsChangeListener) {
        mCropBoundsChangeListener = cropBoundsChangeListener;
    }

    /**
     * This method sets maximum width for resulting cropped image
     *
     * @param maxResultImageSizeX - size in pixels
     */
    public void setMaxResultImageSizeX(@IntRange(from = 10) int maxResultImageSizeX) {
        mMaxResultImageSizeX = maxResultImageSizeX;
    }

    /**
     * This method sets maximum width for resulting cropped image
     *
     * @param maxResultImageSizeY - size in pixels
     */
    public void setMaxResultImageSizeY(@IntRange(from = 10) int maxResultImageSizeY) {
        mMaxResultImageSizeY = maxResultImageSizeY;
    }

    /**
     * This method sets animation duration for image to wrap the crop bounds
     *
     * @param imageToWrapCropBoundsAnimDuration - duration in milliseconds
     */
    public void setImageToWrapCropBoundsAnimDuration(@IntRange(from = 100) long imageToWrapCropBoundsAnimDuration) {
        if (imageToWrapCropBoundsAnimDuration > 0) {
            mImageToWrapCropBoundsAnimDuration = imageToWrapCropBoundsAnimDuration;
        } else {
            throw new IllegalArgumentException("Animation duration cannot be negative value.");
        }
    }

    /**
     * This method sets multiplier that is used to calculate max image scale from min image scale.
     *
     * @param maxScaleMultiplier - (minScale * maxScaleMultiplier) = maxScale
     */
    public void setMaxScaleMultiplier(float maxScaleMultiplier) {
        mMaxScaleMultiplier = maxScaleMultiplier;
    }

    /**
     * This method scales image down for given value related to image center.
     */
    public void zoomOutImage(float deltaScale) {
        zoomOutImage(deltaScale, mCropRect.centerX(), mCropRect.centerY());
    }

    /**
     * This method scales image down for given value related given coords (x, y).
     */
    public void zoomOutImage(float scale, float centerX, float centerY) {
        if (scale >= getMinScale()) {
            postScale(scale / getCurrentScale(), centerX, centerY);
        }
    }

    /**
     * This method scales image up for given value related to image center.
     */
    public void zoomInImage(float deltaScale) {
        zoomInImage(deltaScale, mCropRect.centerX(), mCropRect.centerY());
    }

    /**
     * This method scales image up for given value related to given coords (x, y).
     */
    public void zoomInImage(float scale, float centerX, float centerY) {
        if (scale <= getMaxScale()) {
            postScale(scale / getCurrentScale(), centerX, centerY);
        }
    }

    /**
     * This method changes image scale for given value related to point (px, py) but only if
     * resulting scale is in min/max bounds.
     *
     * @param deltaScale - scale value
     * @param px         - scale center X
     * @param py         - scale center Y
     */
    public void postScale(float deltaScale, float px, float py) {
        if (deltaScale > 1 && getCurrentScale() * deltaScale <= getMaxScale()) {
            super.postScale(deltaScale, px, py);
        } else if (deltaScale < 1 && getCurrentScale() * deltaScale >= getMinScale()) {
            super.postScale(deltaScale, px, py);
        }
    }

    /**
     * This method rotates image for given angle related to the image center.
     *
     * @param deltaAngle - angle to rotate
     */
    public void postRotate(float deltaAngle) {
        postRotate(deltaAngle, mCropRect.centerX(), mCropRect.centerY());
    }

    /**
     * This method cancels all current Runnable objects that represent animations.
     */
    public void cancelAllAnimations() {
        removeCallbacks(mWrapCropBoundsRunnable);
        removeCallbacks(mZoomImageToPositionRunnable);
    }

    public void setImageToWrapCropBounds() {
        setImageToWrapCropBounds(true);
    }

    public static void setImagetoWrapBounds(boolean nocropping) {
        setImagetoWrapBounds(nocropping);
    }

    /**
     * If image doesn't fill the crop bounds it must be translated and scaled properly to fill those.
     * <p/>
     * Therefore this method calculates delta X, Y and scale values and passes them to the
     * {@link WrapCropBoundsRunnable} which animates image.
     * Scale value must be calculated only if image won't fill the crop bounds after it's translated to the
     * crop bounds rectangle center. Using temporary variables this method checks this case.
     */
    public void setImageToWrapCropBounds(boolean animate) {
        if (mBitmapLaidOut && !isImageWrapCropBounds()) {

            float currentX = mCurrentImageCenter[0];
            float currentY = mCurrentImageCenter[1];
            float currentScale = getCurrentScale();

            float deltaX = mCropRect.centerX() - currentX;
            float deltaY = mCropRect.centerY() - currentY;
            float deltaScale = 0;

            mTempMatrix.reset();
            mTempMatrix.setTranslate(deltaX, deltaY);

            final float[] tempCurrentImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
            mTempMatrix.mapPoints(tempCurrentImageCorners);

            boolean willImageWrapCropBoundsAfterTranslate = isImageWrapCropBounds(tempCurrentImageCorners);

            if (willImageWrapCropBoundsAfterTranslate) {
                final float[] imageIndents = calculateImageIndents();
                deltaX = -(imageIndents[0] + imageIndents[2]);
                deltaY = -(imageIndents[1] + imageIndents[3]);
            } else {
                RectF tempCropRect = new RectF(mCropRect);
                mTempMatrix.reset();
                mTempMatrix.setRotate(getCurrentAngle());
                mTempMatrix.mapRect(tempCropRect);

                final float[] currentImageSides = RectUtils.getRectSidesFromCorners(mCurrentImageCorners);

                deltaScale = Math.max(tempCropRect.width() / currentImageSides[0], tempCropRect.height() / currentImageSides[1]);
                deltaScale = deltaScale * currentScale - currentScale;
            }

            if (animate) {
                post(mWrapCropBoundsRunnable = new WrapCropBoundsRunnable(CropImageView.this, mImageToWrapCropBoundsAnimDuration, currentX, currentY, deltaX, deltaY, currentScale, deltaScale, willImageWrapCropBoundsAfterTranslate));
            } else {
                postTranslate(deltaX, deltaY);
                if (!willImageWrapCropBoundsAfterTranslate) {
                    zoomInImage(currentScale + deltaScale, mCropRect.centerX(), mCropRect.centerY());
                }
            }
        }
    }

    /**
     * First, un-rotate image and crop rectangles (make image rectangle axis-aligned).
     * Second, calculate deltas between those rectangles sides.
     * Third, depending on delta (its sign) put them or zero inside an array.
     * Fourth, using Matrix, rotate back those points (indents).
     *
     * @return - the float array of image indents (4 floats) - in this order [left, top, right, bottom]
     */
    private float[] calculateImageIndents() {
        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        float[] unrotatedImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);

        mTempMatrix.mapPoints(unrotatedImageCorners);
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);

        RectF unrotatedImageRect = RectUtils.trapToRect(unrotatedImageCorners);
        RectF unrotatedCropRect = RectUtils.trapToRect(unrotatedCropBoundsCorners);

        float deltaLeft = unrotatedImageRect.left - unrotatedCropRect.left;
        float deltaTop = unrotatedImageRect.top - unrotatedCropRect.top;
        float deltaRight = unrotatedImageRect.right - unrotatedCropRect.right;
        float deltaBottom = unrotatedImageRect.bottom - unrotatedCropRect.bottom;

        float indents[] = new float[4];
        indents[0] = (deltaLeft > 0) ? deltaLeft : 0;
        indents[1] = (deltaTop > 0) ? deltaTop : 0;
        indents[2] = (deltaRight < 0) ? deltaRight : 0;
        indents[3] = (deltaBottom < 0) ? deltaBottom : 0;

        mTempMatrix.reset();
        mTempMatrix.setRotate(getCurrentAngle());
        mTempMatrix.mapPoints(indents);

        return indents;
    }

    /**
     * When image is laid out it must be centered properly to fit current crop bounds.
     */
    @Override
    protected void onImageLaidOut() {
        super.onImageLaidOut();
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        float drawableWidth = drawable.getIntrinsicWidth();
        float drawableHeight = drawable.getIntrinsicHeight();

        if (mTargetAspectRatio == SOURCE_IMAGE_ASPECT_RATIO) {
            mTargetAspectRatio = drawableWidth / drawableHeight;
        }

        int height = (int) (mThisWidth / mTargetAspectRatio);
        if (height > mThisHeight) {
            int width = (int) (mThisHeight * mTargetAspectRatio);
            int halfDiff = (mThisWidth - width) / 2;
            mCropRect.set(halfDiff, 0, width + halfDiff, mThisHeight);
        } else {
            int halfDiff = (mThisHeight - height) / 2;
            mCropRect.set(0, halfDiff, mThisWidth, height + halfDiff);
        }

        calculateImageScaleBounds(drawableWidth, drawableHeight);
        setupInitialImagePosition(drawableWidth, drawableHeight);

        if (mCropBoundsChangeListener != null) {
            mCropBoundsChangeListener.onCropAspectRatioChanged(mTargetAspectRatio);
        }
        if (mTransformImageListener != null) {
            mTransformImageListener.onScale(getCurrentScale());
            mTransformImageListener.onRotate(getCurrentAngle());
            mTransformImageListener.onBrightness(getCurrentBrightness());
            mTransformImageListener.onContrast(getCurrentContrast());
            mTransformImageListener.onSaturation(getCurrentSaturation());
            mTransformImageListener.onSharpness(getCurrentSharpness());
        }
    }

    /**
     * This method checks whether current image fills the crop bounds.
     */
    protected boolean isImageWrapCropBounds() {
        return isImageWrapCropBounds(mCurrentImageCorners);
    }

    /**
     * This methods checks whether a rectangle that is represented as 4 corner points (8 floats)
     * fills the crop bounds rectangle.
     *
     * @param imageCorners - corners of a rectangle
     * @return - true if it wraps crop bounds, false - otherwise
     */
    protected boolean isImageWrapCropBounds(float[] imageCorners) {
        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        float[] unrotatedImageCorners = Arrays.copyOf(imageCorners, imageCorners.length);
        mTempMatrix.mapPoints(unrotatedImageCorners);

        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);

        return RectUtils.trapToRect(unrotatedImageCorners).contains(RectUtils.trapToRect(unrotatedCropBoundsCorners));
    }

    /**
     * This method changes image scale (animating zoom for given duration), related to given center (x,y).
     *
     * @param scale      - target scale
     * @param centerX    - scale center X
     * @param centerY    - scale center Y
     * @param durationMs - zoom animation duration
     */
    protected void zoomImageToPosition(float scale, float centerX, float centerY, long durationMs) {
        if (scale > getMaxScale()) {
            scale = getMaxScale();
        }

        final float oldScale = getCurrentScale();
        final float deltaScale = scale - oldScale;

        post(mZoomImageToPositionRunnable = new ZoomImageToPosition(CropImageView.this, durationMs, oldScale, deltaScale, centerX, centerY));
    }

    private void calculateImageScaleBounds() {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        calculateImageScaleBounds(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    /**
     * This method calculates image minimum and maximum scale values for current {@link #mCropRect}.
     *
     * @param drawableWidth  - image width
     * @param drawableHeight - image height
     */
    private void calculateImageScaleBounds(float drawableWidth, float drawableHeight) {
        float widthScale = Math.min(mCropRect.width() / drawableWidth, mCropRect.width() / drawableHeight);
        float heightScale = Math.min(mCropRect.height() / drawableHeight, mCropRect.height() / drawableWidth);

        mMinScale = Math.min(widthScale, heightScale);
        mMaxScale = mMinScale * mMaxScaleMultiplier;
    }

    /**
     * This method calculates initial image position so it is positioned properly.
     * Then it sets those values to the current image matrix.
     *
     * @param drawableWidth  - image width
     * @param drawableHeight - image height
     */
    private void setupInitialImagePosition(float drawableWidth, float drawableHeight) {
        float cropRectWidth = mCropRect.width();
        float cropRectHeight = mCropRect.height();

        float widthScale = mCropRect.width() / drawableWidth;
        float heightScale = mCropRect.height() / drawableHeight;

        float initialMinScale = Math.max(widthScale, heightScale);

        float tw = (cropRectWidth - drawableWidth * initialMinScale) / 2.0f + mCropRect.left;
        float th = (cropRectHeight - drawableHeight * initialMinScale) / 2.0f + mCropRect.top;

        mCurrentImageMatrix.reset();
        mCurrentImageMatrix.postScale(initialMinScale, initialMinScale);
        mCurrentImageMatrix.postTranslate(tw, th);
        setImageMatrix(mCurrentImageMatrix);

        if (transformImageView.faceRectsNew != null) {
            if (transformImageView.faceRectsNew.size() > 0) {
                faceToTheCenterOfScreen(drawableWidth, drawableHeight);
            }
        }
    }

    // Method to calculate the screen density
    private float getScreenDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(metrics);
        }
        return metrics.density;
    }

    // Method to form a new Rect with adjusted dimensions
    public Rect getNewRectWithDensityAdjusted(Rect faceRect) {
        // Get the screen density
        float density = getScreenDensity();

        // Calculate the previous width and height in screen pixels
        int previousWidthInScreenPixels = faceRect.width();
        int previousHeightInScreenPixels = faceRect.height();

        // Calculate the previous width and height in actual pixels
        int previousWidthInActualPixels = (int) (previousWidthInScreenPixels / density);
        int previousHeightInActualPixels = (int) (previousHeightInScreenPixels / density);

        // Calculate the left, top, right, and bottom coordinates for the new Rect
        int left = faceRect.left;
        int top = faceRect.top;
        int right = left + previousWidthInActualPixels;
        int bottom = top + previousHeightInActualPixels;

        // Create the new Rect
        Rect newRect = new Rect(left, top, right, bottom);

        return newRect;
    }

    public void faceToTheCenterOfScreen(float drawableWidth, float drawableHeight) {
        // Assuming you have the faceRect as a Rect object representing the rectangular area you want to move
        if (transformImageView.faceRectFocusAfterPadding != null) {
//            if (transformImageView.faceRectsNew.size() > 0) {
//                if (transformImageView.faceRectsNew.get(0) != null) {
            System.out.println("uCrop _____" + "transformImageView.faceRectFocusAfterPadding" + transformImageView.faceRectFocusAfterPadding);
            System.out.println("uCrop _____" + "transformImageView.faceRectsNew.get(0)" + transformImageView.faceRectsNew.get(0));
//        transformImageView.faceRectFocusAfterPadding = getNewRectWithDensityAdjusted(transformImageView.faceRectsNew.get(0));

//        System.out.println("uCrop"+"transformImageView.Updated"+transformImageView.faceRectFocusAfterPadding);


// Get the dimensions of the screen
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;
            float density = displayMetrics.density;
            System.out.println("uCrop _____" + "screenWidth---" + screenWidth);
            System.out.println("uCrop _____" + "screenHeight---" + screenHeight);
            // Calculate the center point of the screen
            int screenCenterX = screenWidth / 2;
            int screenCenterY = screenHeight / 2;
// Get the ImageView dimensions and scale
//    int imageViewWidth = transformImageView.getWidth();
//    int imageViewHeight = transformImageView.getHeight();
            Drawable drawable = getDrawable();
            int imageIntrinsicWidth = drawable.getIntrinsicWidth();
            int imageIntrinsicHeight = drawable.getIntrinsicHeight();
            float scaleX = (float) drawableWidth / imageIntrinsicWidth;
            float scaleY = (float) drawableHeight / imageIntrinsicHeight;
            System.out.println("uCrop _____" + "drawableWidth---" + drawableWidth);
            System.out.println("uCrop _____" + "drawableHeight---" + drawableHeight);
            System.out.println("uCrop _____" + "imageIntrinsicWidth---" + imageIntrinsicWidth);
            System.out.println("uCrop _____" + "imageIntrinsicHeight---" + imageIntrinsicHeight);

// Calculate the translation required to move faceRect to the center of the screen
            int faceRectCenterX = (transformImageView.faceRectFocusAfterPadding.left + transformImageView.faceRectFocusAfterPadding.right) / 2;
            int faceRectCenterY = (transformImageView.faceRectFocusAfterPadding.top + transformImageView.faceRectFocusAfterPadding.bottom) / 2;
            int deltaX = (screenWidth / 2) - (int) (faceRectCenterX * scaleX);
            int deltaY = (screenHeight / 2) - (int) (faceRectCenterY * scaleY);



            transformImageView.faceRectFocusAfterPadding.offset(deltaX, deltaY);

            System.out.println("uCrop _____" +" ScaleIn " + " deltaX----)" + deltaX);
            System.out.println("uCrop _____" + " ScaleIn " +" deltaY----)" + deltaY);
// Update the ImageView's matrix
            mCurrentImageMatrix.reset();
            mCurrentImageMatrix.postTranslate(deltaX, deltaY);

            System.out.println("uCrop _____" + "mCurrentImageMatrix---" + mCurrentImageMatrix);

            setImageMatrix(mCurrentImageMatrix);
        }
        checkInitialFaceIntersection();

    }

    private void checkInitialFaceIntersection() {
        Matrix imageMatrix = transformImageView.mCurrentImageMatrix;

        if (transformImageView.faceRectsNew != null) {
            if (transformImageView.faceRectsNew.size() > 0) {

                imageBoxRectFOnSet = new RectF(transformImageView.faceRectsNew.get(0));
                imageMatrix.mapRect(imageBoxInViewOnSet, imageBoxRectFOnSet);

            }
        }
        if (transformImageView.faceRectsTop != null) {
            if (transformImageView.faceRectsTop.size() > 0) {
                imageBoxRectFTopOnSet = new RectF(transformImageView.faceRectsTop.get(0));
                imageMatrix.mapRect(imageBoxInViewTopOnset, imageBoxRectFTopOnSet);

            }
        }
        RectF intersectionRectSet = new RectF();
        RectF intersectionRectTopSet = new RectF();
        boolean isIntersectingAfterSet = intersectionRectSet.setIntersect(imageBoxRectFOnSet, CropImageView.mCropRect);
        boolean isIntersectingAfterTopSet = intersectionRectTopSet.setIntersect(imageBoxInViewTopOnset, CropImageView.mCropRect);
        if (isIntersectingAfterSet || isIntersectingAfterTopSet) {
             uCropActivity.setCautionText("Face Detected");
            uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
        } else {
             uCropActivity.setCautionText("Face Not Detected");
            uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);
        }

    }






    /**
     * This method extracts all needed values from the styled attributes.
     * Those are used to configure the view.
     */
    @SuppressWarnings("deprecation")
    protected void processStyledAttributes(@NonNull TypedArray a) {
        float targetAspectRatioX = Math.abs(a.getFloat(R.styleable.ucrop_UCropView_ucrop_aspect_ratio_x, DEFAULT_ASPECT_RATIO));
        float targetAspectRatioY = Math.abs(a.getFloat(R.styleable.ucrop_UCropView_ucrop_aspect_ratio_y, DEFAULT_ASPECT_RATIO));

        if (targetAspectRatioX == SOURCE_IMAGE_ASPECT_RATIO || targetAspectRatioY == SOURCE_IMAGE_ASPECT_RATIO) {
            mTargetAspectRatio = SOURCE_IMAGE_ASPECT_RATIO;
        } else {
            mTargetAspectRatio = targetAspectRatioX / targetAspectRatioY;
        }
    }

    /**
     * This Runnable is used to animate an image so it fills the crop bounds entirely.
     * Given values are interpolated during the animation time.
     * Runnable can be terminated either vie {@link #cancelAllAnimations()} method
     * or when certain conditions inside {@link WrapCropBoundsRunnable#run()} method are triggered.
     */
    private static class WrapCropBoundsRunnable implements Runnable {

        private final WeakReference<CropImageView> mCropImageView;

        private final long mDurationMs, mStartTime;
        private final float mOldX, mOldY;
        private final float mCenterDiffX, mCenterDiffY;
        private final float mOldScale;
        private final float mDeltaScale;
        private final boolean mWillBeImageInBoundsAfterTranslate;

        public WrapCropBoundsRunnable(CropImageView cropImageView, long durationMs, float oldX, float oldY, float centerDiffX, float centerDiffY, float oldScale, float deltaScale, boolean willBeImageInBoundsAfterTranslate) {

            mCropImageView = new WeakReference<>(cropImageView);

            mDurationMs = durationMs;
            mStartTime = System.currentTimeMillis();
            mOldX = oldX;
            mOldY = oldY;
            mCenterDiffX = centerDiffX;
            mCenterDiffY = centerDiffY;
            mOldScale = oldScale;
            mDeltaScale = deltaScale;
            mWillBeImageInBoundsAfterTranslate = willBeImageInBoundsAfterTranslate;
        }

        @Override
        public void run() {
            CropImageView cropImageView = mCropImageView.get();
            if (cropImageView == null) {
                return;
            }

            long now = System.currentTimeMillis();
            float currentMs = Math.min(mDurationMs, now - mStartTime);

            float newX = CubicEasing.easeOut(currentMs, 0, mCenterDiffX, mDurationMs);
            float newY = CubicEasing.easeOut(currentMs, 0, mCenterDiffY, mDurationMs);
            float newScale = CubicEasing.easeInOut(currentMs, 0, mDeltaScale, mDurationMs);

            if (currentMs < mDurationMs) {
                cropImageView.postTranslate(newX - (cropImageView.mCurrentImageCenter[0] - mOldX), newY - (cropImageView.mCurrentImageCenter[1] - mOldY));
                if (!mWillBeImageInBoundsAfterTranslate) {
                    cropImageView.zoomInImage(mOldScale + newScale, cropImageView.mCropRect.centerX(), cropImageView.mCropRect.centerY());
                }
                if (!cropImageView.isImageWrapCropBounds()) {
                    cropImageView.post(this);
                }
            }
        }
    }

    /**
     * This Runnable is used to animate an image zoom.
     * Given values are interpolated during the animation time.
     * Runnable can be terminated either vie {@link #cancelAllAnimations()} method
     * or when certain conditions inside {@link ZoomImageToPosition#run()} method are triggered.
     */
    private static class ZoomImageToPosition implements Runnable {

        private final WeakReference<CropImageView> mCropImageView;

        private final long mDurationMs, mStartTime;
        private final float mOldScale;
        private final float mDeltaScale;
        private final float mDestX;
        private final float mDestY;

        public ZoomImageToPosition(CropImageView cropImageView, long durationMs, float oldScale, float deltaScale, float destX, float destY) {

            mCropImageView = new WeakReference<>(cropImageView);

            mStartTime = System.currentTimeMillis();
            mDurationMs = durationMs;
            mOldScale = oldScale;
            mDeltaScale = deltaScale;
            mDestX = destX;
            mDestY = destY;
        }

        @Override
        public void run() {
            CropImageView cropImageView = mCropImageView.get();
            if (cropImageView == null) {
                return;
            }

            long now = System.currentTimeMillis();
            float currentMs = Math.min(mDurationMs, now - mStartTime);
            float newScale = CubicEasing.easeInOut(currentMs, 0, mDeltaScale, mDurationMs);

            if (currentMs < mDurationMs) {
                cropImageView.zoomInImage(mOldScale + newScale, mDestX, mDestY);
                cropImageView.post(this);
            } else {
                cropImageView.setImageToWrapCropBounds();
            }
        }

    }

}