package shashank.treusbs;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{
    private FloatingActionButton uploadFab, uploadImageFab, uploadVideoFab;
    private static boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private static final int PIC_CAPTURED = 0;
    private static final int REQUEST_VIDEO_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadFab = (FloatingActionButton) findViewById(R.id.upload);
        uploadImageFab = (FloatingActionButton) findViewById(R.id.cameraUpload);
        uploadVideoFab = (FloatingActionButton) findViewById(R.id.videoUpload);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.roate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        uploadFab.setOnClickListener(this);
        uploadImageFab.setOnClickListener(this);
        uploadVideoFab.setOnClickListener(this);
    }

    public void animateFAB(){
        if(isFabOpen){
            uploadFab.startAnimation(rotate_backward);
            uploadImageFab.startAnimation(fab_close);
            uploadVideoFab.startAnimation(fab_close);
            uploadImageFab.setClickable(false);
            uploadVideoFab.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {
            uploadFab.startAnimation(rotate_forward);
            uploadImageFab.startAnimation(fab_open);
            uploadVideoFab.startAnimation(fab_open);
            uploadImageFab.setClickable(true);
            uploadVideoFab.setClickable(true);
            isFabOpen = true;
            Log.d("Raj","open");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upload:
                animateFAB();
                break;

            case R.id.cameraUpload:
                Intent intent = new Intent(android.provider
                        .MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, PIC_CAPTURED);
                break;

            case R.id.videoUpload:
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case PIC_CAPTURED:
                    break;

                case REQUEST_VIDEO_CAPTURE:
                    break;
            }
        }
    }
}
