package shashank.treusbs;

import android.os.Bundle;
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
        }
    }
}
