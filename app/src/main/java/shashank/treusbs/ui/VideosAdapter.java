package shashank.treusbs.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import shashank.treusbs.R;
import shashank.treusbs.Upload;

/**
 * Created by shashankm on 30/04/16.
 */
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {
    private List<Upload> uploadList;
    private Activity activity;

    public VideosAdapter(Activity activity, List<Upload> uploadList) {
        this.activity = activity;
        this.uploadList = new ArrayList<>(uploadList);
    }

    @Override
    public VideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.video_card, parent, false);



        return new VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideosViewHolder holder, int position) {
        final Upload upload = uploadList.get(position);

        holder.nameOfUploader.setText(upload.getUploaderName());
        holder.licenceNumber.setText(upload.getRegistrationNumber());
        Glide.with(activity).load(upload.getThumbnail())
                .asBitmap().into(holder.videoThumbnail);

        holder.videoCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PlayVideoActivity.class);
                intent.putExtra("Video id", upload.getVideoId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    public class VideosViewHolder extends  RecyclerView.ViewHolder{
        protected ImageView videoThumbnail;
        protected TextView nameOfUploader;
        protected TextView timeStamp;
        protected TextView licenceNumber;
        protected CardView videoCard;

        public VideosViewHolder(View itemView) {
            super(itemView);

            videoThumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            nameOfUploader = (TextView) itemView.findViewById(R.id.name_of_uploader);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
            licenceNumber = (TextView) itemView.findViewById(R.id.licence_number);
            videoCard = (CardView) itemView.findViewById(R.id.individual_card);
        }
    }
}
