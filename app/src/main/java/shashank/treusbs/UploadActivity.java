package shashank.treusbs;

import android.Manifest.permission;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import shashank.treusbs.util.AppUtils;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton uploadFab, uploadImageFab, uploadVideoFab;
    private static boolean isFabOpen = false;
    private View blackTint;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private static final int PIC_CAPTURED = 0;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int CROP_IMAGE = 2;
    private int x1, y1, x2, y2;
    private TessBaseAPI baseApi;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory()
            + "/tesseract-ocr";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;
    private EditText licencePlate;
    private VideoView capturedVideo;
    private ImageView playImage;
    private int pausedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        checkPermissions();
        fetchViews();
        fetchAnimations();

        uploadFab.setOnClickListener(this);
        uploadImageFab.setOnClickListener(this);
        uploadVideoFab.setOnClickListener(this);
        playImage.setOnClickListener(this);
        playImage.setClickable(false);
    }

    private void fetchViews() {
        uploadFab = (FloatingActionButton) findViewById(R.id.upload);
        uploadImageFab = (FloatingActionButton) findViewById(R.id.cameraUpload);
        uploadVideoFab = (FloatingActionButton) findViewById(R.id.videoUpload);
        licencePlate = (EditText) findViewById(R.id.licence_plate);
        capturedVideo = (VideoView) findViewById(R.id.captured_video);
        playImage = (ImageView) findViewById(R.id.play_image);
        blackTint = findViewById(R.id.black_tint);
    }

    private void fetchAnimations() {
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.roate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
    }

    private void initializeTesseract() {
        copyAssets();
        baseApi = new TessBaseAPI();
        baseApi.init(DATA_PATH, "eng");
    }

    private void checkPermissions() {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            boolean readStoragePermission = ContextCompat.checkSelfPermission(this,
                    permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
            boolean writeStoragePermission = ContextCompat.checkSelfPermission(this,
                    permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
            if (readStoragePermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else if (writeStoragePermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                initializeTesseract();
            }
        }
    }

    public void animateFAB() {
        if (isFabOpen) {
            uploadFab.startAnimation(rotate_backward);
            uploadImageFab.startAnimation(fab_close);
            uploadVideoFab.startAnimation(fab_close);
            uploadImageFab.setClickable(false);
            uploadVideoFab.setClickable(false);
            isFabOpen = false;

        } else {
            uploadFab.startAnimation(rotate_forward);
            uploadImageFab.startAnimation(fab_open);
            uploadVideoFab.startAnimation(fab_open);
            uploadImageFab.setClickable(true);
            uploadVideoFab.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                } else {
                    AppUtils.getInstance().showAlertDialog(UploadActivity.this,
                            getString(R.string.need_read_permissions));
                }
                break;

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                } else {
                    AppUtils.getInstance().showAlertDialog(UploadActivity.this,
                            getString(R.string.need_write_permissions));
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload:
                animateFAB();
                if (blackTint.getVisibility() == View.GONE) {
                    blackTint.setVisibility(View.VISIBLE);
                    blackTint.setClickable(true);
                    blackTint.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            blackTint.setVisibility(View.GONE);
                            blackTint.setClickable(false);
                            animateFAB();
                        }
                    });
                } else {
                    blackTint.setVisibility(View.GONE);
                    blackTint.setClickable(false);
                }
                break;

            case R.id.cameraUpload:
                animateFAB();
                blackTint.setVisibility(View.GONE);
                Intent intent = new Intent(android.provider
                        .MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, PIC_CAPTURED);
                break;

            case R.id.videoUpload:
                animateFAB();
                blackTint.setVisibility(View.GONE);
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 8);
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
                break;

            case R.id.play_image:
                Log.d("Upload Activity", "Is invisible? " + (playImage.getVisibility() == View.INVISIBLE));
                if (playImage.getVisibility() == View.INVISIBLE) {
                    Log.d("Upload Activity", "Entering here");
                    playImage.setVisibility(View.VISIBLE);
                    pausedPlace = capturedVideo.getCurrentPosition();
                    capturedVideo.pause();
                } else if (playImage.getVisibility() == View.VISIBLE) {
                    Log.d("Upload Activity", "What about here");
                    playImage.setVisibility(View.INVISIBLE);
                    capturedVideo.seekTo(pausedPlace);
                    capturedVideo.start();
                    playImage.setClickable(true);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PIC_CAPTURED:
                    performCrop(data.getData());
                    break;

                case REQUEST_VIDEO_CAPTURE:
                    Log.d("Upload Activity", "Is it entering here?");
                    capturedVideo.setVideoURI(data.getData());
                    playImage.setVisibility(View.VISIBLE);
                    playImage.setClickable(true);
                    pausedPlace = 0;
                    break;

                case CROP_IMAGE:
                    Bundle extras = data.getExtras();
                    Bitmap croppedPic = extras.getParcelable("data");
                    textExtraction(croppedPic);
                    break;
            }
        }
    }

    private void performCrop(Uri bp) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(bp, "image/*");

            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", x1);
            cropIntent.putExtra("aspectY", y1);
            cropIntent.putExtra("outputX", x2);
            cropIntent.putExtra("outputY", y2);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP_IMAGE);
        } catch (ActivityNotFoundException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Your device doesnot support cropping an image", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    //Creating tesseract-ocr directory in user's sdcard
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        String name = "/tessdata/";
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            // Checking file on assets subfolder
            boolean success = false;
            try {
                files = assetManager.list("Files");
                File folder = new File(DATA_PATH);
                File subFolder = new File(folder + "/tessdata");

                if (!folder.exists()) {
                    Log.d("Upload Activity", "Is it entering?");
                    folder.mkdir();
                    subFolder.mkdir();
                    success = true;
                }
            } catch (IOException e) {
                Log.e("ERROR", "Failed to get asset file list.", e);
            }
            if (success) {
                // Analyzing all file on assets subfolder
                for (String filename : files) {
                    InputStream in = null;
                    OutputStream out = null;
                    // First: checking if there is already a target folder

                    // Moving all the files on external SD
                    try {
                        Log.e("MainActivity", "Creating again?");
                        in = assetManager.open("Files/" + filename);
                        Log.d("Upload Activity", "Coming here now?");
                        out = new FileOutputStream(DATA_PATH + name + filename);
                        copyFile(in, out);
                    } catch (IOException e) {
                        Log.e("ERROR", "Failed to copy asset file: " + filename, e);
                    } finally {
                        // Edit 3 (after MMs comment)
                        try {
                            if (in != null) {
                                in.close();
                            }
                            in = null;
                            if (out != null) {
                                out.flush();
                                out.close();
                            }
                            out = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            Log.e("Main Activity", "Change user permissions to write and read!");
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            // is to know is we can neither read nor write
            Toast toast = Toast.makeText(getApplicationContext(), "Error files couldnot be loaded!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void textExtraction(Bitmap croppedPic) {
        int width, height;
        height = croppedPic.getHeight();
        width = croppedPic.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(croppedPic, 0, 0, paint);

        baseApi.setImage(croppedPic);
        baseApi.setImage(baseApi.getThresholdedImage());
        String recognizedText = baseApi.getUTF8Text();
        recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9\\s]+", "");
        licencePlate.setText(recognizedText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baseApi.end();
    }
}
