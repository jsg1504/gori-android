package com.mozible.gori.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
 * Created by JunLee on 7/18/16.
 */

public class LoginTask extends AsyncTask<Integer, String, LoginResult> {

    public interface TaskListener {
        public void onPreExecute();

        public void onPostExecute(GoriConstants.STATUS STATUS, UserProfile userProfile, int errorCode);

        public void onCanceled();

    }

    private Activity mActivity;
    private String mUserName;
    private String mPassword;
    private String mSession;
    private TaskListener mTaskListener;

    public LoginTask(Activity activity, String username, String password, TaskListener taskListener) {
        mActivity = activity;
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
    protected LoginResult doInBackground(Integer... arg0) {
        LoginResult result = new LoginResult();
        String jsonResult = null;
        try {
            jsonResult = requestLogin(mUserName, mPassword);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            result.userProfile = UserProfile.getObjectFromJSonObject(jsonResult);
            if(result.userProfile.user == null) {
                result = LoginResult.getFromJSonObject(jsonResult);
            }
        } catch(Exception ex) {
            try {
                result = LoginResult.getFromJSonObject(jsonResult);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(LoginResult result) {
        if(mTaskListener != null) {
            if (result != null) {
                if(result.userProfile != null) {
                    GoriPreferenceManager.getInstance(mActivity).setSession(mSession);
                    mTaskListener.onPostExecute(GoriConstants.STATUS.SUCCESS, result.userProfile, result.error_code);
                }
                else if(result.status != null && result.status.equals(GoriConstants.STATUS.ERROR.name().toLowerCase())) {
                    mTaskListener.onPostExecute(GoriConstants.STATUS.ERROR, null, result.error_code);
                } else {
                    mTaskListener.onPostExecute(GoriConstants.STATUS.ERROR, null, -1);
                }
            } else {
                mTaskListener.onPostExecute(GoriConstants.STATUS.ETC_ERROR, null, 0);
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
    public String requestLogin(String username, String password) {
        String result = null;
        try {
            URL url = new URL(GoriConstants.getLoginURL());
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            params.put("password", password);

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
            List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
            if(cookies != null && cookies.size() > 1) {
                mSession = cookies.get(1);
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
class LoginResult {
    public String status;
    public int error_code;
    public String desc;
    public UserProfile userProfile;
    public static LoginResult getFromJSonObject(String jsonObject) {
        LoginResult result = null;

        Gson gson = new Gson();
        Type resultType = new TypeToken<LoginResult>() {
        }.getType();
        result = gson.fromJson(jsonObject, resultType);

        return result;
    }
}