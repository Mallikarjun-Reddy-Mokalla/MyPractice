package com.yalantis.ucrop.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yalantis.ucrop.DrawTextVlue;
import com.yalantis.ucrop.R;
import com.yalantis.ucrop.callback.CropBoundsChangeListener;
import com.yalantis.ucrop.callback.OverlayViewChangeListener;

import androidx.annotation.NonNull;

public class UCropView extends FrameLayout implements DrawTextVlue {

    public static GestureCropImageView mGestureCropImageView;
    private static OverlayView mViewOverlay;
    TransformImageView transformImageView;

    public UCropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UCropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        transformImageView = new TransformImageView(context);
        LayoutInflater.from(context).inflate(R.layout.ucrop_view, this, true);
        mGestureCropImageView = findViewById(R.id.image_view_crop);
        mViewOverlay = findViewById(R.id.view_overlay);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ucrop_UCropView);
        mViewOverlay.processStyledAttributes(a);
        mGestureCropImageView.processStyledAttributes(a);
        a.recycle();

        setListenersToViews();
    }

/*
    public static void autoScaleToFaceRect(Rect faceRect) {
        // Get the dimensions of the ImageView and the faceRect
        int imageViewWidth = mGestureCropImageView.getWidth();
        int imageViewHeight = mGestureCropImageView.getHeight();
        int faceRectWidth = faceRect.width();
        int faceRectHeight = faceRect.height();

        // Calculate the scaling factor required to fit the faceRect within the ImageView bounds
        float scaleX = (float) imageViewWidth / faceRectWidth;
        float scaleY = (float) imageViewHeight / faceRectHeight;
        float scale = Math.min(scaleX, scaleY);

        // Set the image scale to fit the faceRect within the ImageView bounds
        mGestureCropImageView.setScaleType(ImageView.ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale, faceRect.centerX(), faceRect.centerY());
        mGestureCropImageView.setImageMatrix(matrix);
    }
*/

    private void setListenersToViews() {
        mGestureCropImageView.setCropBoundsChangeListener(new CropBoundsChangeListener() {
            @Override
            public void onCropAspectRatioChanged(float cropRatio) {
                mViewOverlay.setTargetAspectRatio(cropRatio);
            }
        });
        mViewOverlay.setOverlayViewChangeListener(new OverlayViewChangeListener() {
            @Override
            public void onCropRectUpdated(RectF cropRect) {
                mGestureCropImageView.setCropRect(cropRect);
            }
        });
    }

    public static void setValueToText(String text,String text2,String text3) {
        mViewOverlay.setTextToDraw(text ,text2,text3);
    }

    public static void setValueToTextCrop(String text, String text2) {
        mViewOverlay.setTextToDrawCrop(text, text2);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @NonNull
    public GestureCropImageView getCropImageView() {
        return mGestureCropImageView;
    }

    @NonNull
    public OverlayView getOverlayView() {
        return mViewOverlay;
    }

    /**
     * Method for reset state for UCropImageView such as rotation, scale, translation.
     * Be careful: this method recreate UCropImageView instance and reattach it to layout.
     */
    public void resetCropImageView() {
        removeView(mGestureCropImageView);
        mGestureCropImageView = new GestureCropImageView(getContext());
        setListenersToViews();
        mGestureCropImageView.setCropRect(getOverlayView().getCropViewRect());
        addView(mGestureCropImageView, 0);
    }

    @Override
    public void assignTextValue(String text) {
    }
}