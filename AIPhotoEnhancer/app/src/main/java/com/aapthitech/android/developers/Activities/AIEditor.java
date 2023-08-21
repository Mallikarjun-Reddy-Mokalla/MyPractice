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

import com.aapthitech.android.developers.databinding.ActivityAieditorBinding;
import com.aapthitech.android.developers.databinding.SaveSheetDialogBinding;

public class AIEditor extends AppCompatActivity {
    ActivityAieditorBinding aieditorBinding;
    String titleText;
    String proTag;
    String effectTitle;
    private Dialog saveDialog;
    private SaveSheetDialogBinding saveSheetDialogBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        aieditorBinding = ActivityAieditorBinding.inflate(getLayoutInflater());
        View view = aieditorBinding.getRoot();
        setContentView(view);
        titleText = getIntent().getStringExtra("TITLE");
        effectTitle = getIntent().getStringExtra("TITLE");
        proTag = getIntent().getStringExtra("PRO_TAG");
        if (titleText != null) {
            aieditorBinding.titleType.setText(titleText);
            aieditorBinding.effectType.setText(titleText);
        }
        if (proTag != null) {
            aieditorBinding.proTagText.setText(proTag);

        }
        aieditorBinding.adsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aieditorBinding.appBarLay.setVisibility(View.GONE);
                aieditorBinding.nextLay.nextLayLoad.setVisibility(View.VISIBLE);
            }
        });
        aieditorBinding.nextLay.magicEnhanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aieditorBinding.nextLay.saveMagicLayout.setVisibility(View.GONE);
                aieditorBinding.nextLay.magicToolsLayout.setVisibility(View.VISIBLE);
            }
        });
        aieditorBinding.nextLay.tabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aieditorBinding.nextLay.saveMagicLayout.setVisibility(View.VISIBLE);
                aieditorBinding.nextLay.magicToolsLayout.setVisibility(View.GONE);
            }
        });
        aieditorBinding.onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        aieditorBinding.nextLay.onBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        aieditorBinding.nextLay.saveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSaveDialog();
            }
        });
    }

    private void openSaveDialog() {
        saveDialog = new Dialog(AIEditor.this);
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