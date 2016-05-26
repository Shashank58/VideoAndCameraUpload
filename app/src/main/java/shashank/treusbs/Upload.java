package shashank.treusbs;

/**
 * Created by shashankm on 30/04/16.
 */
public class Upload {
    private static final String TAG = "Upload";
    String videoPath;
    String date;
    String uploaderName;
    String registrationNumber;
    double latitude;
    double longitude;
    String id;

    public Upload(String videoPath, String date, String uploaderName, String registrationNumber,
                  double latitude, double longitude) {
        this.videoPath = videoPath;
        this.uploaderName = uploaderName;
        this.registrationNumber = registrationNumber;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
    }

    public Upload() {
        //Empty constructor for fire base
    }

    public String getVideoPath() {
        return videoPath;
    }

    public String getDate() {
        return date;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
