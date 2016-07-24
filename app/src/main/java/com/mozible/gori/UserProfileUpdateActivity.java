package com.mozible.gori;

/**
 * Created by JunLee on 7/17/16.
 */

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mozible.gori.fragments.UserProfileFragment;
import com.mozible.gori.models.PostResult;
import com.mozible.gori.models.UserProfile;
import com.mozible.gori.utils.GoriPreferenceManager;
import com.mozible.gori.utils.ServerInterface;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserProfileUpdateActivity extends AppCompatActivity implements ActivitySnack, ActivityGA{
    private EditText edit_text_description;
    private EditText edit_text_job;
    private Button button_apply;
    private Toolbar toolbar;
    private CoordinatorLayout coordinator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_update);
        UserProfile userProfile = UserProfile.getObjectFromJSonObject(getIntent().getExtras().getString("USER_PROFILE", ""));
        initViews(userProfile);

        Tracker tracker = ((GoriApplication) getApplication())
                .getTracker(GoriApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("UserProfileUpdateActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    private void initViews(UserProfile userProfile) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        edit_text_description = (EditText) findViewById(R.id.edit_text_description);
        edit_text_job = (EditText) findViewById(R.id.edit_text_job);
        button_apply = (Button) findViewById(R.id.button_apply);
        edit_text_description.setText(userProfile.getDescription());
        edit_text_job.setText(userProfile.getJob());
        button_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_text_description.getText() != null && edit_text_description.getText().length() > 0 &&
                        edit_text_job.getText() != null && edit_text_job.getText().length() > 0) {
                    sendGA("user", "modify", "request");
                    String session = GoriPreferenceManager.getInstance(UserProfileUpdateActivity.this).getSession();
                    ServerInterface api = GoriApplication.getInstance().getServerInterface();
                    api.updateMyInfo(session, 1, "nick_name", edit_text_description.getText().toString(),
                            (float)37.549723, (float)126.992468, "서울중심", edit_text_job.getText().toString(), new Callback<PostResult>() {

                                @Override
                                public void success(PostResult s, Response response) {
                                    if(s.status.toLowerCase().equals("success")) {
                                        sendGA("user", "modify", "success");
                                        showSnackbar("profile info update success");
                                        finish();
                                    } else {
                                        sendGA("user", "modify", "fail:" + s.desc);
                                        showSnackbar(s.desc);
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    showSnackbar("profile info update failed!");
                                    sendGA("user", "modify", "fail:" + error.getMessage());
                                }
                            });
                } else {
                    showSnackbar("내용을 모두 채워주세요");
                }
            }
        });
    }

    public void showSnackbar(String text) {
        Snackbar.make(coordinator, text, Snackbar.LENGTH_SHORT).show();
    }

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
