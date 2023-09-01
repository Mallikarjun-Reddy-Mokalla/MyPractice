package com.hangoverstudios.ai.enhancer.toonify.me.service;

import static com.hangoverstudios.ai.enhancer.toonify.me.commonclass.CommonMethods.commonMethods;
import static com.hangoverstudios.ai.enhancer.toonify.me.appopen.MyAppOpen.universalAppOpen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchaseHistoryParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.hangoverstudios.ai.enhancer.toonify.me.activity.HomeActivity;

import java.util.List;

public class SubcriptionServices implements PurchasesUpdatedListener {

    private BillingClient billingClient;
    private Context context;

    private BillingResult billingResult;
    Activity activity;

    public static SharedPreferences sharedPrefs;
    public static SharedPreferences.Editor editors;
    private String planChoosen;

    public SubcriptionServices(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        sharedPrefs = context.getSharedPreferences("SUBSCRIBE", Context.MODE_PRIVATE);
        editors = sharedPrefs.edit();
    }

    public void getBillingConnection(Context mcontext, String produtID) {
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

        ImmutableList productDetailsParamsList = ImmutableList.of(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(productDetails).setOfferToken(offerToken).build());

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build();

// Launch the billing flow
        billingResult = billingClient.launchBillingFlow((Activity) context, billingFlowParams);
    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                    handlePurchase(purchase);
                }
            }
        }
    }
    void verifySubPurchase(Purchase purchases) {
        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                String productId = purchases.getProducts().get(0); /// this one gets the product Id
                String purchaseToken = purchases.getPurchaseToken(); /// this one gets the purchase token

            }
        });
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
                                editors.putBoolean("ITEM_OWNED", true);
                                editors.apply();
                                commonMethods.disableAds();
                                universalAppOpen = true;
                                billingClient.endConnection();
                                context.startActivity(new Intent(context, HomeActivity.class));
                            } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAutoRenewing()) {
                                editors.putBoolean("ITEM_OWNED", true);
                                editors.apply();
                                commonMethods.disableAds();
                                universalAppOpen = true;
                                billingClient.endConnection();
                                context.startActivity(new Intent(context, HomeActivity.class));

                            } else {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    editors.putBoolean("ITEM_OWNED", true);
                                    editors.apply();
                                    commonMethods.disableAds();
                                    universalAppOpen = true;
                                    billingClient.endConnection();
                                    context.startActivity(new Intent(context, HomeActivity.class));

                                }
                            }
                        } else {
                            editors.putBoolean("ITEM_OWNED", false);
                            editors.apply();
                            billingClient.endConnection();
                            context.startActivity(new Intent(context, HomeActivity.class));

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
                billingClient.endConnection();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                billingClient.queryPurchaseHistoryAsync(QueryPurchaseHistoryParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), new PurchaseHistoryResponseListener() {
                    public void onPurchaseHistoryResponse(@NonNull BillingResult billingResult, List purchasesHistoryList) {


                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (purchasesHistoryList != null && purchasesHistoryList.size() > 0) {

                                editors.putBoolean("ITEM_OWNED", true);
                                editors.apply();
                                System.out.println("Restore----" + "Sucesss");
                                System.out.println("IAP:" + "Restore" + purchasesHistoryList);
                                System.out.println("IAP:" + "Restore" + purchasesHistoryList.size());
                            } else {
                                editors.putBoolean("ITEM_OWNED", false);
                                editors.apply();
                                System.out.println("Restore----" + "fail");
                                System.out.println("IAP:" + "Restore fail" + purchasesHistoryList);
                                System.out.println("IAP:" + "Restore fail" + purchasesHistoryList.size());
                            }
                        } else {
                            editors.putBoolean("ITEM_OWNED", false);
                            editors.apply();
                            System.out.println("Restore----" + "fail");
                            Toast.makeText(context1, "Restore NO Records Found ", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });

    }

    void handleThePayment(Purchase purchase) {

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
//                billingClient.acknowledgePurchase(acknowledgePurchaseParams);
            }
        }
    }

    public void restorePurchases(Context contextR) {

        billingClient = BillingClient.newBuilder(contextR).enablePendingPurchases().setListener((billingResult, list) -> {
        }).build();

        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                billingClient.endConnection();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult1, list) -> {
                        if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (list.size() > 0) {
                                editors.putBoolean("ITEM_OWNED", true);
                                editors.apply();
                                System.out.println("Restore----" + "Sucesss");
                                System.out.println("IAP:" + "Restore" + list);
                                System.out.println("IAP:" + "Restore" + list.size());
                            } else {
                                editors.putBoolean("ITEM_OWNED", false);
                                editors.apply();
                                System.out.println("Restore----" + "Sucesss NO Record Found");
                                System.out.println("IAP:" + "Restore" + list);
                                System.out.println("IAP:" + "Restore" + list.size());
                            }
                        }
                    });
                }
            }
        });
    }

    public void onResume() {
/*
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                handlePurchase(purchase);
                            }
                        }
                    }
                }
        );
*/
        PurchasesResponseListener purchasesResponseListener = new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchaseList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (Purchase purchase : purchaseList) {
                        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                            handlePurchase(purchase);
                        }
                    }
                }
            }
        };
    }
}
