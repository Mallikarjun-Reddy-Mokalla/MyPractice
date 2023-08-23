package com.aapthitech.android.developers.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PhotoData {

    /*The reason of this, is pure paranoia, Im not kidding, the configuration changes in the manifest,
     * should be enough to the variables not to reset during the go to the camera, take the picture,
     * come back to the activity, but some time doesnt work!
     * So we at least save the last picture taken here*/

    private Context context;
    private static final String PHOTO_NAME_PREFIX = "men suit editor";
    private static final String PHOTO_PREFERENCE_NAME = "temp_data";
    private static final String PHOTO_KEY = "IMAGE_URI_PATH";

    public PhotoData(Context context) {
        this.context = context;
    }



    public void saveUriPath(String selectedImageUriPath) {
        SharedPreferences savePhotoData = context.getSharedPreferences(PHOTO_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = savePhotoData.edit();
        prefEditor.putString(PHOTO_KEY, selectedImageUriPath);
        prefEditor.apply();
    }

    public String getUriPath() {
        SharedPreferences getSelectedImageUriPath = context.getSharedPreferences(PHOTO_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return getSelectedImageUriPath.getString(PHOTO_KEY, null);
    }

}
