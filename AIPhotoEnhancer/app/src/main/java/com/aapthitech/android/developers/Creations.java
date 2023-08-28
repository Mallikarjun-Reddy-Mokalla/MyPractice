package com.aapthitech.android.developers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.aapthitech.android.developers.Activities.MainActivity;
import com.aapthitech.android.developers.Adapters.GalleryAdapter;
import com.aapthitech.android.developers.databinding.ActivityCreationsBinding;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Creations extends AppCompatActivity {
    ActivityCreationsBinding creationsBinding;
    private ArrayList<String> creationList = new ArrayList<>();
    private static final String subFolderName2 = "/MyCreations";
   public static Creations creationsInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        creationsBinding = ActivityCreationsBinding.inflate(getLayoutInflater());
        View view = creationsBinding.getRoot();
        setContentView(view);
        creationsInstance = this;
        if (isDirectoryNotEmpty(pathtoSave())) {
            creationList = getImagesFromDevice(subFolderName2);
            if (creationList != null && creationList.size() > 0) {
                creationsBinding.noImages.setVisibility(View.GONE);
            } else {
                creationsBinding.noImages.setVisibility(View.VISIBLE);
            }
            setImageAdapter(creationList);
        } else {
            creationsBinding.noImages.setVisibility(View.VISIBLE);
        }
        creationsBinding.createImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Creations.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
        creationsBinding.creationsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
public static Creations getInstance(){
        return creationsInstance;
}

    private void setImageAdapter(final ArrayList<String> imageFilesList) {
        GalleryAdapter creationsAdapter = new GalleryAdapter(imageFilesList, Creations.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false);

        creationsBinding.mRecyclerViewCreations.setLayoutManager(gridLayoutManager);
        creationsBinding.mRecyclerViewCreations.setNestedScrollingEnabled(true);

        creationsBinding.mRecyclerViewCreations.setAdapter(creationsAdapter);
    }

    public static String pathtoSave() {
        String SAVE_PATH = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() : Environment.getExternalStorageDirectory().toString();
        return new File(SAVE_PATH + "/PictureCraft" + "/MyCreations").getPath();
    }

    public boolean isDirectoryNotEmpty(String directoryPath) {
        Log.e("TEST_1", "directoryPath" + directoryPath);
        try {
            File file = new File(directoryPath);
            if (file.exists() && file.isDirectory()) {
                if (file.exists() && file.list().length > 0) {
                    System.out.println("Directory is not empty!");
                    return true;
                } else {
                    System.out.println("Directory is empty!");
                    return false;
                }
            } else {
                System.out.println("This is not a directory");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<String> getImagesFromDevice(String str) {
        final ArrayList<String> tempAudioList = new ArrayList<>();
        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/AIEnhancer" + subFolderName2;
        /*String directoryPath = (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) ?
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + str
                : Environment.getExternalStorageDirectory().toString() + "/" + getResources().getString(R.string.app_name) + str;*/
        File directory1 = new File(directoryPath);
        File[] files = directory1.listFiles();
        if (files != null) {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {

                    if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                        return -1;
                    } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        if (isDirectoryNotEmpty(directoryPath)) {
            for (int i = 0; i < files.length; i++) {
                tempAudioList.add(files[i].getAbsolutePath());
                Log.e("Files", "FileName:" + files[i].getAbsolutePath());
            }


            return tempAudioList;
        } else {
            return null;
        }
    }

    public void onImagesListEmpty() {
        creationsBinding.noImages.setVisibility(View.VISIBLE);
    }
}