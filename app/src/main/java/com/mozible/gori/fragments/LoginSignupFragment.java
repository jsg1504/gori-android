package com.mozible.gori.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.mozible.gori.ActivityGA;
import com.mozible.gori.LoginActivity;
import com.mozible.gori.R;
import com.mozible.gori.tasks.SignUpTask;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.utils.GoriPreferenceManager;
import com.nispok.snackbar.Snackbar;

/**
 * Created by JunLee on 7/18/16.
 */
public class LoginSignupFragment extends Fragment {
    private EditText edit_text_email;
    private EditText edit_text_username;
    private EditText edit_text_password;
    private EditText edit_text_password2;
    private Button sign_up_button;
    private Button login_button;

    private SignUpTask mSignUpTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_signup, container, false);
        initView(rootView);
        return rootView;
    }
    public void initView(View rootView) {
        edit_text_email = (EditText)rootView.findViewById(R.id.edit_text_email);
        edit_text_username = (EditText)rootView.findViewById(R.id.edit_text_username);
        edit_text_password = (EditText)rootView.findViewById(R.id.edit_text_password);
        edit_text_password2 = (EditText)rootView.findViewById(R.id.edit_text_password2);
        sign_up_button = (Button)rootView.findViewById(R.id.sign_up_button);
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edit_text_email.getText().toString();
                String username = edit_text_username.getText().toString();
                String password = edit_text_password.getText().toString();
                String password2 = edit_text_password2.getText().toString();

                if(!password.equals(password2.toString())) {
                    Snackbar.with(getActivity())
                            .text("비밀번호를 제대로 입력해 주세요")
                            .show(getActivity());
                    ((ActivityGA)getActivity()).sendGA("user", "signup", "request fail : 비밀번호를 제대로 입력해 주세요");

                } else {
                    edit_text_email.setEnabled(false);
                    edit_text_username.setEnabled(false);
                    edit_text_password.setEnabled(false);
                    edit_text_password2.setEnabled(false);
                    sign_up_button.setEnabled(false);
                    login_button.setEnabled(false);
                    startSignUpTask(email, username, password);
                    ((ActivityGA)getActivity()).sendGA("user", "signup", "request");

                }
                hideKeyboard();
            }
        });

        login_button = (Button)rootView.findViewById(R.id.login_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity loginActivity = (LoginActivity)getActivity();
                loginActivity.changeLoginFragment();
            }
        });
    }

    private void startSignUpTask(String email, String username, String password) {
        if (mSignUpTask == null) {
            mSignUpTask = new SignUpTask(getActivity(), email, username, password, mTaskListener);
        } else {
            mSignUpTask.cancel(true);
            mSignUpTask = null;
            mSignUpTask = new SignUpTask(getActivity(), email, username, password, mTaskListener);
        }

        mSignUpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public SignUpTask.TaskListener mTaskListener = new SignUpTask.TaskListener() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(GoriConstants.STATUS STATUS, String username, int errorCode) {
            if(STATUS == GoriConstants.STATUS.SUCCESS) {
                GoriPreferenceManager.getInstance(getActivity()).setUsername(username);
                Snackbar.with(getActivity())
                        .text("가입완료, 이메일 인증 후에 로그인 해주세요.")
                        .show(getActivity());
                LoginActivity loginActivity = (LoginActivity)getActivity();
                loginActivity.changeLoginFragment();
                ((ActivityGA)getActivity()).sendGA("user", "signup", "success");

            } else if(STATUS == GoriConstants.STATUS.ERROR) {
                switch(errorCode) {
                    case 200:
                        ((ActivityGA)getActivity()).sendGA("user", "signup", "fail : 이미 사용중인 username 입니다");

                        Snackbar.with(getActivity())
                                .text("이미 사용중인 username 입니다")
                                .show(getActivity());
                        break;
                    case 201:
                        ((ActivityGA)getActivity()).sendGA("user", "signup", "fail : 이미 사용중인 email 입니다");

                        Snackbar.with(getActivity())
                                .text("이미 사용중인 email 입니다")
                                .show(getActivity());
                        break;
                }
            }
            edit_text_email.setEnabled(true);
            edit_text_username.setEnabled(true);
            edit_text_password.setEnabled(true);
            edit_text_password2.setEnabled(true);
            sign_up_button.setEnabled(true);
            login_button.setEnabled(true);

        }

        @Override
        public void onCanceled() {

        }
    };

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                edit_text_password2.getWindowToken(),
                0);
    }
}
