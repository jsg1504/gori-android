package com.mozible.gori.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.mozible.gori.models.Content;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.utils.GoriPreferenceManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JunLee on 7/19/16.
 */
public class UserContentListTask extends AsyncTask<Integer, String, List<Content>> {

    public interface TaskListener {
        public void onPreExecute();

        public void onPostExecute(GoriConstants.STATUS STATUS, List<Content> contentList);

        public void onCanceled();

    }

    private Activity mActivity;
    private String mDate;
    private String mUsername;
    private TaskListener mTaskListener;
    private String mSession;

    public UserContentListTask(Activity activity, String date, String username, TaskListener taskListener) {
        mActivity = activity;
        mDate = date;
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
    protected List<Content> doInBackground(Integer... arg0) {
        List<Content> result = null;
        try {
            String jsonResult = requestContentListByUser(mDate, mUsername, mSession);
            result = Content.getListFromJSonObject(jsonResult);
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
    protected void onPostExecute(List<Content> result) {
        if(mTaskListener != null) {
            if (result != null) {
                mTaskListener.onPostExecute(GoriConstants.STATUS.SUCCESS, result);
            } else {
                mTaskListener.onPostExecute(GoriConstants.STATUS.ETC_ERROR, null);
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
    public String requestContentListByUser(String date, String username, String session) {
        String result = null;
        try {
            URL url;
            if(date != null) {
                url = new URL(GoriConstants.getContentListByUserURL(username, date));
            } else {
                url = new URL(GoriConstants.getContentListByUserURL(username));
            }
            HashMap<String, String> params = new HashMap<String, String>();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Cookie", session);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            result = builder.toString();
            reader.close();
            conn.disconnect();
        }catch (Exception ex) {
            ex.printStackTrace();
        }


        return result;
    }

}
