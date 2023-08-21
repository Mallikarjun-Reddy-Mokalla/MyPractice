package com.aapthitech.android.developers.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.aapthitech.android.developers.R;

public class ContentView extends View {
    private boolean isDragging = false;

    private Bitmap beforeImage;
    private Bitmap afterImage;
    private float progress = 0.5f;
    private Paint linePaint;
    private float linePosition;
    private Drawable directionIcon;
    private Paint textPaint;
    private Paint textPaintButton;
    private String topRightText = "Before";
    private String topLeftText = "After";
    private String bottomText = "";

    private String fontFamily = "sans-serif";
    private int fontStyle = Typeface.BOLD;
    private float textSize = 30f;
    private float textBottomSize = 32f;
    private int bitmapHeight, bitmapWidth;
    private CardView parentCardView;

    public ContentView(Context context) {
        super(context);

        init();
    }

    private void init() {
        // Initialize line paint
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(2f);
        directionIcon = getResources().getDrawable(R.drawable.direction);
// Initialize text paint
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.create(fontFamily, fontStyle));

        textPaintButton = new Paint();
        textPaintButton.setColor(Color.WHITE);
        textPaintButton.setTextSize(textBottomSize);
        textPaintButton.setTypeface(Typeface.create(fontFamily, fontStyle));



    }
    public void setParentCardView(CardView cardView) {
        parentCardView = cardView;
    }


    public ContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();


        // Process custom attributes
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ContentView);
        int beforeImageResId = typedArray.getResourceId(R.styleable.ContentView_beforeImage, 0);
        int afterImageResId = typedArray.getResourceId(R.styleable.ContentView_afterImage, 0);
        int bitmapWidth = typedArray.getDimensionPixelSize(R.styleable.ContentView_bitmapWidth, 0);
        int bitmapHeight = typedArray.getDimensionPixelSize(R.styleable.ContentView_bitmapHeight, 0);
        String bottomText = typedArray.getString(R.styleable.ContentView_bottomText);

        if (beforeImageResId != 0 && afterImageResId != 0 && bitmapWidth > 0 && bitmapHeight > 0) {
            Bitmap beforeBitmap = BitmapFactory.decodeResource(getResources(), beforeImageResId);
            Bitmap afterBitmap = BitmapFactory.decodeResource(getResources(), afterImageResId);

            // Resize the bitmaps to the provided width and height
            Bitmap resizedBeforeBitmap = Bitmap.createScaledBitmap(beforeBitmap, bitmapWidth, bitmapHeight, true);
            Bitmap resizedAfterBitmap = Bitmap.createScaledBitmap(afterBitmap, bitmapWidth, bitmapHeight, true);

            setImages(resizedBeforeBitmap, resizedAfterBitmap);
        }
        typedArray.recycle();
        // Store the bottom text
        if (bottomText != null) {
            this.bottomText = bottomText;
        }
    }

    public ContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /*   public void setImages(Bitmap beforeImage, Bitmap afterImage) {
           this.beforeImage = Bitmap.createScaledBitmap(beforeImage, 300, 300, true);
           this.afterImage = Bitmap.createScaledBitmap(afterImage, 300, 300, true);
           invalidate();
       }*/
    public void setImages(Bitmap beforeImage, Bitmap afterImage) {
        this.beforeImage = beforeImage;
        this.afterImage = afterImage;
    }

    /*    public void setImages(Bitmap beforeImage, Bitmap afterImage) {
            int cornerRadius = 16; // Adjust this value as needed
            Bitmap roundedBeforeBitmap = getRoundedBitmap(beforeImage, cornerRadius);
            Bitmap roundedAfterBitmap = getRoundedBitmap(afterImage, cornerRadius);

            this.beforeImage = roundedBeforeBitmap;
            this.afterImage = roundedAfterBitmap;
            invalidate();
        }*/

    public void setBitmapHeight(int height) {
        // Update the internal bitmapHeight attribute
        bitmapHeight = height;
        // Request a redraw of the view
        invalidate();
    }

    public void setBitmapWidth(int width) {
        // Update the internal bitmapWidth attribute
        bitmapWidth = width;
        // Request a redraw of the view
        invalidate();
    }

    private Bitmap getRoundedBitmap(Bitmap sourceBitmap, int cornerRadius) {
        Bitmap roundedBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawRoundRect(new RectF(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), cornerRadius, cornerRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sourceBitmap, 0, 0, paint);

        return roundedBitmap;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        textPaint.setTypeface(Typeface.create(fontFamily, fontStyle));
        textPaintButton.setTypeface(Typeface.create(fontFamily, fontStyle));
        invalidate();
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
        textPaint.setTypeface(Typeface.create(fontFamily, fontStyle));
        textPaintButton.setTypeface(Typeface.create(fontFamily, fontStyle));
        invalidate();
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        textPaintButton.setTextSize(textSize);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (beforeImage != null && afterImage != null) {
            // Calculate the position to split the images based on the progress
            int splitPosition = (int) (getWidth() * progress);

            // Draw the before and after images
            canvas.drawBitmap(beforeImage, new Rect(0, 0, splitPosition, getHeight()), new Rect(0, 0, splitPosition, getHeight()), null);
            canvas.drawBitmap(afterImage, new Rect(splitPosition, 0, getWidth(), getHeight()), new Rect(splitPosition, 0, getWidth(), getHeight()), null);
            linePosition = splitPosition;
            canvas.drawLine(linePosition, 0, linePosition, beforeImage.getHeight(), linePaint);
            drawDirectionIcon(canvas);
            // Draw text on top right corner
            float topRightTextWidth = textPaint.measureText(topRightText);
            float topRightTextX = getWidth() - topRightTextWidth - 30; // Adjust the padding as needed
            float topRightTextY = 50; // Adjust the vertical position as needed
//                canvas.drawText(topRightText, topRightTextX, topRightTextY, textPaint);
            if (linePosition < topRightTextX) {
//                canvas.drawText(topRightText, topRightTextX, topRightTextY, textPaint);
            }
            // Draw text on top left corner
            float topLeftTextX = 30; // Adjust the padding as needed
            float topLeftTextY = 50; // Adjust the vertical position as needed
//                canvas.drawText(topLeftText, topLeftTextX, topLeftTextY, textPaint);
            if (linePosition > topLeftTextY + 50) {
//                canvas.drawText(topLeftText, topLeftTextX, topLeftTextY, textPaint);
            }
        }

        float bottomTextWidth = textPaintButton.measureText(bottomText);
        float bottomTextX = (getWidth() - bottomTextWidth) / 2; // Center horizontally
        float bottomTextY = getHeight() - 20; // Position above the bottom edge

        canvas.drawText(bottomText, bottomTextX, bottomTextY, textPaintButton);

    }

    private void drawDirectionIcon(Canvas canvas) {
     /*   int iconWidth = directionIcon.getIntrinsicWidth();
        int iconHeight = directionIcon.getIntrinsicHeight();*/
        int iconWidth = 60;
        int iconHeight = 60;
        int iconLeft = (int) (linePosition - iconWidth / 2f);
        int iconTop = beforeImage.getHeight() / 2 - iconHeight / 2;

        // Set the bounds for the direction icon
        directionIcon.setBounds(iconLeft, iconTop, iconLeft + iconWidth, iconTop + iconHeight);

        // Draw the direction icon
        directionIcon.draw(canvas);

    }

    /* @Override
     public boolean onTouchEvent(MotionEvent event) {
         // Update the progress based on the touch position
         progress = event.getX() / getWidth();
         linePosition = event.getX();
         invalidate();
         return true;
     }*/
     @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Rect iconBounds = directionIcon.getBounds();
                if (iconBounds.contains((int) event.getX(), (int) event.getY())) {
                    isDragging = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    // Calculate the new position while clamping within view bounds
                    float newX = Math.max(0, Math.min(event.getX(), getWidth()));
                    progress = newX / getWidth();
                    linePosition = newX;


                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (parentCardView != null) {
                    parentCardView.performClick(); // Trigger click on the CardView
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                invalidate();
                break;
        }
        return true;
    }


}
