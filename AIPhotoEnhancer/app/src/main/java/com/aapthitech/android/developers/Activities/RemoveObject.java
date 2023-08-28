package com.aapthitech.android.developers.Activities;

import static com.aapthitech.android.developers.Activities.MainActivity.mainActivity;

import static java.lang.System.out;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aapthitech.android.developers.BackgroundChanger;
import com.aapthitech.android.developers.Data.RemoteConfig;
import com.aapthitech.android.developers.Editscreen;
import com.aapthitech.android.developers.SaveScreen;
import com.aapthitech.android.developers.View.HoverView2;
import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.ActivityRemoveObjectBinding;
import com.aapthitech.android.developers.databinding.LoadingBinding;
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

public class RemoveObject extends AppCompatActivity {
    ActivityRemoveObjectBinding removeObjectBinding;
    public HoverView2 mHoverView;
    double mDensity;
    int viewWidth;
    int viewHeight;
    int bmWidth;
    int bmHeight;
    int actionBarHeight;
    int bottombarHeight;
    double bmRatio;
    double viewRatio;
    String pictureType;
    Dialog loadingDialog;
    Dialog serverNotFoundDialog;
    ServerNotFoundBinding serverNotFoundBinding;
    LoadingBinding loadingBinding;
    private String BASEURL = "";
    private String SUB_URL_NAME = "";


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

    public static String LASTSAVEIMAGE;

    public Bitmap sampleFilterBitmap;

    int sampleFilterResource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        removeObjectBinding = ActivityRemoveObjectBinding.inflate(getLayoutInflater());
        View view = removeObjectBinding.getRoot();
        setContentView(view);
        pictureType = getIntent().getStringExtra("PICTURE");


        removeObjectBinding.magicIconLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeObjectBinding.magicToolsLayout.setVisibility(View.VISIBLE);
                removeObjectBinding.toolLay.setVisibility(View.GONE);
                removeObjectBinding.seeKLay.setVisibility(View.GONE);
                removeObjectBinding.saveMagicLayout.setVisibility(View.GONE);
            }
        });
        removeObjectBinding.magicTabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeObjectBinding.magicToolsLayout.setVisibility(View.GONE);
                removeObjectBinding.toolLay.setVisibility(View.VISIBLE);
                removeObjectBinding.seeKLay.setVisibility(View.VISIBLE);
                removeObjectBinding.saveMagicLayout.setVisibility(View.VISIBLE);
            }
        });
        removeObjectBinding.checkedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeObjectBinding.saveMagicLayout.setVisibility(View.VISIBLE);
            }
        });
        removeObjectBinding.removeObjBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /* setting the bitmap to the view */
        sampleFilterResource = R.drawable.d_remove_obj_sel;
        sampleFilterBitmap = BitmapFactory.decodeResource(getResources(), sampleFilterResource);
        mainActivity.globalBitmap = sampleFilterBitmap;
        drawtheObjectToRemove(mainActivity.globalBitmap, RemoveObject.this);


        removeObjectBinding.seekbarBrushSize.incrementProgressBy(20);
        removeObjectBinding.seekbarBrushSize.setMax(200);
        removeObjectBinding.seekbarBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar != null) {
                    if (mHoverView != null) {
                        mHoverView.setEraseBrushSize(seekBar.getProgress());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
        removeObjectBinding.magicErase.setVisibility(View.GONE);//
        removeObjectBinding.magicCartoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RemoveObject.this, CartoonSelfi.class)
                        .putExtra("TITLE", getString(R.string.cartton)).putExtra("PRO_TAG", getString(R.string.pro_cartoon)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        removeObjectBinding.magicRemoveBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RemoveObject.this, BackgroundChanger.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.removebg)).putExtra("PRO_TAG", getString(R.string.remove_pro_bg)));

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        removeObjectBinding.magicEnhance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RemoveObject.this, AIEditor.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.enhance_title)).putExtra("PRO_TAG", getString(R.string.pro_enhance)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        removeObjectBinding.magicLensBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RemoveObject.this, Editscreen.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.lens_blur)).putExtra("PRO_TAG", getString(R.string.pro_lens)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        removeObjectBinding.magicColorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RemoveObject.this, Editscreen.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.colorize)).putExtra("PRO_TAG", getString(R.string.pro_colorize)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        removeObjectBinding.magicBrighten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RemoveObject.this, Editscreen.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.brighten)).putExtra("PRO_TAG", getString(R.string.pro_brighten)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        removeObjectBinding.magicDehaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RemoveObject.this, Editscreen.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.dehaze)).putExtra("PRO_TAG", getString(R.string.pro_dehaze)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        removeObjectBinding.magicDescratch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RemoveObject.this, Editscreen.class).putExtra("PICTURE", pictureType).putExtra("TITLE", getString(R.string.descratch)).putExtra("PRO_TAG", getString(R.string.pro_descratch)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        removeObjectBinding.magicIconLay.setVisibility(View.GONE);
        /*removeObjectBinding.seekbarBrushSize.incrementProgressBy(20);
        removeObjectBinding.seekbarBrushSize.setMax(200);
        removeObjectBinding.seekbarBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar != null) {
                    if (mHoverView != null) {
                        mHoverView.setPointerOffset(seekBar.getProgress());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

        });*/
        removeObjectBinding.saveRemoveObj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new saveAndGoimag().execute(new Void[0]);
            }
        });
    }

    public class saveAndGoimag extends AsyncTask<Void, Void, String> {
        @Override
        public String doInBackground(Void... voidArr) {

            RemoveObject removeObject = RemoveObject.this;
            removeObject.savePath = removeObject.savePhoto();
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

            if (RemoveObject.this.savePath.equals("")) {
                Toast.makeText(RemoveObject.this, "Couldn't save photo, error", 0).show();
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

    public Bitmap savePhotoFrame() {
        removeObjectBinding.removeObjLayout.setDrawingCacheEnabled(true);
        removeObjectBinding.removeObjLayout.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(removeObjectBinding.removeObjLayout.getDrawingCache());
        removeObjectBinding.removeObjLayout.setDrawingCacheEnabled(false);
        mainActivity.globalBitmap = bitmap;
        if (pictureType != null) {
            if (pictureType.equals("DemoImages")) {
                sampleFilterResource = R.drawable.d_remove_obj_filter;
                sampleFilterBitmap = BitmapFactory.decodeResource(getResources(), sampleFilterResource);
                mainActivity.globalBitmap = sampleFilterBitmap;
            }
        }

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
            this.removeObjectBinding.removeObjLayout.setDrawingCacheEnabled(false);
        } catch (Exception unused) {
        }
        LASTSAVEIMAGE = str;
        return str;
    }

    public void drawtheObjectToRemove(Bitmap mBitmap, Context context) {

        mDensity = getResources().getDisplayMetrics().density;
        actionBarHeight = (int) (110 * mDensity);
        bottombarHeight = (int) (60 * mDensity);
        viewWidth = getResources().getDisplayMetrics().widthPixels;
        viewHeight = getResources().getDisplayMetrics().heightPixels - actionBarHeight - bottombarHeight;
        viewRatio = (double) viewHeight / (double) viewWidth;
        if (mBitmap != null) {
            bmRatio = (double) mBitmap.getHeight() / (double) mBitmap.getWidth();
            if (bmRatio < viewRatio) {
                bmWidth = viewWidth;
                bmHeight = (int) (((double) viewWidth) * ((double) (mBitmap.getHeight()) / (double) (mBitmap.getWidth())));
            } else {
                bmHeight = viewHeight;
                bmWidth = (int) (((double) viewHeight) * ((double) (mBitmap.getWidth()) / (double) (mBitmap.getHeight())));
            }
            mBitmap = Bitmap.createScaledBitmap(mBitmap, bmWidth, bmHeight, false);
            mHoverView = new HoverView2(this, mBitmap, bmWidth, bmHeight, viewWidth, viewHeight);
            mHoverView.setLayoutParams(new ViewGroup.LayoutParams(viewWidth, viewHeight));
            removeObjectBinding.removeObjLayout.addView(mHoverView);
            Bitmap mutableBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

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
//             removeObjectBinding.userImageEditor.setVisibility(View.VISIBLE);
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
                                        removeObjectBinding.rUserImage.setVisibility(View.GONE);
                                        noServerFound();
                                    }
                                });
                            } else {
                                byte[] decodedBytes = Base64.decode(bas, Base64.DEFAULT);
                                apiCartoonBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeObjectBinding.rUserImage.setImageBitmap(apiCartoonBitmap);
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
                                    removeObjectBinding.rUserImage.setVisibility(View.GONE);
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
                            removeObjectBinding.rUserImage.setVisibility(View.GONE);
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
                            removeObjectBinding.rUserImage.setVisibility(View.GONE);
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
                        removeObjectBinding.rUserImage.setVisibility(View.GONE);
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
                    removeObjectBinding.rUserImage.setImageBitmap(bitmap);
                    mainActivity.globalBitmap = bitmap;

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
        loadingDialog = new Dialog(RemoveObject.this);
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
        Intent intent = new Intent(RemoveObject.this, MainActivity.class);
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

}