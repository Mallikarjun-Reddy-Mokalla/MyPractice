package com.aapthitech.android.developers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.ActivityFeedBackBinding;

public class FeedBack extends AppCompatActivity {
    ActivityFeedBackBinding feedBackBinding;
    int reviewCount;
    String feedbackToMail;
    String lowQuality;
    String unRealistic = "Reealstic";
    String bugs = "No bugs";
    String premium = "Premium is offeredable";
    String otherissues;
    int lowclickCount = 1;
    int unRealclickCount = 1;
    int bugsclickCount = 1;
    int proclickCount = 1;
    int otherclickCount = 1;
    String reviewContent;

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
                reviewCount = 1;
                reviewContent = "Just giving 1 Star";
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
                reviewCount = 2;
                reviewContent = "Rating 2 Star,need to improve.";
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
                reviewCount = 3;
                reviewContent = "Rating 3 Star,good application.";
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
                reviewCount = 4;
                reviewContent = "Rating 4 Star,App is useful.";

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
                reviewCount = 5;
                reviewContent = "Rating 5 Star,App is very useful and I like it.";

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

                if (lowclickCount == 1) {
                    lowQuality = "The App is Low Quality";
                    feedBackBinding.lowQualityText.setBackground(getDrawable(R.drawable.magic_gradient));
                    lowclickCount++;
                } else {
                    lowclickCount--;
                    lowQuality = "";
                    feedBackBinding.lowQualityText.setBackground(getDrawable(R.drawable.feed_back_gradient));

                }
            }
        });
        feedBackBinding.unrelisticText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unRealclickCount == 1) {
                    unRealistic = "App do not seem realistic";
                    feedBackBinding.unrelisticText.setBackground(getDrawable(R.drawable.magic_gradient));
                    unRealclickCount++;
                } else {
                    unRealclickCount--;
                    unRealistic = "Reealstic";
                    feedBackBinding.unrelisticText.setBackground(getDrawable(R.drawable.feed_back_gradient));

                }
            }
        });
        feedBackBinding.premiumText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (proclickCount == 1) {
                    premium = "Premium is too Cost";
                    feedBackBinding.premiumText.setBackground(getDrawable(R.drawable.magic_gradient));
                    proclickCount++;
                } else {
                    proclickCount--;
                    premium = "Premium is offeredable";
                    feedBackBinding.premiumText.setBackground(getDrawable(R.drawable.feed_back_gradient));

                }
            }
        });
        feedBackBinding.otherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otherclickCount == 1) {
                    otherissues = "Have issues";
                    feedBackBinding.otherText.setBackground(getDrawable(R.drawable.magic_gradient));
                    otherclickCount++;
                } else {
                    otherclickCount--;
                    otherissues = "No issues";
                    feedBackBinding.otherText.setBackground(getDrawable(R.drawable.feed_back_gradient));

                }
            }
        });
        feedBackBinding.bugsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bugsclickCount == 1) {
                    bugs = "App as Bugs";
                    feedBackBinding.bugsText.setBackground(getDrawable(R.drawable.magic_gradient));
                    bugsclickCount++;
                } else {
                    bugs = "No bugs";
                    bugsclickCount--;
                    feedBackBinding.bugsText.setBackground(getDrawable(R.drawable.feed_back_gradient));

                }
            }
        });
        feedBackBinding.submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!feedBackBinding.feedBackEditText.getText().toString().isEmpty()) {
                    feedbackToMail = feedBackBinding.feedBackEditText.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mallikarjunreddy900@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "AI Enhancer Feedback");
                    intent.putExtra(Intent.EXTRA_TEXT, "Dear AI Enhancer Team,\n\nI would like to provide the following feedback:\n\n" + reviewContent + "\n" + "\n\n" + lowQuality + "\n" + unRealistic + "\n" + bugs + "\n" + premium + "\n" + otherissues + "\n\n" + feedbackToMail);
                    startActivity(Intent.createChooser(intent, "Send Feedback"));
                } else {
                    Toast.makeText(FeedBack.this, "Enter Feedback", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}