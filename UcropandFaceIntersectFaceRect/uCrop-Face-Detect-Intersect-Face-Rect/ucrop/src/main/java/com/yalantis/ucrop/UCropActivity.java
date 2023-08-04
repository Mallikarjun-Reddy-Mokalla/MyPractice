package com.yalantis.ucrop;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.util.SelectedStateListDrawable;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;
import com.yalantis.ucrop.view.widget.AspectRatioTextView;
import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */

@SuppressWarnings("ConstantConditions")
public class UCropActivity extends AppCompatActivity {

    public static final int DEFAULT_COMPRESS_QUALITY = 90;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;

    public static final int NONE = 0;
    public static final int SCALE = 1;
    public static final int ROTATE = 2;
    public static final int ALL = 3;

    @IntDef({NONE, SCALE, ROTATE, ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GestureTypes {

    }

    private static final String TAG = "UCropActivity";
    private static final long CONTROLS_ANIMATION_DURATION = 50;
    private static final int TABS_COUNT = 3;
    private static final int SCALE_WIDGET_SENSITIVITY_COEFFICIENT = 15000;
    private static final int ROTATE_WIDGET_SENSITIVITY_COEFFICIENT = 42;
    private static final int BRIGHTNESS_WIDGET_SENSITIVITY_COEFFICIENT = 3;
    private static final int CONTRAST_WIDGET_SENSITIVITY_COEFFICIENT = 4;
    private static final int SATURATION_WIDGET_SENSITIVITY_COEFFICIENT = 3;
    private static final int SHARPNESS_WIDGET_SENSITIVITY_COEFFICIENT = 400;

    private String mToolbarTitle;

    // Enables dynamic coloring
    private int mToolbarColor;
    private int mStatusBarColor;
    private int mActiveControlsWidgetColor;
    private int mToolbarWidgetColor;
    @ColorInt
    private int mRootViewBackgroundColor;
    @DrawableRes
    private int mToolbarCancelDrawable;
    @DrawableRes
    private int mToolbarCropDrawable;
    private int mLogoColor;

    private boolean mShowBottomControls;
    private boolean mShowLoader = true;

    private UCropView mUCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    private ViewGroup mWrapperStateAspectRatio, mWrapperStateRotate, mWrapperStateScale, mWrapperStateBrightness, mWrapperStateContrast, mWrapperStateSaturation, mWrapperStateSharpness;
    private ViewGroup mLayoutAspectRatio, mLayoutRotate, mLayoutScale, mLayoutBrightnessBar, mLayoutContrastBar, mLayoutSaturationBar, mLayoutSharpnessBar;
    private List<ViewGroup> mCropAspectRatioViews = new ArrayList<>();
    private TextView mTextViewRotateAngle, mTextViewScalePercent, mTextViewBrightness, mTextViewContrast, mTextViewSaturation, mTextViewSharpness;
    private View mBlockingView;

    private Transition mControlsTransition;

    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private int mCompressQuality = DEFAULT_COMPRESS_QUALITY;
    private int[] mAllowedGestures = new int[]{SCALE, ROTATE, ALL};
    public static UCropActivity uCropActivity;
    public static Bitmap inputBitmap;
    public static List<int[]> facePixelArrays;
    public static ArrayList<RectF> faceRects; // List to store RectF bounds of each face
    public TextView cautionText;
    Button goCartton;
    Dialog bottomSheetDialog;
    TextView popUpText;
    ImageView exitIcon;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static Bitmap bitmapcropped;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ucrop_activity_photobox);
        uCropActivity = this;
        final Intent intent = getIntent();
        setupViews(intent);
        setImageData(intent);
        setInitialState();
        addBlockingView();
        setUpCustomView();
        bottomSheetDialog = new Dialog(this);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.pop_up_sheet);
        popUpText = bottomSheetDialog.findViewById(R.id.pop_sheet_text);
        exitIcon = bottomSheetDialog.findViewById(R.id.exit_icon);
        bottomSheetDialog.setCancelable(false);

        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

        goCartton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropAndSaveImage();

            }
        });
        exitIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                    onBackPressed();

                }else {
                    onBackPressed();


                }
            }
        });
    }

    private void setUpCustomView() {
        cautionText = findViewById(R.id.caution_text);
        goCartton = findViewById(R.id.go_cartoon);
    }

    public void setCautionText(String inputText) {
        cautionText.setText(inputText);
    }

    public static UCropActivity getInstance() {
        return uCropActivity;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.ucrop_menu_activity, menu);

        // Change crop & loader menu icons color to match the rest of the UI colors

        MenuItem menuItemLoader = menu.findItem(R.id.menu_loader);
        Drawable menuItemLoaderIcon = menuItemLoader.getIcon();
        if (menuItemLoaderIcon != null) {
            try {
                menuItemLoaderIcon.mutate();
                menuItemLoaderIcon.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
                menuItemLoader.setIcon(menuItemLoaderIcon);
            } catch (IllegalStateException e) {
                Log.i(TAG, String.format("%s - %s", e.getMessage(), getString(R.string.ucrop_mutate_exception_hint)));
            }
            ((Animatable) menuItemLoader.getIcon()).start();
        }

        MenuItem menuItemCrop = menu.findItem(R.id.menu_crop);
        Drawable menuItemCropIcon = ContextCompat.getDrawable(this, mToolbarCropDrawable);
        if (menuItemCropIcon != null) {
            menuItemCropIcon.mutate();
            menuItemCropIcon.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
            menuItemCrop.setIcon(menuItemCropIcon);
        }
        menuItemCrop.setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.menu_crop).setVisible(!mShowLoader);
        menu.findItem(R.id.menu_loader).setVisible(mShowLoader);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_crop) {
//            cropAndSaveImage();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGestureCropImageView != null) {
            mGestureCropImageView.cancelAllAnimations();
        }
    }

    /**
     * This method extracts all data from the incoming intent and setups views properly.
     */
    private void setImageData(@NonNull Intent intent) {
        Uri inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        Uri outputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);
        processOptions(intent);
        try {
            InputStream inputStream = UCropActivity.this.getContentResolver().openInputStream(inputUri);
            inputBitmap = BitmapFactory.decodeStream(inputStream);
            System.out.println(inputBitmap);
            if (inputBitmap != null) {
//                identifyTheFaces(inputBitmap);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Error reading input stream: " + e.getMessage());
        }
        if (inputUri != null && outputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri, outputUri);
            } catch (Exception e) {
                setResultError(e);
                finish();
            }
        } else {
            setResultError(new NullPointerException(getString(R.string.ucrop_error_input_data_is_absent)));
            finish();
        }
    }

    public void identifyTheFaces(final Bitmap faceBitmap) {
        InputImage image = InputImage.fromBitmap(faceBitmap, 0);
        FaceDetector detector = FaceDetection.getClient();
        Task<List<Face>> result = detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(List<Face> faces) {
                System.out.println(faces);
                if (faces.size() == 0) {
                    Toast.makeText(UCropActivity.this, "No faces Found, Choose to Crop the image with face", Toast.LENGTH_LONG).show();
                } else {
                    faceRects = new ArrayList<>(); // Reset the list to clear any previous results
                    for (Face face : faces) {
                        Rect bounds = face.getBoundingBox();
                        float left = (float) bounds.left / faceBitmap.getWidth();
                        float top = (float) bounds.top / faceBitmap.getHeight();
                        float right = (float) bounds.right / faceBitmap.getWidth();
                        float bottom = (float) bounds.bottom / faceBitmap.getHeight();

                        // Create a new RectF containing the face region
                        RectF faceRectF = new RectF(left, top, right, bottom);

                        // Add the faceRectF to the list of faceRects
                        faceRects.add(faceRectF);

                        // Use the faceRectF for display or other purposes
                        // For example, you can draw the faceRectF on a canvas or use it for face recognition
                    }
                }

                if (faces.size() == 1) {
//                    Toast.makeText(UCropActivity.this, faceRects.get(0).toString(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(UCropActivity.this, " Single Face ", Toast.LENGTH_LONG).show();
                    // Do something for a single face
                } else {
                    if (faces.size() > 1) {
                        Toast.makeText(UCropActivity.this, "Image has Multiple faces", Toast.LENGTH_LONG).show();
                        // Do something for multiple faces
                    }
                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UCropActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
        System.out.println(faceRects);
    }

    /**
     * This method extracts {@link com.yalantis.ucrop.UCrop.Options #optionsBundle} from incoming intent
     * and setups Activity, {@link OverlayView} and {@link CropImageView} properly.
     */
    @SuppressWarnings("deprecation")
    private void processOptions(@NonNull Intent intent) {
        // Bitmap compression options
        String compressionFormatName = intent.getStringExtra(UCrop.Options.EXTRA_COMPRESSION_FORMAT_NAME);
        Bitmap.CompressFormat compressFormat = null;
        if (!TextUtils.isEmpty(compressionFormatName)) {
            compressFormat = Bitmap.CompressFormat.valueOf(compressionFormatName);
        }
        mCompressFormat = (compressFormat == null) ? DEFAULT_COMPRESS_FORMAT : compressFormat;

        mCompressQuality = intent.getIntExtra(UCrop.Options.EXTRA_COMPRESSION_QUALITY, UCropActivity.DEFAULT_COMPRESS_QUALITY);

        // Gestures options
        int[] allowedGestures = intent.getIntArrayExtra(UCrop.Options.EXTRA_ALLOWED_GESTURES);
        if (allowedGestures != null && allowedGestures.length == TABS_COUNT) {
            mAllowedGestures = allowedGestures;
        }

        // Crop image view options
        mGestureCropImageView.setMaxBitmapSize(intent.getIntExtra(UCrop.Options.EXTRA_MAX_BITMAP_SIZE, CropImageView.DEFAULT_MAX_BITMAP_SIZE));
        mGestureCropImageView.setMaxScaleMultiplier(intent.getFloatExtra(UCrop.Options.EXTRA_MAX_SCALE_MULTIPLIER, CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER));
        mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(intent.getIntExtra(UCrop.Options.EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION));

        // Overlay view options
        mOverlayView.setFreestyleCropEnabled(intent.getBooleanExtra(UCrop.Options.EXTRA_FREE_STYLE_CROP, OverlayView.DEFAULT_FREESTYLE_CROP_MODE != OverlayView.FREESTYLE_CROP_MODE_DISABLE));

        mOverlayView.setDimmedColor(intent.getIntExtra(UCrop.Options.EXTRA_DIMMED_LAYER_COLOR, getResources().getColor(R.color.ucrop_color_default_dimmed)));
        mOverlayView.setCircleDimmedLayer(intent.getBooleanExtra(UCrop.Options.EXTRA_CIRCLE_DIMMED_LAYER, OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER));

        mOverlayView.setShowCropFrame(intent.getBooleanExtra(UCrop.Options.EXTRA_SHOW_CROP_FRAME, OverlayView.DEFAULT_SHOW_CROP_FRAME));
        mOverlayView.setCropFrameColor(intent.getIntExtra(UCrop.Options.EXTRA_CROP_FRAME_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_frame)));
        mOverlayView.setCropFrameStrokeWidth(intent.getIntExtra(UCrop.Options.EXTRA_CROP_FRAME_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width)));

        mOverlayView.setShowCropGrid(intent.getBooleanExtra(UCrop.Options.EXTRA_SHOW_CROP_GRID, OverlayView.DEFAULT_SHOW_CROP_GRID));
        mOverlayView.setCropGridRowCount(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_ROW_COUNT, OverlayView.DEFAULT_CROP_GRID_ROW_COUNT));
        mOverlayView.setCropGridColumnCount(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_COLUMN_COUNT, OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT));
        mOverlayView.setCropGridColor(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_grid)));
        mOverlayView.setCropGridCornerColor(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_CORNER_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_grid)));
        mOverlayView.setCropGridStrokeWidth(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width)));

        // Aspect ratio options
        float aspectRatioX = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_X, -1);
        float aspectRatioY = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_Y, -1);

        int aspectRationSelectedByDefault = intent.getIntExtra(UCrop.Options.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);
        ArrayList<AspectRatio> aspectRatioList = intent.getParcelableArrayListExtra(UCrop.Options.EXTRA_ASPECT_RATIO_OPTIONS);

        if (aspectRatioX >= 0 && aspectRatioY >= 0) {
            if (mWrapperStateAspectRatio != null) {
                mWrapperStateAspectRatio.setVisibility(View.GONE);
            }
            float targetAspectRatio = aspectRatioX / aspectRatioY;
            mGestureCropImageView.setTargetAspectRatio(1.0f);

//            mGestureCropImageView.setTargetAspectRatio(Float.isNaN(targetAspectRatio) ? CropImageView.SOURCE_IMAGE_ASPECT_RATIO : targetAspectRatio);
        } else if (aspectRatioList != null && aspectRationSelectedByDefault < aspectRatioList.size()) {
            float targetAspectRatio = aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioX() / aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioY();
//            mGestureCropImageView.setTargetAspectRatio(Float.isNaN(targetAspectRatio) ? CropImageView.SOURCE_IMAGE_ASPECT_RATIO : targetAspectRatio);
            mGestureCropImageView.setTargetAspectRatio(1.0f);

        } else {
            mGestureCropImageView.setTargetAspectRatio(1.0f);

//            mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
        }

        // Result bitmap max size options
        int maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0);
        int maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0);

        if (maxSizeX > 0 && maxSizeY > 0) {
            mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
            mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
        }

        mWrapperStateBrightness.setVisibility(getIntent().getBooleanExtra(UCrop.Options.EXTRA_BRIGHTNESS, true) ? View.VISIBLE : View.GONE);
        mWrapperStateContrast.setVisibility(getIntent().getBooleanExtra(UCrop.Options.EXTRA_CONTRAST, true) ? View.VISIBLE : View.GONE);
        mWrapperStateSaturation.setVisibility(getIntent().getBooleanExtra(UCrop.Options.EXTRA_SATURATION, true) ? View.VISIBLE : View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getIntent().getBooleanExtra(UCrop.Options.EXTRA_SHARPNESS, true)) {
            mWrapperStateSharpness.setVisibility(View.VISIBLE);
        } else {
            mWrapperStateSharpness.setVisibility(View.GONE);
        }
    }

    private void setupViews(@NonNull Intent intent) {
        mStatusBarColor = intent.getIntExtra(UCrop.Options.EXTRA_STATUS_BAR_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_statusbar));
        mToolbarColor = intent.getIntExtra(UCrop.Options.EXTRA_TOOL_BAR_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_toolbar));
        mActiveControlsWidgetColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE, ContextCompat.getColor(this, R.color.ucrop_color_active_controls_color));

        mToolbarWidgetColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, ContextCompat.getColor(this, R.color.ucrop_color_toolbar_widget));
        mToolbarCancelDrawable = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, R.drawable.ucrop_ic_cross);
        mToolbarCropDrawable = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_CROP_DRAWABLE, R.drawable.ucrop_ic_done);
        mToolbarTitle = intent.getStringExtra(UCrop.Options.EXTRA_UCROP_TITLE_TEXT_TOOLBAR);
        mToolbarTitle = mToolbarTitle != null ? mToolbarTitle : getResources().getString(R.string.ucrop_label_edit_photo);
        mLogoColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_LOGO_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_default_logo));
        mShowBottomControls = !intent.getBooleanExtra(UCrop.Options.EXTRA_HIDE_BOTTOM_CONTROLS, false);
        mRootViewBackgroundColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_crop_background));

        setupAppBar();
        initiateRootViews();

        if (mShowBottomControls) {

            ViewGroup viewGroup = findViewById(R.id.ucrop_photobox);
            ViewGroup wrapper = viewGroup.findViewById(R.id.controls_wrapper);
            wrapper.setVisibility(View.GONE);
            LayoutInflater.from(this).inflate(R.layout.ucrop_controls, wrapper, true);

            mControlsTransition = new AutoTransition();
            mControlsTransition.setDuration(CONTROLS_ANIMATION_DURATION);

            mWrapperStateAspectRatio = findViewById(R.id.state_aspect_ratio);
            mWrapperStateAspectRatio.setOnClickListener(mStateClickListener);
            mWrapperStateRotate = findViewById(R.id.state_rotate);
            mWrapperStateRotate.setOnClickListener(mStateClickListener);
            mWrapperStateScale = findViewById(R.id.state_scale);
            mWrapperStateScale.setOnClickListener(mStateClickListener);

            mWrapperStateBrightness = findViewById(R.id.state_brightness);
            mWrapperStateBrightness.setOnClickListener(mStateClickListener);
            mWrapperStateContrast = findViewById(R.id.state_contrast);
            mWrapperStateContrast.setOnClickListener(mStateClickListener);
            mWrapperStateSaturation = findViewById(R.id.state_saturation);
            mWrapperStateSaturation.setOnClickListener(mStateClickListener);
            mWrapperStateSharpness = findViewById(R.id.state_sharpness);
            mWrapperStateSharpness.setOnClickListener(mStateClickListener);


            mLayoutAspectRatio = findViewById(R.id.layout_aspect_ratio);
            mLayoutRotate = findViewById(R.id.layout_rotate_wheel);
            mLayoutScale = findViewById(R.id.layout_scale_wheel);
            mLayoutBrightnessBar = findViewById(R.id.layout_brightness_bar);
            mLayoutContrastBar = findViewById(R.id.layout_contrast_bar);
            mLayoutSaturationBar = findViewById(R.id.layout_saturation_bar);
            mLayoutSharpnessBar = findViewById(R.id.layout_sharpness_bar);

            setupAspectRatioWidget(intent);
            setupRotateWidget();
            setupScaleWidget();
            setupStatesWrapper();
            setupBrightnessWidget();
            setupContrastWidget();
            setupSaturationWidget();
            setupSharpnessWidget();
        }
    }

    /**
     * Configures and styles both status bar and toolbar.
     */
    private void setupAppBar() {
        setStatusBarColor(mStatusBarColor);

        final Toolbar toolbar = findViewById(R.id.toolbar);

        // Set all of the Toolbar coloring
        toolbar.setBackgroundColor(mToolbarColor);
        toolbar.setTitleTextColor(mToolbarWidgetColor);

        final TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setTextColor(mToolbarWidgetColor);
        toolbarTitle.setText(mToolbarTitle);

        // Color buttons inside the Toolbar
        Drawable stateButtonDrawable = ContextCompat.getDrawable(this, mToolbarCancelDrawable).mutate();
        stateButtonDrawable.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(stateButtonDrawable);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initiateRootViews() {
        mUCropView = findViewById(R.id.ucrop);
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();

        mGestureCropImageView.setTransformImageListener(mImageListener);

        ((ImageView) findViewById(R.id.image_view_logo)).setColorFilter(mLogoColor, PorterDuff.Mode.SRC_ATOP);

        findViewById(R.id.ucrop_frame).setBackgroundColor(mRootViewBackgroundColor);
        if (!mShowBottomControls) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.ucrop_frame).getLayoutParams();
            params.bottomMargin = 0;
            findViewById(R.id.ucrop_frame).requestLayout();
        }
    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
            setAngleText(currentAngle);
        }

        @Override
        public void onScale(float currentScale) {
            setScaleText(currentScale);
        }

        @Override
        public void onBrightness(float currentBrightness) {
            setBrightnessText(currentBrightness);
        }

        @Override
        public void onContrast(float currentContrast) {
            setContrastText(currentContrast);
        }

        @Override
        public void onSaturation(float currentSaturation) {
            setSaturationText(currentSaturation);
        }

        @Override
        public void onSharpness(float currentSharpness) {
            setSharpnessText(currentSharpness);
        }

        @Override
        public void onLoadComplete() {
            mUCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
            mBlockingView.setClickable(false);
            mShowLoader = false;
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            setResultError(e);
            finish();
        }

    };

    /**
     * Use {@link #mActiveControlsWidgetColor} for color filter
     */
    private void setupStatesWrapper() {
        ImageView stateScaleImageView = findViewById(R.id.image_view_state_scale);
        ImageView stateRotateImageView = findViewById(R.id.image_view_state_rotate);
        ImageView stateAspectRatioImageView = findViewById(R.id.image_view_state_aspect_ratio);
        ImageView stateBrightnessImageView = findViewById(R.id.image_view_state_brightness);
        ImageView stateContrastImageView = findViewById(R.id.image_view_state_contrast);
        ImageView stateSaturationImageView = findViewById(R.id.image_view_state_saturation);
        ImageView stateSharpnessImageView = findViewById(R.id.image_view_state_sharpness);

        stateScaleImageView.setImageDrawable(new SelectedStateListDrawable(stateScaleImageView.getDrawable(), mActiveControlsWidgetColor));
        stateRotateImageView.setImageDrawable(new SelectedStateListDrawable(stateRotateImageView.getDrawable(), mActiveControlsWidgetColor));
        stateAspectRatioImageView.setImageDrawable(new SelectedStateListDrawable(stateAspectRatioImageView.getDrawable(), mActiveControlsWidgetColor));
        stateBrightnessImageView.setImageDrawable(new SelectedStateListDrawable(stateBrightnessImageView.getDrawable(), mActiveControlsWidgetColor));
        stateContrastImageView.setImageDrawable(new SelectedStateListDrawable(stateContrastImageView.getDrawable(), mActiveControlsWidgetColor));
        stateSaturationImageView.setImageDrawable(new SelectedStateListDrawable(stateSaturationImageView.getDrawable(), mActiveControlsWidgetColor));
        stateSharpnessImageView.setImageDrawable(new SelectedStateListDrawable(stateSharpnessImageView.getDrawable(), mActiveControlsWidgetColor));
    }


    /**
     * Sets status-bar color for L devices.
     *
     * @param color - status-bar color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }

    private void setupAspectRatioWidget(@NonNull Intent intent) {

        int aspectRationSelectedByDefault = intent.getIntExtra(UCrop.Options.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);
        ArrayList<AspectRatio> aspectRatioList = intent.getParcelableArrayListExtra(UCrop.Options.EXTRA_ASPECT_RATIO_OPTIONS);

        if (aspectRatioList == null || aspectRatioList.isEmpty()) {
            aspectRationSelectedByDefault = 2;
            aspectRatioList = new ArrayList<>();
      /*      aspectRatioList.add(new AspectRatio(null, 1, 1));
            aspectRatioList.add(new AspectRatio(null, 1, 1));
             aspectRatioList.add(new AspectRatio(getString(R.string.ucrop_label_original).toUpperCase(), 1, 1));
            aspectRatioList.add(new AspectRatio(null, 1, 1));
            aspectRatioList.add(new AspectRatio(null, 1 ,1));*/
            aspectRatioList = new ArrayList<>();
            aspectRatioList.add(new AspectRatio(null, 1, 1));
            aspectRatioList.add(new AspectRatio(null, 3, 4));
            aspectRatioList.add(new AspectRatio(getString(R.string.ucrop_label_original).toUpperCase(), CropImageView.SOURCE_IMAGE_ASPECT_RATIO, CropImageView.SOURCE_IMAGE_ASPECT_RATIO));
            aspectRatioList.add(new AspectRatio(null, 3, 2));
            aspectRatioList.add(new AspectRatio(null, 16, 9));
        }

        LinearLayout wrapperAspectRatioList = findViewById(R.id.layout_aspect_ratio);

        FrameLayout wrapperAspectRatio;
        AspectRatioTextView aspectRatioTextView;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        for (AspectRatio aspectRatio : aspectRatioList) {
            wrapperAspectRatio = (FrameLayout) getLayoutInflater().inflate(R.layout.ucrop_aspect_ratio, null);
            wrapperAspectRatio.setLayoutParams(lp);
            aspectRatioTextView = ((AspectRatioTextView) wrapperAspectRatio.getChildAt(0));
            aspectRatioTextView.setActiveColor(mActiveControlsWidgetColor);
            aspectRatioTextView.setAspectRatio(aspectRatio);

            wrapperAspectRatioList.addView(wrapperAspectRatio);
            mCropAspectRatioViews.add(wrapperAspectRatio);
        }

        mCropAspectRatioViews.get(aspectRationSelectedByDefault).setSelected(true);

        for (ViewGroup cropAspectRatioView : mCropAspectRatioViews) {
            cropAspectRatioView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGestureCropImageView.setTargetAspectRatio(1.0f);

//                    mGestureCropImageView.setTargetAspectRatio(((AspectRatioTextView) ((ViewGroup) v).getChildAt(0)).getAspectRatio(v.isSelected()));
                    mGestureCropImageView.setImageToWrapCropBounds();
                    if (!v.isSelected()) {
                        for (ViewGroup cropAspectRatioView : mCropAspectRatioViews) {
                            cropAspectRatioView.setSelected(cropAspectRatioView == v);
                        }
                    }
                }
            });
        }
    }

    private void setupRotateWidget() {
        mTextViewRotateAngle = findViewById(R.id.text_view_rotate);
        ((HorizontalProgressWheelView) findViewById(R.id.rotate_scroll_wheel)).setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScroll(float delta, float totalDistance) {
                mGestureCropImageView.postRotate(delta / ROTATE_WIDGET_SENSITIVITY_COEFFICIENT);
            }

            @Override
            public void onScrollEnd() {
                mGestureCropImageView.setImageToWrapCropBounds();
            }

            @Override
            public void onScrollStart() {
                mGestureCropImageView.cancelAllAnimations();
            }
        });

        ((HorizontalProgressWheelView) findViewById(R.id.rotate_scroll_wheel)).setMiddleLineColor(mActiveControlsWidgetColor);


        findViewById(R.id.wrapper_reset_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRotation();
            }
        });
        findViewById(R.id.wrapper_rotate_by_angle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateByAngle(90);
            }
        });
        setAngleTextColor(mActiveControlsWidgetColor);
    }

    private void setupScaleWidget() {
        mTextViewScalePercent = findViewById(R.id.text_view_scale);
        ((HorizontalProgressWheelView) findViewById(R.id.scale_scroll_wheel)).setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScroll(float delta, float totalDistance) {
                if (delta > 0) {
                    mGestureCropImageView.zoomInImage(mGestureCropImageView.getCurrentScale() + delta * ((mGestureCropImageView.getMaxScale() - mGestureCropImageView.getMinScale()) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT));
                } else {
                    mGestureCropImageView.zoomOutImage(mGestureCropImageView.getCurrentScale() + delta * ((mGestureCropImageView.getMaxScale() - mGestureCropImageView.getMinScale()) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT));
                }
            }

            @Override
            public void onScrollEnd() {
                mGestureCropImageView.setImageToWrapCropBounds();
            }

            @Override
            public void onScrollStart() {
                mGestureCropImageView.cancelAllAnimations();
            }
        });
        ((HorizontalProgressWheelView) findViewById(R.id.scale_scroll_wheel)).setMiddleLineColor(mActiveControlsWidgetColor);

        setScaleTextColor(mActiveControlsWidgetColor);
    }

    private void setupBrightnessWidget() {
        mTextViewBrightness = findViewById(R.id.text_view_brightness);
        ((HorizontalProgressWheelView) findViewById(R.id.brightness_scroll_wheel)).setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScroll(float delta, float totalDistance) {
                mGestureCropImageView.postBrightness(delta / BRIGHTNESS_WIDGET_SENSITIVITY_COEFFICIENT);
            }

            @Override
            public void onScrollEnd() {
                mGestureCropImageView.setImageToWrapCropBounds();
            }

            @Override
            public void onScrollStart() {
                mGestureCropImageView.cancelAllAnimations();
            }
        });

        ((HorizontalProgressWheelView) findViewById(R.id.brightness_scroll_wheel)).setMiddleLineColor(mActiveControlsWidgetColor);
    }

    private void setupContrastWidget() {
        mTextViewContrast = findViewById(R.id.text_view_contrast);
        ((HorizontalProgressWheelView) findViewById(R.id.contrast_scroll_wheel)).setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScroll(float delta, float totalDistance) {
                mGestureCropImageView.postContrast(delta / CONTRAST_WIDGET_SENSITIVITY_COEFFICIENT);
            }

            @Override
            public void onScrollEnd() {
                mGestureCropImageView.setImageToWrapCropBounds();
            }

            @Override
            public void onScrollStart() {
                mGestureCropImageView.cancelAllAnimations();
            }
        });

        ((HorizontalProgressWheelView) findViewById(R.id.contrast_scroll_wheel)).setMiddleLineColor(mActiveControlsWidgetColor);
    }

    private void setupSaturationWidget() {
        mTextViewSaturation = findViewById(R.id.text_view_saturation);
        ((HorizontalProgressWheelView) findViewById(R.id.saturation_scroll_wheel)).setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScroll(float delta, float totalDistance) {
                mGestureCropImageView.postSaturation(delta / SATURATION_WIDGET_SENSITIVITY_COEFFICIENT);
            }

            @Override
            public void onScrollEnd() {
                mGestureCropImageView.setImageToWrapCropBounds();
            }

            @Override
            public void onScrollStart() {
                mGestureCropImageView.cancelAllAnimations();
            }
        });

        ((HorizontalProgressWheelView) findViewById(R.id.saturation_scroll_wheel)).setMiddleLineColor(mActiveControlsWidgetColor);
    }

    private void setupSharpnessWidget() {
        mTextViewSharpness = findViewById(R.id.text_view_sharpness);
        ((HorizontalProgressWheelView) findViewById(R.id.sharpness_scroll_wheel)).setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScroll(float delta, float totalDistance) {
                mGestureCropImageView.postSharpness(delta / SHARPNESS_WIDGET_SENSITIVITY_COEFFICIENT);
            }

            @Override
            public void onScrollEnd() {
                mGestureCropImageView.setImageToWrapCropBounds();
            }

            @Override
            public void onScrollStart() {
                mGestureCropImageView.cancelAllAnimations();
            }
        });

        ((HorizontalProgressWheelView) findViewById(R.id.sharpness_scroll_wheel)).setMiddleLineColor(mActiveControlsWidgetColor);
    }

    private void setAngleText(float angle) {
        if (mTextViewRotateAngle != null) {
            mTextViewRotateAngle.setText(String.format(Locale.getDefault(), "%.1f°", angle));
        }
    }

    private void setAngleTextColor(int textColor) {
        if (mTextViewRotateAngle != null) {
            mTextViewRotateAngle.setTextColor(textColor);
        }
    }

    private void setScaleText(float scale) {
        if (mTextViewScalePercent != null) {
            mTextViewScalePercent.setText(String.format(Locale.getDefault(), "%d%%", (int) (scale * 100)));
        }
    }

    private void setBrightnessText(float brightness) {
        if (mTextViewBrightness != null) {
            mTextViewBrightness.setText(String.format(Locale.getDefault(), "%d", (int) brightness));
        }
    }

    private void setContrastText(float contrast) {
        if (mTextViewContrast != null) {
            mTextViewContrast.setText(String.format(Locale.getDefault(), "%d", (int) contrast));
        }
    }

    private void setSaturationText(float saturation) {
        if (mTextViewSaturation != null) {
            mTextViewSaturation.setText(String.format(Locale.getDefault(), "%d", (int) saturation));
        }
    }

    private void setSharpnessText(float sharpness) {
        if (mTextViewSharpness != null) {
            mTextViewSharpness.setText(String.format(Locale.getDefault(), "%d", (int) sharpness));
        }
    }

    private void setScaleTextColor(int textColor) {
        if (mTextViewScalePercent != null) {
            mTextViewScalePercent.setTextColor(textColor);
        }
    }

    private void resetRotation() {
        mGestureCropImageView.postRotate(-mGestureCropImageView.getCurrentAngle());
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    private void rotateByAngle(int angle) {
        mGestureCropImageView.postRotate(angle);
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    private final View.OnClickListener mStateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!v.isSelected()) {
                setWidgetState(v.getId());
            }
        }
    };

    private void setInitialState() {
        if (mShowBottomControls) {
            if (mWrapperStateAspectRatio.getVisibility() == View.VISIBLE) {
                setWidgetState(R.id.state_aspect_ratio);
            } else {
                setWidgetState(R.id.state_scale);
            }
        } else {
            setAllowedGestures(0);
        }
    }

    private void setWidgetState(@IdRes int stateViewId) {
        if (!mShowBottomControls) return;

        mWrapperStateAspectRatio.setSelected(stateViewId == R.id.state_aspect_ratio);
        mWrapperStateRotate.setSelected(stateViewId == R.id.state_rotate);
        mWrapperStateScale.setSelected(stateViewId == R.id.state_scale);
        mWrapperStateBrightness.setSelected(stateViewId == R.id.state_brightness);
        mWrapperStateContrast.setSelected(stateViewId == R.id.state_contrast);
        mWrapperStateSaturation.setSelected(stateViewId == R.id.state_saturation);
        mWrapperStateSharpness.setSelected(stateViewId == R.id.state_sharpness);

        mLayoutAspectRatio.setVisibility(stateViewId == R.id.state_aspect_ratio ? View.VISIBLE : View.GONE);
        mLayoutRotate.setVisibility(stateViewId == R.id.state_rotate ? View.VISIBLE : View.GONE);
        mLayoutScale.setVisibility(stateViewId == R.id.state_scale ? View.VISIBLE : View.GONE);
        mLayoutBrightnessBar.setVisibility(stateViewId == R.id.state_brightness ? View.VISIBLE : View.GONE);
        mLayoutContrastBar.setVisibility(stateViewId == R.id.state_contrast ? View.VISIBLE : View.GONE);
        mLayoutSaturationBar.setVisibility(stateViewId == R.id.state_saturation ? View.VISIBLE : View.GONE);
        mLayoutSharpnessBar.setVisibility(stateViewId == R.id.state_sharpness ? View.VISIBLE : View.GONE);

        changeSelectedTab(stateViewId);

        if (stateViewId == R.id.state_brightness || stateViewId == R.id.state_contrast || stateViewId == R.id.state_saturation || stateViewId == R.id.state_sharpness || stateViewId == R.id.state_scale) {
            setAllowedGestures(0);
        } else if (stateViewId == R.id.state_rotate) {
            setAllowedGestures(1);
        } else {
            setAllowedGestures(2);
        }
    }

    private void changeSelectedTab(int stateViewId) {
        TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.ucrop_photobox), mControlsTransition);

        mWrapperStateScale.findViewById(R.id.text_view_scale).setVisibility(stateViewId == R.id.state_scale ? View.VISIBLE : View.GONE);
        mWrapperStateAspectRatio.findViewById(R.id.text_view_crop).setVisibility(stateViewId == R.id.state_aspect_ratio ? View.VISIBLE : View.GONE);
        mWrapperStateRotate.findViewById(R.id.text_view_rotate).setVisibility(stateViewId == R.id.state_rotate ? View.VISIBLE : View.GONE);
        mWrapperStateBrightness.findViewById(R.id.text_view_brightness).setVisibility(stateViewId == R.id.state_brightness ? View.VISIBLE : View.GONE);
        mWrapperStateContrast.findViewById(R.id.text_view_contrast).setVisibility(stateViewId == R.id.state_contrast ? View.VISIBLE : View.GONE);
        mWrapperStateSaturation.findViewById(R.id.text_view_saturation).setVisibility(stateViewId == R.id.state_saturation ? View.VISIBLE : View.GONE);
        mWrapperStateSharpness.findViewById(R.id.text_view_sharpness).setVisibility(stateViewId == R.id.state_sharpness ? View.VISIBLE : View.GONE);
    }

    private void setAllowedGestures(int tab) {
        mGestureCropImageView.setScaleEnabled(mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == SCALE);
        mGestureCropImageView.setRotateEnabled(mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == ROTATE);
    }

    /**
     * Adds view that covers everything below the Toolbar.
     * When it's clickable - user won't be able to click/touch anything below the Toolbar.
     * Need to block user input while loading and cropping an image.
     */
    private void addBlockingView() {
        if (mBlockingView == null) {
            mBlockingView = new View(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            mBlockingView.setLayoutParams(lp);
            mBlockingView.setClickable(true);
        }

        ((RelativeLayout) findViewById(R.id.ucrop_photobox)).addView(mBlockingView);
    }

    public void cropAndSaveImage() {
        mBlockingView.setClickable(true);
        mShowLoader = true;
        supportInvalidateOptionsMenu();

        mGestureCropImageView.cropAndSaveImage(mCompressFormat, mCompressQuality, new BitmapCropCallback() {

            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {

                try {
                    InputStream inputStream = UCropActivity.this.getContentResolver().openInputStream(resultUri);
                    bitmapcropped = BitmapFactory.decodeStream(inputStream);
                    System.out.println(bitmapcropped);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "Error reading input stream: " + e.getMessage());
                }
                if (bitmapcropped != null) {
                 /*   Uri uri = null;
                    resultUri=uri;*/
                    setResultUri(resultUri, mGestureCropImageView.getTargetAspectRatio(), offsetX, offsetY, imageWidth, imageHeight);
                    finish();
                }
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                setResultError(t);
                finish();
            }
        });
    }

    protected void setResultUri(Uri uri, float resultAspectRatio, int offsetX, int offsetY, int imageWidth, int imageHeight) {
        setResult(RESULT_OK, new Intent().putExtra(UCrop.EXTRA_OUTPUT_URI, uri).putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio).putExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH, imageWidth).putExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT, imageHeight).putExtra(UCrop.EXTRA_OUTPUT_OFFSET_X, offsetX).putExtra(UCrop.EXTRA_OUTPUT_OFFSET_Y, offsetY));
    }

    protected void setResultError(Throwable throwable) {
        setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
    }


    public void showPopDialog(String text) {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.show();
            popUpText.setText(text);

        }
    }

    public void setTextToPopDialog(String textPoped) {
    }

    public void enableDiableGoCartoonButtom(boolean clickable, float alpha) {
        goCartton.setClickable(clickable);
        goCartton.setAlpha(alpha);

    }
}