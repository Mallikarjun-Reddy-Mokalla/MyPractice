package com.aapthitech.android.developers.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aapthitech.android.developers.BackgroundChanger;
import com.aapthitech.android.developers.Data.RemoteConfig;
import com.aapthitech.android.developers.IAP.PremiumScreen;
import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.ActivitySettingsAiBinding;
import com.aapthitech.android.developers.databinding.DeleteConfirmDialogBinding;
import com.aapthitech.android.developers.databinding.RateUsBinding;
import com.aapthitech.android.developers.databinding.SaveSheetDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class SettingsAI extends AppCompatActivity {
    ActivitySettingsAiBinding settingsAiBinding;
    Dialog rateDialog;
    RateUsBinding rateUsBinding;
    public static BottomSheetBehavior sheetBehavior;
    ConstraintLayout constraintLayout;
    int reviewCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        settingsAiBinding = ActivitySettingsAiBinding.inflate(getLayoutInflater());
        View view = settingsAiBinding.getRoot();
        setContentView(view);


        if (RemoteConfig.getRemoteConfig().getEnableIAPflag() != null) {
            if (RemoteConfig.getRemoteConfig().getEnableIAPflag().equals("true")) {
                settingsAiBinding.premiumCard.setVisibility(View.VISIBLE);

            } else {
                settingsAiBinding.premiumCard.setVisibility(View.GONE);

            }
        }
        settingsAiBinding.onBackSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        settingsAiBinding.goPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsAI.this, PremiumScreen.class).putExtra("PRO_FROM","SETTINGS"));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
        settingsAiBinding.sendFeedLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsAI.this, FeedBack.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        settingsAiBinding.rateUsLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        BottomSheetBehavior from = BottomSheetBehavior.from(settingsAiBinding.bottomRateSheet.rateCardCon);
        sheetBehavior = from;
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        settingsAiBinding.bottomRateSheet.reviewStar1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 1;
                settingsAiBinding.bottomRateSheet.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                settingsAiBinding.bottomRateSheet.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                settingsAiBinding.bottomRateSheet.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                settingsAiBinding.bottomRateSheet.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                sendFeedBacktoMail();
            }
        });
        settingsAiBinding.bottomRateSheet.reviewStar2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 2;
                settingsAiBinding.bottomRateSheet.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                settingsAiBinding.bottomRateSheet.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                settingsAiBinding.bottomRateSheet.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                sendFeedBacktoMail();

            }
        });
        settingsAiBinding.bottomRateSheet.reviewStar3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 3;
                settingsAiBinding.bottomRateSheet.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                settingsAiBinding.bottomRateSheet.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                sendFeedBacktoMail();
            }
        });
        settingsAiBinding.bottomRateSheet.reviewStar4.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 4;
                settingsAiBinding.bottomRateSheet.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                ratingPlaystore();
            }
        });
        settingsAiBinding.bottomRateSheet.reviewStar5.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 5;
                settingsAiBinding.bottomRateSheet.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                settingsAiBinding.bottomRateSheet.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                ratingPlaystore();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    private void ratingPlaystore() {

        Intent ratingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
        startActivity(ratingIntent);
    }

    private void sendFeedBacktoMail() {
     /*   Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:mallikarjunreddy900@gmail.com?subject= AI Enhancer Feedback ");
        intent.setData(data);
        intent.putExtra(Intent.EXTRA_TEXT, "Dear AI Enhancer Team,\n\nI would like to provide the following feedback:\n\n");
        startActivity(intent);*/
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mallikarjunreddy900@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "AI Enhancer Feedback");
        intent.putExtra(Intent.EXTRA_TEXT, "Dear AI Enhancer Team,\n\nI would like to provide the following feedback:\n\n");
        startActivity(Intent.createChooser(intent, "Send Feedback"));

    }

}