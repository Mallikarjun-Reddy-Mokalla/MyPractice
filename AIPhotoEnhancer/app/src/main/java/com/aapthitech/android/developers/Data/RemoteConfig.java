package com.aapthitech.android.developers.Data;

public class RemoteConfig {
    public static final RemoteConfig remoteConfig = new RemoteConfig();
    private String showbannerAd;
    private String showNativeAd;
    private String showNativeAdExit;
    private String showNativeAdMain;
    private String showNativeAdSave;
    private String showInterstitialOnLaunch;
    private String showInterstitialOnSave;
    private String showInterstitialapplyFilter;
    private String showInterstitial;
    private String showAppopenAd;
    private String enableIAPflag;
    private String baseUrl;
    private String removeObjectApiService;
    private String bgeraserAPI;
    private String cartoonAPI;
    private String upgradeAppVersion;

    public RemoteConfig() {
    }

    public String getUpgradeAppVersion() {
        return upgradeAppVersion;
    }

    public void setUpgradeAppVersion(String upgradeAppVersion) {
        this.upgradeAppVersion = upgradeAppVersion;
    }

    public static RemoteConfig getRemoteConfig() {
        return remoteConfig;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getRemoveObjectApiService() {
        return removeObjectApiService;
    }

    public void setRemoveObjectApiService(String removeObjectApiService) {
        this.removeObjectApiService = removeObjectApiService;
    }

    public String getBgeraserAPI() {
        return bgeraserAPI;
    }

    public void setBgeraserAPI(String bgeraserAPI) {
        this.bgeraserAPI = bgeraserAPI;
    }

    public String getCartoonAPI() {
        return cartoonAPI;
    }

    public void setCartoonAPI(String cartoonAPI) {
        this.cartoonAPI = cartoonAPI;
    }

    public String getShowbannerAd() {
        return showbannerAd;
    }

    public void setShowbannerAd(String showbannerAd) {
        this.showbannerAd = showbannerAd;
    }

    public String getShowNativeAd() {
        return showNativeAd;
    }

    public void setShowNativeAd(String showNativeAd) {
        this.showNativeAd = showNativeAd;
    }

    public String getShowNativeAdExit() {
        return showNativeAdExit;
    }

    public void setShowNativeAdExit(String showNativeAdExit) {
        this.showNativeAdExit = showNativeAdExit;
    }

    public String getShowNativeAdMain() {
        return showNativeAdMain;
    }

    public void setShowNativeAdMain(String showNativeAdMain) {
        this.showNativeAdMain = showNativeAdMain;
    }

    public String getShowNativeAdSave() {
        return showNativeAdSave;
    }

    public void setShowNativeAdSave(String showNativeAdSave) {
        this.showNativeAdSave = showNativeAdSave;
    }

    public String getshowInterstitialOnLaunch() {
        return showInterstitialOnLaunch;
    }

    public void setshowInterstitialOnLaunch(String showInterstitialOnLaunch) {
        this.showInterstitialOnLaunch = showInterstitialOnLaunch;
    }

    public String getShowInterstitialOnSave() {
        return showInterstitialOnSave;
    }

    public void setShowInterstitialOnSave(String showInterstitialOnSave) {
        this.showInterstitialOnSave = showInterstitialOnSave;
    }

    public String getShowInterstitialapplyFilter() {
        return showInterstitialapplyFilter;
    }

    public void setShowInterstitialapplyFilter(String showInterstitialapplyFilter) {
        this.showInterstitialapplyFilter = showInterstitialapplyFilter;
    }

    public String getShowInterstitial() {
        return showInterstitial;
    }

    public void setShowInterstitial(String showInterstitial) {
        this.showInterstitial = showInterstitial;
    }

    public String getShowAppopenAd() {
        return showAppopenAd;
    }

    public void setShowAppopenAd(String showAppopenAd) {
        this.showAppopenAd = showAppopenAd;
    }

    public String getEnableIAPflag() {
        return enableIAPflag;
    }

    public void setEnableIAPflag(String enableIAPflag) {
        this.enableIAPflag = enableIAPflag;
    }
}
