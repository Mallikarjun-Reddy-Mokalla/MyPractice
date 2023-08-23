package com.aapthitech.android.developers.Data;

import static com.aapthitech.android.developers.SplashActivity.splashInstance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aapthitech.android.developers.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAdView;

public class CommonMethods {

    String[] adUnitIds;

    int currentAdIndex = 0;

    private String[] bannerAdUnits;
    private int banneradindex = 0;
    public static final CommonMethods commonMethods = new CommonMethods();

    public static CommonMethods getInstance() {
        return commonMethods;
    }

    /*native Ad loaded using the eCPM flooring Ids */
    public void loadNextNativeAdFlor(Context context, FrameLayout frameLayout) {
        if (context != null && frameLayout != null) {

            adUnitIds = new String[]{context.getString(R.string.native_high), context.getString(R.string.native_mid), context.getString(R.string.native_all)};
            String adUnitId = adUnitIds[currentAdIndex];
            AdLoader.Builder builder = new AdLoader.Builder(context, adUnitId);
            builder.forNativeAd(new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
                // OnLoadedListener implementation.
                @Override
                public void onNativeAdLoaded(com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                    // If this callback occurs after the activity is destroyed, you must call
                    // destroy and return or you may get a memory leak.

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    NativeAdView adView = (NativeAdView) inflater.inflate(R.layout.ad_unified, null);
                    populateUnifiedNativeAdView(nativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                }
            });

            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

            com.google.android.gms.ads.nativead.NativeAdOptions adOptions = new com.google.android.gms.ads.nativead.NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    if (currentAdIndex < 2) {
                        currentAdIndex++;
                        loadNextNativeAdFlor(context, frameLayout);
                    }
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());

        }
    }

    public static void populateUnifiedNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.GONE);
        } else {
            adView.getBodyView().setVisibility(View.GONE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.GONE);
        } else {
            adView.getPriceView().setVisibility(View.GONE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.GONE);
        } else {
            adView.getStoreView().setVisibility(View.GONE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd();
                }
            });
        } else {
        }
    }/*Natvie Ad loaded ended */

    /*banner Ads loading with eCPM ids */
    public void loadBannerAd(AdView adViewBanner, FrameLayout adViewContainer, Context context) {

        if (context != null) {
            bannerAdUnits = new String[]{context.getString(R.string.banner_high), context.getString(R.string.banner_mid), context.getString(R.string.banner_all)};
            String adunitId = bannerAdUnits[banneradindex];
            adViewBanner = new AdView(context);
            adViewBanner.setAdUnitId(adunitId);
            adViewContainer.removeAllViews();
            adViewContainer.addView(adViewBanner);

            AdSize adSize = getAdSize(context, adViewContainer);
            adViewBanner.setAdSize(adSize);

            AdRequest adRequest = new AdRequest.Builder().build();
            adViewBanner.loadAd(adRequest);
            AdView finalAdViewBanner = adViewBanner;
            adViewBanner.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);

                    if (banneradindex < 2) {
                        banneradindex++;
                        loadBannerAd(finalAdViewBanner, adViewContainer, context);
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
    }

    public static AdSize getAdSize(Context context, View adContainerView) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    public void showGoogleAd(Activity activity, Context context) {
        if (splashInstance != null) {
            if (splashInstance.interstitialAdHighFlor != null) {
                splashInstance.interstitialAdHighFlor.show(activity);
            } else if (splashInstance.interstitialAdMidFlor != null) {
                splashInstance.interstitialAdMidFlor.show(activity);
            } else if (splashInstance.interstitialAdAllFlor != null) {
                splashInstance.interstitialAdAllFlor.show(activity);
            } else {
                splashInstance.loadGoogleAdHighFlor();
                splashInstance.loadGoogleAdMidFlor();
                splashInstance.loadGoogleAdAllFlor();
            }
        }
    }
}
