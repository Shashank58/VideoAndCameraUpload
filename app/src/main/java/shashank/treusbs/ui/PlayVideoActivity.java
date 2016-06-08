package shashank.treusbs.ui;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import shashank.treusbs.R;

public class PlayVideoActivity extends AppCompatActivity {
    private static final String TAG = "Play Video Activity";
    private VideoView videoView;
    private ProgressBar videoLoadingLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        videoView = (VideoView) findViewById(R.id.video_view);
        videoLoadingLoader = (ProgressBar) findViewById(R.id.video_loading_loader);

        videoLoadingLoader.setVisibility(View.VISIBLE);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        String url = getIntent().getStringExtra("Video url");
        videoView.setVideoURI(Uri.parse(url));

        videoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoLoadingLoader.setVisibility(View.GONE);
                videoView.start();
                videoView.requestFocus();
            }
        });
    }
}
