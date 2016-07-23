package com.mozible.gori.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.utils.GoriPreferenceManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by JunLee on 7/22/16.
 */
public class ContentImageUploadTask2 extends AsyncTask<Integer, String, ContentImageUploadResult2>{

    public interface TaskListener {
        public void onPreExecute();

        public void onPostExecute(GoriConstants.STATUS STATUS, int contentId, int errorCode);

        public void onCanceled();

    }

    private Activity mActivity;
    private int mContentId;
    private Bitmap mContentImage;
    private TaskListener mTaskListener;
    private String mSession;

    public ContentImageUploadTask2(Activity activity, int contentId, Bitmap contentImage, TaskListener taskListener) {
        mActivity = activity;
        mContentId = contentId;
        mContentImage = contentImage;
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
    protected ContentImageUploadResult2 doInBackground(Integer... arg0) {
        ContentImageUploadResult2 result = null;
        try {
            String jsonResult = requestUpload2(mContentId, mContentImage);
            result = ContentImageUploadResult2.getFromJSonObject(jsonResult);
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
    protected void onPostExecute(ContentImageUploadResult2 result) {
        if (mTaskListener != null && result != null) {
            if(result.status != null) {
                if(result.status.equals(GoriConstants.STATUS.SUCCESS.name().toLowerCase())) {
                    mTaskListener.onPostExecute(GoriConstants.STATUS.SUCCESS, mContentId, result.error_code);
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
    public String requestUpload2(int contentId, Bitmap contentImage) {
        String result = null;
        try {

        }catch (Exception ex) {
            ex.printStackTrace();
        }


        return result;
    }
/*
{
  "status": "success",
  "content_id": 12,
  "desc": "image upload"
}
 */
}
class ContentImageUploadResult2 {
    public String status;
    public int content_id;
    public String desc;
    public int error_code;
    public static ContentImageUploadResult2 getFromJSonObject(String jsonObject) {
        ContentImageUploadResult2 result = null;

        Gson gson = new Gson();
        Type resultType = new TypeToken<ContentImageUploadResult2>() {
        }.getType();
        result = gson.fromJson(jsonObject, resultType);

        return result;
    }
}