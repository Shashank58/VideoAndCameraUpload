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

import shashank.treusbs.NetworkHelper;
import shashank.treusbs.NetworkHelper.PlayVideo;
import shashank.treusbs.R;

public class PlayVideoActivity extends AppCompatActivity implements PlayVideo{
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
        int id = getIntent().getIntExtra("Video id", -1);
        if (id != -1)
            new NetworkHelper().getIndividualVideo(this, id, this);
    }

    @Override
    public void videoFetched(String videoPath) {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setVideoURI(Uri.parse(("http://androidvideo.herokuapp.com"
                + videoPath).replace("\\", "")));

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
