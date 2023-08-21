package com.aapthitech.android.developers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.aapthitech.android.developers.databinding.ActivityFeedBackBinding;

public class FeedBack extends AppCompatActivity {
    ActivityFeedBackBinding feedBackBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedBackBinding = ActivityFeedBackBinding.inflate(getLayoutInflater());
        View view = feedBackBinding.getRoot();
        setContentView(view);

    }
}