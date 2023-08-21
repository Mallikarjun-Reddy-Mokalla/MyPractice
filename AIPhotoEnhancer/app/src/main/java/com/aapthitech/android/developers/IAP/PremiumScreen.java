package com.aapthitech.android.developers.IAP;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.ActivityPremiumScreenBinding;
import com.bumptech.glide.Glide;

public class PremiumScreen extends AppCompatActivity {
    ActivityPremiumScreenBinding premiumScreenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        premiumScreenBinding = ActivityPremiumScreenBinding.inflate(getLayoutInflater());
        View view = premiumScreenBinding.getRoot();
        setContentView(view);

        Glide.with(this).load(R.drawable.premium_screen_slide).into(premiumScreenBinding.videoGif);// to load the gif file
        premiumScreenBinding.onBackPrem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
}