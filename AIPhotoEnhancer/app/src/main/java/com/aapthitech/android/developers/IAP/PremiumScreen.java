package com.aapthitech.android.developers.IAP;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.ActivityPremiumScreenBinding;
import com.bumptech.glide.Glide;

public class PremiumScreen extends AppCompatActivity {
    ActivityPremiumScreenBinding premiumScreenBinding;
    private final String PRODUCT_ID_WEEKLY = "weekly_subscription";
    private final String PRODUCT_ID_MONTHLY = "monthly_subscription";
    private final String PRODUCT_ID_YEARLY = "yearly_subscription";
    private String produtID;
    BillingSubscription billingSubscription;
    String fromAct;
    String planChoosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        premiumScreenBinding = ActivityPremiumScreenBinding.inflate(getLayoutInflater());
        View view = premiumScreenBinding.getRoot();
        setContentView(view);
        fromAct = getIntent().getStringExtra("PRO_FROM");
        billingSubscription = new BillingSubscription(PremiumScreen.this, PremiumScreen.this);

        Glide.with(this).load(R.drawable.premium_screen_slide).into(premiumScreenBinding.videoGif);// to load the gif file
        premiumScreenBinding.onBackPrem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        premiumScreenBinding.premiumYearlySelLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planChoosen = "MONTHLY";
                String sub_Product_Id = PRODUCT_ID_MONTHLY;
                initiateBilling(planChoosen, sub_Product_Id);
            }
        });
        premiumScreenBinding.premiumMontlySelLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planChoosen = "YEARLY";
                String sub_Product_Id = PRODUCT_ID_YEARLY;
                initiateBilling(planChoosen, sub_Product_Id);

            }
        });

    }

    private void initiateBilling(String planChoosen, String sub_Product_Id) {
        if (billingSubscription != null) {
            if (fromAct != null) {
                billingSubscription.getBillingConnection(PremiumScreen.this, sub_Product_Id, fromAct);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}