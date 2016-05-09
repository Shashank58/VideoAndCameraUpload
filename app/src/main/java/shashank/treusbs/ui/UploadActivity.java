package shashank.treusbs.ui;

import android.Manifest.permission;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import shashank.treusbs.NetworkHelper;
import shashank.treusbs.NetworkHelper.VideoResponse;
import shashank.treusbs.R;
import shashank.treusbs.Upload;
import shashank.treusbs.util.AppUtils;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener,
        VideoResponse{
    private static final String TAG = "Upload Activity";
    private FloatingActionButton uploadVideoFab;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;
    public static final String IS_ADMIN = "Is admin";
    private EditText licencePlate;
    private ImageView capturedVideo;
    private ImageView playImage, deleteVideo;
    private Button uploadOffence;
    private Uri contentUri;
    private RecyclerView listOfVideos;
    private String realPathVideo;
    private VideosAdapter videosAdapter;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isAdmin = getIntent().getBooleanExtra(IS_ADMIN, false);
        if (!isAdmin) {
            setContentView(R.layout.activity_upload);

            fetchViews();

            uploadVideoFab.setOnClickListener(this);
            capturedVideo.setOnClickListener(this);
            uploadOffence.setOnClickListener(this);
            deleteVideo.setOnClickListener(this);

            capturedVideo.setClickable(false);
            uploadOffence.setClickable(false);
        } else {
            setContentView(R.layout.admin_layout);

            listOfVideos = (RecyclerView) findViewById(R.id.list_of_video);
            listOfVideos.setLayoutManager(new LinearLayoutManager(this));
            listOfVideos.setHasFixedSize(true);
            new NetworkHelper().getAllVideos(this, this);
        }
    }

    private void fetchViews() {
        uploadVideoFab = (FloatingActionButton) findViewById(R.id.videoUpload);
        licencePlate = (EditText) findViewById(R.id.licence_plate);
        capturedVideo = (ImageView) findViewById(R.id.captured_video);
        playImage = (ImageView) findViewById(R.id.play_image);
        uploadOffence = (Button) findViewById(R.id.upload_offence);
        deleteVideo = (ImageView) findViewById(R.id.delete_video);
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
                captureVideo();
            }
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

    private void captureVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 8);
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.videoUpload:
                checkPermissions();
                break;

            case R.id.delete_video:
                deleteCapturedVideo();
                break;

            case R.id.upload_offence:
                if (realPathVideo != null && licencePlate.getText().length() > 6) {
                    new NetworkHelper().uploadVideo(UploadActivity.this, new File(realPathVideo)
                            , new File(saveBitmap(bitmap)),licencePlate.getText().toString());
                } else {
                    AppUtils.getInstance().showAlertDialog(UploadActivity.this,
                            "Please enter proper licence plate number of the vehicle");
                }
                break;

            case R.id.captured_video:
                Intent intent = new Intent( Intent.ACTION_VIEW );
                intent.setDataAndType(contentUri, "video/mp4" );
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_VIDEO_CAPTURE:
                    if (VERSION.SDK_INT >= 19)
                        realPathVideo = AppUtils.getAbsolutePathPostKitkat(this, data.getData());
                    else
                        realPathVideo = AppUtils.getAbsolutePathPreKitKat(this, data.getData());
                    Glide.with(this).loadFromMediaStore(data.getData())
                            .asBitmap().centerCrop().into(capturedVideo);
                    bitmap = ThumbnailUtils.createVideoThumbnail
                            (realPathVideo, Thumbnails.FULL_SCREEN_KIND);

                    contentUri = data.getData();
                    capturedVideo.setClickable(true);
                    playImage.setVisibility(View.VISIBLE);

                    uploadOffence.setAlpha(1f);
                    uploadOffence.setClickable(true);

                    deleteVideo.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private String saveBitmap(Bitmap croppedImage) {
        Uri saveUri = null;

        String path = Environment.getExternalStorageDirectory().getPath()
                + "/Treusbs/image_thumbnail.jpg";
        File file = new File(path);
        OutputStream outputStream = null;
        try {
            if (file.exists()) {
                if (!file.delete()) {
                    Log.d(TAG, "saveBitmap: Delete failed");
                }
            }
            file.getParentFile().mkdirs();
            file.createNewFile();
            saveUri = Uri.fromFile(file);
            outputStream = getContentResolver().openOutputStream(saveUri);
            if (outputStream != null) {
                croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }
        } catch (IOException e) {
            // log the error
        }
        String realPathThumbnail;
        if (VERSION.SDK_INT >= 19)
            realPathThumbnail = AppUtils.getAbsolutePathPostKitkat(this, saveUri);
        else
            realPathThumbnail = AppUtils.getAbsolutePathPreKitKat(this, saveUri);

        return realPathThumbnail;
    }

    private void deleteCapturedVideo() {
        new Builder(this)
                        .setTitle("TREUSBS")
                        .setMessage("Are you sure you want to delete the video")
                        .setPositiveButton(android.R.string.ok, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                capturedVideo.setImageResource(0);
                                contentUri = null;
                                capturedVideo.setClickable(false);
                                playImage.setVisibility(View.GONE);

                                uploadOffence.setAlpha(0.4f);
                                uploadOffence.setClickable(false);

                                deleteVideo.setVisibility(View.GONE);

                                realPathVideo = null;
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
    }

    @Override
    public void allVideosReceived(List<Upload> allVideos) {
        videosAdapter = new VideosAdapter(this, allVideos);
        listOfVideos.setAdapter(videosAdapter);
    }
}
