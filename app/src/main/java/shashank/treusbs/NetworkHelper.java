package shashank.treusbs;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

    public interface VideoUpload {
        void uploadResponse(boolean isVideoUploaded);
    }

    public void uploadVideo(final Activity activity, File videoFile, File thumbnail,
          String description, final VideoUpload videoUpload, final ProgressBar progressBar,
                            final View tint) {
        tint.setVisibility(View.VISIBLE);
        tint.setClickable(true);
        progressBar.setVisibility(View.VISIBLE);

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
                tint.setVisibility(View.GONE);
                tint.setClickable(false);
                progressBar.setVisibility(View.GONE);
                if (activity != null)
                    AppUtils.getInstance().showAlertDialog(activity, "Video uploaded successfully");
                Log.d(TAG, "onSuccess: Response is - " + response.toString());
                try {
                    videoUpload.uploadResponse(response.getBoolean("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                long progressPercentage = (long)100 * bytesWritten/totalSize;
                progressBar.setProgress((int) progressPercentage);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                tint.setVisibility(View.GONE);
                tint.setClickable(false);
                progressBar.setVisibility(View.GONE);
                if (activity != null)
                    AppUtils.getInstance().showAlertDialog(activity, "Network error");

                videoUpload.uploadResponse(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                tint.setVisibility(View.GONE);
                tint.setClickable(false);
                progressBar.setVisibility(View.GONE);
                if (activity != null)
                    AppUtils.getInstance().showAlertDialog(activity, "Network error");
                videoUpload.uploadResponse(false);
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
                            boolean isDuplicate = false;
                            Upload upload = new Upload();
                            JSONObject video = data.getJSONObject(i);
                            upload.setVideoId(video.getInt("id"));
                            upload.setUploaderName(video.getString("name"));
                            upload.setVideoPath(video.getString("video"));
                            upload.setRegistrationNumber(video.getString("description"));
                            upload.setDate(video.getString("created_at"));
                            Log.d(TAG, "onSuccess: Time - " + upload.getDate());
                            upload.setThumbnail(video.getString("thumbnail"));

                            for (Upload allVideo : allVideos) {
                                String currentVideo = upload.getDate().trim().substring(0,
                                        upload.getDate().indexOf(":"));
                                String existingVideo = allVideo.getDate().trim().substring(0,
                                        allVideo.getDate().indexOf(":"));
                                Log.d(TAG, "onSuccess: Current video - " + currentVideo);
                                Log.d(TAG, "onSuccess: Existing video - " + existingVideo);
                                if (currentVideo.trim().equalsIgnoreCase(existingVideo.trim())
                                        && allVideo.getRegistrationNumber().equalsIgnoreCase
                                        (upload.getRegistrationNumber())) {
                                    Log.d(TAG, "onSuccess: FAKE!");
                                    isDuplicate = true;
                                    break;
                                }
                            }
                            if (!isDuplicate)
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

    public void deleteVideo(int id) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.delete(Constants.GET_INDIVIDUAL_VIDEO_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess: Delete - " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }
}
