package com.aapthitech.android.developers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aapthitech.android.developers.Activities.MainActivity;
import com.aapthitech.android.developers.databinding.ActivitySplashBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding splashBinding;

    public InterstitialAd interstitialAdHighFlor, interstitialAdMidFlor, interstitialAdAllFlor;
    public static boolean intHighFlor = false;
    public static boolean intMidFlor = false;
    public static boolean intAllFlor = false;

    public static SplashActivity splashInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        splashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = splashBinding.getRoot();
        setContentView(view);
        splashInstance = this;
        if (checkInternetConnection()) {

//            assignDefaultValues();


            loadGoogleAdHighFlor();
            loadGoogleAdMidFlor();
            loadGoogleAdAllFlor();
/*
            adContainerView.post(new Runnable() {
                @Override
                public void run() {
                    if (adspurchased) {
                        loadBanner();
                    }

                }
            });
*/
            checkData();
        } else {
            Toast.makeText(SplashActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


    }


    public static SplashActivity getInstance() {
        return splashInstance;
    }

    private boolean checkInternetConnection() {
        boolean isconnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (connectivityManager.getActiveNetworkInfo() != null) {
                isconnected = connectivityManager.getActiveNetworkInfo().isConnected();
            }
            return isconnected;
        } else {
            return isconnected;
        }
    }

    public void loadGoogleAdHighFlor() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.interstitial_high), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                intHighFlor = true;
                interstitialAdHighFlor = interstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadGoogleAdHighFlor();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        interstitialAdHighFlor = null;
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d("TAG", "The ad was shown.");
                    }
                });


            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                intHighFlor = true;
                interstitialAdHighFlor = null;

            }

        });
    }

    public void loadGoogleAdMidFlor() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.interstitial_mid), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                intMidFlor = true;
                interstitialAdMidFlor = interstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadGoogleAdMidFlor();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        interstitialAdMidFlor = null;
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d("TAG", "The ad was shown.");
                    }
                });


            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                intMidFlor = true;
                interstitialAdMidFlor = null;
            }

        });
    }

    public void loadGoogleAdAllFlor() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.interstitial_all), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                intAllFlor = true;
                interstitialAdAllFlor = interstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadGoogleAdAllFlor();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        interstitialAdAllFlor = null;
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d("TAG", "The ad was shown.");
                    }
                });


            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                intAllFlor = true;
                interstitialAdAllFlor = null;
            }

        });
    }

    private void checkData() {

        if (intHighFlor || intAllFlor || intMidFlor) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, 4000);

        } else {
            holdTime();
        }
    }

    private void holdTime() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkData();
            }
        }, 2000);


    }
}