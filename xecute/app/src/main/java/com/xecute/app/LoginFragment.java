/*
 * Project:		Xecute
 *
 * Package:		app
 *
 * Author:		aaronburke
 *
 * Date:		 	2 11, 2014
 */

package com.xecute.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by aaronburke on 2/11/14.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    final private String LOGIN = "LOGIN ACTIVITY";
    LoginFragmentListener mCallback;

    public interface LoginFragmentListener {
        public void onButtonSelected(View v);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout loginView = (LinearLayout) inflater.inflate(R.layout.login, container, false);

        Button loginBtn = (Button) loginView.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        Button resetBtn = (Button) loginView.findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);
        Button signupBtn = (Button) loginView.findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(this);


        return loginView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (LoginFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.loginBtn:
//                Log.i(LOGIN, "Login Btn pressed!");
//                break;
//            case R.id.signupBtn:
//                Log.i(LOGIN, "SignUp Btn pressed!");
                mCallback.onButtonSelected(v);
//                SignupFragment fragment = new SignupFragment();
//                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment);
//                break;
//            case R.id.resetBtn:
//                Log.i(LOGIN, "Reset Btn pressed!");
//                break;
//        }

    }

}
