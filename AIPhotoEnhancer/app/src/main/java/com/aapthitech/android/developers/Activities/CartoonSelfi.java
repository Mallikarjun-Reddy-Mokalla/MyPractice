package com.aapthitech.android.developers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.aapthitech.android.developers.databinding.ActivityCartoonSelfiBinding;
import com.aapthitech.android.developers.databinding.SaveSheetDialogBinding;

public class CartoonSelfi extends AppCompatActivity {
    ActivityCartoonSelfiBinding cartoonSelfiBinding;
    private Dialog saveDialog;
    private SaveSheetDialogBinding saveSheetDialogBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cartoonSelfiBinding = ActivityCartoonSelfiBinding.inflate(getLayoutInflater());
        View view = cartoonSelfiBinding.getRoot();
        setContentView(view);
        cartoonSelfiBinding.cartoonTabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
            }
        });
        cartoonSelfiBinding.adsCardCartoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
                cartoonSelfiBinding.proAdLay.setVisibility(View.GONE);
            }
        });
        cartoonSelfiBinding.magicCartoonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.GONE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.GONE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.proAdLay.setVisibility(View.GONE);
            }
        });
        cartoonSelfiBinding.cartoonOnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cartoonSelfiBinding.saveCartoonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSaveDialog();

            }
        });

    }

    private void openSaveDialog() {
        saveDialog = new Dialog(CartoonSelfi.this);
        saveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        saveSheetDialogBinding = SaveSheetDialogBinding.inflate(getLayoutInflater());
        saveDialog.setContentView(saveSheetDialogBinding.getRoot());
        saveDialog.setCancelable(false);
        saveDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        saveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        saveDialog.getWindow().setGravity(Gravity.BOTTOM);

        if (saveDialog != null) {
            saveDialog.show();
        }
        saveSheetDialogBinding.stillExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveDialog != null) {
                    if (saveDialog.isShowing()) {
                        saveDialog.dismiss();
                        onBackPressed();
                    } else {
                        onBackPressed();
                    }
                }
            }
        });
    }
}