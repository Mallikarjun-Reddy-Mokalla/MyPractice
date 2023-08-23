package com.aapthitech.android.developers;


import static com.aapthitech.android.developers.Activities.MainActivity.finalFlag;
import static com.aapthitech.android.developers.Activities.MainActivity.mainActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aapthitech.android.developers.Activities.AIEditor;
import com.aapthitech.android.developers.Activities.CartoonSelfi;
import com.aapthitech.android.developers.Activities.RemoveObject;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class CropActivity extends AppCompatActivity {

    String result, type,selectedCard;
    Uri fileUri;
    public Uri resutUri;
    public String fromHomeDemoPerson;
    public static CropActivity crop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Window window = CropActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(CropActivity.this, R.color.white));
        readIntent();


        String dest_uri = new StringBuffer(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop.Options options = new UCrop.Options();
        options.setToolbarWidgetColor(getResources().getColor(R.color.white));
        options.setToolbarColor(getResources().getColor(R.color.black));

        UCrop.of(fileUri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                .withOptions(options)
                .withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .start(CropActivity.this);
    }

    private void readIntent() {
        Intent intent = getIntent();
        result = intent.getStringExtra("IMG_DATA");
        selectedCard = intent.getStringExtra("selectedCard");
        type = intent.getStringExtra("PICTURE");
        fileUri = Uri.parse(result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            resutUri = UCrop.getOutput(data);
            Intent retunIntent = new Intent();
            retunIntent.putExtra("RESULT", resutUri + "");
            setResult(RESULT_OK, retunIntent);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
                mainActivity.galleryFinalBitmap = bitmap;
                mainActivity.universalRealBitmap = bitmap;
                mainActivity.globalBitmap = bitmap;

            } catch (IOException e) {
                e.printStackTrace();
                e.getMessage();
            }

            openNewActivity(resutUri, selectedCard);

            if (mainActivity.modelFlag == 1) {
                if (finalFlag == 1) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
                        mainActivity.cameraBitmap = bitmap;
                        mainActivity.universalRealBitmap = bitmap;
                        mainActivity.globalBitmap = bitmap;
                    } catch (IOException e) {
                        e.printStackTrace();
                        e.getMessage();
                    }
                    openNewActivity(resutUri, selectedCard);
                  /*  Intent intent2 = new Intent(CropActivity.this, AIEditor.class);
                    retunIntent.putExtra("ROHIT", resutUri + "");
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent2);*/
                    //finish();
                } else if (finalFlag == 2) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
                        mainActivity.galleryFinalBitmap = bitmap;
                        mainActivity.universalRealBitmap = bitmap;
                        mainActivity.globalBitmap = bitmap;

                    } catch (IOException e) {
                        e.printStackTrace();
                        e.getMessage();
                    }
                  /*  Intent intent2 = new Intent(CropActivity.this, AIEditor.class);
                    retunIntent.putExtra("ROHIT", resutUri + "");
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent2);*/
                    //finish();
                    openNewActivity(resutUri, selectedCard);

                } else {
                    Intent intent = getIntent();
                    String fromSplash = intent.getStringExtra("FROMHOME");
                    if (intent != null) {
                        if (fromSplash != null) {
                            if (fromSplash.equals("DEMOPIC")) {
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
//                                    mainActivity.demoCropBitmap = bitmap;
                                    mainActivity.universalRealBitmap = bitmap;
                                    mainActivity.globalBitmap = bitmap;

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                             /*   Intent intent2 = new Intent(CropActivity.this, AIEditor.class);
                                retunIntent.putExtra("ROHIT", resutUri + "");
                                retunIntent.putExtra("DEMOIMAGE", "FROMCROPACTIVITY");
                                retunIntent.putExtra("CALLBACKPERSON", fromHomeDemoPerson);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                startActivity(intent2);*/
                                //finish();
                                openNewActivity(resutUri, selectedCard);

                            }
                        }
                    }
                }
            } else if (mainActivity.modelFlag == 2) {
                if (finalFlag == 1) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
                        mainActivity.cameraFinalBitamp = bitmap;
                        mainActivity.universalRealBitmap = bitmap;
                        mainActivity.globalBitmap = bitmap;

                    } catch (IOException e) {
                        e.printStackTrace();
                        e.getMessage();
                    }
                  /*  Intent intent2 = new Intent(CropActivity.this, RemoveObject.class);
                    retunIntent.putExtra("ROHIT", resutUri + "");
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent2);*/
                    //finish();
                } else if (finalFlag == 2) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
                        mainActivity.galleryFinalBitmap = bitmap;
                        mainActivity.universalRealBitmap = bitmap;
                        mainActivity.globalBitmap = bitmap;

                    } catch (IOException e) {
                        e.printStackTrace();
                        e.getMessage();
                    }
                 /*   Intent intent2 = new Intent(CropActivity.this, AIEditor.class);
                    retunIntent.putExtra("ROHIT", resutUri + "");
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent2);*/
                    //finish();
                }
            } else if (mainActivity.modelFlag == 3) {
                if (finalFlag == 1) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
                        mainActivity.cameraFinalBitamp = bitmap;
                        mainActivity.universalRealBitmap = bitmap;
                        mainActivity.globalBitmap = bitmap;

                    } catch (IOException e) {
                        e.printStackTrace();
                        e.getMessage();
                    }
                  /*  Intent intent2 = new Intent(CropActivity.this, AIEditor.class);
                    retunIntent.putExtra("ROHIT", resutUri + "");
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent2);*/
                    //finish();
                } else if (finalFlag == 2) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
                        mainActivity.galleryFinalBitmap = bitmap;
                        mainActivity.universalRealBitmap = bitmap;
                        mainActivity.globalBitmap = bitmap;

                    } catch (IOException e) {
                        e.printStackTrace();
                        e.getMessage();
                    }
                   /* Intent intent2 = new Intent(CropActivity.this, AIEditor.class);
                    retunIntent.putExtra("ROHIT", resutUri + "");
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent2);*/
                    //finish();
                }
            } else if (mainActivity.modelFlag == 4) {
                if (finalFlag == 1) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
                        mainActivity.cameraFinalBitamp = bitmap;
                        mainActivity.universalRealBitmap = bitmap;
                        mainActivity.globalBitmap = bitmap;

                    } catch (IOException e) {
                        e.printStackTrace();
                        e.getMessage();
                    }
                    /*Intent intent2 = new Intent(CropActivity.this, AIEditor.class);
                    retunIntent.putExtra("ROHIT", resutUri + "");
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent2);*/
                    //finish();
                } else if (finalFlag == 2) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
                        mainActivity.galleryFinalBitmap = bitmap;
                        mainActivity.universalRealBitmap = bitmap;
                        mainActivity.globalBitmap = bitmap;

                    } catch (IOException e) {
                        e.printStackTrace();
                        e.getMessage();
                    }
                  /*  Intent intent2 = new Intent(CropActivity.this, AIEditor.class);
                    retunIntent.putExtra("ROHIT", resutUri + "");
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent2);*/
                    //finish();
                }
            } else if (mainActivity.modelFlag == 5) {
                if (finalFlag == 1) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
                        mainActivity.cameraFinalBitamp = bitmap;
                        mainActivity.universalRealBitmap = bitmap;
                        mainActivity.globalBitmap = bitmap;

                    } catch (IOException e) {
                        e.printStackTrace();
                        e.getMessage();
                    }
                   /* Intent intent2 = new Intent(CropActivity.this, AIEditor.class);
                    retunIntent.putExtra("ROHIT", resutUri + "");
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent2);*/
                    //finish();
                } else if (finalFlag == 2) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
                        mainActivity.galleryFinalBitmap = bitmap;
                        mainActivity.universalRealBitmap = bitmap;
                        mainActivity.globalBitmap = bitmap;

                    } catch (IOException e) {
                        e.printStackTrace();
                        e.getMessage();
                    }
                   /* Intent intent2 = new Intent(CropActivity.this, AIEditor.class);
                    retunIntent.putExtra("ROHIT", resutUri + "");
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent2);*/
                    //finish();
                } else {
                    Intent intent = getIntent();
                    String fromSplash = intent.getStringExtra("FROMHOME");
                    if (intent != null) {
                        if (fromSplash != null) {
                            if (fromSplash.equals("DEMOPIC")) {
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resutUri);
//                                    mainActivity.demoCropBitmap = bitmap;
                                    mainActivity.universalRealBitmap = bitmap;
                                    mainActivity.globalBitmap = bitmap;

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                          /*      Intent intent2 = new Intent(CropActivity.this, AIEditor.class);
                                retunIntent.putExtra("ROHIT", resutUri + "");
                                retunIntent.putExtra("DEMOIMAGE", "FROMCROPACTIVITY");
                                retunIntent.putExtra("CALLBACKPERSON", fromHomeDemoPerson);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                startActivity(intent2);*/
                                //finish();
                            }
                        }
                    }
                }
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable crpoError = UCrop.getError(data);
            Toast.makeText(this, "Please upload the valid image", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "" + crpoError, Toast.LENGTH_SHORT).show();

        }
        finish();
    }

    private void openNewActivity(Uri uri, String selectedCard) {
        switch (selectedCard) {
            case "removeObj":
                startActivity(new Intent(CropActivity.this, RemoveObject.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case "aiEnhance":
                startActivity(new Intent(CropActivity.this, AIEditor.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.enhance_title)).putExtra("PRO_TAG", getString(R.string.pro_enhance)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
            case "removeBG":
                startActivity(new Intent(CropActivity.this, BackgroundChanger.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.removebg)).putExtra("PRO_TAG", getString(R.string.remove_pro_bg)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
            case "dehaze":
                startActivity(new Intent(CropActivity.this, AIEditor.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.dehaze)).putExtra("PRO_TAG", getString(R.string.pro_dehaze)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
            case "descratch":

                startActivity(new Intent(CropActivity.this, AIEditor.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.descratch)).putExtra("PRO_TAG", getString(R.string.pro_descratch)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
            case "cartoonSelfi":
                startActivity(new Intent(CropActivity.this, CartoonSelfi.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.cartton)).putExtra("PRO_TAG", getString(R.string.pro_cartoon)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
            case "lensBlur":
                startActivity(new Intent(CropActivity.this, AIEditor.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.lens_blur)).putExtra("PRO_TAG", getString(R.string.pro_lens)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case "colorize":
                startActivity(new Intent(CropActivity.this, AIEditor.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.colorize)).putExtra("PRO_TAG", getString(R.string.pro_colorize)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case "brighten":
                startActivity(new Intent(CropActivity.this, AIEditor.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.brighten)).putExtra("PRO_TAG", getString(R.string.pro_brighten)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }


    }

}