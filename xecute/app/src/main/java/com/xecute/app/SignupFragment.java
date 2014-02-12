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
public class SignupFragment extends Fragment implements View.OnClickListener {
    SignupFragmentListener mCallback;

    public interface SignupFragmentListener {
        public void onButtonSelected(View v);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout signupView = (LinearLayout) inflater.inflate(R.layout.sign_up, container, false);

        Button signupBtn = (Button) signupView.findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(this);

        return signupView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (SignupFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SignupFragmentListener");
        }
    }

    @Override
    public void onClick(View v) {
        mCallback.onButtonSelected(v);
    }

}