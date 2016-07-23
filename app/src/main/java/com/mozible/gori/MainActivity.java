package com.mozible.gori;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.mozible.gori.fragments.AllContentFragment;
import com.mozible.gori.fragments.MainNewsfeedFragment;
import com.mozible.gori.fragments.UserProfileFragment;
import com.mozible.gori.models.PostResult;
import com.mozible.gori.models.UserProfile;
import com.mozible.gori.utils.GoriPreferenceManager;
import com.mozible.gori.utils.ServerInterface;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.soundcloud.android.crop.Crop;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Stack;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements ActivitySnack{

    private CoordinatorLayout coordinator;
    private Toolbar toolbar;
    private ImageView logo;
    private MenuItem menuItem;
    private BottomBar mBottomBar;
    private Fragment mMainNewsfeedFragment;
    private Fragment mMainProfileFragment;
    private Fragment mAllContentFragment;

    private boolean mIsBackKeyPressed = false;
    private long mCurrTimeInMillis = 0;
    private static final int MSG_TIMER_EXPIRED = 1;
    private static final int BACKKEY_TIMEOUT = 2;
    private static final int MILLIS_IN_SEC = 1000;

    private final int LAUNCH_IMAGE_UPLOAD_ACTIVITY = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initBottomBar(savedInstanceState);
    }

    public void initBottomBar(Bundle savedInstanceState) {
        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.noTabletGoodness();
        mBottomBar.setMaxFixedTabs(4);

        mBottomBar.setItems(R.menu.three_buttons_menu);
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected( int menuItemId) {
                switch (menuItemId) {
                    case R.id.recent_item:
                        changeNewsfeedFragment();
                        break;
                    case R.id.recent_all_item:
                        changeRecentAllFragment();
                        break;
                    case R.id.photo_item:
                        Intent intent;
                        intent = new Intent(MainActivity.this, ImageUploadActivity.class);
                        startActivityForResult(intent, LAUNCH_IMAGE_UPLOAD_ACTIVITY);
                        break;
                    case R.id.profile_item:
                        changeProfileFragment();
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected( int menuItemId) {
                switch (menuItemId) {
                    case R.id.recent_item:
                        break;
                    case R.id.recent_all_item:
                        break;
                    case R.id.photo_item:
                        Intent intent;
                        intent = new Intent(MainActivity.this, ImageUploadActivity.class);
                        startActivityForResult(intent, LAUNCH_IMAGE_UPLOAD_ACTIVITY);
                        break;
                    case R.id.profile_item:
                        break;
                }
            }
        });
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(3, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.setActiveTabColor("#b5180f");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        menuItem = menu.findItem(R.id.action_location);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_location:
                Snackbar.make(coordinator, "Location", Snackbar.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            ((UserProfileFragment)mMainProfileFragment).beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            ((UserProfileFragment)mMainProfileFragment).handleCrop(resultCode, result);
        } else if(requestCode == LAUNCH_IMAGE_UPLOAD_ACTIVITY && resultCode == Activity.RESULT_CANCELED) {
            String session = GoriPreferenceManager.getInstance(this).getSession();
            ServerInterface api = GoriApplication.getInstance().getServerInterface();
            int contentId = result.getExtras().getInt("CONTENT_ID", -1);
            if(contentId != -1 ){
                api.cancelUploadContent(session, contentId, "", new Callback<PostResult>() {

                    @Override
                    public void success(PostResult s, Response response) {
                        if(s.status.toLowerCase().equals("success")) {
                            showSnackbar("content image cancel completed");
                        } else {
                            showSnackbar(s.desc);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        showSnackbar("content image cancel failed!");
                    }
                });
            }
        }
    }

    private void initView() {
        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        logo = (ImageView) findViewById(R.id.imageview_logo);
        setupToolbar();
        initAdapter();
    }

    private void initAdapter() {

    }

    protected void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
//            toolbar.setNavigationIcon(R.mipmap.search);
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Snackbar.make(coordinator, "search", Snackbar.LENGTH_SHORT).show();
//                }
//            });
        }
    }

    public void showLikedSnackbar() {
        Snackbar.make(coordinator, "Liked!", Snackbar.LENGTH_SHORT).show();
    }

    private Handler mTimerHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_TIMER_EXPIRED: {
                    mIsBackKeyPressed = false;
                }
                break;
            }
        }
    };

    private void startTimer() {
        mTimerHandler.sendEmptyMessageDelayed(MSG_TIMER_EXPIRED,
                BACKKEY_TIMEOUT * MILLIS_IN_SEC);
    }

    private void finishApplication() {
        moveTaskToBack(true); finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onBackPressed() {
        if (mIsBackKeyPressed == false) {
            mIsBackKeyPressed = true;
            mCurrTimeInMillis = Calendar.getInstance().getTimeInMillis();
            Snackbar.make(coordinator, "\"Back key\"를 한번 더 누르시면 종료됩니다.", Snackbar.LENGTH_SHORT).show();
            startTimer();
        } else {
            mIsBackKeyPressed = false;

            if (Calendar.getInstance().getTimeInMillis() <= (mCurrTimeInMillis + (BACKKEY_TIMEOUT * MILLIS_IN_SEC))) {
                finishApplication();
            }
        }
    }

    public void showSnackbar(String text) {
        Snackbar.make(coordinator, text, Snackbar.LENGTH_SHORT).show();
    }

    public void changeNewsfeedFragment() {
        if ( mMainNewsfeedFragment == null ) {
            mMainNewsfeedFragment= new MainNewsfeedFragment();
        }
        Fragment newFragment = mMainNewsfeedFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_layout, newFragment);

        transaction.commit();
    }

    public void changeProfileFragment() {
        if ( mMainProfileFragment == null) {
            UserProfile userProfile = GoriPreferenceManager.getInstance(this).getMyProfileObject();
            mMainProfileFragment = UserProfileFragment.newInstance(userProfile, true);
        }
        Fragment newFragment = mMainProfileFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_layout, newFragment);

        transaction.commit();
    }

    public void changeRecentAllFragment() {
        if ( mAllContentFragment == null ) {
            mAllContentFragment= new AllContentFragment();
        }
        Fragment newFragment = mAllContentFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_layout, newFragment);

        transaction.commit();
    }
}