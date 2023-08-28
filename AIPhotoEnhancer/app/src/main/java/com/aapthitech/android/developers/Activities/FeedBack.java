package com.aapthitech.android.developers.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aapthitech.android.developers.R;
import com.aapthitech.android.developers.databinding.ActivityFeedBackBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FeedBack extends AppCompatActivity {
    ActivityFeedBackBinding feedBackBinding;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    private static final int STORAGE_PERMISSION_CREATIONS = 3;
    private static final int IMAGE_PICK_CODE = 505;
    int reviewCount;
    String feedbackToMail;
    String lowQuality;
    String unRealistic = "Reealstic";
    String bugs = "No bugs";
    String premium = "Premium is offeredable";
    String otherissues;
    int lowclickCount = 1;
    int unRealclickCount = 1;
    int bugsclickCount = 1;
    int proclickCount = 1;
    int otherclickCount = 1;
    String reviewContent;
    private String intentCall;
    Bitmap issueBitmap, issueBitmap2;
    ArrayList<Uri> imageUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        feedBackBinding = ActivityFeedBackBinding.inflate(getLayoutInflater());
        View view = feedBackBinding.getRoot();
        setContentView(view);
        intentCall = getIntent().getStringExtra("INTENT_FROM");
        imageUris.clear();
        feedBackBinding.reviewStar1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 1;
                reviewContent = "Just giving 1 Star";
                feedBackBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
            }
        });
        feedBackBinding.reviewStar2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 2;
                reviewContent = "Rating 2 Star,need to improve.";
                feedBackBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
            }
        });
        feedBackBinding.reviewStar3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 3;
                reviewContent = "Rating 3 Star,good application.";
                feedBackBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star));
                feedBackBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));

            }
        });
        feedBackBinding.reviewStar4.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 4;
                reviewContent = "Rating 4 Star,App is useful.";

                feedBackBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star));
            }
        });
        feedBackBinding.reviewStar5.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                reviewCount = 5;
                reviewContent = "Rating 5 Star,App is very useful and I like it.";

                feedBackBinding.reviewStar1.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar2.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar3.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar4.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
                feedBackBinding.reviewStar5.setImageDrawable(getDrawable(R.drawable.ai_review_star_full));
            }
        });
        feedBackBinding.lowQualityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lowclickCount == 1) {
                    lowQuality = "The App is Low Quality";
                    feedBackBinding.lowQualityText.setBackground(getDrawable(R.drawable.magic_gradient));
                    lowclickCount++;
                } else {
                    lowclickCount--;
                    lowQuality = "";
                    feedBackBinding.lowQualityText.setBackground(getDrawable(R.drawable.feed_back_gradient));

                }
            }
        });
        feedBackBinding.unrelisticText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unRealclickCount == 1) {
                    unRealistic = "App do not seem realistic";
                    feedBackBinding.unrelisticText.setBackground(getDrawable(R.drawable.magic_gradient));
                    unRealclickCount++;
                } else {
                    unRealclickCount--;
                    unRealistic = "Realstic";
                    feedBackBinding.unrelisticText.setBackground(getDrawable(R.drawable.feed_back_gradient));

                }
            }
        });
        feedBackBinding.premiumText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (proclickCount == 1) {
                    premium = "Premium is too Cost";
                    feedBackBinding.premiumText.setBackground(getDrawable(R.drawable.magic_gradient));
                    proclickCount++;
                } else {
                    proclickCount--;
                    premium = "Premium is offeredable";
                    feedBackBinding.premiumText.setBackground(getDrawable(R.drawable.feed_back_gradient));

                }
            }
        });
        feedBackBinding.otherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otherclickCount == 1) {
                    otherissues = "Have issues";
                    feedBackBinding.otherText.setBackground(getDrawable(R.drawable.magic_gradient));
                    otherclickCount++;
                } else {
                    otherclickCount--;
                    otherissues = "No issues";
                    feedBackBinding.otherText.setBackground(getDrawable(R.drawable.feed_back_gradient));

                }
            }
        });
        feedBackBinding.bugsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bugsclickCount == 1) {
                    bugs = "App as Bugs";
                    feedBackBinding.bugsText.setBackground(getDrawable(R.drawable.magic_gradient));
                    bugsclickCount++;
                } else {
                    bugs = "No bugs";
                    bugsclickCount--;
                    feedBackBinding.bugsText.setBackground(getDrawable(R.drawable.feed_back_gradient));

                }
            }
        });
        feedBackBinding.submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitFeedBack();


            }
        });
        feedBackBinding.issueImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*check the media permission before opening the gallery & allowing permission */
                if (android.os.Build.VERSION.SDK_INT >= 32) {
                    if (ContextCompat.checkSelfPermission(FeedBack.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(FeedBack.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_STORAGE_PERMISSION);
                    } else {
                        /* to open gallery*/
                        pickImageFromGallery();

                    }
                } else if (ContextCompat.checkSelfPermission(FeedBack.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FeedBack.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                } else {
                    /* to open gallery*/
                    pickImageFromGallery();
                }
            }
        });

    }
    private void submitFeedBack() {
        if (!feedBackBinding.feedBackEditText.getText().toString().isEmpty()) {
            feedbackToMail = feedBackBinding.feedBackEditText.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("text/plain");
            intent.setPackage("com.google.android.gm");  // Gmail package name

            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mallikarjunreddy900@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "AI Enhancer Feedback");
            intent.putExtra(Intent.EXTRA_TEXT, "Dear AI Enhancer Team,\n\nI would like to provide the following feedback:\n\n" + reviewContent + "\n" + "\n\n" + lowQuality + "\n" + unRealistic + "\n" + bugs + "\n" + premium + "\n" + otherissues + "\n\n" + feedbackToMail);

            if (imageUris != null && imageUris.size() > 0) {
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            }

            startActivity(Intent.createChooser(intent, "Send Feedback"));
        } else {
            Toast.makeText(FeedBack.this, "Enter Feedback", Toast.LENGTH_SHORT).show();
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == IMAGE_PICK_CODE && resultCode == -1 && data != null) {
            Uri uri = data.getData();
            imageUris.add(uri);
            System.out.println("FeedBack : "+imageUris);
            if (uri != null) {
                try {
                    if (issueBitmap == null) {
                        issueBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        feedBackBinding.issueImage1.setVisibility(View.VISIBLE);
                        feedBackBinding.issueImage1.setImageBitmap(issueBitmap);
                    } else {
                        issueBitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        feedBackBinding.issueImage2.setVisibility(View.VISIBLE);
                        feedBackBinding.issueImage3.setVisibility(View.GONE);
                        feedBackBinding.issueImage2.setImageBitmap(issueBitmap2);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Please Select image", Toast.LENGTH_SHORT).show();
        }
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


        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(FeedBack.this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == STORAGE_PERMISSION_CREATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(FeedBack.this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}