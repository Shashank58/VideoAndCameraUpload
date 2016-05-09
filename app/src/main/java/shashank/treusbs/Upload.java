package shashank.treusbs;

/**
 * Created by shashankm on 30/04/16.
 */
public class Upload {
    private String videoPath;
    private String timeStamp;
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
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
