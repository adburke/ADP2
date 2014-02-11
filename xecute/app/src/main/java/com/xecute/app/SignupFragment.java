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
public class SignupFragment extends Fragment implements View.OnClickListener {
    final private String LOGIN = "LOGIN ACTIVITY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout signupView = (LinearLayout) inflater.inflate(R.layout.sign_up, container, false);

        return signupView;
    }

    @Override
    public void onClick(View v) {
        Log.i(LOGIN, "Signup Pressed!");

    }

}