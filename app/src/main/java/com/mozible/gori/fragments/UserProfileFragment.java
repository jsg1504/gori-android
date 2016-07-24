package com.mozible.gori.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mozible.gori.ActivityGA;
import com.mozible.gori.ActivitySnack;
import com.mozible.gori.GoriApplication;
import com.mozible.gori.LogoActivity;
import com.mozible.gori.R;
import com.mozible.gori.UserProfileActivity;
import com.mozible.gori.UserProfileUpdateActivity;
import com.mozible.gori.adapter.MainAdapter;
import com.mozible.gori.models.Content;
import com.mozible.gori.models.MainAdapterObject;
import com.mozible.gori.models.PostResult;
import com.mozible.gori.models.UserProfile;
import com.mozible.gori.tasks.UserContentListTask;
import com.mozible.gori.tasks.UserDetailInfoTask;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.utils.GoriPreferenceManager;
import com.mozible.gori.utils.ServerInterface;
import com.mozible.gori.utils.UserDatabaseHelper;
import com.soundcloud.android.crop.Crop;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by JunLee on 7/21/16.
 */
public class UserProfileFragment extends Fragment {
    private final String TAG = "UserProfileFragment";
    private UserProfile mUserProfile;
    private ActivitySnack mActivitySnack;
    private PullToRefreshListView mListView;
    private UserContentListTask mUserContentListTask;
    private MainAdapter mMainAdapter;
    private List<MainAdapterObject> mMainContentList;
    private boolean mIsMyProfile;
    private UserDetailInfoTask mUserDetailInfoTask;
    private UserDatabaseHelper mUserDatabaseHelper;
    private HashMap<Integer, String> mFollowingUsers;

    private static final int FILE_CODE = 1;

    public static UserProfileFragment newInstance(UserProfile userProfile, boolean isMyProfile) {
        UserProfileFragment f = new UserProfileFragment();
        Bundle args = new Bundle();
        if(userProfile != null) {
            String jsonString = UserProfile.makeJSon(userProfile);
            args.putBoolean("isMyProfile", isMyProfile);
            args.putString("userProfile", jsonString);
        }
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_profile, container, false);

        if(mUserDatabaseHelper == null) {
            mUserDatabaseHelper = new UserDatabaseHelper(getActivity());
        }
        mFollowingUsers = mUserDatabaseHelper.getFollowingUsers();

        initArgs();
        initView(rootView);
        mActivitySnack = (ActivitySnack) getActivity();
        if(mMainContentList.size() == 0) {
            if(mIsMyProfile) {
                mUserProfile.mainAdapterType = MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_MY_PROFILE;
            } else {
                mUserProfile.mainAdapterType = MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_OTHER_PROFILE;
            }
            mMainContentList.add(mUserProfile);
            startUserContentListTask(null, mUserProfile.user.username);
            startUserDetailTask(mUserProfile.user.username);
        }


        return rootView;
    }

    private void initArgs() {
        Bundle args = getArguments();
        String tmpUserProfileJson = args.getString("userProfile", null);
        boolean isMyProfile = args.getBoolean("isMyProfile", false);
        if(tmpUserProfileJson != null) {

            mUserProfile = UserProfile.getObjectFromJSonObject(tmpUserProfileJson);
            if(mFollowingUsers != null && mFollowingUsers.containsKey(mUserProfile.user.id)) {
                mUserProfile.isFollow = true;
            } else {
                mUserProfile.isFollow = false;
            }
            mIsMyProfile = isMyProfile;
        }
    }

    private void initView(View rootView) {
        mListView = (PullToRefreshListView) rootView.findViewById(R.id.listview);
        initAdapter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initAdapter() {
        if(mMainContentList == null) {
            mMainContentList = new ArrayList<>();
        }
        if(mMainAdapter == null) {
            mMainAdapter = new MainAdapter(getActivity(), mMainContentList, listInButtonClickListener,
                    listInButtonLongClickListener, otherProfileInButtonClickListener,
                    myProfileInButtonClickListener);
        }
        mListView.setAdapter(mMainAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getId() == mListView.getId()) {

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
                refreshAll();
            }
        });
    }

    private void startUserContentListTask(String date, String username) {
        if (mUserContentListTask == null) {
            mUserContentListTask = new UserContentListTask(getActivity(), date, username, mUserContentListTaskListener);
        } else {
            mUserContentListTask.cancel(true);
            mUserContentListTask = null;
            mUserContentListTask = new UserContentListTask(getActivity(), date, username, mUserContentListTaskListener);
        }

        mUserContentListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public UserContentListTask.TaskListener mUserContentListTaskListener = new UserContentListTask.TaskListener() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(GoriConstants.STATUS STATUS, List<Content> contentList) {
            if(STATUS == GoriConstants.STATUS.SUCCESS) {
                mMainContentList.addAll(contentList);
                mMainAdapter.notifyDataSetChanged();
            } else if(STATUS == GoriConstants.STATUS.ERROR) {
            }
        }

        @Override
        public void onCanceled() {
        }
    };

    private void startUserDetailTask(String username) {
        if (mUserDetailInfoTask == null) {
            mUserDetailInfoTask = new UserDetailInfoTask(getActivity(), username, mUserDetailInfoTaskListener);
        } else {
            mUserDetailInfoTask.cancel(true);
            mUserDetailInfoTask = null;
            mUserDetailInfoTask = new UserDetailInfoTask(getActivity(), username, mUserDetailInfoTaskListener);
        }

        mUserDetailInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public UserDetailInfoTask.TaskListener mUserDetailInfoTaskListener = new UserDetailInfoTask.TaskListener() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(GoriConstants.STATUS STATUS, int user_content_count, int user_following_count, int user_follower_count) {
            if(STATUS == GoriConstants.STATUS.SUCCESS) {
                mUserProfile.user_content_count = user_content_count;
                mUserProfile.user_following_count = user_following_count;
                mUserProfile.user_follower_count = user_follower_count;
//                ((UserProfile)mMainContentList.get(0)).user_content_count = user_content_count;
//                ((UserProfile)mMainContentList.get(1)).user_following_count = user_following_count;
//                ((UserProfile)mMainContentList.get(2)).user_follower_count = user_follower_count;
                mMainAdapter.notifyDataSetChanged();
            } else if(STATUS == GoriConstants.STATUS.ERROR) {
            }
        }

        @Override
        public void onCanceled() {
        }
    };

    private View.OnClickListener listInButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.relative_layout_profile:
                    break;
                case R.id.btnLike:
                    break;
                case R.id.btnComments:
                    break;
                case R.id.tsLikesCounter:
                    break;
            }
        }
    };

    private View.OnLongClickListener listInButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            switch(v.getId()) {
                case R.id.list_item_square_frame_layout:
                    break;
            }
            return true;
        }
    };

    private View.OnClickListener otherProfileInButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.follow_user:
                    int position = (Integer)v.getTag();
                    UserProfile userProfile = (UserProfile) mMainAdapter.getItem(position);
                    if(mFollowingUsers.containsKey(userProfile.user.id)) {
                        //do un follow
                        ((ActivityGA)getActivity()).sendGA("user profile", "unfollow", "request");

                        String session = GoriPreferenceManager.getInstance(getActivity()).getSession();
                        ServerInterface api = GoriApplication.getInstance().getServerInterface();
                        api.unFollowUser(session, userProfile.user.username, "", new Callback<PostResult>() {

                            @Override
                            public void success(PostResult s, Response response) {
                                if(s.status.toLowerCase().equals("success")) {
                                    ((ActivityGA)getActivity()).sendGA("user profile", "unfollow", "success");
                                    mActivitySnack.showSnackbar("unfollow success!");
                                    mUserDatabaseHelper.deleteFollowingUser(mUserProfile.user.id);
                                    mFollowingUsers.remove(mUserProfile.user.id);
                                    mUserProfile.isFollow = false;
                                    mMainAdapter.notifyDataSetChanged();
                                } else {
                                    ((ActivityGA)getActivity()).sendGA("user profile", "unfollow", "fail : " + s.desc);
                                    mActivitySnack.showSnackbar(s.desc);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                mActivitySnack.showSnackbar("unfollow failed!");
                                ((ActivityGA)getActivity()).sendGA("user profile", "unfollow", "fail : " + error.getMessage());

                                Log.d(TAG, error.toString());
                            }
                        });
                    } else {
                        //do follow
                        ((ActivityGA)getActivity()).sendGA("user profile", "follow", "request");
                        String session = GoriPreferenceManager.getInstance(getActivity()).getSession();
                        ServerInterface api = GoriApplication.getInstance().getServerInterface();
                        api.followUser(session, userProfile.user.username, "", new Callback<PostResult>() {

                            @Override
                            public void success(PostResult s, Response response) {
                                if(s.status.toLowerCase().equals("success")) {
                                    ((ActivityGA)getActivity()).sendGA("user profile", "follow", "success");
                                    mActivitySnack.showSnackbar("follow success!");
                                    mUserDatabaseHelper.insertFollowingUser(mUserProfile);
                                    mFollowingUsers.put(mUserProfile.user.id, mUserProfile.user.username);
                                    mUserProfile.isFollow = true;
                                    mMainAdapter.notifyDataSetChanged();
                                } else {
                                    mActivitySnack.showSnackbar(s.desc);
                                    ((ActivityGA)getActivity()).sendGA("user profile", "follow", "fail : " + s.desc);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                mActivitySnack.showSnackbar("follow failed!");
                                ((ActivityGA)getActivity()).sendGA("user profile", "follow", "fail : " + error.getMessage());

                                Log.d(TAG, error.toString());
                            }
                        });
                    }
                    break;
                case R.id.linearlayout_content_count_wrapper:
                    break;
                case R.id.linearlayout_follower_count_wrapper:
                    break;
                case R.id.linearlayout_following_count_wrapper:
                    break;
            }
        }
    };

    private View.OnClickListener myProfileInButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.list_item_user_profile_photo:
                    startFilePicker();
                    break;
                case R.id.profile_modify:
                    ((ActivityGA)getActivity()).sendGA("user profile", "modify", "request move to user profile update fragment");

                    mActivitySnack.showSnackbar("profile_modify : " + v.getTag());
                    Intent i = new Intent(getActivity(), UserProfileUpdateActivity.class);
                    int position = (Integer)v.getTag();
                    UserProfile userProfile = (UserProfile) mMainAdapter.getItem(position);
                    i.putExtra("USER_PROFILE", UserProfile.makeJSon(userProfile));
                    startActivity(i);
                    break;
                case R.id.linearlayout_content_count_wrapper:
                    break;
                case R.id.linearlayout_follower_count_wrapper:
                    break;
                case R.id.linearlayout_following_count_wrapper:
                    break;
                case R.id.logout:
                    ((ActivityGA)getActivity()).sendGA("user profile", "logout", "request");
                    ServerInterface api = GoriApplication.getInstance().getServerInterface();
                    String session = GoriPreferenceManager.getInstance(getActivity()).getSession();
                    api.logout(session, "", new Callback<PostResult>() {
                        @Override
                        public void success(PostResult s, Response response) {
                            ((ActivityGA)getActivity()).sendGA("user profile", "logout", "success");
                            GoriPreferenceManager.getInstance(getActivity()).setUsername("");
                            GoriPreferenceManager.getInstance(getActivity()).setPassword("");
                            getActivity().finish();
                            Intent i = new Intent(getActivity(), LogoActivity.class);
                            startActivity(i);

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            ((ActivityGA)getActivity()).sendGA("user profile", "logout", "fail : " + error.getMessage());
                        }
                    });
                    break;
            }
        }
    };

    public void refreshAll() {
        String session = GoriPreferenceManager.getInstance(getActivity()).getSession();
        ServerInterface api = GoriApplication.getInstance().getServerInterface();
        api.getUserInfoByUserName(session, mUserProfile.user.username, new Callback<UserProfile>() {

            @Override
            public void success(UserProfile s, Response response) {
                if(s != null && s.user != null) {
                    mMainContentList.clear();
                    mMainAdapter.notifyDataSetChanged();
                    mUserProfile = s;
                    if(mFollowingUsers != null && mFollowingUsers.containsKey(mUserProfile.user.id)) {
                        mUserProfile.isFollow = true;
                    } else {
                        mUserProfile.isFollow = false;
                    }
                    if(mIsMyProfile) {
                        mUserProfile.mainAdapterType = MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_MY_PROFILE;
                    } else {
                        mUserProfile.mainAdapterType = MainAdapterObject.MAIN_ADAPTER_TYPE.TYPE_OTHER_PROFILE;
                    }
                    mMainContentList.add(mUserProfile);
                    mMainAdapter.notifyDataSetChanged();
                    startUserContentListTask(null, mUserProfile.user.username);
                    startUserDetailTask(mUserProfile.user.username);
                }
                mListView.onRefreshComplete();
            }

            @Override
            public void failure(RetrofitError error) {
                mActivitySnack.showSnackbar("refresh failed");
                Log.d(TAG, error.toString());
            }
        });
    }
    public void startFilePicker() {
        ((ActivityGA)getActivity()).sendGA("user profile", "update profile image", "pick image request");
        Crop.pickImage(getActivity());
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent result) {
//        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
//            beginCrop(result.getData());
//        } else if (requestCode == Crop.REQUEST_CROP) {
//            handleCrop(resultCode, result);
//        }
//    }
    public void beginCrop(Uri source) {
        ((ActivityGA)getActivity()).sendGA("user profile", "update profile image", "crop image request");
        String username = GoriPreferenceManager.getInstance(getActivity()).getUsername();
        String imageName = username + System.currentTimeMillis() + ".jpg";
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), imageName));
        Crop.of(source, destination).asSquare().start(getActivity());
    }

    public void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            try {
                ((ActivityGA)getActivity()).sendGA("user profile", "update profile image", "cropped image update request");

                TypedFile uploadFile = new TypedFile("multipart/form-data", new File(uri.getPath()));
                String session = GoriPreferenceManager.getInstance(getActivity()).getSession();
                ServerInterface api = GoriApplication.getInstance().getServerInterface();
                api.uploadProfileImage(session, uploadFile, new Callback<PostResult>() {

                    @Override
                    public void success(PostResult s, Response response) {
                        if(s.status.toLowerCase().equals("success")) {
                            ((ActivityGA)getActivity()).sendGA("user profile", "update profile image", "success");

                            mActivitySnack.showSnackbar("profile upload success!");
                        } else {
                            mActivitySnack.showSnackbar(s.desc);
                            ((ActivityGA)getActivity()).sendGA("user profile", "update profile image", "fail : " + s.desc);

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mActivitySnack.showSnackbar("profile upload failed!");
                        ((ActivityGA)getActivity()).sendGA("user profile", "update profile image", "fail : " + error.getMessage());

                        Log.d(TAG, error.toString());
                    }
                });
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            ((ActivityGA)getActivity()).sendGA("user profile", "update profile image", "fail : activity for result code error");
        }
    }
}
