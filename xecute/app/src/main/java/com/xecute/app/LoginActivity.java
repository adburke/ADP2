package com.xecute.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;


public class LoginActivity extends FragmentActivity implements LoginFragment.LoginFragmentListener {
    final private String LOGIN = "LOGIN ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        LoginFragment loginFragment = new LoginFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, loginFragment).commit();
    }


    @Override
    public void onButtonSelected(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                Log.i(LOGIN, "Login Btn pressed!");
                break;
            case R.id.signupBtn:
                Log.i(LOGIN, "SignUp Btn pressed!");

                SignupFragment signupFragment = new SignupFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, signupFragment)
                        .addToBackStack(null).commit();

                break;
            case R.id.resetBtn:
                Log.i(LOGIN, "Reset Btn pressed!");

                ResetFragment resetFragment = new ResetFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, resetFragment)
                        .addToBackStack(null).commit();

                break;
        }
    }
}
