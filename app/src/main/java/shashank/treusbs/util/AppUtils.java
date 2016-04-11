package shashank.treusbs.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by shashankm on 14/02/16.
 */
public class AppUtils {
    private static AppUtils instance;
    private static final String APP_NAME = "Smart Enforcement";
    private File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "MyCameraVideo");
    private static final String VIDEO_FILE_NAME = "MyVid.mp4";

    public static AppUtils getInstance(){
        if (instance == null){
            instance = new AppUtils();
        }
        return instance;
    }

    public void showAlertDialog(Activity activity, String message){
        new AlertDialog.Builder(activity)
                        .setTitle(APP_NAME)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
    }

    public Uri getOutputFromUri(Activity activity){
        return Uri.fromFile(getOutputMediaFile(activity));
    }

    private File getOutputMediaFile(Activity activity){
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Toast.makeText(activity, "Failed to create directory MyCameraVideo.",
                        Toast.LENGTH_LONG).show();

                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }

        return new File(mediaStorageDir.getPath() + File.separator + VIDEO_FILE_NAME);
    }
}
