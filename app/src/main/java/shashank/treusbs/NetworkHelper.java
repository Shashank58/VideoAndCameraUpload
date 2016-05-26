package shashank.treusbs;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.util.ArrayList;
import java.util.List;

import shashank.treusbs.util.AppUtils;

/**
 * Created by shashankm on 30/04/16.
 */
public class NetworkHelper {
    private static final String TAG = "Network Helper";

    public interface VideoResponse {
        void allVideosReceived(List<Upload> allVideos);
    }

    public interface PlayVideo {
        void videoFetched(String videoPath);
    }

    public interface VideoUpload {
        void uploadResponse(boolean isVideoUploaded, String url);
    }

    public void uploadVideo(final Activity activity, Uri videoFile, final VideoUpload
            videoUpload, final ProgressBar progressBar, final View tint) {
        tint.setVisibility(View.VISIBLE);
        tint.setClickable(true);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://treusbs.appspot.com");
        StorageReference videoRef = storageRef.child("videos/" + videoFile.getLastPathSegment());
        UploadTask uploadTask = videoRef.putFile(videoFile);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                AppUtils.getInstance().showAlertDialog(activity, e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
            @Override
            public void onSuccess(TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                AppUtils.getInstance().showAlertDialog(activity, "Video uploaded successfully");
                Log.d(TAG, "onSuccess: Url - " + taskSnapshot.getDownloadUrl());
                Log.d(TAG, "onSuccess: File size - " + taskSnapshot.getTotalByteCount());
                videoUpload.uploadResponse(true, taskSnapshot.getDownloadUrl().toString());
                tint.setVisibility(View.GONE);
                tint.setClickable(false);
            }
        });
    }

    public void getAllVideos(final Activity activity, final VideoResponse videoResponse) {
        AppUtils.getInstance().showProgressDialog(activity, "Fetching...");

        Firebase ref = new Firebase("https://treusbs.firebaseio.com/videos");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Data - " + dataSnapshot.toString());
                ArrayList<Upload> allVideos = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: Child - " + child.toString());
                    Upload upload = child.getValue(Upload.class);
                    upload.setId(child.getKey());

                    allVideos.add(0, upload);
                }
                AppUtils.getInstance().dismissProgressDialog();
                videoResponse.allVideosReceived(allVideos);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                AppUtils.getInstance().dismissProgressDialog();
                videoResponse.allVideosReceived(null);
            }
        });
    }
}
