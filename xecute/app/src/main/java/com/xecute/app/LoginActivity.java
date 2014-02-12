package com.xecute.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;


public class LoginActivity extends FragmentActivity implements LoginFragment.LoginFragmentListener,
        SignupFragment.SignupFragmentListener, ResetFragment.ResetFragmentListener {

    final private String LOGIN = "LOGIN ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Parse.initialize(this, "0168Opz62QUNZQq7KBoYpky76XHovSkbsic0CuaV", "geMyhc0Ni3HR5IX8uzpNt5dxklmOgVtfveIJDxNt" );

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

            case R.id.goToSignupBtn:
                Log.i(LOGIN, "Go to signup!");

                SignupFragment signupFragment = new SignupFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, signupFragment)
                        .addToBackStack(null).commit();
                break;

            case R.id.goToResetBtn:
                Log.i(LOGIN, "Go to reset!");

                ResetFragment resetFragment = new ResetFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, resetFragment)
                        .addToBackStack(null).commit();
                break;

            case R.id.signupBtn:
                Log.i(LOGIN, "SignUp Btn pressed!");
                UserSignUp();
                break;

            case R.id.resetBtn:
                Log.i(LOGIN, "Reset Btn pressed!");
                PasswordReset();
                break;
        }
    }

    public void UserSignUp() {
        Log.i(LOGIN, "UserSignUp fired");
        EditText userNameInput = (EditText) findViewById(R.id.signupEmail);
        EditText passwordInput = (EditText) findViewById(R.id.signupPassword);
        EditText confirmPassInput = (EditText) findViewById(R.id.signupConfPassword);

        Log.i(LOGIN, "email= " + userNameInput.getText() + " Pass= " + passwordInput.getText() + " ConfPass= " + confirmPassInput.getText());

        if (passwordInput.getText().toString().isEmpty() && confirmPassInput.getText().toString().isEmpty() && userNameInput.getText().toString().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("All Fields Must Be Filled In").setTitle("Alert");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (!passwordInput.getText().toString().equals(confirmPassInput.getText().toString())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Passwords Do Not Match").setTitle("Alert");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void PasswordReset() {

    }
}
