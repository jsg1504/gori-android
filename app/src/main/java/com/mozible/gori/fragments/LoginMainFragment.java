package com.mozible.gori.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mozible.gori.ActivityGA;
import com.mozible.gori.LoginActivity;
import com.mozible.gori.R;

/**
 * Created by JunLee on 7/18/16.
 */
public class LoginMainFragment extends Fragment {
    private TextView logo_text;
    private Button sign_up_button;
    private Button login_button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_main, container, false);
        initView(rootView);
        return rootView;
    }

    public void initView(View rootView) {

        logo_text = (TextView) rootView.findViewById(R.id.logo_text);
        logo_text.setText(getString(R.string.logo_text));
        LoginActivity loginActivity = (LoginActivity)getActivity();
        logo_text.setTypeface(loginActivity.typeface);

        login_button = (Button) rootView.findViewById(R.id.login_button);
        login_button.setText(getString(R.string.login_text));
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityGA)getActivity()).sendGA("fragment", "move", "login fragment");

                LoginActivity loginActivity = (LoginActivity)getActivity();
                loginActivity.changeLoginFragment();
            }
        });

        sign_up_button = (Button) rootView.findViewById(R.id.sign_up_button);
        sign_up_button.setText(getString(R.string.sign_up_email_text));
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityGA)getActivity()).sendGA("fragment", "move", "signup fragment");
                LoginActivity loginActivity = (LoginActivity)getActivity();
                loginActivity.changeSignupFragment();
            }
        });
    }

}
