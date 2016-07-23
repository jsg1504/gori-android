package com.mozible.gori.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mozible.gori.models.Content;
import com.mozible.gori.models.UserProfile;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.utils.GoriPreferenceManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JunLee on 7/19/16.
 */
public class UserDetailInfoTask extends AsyncTask<Integer, String, UserDetailInfoResult> {

    public interface TaskListener {
        public void onPreExecute();

        public void onPostExecute(GoriConstants.STATUS STATUS, int user_content_count, int user_following_count, int user_follower_count);

        public void onCanceled();

    }

    private Activity mActivity;
    private String mUsername;
    private TaskListener mTaskListener;
    private String mSession;

    public UserDetailInfoTask(Activity activity, String username, TaskListener taskListener) {
        mActivity = activity;
        mUsername = username;
        mTaskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        if (mTaskListener != null) {
            mTaskListener.onPreExecute();
        }
        mSession = GoriPreferenceManager.getInstance(mActivity).getSession();
        super.onPreExecute();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected UserDetailInfoResult doInBackground(Integer... arg0) {
        UserDetailInfoResult result = null;
        try {
            String jsonResult = requestUserDetail(mUsername, mSession);
            result = UserDetailInfoResult.getFromJSonObject(jsonResult);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(UserDetailInfoResult result) {
        if(mTaskListener != null) {
            if (result != null) {
                mTaskListener.onPostExecute(GoriConstants.STATUS.SUCCESS, result.user_content_count, result.user_following_count, result.user_follower_count);
            } else {
                mTaskListener.onPostExecute(GoriConstants.STATUS.ETC_ERROR, -1, -1, -1);
            }
        }
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        if (mTaskListener != null) {
            mTaskListener.onCanceled();
        }
        super.onCancelled();
    }
    public String requestUserDetail(String username, String session) {
        String result = null;
        try {
            URL url;
            url = new URL(GoriConstants.getUserDetailURL(username));
            HashMap<String, String> params = new HashMap<String, String>();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Cookie", session);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            result = builder.toString();
            reader.close();
            conn.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return result;
    }
}
class UserDetailInfoResult {
    public String status;
    public int user_content_count;
    public int user_following_count;
    public int user_follower_count;

    public static UserDetailInfoResult getFromJSonObject(String jsonObject) {
        UserDetailInfoResult result = null;

        Gson gson = new Gson();
        Type resultType = new TypeToken<UserDetailInfoResult>() {
        }.getType();
        result = gson.fromJson(jsonObject, resultType);

        return result;
    }
}
