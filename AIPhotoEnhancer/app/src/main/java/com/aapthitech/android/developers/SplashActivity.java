package com.aapthitech.android.developers;

import static com.aapthitech.android.developers.Data.CommonMethods.commonMethods;
import static com.aapthitech.android.developers.Data.CommonMethods.getAdSize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aapthitech.android.developers.Activities.MainActivity;
import com.aapthitech.android.developers.Data.RemoteConfig;
import com.aapthitech.android.developers.IAP.BillingSubscription;
import com.aapthitech.android.developers.databinding.ActivitySplashBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding splashBinding;
    BillingSubscription billingSubscription;
    public InterstitialAd interstitialAdHighFlor, interstitialAdMidFlor, interstitialAdAllFlor;
    public static boolean intHighFlor = false;
    public static boolean intMidFlor = false;
    public static boolean intAllFlor = false;

    public static SplashActivity splashInstance;
    public static FirebaseAnalytics mFirebaseAnalytics;
    public FirebaseRemoteConfig remoteConfig;

    private String[] bannerAdUnits;
    private int banneradindex = 0;
    AdView adViewBanner;
    public static SharedPreferences sharedPrefer;
    boolean subscribed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        splashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = splashBinding.getRoot();
        setContentView(view);
        splashInstance = this;
        FirebaseApp.initializeApp(this);
        bannerAdUnits = new String[]{getString(R.string.banner_high), getString(R.string.banner_mid), getString(R.string.banner_all)};
        billingSubscription = new BillingSubscription(SplashActivity.this, (Activity) SplashActivity.this);
        if (checkInternetConnection()) {

            setDefaultFirebaseValues();

            loadGoogleAdHighFlor();


            splashBinding.bannerSplash.post(new Runnable() {
                @Override
                public void run() {
                    if (!checkIAP()) {
                        loadBannerAd();
                    }
                }
            });

//            checkData();
        } else {
            Toast.makeText(SplashActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


    }

    private void setDefaultFirebaseValues() {
        try {
            JSONObject defaultJson = new JSONObject("{\n" + "    \"upgradeAppVersion\": \"1\",\n" + "    \"showbannerAd\": \"false\",\n" + "    \"showNativeAd\": \"false\",\n" + "    \"showNativeAdExit\": \"false\",\n" + "    \"showNativeAdMain\": \"false\",\n" + "    \"showNativeAdSave\": \"false\",\n" + "    \"showInterstitialOnLaunch\": \"false\",\n" + "    \"showInterstitialOnSave\": \"false\",\n" + "    \"showInterstitialapplyFilter\": \"false\",\n" + "    \"showInterstitial\": \"false\",\n" + "    \"showAppopenAd\": \"false\",\n" + "    \"enableIAPflag\": \"false\",\n" + "    \"baseUrl\": \"\",\n" + "    \"removeObjectApiService\": \"\",\n" + "    \"bgeraserAPI\": \"\",\n" + "    \"cartoonAPI\": \"\"\n" + "}");

            assignValuesRemote(defaultJson, "local");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadRemoteConfigValues();
    }

    private void setDefaultFirebaseValue() {
        RemoteConfig.getRemoteConfig().setUpgradeAppVersion("1");
        RemoteConfig.getRemoteConfig().setShowbannerAd("false");

        RemoteConfig.getRemoteConfig().setShowNativeAd("false");
        RemoteConfig.getRemoteConfig().setShowNativeAdExit("false");
        RemoteConfig.getRemoteConfig().setShowNativeAdMain("false");
        RemoteConfig.getRemoteConfig().setShowNativeAdSave("false");

        RemoteConfig.getRemoteConfig().setshowInterstitialOnLaunch("false");
        RemoteConfig.getRemoteConfig().setShowInterstitialOnSave("false");
        RemoteConfig.getRemoteConfig().setShowInterstitialapplyFilter("false");
        RemoteConfig.getRemoteConfig().setShowInterstitial("false");

        RemoteConfig.getRemoteConfig().setShowAppopenAd("false");

        RemoteConfig.getRemoteConfig().setEnableIAPflag("false");

        RemoteConfig.getRemoteConfig().setBaseUrl("");
        RemoteConfig.getRemoteConfig().setRemoveObjectApiService("");
        RemoteConfig.getRemoteConfig().setBgeraserAPI("");
        RemoteConfig.getRemoteConfig().setCartoonAPI("");
        loadRemoteConfigValues();
    }

    private void assignValuesRemote(JSONObject remoteConfig, String remote) {
        try {

            RemoteConfig.getRemoteConfig().setUpgradeAppVersion(remoteConfig.getString("upgradeAppVersion"));
            RemoteConfig.getRemoteConfig().setShowbannerAd(remoteConfig.getString("showbannerAd"));

            RemoteConfig.getRemoteConfig().setShowNativeAd(remoteConfig.getString("showNativeAd"));
            RemoteConfig.getRemoteConfig().setShowNativeAdExit(remoteConfig.getString("showNativeAdExit"));
            RemoteConfig.getRemoteConfig().setShowNativeAdMain(remoteConfig.getString("showNativeAdMain"));
            RemoteConfig.getRemoteConfig().setShowNativeAdSave(remoteConfig.getString("showNativeAdSave"));

            RemoteConfig.getRemoteConfig().setshowInterstitialOnLaunch(remoteConfig.getString("showInterstitialOnLaunch"));
            RemoteConfig.getRemoteConfig().setShowInterstitialOnSave(remoteConfig.getString("showInterstitialOnSave"));
            RemoteConfig.getRemoteConfig().setShowInterstitialapplyFilter(remoteConfig.getString("showInterstitialapplyFilter"));
            RemoteConfig.getRemoteConfig().setShowInterstitial(remoteConfig.getString("showInterstitial"));

            RemoteConfig.getRemoteConfig().setShowAppopenAd(remoteConfig.getString("showAppopenAd"));

            RemoteConfig.getRemoteConfig().setEnableIAPflag(remoteConfig.getString("enableIAPflag"));

            RemoteConfig.getRemoteConfig().setBaseUrl(remoteConfig.getString("baseUrl"));
            RemoteConfig.getRemoteConfig().setRemoveObjectApiService(remoteConfig.getString("removeObjectApiService"));
            RemoteConfig.getRemoteConfig().setBgeraserAPI(remoteConfig.getString("bgeraserAPI"));
            RemoteConfig.getRemoteConfig().setCartoonAPI(remoteConfig.getString("cartoonAPI"));
            if (remote.equals("remote")) {

                checkData();
                checkSubcription();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadRemoteConfigValues() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder().build();
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings);
        remoteConfig.setDefaultsAsync(R.xml.remote_values);
//        long cacheExpiration = 43200;/*this for realease*/
        long cacheExpiration = 0;// for developing testing

        remoteConfig.fetch(cacheExpiration).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    remoteConfig.activate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            String two = remoteConfig.getString("firebase_remote_config");
                            // Log.d("messsssssss",two);
                            try {
                                JSONObject remoteObj = new JSONObject(two);
                                assignValuesRemote(remoteObj, "remote");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });

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
                interstitialAdHighFlor = null;
                loadGoogleAdMidFlor();

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
                loadGoogleAdAllFlor();
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

                    checkUpdateandStart();
                }
            }, 3000);

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

    private void checkUpdateandStart() {
        int loaclVersion = 1;
        int a;
        if (RemoteConfig.getRemoteConfig().getUpgradeAppVersion() != null) {
            a = Integer.parseInt(RemoteConfig.getRemoteConfig().getUpgradeAppVersion());
            if (loaclVersion >= a) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, UpdateApp.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }
    }

    public void loadBannerAd() {

        String adunitId = bannerAdUnits[banneradindex];
        adViewBanner = new AdView(SplashActivity.this);
        adViewBanner.setAdUnitId(adunitId);
        splashBinding.bannerSplash.removeAllViews();
        splashBinding.bannerSplash.addView(adViewBanner);

        AdSize adSize = getAdSize(SplashActivity.this, splashBinding.bannerSplash);
        adViewBanner.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBanner.loadAd(adRequest);
        AdView finalAdViewBanner = adViewBanner;
        adViewBanner.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                if (banneradindex < 1) {
                    banneradindex++;
                    loadBannerAd();
                }
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
    }

    public void checkSubcription() {
        if (billingSubscription != null) {
            billingSubscription.checkPurchaseHistory(SplashActivity.this);
        }
        sharedPrefer = getSharedPreferences("SUBSCRIBE", this.MODE_PRIVATE);
        subscribed = sharedPrefer.getBoolean("GO_PREMIUM", false);
        if (subscribed == true) {
            commonMethods.disableAds();
        }
    }

    public boolean checkIAP() {
        boolean isPremium = false;
        sharedPrefer = getSharedPreferences("SUBSCRIBE", this.MODE_PRIVATE);
        isPremium = sharedPrefer.getBoolean("GO_PREMIUM", false);

        return isPremium;
    }
}