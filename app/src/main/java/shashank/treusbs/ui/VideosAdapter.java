package shashank.treusbs.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Firebase.CompletionListener;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import shashank.treusbs.R;
import shashank.treusbs.Upload;
import shashank.treusbs.util.AppUtils;

/**
 * Created by shashankm on 30/04/16.
 */
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {
    private List<Upload> uploadList;
    private Activity activity;
    private Firebase myFirebaseRef;

    public VideosAdapter(Activity activity, List<Upload> uploadList, Firebase myFirebaseRef) {
        this.activity = activity;
        this.uploadList = new ArrayList<>(uploadList);
        this.myFirebaseRef = myFirebaseRef;
    }

    @Override
    public VideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.video_card, parent, false);



        return new VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideosViewHolder holder, final int position) {
        final Upload upload = uploadList.get(position);

        holder.nameOfUploader.setText("Uploader Name: " + upload.getUploaderName());
        holder.licenceNumber.setText("Registration Number: " + upload.getRegistrationNumber());

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(upload.getLatitude(), upload.getLongitude(), 1);
            String address = "";
            for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                address += addresses.get(0).getAddressLine(i);
                address += " ";
            }
            holder.location.setText("Location: " + address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.timeStamp.setText("Date: " + upload.getDate());

        holder.playVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PlayVideoActivity.class);
                intent.putExtra("Video url", upload.getVideoPath());
                activity.startActivity(intent);
            }
        });

        holder.deleteVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.getInstance().showProgressDialog(activity, "Deleting..");
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://treusbs.appspot.com");
                StorageReference videoRef = storageRef.child("videos/"+upload.getId());

                videoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Firebase videos = myFirebaseRef.child("videos").child(upload.getId());
                        videos.removeValue(new CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                AppUtils.getInstance().dismissProgressDialog();
                                uploadList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, uploadList.size());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AppUtils.getInstance().dismissProgressDialog();
                        AppUtils.getInstance().showAlertDialog(activity, "Network error");
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    public class VideosViewHolder extends  RecyclerView.ViewHolder{
        protected TextView nameOfUploader;
        protected TextView timeStamp, location;
        protected TextView licenceNumber;
        protected CardView videoCard;
        protected ImageView deleteVideo;
        protected LinearLayout playVideo;

        public VideosViewHolder(View itemView) {
            super(itemView);

            nameOfUploader = (TextView) itemView.findViewById(R.id.name_of_uploader);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
            licenceNumber = (TextView) itemView.findViewById(R.id.licence_number);
            videoCard = (CardView) itemView.findViewById(R.id.individual_card);
            deleteVideo = (ImageView) itemView.findViewById(R.id.delete_video);
            playVideo = (LinearLayout) itemView.findViewById(R.id.play_video);
            location = (TextView) itemView.findViewById(R.id.location);
        }
    }
}
