package shashank.treusbs.util;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by shashankm on 14/02/16.
 */
public class HashPassword {
    private String SHAHash;
    public static int NO_OPTIONS = 0;
    private static HashPassword instance;

    public static HashPassword getInstance(){
        if (instance == null){
            instance = new HashPassword();
        }
        return instance;
    }

    public String computeSHAHash(String password) {
        MessageDigest mdSha1 = null;
        try
        {
            mdSha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e1) {
            Log.e("Hash Password", "Error initializing SHA1 message digest");
        }
        try {
            mdSha1.update(password.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] data = mdSha1.digest();
        try {
            SHAHash = convertToHex(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("Sign Up Activity", "SHA-1 hash generated is: " + " " + SHAHash);
        return SHAHash;
    }

    private String convertToHex(byte[] data) throws java.io.IOException {
        StringBuilder sb = new StringBuilder();

        String hex= Base64.encodeToString(data, 0, data.length, NO_OPTIONS);

        sb.append(hex);

        return sb.toString();
    }
}
