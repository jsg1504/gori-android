package com.mozible.gori.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mozible.gori.models.Content;
import com.mozible.gori.models.User;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.utils.GoriPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by JunLee on 7/19/16.
 */
public class MainInfoTask extends AsyncTask<Integer, String, List<Content>> {

    public interface TaskListener {
        public void onPreExecute();

        public void onPostExecute(GoriConstants.STATUS STATUS, List<Content> contentList);

        public void onCanceled();

    }

    private Activity mActivity;
    private String mDate;
    private TaskListener mTaskListener;
    private String mSession;

    public MainInfoTask(Activity activity, String date, TaskListener taskListener) {
        mActivity = activity;
        mDate = date;
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
            String jsonResult = requestMainInfo(mDate, mSession);
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
    public String requestMainInfo(String date, String session) {
        String result = null;
        try {
            URL url;
            if(date != null) {
                url = new URL(GoriConstants.getMainInfoURL(date));
            } else {
                url = new URL(GoriConstants.getMainInfoURL());
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
