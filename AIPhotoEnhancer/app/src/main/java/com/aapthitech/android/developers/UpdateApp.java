package com.aapthitech.android.developers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.aapthitech.android.developers.databinding.ActivityUpdateAppBinding;

public class UpdateApp extends AppCompatActivity {
    ActivityUpdateAppBinding updateAppBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateAppBinding = ActivityUpdateAppBinding.inflate(getLayoutInflater());
        View view = updateAppBinding.getRoot();
        setContentView(view);

        updateAppBinding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://play.google.com/store/apps/details?id=" + UpdateApp.this.getPackageName();
//                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


    }
}