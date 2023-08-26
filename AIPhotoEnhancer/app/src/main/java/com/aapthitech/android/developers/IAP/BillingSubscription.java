package com.aapthitech.android.developers.IAP;


import static com.aapthitech.android.developers.Data.CommonMethods.commonMethods;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchaseHistoryParams;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;

import java.util.List;

public class BillingSubscription implements PurchasesUpdatedListener {
    private BillingClient billingClient;
    private Context context;

    private BillingResult billingResult;
    Activity activity;

    public static SharedPreferences sharedPrefs;
    public static SharedPreferences.Editor editors;
    private String planChoosen;
    private String fromActivity;

    public BillingSubscription(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
         sharedPrefs = context.getSharedPreferences("SUBSCRIBE", Context.MODE_PRIVATE);
        editors = sharedPrefs.edit();
    }

    public void getBillingConnection(Context mcontext, String produtID,String fromScreen) {
        context = mcontext;
        billingClient = BillingClient.newBuilder(mcontext).setListener(this).enablePendingPurchases().build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // BillingClient is ready

                    showSubscriptionProducts(produtID);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Handle disconnection
                Toast.makeText(mcontext, "Something Went wrong ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showSubscriptionProducts(String produtID) {

        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(ImmutableList.of(QueryProductDetailsParams.Product.newBuilder().setProductId(produtID).setProductType(BillingClient.ProductType.SUBS).build())).build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, new ProductDetailsResponseListener() {
            public void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> productDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                    for (ProductDetails productDetails : productDetailsList) {

                        initiatePurchaseForProduct(productDetails);
                    }
                }
            }
        });


    }

    // Method to initiate the purchase flow based on selected product
    public void initiatePurchaseForProduct(ProductDetails productDetails) {
        String offerToken = productDetails.getSubscriptionOfferDetails().get(0).getOfferToken();

        ImmutableList productDetailsParamsList = ImmutableList.of(BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build());

     /*   susName = productDetails.getName();
        description = productDetails.getDescription();
        formattedprice = productDetails.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
        billingPeriod = productDetails.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getBillingPeriod();
        recurrenceMode = productDetails.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getRecurrenceMode();
        bP = billingPeriod;
        n = billingPeriod.substring(1, 2);
        duration = billingPeriod.substring(2, 3);
        if (recurrenceMode == 2) {
            if (duration.equals("M")) {
                dur = "For" + n + "Month";
            } else if (duration.equals("Y")) {
                dur = "For" + n + "Year";
            } else if (duration.equals("W")) {
                dur = "For" + n + "Week";
            }
        } else {
            if (bP.equals("P1M")) {
                dur = "/Monthly";
            } else if (bP.equals("P1Y")) {
                dur = "/Yearly";
            } else if (bP.equals("P1W")) {
                dur = "/Weekly";
            }
        }
        String phase = formattedprice+""+dur;
        for (int i= 0;i<productDetails.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().size();i++){
            if (i>0){
                String period= productDetails.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(i).getBillingPeriod();
                String price = productDetails.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(i).getFormattedPrice();
            }
        }*/
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build();

// Launch the billing flow
        billingResult = billingClient.launchBillingFlow((Activity) context, billingFlowParams);
    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {

            // Handle an error caused by a user cancelling the purchase flow.
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            // Handle any other error codes.
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
            // Handle any other error codes.
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
            // Handle any other error codes.
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.NETWORK_ERROR) {
            // Handle any other error codes.
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
            // Handle any other error codes.
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
            // Handle any other error codes.
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_UNAVAILABLE) {
            // Handle any other error codes.
        }
    }

    void handlePurchase(Purchase purchase) {

        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase.isAutoRenewing()) {
                                editors.putBoolean("GO_PREMIUM", true);
                                editors.apply();
                                commonMethods.disableAds();

//                                context.startActivity(new Intent(context, HomeActivity.class));
                            } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAutoRenewing()) {
                                editors.putBoolean("GO_PREMIUM", true);
                                editors.apply();
                                commonMethods.disableAds();

//                                context.startActivity(new Intent(context, HomeActivity.class));

                            } else {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    editors.putBoolean("GO_PREMIUM", true);
                                    editors.apply();
                                    commonMethods.disableAds();

//                                    context.startActivity(new Intent(context, HomeActivity.class));

                                }
                            }


                        } else {
                            editors.putBoolean("GO_PREMIUM", false);
                            editors.apply();
//                            context.startActivity(new Intent(context, HomeActivity.class));

                        }
                    }
                });


            }
        }
    }

    public void checkPurchaseHistory(Context context1) {
        billingClient = BillingClient.newBuilder(context1).setListener(this).enablePendingPurchases().build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                billingClient.queryPurchaseHistoryAsync(QueryPurchaseHistoryParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), new PurchaseHistoryResponseListener() {
                    public void onPurchaseHistoryResponse(BillingResult billingResult, List purchasesHistoryList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (purchasesHistoryList.size() > 0 && purchasesHistoryList != null) {
                                editors.putBoolean("GO_PREMIUM", true);
                                editors.apply();
                                System.out.println("Restore----" + "Sucesss");
                            }
                        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                            editors.putBoolean("GO_PREMIUM", true);
                            editors.apply();
                            System.out.println("Restore----" + "Sucesss");
                        } else {
                            editors.putBoolean("GO_PREMIUM", false);
                            editors.apply();
                            System.out.println("Restore----" + "fail");

                        }
                    }
                });
            }
        });

    }


}
