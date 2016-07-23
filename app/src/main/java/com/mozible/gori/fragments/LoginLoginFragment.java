package com.mozible.gori.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.mozible.gori.GoriApplication;
import com.mozible.gori.LoginActivity;
import com.mozible.gori.MainActivity;
import com.mozible.gori.R;
import com.mozible.gori.tasks.LoginTask;
import com.mozible.gori.models.UserProfile;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.utils.GoriTextUtil;
import com.mozible.gori.utils.GoriPreferenceManager;
import com.mozible.gori.utils.ServerInterface;
import com.mozible.gori.utils.UserDatabaseHelper;
import com.nispok.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JunLee on 7/18/16.
 */
public class LoginLoginFragment extends Fragment {
    private EditText edit_text_username;
    private EditText edit_text_password;
    private Button login_button;
    private Button sign_up_button;
    private LoginTask mLoginTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_login, container, false);
        initView(rootView);
        return rootView;
    }
    public void initView(View rootView) {
        edit_text_username = (EditText) rootView.findViewById(R.id.edit_text_username);
        String username = GoriPreferenceManager.getInstance(getActivity()).getUsername();
        if(!GoriTextUtil.isEmpty(username)) {
            edit_text_username.setText(username);
        }
        edit_text_password = (EditText) rootView.findViewById(R.id.edit_text_password);
        login_button = (Button) rootView.findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edit_text_username.getText().toString();
                String password = edit_text_password.getText().toString();
                if(GoriTextUtil.isEmpty(username) || GoriTextUtil.isEmpty(password)) {
                    hideKeyboard();
                    Snackbar.with(getActivity())
                            .text("username, password를 입력해 주세요")
                            .show(getActivity());
                } else {
                    startLoginTask(username, password);
                    edit_text_username.setEnabled(false);
                    edit_text_password.setEnabled(false);
                    login_button.setEnabled(false);
                    sign_up_button.setEnabled(false);
                    hideKeyboard();
                }
            }
        });

        sign_up_button = (Button) rootView.findViewById(R.id.sign_up_button);

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity loginActivity = (LoginActivity)getActivity();
                loginActivity.changeSignupFragment();
            }
        });
    }
    private void startLoginTask(String username, String password) {
        if (mLoginTask == null) {
            mLoginTask = new LoginTask(getActivity(), username, password, mLoginTaskListener);
        } else {
            mLoginTask.cancel(true);
            mLoginTask = null;
            mLoginTask = new LoginTask(getActivity(), username, password, mLoginTaskListener);
        }

        mLoginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public LoginTask.TaskListener mLoginTaskListener = new LoginTask.TaskListener() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(GoriConstants.STATUS STATUS, UserProfile userProfile, int errorCode) {
            if(userProfile != null) {
                GoriPreferenceManager.getInstance(getActivity()).setMyProfileObject(userProfile);
                GoriPreferenceManager.getInstance(getActivity()).setUsername(edit_text_username.getText().toString());
                GoriPreferenceManager.getInstance(getActivity()).setPassword(edit_text_password.getText().toString());
                Snackbar.with(getActivity())
                        .text("Connect")
                        .show(getActivity());
                String session = GoriPreferenceManager.getInstance(getActivity()).getSession();
                ServerInterface api = GoriApplication.getInstance().getServerInterface();
                api.getUserFollowings(session, userProfile.user.username, new Callback<ArrayList<UserProfile>>() {

                    @Override
                    public void success(ArrayList<UserProfile> s, Response response) {
                        if(s != null && s.size() > 0) {
                            try {
                                UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(getActivity());
                                userDatabaseHelper.deleteAllFollowingUser();
                                userDatabaseHelper.insertFollowingUsers(s);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                            showSnackbar("content image cancel failed!");
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            } else if(STATUS == GoriConstants.STATUS.ERROR) {
                switch(errorCode) {
                    case 203:
                        Snackbar.with(getActivity())
                                .text("Username / Password 가 일치하지 않습니다.")
                                .show(getActivity());
                        break;
                    case 204:
                        Snackbar.with(getActivity())
                                .text("email 인증이 완료되지 않았습니다")
                                .show(getActivity());
                        break;
                    case -1:
                        Snackbar.with(getActivity())
                                .text("네트워크 상태를 확인해 주세요.")
                                .show(getActivity());
                        break;
                }
            }
            edit_text_username.setEnabled(true);
            edit_text_password.setEnabled(true);
            login_button.setEnabled(true);
            sign_up_button.setEnabled(true);
        }

        @Override
        public void onCanceled() {
            edit_text_username.setEnabled(true);
            edit_text_password.setEnabled(true);
            login_button.setEnabled(true);
            sign_up_button.setEnabled(true);
        }
    };

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                edit_text_password.getWindowToken(),
                0);
    }
}
