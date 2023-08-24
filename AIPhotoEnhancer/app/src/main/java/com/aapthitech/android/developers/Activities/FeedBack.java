package com.aapthitech.android.developers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.ActivityFeedBackBinding;

public class FeedBack extends AppCompatActivity {
    ActivityFeedBackBinding feedBackBinding;
    int reviewCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        feedBackBinding = ActivityFeedBackBinding.inflate(getLayoutInflater());
        View view = feedBackBinding.getRoot();
        setContentView(view);
        feedBackBinding.reviewStar1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount=1;
                feedBackBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
            }
        });
        feedBackBinding.reviewStar2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount=2;
                feedBackBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
            }
        });
        feedBackBinding.reviewStar3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount=3;
                feedBackBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));

            }
        });
        feedBackBinding.reviewStar4.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount=4;
                feedBackBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
            }
        });
        feedBackBinding.reviewStar5.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount=5;
                feedBackBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
            }
        });
        feedBackBinding.lowQualityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        feedBackBinding.unrelisticText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        feedBackBinding.premiumText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        feedBackBinding.otherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        feedBackBinding.bugsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}