package com.mozible.gori.tasks;


import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mozible.gori.utils.GoriConstants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


/**
 * Created by JunLee on 7/18/16.
 */

public class SignUpTask extends AsyncTask<Integer, String, SignUpResult> {

    public interface TaskListener {
        public void onPreExecute();

        public void onPostExecute(GoriConstants.STATUS STATUS, String username, int errorCode);

        public void onCanceled();

    }

    private Activity mActivity;
    private String mEmail;
    private String mUserName;
    private String mPassword;
    private TaskListener mTaskListener;

    public SignUpTask(Activity activity, String email, String username, String password, TaskListener taskListener) {
        mActivity = activity;
        mEmail = email;
        mUserName = username;
        mPassword = password;
        mTaskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        if (mTaskListener != null) {
            mTaskListener.onPreExecute();
        }
        super.onPreExecute();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected SignUpResult doInBackground(Integer... arg0) {
        SignUpResult result = null;
        try {
            String jsonResult = requestSignUp(mEmail, mUserName, mPassword);
            result = SignUpResult.getFromJSonObject(jsonResult);
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
    protected void onPostExecute(SignUpResult result) {
        if (mTaskListener != null && result != null) {
            if(result.status != null) {
                if(result.status.equals(GoriConstants.STATUS.SUCCESS.name().toLowerCase())) {
                    mTaskListener.onPostExecute(GoriConstants.STATUS.SUCCESS, result.username, result.error_code);
                } else if(result.status.equals(GoriConstants.STATUS.ERROR.name().toLowerCase())) {
                    mTaskListener.onPostExecute(GoriConstants.STATUS.ERROR, "", result.error_code);
                }
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
    public String requestSignUp(String email, String username, String password) {
        String result = null;
        try {
            URL url = new URL(GoriConstants.getSignUpURL());
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("email", email);
            params.put("username", username);
            params.put("password", password);
            params.put("is_expert", "1");

            byte[] postDataBytes = GoriConstants.makePostDataBytes(params);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
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
class SignUpResult {
    public String status;
    public int error_code;
    public String desc;
    public String username;
    public static SignUpResult getFromJSonObject(String jsonObject) {
        SignUpResult result = null;

        Gson gson = new Gson();
        Type resultType = new TypeToken<SignUpResult>() {
        }.getType();
        result = gson.fromJson(jsonObject, resultType);

        return result;
    }
}