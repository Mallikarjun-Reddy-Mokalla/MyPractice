package com.aapthitech.android.developers.Activities;

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
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aapthitech.android.developers.BackgroundChanger;
import com.aapthitech.android.developers.Data.RemoteConfig;
import com.aapthitech.android.developers.Editscreen;
import com.aapthitech.android.developers.IAP.PremiumScreen;
import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.SaveScreen;
import com.aapthitech.android.developers.databinding.ActivityCartoonSelfiBinding;
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

public class CartoonSelfi extends AppCompatActivity {
    ActivityCartoonSelfiBinding cartoonSelfiBinding;
    private Dialog saveDialog;
    private SaveSheetDialogBinding saveSheetDialogBinding;
    private ServerNotFoundBinding serverNotFoundBinding;
    private Dialog loadingDialog, serverNotFoundDialog;
    //    private String BASEURL = "https://toonifime.com/toonifyme";
    private String BASEURL = "";
    private String SUB_URL_NAME = "";

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
    public static String LASTSAVEIMAGE;

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    String title;
    String proTagTitle;
    String pictureType;
    int demoimageResource;
    Bitmap demoCartoonFilterBitmap;
    private boolean iapFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cartoonSelfiBinding = ActivityCartoonSelfiBinding.inflate(getLayoutInflater());
        View view = cartoonSelfiBinding.getRoot();
        setContentView(view);

        title = getIntent().getStringExtra("TITLE");
        proTagTitle = getIntent().getStringExtra("PRO_TAG");
        pictureType = getIntent().getStringExtra("PICTURE");
        if (pictureType != null) checkPictype(pictureType);
        if (title != null) {
            cartoonSelfiBinding.title.setText(title);
        }
        if (proTagTitle != null) {
            cartoonSelfiBinding.proText.setText(proTagTitle);
        }
        cartoonSelfiBinding.userImage.setImageBitmap(mainActivity.globalBitmap);
        if (RemoteConfig.getRemoteConfig().getEnableIAPflag() != null) {
            if (RemoteConfig.getRemoteConfig().getEnableIAPflag().equals("true")) {
                cartoonSelfiBinding.proCard.setVisibility(View.VISIBLE);
                iapFlag = true;
            } else {
                iapFlag = false;
                cartoonSelfiBinding.proCard.setVisibility(View.GONE);
            }
        }
        cartoonSelfiBinding.cartoonTabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
            }
        });
        cartoonSelfiBinding.adsCardCartoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showInterstitialAd()) {
                    performEditing();
                } else {
                    performEditing();
                }
            }
        });
        cartoonSelfiBinding.magicCartoonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.GONE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.GONE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.proAdLay.setVisibility(View.GONE);
            }
        });
        cartoonSelfiBinding.cartoonOnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cartoonSelfiBinding.saveCartoonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSaveDialog();

            }
        });
        cartoonSelfiBinding.cartoon.setVisibility(View.GONE);//
        cartoonSelfiBinding.erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
                startActivity(new Intent(CartoonSelfi.this, RemoveObject.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        cartoonSelfiBinding.enhance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
                startActivity(new Intent(CartoonSelfi.this, AIEditor.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.enhance_title)).putExtra("PRO_TAG", getString(R.string.pro_enhance)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        cartoonSelfiBinding.lensBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
                startActivity(new Intent(CartoonSelfi.this, Editscreen.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.lens_blur)).putExtra("PRO_TAG", getString(R.string.pro_lens)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        cartoonSelfiBinding.colorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
                startActivity(new Intent(CartoonSelfi.this, Editscreen.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.colorize)).putExtra("PRO_TAG", getString(R.string.pro_colorize)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        cartoonSelfiBinding.brighten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
                startActivity(new Intent(CartoonSelfi.this, Editscreen.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.brighten)).putExtra("PRO_TAG", getString(R.string.pro_brighten)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        cartoonSelfiBinding.dehaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
                startActivity(new Intent(CartoonSelfi.this, Editscreen.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.dehaze)).putExtra("PRO_TAG", getString(R.string.pro_dehaze)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        cartoonSelfiBinding.descratch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
                startActivity(new Intent(CartoonSelfi.this, Editscreen.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.descratch)).putExtra("PRO_TAG", getString(R.string.pro_descratch)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        cartoonSelfiBinding.removeBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
                startActivity(new Intent(CartoonSelfi.this, BackgroundChanger.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.removebg)).putExtra("PRO_TAG", getString(R.string.remove_pro_bg)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        cartoonSelfiBinding.proCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                startActivity(new Intent(CartoonSelfi.this, PremiumScreen.class).putExtra("PRO_FROM", "CARTOONSELFI"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


    }

    private void openSaveDialog() {
        saveDialog = new Dialog(CartoonSelfi.this);
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
                if (saveDialog != null && saveDialog.isShowing()) {

                    saveDialog.dismiss();
                    new saveAndGoimag().execute(new Void[0]);

                } else {
                    new saveAndGoimag().execute(new Void[0]);
                }

            }
        });


    }

    public String pathtoSave() {
        String SAVE_PATH = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() : Environment.getExternalStorageDirectory().toString();
        return new File(SAVE_PATH + "/AIEnhancer" + "/MyCreations").getPath();
    }

    public String saveCartoonPhoto() {
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
        cartoonSelfiBinding.mainLayout.setDrawingCacheEnabled(true);
        cartoonSelfiBinding.mainLayout.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cartoonSelfiBinding.mainLayout.getDrawingCache());
        cartoonSelfiBinding.mainLayout.setDrawingCacheEnabled(false);
        mainActivity.globalBitmap = bitmap;
        return bitmap;
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

    private void loadingDialog() {
        loadingDialog = new Dialog(CartoonSelfi.this);
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

    private void goToHome() {
        Intent intent = new Intent(CartoonSelfi.this, MainActivity.class);
        startActivity(intent);
        finish();

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
            cartoonSelfiBinding.userImage.setVisibility(View.VISIBLE);
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
            if (RemoteConfig.getRemoteConfig() != null) {
                if (RemoteConfig.getRemoteConfig().getBaseUrl() != null) {
                    BASEURL = RemoteConfig.getRemoteConfig().getBaseUrl();
                }
            }
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
                                        cartoonSelfiBinding.userImage.setVisibility(View.GONE);
                                        noServerFound();
                                    }
                                });
                            } else {
                                byte[] decodedBytes = Base64.decode(bas, Base64.DEFAULT);
                                apiCartoonBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        cartoonSelfiBinding.userImage.setImageBitmap(apiCartoonBitmap);
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
                                    cartoonSelfiBinding.userImage.setVisibility(View.GONE);
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
                            cartoonSelfiBinding.userImage.setVisibility(View.GONE);
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
                            cartoonSelfiBinding.userImage.setVisibility(View.GONE);
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
                        cartoonSelfiBinding.userImage.setVisibility(View.GONE);
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
                    cartoonSelfiBinding.userImage.setImageBitmap(bitmap);
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
        } catch (Exception unused) {
        }
        LASTSAVEIMAGE = str;
        return str;
    }

    private class saveAndGoimag extends AsyncTask<Void, Void, String> {
        @Override
        public String doInBackground(Void... voidArr) {

            CartoonSelfi cartoonSelfi = CartoonSelfi.this;
            cartoonSelfi.savePath = cartoonSelfi.savePhoto();
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

            if (CartoonSelfi.this.savePath.equals("")) {
                Toast.makeText(CartoonSelfi.this, "Couldn't save photo, error", 0).show();
            } else {
                openSaveActivity();
            }
        }

    }

    private void openSaveActivity() {

        Intent intent = new Intent(this, SaveScreen.class);
        intent.putExtra("savedImage", savePath);
        intent.putExtra("PICTURE", pictureType);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    private boolean showInterstitialAd() {
        boolean adLoaded = false;
        if (RemoteConfig.getRemoteConfig() != null && RemoteConfig.getRemoteConfig().getShowInterstitial() != null && RemoteConfig.getRemoteConfig().getShowInterstitialapplyFilter() != null) {
            if (RemoteConfig.getRemoteConfig().getShowInterstitial().equals("true") && RemoteConfig.getRemoteConfig().getShowInterstitialapplyFilter().equals("true")) {
                adLoaded = commonMethods.displayInterstitialAd((Activity) CartoonSelfi.this, CartoonSelfi.this);
            }
        }
        return adLoaded;
    }

    private void checkPictype(String pictureType) {
        if (!pictureType.equals("DemoImages")) {
            cartoonSelfiBinding.erase.setVisibility(View.GONE);
            cartoonSelfiBinding.lensBlur.setVisibility(View.GONE);
            cartoonSelfiBinding.brighten.setVisibility(View.GONE);
            cartoonSelfiBinding.colorize.setVisibility(View.GONE);
            cartoonSelfiBinding.descratch.setVisibility(View.GONE);
            cartoonSelfiBinding.dehaze.setVisibility(View.GONE);
            cartoonSelfiBinding.cartoon.setVisibility(View.VISIBLE);
            cartoonSelfiBinding.enhance.setVisibility(View.VISIBLE);
            cartoonSelfiBinding.removeBg.setVisibility(View.VISIBLE);
        } else {
            cartoonSelfiBinding.erase.setVisibility(View.GONE);
            cartoonSelfiBinding.lensBlur.setVisibility(View.GONE);
            cartoonSelfiBinding.brighten.setVisibility(View.GONE);
            cartoonSelfiBinding.colorize.setVisibility(View.GONE);
            cartoonSelfiBinding.descratch.setVisibility(View.GONE);
            cartoonSelfiBinding.dehaze.setVisibility(View.GONE);
            cartoonSelfiBinding.cartoon.setVisibility(View.VISIBLE);
            cartoonSelfiBinding.enhance.setVisibility(View.VISIBLE);
            cartoonSelfiBinding.removeBg.setVisibility(View.VISIBLE);

        }
    }

    private boolean showBannerAd() {
        boolean banner = false;
        if (RemoteConfig.getRemoteConfig() != null) {
            if (RemoteConfig.getRemoteConfig().getShowbannerAd() != null) {
                if (RemoteConfig.getRemoteConfig().getShowbannerAd().equals("true")) {
                    cartoonSelfiBinding.bannerCartoon.setVisibility(View.VISIBLE);
                    banner = true;
                    commonMethods.loadBannerAd(cartoonSelfiBinding.bannerCartoon, CartoonSelfi.this);

                }
            }
        }
        return banner;
    }

    private void performEditing() {
        if (showBannerAd()) {
            cartoonSelfiBinding.bannerCartoon.setVisibility(View.VISIBLE);

        } else {
            cartoonSelfiBinding.bannerCartoon.setVisibility(View.GONE);

        }
        cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
        cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
        cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
        cartoonSelfiBinding.proAdLay.setVisibility(View.GONE);
        if (pictureType != null) {
            if (pictureType.equals("DemoImages")) {
                demoimageResource = R.drawable.d_cartoon_selfie_filter;
                demoCartoonFilterBitmap = BitmapFactory.decodeResource(getResources(), demoimageResource);
                if (demoCartoonFilterBitmap != null) {
                    cartoonSelfiBinding.userImage.setImageBitmap(demoCartoonFilterBitmap);
                }
            } else {
                SUB_URL_NAME = "/caricature";
                new ApplyfilterToImageAsyncTask().execute();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }


}