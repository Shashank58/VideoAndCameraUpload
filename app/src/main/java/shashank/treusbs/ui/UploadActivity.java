package shashank.treusbs.ui;

import android.Manifest.permission;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import shashank.treusbs.NetworkHelper;
import shashank.treusbs.NetworkHelper.VideoResponse;
import shashank.treusbs.NetworkHelper.VideoUpload;
import shashank.treusbs.R;
import shashank.treusbs.Upload;
import shashank.treusbs.util.AppUtils;
import shashank.treusbs.util.SharedPreferenceHandler;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener,
        VideoResponse, VideoUpload, ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = "Upload Activity";
    private FloatingActionButton uploadVideoFab;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
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
    private ProgressBar progressBar;
    private View tint;
    private Firebase myFireBaseRef;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        myFireBaseRef = new Firebase("https://treusbs.firebaseio.com/");
        isAdmin = getIntent().getBooleanExtra(IS_ADMIN, false);
        if (!isAdmin) {
            setContentView(R.layout.activity_upload);

            fetchViews();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tint = findViewById(R.id.tint);
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
        } else {
            captureVideo();
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

            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkForPermissionAndSetLocation();
                } else {
                    AppUtils.getInstance().showAlertDialog(UploadActivity.this,
                            "Need location permissions");
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
                    if (mLastLocation == null) {
                        new AlertDialog.Builder(this)
                                .setTitle("TREUSBS")
                                .setMessage("Please enable location services before uploading offence")
                                .setPositiveButton(android.R.string.yes, new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkForPermissionAndSetLocation();
                                    }
                                }).create().show();
                    } else {
                        uploadOffence.setVisibility(View.GONE);
                        uploadVideoFab.setVisibility(View.GONE);
                        new NetworkHelper().uploadVideo(UploadActivity.this, contentUri
                                , this, progressBar, tint);
                    }
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
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to log out")
                .setPositiveButton(android.R.string.yes, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SharedPreferenceHandler().deleteAllData(UploadActivity.this);
                        startActivity(new Intent(UploadActivity.this, SignInActivity.class));
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
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

    protected void onStart() {
        super.onStart();
        if (!isAdmin)
            mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (!isAdmin)
            mGoogleApiClient.disconnect();
    }

    @Override
    public void allVideosReceived(List<Upload> allVideos) {
        if (allVideos != null) {
            videosAdapter = new VideosAdapter(this, allVideos, myFireBaseRef);
            listOfVideos.setAdapter(videosAdapter);
        }
    }

    @Override
    public void uploadResponse(boolean isVideoUploaded, String url) {
        uploadOffence.setVisibility(View.VISIBLE);
        uploadVideoFab.setVisibility(View.VISIBLE);
        if (isVideoUploaded) {
            if (mLastLocation == null) {
                new AlertDialog.Builder(this)
                        .setTitle("TREUSBS")
                        .setMessage("Please enable location services before uploading offence")
                        .setPositiveButton(android.R.string.yes, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkForPermissionAndSetLocation();
                            }
                        }).create().show();
            } else {
                storeOffenceInfo(url);
            }
        }
    }

    private void storeOffenceInfo(String url) {
        String name = new SharedPreferenceHandler().getUserName(this);
        Firebase videos = myFireBaseRef.child("videos").child(contentUri.getLastPathSegment());

        Calendar cal = Calendar.getInstance();

        String time= new SimpleDateFormat("dd MMM yyyy hh:mm a").format(cal.getTime());
        Upload upload = new Upload(url, time, name , licencePlate.getText().toString().trim(),
                mLastLocation.getLatitude(), mLastLocation.getLongitude());
        videos.setValue(upload);

        capturedVideo.setImageResource(0);
        contentUri = null;
        capturedVideo.setClickable(false);
        playImage.setVisibility(View.GONE);

        uploadOffence.setAlpha(0.4f);
        uploadOffence.setClickable(false);

        deleteVideo.setVisibility(View.GONE);

        realPathVideo = null;

        licencePlate.setText("");
    }

    private void checkForPermissionAndSetLocation() {
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkForPermissionAndSetLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
