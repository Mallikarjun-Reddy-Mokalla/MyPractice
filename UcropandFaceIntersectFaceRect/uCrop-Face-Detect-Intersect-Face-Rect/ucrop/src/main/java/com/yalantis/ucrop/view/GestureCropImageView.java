package com.yalantis.ucrop.view;

import static com.yalantis.ucrop.UCropActivity.uCropActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.yalantis.ucrop.DrawTextVlue;
import com.yalantis.ucrop.util.RotationGestureDetector;

import java.util.ArrayList;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
public class GestureCropImageView extends CropImageView {
    float lastTouchX;
    float lastTouchY;
    float dx;
    float dy;
    Rect faceRect2 = new Rect(); // Define your face rectangle here
    Rect cropRect = new Rect(); // Define your crop rectangle here
    private static final int DOUBLE_TAP_ZOOM_DURATION = 200;
    Matrix imageMatrix = new Matrix();
    DrawTextVlue drawTextVlue;
    private ScaleGestureDetector mScaleDetector;
    private RotationGestureDetector mRotateDetector;
    private GestureDetector mGestureDetector;
    private float mMidPntX, mMidPntY;
    private RectF mCropRectscalef = new RectF();
    private RectF mCustomCropRectscalef = new RectF();
    private boolean mIsRotateEnabled = true, mIsScaleEnabled = true, mIsGestureEnabled = true;
    private int mDoubleTapScaleSteps = 5;
    private Bitmap intBitmap;
    public static ArrayList<RectF> faceRectFfound = new ArrayList<>();
    int mMaxResultImageSizeX = 0, mMaxResultImageSizeY = 0, compressQuality = 0;
    Bitmap.CompressFormat compressFormat;
    OverlayView overlayView = new OverlayView(getContext().getApplicationContext());
    private GestureCropImageView mGestureCropImageView;
    private Matrix inverseMatrix = new Matrix();
    private float[] point;
    TransformImageView transformImageView = new TransformImageView(getContext().getApplicationContext());

    private RectF imageBoxRectF = new RectF();
    private RectF imageBoxRectFOne = new RectF();
    private RectF imageBoxRectFTwo = new RectF();
    private RectF imageBoxRectFThree = new RectF();
    private RectF imageBoxRectFFour = new RectF();


    private RectF imageBoxRectFTop = new RectF();


    private float initialDistance = -1f;
    boolean faceintersected = false;

    RectF imageBoxInView = new RectF();
    RectF imageBoxInViewOne = new RectF();
    RectF imageBoxInViewTwo = new RectF();
    RectF imageBoxInViewThree = new RectF();
    RectF imageBoxInViewFour = new RectF();

    RectF imageBoxInViewTop = new RectF();

    RectF imageBoxInViewAfterZoom = new RectF();
    RectF imageBoxInViewAfterZoomOne = new RectF();
    RectF imageBoxInViewAfterZoomTwo = new RectF();
    RectF imageBoxInViewAfterZoomThree = new RectF();
    RectF imageBoxInViewAfterZoomFour = new RectF();


    RectF imageBoxInViewAfterZoomTop = new RectF();
    private RectF initialCropWindowRect;
    RectF intersectionRect = new RectF();
    RectF intersectionRectTop = new RectF();
    boolean isFaceRectsTopIntersecting = false;

    boolean isFaceRectsNewIntersecting = false;
    int numberOfIntersectNew = 0;
    int numberOfIntersectTop;
    private int size;
private String faceDetected="Face Detected";
private String multipleFaces= "  Two or More faces";
private String noFaces= "Face Not Detected";
    public GestureCropImageView(Context context) {
        super(context);
        initHere();

    }


    public GestureCropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initHere();
    }

    public GestureCropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHere();
    }

    private void initHere() {

    }

    public void setScaleEnabled(boolean scaleEnabled) {
        mIsScaleEnabled = scaleEnabled;
    }

    public boolean isScaleEnabled() {
        return mIsScaleEnabled;
    }

    public void setRotateEnabled(boolean rotateEnabled) {
        mIsRotateEnabled = rotateEnabled;
    }

    public boolean isRotateEnabled() {
        return mIsRotateEnabled;
    }

    public void setGestureEnabled(boolean gestureEnabled) {
        mIsGestureEnabled = gestureEnabled;
    }

    public boolean isGestureEnabled() {
        return mIsGestureEnabled;
    }

    public void setDoubleTapScaleSteps(int doubleTapScaleSteps) {
        mDoubleTapScaleSteps = doubleTapScaleSteps;
    }

    public int getDoubleTapScaleSteps() {
        return mDoubleTapScaleSteps;
    }


    private float getDistance(MotionEvent event) {
        if (event.getPointerCount() >= 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }
        return 0f;
    }

    /**
     * If it's ACTION_DOWN event - user touches the screen and all current animation must be canceled.
     * If it's ACTION_UP event - user removed all fingers from the screen and current image position must be corrected.
     * If there are more than 2 fingers - update focal point coordinates.
     * Pass the event to the gesture detectors if those are enabled.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            cancelAllAnimations();
            // User started to touch, save the initial coordinates
            lastTouchX = event.getX();
            lastTouchY = event.getY();
            initialCropWindowRect = new RectF(overlayView.mCropViewRect);


        }
        /*copying the face Rects*/
        if (transformImageView.faceRectsNew != null) {
            if (transformImageView.faceRectsNew.size() > 0) {

                size = transformImageView.faceRectsNew.size();
                System.out.println("Ucrop" + "Rect Sizes---)" + size);
                for (int i = 1; i <= size; i++) {
                    if (i == 1) {

                        imageBoxRectF = new RectF(transformImageView.faceRectsNew.get(i - 1));
                        System.out.println("uCrop" + "Rects" + "------imageBoxRectF-------" + imageBoxRectF);

                    }
                    if (i == 2) {
                        imageBoxRectFOne = new RectF(transformImageView.faceRectsNew.get(i - 1));
                        System.out.println("uCrop" + "Rects" + "------imageBoxRectFOne-------" + imageBoxRectFOne);

                    }
                    if (i == 3) {
                        imageBoxRectFTwo = new RectF(transformImageView.faceRectsNew.get(i - 1));
                        System.out.println("uCrop" + "Rects" + "------imageBoxRectFTwo-------" + imageBoxRectFTwo);

                    }
                    if (i == 4) {
                        imageBoxRectFThree = new RectF(transformImageView.faceRectsNew.get(i - 1));
                        System.out.println("uCrop" + "Rects" + "------imageBoxRectFThree-------" + imageBoxRectFThree);

                    }
                    if (i == 5) {
                        imageBoxRectFFour = new RectF(transformImageView.faceRectsNew.get(i - 1));
                        System.out.println("uCrop" + "Rects" + "------imageBoxRectFFour-------" + imageBoxRectFFour);

                    }
                }

            }
        }
        if (transformImageView.faceRectsTop != null) {
            if (transformImageView.faceRectsTop.size() > 0) {
                imageBoxRectFTop = new RectF(transformImageView.faceRectsTop.get(0));

            }
        }

        if (event.getPointerCount() > 1) {
            mMidPntX = (event.getX(0) + event.getX(1)) / 2;
            mMidPntY = (event.getY(0) + event.getY(1)) / 2;
        }

        if (mIsGestureEnabled) {
            mGestureDetector.onTouchEvent(event);

        }

        if (mIsScaleEnabled) {
            mScaleDetector.onTouchEvent(event);
        }

        if (mIsRotateEnabled) {
            mRotateDetector.onTouchEvent(event);
        }
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            setImageToWrapCropBounds();
            checkTheIntersectionOfRectsOnActionUpOrOnFling();

        }

        // Check for intersection during ACTION_MOVE
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {

            float translationX = event.getX() - lastTouchX;
            float translationY = event.getY() - lastTouchY;
            checkEachFaceRectTranslationAndIntersection(translationX, translationY);// to check the intesecting faces

        }

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
            // Code to handle touch zoom event
            // Get the distance between the two pointers
            float distance = getDistance(event);
            checkTheIntertionOfRectWhileZooming(distance);

        }


        return true;

    }

    @Override
    protected void init() {
        super.init();
        setupGestureListeners();
    }

    /**
     * This method calculates target scale value for double tap gesture.
     * User is able to zoom the image from min scale value
     * to the max scale value with {@link #mDoubleTapScaleSteps} double taps.
     */
    protected float getDoubleTapTargetScale() {
        return getCurrentScale() * (float) Math.pow(getMaxScale() / getMinScale(), 1.0f / mDoubleTapScaleSteps);
    }

    private void setupGestureListeners() {
        mGestureDetector = new GestureDetector(getContext(), new GestureListener(), null, true);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mRotateDetector = new RotationGestureDetector(new RotateListener());
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            postScale(detector.getScaleFactor(), mMidPntX, mMidPntY);

            System.out.println("uCrop" + "------Pull Down1 -------" + detector.getScaleFactor());

            return true;
        }
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            zoomImageToPosition(getDoubleTapTargetScale(), e.getX(), e.getY(), DOUBLE_TAP_ZOOM_DURATION);
            return super.onDoubleTap(e);
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            postTranslate(-distanceX, -distanceY);
            System.out.println("uCrop" + "------Pull Down X -------" + -distanceX);
            System.out.println("uCrop" + "------Pull Down Y-------" + -distanceY);
            checkTheIntersectionOfRectsOnActionUpOrOnFling();

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkTheIntersectionOfRectsOnActionUpOrOnFling();
                }
            }, 400);


            return true;

        }
    }

    private class RotateListener extends RotationGestureDetector.SimpleOnRotationGestureListener {

        @Override
        public boolean onRotation(RotationGestureDetector rotationDetector) {
            postRotate(rotationDetector.getAngle(), mMidPntX, mMidPntY);
            System.out.println("uCrop" + "------Pull Down Rotate -------" + -mMidPntY);

            return true;
        }

    }





/* Method to check the intersection of face at 50 % with crop window */
    private boolean checkHalfRectIntersection(RectF imageBoxInViewIn, RectF mCropRect) {
        // Calculate the intersection area between imageBoxInView and CropImageView.mCropRect
        RectF intersectionRect = new RectF();
        boolean isIntersectingAfterMove = intersectionRect.setIntersect(imageBoxInViewIn, mCropRect);

// Calculate the total area of imageBoxInView
        float totalArea = imageBoxInViewIn.width() * imageBoxInViewIn.height();

// Calculate the area of intersection
        float intersectionArea = intersectionRect.width() * intersectionRect.height();

// Calculate the percentage of intersection
        float intersectionPercentage = (intersectionArea / totalArea) * 100;

// Check if the intersection percentage is at least 50%
        if (intersectionPercentage >= 50.0f) {
            return true;
        }
        return false;
    }
/*Mehod used to call to check the intersecting of second face  when a face intersected 50% or more */
    private boolean checkSceondRectIntersection(RectF imageBoxInViewIn, RectF mCropRect) {
        // Calculate the intersection area between imageBoxInView and CropImageView.mCropRect
        RectF intersectionRect = new RectF();
        boolean isIntersectingAfterMove = intersectionRect.setIntersect(imageBoxInViewIn, mCropRect);

// Calculate the total area of imageBoxInView
        float totalArea = imageBoxInViewIn.width() * imageBoxInViewIn.height();

// Calculate the area of intersection
        float intersectionArea = intersectionRect.width() * intersectionRect.height();

// Calculate the percentage of intersection
        float intersectionPercentage = (intersectionArea / totalArea) * 100;

// Check if the intersection percentage is at least 50%
        if (intersectionPercentage >= 10.0f) {
            return true;
        }
        return false;
    }

/*Method to check the Intersecting of Faces when Image Scrolled and leaved */
    private void checkTheIntersectionOfRectsOnActionUpOrOnFling() {
        Matrix imageMatrix = transformImageView.mCurrentImageMatrix;


        if (size == 1) {
            imageMatrix.mapRect(imageBoxInView, imageBoxRectF);
            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect)) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }
        } else if (size == 2) {
            imageMatrix.mapRect(imageBoxInView, imageBoxRectF);
            imageMatrix.mapRect(imageBoxInViewOne, imageBoxRectFOne);
            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            }else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }

        } else if (size == 3) {
            imageMatrix.mapRect(imageBoxInView, imageBoxRectF);
            imageMatrix.mapRect(imageBoxInViewOne, imageBoxRectFOne);
            imageMatrix.mapRect(imageBoxInViewTwo, imageBoxRectFTwo);

            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (!checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }
        } else if (size == 4) {
            imageMatrix.mapRect(imageBoxInView, imageBoxRectF);
            imageMatrix.mapRect(imageBoxInViewOne, imageBoxRectFOne);
            imageMatrix.mapRect(imageBoxInViewTwo, imageBoxRectFTwo);
            imageMatrix.mapRect(imageBoxInViewThree, imageBoxRectFThree);
            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            }else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkHalfRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }
        } else if (size == 5) {
            imageMatrix.mapRect(imageBoxInView, imageBoxRectF);
            imageMatrix.mapRect(imageBoxInViewOne, imageBoxRectFOne);
            imageMatrix.mapRect(imageBoxInViewTwo, imageBoxRectFTwo);
            imageMatrix.mapRect(imageBoxInViewThree, imageBoxRectFThree);
            imageMatrix.mapRect(imageBoxInViewFour, imageBoxRectFFour);
            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewThree, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))
                    && (!checkHalfRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }
        }
    }
/*Method to check the face intersecting with Crop Window on moving Up and Down */
    private void checkEachFaceRectTranslationAndIntersection(float translationX, float translationY) {
        Matrix imageMatrix = transformImageView.mCurrentImageMatrix;


        System.out.println("uCrop" + "------translationX-------" + translationX);
        System.out.println("uCrop" + "------translationY-------" + translationY);
        if (size == 1) {
            imageBoxRectF.left += translationX;
            imageBoxRectF.top += translationY;
            imageBoxRectF.right += translationX;
            imageBoxRectF.bottom += translationY;
            imageMatrix.mapRect(imageBoxInView, imageBoxRectF);
            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect)) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }
        } else if (size == 2) {
            imageBoxRectF.left += translationX;
            imageBoxRectF.top += translationY;
            imageBoxRectF.right += translationX;
            imageBoxRectF.bottom += translationY;
            imageMatrix.mapRect(imageBoxInView, imageBoxRectF);
            imageBoxRectFOne.left += translationX;
            imageBoxRectFOne.top += translationY;
            imageBoxRectFOne.right += translationX;
            imageBoxRectFOne.bottom += translationY;
            imageMatrix.mapRect(imageBoxInViewOne, imageBoxRectFOne);

            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            }else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }
        } else if (size == 3) {
            imageBoxRectF.left += translationX;
            imageBoxRectF.top += translationY;
            imageBoxRectF.right += translationX;
            imageBoxRectF.bottom += translationY;
            imageMatrix.mapRect(imageBoxInView, imageBoxRectF);
            imageBoxRectFOne.left += translationX;
            imageBoxRectFOne.top += translationY;
            imageBoxRectFOne.right += translationX;
            imageBoxRectFOne.bottom += translationY;
            imageMatrix.mapRect(imageBoxInViewOne, imageBoxRectFOne);
            imageBoxRectFTwo.left += translationX;
            imageBoxRectFTwo.top += translationY;
            imageBoxRectFTwo.right += translationX;
            imageBoxRectFTwo.bottom += translationY;
            imageMatrix.mapRect(imageBoxInViewTwo, imageBoxRectFTwo);

            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            }else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }
        } else if (size == 4) {
            imageBoxRectF.left += translationX;
            imageBoxRectF.top += translationY;
            imageBoxRectF.right += translationX;
            imageBoxRectF.bottom += translationY;
            imageMatrix.mapRect(imageBoxInView, imageBoxRectF);
            imageBoxRectFOne.left += translationX;
            imageBoxRectFOne.top += translationY;
            imageBoxRectFOne.right += translationX;
            imageBoxRectFOne.bottom += translationY;
            imageMatrix.mapRect(imageBoxInViewOne, imageBoxRectFOne);
            imageBoxRectFTwo.left += translationX;
            imageBoxRectFTwo.top += translationY;
            imageBoxRectFTwo.right += translationX;
            imageBoxRectFTwo.bottom += translationY;
            imageMatrix.mapRect(imageBoxInViewTwo, imageBoxRectFTwo);
            imageBoxRectFThree.left += translationX;
            imageBoxRectFThree.top += translationY;
            imageBoxRectFThree.right += translationX;
            imageBoxRectFThree.bottom += translationY;
            imageMatrix.mapRect(imageBoxInViewThree, imageBoxRectFThree);
            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            }  else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkHalfRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }


        } else if (size == 5) {

            imageBoxRectF.left += translationX;
            imageBoxRectF.top += translationY;
            imageBoxRectF.right += translationX;
            imageBoxRectF.bottom += translationY;
            imageMatrix.mapRect(imageBoxInView, imageBoxRectF);
            imageBoxRectFOne.left += translationX;
            imageBoxRectFOne.top += translationY;
            imageBoxRectFOne.right += translationX;
            imageBoxRectFOne.bottom += translationY;
            imageMatrix.mapRect(imageBoxInViewOne, imageBoxRectFOne);
            imageBoxRectFTwo.left += translationX;
            imageBoxRectFTwo.top += translationY;
            imageBoxRectFTwo.right += translationX;
            imageBoxRectFTwo.bottom += translationY;
            imageMatrix.mapRect(imageBoxInViewTwo, imageBoxRectFTwo);
            imageBoxRectFThree.left += translationX;
            imageBoxRectFThree.top += translationY;
            imageBoxRectFThree.right += translationX;
            imageBoxRectFThree.bottom += translationY;
            imageMatrix.mapRect(imageBoxInViewThree, imageBoxRectFThree);
            imageBoxRectFFour.left += translationX;
            imageBoxRectFFour.top += translationY;
            imageBoxRectFFour.right += translationX;
            imageBoxRectFFour.bottom += translationY;
            imageMatrix.mapRect(imageBoxInViewFour, imageBoxRectFFour);

            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewThree, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect)) && (!checkHalfRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }
        }


    }


    /* Method to handle touch zoom event*/
    private void checkTheIntertionOfRectWhileZooming(float distanceZoom) {
        // Calculate the scale factor based on the previous distance and current distance
        Matrix imageMatrixAfterZoom = transformImageView.mCurrentImageMatrix;

        float scaleFactor = distanceZoom / initialDistance;
        System.out.println("uCrop" + "------scaleFactor-------" + scaleFactor);
        if (size == 1) {
            // Update the width and height of the imageBoxRectF
            imageBoxRectF.right = imageBoxRectF.left + imageBoxRectF.width() * scaleFactor;
            imageBoxRectF.bottom = imageBoxRectF.top + imageBoxRectF.height() * scaleFactor;
            // Get the current matrix of the image after zooming
            // Map the imageBoxRectF to the View's coordinate system after zooming
            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoom, imageBoxRectF);
            if (checkHalfRectIntersection(imageBoxInViewAfterZoom, CropImageView.mCropRect)) {
                // Both imageBoxRectF and imageBoxRectFTop intersect with the CropImageView's cropRect
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                // Either imageBoxRectF or imageBoxRectFTop (or both) do not intersect with the CropImageView's cropRect
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }

        } else if (size == 2) {
            imageBoxRectF.right = imageBoxRectF.left + imageBoxRectF.width() * scaleFactor;
            imageBoxRectF.bottom = imageBoxRectF.top + imageBoxRectF.height() * scaleFactor;

            imageBoxRectFOne.right = imageBoxRectFOne.left + imageBoxRectFOne.width() * scaleFactor;
            imageBoxRectFOne.bottom = imageBoxRectFOne.top + imageBoxRectFOne.height() * scaleFactor;

            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoom, imageBoxRectF);
            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoomOne, imageBoxRectFOne);

            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            }else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }
        } else if (size == 3) {
            imageBoxRectF.right = imageBoxRectF.left + imageBoxRectF.width() * scaleFactor;
            imageBoxRectF.bottom = imageBoxRectF.top + imageBoxRectF.height() * scaleFactor;

            imageBoxRectFOne.right = imageBoxRectFOne.left + imageBoxRectFOne.width() * scaleFactor;
            imageBoxRectFOne.bottom = imageBoxRectFOne.top + imageBoxRectFOne.height() * scaleFactor;

            imageBoxRectFTwo.right = imageBoxRectFTwo.left + imageBoxRectFTwo.width() * scaleFactor;
            imageBoxRectFTwo.bottom = imageBoxRectFTwo.top + imageBoxRectFTwo.height() * scaleFactor;

            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoom, imageBoxRectF);
            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoomOne, imageBoxRectFOne);
            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoomTwo, imageBoxRectFTwo);
            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect)
                    && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect))
                    && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)
            ) && (checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            }  else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (!checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }


        } else if (size == 4) {

            imageBoxRectF.right = imageBoxRectF.left + imageBoxRectF.width() * scaleFactor;
            imageBoxRectF.bottom = imageBoxRectF.top + imageBoxRectF.height() * scaleFactor;

            imageBoxRectFOne.right = imageBoxRectFOne.left + imageBoxRectFOne.width() * scaleFactor;
            imageBoxRectFOne.bottom = imageBoxRectFOne.top + imageBoxRectFOne.height() * scaleFactor;

            imageBoxRectFTwo.right = imageBoxRectFTwo.left + imageBoxRectFTwo.width() * scaleFactor;
            imageBoxRectFTwo.bottom = imageBoxRectFTwo.top + imageBoxRectFTwo.height() * scaleFactor;

            imageBoxRectFThree.right = imageBoxRectFThree.left + imageBoxRectFThree.width() * scaleFactor;
            imageBoxRectFThree.bottom = imageBoxRectFThree.top + imageBoxRectFThree.height() * scaleFactor;

            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoom, imageBoxRectF);
            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoomOne, imageBoxRectFOne);
            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoomTwo, imageBoxRectFTwo);
            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoomThree, imageBoxRectFThree);
            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (checkHalfRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            }  else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect)) && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect)) && (!checkHalfRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }

        } else if (size == 5) {
            imageBoxRectF.right = imageBoxRectF.left + imageBoxRectF.width() * scaleFactor;
            imageBoxRectF.bottom = imageBoxRectF.top + imageBoxRectF.height() * scaleFactor;

            imageBoxRectFOne.right = imageBoxRectFOne.left + imageBoxRectFOne.width() * scaleFactor;
            imageBoxRectFOne.bottom = imageBoxRectFOne.top + imageBoxRectFOne.height() * scaleFactor;

            imageBoxRectFTwo.right = imageBoxRectFTwo.left + imageBoxRectFTwo.width() * scaleFactor;
            imageBoxRectFTwo.bottom = imageBoxRectFTwo.top + imageBoxRectFTwo.height() * scaleFactor;

            imageBoxRectFThree.right = imageBoxRectFThree.left + imageBoxRectFThree.width() * scaleFactor;
            imageBoxRectFThree.bottom = imageBoxRectFThree.top + imageBoxRectFThree.height() * scaleFactor;

            imageBoxRectFFour.right = imageBoxRectFFour.left + imageBoxRectFFour.width() * scaleFactor;
            imageBoxRectFFour.bottom = imageBoxRectFFour.top + imageBoxRectFFour.height() * scaleFactor;

            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoom, imageBoxRectF);
            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoomOne, imageBoxRectFOne);
            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoomThree, imageBoxRectFThree);
            imageMatrixAfterZoom.mapRect(imageBoxInViewAfterZoomFour, imageBoxRectFFour);

            if (checkHalfRectIntersection(imageBoxInView, CropImageView.mCropRect)
                    && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect))
                    && (checkHalfRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (checkHalfRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))
                    && (checkHalfRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))
                    && (checkHalfRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(faceDetected);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            }  else if ((!checkSceondRectIntersection(imageBoxInView, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewOne, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewTwo, CropImageView.mCropRect))
                    && (!checkSceondRectIntersection(imageBoxInViewThree, CropImageView.mCropRect))
                    && (!checkHalfRectIntersection(imageBoxInViewFour, CropImageView.mCropRect))) {
                uCropActivity.setCautionText(noFaces);
                uCropActivity.enableDiableGoCartoonButtom(true, 1.0f);
            } else {
                uCropActivity.setCautionText(multipleFaces);
                uCropActivity.enableDiableGoCartoonButtom(false, 0.1f);

            }
        }

        initialDistance = distanceZoom;

    }


}
