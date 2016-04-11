package shashank.treusbs.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by shashankm on 08/04/16.
 */
public class SharedPreferenceHandler {
    private static final String USER_KEY = "User Key";
    private static final int PRIVATE_MODE = 0;
    private static final String USER_ID = "User Id";
    private static final String USER_NAME = "User Name";
    private static final String USER_NUMBER = "User Number";

    public void storeUID(Context context, String userID) {
        SharedPreferences preferences = context.getSharedPreferences(USER_KEY, PRIVATE_MODE);
        Editor editor = preferences.edit();

        editor.putString(USER_ID, userID);

        editor.apply();
    }

    public String getUID(Context context) {
        SharedPreferences pref = context.getSharedPreferences(USER_KEY, PRIVATE_MODE);
        return pref.getString(USER_ID, null);
    }

    public void storeName(Context context, String userName) {
        SharedPreferences preferences = context.getSharedPreferences(USER_KEY, PRIVATE_MODE);
        Editor editor = preferences.edit();

        editor.putString(USER_NAME, userName);

        editor.apply();
    }

    public String getUserName(Context context) {
        SharedPreferences pref = context.getSharedPreferences(USER_KEY, PRIVATE_MODE);
        return pref.getString(USER_NAME, null);
    }

    public void storeNumber(Context context, String userNumber) {
        SharedPreferences preferences = context.getSharedPreferences(USER_KEY, PRIVATE_MODE);
        Editor editor = preferences.edit();

        editor.putString(USER_NUMBER, userNumber);

        editor.apply();
    }

    public String getUserNumber(Context context) {
        SharedPreferences pref = context.getSharedPreferences(USER_KEY, PRIVATE_MODE);
        return pref.getString(USER_NUMBER, null);
    }
}
