package com.aapthitech.android.developers;


import static com.aapthitech.android.developers.Activities.MainActivity.mainActivity;
import static com.aapthitech.android.developers.Data.CommonMethods.commonMethods;

import static java.lang.System.out;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
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

import com.aapthitech.android.developers.Activities.AIEditor;
import com.aapthitech.android.developers.Activities.CartoonSelfi;
import com.aapthitech.android.developers.Activities.FeedBack;
import com.aapthitech.android.developers.Activities.MainActivity;
import com.aapthitech.android.developers.Activities.RemoveObject;
import com.aapthitech.android.developers.Data.RemoteConfig;
import com.aapthitech.android.developers.IAP.PremiumScreen;
import com.aapthitech.android.developers.TouchEvents.MultiTouchListener2;
import com.aapthitech.android.developers.databinding.ActivityBackgroundChangerBinding;
import com.aapthitech.android.developers.databinding.ExitSheetDialogBinding;
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

public class BackgroundChanger extends AppCompatActivity {
    ActivityBackgroundChangerBinding changerBinding;
    private SaveSheetDialogBinding saveSheetDialogBinding;
    private ServerNotFoundBinding serverNotFoundBinding;
    Dialog loadingDialog, serverNotFoundDialog;
    LoadingBinding loadingBinding;
    private String titleText;
    private String proTag;
    private String effectTitle;
    private Dialog saveDialog;
    private String BASEURL = "http://stage.toonifime.com/toonifyme/erase";
    Bitmap apiCartoonBitmap = null;
    Bitmap bitmap;
    public MultiTouchListener2 multiTouchListener2m;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        changerBinding = ActivityBackgroundChangerBinding.inflate(getLayoutInflater());
        View view = changerBinding.getRoot();
        setContentView(view);
        titleText = getIntent().getStringExtra("TITLE");
        effectTitle = getIntent().getStringExtra("TITLE");
        proTag = getIntent().getStringExtra("PRO_TAG");
        changerBinding.userImageBC.setImageBitmap(mainActivity.globalBitmap);
        changerBinding.nextLaySave.userImageBCSave.setImageBitmap(mainActivity.globalBitmap);

        if (titleText != null) {
            changerBinding.titleType.setText(titleText);
            changerBinding.effectType.setText(titleText);
        }
        if (proTag != null) {
            changerBinding.proTagText.setText(proTag);

        }
        changerBinding.adsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changerBinding.appBarLayBg.setVisibility(View.GONE);
                changerBinding.nextLaySave.nextLayLoad.setVisibility(View.VISIBLE);

                if (changerBinding.userImageBC != null) {
                    new ApplyfilterToImageAsyncTask().execute();
                } else {
                    Toast.makeText(BackgroundChanger.this, "Please Upload Image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        changerBinding.proCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BackgroundChanger.this, PremiumScreen.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        changerBinding.nextLaySave.magicEnhanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changerBinding.nextLaySave.saveMagicLayout.setVisibility(View.GONE);
                changerBinding.nextLaySave.magicToolsLayout.setVisibility(View.VISIBLE);
            }
        });
        changerBinding.nextLaySave.tabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changerBinding.nextLaySave.saveMagicLayout.setVisibility(View.VISIBLE);
                changerBinding.nextLaySave.magicToolsLayout.setVisibility(View.GONE);
            }
        });
        changerBinding.onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        changerBinding.nextLaySave.onBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        changerBinding.nextLaySave.saveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changerBinding.nextLaySave.backgroundImageS.setVisibility(View.GONE);
                showSaveDialog();
            }
        });

        multiTouchListener2m = new MultiTouchListener2(changerBinding.userImageBC, BackgroundChanger.this);
        changerBinding.userImageBC.setOnTouchListener(multiTouchListener2m);
        multiTouchListener2m = new MultiTouchListener2(changerBinding.nextLaySave.userImageBCSave, BackgroundChanger.this);
        changerBinding.nextLaySave.userImageBCSave.setOnTouchListener(multiTouchListener2m);
        changerBinding.nextLaySave.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BackgroundChanger.this, FeedBack.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        setupViewPager(changerBinding.nextLaySave.viewPagers);
        changerBinding.nextLaySave.tabLayouts.setupWithViewPager(changerBinding.nextLaySave.viewPagers);
        commonMethods.loadBannerAd(changerBinding.nextLaySave.bannerEraser, BackgroundChanger.this);
        changerBinding.nextLaySave.removeBg.setVisibility(View.GONE);
        changerBinding.nextLaySave.cartoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BackgroundChanger.this, CartoonSelfi.class).putExtra("TITLE", getString(R.string.cartton)).putExtra("PRO_TAG", getString(R.string.pro_cartoon)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        changerBinding.nextLaySave.erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BackgroundChanger.this, RemoveObject.class) );
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    private void setupViewPager(ViewPager viewPagers) {


    }

    private void showSaveDialog() {
        saveDialog = new Dialog(BackgroundChanger.this);
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

                new saveAndGoimag().execute(new Void[0]);
            }
        });

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
            changerBinding.userImageBC.setVisibility(View.VISIBLE);
            changerBinding.nextLaySave.userImageBCSave.setVisibility(View.VISIBLE);
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


            if (BASEURL != null) {

                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(1, TimeUnit.MINUTES)
                            .readTimeout(1, TimeUnit.MINUTES)
                            .build();

                    if (mainActivity != null && mainActivity.universalRealBitmap != null) {
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
                    String completeUrl = BASEURL;
                    Request request = new Request.Builder()
                            .url(completeUrl)
                            .addHeader("Content-Type", "application/json")
                            .post(requestBody)
                            .build();

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
                                        changerBinding.userImageBC.setVisibility(View.GONE);
                                        changerBinding.nextLaySave.userImageBCSave.setVisibility(View.GONE);
                                        noServerFound();
                                    }
                                });
                            } else {
                                byte[] decodedBytes = Base64.decode(bas, Base64.DEFAULT);
                                apiCartoonBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        changerBinding.userImageBC.setImageBitmap(apiCartoonBitmap);
                                        changerBinding.nextLaySave.userImageBCSave.setImageBitmap(apiCartoonBitmap);
                                    }
                                });
                            }
                        } else {
                            //  '//noServerFound()' dialog on the main (UI) thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //placeHolderImageView.setVisibility(View.GONE);
                                    //errorImageView.setVisibility(View.VISIBLE);
                                    changerBinding.userImageBC.setVisibility(View.GONE);
                                    changerBinding.nextLaySave.userImageBCSave.setVisibility(View.GONE);
                                    noServerFound();
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //  noServerFound()' dialog on the main (UI) thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //placeHolderImageView.setVisibility(View.GONE);
                            //errorImageView.setVisibility(View.VISIBLE);
                            changerBinding.userImageBC.setVisibility(View.GONE);
                            changerBinding.nextLaySave.userImageBCSave.setVisibility(View.GONE);
                            noServerFound();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    // ' noServerFound()' dialog on the main (UI) thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //placeHolderImageView.setVisibility(View.GONE);
                            //errorImageView.setVisibility(View.VISIBLE);
                            changerBinding.userImageBC.setVisibility(View.GONE);
                            changerBinding.nextLaySave.userImageBCSave.setVisibility(View.GONE);

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
                        changerBinding.userImageBC.setVisibility(View.GONE);
                        changerBinding.nextLaySave.userImageBCSave.setVisibility(View.GONE);
                        noServerFound();
                    }
                });
            }
            return apiCartoonBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (!isFinishing()) {
                System.out.println(getStatus());
                if (bitmap != null) {
                    changerBinding.userImageBC.setImageBitmap(bitmap);
                    changerBinding.nextLaySave.userImageBCSave.setImageBitmap(bitmap);
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

    private void loadingDialog() {
        loadingDialog = new Dialog(BackgroundChanger.this);
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

    public Bitmap savePhotoFrame() {
        changerBinding.nextLaySave.mainLayoutSave.setDrawingCacheEnabled(true);
        changerBinding.nextLaySave.mainLayoutSave.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(changerBinding.nextLaySave.mainLayoutSave.getDrawingCache());
        changerBinding.nextLaySave.mainLayoutSave.setDrawingCacheEnabled(false);
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


    private void goToHome() {
        Intent intent = new Intent(BackgroundChanger.this, MainActivity.class);
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
            this.changerBinding.nextLaySave.userImageBCSave.setDrawingCacheEnabled(false);
        } catch (Exception unused) {
        }
        LASTSAVEIMAGE = str;
        return str;
    }

    public class saveAndGoimag extends AsyncTask<Void, Void, String> {
        @Override
        public String doInBackground(Void... voidArr) {
            BackgroundChanger bchanger = BackgroundChanger.this;
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

            if (BackgroundChanger.this.savePath.equals("")) {
                Toast.makeText(BackgroundChanger.this, "Couldn't save photo, error", 0).show();
            } else {
                openSaveActivity();
            }
        }

    }

    private void openSaveActivity() {
        Intent intent = new Intent(this, SaveScreen.class);
        intent.putExtra("savedImage", savePath);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

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
}