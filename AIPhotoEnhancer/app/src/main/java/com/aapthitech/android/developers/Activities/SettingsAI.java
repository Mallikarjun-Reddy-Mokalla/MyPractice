package com.aapthitech.android.developers.Activities;

import static com.aapthitech.android.developers.Data.CommonMethods.commonMethods;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aapthitech.android.developers.BuildConfig;
import com.aapthitech.android.developers.Data.RemoteConfig;
import com.aapthitech.android.developers.IAP.PremiumScreen;
import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.ActivitySettingsAiBinding;
import com.aapthitech.android.developers.databinding.RateUsBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class SettingsAI extends AppCompatActivity {
    ActivitySettingsAiBinding settingsAiBinding;
    Dialog rateDialog;
    RateUsBinding rateUsBinding;
    public static BottomSheetBehavior sheetBehavior;
    ConstraintLayout constraintLayout;
    int reviewCount = 0;

    String versionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        settingsAiBinding = ActivitySettingsAiBinding.inflate(getLayoutInflater());
        View view = settingsAiBinding.getRoot();
        setContentView(view);
        // Set version information to TextView
        versionInfo = BuildConfig.VERSION_NAME;

        settingsAiBinding.versionNbr.setText(versionInfo);
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
                startActivity(new Intent(SettingsAI.this, PremiumScreen.class).putExtra("PRO_FROM", "SETTINGS"));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
        settingsAiBinding.sendFeedLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsAI.this, FeedBack.class).putExtra("INTENT_FROM", "SETTINGS"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        settingsAiBinding.rateUsLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openrateDialog();
            }
        });
        settingsAiBinding.termsConLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCustomTab("https://www.hangoverstudios.com/games/privacy.html");
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        settingsAiBinding.privacyLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCustomTab("https://www.hangoverstudios.com/games/privacy.html");
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (rateDialog != null && rateDialog.isShowing()) {
            rateDialog.dismiss();
        }
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


    private void openrateDialog() {
        rateDialog = new Dialog(SettingsAI.this);
        rateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rateUsBinding = RateUsBinding.inflate(getLayoutInflater());
        rateDialog.setContentView(rateUsBinding.getRoot());


        rateDialog.setCancelable(true);
        rateDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        rateDialog.getWindow().setGravity(Gravity.BOTTOM);
        if (rateDialog != null) {
            rateDialog.show();
        }
        rateUsBinding.reviewStar1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 1;
                if (rateDialog != null && rateDialog.isShowing()) {
                    rateDialog.dismiss();
                }
                rateUsBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                rateUsBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                rateUsBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                rateUsBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                sendFeedBacktoMail();
            }
        });
        rateUsBinding.reviewStar2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 2;
                if (rateDialog != null && rateDialog.isShowing()) {
                    rateDialog.dismiss();
                }
                rateUsBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                rateUsBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                rateUsBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                sendFeedBacktoMail();

            }
        });
        rateUsBinding.reviewStar3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 3;
                if (rateDialog != null && rateDialog.isShowing()) {
                    rateDialog.dismiss();
                }
                rateUsBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                rateUsBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                sendFeedBacktoMail();
            }
        });
        rateUsBinding.reviewStar4.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 4;
                if (rateDialog != null && rateDialog.isShowing()) {
                    rateDialog.dismiss();
                }
                rateUsBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                ratingPlaystore();
            }
        });
        rateUsBinding.reviewStar5.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 5;
                if (rateDialog != null && rateDialog.isShowing()) {
                    rateDialog.dismiss();
                }
                rateUsBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                rateUsBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                ratingPlaystore();
            }
        });
        rateUsBinding.rateHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reviewCount == 0) {
                    Toast.makeText(SettingsAI.this, "Selcet atleast one Star", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void launchCustomTab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

}