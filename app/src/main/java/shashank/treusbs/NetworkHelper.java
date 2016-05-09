package shashank.treusbs;

import android.app.Activity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import shashank.treusbs.util.AppUtils;
import shashank.treusbs.util.Constants;
import shashank.treusbs.util.SharedPreferenceHandler;

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

    public void uploadVideo(final Activity activity, File videoFile, File thumbnail,
                            String description) {
        if (activity != null)
            AppUtils.getInstance().showProgressDialog(activity, "Uploading...");
        String name = new SharedPreferenceHandler().getUserName(activity);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("description", description);
        try {
            params.put("thumbnail", thumbnail);
            params.put("video", videoFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        client.post(Constants.CREATE_VIDEO_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                AppUtils.getInstance().dismissProgressDialog();
                if (activity != null)
                    AppUtils.getInstance().showAlertDialog(activity, "Video uploaded successfully");
                Log.d(TAG, "onSuccess: Response is - " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppUtils.getInstance().dismissProgressDialog();
                if (activity != null)
                    AppUtils.getInstance().showAlertDialog(activity, "Network error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                AppUtils.getInstance().dismissProgressDialog();
                if (activity != null)
                    AppUtils.getInstance().showAlertDialog(activity, "Network error");
            }
        });
    }

    public void getAllVideos(final Activity activity, final VideoResponse videoResponse) {
        AppUtils.getInstance().showProgressDialog(activity, "Fetching...");

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Constants.GET_ALL_VIDEOS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                AppUtils.getInstance().dismissProgressDialog();
                Log.d(TAG, "onSuccess: All videos - " + response.toString());

                if (response.has("data")) {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        List<Upload> allVideos = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            Upload upload = new Upload();
                            JSONObject video = data.getJSONObject(i);
                            upload.setVideoId(video.getInt("id"));
                            upload.setUploaderName(video.getString("name"));
                            upload.setVideoPath(video.getString("video"));
                            upload.setRegistrationNumber(video.getString("description"));
                            upload.setTimeStamp(video.getString("created_at"));
                            upload.setThumbnail(video.getString("thumbnail"));
                            allVideos.add(upload);
                        }
                        videoResponse.allVideosReceived(allVideos);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppUtils.getInstance().dismissProgressDialog();
                if (activity != null)
                    AppUtils.getInstance().showAlertDialog(activity, "Network error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                AppUtils.getInstance().dismissProgressDialog();
                if (activity != null)
                    AppUtils.getInstance().showAlertDialog(activity, "Network error");
            }
        });
    }

    public void getIndividualVideo(final Activity activity, int id, final PlayVideo playVideo) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Constants.GET_INDIVIDUAL_VIDEO_URL + "?id="+String.valueOf(id);
        Log.d(TAG, "getIndividualVideo: Url - " + url);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess: " + response.toString());
                try {
                    playVideo.videoFetched(response.getString("video"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (activity != null)
                    AppUtils.getInstance().showAlertDialog(activity, "Network error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (activity != null)
                    AppUtils.getInstance().showAlertDialog(activity, "Network error");
            }
        });
    }
}
