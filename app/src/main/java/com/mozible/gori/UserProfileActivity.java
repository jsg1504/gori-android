package com.mozible.gori;

/**
 * Created by JunLee on 7/17/16.
 */

import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mozible.gori.fragments.LoginLoginFragment;
import com.mozible.gori.fragments.LoginMainFragment;
import com.mozible.gori.fragments.LoginSignupFragment;
import com.mozible.gori.fragments.UserProfileFragment;
import com.mozible.gori.models.UserProfile;
import com.mozible.gori.utils.GoriPreferenceManager;

public class UserProfileActivity extends AppCompatActivity implements ActivitySnack, ActivityGA{
    private Fragment mUserProfileFragment;
    private Toolbar toolbar;
    private CoordinatorLayout coordinator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        UserProfile userProfile = UserProfile.getObjectFromJSonObject(getIntent().getExtras().getString("USER_PROFILE", ""));
        initViews(userProfile);

        Tracker tracker = ((GoriApplication) getApplication())
                .getTracker(GoriApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("UserProfileActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());

    }

    private void initViews(UserProfile userProfile) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        String username = GoriPreferenceManager.getInstance(this).getUsername();
        if(username.equals(userProfile.user.username)) {
            mUserProfileFragment = UserProfileFragment.newInstance(userProfile, true);
        } else {
            mUserProfileFragment = UserProfileFragment.newInstance(userProfile, false);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_layout, mUserProfileFragment);

        transaction.commit();
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
