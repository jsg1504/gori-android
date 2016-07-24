package com.mozible.gori;

/**
 * Created by JunLee on 7/17/16.
 */

import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mozible.gori.fragments.LoginLoginFragment;
import com.mozible.gori.fragments.LoginMainFragment;
import com.mozible.gori.fragments.LoginSignupFragment;

public class LoginActivity extends AppCompatActivity implements ActivityGA{

    private final int STATE_MAIN = 0;
    private final int STATE_LOGIN = 1;
    private final int STATE_SIGN_UP = 2;

    private AnimationDrawable animation = null;


    private int mCurrentState = STATE_MAIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        Tracker tracker = ((GoriApplication) getApplication())
                .getTracker(GoriApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("LoginActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animation != null && !animation.isRunning())
            animation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animation != null && animation.isRunning())
            animation.stop();
    }

    @Override
    public void onBackPressed() {
        if(mCurrentState == STATE_LOGIN || mCurrentState == STATE_SIGN_UP) {
            changeMainFragment();
        } else {
            super.onBackPressed();
        }
    }

    private void initViews() {
        loadTypeface();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        AnimationDrawable animation = (AnimationDrawable) container.getBackground();
        animation.setEnterFadeDuration(6000);
        animation.setExitFadeDuration(2000);
        if (!animation.isRunning()) {
            animation.start();
        }


        changeMainFragment();

    }

    private static final String JUA_TYPEFACE_NAME = "fonts/BMJUA_ttf.ttf";
    public Typeface typeface = null;
    private void loadTypeface(){
        if(typeface==null) {
            typeface = Typeface.createFromAsset(getAssets(), JUA_TYPEFACE_NAME);
        }
    }

    public void changeMainFragment() {
        mCurrentState = STATE_MAIN;

        Fragment newFragment = new LoginMainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_layout, newFragment);

        transaction.commit();
    }
    public void changeLoginFragment() {
        mCurrentState = STATE_LOGIN;

        Fragment newFragment = new LoginLoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_layout, newFragment);

        transaction.commit();
    }
    public void changeSignupFragment() {
        mCurrentState = STATE_SIGN_UP;

        Fragment newFragment = new LoginSignupFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_layout, newFragment);

        transaction.commit();
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
