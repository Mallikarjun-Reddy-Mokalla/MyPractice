package com.aapthitech.android.developers.TouchEvents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MultiTouchListener2 implements OnTouchListener, OnGestureListener {

    private static final int INVALID_POINTER_ID = -1;
    boolean isRotateEnabled = true;
    boolean isTranslateEnabled = true;
    boolean isScaleEnabled = true;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mPrevX;
    private float mPrevY;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector gd;
    static View v;
    RelativeLayout tv;


    public MultiTouchListener2(ImageView imageView, Context mContext) {

        mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
        gd = new GestureDetector(this);
        gd.setOnDoubleTapListener(new OnDoubleTapListener() {
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

            public boolean onDoubleTapEvent(MotionEvent e) {

                return false;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {

                /*Intent mIntent = new Intent(mContext, ProfileActivity.class);
                mContext.startActivity(mIntent);
                ((Activity) mContext).finish();*/


                return true;
            }
        });


    }

    public MultiTouchListener2(View View) {
        mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
        tv = (RelativeLayout) View;
        gd = new GestureDetector(this);


    }

    private static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }

        return degrees;
    }

    private static void move(View view, TransformInfo info) {
        v = view;
        computeRenderOffset(view, info.pivotX, info.pivotY);
        adjustTranslation(view, info.deltaX, info.deltaY);
        float scale = view.getScaleX() * info.deltaScale;
        scale = Math.max(info.minimumScale, Math.min(info.maximumScale, scale));
        view.setScaleX(scale);
        view.setScaleY(scale);

        float rotation = adjustAngle(view.getRotation() + info.deltaAngle);
        view.setRotation(rotation);
    }

    private static void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = {deltaX, deltaY};
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
    }

    private static void computeRenderOffset(View view, float pivotX, float pivotY) {
        if (view.getPivotX() == pivotX && view.getPivotY() == pivotY) {
            return;
        }

        float[] prevPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(prevPoint);

        view.setPivotX(pivotX);
        view.setPivotY(pivotY);

        float[] currPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(currPoint);

        float offsetX = currPoint[0] - prevPoint[0];
        float offsetY = currPoint[1] - prevPoint[1];

        view.setTranslationX(view.getTranslationX() - offsetX);
        view.setTranslationY(view.getTranslationY() - offsetY);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @SuppressLint("Range")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        v = view;
        boolean result = mScaleGestureDetector.onTouchEvent(view, event);
        boolean isScaling = result = mScaleGestureDetector.isInProgress();

        if (!isScaling) {
            result = gd.onTouchEvent(event);
        }


        if (!isTranslateEnabled) {
            return true;
        }

        int action = event.getAction();
        switch (action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                if (tv != null) tv.setAlpha(150);
                mPrevX = event.getX();
                mPrevY = event.getY();
                mActivePointerId = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex != -1) {
                    float currX = event.getX(pointerIndex);
                    float currY = event.getY(pointerIndex);

                    if (!mScaleGestureDetector.isInProgress()) {
                        adjustTranslation(view, currX - mPrevX, currY - mPrevY);
                    }
                }

                break;
            }

            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_UP:
                if (tv != null) tv.setAlpha(255);
                mActivePointerId = INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_POINTER_UP: {
                int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mPrevX = event.getX(newPointerIndex);
                    mPrevY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }

                break;
            }
        }

        return result ? result : mScaleGestureDetector.onTouchEvent(view, event);
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float mPivotX;
        private float mPivotY;
        private Vector2D mPrevSpanVector = new Vector2D();

        @Override
        public boolean onScaleBegin(View view, ScaleGestureDetector detector) {
            v = view;
            mPivotX = detector.getFocusX();
            mPivotY = detector.getFocusY();
            mPrevSpanVector.set(detector.getCurrentSpanVector());
            return true;
        }

        @Override
        public boolean onScale(View view, ScaleGestureDetector detector) {
            v = view;
            TransformInfo info = new TransformInfo();
            info.deltaScale = isScaleEnabled ? detector.getScaleFactor() : 1.0f;
            info.deltaAngle = isRotateEnabled ? Vector2D.getAngle(mPrevSpanVector, detector.getCurrentSpanVector()) : 0.0f;
            info.deltaX = isTranslateEnabled ? detector.getFocusX() - mPivotX : 0.0f;
            info.deltaY = isTranslateEnabled ? detector.getFocusY() - mPivotY : 0.0f;
            info.pivotX = mPivotX;
            info.pivotY = mPivotY;
            info.minimumScale = 0.1f;
            info.maximumScale = 10.0f;

            move(view, info);
            return false;
        }
    }

    private class TransformInfo {

        float deltaX;
        float deltaY;
        float deltaScale;
        float deltaAngle;
        float pivotX;
        float pivotY;
        float minimumScale;
        float maximumScale;
    }
}

class ScaleGestureDetector {
    private static final String TAG = "ScaleGestureDetector";


    public interface OnScaleGestureListener {

        public boolean onScale(View view, ScaleGestureDetector detector);


        public boolean onScaleBegin(View view, ScaleGestureDetector detector);


        public void onScaleEnd(View view, ScaleGestureDetector detector);
    }


    public static class SimpleOnScaleGestureListener implements OnScaleGestureListener {

        public boolean onScale(View view, ScaleGestureDetector detector) {
            return false;
        }

        public boolean onScaleBegin(View view, ScaleGestureDetector detector) {
            return true;
        }

        public void onScaleEnd(View view, ScaleGestureDetector detector) {
            // Intentionally empty
        }
    }


    private static final float PRESSURE_THRESHOLD = 0.67f;

    private final OnScaleGestureListener mListener;
    private boolean mGestureInProgress;

    private MotionEvent mPrevEvent;
    private MotionEvent mCurrEvent;

    private Vector2D mCurrSpanVector;
    private float mFocusX;
    private float mFocusY;
    private float mPrevFingerDiffX;
    private float mPrevFingerDiffY;
    private float mCurrFingerDiffX;
    private float mCurrFingerDiffY;
    private float mCurrLen;
    private float mPrevLen;
    private float mScaleFactor;
    private float mCurrPressure;
    private float mPrevPressure;
    private long mTimeDelta;

    private boolean mInvalidGesture;

    private int mActiveId0;
    private int mActiveId1;
    private boolean mActive0MostRecent;

    public ScaleGestureDetector(OnScaleGestureListener listener) {
        mListener = listener;
        mCurrSpanVector = new Vector2D();
    }

    public boolean onTouchEvent(View view, MotionEvent event) {
        final int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            reset(); // Start fresh
        }

        boolean handled = true;
        if (mInvalidGesture) {
            handled = false;
        } else if (!mGestureInProgress) {
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mActiveId0 = event.getPointerId(0);
                    mActive0MostRecent = true;
                }
                break;

                case MotionEvent.ACTION_UP:
                    reset();
                    break;

                case MotionEvent.ACTION_POINTER_DOWN: {
                    // We have a new multi-finger gesture
                    if (mPrevEvent != null) mPrevEvent.recycle();
                    mPrevEvent = MotionEvent.obtain(event);
                    mTimeDelta = 0;

                    int index1 = event.getActionIndex();
                    int index0 = event.findPointerIndex(mActiveId0);
                    mActiveId1 = event.getPointerId(index1);
                    if (index0 < 0 || index0 == index1) {
                        // Probably someone sending us a broken event stream.
                        index0 = findNewActiveIndex(event, mActiveId1, -1);
                        mActiveId0 = event.getPointerId(index0);
                    }
                    mActive0MostRecent = false;

                    setContext(view, event);

                    mGestureInProgress = mListener.onScaleBegin(view, this);
                    break;
                }
            }
        } else {
            switch (action) {
                case MotionEvent.ACTION_POINTER_DOWN: {
                    mListener.onScaleEnd(view, this);
                    final int oldActive0 = mActiveId0;
                    final int oldActive1 = mActiveId1;
                    reset();

                    mPrevEvent = MotionEvent.obtain(event);
                    mActiveId0 = mActive0MostRecent ? oldActive0 : oldActive1;
                    mActiveId1 = event.getPointerId(event.getActionIndex());
                    mActive0MostRecent = false;

                    int index0 = event.findPointerIndex(mActiveId0);
                    if (index0 < 0 || mActiveId0 == mActiveId1) {
                        index0 = findNewActiveIndex(event, mActiveId1, -1);
                        mActiveId0 = event.getPointerId(index0);
                    }

                    setContext(view, event);

                    mGestureInProgress = mListener.onScaleBegin(view, this);
                }
                break;

                case MotionEvent.ACTION_POINTER_UP: {
                    final int pointerCount = event.getPointerCount();
                    final int actionIndex = event.getActionIndex();
                    final int actionId = event.getPointerId(actionIndex);

                    boolean gestureEnded = false;
                    if (pointerCount > 2) {
                        if (actionId == mActiveId0) {
                            final int newIndex = findNewActiveIndex(event, mActiveId1, actionIndex);
                            if (newIndex >= 0) {
                                mListener.onScaleEnd(view, this);
                                mActiveId0 = event.getPointerId(newIndex);
                                mActive0MostRecent = true;
                                mPrevEvent = MotionEvent.obtain(event);
                                setContext(view, event);
                                mGestureInProgress = mListener.onScaleBegin(view, this);
                            } else {
                                gestureEnded = true;
                            }
                        } else if (actionId == mActiveId1) {
                            final int newIndex = findNewActiveIndex(event, mActiveId0, actionIndex);
                            if (newIndex >= 0) {
                                mListener.onScaleEnd(view, this);
                                mActiveId1 = event.getPointerId(newIndex);
                                mActive0MostRecent = false;
                                mPrevEvent = MotionEvent.obtain(event);
                                setContext(view, event);
                                mGestureInProgress = mListener.onScaleBegin(view, this);
                            } else {
                                gestureEnded = true;
                            }
                        }
                        mPrevEvent.recycle();
                        mPrevEvent = MotionEvent.obtain(event);
                        setContext(view, event);
                    } else {
                        gestureEnded = true;
                    }

                    if (gestureEnded) {
                        setContext(view, event);

                        final int activeId = actionId == mActiveId0 ? mActiveId1 : mActiveId0;
                        final int index = event.findPointerIndex(activeId);
                        mFocusX = event.getX(index);
                        mFocusY = event.getY(index);

                        mListener.onScaleEnd(view, this);
                        reset();
                        mActiveId0 = activeId;
                        mActive0MostRecent = true;
                    }
                }
                break;

                case MotionEvent.ACTION_CANCEL:
                    mListener.onScaleEnd(view, this);
                    reset();
                    break;

                case MotionEvent.ACTION_UP:
                    reset();
                    break;

                case MotionEvent.ACTION_MOVE: {
                    setContext(view, event);

                    if (mCurrPressure / mPrevPressure > PRESSURE_THRESHOLD) {
                        final boolean updatePrevious = mListener.onScale(view, this);

                        if (updatePrevious) {
                            mPrevEvent.recycle();
                            mPrevEvent = MotionEvent.obtain(event);
                        }
                    }
                }
                break;
            }
        }

        return handled;
    }

    private int findNewActiveIndex(MotionEvent ev, int otherActiveId, int removedPointerIndex) {
        final int pointerCount = ev.getPointerCount();

        final int otherActiveIndex = ev.findPointerIndex(otherActiveId);

        for (int i = 0; i < pointerCount; i++) {
            if (i != removedPointerIndex && i != otherActiveIndex) {
                return i;
            }
        }
        return -1;
    }

    private void setContext(View view, MotionEvent curr) {
        if (mCurrEvent != null) {
            mCurrEvent.recycle();
        }

        mCurrEvent = MotionEvent.obtain(curr);

        mCurrLen = -1;
        mPrevLen = -1;
        mScaleFactor = -1;
        mCurrSpanVector.set(0.0f, 0.0f);

        final MotionEvent prev = mPrevEvent;

        final int prevIndex0 = prev.findPointerIndex(mActiveId0);
        final int prevIndex1 = prev.findPointerIndex(mActiveId1);
        final int currIndex0 = curr.findPointerIndex(mActiveId0);
        final int currIndex1 = curr.findPointerIndex(mActiveId1);

        if (prevIndex0 < 0 || prevIndex1 < 0 || currIndex0 < 0 || currIndex1 < 0) {
            mInvalidGesture = true;
            /*    Log.e(TAG, "Invalid MotionEvent stream detected.", new Throwable());*/
            if (mGestureInProgress) {
                mListener.onScaleEnd(view, this);
            }
            return;
        }

        final float px0 = prev.getX(prevIndex0);
        final float py0 = prev.getY(prevIndex0);
        final float px1 = prev.getX(prevIndex1);
        final float py1 = prev.getY(prevIndex1);
        final float cx0 = curr.getX(currIndex0);
        final float cy0 = curr.getY(currIndex0);
        final float cx1 = curr.getX(currIndex1);
        final float cy1 = curr.getY(currIndex1);

        final float pvx = px1 - px0;
        final float pvy = py1 - py0;
        final float cvx = cx1 - cx0;
        final float cvy = cy1 - cy0;

        mCurrSpanVector.set(cvx, cvy);

        mPrevFingerDiffX = pvx;
        mPrevFingerDiffY = pvy;
        mCurrFingerDiffX = cvx;
        mCurrFingerDiffY = cvy;

        mFocusX = cx0 + cvx * 0.5f;
        mFocusY = cy0 + cvy * 0.5f;
        mTimeDelta = curr.getEventTime() - prev.getEventTime();
        mCurrPressure = curr.getPressure(currIndex0) + curr.getPressure(currIndex1);
        mPrevPressure = prev.getPressure(prevIndex0) + prev.getPressure(prevIndex1);
    }

    private void reset() {
        if (mPrevEvent != null) {
            mPrevEvent.recycle();
            mPrevEvent = null;
        }
        if (mCurrEvent != null) {
            mCurrEvent.recycle();
            mCurrEvent = null;
        }
        mGestureInProgress = false;
        mActiveId0 = -1;
        mActiveId1 = -1;
        mInvalidGesture = false;
    }


    public boolean isInProgress() {
        return mGestureInProgress;
    }


    public float getFocusX() {
        return mFocusX;
    }


    public float getFocusY() {
        return mFocusY;
    }

    public float getCurrentSpan() {
        if (mCurrLen == -1) {
            final float cvx = mCurrFingerDiffX;
            final float cvy = mCurrFingerDiffY;
            mCurrLen = (float) Math.sqrt(cvx * cvx + cvy * cvy);
        }
        return mCurrLen;
    }

    public Vector2D getCurrentSpanVector() {
        return mCurrSpanVector;
    }

    public float getCurrentSpanX() {
        return mCurrFingerDiffX;
    }

    public float getCurrentSpanY() {
        return mCurrFingerDiffY;
    }


    public float getPreviousSpan() {
        if (mPrevLen == -1) {
            final float pvx = mPrevFingerDiffX;
            final float pvy = mPrevFingerDiffY;
            mPrevLen = (float) Math.sqrt(pvx * pvx + pvy * pvy);
        }
        return mPrevLen;
    }


    public float getPreviousSpanX() {
        return mPrevFingerDiffX;
    }


    public float getPreviousSpanY() {
        return mPrevFingerDiffY;
    }


    public float getScaleFactor() {
        if (mScaleFactor == -1) {
            mScaleFactor = getCurrentSpan() / getPreviousSpan();
        }
        return mScaleFactor;
    }


    public long getTimeDelta() {
        return mTimeDelta;
    }

    public long getEventTime() {
        return mCurrEvent.getEventTime();
    }
}






/*
public class Vector2D extends PointF {

    public Vector2D() {
        super();
    }

    public Vector2D(float x, float y) {
        super(x, y);
    }

    public static float getAngle(Vector2D vector1, Vector2D vector2) {
        vector1.normalize();
        vector2.normalize();
        double degrees = (180.0 / Math.PI) * (Math.atan2(vector2.y, vector2.x) - Math.atan2(vector1.y, vector1.x));
        return (float) degrees;
    }

    public void normalize() {
        float length = (float) Math.sqrt(x * x + y * y);
        x /= length;
        y /= length;
    }*/
