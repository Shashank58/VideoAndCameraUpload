package shashank.treusbs.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;

/**
 * Created by shashankm on 14/02/16.
 */
public class AppUtils {
    private static AppUtils instance;
    private static final String APP_NAME = "Smart Enforcement";

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
}
