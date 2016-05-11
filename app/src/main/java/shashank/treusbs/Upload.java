package shashank.treusbs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by shashankm on 30/04/16.
 */
public class Upload {
    private static final String TAG = "Upload";
    private String videoPath;
    private String date;
    private String uploaderName;
    private String registrationNumber;
    private int videoId;
    private String thumbnail;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = ("http://androidvideo.herokuapp.com" + videoPath).replace("\\", "");
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        String[] completeDate = date.split("T");

        String[] utcTime = completeDate[1].split("\\.");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date myDate = simpleDateFormat.parse(completeDate[0] + " " + utcTime[0]);
//            simpleDateFormat.setTimeZone(TimeZone.getDefault());
//            this.date = simpleDateFormat.format(myDate);
            String[] fullDate = myDate.toString().split("GMT");
            this.date = fullDate[0];
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = ("http://androidvideo.herokuapp.com" + thumbnail).replace("\\", "");
    }
}
