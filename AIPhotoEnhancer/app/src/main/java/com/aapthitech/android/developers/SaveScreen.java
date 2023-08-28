package com.aapthitech.android.developers;

import static com.aapthitech.android.developers.Activities.MainActivity.mainActivity;
import static com.aapthitech.android.developers.Data.CommonMethods.commonMethods;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aapthitech.android.developers.Activities.AIEditor;
import com.aapthitech.android.developers.Activities.CartoonSelfi;
import com.aapthitech.android.developers.Activities.MainActivity;
import com.aapthitech.android.developers.Activities.RemoveObject;
import com.aapthitech.android.developers.Data.RemoteConfig;
import com.aapthitech.android.developers.databinding.ActivitySaveScreenBinding;

import java.io.File;

public class SaveScreen extends AppCompatActivity {
    ActivitySaveScreenBinding saveScreenBinding;
    String imagePath;

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    File file;
    int fileValue;
    String fn;
    public String savePath;
    Uri imgUri = null;
    private String filename;
    String finalSavedImagePath;
    String pictureType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        saveScreenBinding = ActivitySaveScreenBinding.inflate(getLayoutInflater());
        View view = saveScreenBinding.getRoot();
        setContentView(view);
        saveScreenBinding.savedImage.setImageBitmap(mainActivity.globalBitmap);
        commonMethods.loadNextNativeAdFlor(SaveScreen.this, saveScreenBinding.nativeAdSave);
        savePath = getIntent().getStringExtra("savedImage");
        finalSavedImagePath = savePath;
        showInterstialAdOnSave();
        pictureType = getIntent().getStringExtra("PICTURE");

        String imagePath = getIntent().getStringExtra("SAVED_IMG");
        checkPictype(pictureType);
        if (imagePath != null) {
            File imageFile = new File(imagePath);


        }
        saveScreenBinding.shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImages();
            }
        });
        saveScreenBinding.goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SaveScreen.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        saveScreenBinding.cartoonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SaveScreen.this, CartoonSelfi.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        saveScreenBinding.erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SaveScreen.this, RemoveObject.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        saveScreenBinding.removeBgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SaveScreen.this, BackgroundChanger.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        saveScreenBinding.dehaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SaveScreen.this, AIEditor.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public void shareImages() {

        if (fileValue == 0) {
            if (savePath != null) {
                imgUri = FileProvider.getUriForFile(SaveScreen.this, getApplicationContext().getPackageName() + ".provider", new File(savePath));
            }
        } else {
            if (fn != null) {
                imgUri = FileProvider.getUriForFile(SaveScreen.this, getApplicationContext().getPackageName() + ".provider", new File(fn));
            }

        }
        File file = new File(savePath);
        System.out.println(file);
        System.out.println(imgUri);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name));
        String shareMessage = "I'm using " + getResources().getString(R.string.app_name) + " ! Get the app free at ";
        shareMessage = shareMessage + " : https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
        startActivity(sharingIntent);
    }

    private void showInterstialAdOnSave() {
        boolean adLoaded = false;
        if (RemoteConfig.getRemoteConfig() != null && RemoteConfig.getRemoteConfig().getShowInterstitial() != null && RemoteConfig.getRemoteConfig().getShowInterstitialOnSave() != null) {
            if (RemoteConfig.getRemoteConfig().getShowInterstitial().equals("true") && RemoteConfig.getRemoteConfig().getShowInterstitialOnSave().equals("true")) {
                commonMethods.showGoogleAd((Activity) SaveScreen.this, SaveScreen.this);
            }
        }
        ;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    private void checkPictype(String pictureType) {
        if (!pictureType.equals("DemoImages")) {
            saveScreenBinding.erase.setVisibility(View.GONE);
            saveScreenBinding.lensBlur.setVisibility(View.GONE);
            saveScreenBinding.brighten.setVisibility(View.GONE);
            saveScreenBinding.colorize.setVisibility(View.GONE);
            saveScreenBinding.descratchSave.setVisibility(View.GONE);
            saveScreenBinding.dehaze.setVisibility(View.GONE);
            saveScreenBinding.cartoonSave.setVisibility(View.VISIBLE);
            saveScreenBinding.enhance.setVisibility(View.VISIBLE);
        } else {
            saveScreenBinding.erase.setVisibility(View.GONE);
            saveScreenBinding.lensBlur.setVisibility(View.GONE);
            saveScreenBinding.brighten.setVisibility(View.GONE);
            saveScreenBinding.colorize.setVisibility(View.GONE);
            saveScreenBinding.descratchSave.setVisibility(View.GONE);
            saveScreenBinding.dehaze.setVisibility(View.GONE);
            saveScreenBinding.cartoonSave.setVisibility(View.VISIBLE);
            saveScreenBinding.enhance.setVisibility(View.VISIBLE);
        }
    }

}