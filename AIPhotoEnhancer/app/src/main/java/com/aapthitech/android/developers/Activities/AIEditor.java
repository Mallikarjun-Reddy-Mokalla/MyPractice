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
import com.aapthitech.android.developers.TouchEvents.MultiTouchListener2;
import com.aapthitech.android.developers.databinding.ActivityAieditorBinding;
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

public class AIEditor extends AppCompatActivity {
    ActivityAieditorBinding aieditorBinding;
    String titleText;
    String proTag;
    String effectTitle;
    private Dialog saveDialog;
    private SaveSheetDialogBinding saveSheetDialogBinding;
    private ServerNotFoundBinding serverNotFoundBinding;
    private Dialog loadingDialog, serverNotFoundDialog;
    //    private String BASEURL = "https://toonifime.com/toonifyme";
    private String BASEURL = "";
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
    boolean iapFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        aieditorBinding = ActivityAieditorBinding.inflate(getLayoutInflater());
        View view = aieditorBinding.getRoot();
        setContentView(view);
        titleText = getIntent().getStringExtra("TITLE");
        effectTitle = getIntent().getStringExtra("TITLE");
        proTag = getIntent().getStringExtra("PRO_TAG");
        if (RemoteConfig.getRemoteConfig().getEnableIAPflag() != null) {
            if (RemoteConfig.getRemoteConfig().getEnableIAPflag().equals("true")) {
                aieditorBinding.proCard.setVisibility(View.VISIBLE);
            } else {
                aieditorBinding.proCard.setVisibility(View.GONE);

            }
        }
        if (titleText != null) {
            aieditorBinding.titleType.setText(titleText);
            aieditorBinding.effectType.setText(titleText);
        }
        if (proTag != null) {
            aieditorBinding.proTagText.setText(proTag);

        }
        showBannerAd();
        checkIapFlag();
        assert titleText != null;
        pictureType = getIntent().getStringExtra("PICTURE");
        if (pictureType != null) checkPictype(pictureType);
        if (titleText.equals(getString(R.string.enhance))) {
            SUB_URL_NAME = "/superres";
        }

        aieditorBinding.nextLayEdit.magicEnhanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aieditorBinding.nextLayEdit.saveMagicLayout.setVisibility(View.GONE);
                aieditorBinding.nextLayEdit.magicToolsLayout.setVisibility(View.VISIBLE);
            }
        });
        aieditorBinding.nextLayEdit.tabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aieditorBinding.nextLayEdit.saveMagicLayout.setVisibility(View.VISIBLE);
                aieditorBinding.nextLayEdit.magicToolsLayout.setVisibility(View.GONE);
            }
        });
        aieditorBinding.onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        aieditorBinding.nextLayEdit.onBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        aieditorBinding.nextLayEdit.saveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSaveDialog();
            }
        });
        aieditorBinding.userImageEditor.setImageBitmap(mainActivity.globalBitmap);
        aieditorBinding.nextLayEdit.userImageBCSave.setImageBitmap(mainActivity.globalBitmap);
        multiTouchListener2m = new MultiTouchListener2(aieditorBinding.userImageEditor, AIEditor.this);
        multiTouchListener2m = new MultiTouchListener2(aieditorBinding.nextLayEdit.userImageBCSave, AIEditor.this);
        aieditorBinding.userImageEditor.setOnTouchListener(multiTouchListener2m);
        aieditorBinding.nextLayEdit.userImageBCSave.setOnTouchListener(multiTouchListener2m);
        aieditorBinding.adsCardEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pictureType != null) {
                    if (pictureType.equals("DemoImages")) {
                        if (showInterstitialAd()) {
                            aieditorBinding.nextLayEdit.userImageBCSave.setVisibility(View.VISIBLE);
                            aieditorBinding.nextLayEdit.userImageBCSave.setImageBitmap(mainActivity.globalBitmap);

                        } else {
                            aieditorBinding.nextLayEdit.userImageBCSave.setVisibility(View.VISIBLE);
                            aieditorBinding.nextLayEdit.userImageBCSave.setImageBitmap(mainActivity.globalBitmap);

                        }
                    } else {
                        if (showInterstitialAd()) {
                            imgEnhance();
                        } else {
                            imgEnhance();
                        }
                    }
                }
                aieditorBinding.nextLayEdit.proLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(AIEditor.this, PremiumScreen.class).putExtra("PRO_FROM", "AIEDITOR"));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
                aieditorBinding.nextLayEdit.pro3X.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(AIEditor.this, PremiumScreen.class).putExtra("PRO_FROM", "AIEDITOR"));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
                aieditorBinding.appBarLay.setVisibility(View.GONE);
                aieditorBinding.nextLayEdit.nextLayLoad.setVisibility(View.VISIBLE);
            }
        });
        aieditorBinding.proCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AIEditor.this, PremiumScreen.class).putExtra("PRO_FROM", "AIEDITOR"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        aieditorBinding.nextLayEdit.enhance.setVisibility(View.GONE);//
        aieditorBinding.nextLayEdit.cartoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                startActivity(new Intent(AIEditor.this, CartoonSelfi.class).putExtra("TITLE", getString(R.string.cartton)).putExtra("PRO_TAG", getString(R.string.pro_cartoon)).putExtra("PICTURE", pictureType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        aieditorBinding.nextLayEdit.erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                startActivity(new Intent(AIEditor.this, RemoveObject.class).putExtra("PICTURE", pictureType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        aieditorBinding.nextLayEdit.removeBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();

                startActivity(new Intent(AIEditor.this, BackgroundChanger.class).putExtra("TITLE", getString(R.string.remove_bg)).putExtra("PRO_TAG", getString(R.string.remove_pro_bg)).putExtra("PICTURE", pictureType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        aieditorBinding.nextLayEdit.lensBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();

                startActivity(new Intent(AIEditor.this, Editscreen.class).putExtra("TITLE", getString(R.string.lens_blur)).putExtra("PRO_TAG", getString(R.string.pro_lens)).putExtra("PICTURE", pictureType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        aieditorBinding.nextLayEdit.colorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();

                startActivity(new Intent(AIEditor.this, Editscreen.class).putExtra("TITLE", getString(R.string.colorize)).putExtra("PRO_TAG", getString(R.string.pro_colorize)).putExtra("PICTURE", pictureType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        aieditorBinding.nextLayEdit.brighten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();

                startActivity(new Intent(AIEditor.this, Editscreen.class).putExtra("TITLE", getString(R.string.brighten)).putExtra("PRO_TAG", getString(R.string.pro_brighten)).putExtra("PICTURE", pictureType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        aieditorBinding.nextLayEdit.dehaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();

                startActivity(new Intent(AIEditor.this, Editscreen.class).putExtra("TITLE", getString(R.string.dehaze)).putExtra("PRO_TAG", getString(R.string.pro_dehaze)).putExtra("PICTURE", pictureType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        aieditorBinding.nextLayEdit.descratch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();

                startActivity(new Intent(AIEditor.this, Editscreen.class).putExtra("TITLE", getString(R.string.descratch)).putExtra("PRO_TAG", getString(R.string.pro_descratch)).putExtra("PICTURE", pictureType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        aieditorBinding.nextLayEdit.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AIEditor.this, FeedBack.class).putExtra("INTENT_FROM", "AIEDITOR"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });


    }

    private void checkIapFlag() {
        if (RemoteConfig.getRemoteConfig().getEnableIAPflag() != null && RemoteConfig.getRemoteConfig().getEnableIAPflag().equals("true")) {
            aieditorBinding.nextLayEdit.xCard.setPadding(5, 5, 30, 5);
            aieditorBinding.nextLayEdit.proLayout.setVisibility(View.VISIBLE);
            aieditorBinding.nextLayEdit.pro3X.setVisibility(View.VISIBLE);
            iapFlag = true;

        } else {
            iapFlag = false;
            aieditorBinding.nextLayEdit.xCard.setPadding(5, 5, 5, 5);
            aieditorBinding.nextLayEdit.proLayout.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.pro3X.setVisibility(View.GONE);
        }
    }

    private void openSaveDialog() {
        saveDialog = new Dialog(AIEditor.this);
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
        saveSheetDialogBinding.proCardSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.globalBitmap = savePhotoFrame();
                startActivity(new Intent(AIEditor.this, PremiumScreen.class).putExtra("PRO_FROM", "AIEDITOR"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    public void imgEnhance() {

        new ApplyfilterToImageAsyncTask().execute();
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
        loadingDialog = new Dialog(AIEditor.this);
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
        Intent intent = new Intent(AIEditor.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    public static String pathtoSave() {
        String SAVE_PATH = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() : Environment.getExternalStorageDirectory().toString();
        return new File(SAVE_PATH + "/PictureCraft" + "/MyCreations").getPath();
    }

    public static String saveCartoonPhoto() {
        String SAVE_PATH = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() : Environment.getExternalStorageDirectory().toString();
        return new File(SAVE_PATH + "/PictureCraft" + "/CartoonPhoto").getPath();
    }

    private void addImageGallery(String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_data", str);
        contentValues.put("mime_type", "image/jpeg");
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    public Bitmap savePhotoFrame() {
        aieditorBinding.nextLayEdit.mainLayoutSave.setDrawingCacheEnabled(true);
        aieditorBinding.nextLayEdit.mainLayoutSave.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(aieditorBinding.nextLayEdit.mainLayoutSave.getDrawingCache());
        aieditorBinding.nextLayEdit.mainLayoutSave.setDrawingCacheEnabled(false);
        mainActivity.globalBitmap = bitmap;
        return bitmap;
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
            this.aieditorBinding.nextLayEdit.userImageBCSave.setDrawingCacheEnabled(false);
        } catch (Exception unused) {
        }
        LASTSAVEIMAGE = str;
        return str;
    }

    public class saveAndGoimag extends AsyncTask<Void, Void, String> {
        @Override
        public String doInBackground(Void... voidArr) {

            AIEditor edit = AIEditor.this;
            edit.savePath = edit.savePhoto();
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

            if (AIEditor.this.savePath.equals("")) {
                Toast.makeText(AIEditor.this, "Couldn't save photo, error", 0).show();
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
//            aieditorBinding.userImageEditor.setVisibility(View.VISIBLE);
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
                                        aieditorBinding.userImageEditor.setVisibility(View.GONE);
                                        aieditorBinding.nextLayEdit.userImageBCSave.setVisibility(View.GONE);
                                        noServerFound();
                                    }
                                });
                            } else {
                                byte[] decodedBytes = Base64.decode(bas, Base64.DEFAULT);
                                apiCartoonBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        aieditorBinding.userImageEditor.setImageBitmap(apiCartoonBitmap);
                                        aieditorBinding.nextLayEdit.userImageBCSave.setImageBitmap(apiCartoonBitmap);
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
                                    aieditorBinding.userImageEditor.setVisibility(View.GONE);
                                    aieditorBinding.nextLayEdit.userImageBCSave.setVisibility(View.GONE);
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
                            aieditorBinding.userImageEditor.setVisibility(View.GONE);
                            aieditorBinding.nextLayEdit.userImageBCSave.setVisibility(View.GONE);
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
                            aieditorBinding.userImageEditor.setVisibility(View.GONE);
                            aieditorBinding.nextLayEdit.userImageBCSave.setVisibility(View.GONE);
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
                        aieditorBinding.userImageEditor.setVisibility(View.GONE);
                        aieditorBinding.nextLayEdit.userImageBCSave.setVisibility(View.GONE);
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
                    aieditorBinding.userImageEditor.setImageBitmap(bitmap);
                    aieditorBinding.nextLayEdit.userImageBCSave.setImageBitmap(bitmap);
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

    private boolean showInterstitialAd() {
        boolean adLoaded = false;
        if (RemoteConfig.getRemoteConfig() != null && RemoteConfig.getRemoteConfig().getShowInterstitial() != null && RemoteConfig.getRemoteConfig().getShowInterstitialapplyFilter() != null) {
            if (RemoteConfig.getRemoteConfig().getShowInterstitial().equals("true") && RemoteConfig.getRemoteConfig().getShowInterstitialapplyFilter().equals("true")) {
                adLoaded = commonMethods.displayInterstitialAd((Activity) AIEditor.this, AIEditor.this);
            }
        }
        return adLoaded;
    }


    private void checkPictype(String pictureType) {
        if (!pictureType.equals("DemoImages")) {
            aieditorBinding.nextLayEdit.erase.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.lensBlur.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.brighten.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.colorize.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.descratch.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.dehaze.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.cartoon.setVisibility(View.VISIBLE);
            aieditorBinding.nextLayEdit.enhance.setVisibility(View.VISIBLE);
        } else {
            aieditorBinding.nextLayEdit.erase.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.lensBlur.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.brighten.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.colorize.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.descratch.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.dehaze.setVisibility(View.GONE);
            aieditorBinding.nextLayEdit.cartoon.setVisibility(View.VISIBLE);
            aieditorBinding.nextLayEdit.enhance.setVisibility(View.VISIBLE);
        }
    }

    private void showBannerAd() {
        if (RemoteConfig.getRemoteConfig() != null) {
            if (RemoteConfig.getRemoteConfig().getShowbannerAd() != null) {
                if (RemoteConfig.getRemoteConfig().getShowbannerAd().equals("true")) {
                    commonMethods.loadBannerAd(aieditorBinding.nextLayEdit.bannerEraser, AIEditor.this);

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}