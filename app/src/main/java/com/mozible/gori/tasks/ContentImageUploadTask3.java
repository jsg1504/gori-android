package com.mozible.gori.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.utils.GoriPreferenceManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by JunLee on 7/22/16.
 */
public class ContentImageUploadTask3 extends AsyncTask<Integer, String, ContentImageUploadResult3> {

    public interface TaskListener {
        public void onPreExecute();

        public void onPostExecute(GoriConstants.STATUS STATUS, int contentId, int errorCode);

        public void onCanceled();

    }

    private Activity mActivity;
    private int mContentId;
    private String mDescription;
    private String mSession;
    private TaskListener mTaskListener;

    public ContentImageUploadTask3(Activity activity, int contentId, String description, TaskListener taskListener) {
        mActivity = activity;
        mContentId = contentId;
        mDescription = description;
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
    protected ContentImageUploadResult3 doInBackground(Integer... arg0) {
        ContentImageUploadResult3 result = null;
        try {
            String jsonResult = requestUpload3(mContentId, mDescription, mSession);
            result = ContentImageUploadResult3.getFromJSonObject(jsonResult);
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
    protected void onPostExecute(ContentImageUploadResult3 result) {
        if (mTaskListener != null && result != null) {
            if(result.status != null) {
                if(result.status.equals(GoriConstants.STATUS.SUCCESS.name().toLowerCase())) {
                    mTaskListener.onPostExecute(GoriConstants.STATUS.SUCCESS, result.content_id, result.error_code);
                } else if(result.status.equals(GoriConstants.STATUS.ERROR.name().toLowerCase())) {
                    mTaskListener.onPostExecute(GoriConstants.STATUS.ERROR, -1, result.error_code);
                } else {
                    mTaskListener.onPostExecute(GoriConstants.STATUS.ERROR, -1, -1);
                }
            } else {
                mTaskListener.onPostExecute(GoriConstants.STATUS.ERROR, -1, -1);
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
    public String requestUpload3(int contentId, String description, String session) {
        String result = null;
        try {
            URL url = new URL(GoriConstants.getContentUpload3URL(contentId));
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("description", description);
//            params.put("password", password);
//            params.put("is_expert", "1");

            byte[] postDataBytes = GoriConstants.makePostDataBytes(params);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setRequestProperty("Cookie", session);

            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
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

/*
{
  "status": "success",
  "content_id": 13,
  "desc": "content create"
}
 */
class ContentImageUploadResult3 {
    public String status;
    public int content_id;
    public String desc;
    public int error_code;
    public static ContentImageUploadResult3 getFromJSonObject(String jsonObject) {
        ContentImageUploadResult3 result = null;

        Gson gson = new Gson();
        Type resultType = new TypeToken<ContentImageUploadResult3>() {
        }.getType();
        result = gson.fromJson(jsonObject, resultType);

        return result;
    }
}