package com.aapthitech.android.developers.Activities;

import static android.content.ContentValues.TAG;

import static com.aapthitech.android.developers.Data.CommonMethods.commonMethods;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aapthitech.android.developers.BackgroundChanger;
import com.aapthitech.android.developers.Creations;
import com.aapthitech.android.developers.CropActivity;
import com.aapthitech.android.developers.Data.ActionData;
import com.aapthitech.android.developers.Data.RemoteConfig;
import com.aapthitech.android.developers.Editscreen;
import com.aapthitech.android.developers.IAP.PremiumScreen;
import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.Utils.PhotoData;
import com.aapthitech.android.developers.databinding.ActivityMainBinding;
import com.aapthitech.android.developers.databinding.DemoImagesBinding;
import com.aapthitech.android.developers.databinding.ExitSheetDialogBinding;
import com.aapthitech.android.developers.databinding.GalleryCamDialogBinding;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMainBinding mainBinding;
    private Dialog exitDialog;
    ExitSheetDialogBinding exitSheetDialogBinding;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    private static final int STORAGE_PERMISSION_CREATIONS = 3;
    private String optedAction;
    private static final int IMAGE_PICK_CODE = 505;
    PhotoData photoData;
    private static final int CAMERA_REQUEST = 108;
    Dialog galCamDialog;
    GalleryCamDialogBinding bindingCamGal;
    public Bitmap galleryBitmap;
    public Bitmap galleryFinalBitmap;
    public Bitmap cameraBitmap;
    public Bitmap cameraFinalBitamp;
    public static MainActivity mainActivity;
    public Bitmap globalBitmap;
    public Bitmap universalRealBitmap;
    public static int cameraFlag;
    public static int finalFlag;
    public int modelFlag;
    public static ActivityResultLauncher<String> mGetContent;
    Bitmap bitmap1;
    Bitmap bitmap2;
    Bitmap bitmap3;
    public static boolean selecteddummy = false;
    public static boolean dummy1 = false;
    public static boolean dummy2 = false;
    public static boolean dummy3 = false;
    public int imageResource;
    public int imageResource2;
    public int imageResource3;
    String optType;
    private DemoImagesBinding demoImagesBinding;
    Dialog demoImagesDialog;
    Map<String, ActionData> actionMapping = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);
        photoData = new PhotoData(this);
        mainActivity = this;
        Glide.with(this).load(R.drawable.ai_enhancer).into(mainBinding.gifView);// to load the gif file
        showInterstitialAdOnLaunch();

        /* set card to view to perform the click event */
        mainBinding.removeObj.setParentCardView(mainBinding.removeObjCard);
        mainBinding.removeBg.setParentCardView(mainBinding.removeBgCard);
        mainBinding.dehaze.setParentCardView(mainBinding.dehazeCard);
        mainBinding.descratch.setParentCardView(mainBinding.descratchCard);
        mainBinding.cortoonSelfie.setParentCardView(mainBinding.cartoonSelfiCard);
        mainBinding.lensBlur.setParentCardView(mainBinding.lensBlurCard);
        mainBinding.cororizeView.setParentCardView(mainBinding.colorizeCard);
        mainBinding.brighter.setParentCardView(mainBinding.brighterCard);
        mainBinding.dehaze.setParentCardView(mainBinding.dehazeCard);
        /*on click events*/
        mainBinding.aiEnhanceLay.setOnClickListener(this);
        mainBinding.removeBgCard.setOnClickListener(this);
        mainBinding.dehazeCard.setOnClickListener(this);
        mainBinding.descratchCard.setOnClickListener(this);
        mainBinding.cartoonSelfiCard.setOnClickListener(this);
        mainBinding.lensBlurCard.setOnClickListener(this);
        mainBinding.colorizeCard.setOnClickListener(this);
        mainBinding.brighterCard.setOnClickListener(this);
        mainBinding.removeObjCard.setOnClickListener(this);
        if (RemoteConfig.getRemoteConfig().getEnableIAPflag() != null) {
            if (RemoteConfig.getRemoteConfig().getEnableIAPflag().equals("true")) {
                mainBinding.proLayout.setVisibility(View.VISIBLE);

            } else {
                mainBinding.proLayout.setVisibility(View.GONE);

            }
        }
        mainBinding.proLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PremiumScreen.class).putExtra("PRO_FROM","MAIN"));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
        mainBinding.mainSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsAI.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    Intent intent = new Intent(MainActivity.this, CropActivity.class);
                    intent.putExtra("IMG_DATA", result.toString());
                    startActivityForResult(intent, 101);

                }
            }
        });

        mainBinding.historyCreations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Creations.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });


    }

    private void showInterstitialAdOnLaunch() {
        if (RemoteConfig.getRemoteConfig() != null) {
            if (RemoteConfig.getRemoteConfig().getShowInterstitial() != null) {
                if (RemoteConfig.getRemoteConfig().getShowInterstitial().equals("true")) {
                    if (RemoteConfig.getRemoteConfig().getshowInterstitialOnLaunch() != null) {
                        if (RemoteConfig.getRemoteConfig().getshowInterstitialOnLaunch().equals("true")) {
                            commonMethods.showGoogleAd((Activity) MainActivity.this, MainActivity.this);
                        }
                    }
                }
            }
        }

    }

    public static MainActivity getInstance() {
        return mainActivity;
    }

    private void pickImageFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");
        if (pickIntent.resolveActivity(getPackageManager()) != null) {
            try {
                startActivityForResult(pickIntent, IMAGE_PICK_CODE);
            } catch (ActivityNotFoundException e) {
                Log.e("TAG", "Not Found.");
            }
        }
    }

    public void launchCamera() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            File photo = null;
            try {
                photo = createImageFiles();
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", photo);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                photoData.saveUriPath(Uri.fromFile(photo).getPath());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Start the image capture intent to take photo
                    startActivityForResult(intent, CAMERA_REQUEST);
                }

            } catch (IOException e) {
                //TODO warn the user the photo fail
            }
        }


    }

    private File createImageFiles() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        // currentPhotoPaths = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted");
            } else {
                Log.d(TAG, "Permission denied");
            }
        }

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
                galCamDialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
                galCamDialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == STORAGE_PERMISSION_CREATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
                galCamDialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1 && requestCode == CAMERA_REQUEST) {

            Uri uri = Uri.fromFile(new File(photoData.getUriPath()));
            cameraBitmap = null;
            if (uri != null) {
                try {
                    cameraBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    globalBitmap = cameraBitmap;
                    openNewActivity(uri, "CAMERA");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == IMAGE_PICK_CODE && resultCode == -1 && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    galleryBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    globalBitmap = galleryBitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (uri != null) {
                openNewActivity(uri, "GALLERY");
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Please Select image", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        openexitDialog();
    }

    private void openexitDialog() {
        exitDialog = new Dialog(MainActivity.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitSheetDialogBinding = ExitSheetDialogBinding.inflate(getLayoutInflater());
        exitDialog.setContentView(exitSheetDialogBinding.getRoot());

        /*  Apply margin to the CardView using layout parameters*/
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) exitSheetDialogBinding.exitSheet.getLayoutParams();
        int marginInPixels = (int) getResources().getDimension(R.dimen.dialog_margin);/* Replace with your actual dimension resource*/
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
        exitSheetDialogBinding.exitSheet.setLayoutParams(layoutParams);
        exitDialog.setCancelable(false);
        exitDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        exitDialog.getWindow().setGravity(Gravity.CENTER);
        if (exitDialog != null) {
            exitDialog.show();
        }
        exitSheetDialogBinding.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exitDialog != null) {
                    if (exitDialog.isShowing()) {
                        exitDialog.dismiss();
                        finish();

                    } else {
                        finish();
                    }
                }
            }
        });
        exitSheetDialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exitDialog != null) {
                    if (exitDialog.isShowing()) {
                        exitDialog.dismiss();
                    }
                }
            }
        });
        if (exitDialog.isShowing()) {
            commonMethods.loadNextNativeAdFlor(MainActivity.this, exitSheetDialogBinding.nativeOnExit);
        }
    }

    public void demoImagesDialog() {
        demoImagesDialog = new Dialog(this);
        demoImagesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        demoImagesBinding = DemoImagesBinding.inflate(getLayoutInflater());
        demoImagesDialog.setContentView(bindingCamGal.getRoot());
        // Apply margin to the CardView using layout parameters
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) demoImagesBinding.demoCard.getLayoutParams();
        int marginInPixels = (int) getResources().getDimension(R.dimen.dialog_margin); // Replace with your actual dimension resource
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
        demoImagesBinding.demoCard.setLayoutParams(layoutParams);

        demoImagesDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        demoImagesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        demoImagesDialog.getWindow().setGravity(Gravity.CENTER);
        demoImagesDialog.show();
        demoImagesDialog.setCancelable(false);

    }

    public void openCameraGalleryDialog() {
        galCamDialog = new Dialog(this);
        galCamDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bindingCamGal = GalleryCamDialogBinding.inflate(getLayoutInflater());
        galCamDialog.setContentView(bindingCamGal.getRoot());
        // Apply margin to the CardView using layout parameters
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) bindingCamGal.cardView.getLayoutParams();
        int marginInPixels = (int) getResources().getDimension(R.dimen.dialog_margin); // Replace with your actual dimension resource
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
        bindingCamGal.cardView.setLayoutParams(layoutParams);

        galCamDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        galCamDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        galCamDialog.getWindow().setGravity(Gravity.CENTER);
        galCamDialog.show();
        galCamDialog.setCancelable(false);
        setDummyImage(bindingCamGal.camGalLay);
        actionMapping.put("removeObj", new ActionData("removeObj", R.drawable.d_remove_obj));
        actionMapping.put("aiEnhance", new ActionData("aiEnhance", R.drawable.d_enhance));
        actionMapping.put("removeBG", new ActionData("removeBG", R.drawable.d_remove_bg));
        actionMapping.put("dehaze", new ActionData("dehaze", R.drawable.d_dehaze));
        actionMapping.put("descratch", new ActionData("descratch", R.drawable.d_descratch));
        actionMapping.put("cartoonSelfi", new ActionData("cartoonSelfi", R.drawable.d_cartoon_selfie));
        actionMapping.put("lensBlur", new ActionData("lensBlur", R.drawable.d_lens_blur));
        actionMapping.put("colorize", new ActionData("colorize", R.drawable.d_colorize));
        actionMapping.put("brighten", new ActionData("brighten", R.drawable.d_brighten));
        ActionData selectedAction = actionMapping.get(optedAction);
        if (selectedAction != null) {
            optType = selectedAction.optType;
            int imageResource = selectedAction.imageResource1;
            // Load bitmaps using imageResource values
            bitmap1 = BitmapFactory.decodeResource(getResources(), imageResource);
            bindingCamGal.demoImage.setImageBitmap(bitmap1);
            // Replace the above lines with actual bitmap loading logic
            System.out.println("Opted Type: " + optType);
            System.out.println("Image Resource 1: " + imageResource);
            System.out.println("Image Resource 2: " + imageResource2);
            System.out.println("Image Resource 3: " + imageResource3);
        }


        bindingCamGal.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    launchCamera();
                    galCamDialog.dismiss();
                }

            }
        });
        bindingCamGal.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*check the media permission before opening the gallery & allowing permission */
                if (android.os.Build.VERSION.SDK_INT >= 32) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_STORAGE_PERMISSION);
                    } else {
                        /* to open gallery*/
                        pickImageFromGallery();
                        galCamDialog.dismiss();
                    }
                } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                } else {
                    /* to open gallery*/
                    pickImageFromGallery();
                    galCamDialog.dismiss();
                }
            }
        });
        bindingCamGal.closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galCamDialog != null && galCamDialog.isShowing()) {
                    galCamDialog.dismiss();
                }
            }
        });


        bindingCamGal.demoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = convertBitmapToUri(getApplicationContext(), bitmap1);
                mainActivity.galleryFinalBitmap = bitmap1;
                mainActivity.universalRealBitmap = bitmap1;
                mainActivity.globalBitmap = bitmap1;

                if (galCamDialog != null && galCamDialog.isShowing()) {
                    galCamDialog.dismiss();
                }
                openNewActivityforDemo(uri, "DemoImages", optType);
            }
        });

    }

    private void setDummyImage(LinearLayout camGalLay) {
        if (optedAction != null) {
            if (optedAction.equals("removeObj")) {
                camGalLay.setVisibility(View.GONE);

            } /*else if (optedAction.equals("aiEnhance")) {


            } else if (optedAction.equals("removeBG")) {


            }*/ else if (optedAction.equals("dehaze")) {
                camGalLay.setVisibility(View.GONE);

            } else if (optedAction.equals("descratch")) {
                camGalLay.setVisibility(View.GONE);

            } /*else if (optedAction.equals("cartoonSelfi")) {
                camGalLay.setVisibility(View.GONE);


            }*/ else if (optedAction.equals("lensBlur")) {
                camGalLay.setVisibility(View.GONE);


            } else if (optedAction.equals("colorize")) {
                camGalLay.setVisibility(View.GONE);


            } else if (optedAction.equals("brighten")) {

                camGalLay.setVisibility(View.GONE);

            }
        }
        bitmap1 = BitmapFactory.decodeResource(getResources(), imageResource);

    }

    public Uri convertBitmapToUri(Context context, Bitmap bitmap) {
        File imagesDirectory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyAppImages");
        if (!imagesDirectory.exists()) {
            imagesDirectory.mkdirs();
        }

        String imageName = "image_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(imagesDirectory, imageName);

        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(imageFile);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remove_obj_card:
                optedAction = "removeObj";
                openCameraGalleryDialog();
                break;
            case R.id.ai_enhance_lay:
                optedAction = "aiEnhance";
                openCameraGalleryDialog();
                break;
            case R.id.remove_bg_card:
                optedAction = "removeBG";
                openCameraGalleryDialog();

                break;
            case R.id.dehaze_card:
                optedAction = "dehaze";
                openCameraGalleryDialog();
                break;
            case R.id.descratch_card:
                optedAction = "descratch";
                openCameraGalleryDialog();
                break;
            case R.id.cartoon_selfi_card:
                optedAction = "cartoonSelfi";
                openCameraGalleryDialog();
                break;
            case R.id.lens_blur_card:
                optedAction = "lensBlur";
                openCameraGalleryDialog();
                break;
            case R.id.colorize_card:
                optedAction = "colorize";
                openCameraGalleryDialog();
                break;
            case R.id.brighter_card:
                optedAction = "brighten";
                openCameraGalleryDialog();
                break;

        }
    }

    public void openCameraGalleryDialog1(String image, String image2, String image3) {
        imageResource = R.drawable.remove_before_after;
        imageResource2 = R.drawable.remove_before_after;
        imageResource3 = R.drawable.remove_before_after;
    }



        private void openNewActivityforDemo(Uri uri, String type,String optedAction) {
            switch (optedAction) {
                case "removeObj":
                    startActivity(new Intent(MainActivity.this, RemoveObject.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case "aiEnhance":
                    startActivity(new Intent(MainActivity.this, AIEditor.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.enhance_title)).putExtra("PRO_TAG", getString(R.string.pro_enhance)));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    break;
                case "removeBG":
                    startActivity(new Intent(MainActivity.this, BackgroundChanger.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.removebg)).putExtra("PRO_TAG", getString(R.string.remove_pro_bg)));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    break;
                case "dehaze":
                    startActivity(new Intent(MainActivity.this, Editscreen.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.dehaze)).putExtra("PRO_TAG", getString(R.string.pro_dehaze)));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    break;
                case "descratch":
                    startActivity(new Intent(MainActivity.this, Editscreen.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.descratch)).putExtra("PRO_TAG", getString(R.string.pro_descratch)));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    break;
                case "cartoonSelfi":
                    startActivity(new Intent(MainActivity.this, CartoonSelfi.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.cartton)).putExtra("PRO_TAG", getString(R.string.pro_cartoon)));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    break;
                case "lensBlur":
                    startActivity(new Intent(MainActivity.this, Editscreen.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.lens_blur)).putExtra("PRO_TAG", getString(R.string.pro_lens)));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case "colorize":
                     startActivity(new Intent(MainActivity.this, Editscreen.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.colorize)).putExtra("PRO_TAG", getString(R.string.pro_colorize)));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case "brighten":
                    startActivity(new Intent(MainActivity.this, Editscreen.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.brighten)).putExtra("PRO_TAG", getString(R.string.pro_brighten)));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

            }


        }

    private void openNewActivity(Uri uri, String type) {
        switch (optedAction) {
            case "removeObj":
                startCropActivity(uri, type, "removeObj");
                break;
            case "aiEnhance":
                startCropActivity(uri, type, "aiEnhance");
                break;
            case "removeBG":
                startCropActivity(uri, type, "removeBG");
                break;
            case "dehaze":
                startCropActivity(uri, type, "dehaze");
                break;
            case "descratch":
                startCropActivity(uri, type, "descratch");
                break;
            case "cartoonSelfi":
                startCropActivity(uri, type, "cartoonSelfi");
                break;
            case "lensBlur":
                startCropActivity(uri, type, "lensBlur");
                break;
            case "colorize":
                startCropActivity(uri, type, "colorize");
                break;
            case "brighten":
                startCropActivity(uri, type, "brighten");
                break;
        }


    }

    private void startCropActivity(Uri uri, String type) {
        startActivity(new Intent(MainActivity.this, CropActivity.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("TITLE", getString(R.string.brighten)).putExtra("PRO_TAG", getString(R.string.pro_brighten)));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void startCropActivity(Uri uri, String type, String selectedCard) {
        startActivity(new Intent(MainActivity.this, CropActivity.class).putExtra("IMG_DATA", uri.toString()).putExtra("PICTURE", type).putExtra("selectedCard", selectedCard).putExtra("TITLE", getString(R.string.brighten)).putExtra("PRO_TAG", getString(R.string.pro_brighten)));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}