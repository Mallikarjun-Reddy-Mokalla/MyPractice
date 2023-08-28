package com.aapthitech.android.developers;

import static com.aapthitech.android.developers.Activities.MainActivity.mainActivity;
import static com.aapthitech.android.developers.Data.CommonMethods.commonMethods;

import static java.lang.System.out;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aapthitech.android.developers.Activities.AIEditor;
import com.aapthitech.android.developers.Activities.CartoonSelfi;
import com.aapthitech.android.developers.Activities.FeedBack;
import com.aapthitech.android.developers.Activities.MainActivity;
import com.aapthitech.android.developers.Activities.RemoveObject;
import com.aapthitech.android.developers.Data.RemoteConfig;
import com.aapthitech.android.developers.IAP.PremiumScreen;
import com.aapthitech.android.developers.TouchEvents.MultiTouchListener2;
import com.aapthitech.android.developers.databinding.ActivityEditscreenBinding;
import com.aapthitech.android.developers.databinding.LoadingBinding;
import com.aapthitech.android.developers.databinding.SaveSheetDialogBinding;
import com.aapthitech.android.developers.databinding.ServerNotFoundBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Editscreen extends AppCompatActivity implements View.OnClickListener {
    ActivityEditscreenBinding editscreenBinding;
    String editTitle;
    String proTitle;
    private Dialog saveDialog;
    private SaveSheetDialogBinding saveSheetDialogBinding;
    private ServerNotFoundBinding serverNotFoundBinding;
    private Dialog loadingDialog, serverNotFoundDialog;
    private String BASEURL = "https://toonifime.com/toonifyme";
    private String SUB_URL_NAME = "";
    private MultiTouchListener2 multiTouchListener2m;
    public static String LASTSAVEIMAGE;

    private LoadingBinding loadingBinding;

    private Bitmap apiCartoonBitmap = null;
    private Bitmap bitmap;
    Bitmap finalBitmap;
    public String savePath;
    String imagePath;
    private String convertedImageFileName = "";
    String saveImageName = convertedImageFileName + ".png";
    File fileSaveDir = new File(saveCartoonPhoto());
    File file2 = new File(fileSaveDir.getAbsolutePath() + File.separator + saveImageName);

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    String pictureType;

    private Bitmap sampleFilterBitmap;
    private int sampleFilterResource;
    private boolean iapFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editscreenBinding = ActivityEditscreenBinding.inflate(getLayoutInflater());
        View view = editscreenBinding.getRoot();
        setContentView(view);
        editTitle = getIntent().getStringExtra("TITLE");
        proTitle = getIntent().getStringExtra("PRO_TAG");
        pictureType = getIntent().getStringExtra("PICTURE");

        if (editTitle != null) {
            editscreenBinding.editTitle.setText(editTitle);
        }
        if (proTitle != null) {
            editscreenBinding.proTagText.setText(proTitle);
        }
        if (RemoteConfig.getRemoteConfig().getEnableIAPflag() != null) {
            if (RemoteConfig.getRemoteConfig().getEnableIAPflag().equals("true")) {
                editscreenBinding.proCard.setVisibility(View.VISIBLE);
                editscreenBinding.nextSave.proLayout.setVisibility(View.VISIBLE);
                editscreenBinding.nextSave.pro3X.setVisibility(View.VISIBLE);
            } else {
                editscreenBinding.proCard.setVisibility(View.GONE);
                editscreenBinding.nextSave.proLayout.setVisibility(View.GONE);
                editscreenBinding.nextSave.pro3X.setVisibility(View.GONE);
            }
        }
        editscreenBinding.proCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Editscreen.this, PremiumScreen.class).putExtra("PRO_FROM", "EDITSCREEN"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        editscreenBinding.nextSave.pro3X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Editscreen.this, PremiumScreen.class).putExtra("PRO_FROM", "EDITSCREEN"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        editscreenBinding.nextSave.proLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Editscreen.this, PremiumScreen.class).putExtra("PRO_FROM", "EDITSCREEN"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        editscreenBinding.editWithAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationSet animate = loadAnimation();
                editscreenBinding.appBarEdit.setVisibility(View.GONE);
                editscreenBinding.nextSave.nextLayLoad.setVisibility(View.VISIBLE);
                if (showInterstitialAd()) {
                    getDummyFilterImages();
                } else {
                    getDummyFilterImages();
                }
            }
        });
        editscreenBinding.nextSave.saveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSaveDialog();
            }
        });
        editscreenBinding.userImageEdit.setImageBitmap(mainActivity.globalBitmap);
        editscreenBinding.nextSave.onBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        /*magic layout Edit Click events */
        editscreenBinding.nextSave.tabClose.setOnClickListener(this);
        editscreenBinding.nextSave.enhance.setOnClickListener(this);
        editscreenBinding.nextSave.removeBg.setOnClickListener(this);
        editscreenBinding.nextSave.cartoon.setOnClickListener(this);
        editscreenBinding.nextSave.descratch.setOnClickListener(this);
        editscreenBinding.nextSave.erase.setOnClickListener(this);
        editscreenBinding.nextSave.lensBlur.setOnClickListener(this);
        editscreenBinding.nextSave.colorize.setOnClickListener(this);
        editscreenBinding.nextSave.brighten.setOnClickListener(this);
        editscreenBinding.nextSave.dehaze.setOnClickListener(this);
        editscreenBinding.nextSave.comment.setOnClickListener(this);

        /*ads*/
        showBannerAd();

    }

    private void showBannerAd() {
        if (RemoteConfig.getRemoteConfig() != null) {
            if (RemoteConfig.getRemoteConfig().getShowbannerAd() != null) {
                if (RemoteConfig.getRemoteConfig().getShowbannerAd().equals("true")) {
                    commonMethods.loadBannerAd(editscreenBinding.nextSave.bannerEraser, Editscreen.this);//load banner Ad
                }
            }
        }
    }

    private boolean showInterstitialAd() {
        boolean ads = false;
        if (RemoteConfig.getRemoteConfig() != null) {
            if (RemoteConfig.getRemoteConfig().getShowInterstitial() != null) {
                if (RemoteConfig.getRemoteConfig().getShowInterstitial().equals("true")) {
                    if (RemoteConfig.getRemoteConfig().getShowInterstitialapplyFilter() != null) {
                        if (RemoteConfig.getRemoteConfig().getShowInterstitialapplyFilter().equals("true")) {
                            ads = true;
                            commonMethods.showGoogleAd((Activity) Editscreen.this, Editscreen.this);
                            return ads;
                        }
                    }
                }
            }
        }
        return ads;
    }

    public AnimationSet loadAnimation() {

// For hiding the appBarEdit view with a fade-out animation
        Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(300); // Adjust the duration as needed
        editscreenBinding.appBarEdit.startAnimation(fadeOutAnimation);
        editscreenBinding.appBarEdit.setVisibility(View.GONE);

// For showing the nextLayLoad view with a fade-in and scale-up animation
        Animation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        fadeInAnimation.setDuration(300); // Adjust the duration as needed

        Animation scaleUpAnimation = new ScaleAnimation(0.5f, 1.0f, // X-axis scaling
                0.5f, 1.0f, // Y-axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point X
                Animation.RELATIVE_TO_SELF, 0.5f  // Pivot point Y
        );
        scaleUpAnimation.setDuration(300); // Adjust the duration as needed

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeInAnimation);
        animationSet.addAnimation(scaleUpAnimation);
        return animationSet;
    }

    private void getDummyFilterImages() {
        if (pictureType != null) {
            if (pictureType.equals("DemoImages")) {

                editscreenBinding.nextSave.magicToolsLayout.setVisibility(View.GONE);
                if (editTitle != null) {
                    if (editTitle.equals(getString(R.string.descratch))) {
                        sampleFilterResource = R.drawable.d_descratch_filter;
                        sampleFilterBitmap = BitmapFactory.decodeResource(getResources(), sampleFilterResource);
                    } else if (editTitle.equals(getString(R.string.lens_blur))) {
                        sampleFilterResource = R.drawable.d_lens_blur_filter;
                        sampleFilterBitmap = BitmapFactory.decodeResource(getResources(), sampleFilterResource);
                    } else if (editTitle.equals(getString(R.string.colorize))) {
                        sampleFilterResource = R.drawable.d_colorize_filter;
                        sampleFilterBitmap = BitmapFactory.decodeResource(getResources(), sampleFilterResource);
                    } else if (editTitle.equals(getString(R.string.brighten))) {
                        sampleFilterResource = R.drawable.d_brighten_filter;
                        sampleFilterBitmap = BitmapFactory.decodeResource(getResources(), sampleFilterResource);
                    } else if (editTitle.equals(getString(R.string.dehaze))) {
                        sampleFilterResource = R.drawable.d_dehaze_filter;
                        sampleFilterBitmap = BitmapFactory.decodeResource(getResources(), sampleFilterResource);
                    } else {
                        //setting the bitmap
                        editscreenBinding.userImageEdit.setImageBitmap(mainActivity.globalBitmap);
                        editscreenBinding.nextSave.userImageBCSave.setImageBitmap(mainActivity.globalBitmap);
                    }
                    if (sampleFilterBitmap != null) {
                        sampleFilterBitmap = BitmapFactory.decodeResource(getResources(), sampleFilterResource);
                        editscreenBinding.userImageEdit.setImageBitmap(mainActivity.globalBitmap);
                        editscreenBinding.nextSave.userImageBCSave.setImageBitmap(sampleFilterBitmap);

                    } else {
                        editscreenBinding.nextSave.userImageBCSave.setImageBitmap(mainActivity.globalBitmap);
                    }
                }

            } else {
                new ApplyfilterToImageAsyncTask().execute();
            }
        }
    }

    private void openSaveDialog() {
        saveDialog = new Dialog(Editscreen.this);
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
        if (!iapFlag) {
            saveSheetDialogBinding.proCardSave.setVisibility(View.GONE);
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
        saveSheetDialogBinding.saveAdsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveDialog != null) {
                    if (saveDialog.isShowing()) {
                        saveDialog.dismiss();
                        new Editscreen.saveAndGoimag().execute(new Void[0]);
                    } else {
                        new Editscreen.saveAndGoimag().execute(new Void[0]);

                    }
                }
            }
        });

    }

    public void imgEnhance() {

        new ApplyfilterToImageAsyncTask().execute();
    }


    public class saveAndGoimag extends AsyncTask<Void, Void, String> {
        @Override
        public String doInBackground(Void... voidArr) {
            Editscreen bchanger = Editscreen.this;
            bchanger.savePath = bchanger.savePhoto();
            out.println(savePath);
            return "";
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("WrongConstant")
        @Override
        public void onPostExecute(String str) {

            if (Editscreen.this.savePath.equals("")) {
                Toast.makeText(Editscreen.this, "Couldn't save photo, error", 0).show();
            } else {
                openSaveActivity();
            }
        }

    }

    public void noServerFound() {
        if (isFinishing()) {
            return;
        }
        serverNotFoundDialog = new Dialog(this);
        serverNotFoundDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        serverNotFoundBinding = ServerNotFoundBinding.inflate(getLayoutInflater());
        serverNotFoundDialog.setContentView(serverNotFoundBinding.getRoot());
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) serverNotFoundBinding.noserverCard.getLayoutParams();
        int marginInPixels = (int) getResources().getDimension(R.dimen.dialog_margin);/* Replace with your actual dimension resource*/

        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
        serverNotFoundBinding.noserverCard.setLayoutParams(layoutParams);
        serverNotFoundDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        serverNotFoundDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        serverNotFoundDialog.getWindow().setGravity(Gravity.CENTER);
        serverNotFoundDialog.setCancelable(false);
        serverNotFoundDialog.show();

        final TextView cancel_dialog = serverNotFoundDialog.findViewById(R.id.cancel_warning_dialog);

        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serverNotFoundDialog != null && serverNotFoundDialog.isShowing()) {
                    serverNotFoundDialog.dismiss();
                    goToHome();

                }
            }
        });


    }

    private void goToHome() {
        Intent intent = new Intent(Editscreen.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    public String savePhoto() {
        String str = "";
        try {

            Bitmap drawingCache = savePhotoFrame();
            String str2 = "PC" + String.valueOf(System.currentTimeMillis()) + ".png";
            File file = new File(pathtoSave());
            setImagePath(file + "/" + str2);
            if (!file.exists()) {
                file.mkdirs();
            }
            file2 = new File(file.getAbsolutePath() + File.separator + str2);


            try (FileOutputStream out = new FileOutputStream(file2)) {
                drawingCache.compress(Bitmap.CompressFormat.JPEG, 100, out);
                str = file.getAbsolutePath() + File.separator + str2;

                if (Build.VERSION.SDK_INT < 29) {
                    addImageGallery(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.editscreenBinding.nextSave.userImageBCSave.setDrawingCacheEnabled(false);
        } catch (Exception unused) {
        }
        LASTSAVEIMAGE = str;
        return str;
    }

    private void loadingDialog() {
        loadingDialog = new Dialog(Editscreen.this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingBinding = LoadingBinding.inflate(getLayoutInflater());
        loadingDialog.setContentView(loadingBinding.getRoot());

        /*  Apply margin to the CardView using layout parameters*/
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) loadingBinding.loadingLay.getLayoutParams();
        int marginInPixels = (int) getResources().getDimension(R.dimen.dialog_margin);/* Replace with your actual dimension resource*/
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
        loadingBinding.loadingLay.setLayoutParams(layoutParams);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.show();
    }


    public static String pathtoSave() {
        String SAVE_PATH = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() : Environment.getExternalStorageDirectory().toString();
        return new File(SAVE_PATH + "/AIEnhancer" + "/MyCreations").getPath();
    }

    public static String saveCartoonPhoto() {
        String SAVE_PATH = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() : Environment.getExternalStorageDirectory().toString();
        return new File(SAVE_PATH + "/AIEnhancer" + "/CartoonPhoto").getPath();
    }

    private void addImageGallery(String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_data", str);
        contentValues.put("mime_type", "image/jpeg");
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    public Bitmap savePhotoFrame() {
        editscreenBinding.nextSave.mainLayoutSave.setDrawingCacheEnabled(true);
        editscreenBinding.nextSave.mainLayoutSave.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(editscreenBinding.nextSave.mainLayoutSave.getDrawingCache());
        editscreenBinding.nextSave.mainLayoutSave.setDrawingCacheEnabled(false);
        mainActivity.globalBitmap = bitmap;
        return bitmap;
    }


    private void openSaveActivity() {
        Intent intent = new Intent(this, SaveScreen.class);
        intent.putExtra("savedImage", savePath);
        intent.putExtra("PICTURE", pictureType);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public class ApplyfilterToImageAsyncTask extends AsyncTask<Void, Integer, Bitmap> {

        private Context context;
        private int totalItems = 100;
        private int currentProgress = 0;

        public ApplyfilterToImageAsyncTask() {

        }

        @Override
        protected void onPreExecute() {
            loadingDialog();
            //errorImageView.setVisibility(View.GONE);
//            editscreenBinding.userImageEdit.setVisibility(View.VISIBLE);
            //placeHolderImageView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            switch (progress) {
                case 10:
                    loadingBinding.loadingStatus.setText("Sending...");
                    break;
                case 30:
                    loadingBinding.loadingStatus.setText("Processing...");
                    break;
                case 50:
                    loadingBinding.loadingStatus.setText("Converting...");
                    break;
                case 70:
                    loadingBinding.loadingStatus.setText("Applying...");
                    break;
                case 100:
                    loadingBinding.loadingStatus.setText("Generating...");
                    break;
            }
            String progressText = String.valueOf(progress);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            /*for (int item = 0; item < totalItems; item++) {
                currentProgress = (int) (((float) (item + 1) / totalItems) * 100);
                publishProgress(currentProgress);
                try {
                    Thread.sleep(140);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/

            if (BASEURL != null || SUB_URL_NAME != null) {

                try {
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES).build();

                    if (mainActivity != null && mainActivity.globalBitmap != null) {
                        bitmap = mainActivity.globalBitmap;
                    }

                    // Compress the bitmap
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                    // Convert to base64
                    String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    JSONObject jsonParams = new JSONObject();
                    jsonParams.put("Base64", base64Image);
                    jsonParams.put("style_id", 26);

                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonParams.toString());
                    String completeUrl = BASEURL + SUB_URL_NAME;
                    Request request = new Request.Builder().url(completeUrl).addHeader("Content-Type", "application/json").post(requestBody).build();

                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        // Here response Handled
                        String resp = response.body().string();
                        JSONObject responseJson = new JSONObject(resp);
                        String status = responseJson.getString("Status");
                        if (status.equals("200")) {
                            JSONObject res = responseJson.getJSONObject("Response");
                            String bas = res.getString("output_base64");
                            if (bas.contains("Traceback")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //placeHolderImageView.setVisibility(View.GONE);
                                        //errorImageView.setVisibility(View.VISIBLE);
                                        editscreenBinding.userImageEdit.setVisibility(View.GONE);
                                        editscreenBinding.nextSave.userImageBCSave.setVisibility(View.GONE);
                                        noServerFound();
                                    }
                                });
                            } else {
                                byte[] decodedBytes = Base64.decode(bas, Base64.DEFAULT);
                                apiCartoonBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        editscreenBinding.userImageEdit.setImageBitmap(apiCartoonBitmap);
                                        editscreenBinding.nextSave.userImageBCSave.setImageBitmap(apiCartoonBitmap);
                                        finalBitmap = apiCartoonBitmap;
                                    }
                                });
                            }

                        } else {
                            //  'noServerFound()' dialog on the main (UI) thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //placeHolderImageView.setVisibility(View.GONE);
                                    //errorImageView.setVisibility(View.VISIBLE);
                                    editscreenBinding.userImageEdit.setVisibility(View.GONE);
                                    editscreenBinding.nextSave.userImageBCSave.setVisibility(View.GONE);
                                    noServerFound();
                                }
                            });
                        }
                    } else {

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // 'noServerFound()' dialog on the main (UI) thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //placeHolderImageView.setVisibility(View.GONE);
                            //errorImageView.setVisibility(View.VISIBLE);
                            editscreenBinding.userImageEdit.setVisibility(View.GONE);
                            editscreenBinding.nextSave.userImageBCSave.setVisibility(View.GONE);
                            noServerFound();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    // 'noServerFound()' dialog on the main (UI) thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //placeHolderImageView.setVisibility(View.GONE);
                            //errorImageView.setVisibility(View.VISIBLE);
                            editscreenBinding.userImageEdit.setVisibility(View.GONE);
                            editscreenBinding.nextSave.userImageBCSave.setVisibility(View.GONE);
                            noServerFound();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //placeHolderImageView.setVisibility(View.GONE);
                        //errorImageView.setVisibility(View.VISIBLE);
                        editscreenBinding.userImageEdit.setVisibility(View.GONE);
                        editscreenBinding.nextSave.userImageBCSave.setVisibility(View.GONE);
                        noServerFound();
                    }
                });
            }
            return apiCartoonBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (!isFinishing()) {
                if (bitmap != null) {
                    editscreenBinding.userImageEdit.setImageBitmap(bitmap);
                    editscreenBinding.nextSave.userImageBCSave.setImageBitmap(bitmap);
                    mainActivity.globalBitmap = bitmap;
                /*    mainActivity.CheckActivity = "CartoonBitmap";
                    Next.setVisibility(View.VISIBLE);
                    if (modelName.equals("2DCARTOON")) {
                        bitmap1 = bitmap;
                    } else if (modelName.equals("CARICATURE")) {
                        bitmap2 = bitmap;
                    } else if (modelName.equals("ARCANEFILTER")) {
                        bitmap3 = bitmap;
                    } else if (modelName.equals("PIXARFILTER")) {
                        bitmap4 = bitmap;
                    } else if (modelName.equals("COMIC")) {
                        bitmap5 = bitmap;
                    } else if (modelName.equals("ILLUSTRATION")) {
                        bitmap6 = bitmap;
                    }*/
                    if (loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }

                } else {
                    if (loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    noServerFound();
                }
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tab_close:
                editscreenBinding.nextSave.magicToolsLayout.setVisibility(View.GONE);
                break;
            case R.id.comment:
                startActivity(new Intent(Editscreen.this, FeedBack.class).putExtra("INTENT_FROM", "EDITOR"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.enhance:
                editscreenBinding.nextSave.magicToolsLayout.setVisibility(View.GONE);

                break;
            case R.id.remove_bg:
                editscreenBinding.nextSave.magicToolsLayout.setVisibility(View.GONE);
                startActivity(new Intent(Editscreen.this, BackgroundChanger.class).putExtra("TITLE", getString(R.string.remove_bg)).putExtra("PRO_TAG", getString(R.string.remove_pro_bg)).putExtra("PICTURE", pictureType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.cartoon:
                editscreenBinding.nextSave.magicToolsLayout.setVisibility(View.GONE);
                mainActivity.globalBitmap = savePhotoFrame();
                startActivity(new Intent(Editscreen.this, CartoonSelfi.class).putExtra("TITLE", getString(R.string.cartton)).putExtra("PRO_TAG", getString(R.string.pro_cartoon)).putExtra("PICTURE", pictureType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.descratch:
                editscreenBinding.nextSave.magicToolsLayout.setVisibility(View.GONE);
                break;
            case R.id.erase:
                editscreenBinding.nextSave.magicToolsLayout.setVisibility(View.GONE);
                mainActivity.globalBitmap = savePhotoFrame();
                startActivity(new Intent(Editscreen.this, RemoveObject.class).putExtra("PICTURE", pictureType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.lens_blur:
                editscreenBinding.nextSave.magicToolsLayout.setVisibility(View.GONE);
                break;
            case R.id.colorize:
                editscreenBinding.nextSave.magicToolsLayout.setVisibility(View.GONE);
                break;
            case R.id.brighten:
                editscreenBinding.nextSave.magicToolsLayout.setVisibility(View.GONE);
                break;
            case R.id.dehaze:
                editscreenBinding.nextSave.magicToolsLayout.setVisibility(View.GONE);
                break;


        }
    }

    private void checkIapFlag() {
        if (RemoteConfig.getRemoteConfig().getEnableIAPflag() != null && RemoteConfig.getRemoteConfig().getEnableIAPflag().equals("true")) {
            editscreenBinding.nextSave.xCard.setPadding(5, 5, 30, 5);
            editscreenBinding.nextSave.proLayout.setVisibility(View.VISIBLE);
            editscreenBinding.nextSave.pro3X.setVisibility(View.VISIBLE);
            iapFlag = true;

        } else {
            iapFlag = false;
            editscreenBinding.nextSave.xCard.setPadding(5, 5, 5, 5);
            editscreenBinding.nextSave.proLayout.setVisibility(View.GONE);
            editscreenBinding.nextSave.pro3X.setVisibility(View.GONE);
        }
    }
}