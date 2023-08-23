package com.aapthitech.android.developers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.aapthitech.android.developers.databinding.ActivitySaveScreenBinding;

public class SaveScreen extends AppCompatActivity {
    ActivitySaveScreenBinding saveScreenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveScreenBinding = ActivitySaveScreenBinding.inflate(getLayoutInflater());
        View view = saveScreenBinding.getRoot();
        setContentView(view);


    }
}