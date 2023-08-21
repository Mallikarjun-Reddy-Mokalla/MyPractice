package com.aapthitech.android.developers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aapthitech.android.developers.databinding.ActivityRemoveObjectBinding;

public class RemoveObject extends AppCompatActivity {
    ActivityRemoveObjectBinding removeObjectBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        removeObjectBinding = ActivityRemoveObjectBinding.inflate(getLayoutInflater());
        View view = removeObjectBinding.getRoot();
        setContentView(view);
        removeObjectBinding.magicIconLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeObjectBinding.magicToolsLayout.setVisibility(View.VISIBLE);
                removeObjectBinding.toolLay.setVisibility(View.GONE);
                removeObjectBinding.seeKLay.setVisibility(View.GONE);
                removeObjectBinding.saveMagicLayout.setVisibility(View.GONE);
            }
        });
        removeObjectBinding.magicTabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeObjectBinding.magicToolsLayout.setVisibility(View.GONE);
                removeObjectBinding.toolLay.setVisibility(View.VISIBLE);
                removeObjectBinding.seeKLay.setVisibility(View.VISIBLE);
                removeObjectBinding.saveMagicLayout.setVisibility(View.VISIBLE);
            }
        });
        removeObjectBinding.checkedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeObjectBinding.saveMagicLayout.setVisibility(View.VISIBLE);
            }
        });
        removeObjectBinding.removeObjBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}