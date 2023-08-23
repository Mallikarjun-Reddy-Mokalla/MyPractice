package com.aapthitech.android.developers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aapthitech.android.developers.databinding.ActivityFeedBackBinding;

public class FeedBack extends AppCompatActivity {
    ActivityFeedBackBinding feedBackBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        feedBackBinding = ActivityFeedBackBinding.inflate(getLayoutInflater());
        View view = feedBackBinding.getRoot();
        setContentView(view);

    }
}