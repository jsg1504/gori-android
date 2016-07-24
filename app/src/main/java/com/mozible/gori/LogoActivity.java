package com.mozible.gori;

/**
 * Created by JunLee on 7/17/16.
 */

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mozible.gori.models.PostResult;
import com.mozible.gori.tasks.LoginTask;
import com.mozible.gori.models.UserProfile;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.utils.GoriPreferenceManager;
import com.mozible.gori.utils.GoriTextUtil;
import com.mozible.gori.utils.ServerInterface;
import com.mozible.gori.utils.UserDatabaseHelper;
import com.nispok.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LogoActivity extends AppCompatActivity implements ActivityGA{
    private LoginTask mLoginTask;
    private UserDatabaseHelper mUserDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String username = GoriPreferenceManager.getInstance(this).getUsername();
        String password = GoriPreferenceManager.getInstance(this).getPassword();

        GoriApplication.getInstance().buildServerinterface();
        ServerInterface api = GoriApplication.getInstance().getServerInterface();
        if(api != null) {

        } else {

        }
        mUserDatabaseHelper = new UserDatabaseHelper(this);
        mUserDatabaseHelper.deleteAllFollowingUser();

        if(!GoriTextUtil.isEmpty(username) && !GoriTextUtil.isEmpty(password)) {
            startLoginTask(username, password);
        } else {
            Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        Tracker tracker = ((GoriApplication) getApplication())
                .getTracker(GoriApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("LogoActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    private void startLoginTask(String username, String password) {
        mLoginTask = new LoginTask(this, username, password, mLoginTaskListener);
        mLoginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public LoginTask.TaskListener mLoginTaskListener = new LoginTask.TaskListener() {
        @Override
        public void onPreExecute() {
            sendGA("user", "login", "request");
        }

        @Override
        public void onPostExecute(GoriConstants.STATUS STATUS, UserProfile userProfile, int errorCode) {
            if(userProfile != null) {
                GoriPreferenceManager.getInstance(LogoActivity.this).setMyProfileObject(userProfile);
                Snackbar.with(LogoActivity.this)
                        .text("Connect")
                        .show(LogoActivity.this);
                sendGA("user", "login", "success");
                sendGA("user", "get following", "request");
                String session = GoriPreferenceManager.getInstance(LogoActivity.this).getSession();
                ServerInterface api = GoriApplication.getInstance().getServerInterface();
                api.getUserFollowings(session, userProfile.user.username, new Callback<ArrayList<UserProfile>>() {

                    @Override
                    public void success(ArrayList<UserProfile> s, Response response) {
                        if(s != null && s.size() > 0) {
                            try {
                                mUserDatabaseHelper.insertFollowingUsers(s);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        sendGA("user", "get following", "success");

                        Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                            showSnackbar("content image cancel failed!");
                        sendGA("user", "get following", "fail : " + error.getMessage());

                        Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } else if(STATUS == GoriConstants.STATUS.ERROR) {
                Intent intent;
                switch(errorCode) {
                    case 203:
                        sendGA("user", "login", "fail : Username / Password 가 일치하지 않습니다.");
                        Snackbar.with(LogoActivity.this)
                                .text("Username / Password 가 일치하지 않습니다.")
                                .show(LogoActivity.this);
                        intent = new Intent(LogoActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 204:
                        sendGA("user", "login", "fail : email 인증이 완료되지 않았습니다.");
                        Snackbar.with(LogoActivity.this)
                                .text("email 인증이 완료되지 않았습니다.")
                                .show(LogoActivity.this);
                        intent = new Intent(LogoActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case -1:
                        sendGA("user", "login", "fail : 네트워크가 원활하지 않습니다.");
                        Snackbar.with(LogoActivity.this)
                                .text("네트워크가 원활하지 않습니다.")
                                .show(LogoActivity.this);
                        intent = new Intent(LogoActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            } else {
                Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onCanceled() {
        }
    };

    @Override
    public void sendGA(String category, String action, String label) {
        //GA
        try {
            Tracker tracker = ((GoriApplication) getApplication())
                    .getTracker(GoriApplication.TrackerName.APP_TRACKER);
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label).build());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
