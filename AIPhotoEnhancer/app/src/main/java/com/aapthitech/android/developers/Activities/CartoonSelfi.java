package com.aapthitech.android.developers.Activities;

import static com.aapthitech.android.developers.Activities.MainActivity.mainActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

 import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.ActivityCartoonSelfiBinding;
import com.aapthitech.android.developers.databinding.LoadingBinding;
import com.aapthitech.android.developers.databinding.SaveSheetDialogBinding;
import com.aapthitech.android.developers.databinding.ServerNotFoundBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
    private String BASEURL = "https://toonifime.com/toonifyme";
    private String SUB_URL_NAME = "";

    private LoadingBinding loadingBinding;

    private Bitmap apiCartoonBitmap = null;
    private Bitmap bitmap;
    Bitmap finalBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cartoonSelfiBinding = ActivityCartoonSelfiBinding.inflate(getLayoutInflater());
        View view = cartoonSelfiBinding.getRoot();
        setContentView(view);
        cartoonSelfiBinding.userImage.setImageBitmap(mainActivity.globalBitmap);

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
                cartoonSelfiBinding.cartoonsListLay.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.saveCartoonLayout.setVisibility(View.VISIBLE);
                cartoonSelfiBinding.magicSaveToolsLay.setVisibility(View.GONE);
                cartoonSelfiBinding.proAdLay.setVisibility(View.GONE);
                SUB_URL_NAME = "/caricature";

                new ApplyfilterToImageAsyncTask().execute();
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
    }

    public void savePhoto() {
        cartoonSelfiBinding.mainLayout.setDrawingCacheEnabled(true);
        cartoonSelfiBinding.mainLayout.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cartoonSelfiBinding.mainLayout.getDrawingCache());
        cartoonSelfiBinding.mainLayout.setDrawingCacheEnabled(false);
        mainActivity.globalBitmap = bitmap;
        Intent intent = new Intent(this, AIEditor.class);
        startActivity(intent);
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

}