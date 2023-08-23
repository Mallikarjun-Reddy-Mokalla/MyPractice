package com.aapthitech.android.developers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class HoverView2 extends View {

    public Context mContext;
    public static int mode = 0;
    public static Bitmap savedBitmap;
    Bitmap bm;
    Bitmap clippedBitmap;
    Bitmap dummyBitmap;
    Bitmap magicPointer;
    int[] saveBitmapData;
    int[] lastBitmapData;
    int viewWidth, viewHeight;
    int bmWidth, bmHeight;
    static Canvas newCanvas, dummycanvas;
    static Paint eraser, uneraser, dummyerase, dummyunerase;
    private Paint mBitmapPaint;
    private Paint mMaskPaint;
    static Canvas CANVAS;
    private Bitmap maskedBitmap;

    private static Path mPath, mPathErase, dummypath;
    public static int ERASE_MODE = 0;
    public static int UNERASE_MODE = 1;
    public static int MAGIC_MODE = 2;
    public static int MAGIC_MODE_RESTORE = 3;
    public static int MOVING_MODE = 4;

    public PointF touchPoint;
    public PointF drawingPoint;

    public int magicTouchRange = 200;
    public int magicThreshold = 15;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private int strokeWidth = 25;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int touchMode = NONE;
    String TAG = "tri.dung";
    boolean TOUCH = false;
    ArrayList<int[]> stackChange;
    ArrayList<int[]> stackChangedummy;
    ArrayList<Boolean> checkMirrorStep;
    int currentIndex = -1;
    final int STACKSIZE = 10;
    private String filename;
    HashMap<Integer, Float> zoomIncrease = new HashMap<>();
    HashMap<Integer, Float> newDistance = new HashMap<>();
    public static int POINTER_DISTANCE;
    public static int POINTER_OFFSET = 20;
    int ZOOM_PROGRESS = 0;
    Bitmap bmp;
    public MotionEvent motionEvent;
    Point zoomPos = new Point();

    public HoverView2(Context context, Bitmap bm, int w, int h, int viewwidth, int viewheight) {
        super(context);
        bmp = bm;
        mContext = context;
        viewWidth = viewwidth;
        viewHeight = viewheight;
        bmWidth = w;
        bmHeight = h;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        init(bm, w, h);
        zoomIncrease.put(1, (float) 1.5);
        zoomIncrease.put(2, (float) 2.0);
        zoomIncrease.put(3, (float) 2.5);
        zoomIncrease.put(4, (float) 3.0);
        zoomIncrease.put(5, (float) 3.5);
        newDistance.put(1, (float) 500);
        newDistance.put(2, (float) 525);
        newDistance.put(3, (float) 550);
        newDistance.put(4, (float) 575);
        newDistance.put(5, (float) 600);
    }

    public void switchMode(int _mode) {
        mode = _mode;
        resetPath();
        saveLastMaskData();
        if (mode == MAGIC_MODE || mode == MAGIC_MODE_RESTORE) {
            magicPointer = BitmapFactory.decodeResource(getResources(), R.drawable.erase_pointer);
        } else if (mode == ERASE_MODE || mode == UNERASE_MODE) {
            magicPointer = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.erase_pointer), strokeWidth + 5, strokeWidth + 5, false);
        }
        invalidate();

    }

    public int getMode() {
        return mode;
    }

    public void setMagicThreshold(int value) {
        magicThreshold = value;
    }

    public void setEraseBrushSize(int offSet) {
        strokeWidth = offSet;
        eraser.setStrokeWidth(offSet);
        uneraser.setStrokeWidth(offSet);
        dummyerase.setStrokeWidth(offSet);
        dummyunerase.setStrokeWidth(offSet);
        magicPointer = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.erase_pointer), offSet + 5, offSet + 5, false);
        mPath.reset();
        dummypath.reset();
        resetPath();
        invalidate();
    }

    public void init(Bitmap bitmap, int w, int h) {
        mPath = new Path();
        mPathErase = new Path();
        dummypath = new Path();
        eraser = new Paint();
        dummyerase = new Paint();

//        dummyerase.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        dummyerase.setColor(Color.RED);

        dummyerase.setAlpha(100);
        dummyerase.setAntiAlias(true);
        dummyerase.setStyle(Paint.Style.STROKE);
        dummyerase.setStrokeJoin(Paint.Join.ROUND);
        dummyerase.setStrokeCap(Paint.Cap.ROUND);
        dummyerase.setStrokeWidth(strokeWidth);


        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        eraser.setColor(Color.RED);
        eraser.setAlpha(100);
        eraser.setAntiAlias(true);
        eraser.setStyle(Paint.Style.STROKE);
        eraser.setStrokeJoin(Paint.Join.ROUND);
        eraser.setStrokeCap(Paint.Cap.ROUND);
        eraser.setStrokeWidth(strokeWidth);

        uneraser = new Paint();
        uneraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        uneraser.setAntiAlias(true);
        uneraser.setStyle(Paint.Style.STROKE);
        uneraser.setStrokeJoin(Paint.Join.ROUND);
        uneraser.setStrokeCap(Paint.Cap.ROUND);
        uneraser.setStrokeWidth(strokeWidth);


        dummyunerase = new Paint();
//        dummyunerase.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        dummyunerase.setAntiAlias(true);
        dummyunerase.setColor(Color.RED);

        dummyunerase.setStyle(Paint.Style.STROKE);
        dummyunerase.setStrokeJoin(Paint.Join.ROUND);
        dummyunerase.setStrokeCap(Paint.Cap.ROUND);
        dummyunerase.setStrokeWidth(strokeWidth);

        matrix.postTranslate((viewWidth - w) / 2, (viewHeight - h) / 2);

        mBitmapPaint = new Paint();
        mBitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mBitmapPaint.setAntiAlias(true);

        mMaskPaint = new Paint();

        mMaskPaint.setAntiAlias(true);

        bm = bitmap;
        bm = bm.copy(Bitmap.Config.ARGB_8888, true);

        clippedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        newCanvas = new Canvas(clippedBitmap);
        newCanvas.save();
        newCanvas.drawARGB(255, 0, 0, 0);
        dummyBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        dummyBitmap = bitmap;
        dummycanvas = new Canvas(dummyBitmap);
        dummycanvas.drawARGB(255, 255, 255, 255);
        dummycanvas.save();
//        dummycanvas.drawARGB(255, 0, 0, 0);
//        dummycanvas.drawColor(Color.RED);
        magicTouchRange = w > h ? h / 2 : w / 2;

        saveBitmapData = new int[w * h];
        bm.getPixels(saveBitmapData, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());

        lastBitmapData = new int[w * h];

        magicPointer = BitmapFactory.decodeResource(getResources(), R.drawable.erase_pointer);
        touchPoint = new PointF(w / 2, h / 2);
        drawingPoint = new PointF(w / 2, h / 2);

        saveLastMaskData();
        stackChange = new ArrayList<>();
        stackChangedummy = new ArrayList<>();
        checkMirrorStep = new ArrayList<>();
        addToStack(false);

        filename = "img_" + String.format("%d.jpg", System.currentTimeMillis());
        POINTER_DISTANCE = (int) (POINTER_OFFSET * mContext.getResources().getDisplayMetrics().density);
    }

    public void setPointerOffset(int pointerOffset) {
        POINTER_OFFSET = pointerOffset;
        POINTER_DISTANCE = (int) (POINTER_OFFSET * mContext.getResources().getDisplayMetrics().density);
    }

    void addToStack(boolean isMirror) {
        if (stackChange.size() >= STACKSIZE) {
            stackChange.remove(0);
            stackChangedummy.remove(0);
            if (currentIndex > 0) currentIndex--;
        }
        if (stackChange != null) {
            if (currentIndex == 0) {
                int size = stackChange.size();
                for (int i = size - 1; i > 0; i--) {
                    stackChange.remove(i);
                    stackChangedummy.remove(i);
                    checkMirrorStep.remove(i);
                }
            }
            int[] pix = new int[clippedBitmap.getWidth() * clippedBitmap.getHeight()];
            int[] pix_d = new int[dummyBitmap.getWidth() * dummyBitmap.getHeight()];

            clippedBitmap.getPixels(pix, 0, clippedBitmap.getWidth(), 0, 0, clippedBitmap.getWidth(), clippedBitmap.getHeight());
            dummyBitmap.getPixels(pix_d, 0, dummyBitmap.getWidth(), 0, 0, dummyBitmap.getWidth(), dummyBitmap.getHeight());


            stackChange.add(pix);
            stackChangedummy.add(pix_d);


            checkMirrorStep.add(isMirror);
            currentIndex = stackChange.size() - 1;
            System.out.println(clippedBitmap);
            System.out.println(dummyBitmap);
        }
    }

    public void redo() {
        Log.d(TAG, "Redo");
        resetPath();
        if (stackChange != null && stackChange.size() > 0 && currentIndex < stackChange.size() - 1) {
            currentIndex++;
            if (checkMirrorStep.get(currentIndex)) {
                Matrix matrix = new Matrix();
                matrix.preScale(-1.0f, 1.0f);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                bm.getPixels(saveBitmapData, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());
            }
            int[] pix = stackChange.get(currentIndex);
            int[] pix_rd = stackChangedummy.get(currentIndex);
            clippedBitmap.setPixels(pix, 0, bmWidth, 0, 0, bmWidth, bmHeight);
            dummyBitmap.setPixels(pix_rd, 0, bmWidth, 0, 0, bmWidth, bmHeight);
            System.out.println(dummyBitmap);
            invalidate();
        }
    }

    public void undo() {
        Log.d(TAG, "Undo");
        resetPath();

        if (stackChange != null && stackChange.size() > 0 && currentIndex > 0) {
            currentIndex--;
            if (checkMirrorStep.get(currentIndex + 1)) {
                Matrix matrix = new Matrix();
                matrix.preScale(-1.0f, 1.0f);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                bm.getPixels(saveBitmapData, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());
            }

            int[] pix = stackChange.get(currentIndex);
            int[] pix_ud = stackChangedummy.get(currentIndex);
            clippedBitmap.setPixels(pix, 0, bmWidth, 0, 0, bmWidth, bmHeight);
            dummyBitmap.setPixels(pix_ud, 0, bmWidth, 0, 0, bmWidth, bmHeight);
            invalidate();
            System.out.println(dummyBitmap);

        }
    }

    public boolean checkUndoEnable() {
        if (stackChange != null && stackChange.size() > 0 && currentIndex > 0) return true;
        return false;
    }

    public boolean checkRedoEnable() {
        if (stackChange != null && stackChange.size() > 0 && currentIndex < stackChange.size() - 1)
            return true;
        return false;
    }

    public Bitmap drawBitmap(Bitmap bmpDraw) {

        if (mode == ERASE_MODE || mode == UNERASE_MODE) {
            if (mode == ERASE_MODE) {
                uneraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                dummyunerase.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

            } else {

                uneraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                dummyunerase.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            }

            float strokeRatio = 1;

            if (SCALE > 1) strokeRatio = SCALE;

            eraser.setStrokeWidth(strokeWidth / strokeRatio);
            uneraser.setStrokeWidth(strokeWidth / strokeRatio);

            dummyerase.setStrokeWidth(strokeWidth / strokeRatio);
            dummyunerase.setStrokeWidth(strokeWidth / strokeRatio);

            newCanvas.drawPath(mPath, eraser);
            newCanvas.drawPath(mPathErase, uneraser);

            dummycanvas.drawPath(mPath, dummyerase);


            dummycanvas.drawPath(mPathErase, dummyunerase);


        }
        System.out.println(dummyBitmap);
        return clippedBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        CANVAS = canvas;
        if (ZOOM_PROGRESS != 0 && !TOUCH) {
            Matrix matrix2 = new Matrix();
            matrix2.reset();
            matrix2.postScale(ZOOM_PROGRESS, ZOOM_PROGRESS, zoomPos.x, zoomPos.y);
            canvas.drawBitmap(bm, matrix2, mMaskPaint);

            canvas.drawBitmap(drawBitmap(bm), matrix2, mBitmapPaint);
        } else {
            canvas.drawBitmap(bm, matrix, mMaskPaint);
            canvas.drawBitmap(drawBitmap(bm), matrix, mBitmapPaint);
        }
        if (mode == MAGIC_MODE || mode == MAGIC_MODE_RESTORE || mode == ERASE_MODE || mode == UNERASE_MODE) {
            canvas.drawBitmap(magicPointer, drawingPoint.x - magicPointer.getWidth() / 2, drawingPoint.y - magicPointer.getHeight() / 2, mMaskPaint);
        }
        super.onDraw(canvas);
    }

    public void saveLastMaskData() {

        clippedBitmap.getPixels(lastBitmapData, 0, clippedBitmap.getWidth(), 0, 0, clippedBitmap.getWidth(), clippedBitmap.getHeight());
        dummyBitmap.getPixels(lastBitmapData, 0, dummyBitmap.getWidth(), 0, 0, dummyBitmap.getWidth(), dummyBitmap.getHeight());
        System.out.println(clippedBitmap);
        System.out.println(dummyBitmap);
    }

    public void resetPath() {
        mPath.reset();
        mPathErase.reset();
    }

    public void invalidateView() {
        invalidate();
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPathErase.reset();

        if (mode == ERASE_MODE) mPath.moveTo(x, y);
        else mPathErase.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            if (mode == ERASE_MODE) mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            else mPathErase.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        if (mode == ERASE_MODE) mPath.lineTo(mX, mY);
        else mPathErase.lineTo(mX, mY);
    }

    PointF DownPT = new PointF();
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    float SCALE = 1.0f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        zoomPos.x = (int) event.getX();
        zoomPos.y = (int) event.getY();
        setMotionEvent(event);
        if (mode == ERASE_MODE || mode == UNERASE_MODE) {
            y = y - POINTER_DISTANCE;
        }

        if (mode == MAGIC_MODE || mode == MAGIC_MODE_RESTORE || mode == ERASE_MODE || mode == UNERASE_MODE) {
            drawingPoint.x = x;
            drawingPoint.y = y;
        }
        if (mode != MOVING_MODE) {
            float[] v = new float[9];
            matrix.getValues(v);
            float mScalingFactor = v[Matrix.MSCALE_X];
            RectF r = new RectF();
            matrix.mapRect(r);
            float scaledX = (x - r.left);
            float scaledY = (y - r.top);

            scaledX /= mScalingFactor;
            scaledY /= mScalingFactor;

            x = scaledX;
            y = scaledY;
        }
        int maskedAction = event.getActionMasked();
        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                touchMode = DRAG;
                if (mode == ERASE_MODE || mode == UNERASE_MODE) {
                    touch_start(x, y);
                } else if (mode == MOVING_MODE) {
                    DownPT.x = event.getX();
                    DownPT.y = event.getY();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchMode == DRAG) {
                    if (mode == ERASE_MODE || mode == UNERASE_MODE) touch_move(x, y);
                    else if (mode == MOVING_MODE) {
                        PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                        matrix.postTranslate(mv.x, mv.y);
                        DownPT.x = event.getX();
                        DownPT.y = event.getY();
                    } else if (mode == MAGIC_MODE || mode == MAGIC_MODE_RESTORE) {
                        touchPoint.x = x;
                        touchPoint.y = y;
                    }
                    invalidate();
                } else if (touchMode == ZOOM && mode == MOVING_MODE) {
                    draw(CANVAS);
                    setZoom1(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mode == ERASE_MODE || mode == UNERASE_MODE) {
                    touch_up();
                    Log.d(TAG, "add to stack");
                    addToStack(false);
                } else if (mode == MAGIC_MODE || mode == MAGIC_MODE_RESTORE) {
                    touchPoint.x = x;
                    touchPoint.y = y;
                    saveLastMaskData();
//                    ((ObjectRemover) mContext).resetSeekBar();
                }
//                ((ObjectRemover) mContext).updateUndoButton();
//                ((ObjectRemover) mContext).updateRedoButton();
                invalidate();
                resetPath();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                touchMode = NONE;
                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                draw(CANVAS);
                setZoom2(event);
                break;
        }
        return true;
    }

    private void setMotionEvent(MotionEvent event) {
        motionEvent = event;
    }

    private void setZoom1(MotionEvent event) {
        TOUCH = true;
        float newDist = spacing(event);
        Log.d(TAG, "newDist=" + newDist);
        if (newDist > 5f) {
            matrix.set(savedMatrix);
            SCALE = newDist / oldDist;
            matrix.postScale(SCALE, SCALE, mid.x, mid.y);
            Log.d(TAG, "scale =" + SCALE);
        }
        invalidate();
    }

    private void setZoom2(MotionEvent event) {
        TOUCH = true;
        oldDist = spacing(event);
        if (oldDist > 5f) {
            savedMatrix.set(matrix);
            midPoint(mid, event);
            touchMode = ZOOM;
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void saveDrawnBitmap() {

        Bitmap saveBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas cv = new Canvas(saveBitmap);
        cv.save();
        cv.drawBitmap(bm, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cv.drawBitmap(clippedBitmap, 0, 0, paint);
        savedBitmap = Bitmap.createScaledBitmap(saveBitmap, (int) (saveBitmap.getWidth() * 0.8), (int) (saveBitmap.getHeight() * 0.8), true);
    }

    public void saveTheDrawBitmap() {
        // Get the app name
        Bitmap tosaveBitmap = dummyBitmap;
        String appName = getResources().getString(R.string.app_name);

        // Create a folder with the app name
        File folder = new File(Environment.getExternalStorageDirectory(), appName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Create a file with the current time in seconds as the file name
        String fileName = System.currentTimeMillis() / 1000 + ".jpg";
        File file = new File(folder, fileName);

        saveImageToFile(tosaveBitmap, file);

    }

    private void saveImageToFile(Bitmap bitmap, File file) {

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            // Show a success message or take further actions
            Toast.makeText(mContext, "Bitmap saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show an error message or take appropriate action
            Toast.makeText(mContext, "Failed to save bitmap", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveHere() {
//        Date now = new Date();
        java.util.Date now = new java.util.Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            String filename = "screenshot_" + System.currentTimeMillis() + ".png";
            FileOutputStream outputStream;
            try {
                // Get the directory to save the file to
                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                // Create a new file in the directory
                File file = new File(directory, filename);
                // Open an output stream to the file and compress the bitmap to PNG format
                outputStream = new FileOutputStream(file);
                dummyBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                // Flush and close the output stream
                outputStream.flush();
                outputStream.close();

                // Tell the media scanner to scan the file so it's available in the Gallery app
                MediaScannerConnection.scanFile(mContext, new String[]{file.getAbsolutePath()}, null, null);

                // Show a toast message to indicate success
                Toast.makeText(mContext, "Screenshot saved to Pictures directory", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    public void bitmapEcode() {
        Bitmap bitmap = dummyBitmap;
//        try {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedbit = Base64.encodeToString(byteArray, Base64.DEFAULT);
            System.out.println(encodedbit);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        String base64String = "your_base64_encoded_string_here";
        Bitmap bitmapdec =  decodeBase64(base64String);
        System.out.println(bitmapdec);

    }

    public static Bitmap decodeBase64(String base64String) {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);


    }

}
