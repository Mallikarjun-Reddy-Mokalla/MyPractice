package com.aapthitech.android.developers.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.ActivityMainBinding;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);
        Glide.with(this).load(R.drawable.ai_enhancer).into(mainBinding.gifView);// to load the gif file
        /* set card to view to perform the click event */
        mainBinding.removeObj.setParentCardView(mainBinding.removeObjCard);
        mainBinding.removeBg.setParentCardView(mainBinding.removeBgCard);
        mainBinding.dehaze.setParentCardView(mainBinding.dehazeCard);
        mainBinding.descratch.setParentCardView(mainBinding.descratchCard);
        mainBinding.cortoonSelfie.setParentCardView(mainBinding.cartoonSelfiCard);
        mainBinding.lensBlur.setParentCardView(mainBinding.lensBlurCard);
        mainBinding.cororizeView.setParentCardView(mainBinding.colorizeCard);
        mainBinding.brighter.setParentCardView(mainBinding.brighterCard);
        mainBinding.dehaze.setParentCardView(mainBinding.dehazeCard);
        /*on click events*/
        mainBinding.aiEnhanceLay.setOnClickListener(this);
        mainBinding.removeBgCard.setOnClickListener(this);
        mainBinding.dehazeCard.setOnClickListener(this);
        mainBinding.descratchCard.setOnClickListener(this);
        mainBinding.cartoonSelfiCard.setOnClickListener(this);
        mainBinding.lensBlurCard.setOnClickListener(this);
        mainBinding.colorizeCard.setOnClickListener(this);
        mainBinding.brighterCard.setOnClickListener(this);
        mainBinding.removeObjCard.setOnClickListener(this);
        mainBinding.mainSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mainBinding.drawingLayout.openDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, SettingsAI.class));
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* if (mainBinding.drawingLayout.isDrawerOpen(GravityCompat.START)) {
            mainBinding.drawingLayout.closeDrawer(GravityCompat.START);
        }*/

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.remove_obj_card:
                startActivity(new Intent(MainActivity.this, RemoveObject.class));
                break;
            case R.id.ai_enhance_lay:
                startActivity(new Intent(MainActivity.this, AIEditor.class)
                        .putExtra("TITLE", getString(R.string.enhance_title))
                        .putExtra("PRO_TAG", getString(R.string.pro_enhance))
                );
                break;
            case R.id.remove_bg_card:
                startActivity(new Intent(MainActivity.this, AIEditor.class)
                        .putExtra("TITLE", getString(R.string.removebg))
                        .putExtra("PRO_TAG", getString(R.string.remove_pro_bg))
                );
                break;
            case R.id.dehaze_card:
                startActivity(new Intent(MainActivity.this, AIEditor.class)
                        .putExtra("TITLE", getString(R.string.dehaze))
                        .putExtra("PRO_TAG", getString(R.string.pro_dehaze))
                );
                break;
            case R.id.descratch_card:
                startActivity(new Intent(MainActivity.this, AIEditor.class)
                        .putExtra("TITLE", getString(R.string.descratch))
                        .putExtra("PRO_TAG", getString(R.string.pro_descratch))
                );
                break;
            case R.id.cartoon_selfi_card:
                startActivity(new Intent(MainActivity.this, CartoonSelfi.class)
                        .putExtra("TITLE", getString(R.string.cartton))
                        .putExtra("PRO_TAG", getString(R.string.pro_cartoon))
                );
                break;
            case R.id.lens_blur_card:
                startActivity(new Intent(MainActivity.this, AIEditor.class)
                        .putExtra("TITLE", getString(R.string.lens_blur))
                        .putExtra("PRO_TAG", getString(R.string.pro_lens))
                );
                break;
            case R.id.colorize_card:
                startActivity(new Intent(MainActivity.this, AIEditor.class)
                        .putExtra("TITLE", getString(R.string.colorize))
                        .putExtra("PRO_TAG", getString(R.string.pro_colorize))
                );
                break;
            case R.id.brighter_card:
                startActivity(new Intent(MainActivity.this, AIEditor.class)
                        .putExtra("TITLE", getString(R.string.brighten))
                        .putExtra("PRO_TAG", getString(R.string.pro_brighten))
                );
                break;

        }

    }
}