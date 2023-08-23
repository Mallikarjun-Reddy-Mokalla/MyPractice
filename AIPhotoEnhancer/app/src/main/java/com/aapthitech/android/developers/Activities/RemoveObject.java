package com.aapthitech.android.developers.Activities;

import static com.aapthitech.android.developers.Activities.MainActivity.mainActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.aapthitech.android.developers.HoverView2;
import com.aapthitech.android.developers.databinding.ActivityRemoveObjectBinding;

public class RemoveObject extends AppCompatActivity {
    ActivityRemoveObjectBinding removeObjectBinding;
    public HoverView2 mHoverView;
    double mDensity;
    int viewWidth;
    int viewHeight;
    int bmWidth;
    int bmHeight;
    int actionBarHeight;
    int bottombarHeight;
    double bmRatio;
    double viewRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        removeObjectBinding = ActivityRemoveObjectBinding.inflate(getLayoutInflater());
        View view = removeObjectBinding.getRoot();
        setContentView(view);
        removeObjectBinding.magicIconLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeObjectBinding.magicToolsLayout.setVisibility(View.VISIBLE);
                removeObjectBinding.toolLay.setVisibility(View.GONE);
                removeObjectBinding.seeKLay.setVisibility(View.GONE);
                removeObjectBinding.saveMagicLayout.setVisibility(View.GONE);
            }
        });
        removeObjectBinding.magicTabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeObjectBinding.magicToolsLayout.setVisibility(View.GONE);
                removeObjectBinding.toolLay.setVisibility(View.VISIBLE);
                removeObjectBinding.seeKLay.setVisibility(View.VISIBLE);
                removeObjectBinding.saveMagicLayout.setVisibility(View.VISIBLE);
            }
        });
        removeObjectBinding.checkedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeObjectBinding.saveMagicLayout.setVisibility(View.VISIBLE);
            }
        });
        removeObjectBinding.removeObjBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        /* setting the bitmap to the view */
        drawtheObjectToRemove(mainActivity.globalBitmap, RemoveObject.this);
        removeObjectBinding.seekbarBrushSize.incrementProgressBy(20);
        removeObjectBinding.seekbarBrushSize.setMax(200);
        removeObjectBinding.seekbarBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar != null) {
                    if (mHoverView != null) {
                        mHoverView.setEraseBrushSize(seekBar.getProgress());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });

        /*removeObjectBinding.seekbarBrushSize.incrementProgressBy(20);
        removeObjectBinding.seekbarBrushSize.setMax(200);
        removeObjectBinding.seekbarBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar != null) {
                    if (mHoverView != null) {
                        mHoverView.setPointerOffset(seekBar.getProgress());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

        });*/
    }

    public void drawtheObjectToRemove(Bitmap mBitmap, Context context) {

        mDensity = getResources().getDisplayMetrics().density;
        actionBarHeight = (int) (110 * mDensity);
        bottombarHeight = (int) (60 * mDensity);
        viewWidth = getResources().getDisplayMetrics().widthPixels;
        viewHeight = getResources().getDisplayMetrics().heightPixels - actionBarHeight - bottombarHeight;
        viewRatio = (double) viewHeight / (double) viewWidth;
        if (mBitmap != null) {
            bmRatio = (double) mBitmap.getHeight() / (double) mBitmap.getWidth();
            if (bmRatio < viewRatio) {
                bmWidth = viewWidth;
                bmHeight = (int) (((double) viewWidth) * ((double) (mBitmap.getHeight()) / (double) (mBitmap.getWidth())));
            } else {
                bmHeight = viewHeight;
                bmWidth = (int) (((double) viewHeight) * ((double) (mBitmap.getWidth()) / (double) (mBitmap.getHeight())));
            }
            mBitmap = Bitmap.createScaledBitmap(mBitmap, bmWidth, bmHeight, false);
            mHoverView = new HoverView2(this, mBitmap, bmWidth, bmHeight, viewWidth, viewHeight);
            mHoverView.setLayoutParams(new ViewGroup.LayoutParams(viewWidth, viewHeight));
            removeObjectBinding.removeObjLayout.addView(mHoverView);
            Bitmap mutableBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);


        }
    }

}