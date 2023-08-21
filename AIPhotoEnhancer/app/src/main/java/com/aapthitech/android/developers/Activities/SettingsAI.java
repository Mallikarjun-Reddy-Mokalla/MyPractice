package com.aapthitech.android.developers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aapthitech.android.developers.IAP.PremiumScreen;
import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.ActivitySettingsAiBinding;

public class SettingsAI extends AppCompatActivity {
    ActivitySettingsAiBinding settingsAiBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        settingsAiBinding = ActivitySettingsAiBinding.inflate(getLayoutInflater());
        View view = settingsAiBinding.getRoot();
        setContentView(view);
        settingsAiBinding.onBackSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        settingsAiBinding.goPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsAI.this, PremiumScreen.class));
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
}