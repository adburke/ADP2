package com.xecute.app;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;


public class LoginActivity extends FragmentActivity implements LoginFragment.LoginFragmentListener,
        SignupFragment.SignupFragmentListener, ResetFragment.ResetFragmentListener {

    final private String LOGIN = "LOGIN ACTIVITY";
    Context mContext;

    LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        Parse.initialize(mContext, "0168Opz62QUNZQq7KBoYpky76XHovSkbsic0CuaV", "geMyhc0Ni3HR5IX8uzpNt5dxklmOgVtfveIJDxNt" );

        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        fragTrans.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.back_enter, R.anim.back_exit);

        loginFragment = new LoginFragment();
        fragTrans.add(R.id.fragment_container, loginFragment).commit();

    }


    @Override
    public void onButtonSelected(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                Log.i(LOGIN, "Login Btn pressed!");
                UserLogin();
                break;

            case R.id.goToSignupBtn:
                Log.i(LOGIN, "Go to signup!");


                SignupFragment signupFragment = new SignupFragment();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.back_enter, R.anim.back_exit)
                        .replace(R.id.fragment_container, signupFragment)
                        .addToBackStack(null).commit();
                break;

            case R.id.goToResetBtn:
                Log.i(LOGIN, "Go to reset!");

                ResetFragment resetFragment = new ResetFragment();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.back_enter, R.anim.back_exit)
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

    public void UserLogin() {
        EditText userEmailInput = (EditText) findViewById(R.id.user_email);
        final EditText userPasswordInput = (EditText) findViewById(R.id.user_password);
        String email = userEmailInput.getText().toString();
        String password = userPasswordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("All Fields Must Be Filled In").setTitle("Alert");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            ParseUser.logInInBackground(email, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Log.i(LOGIN, "Log In Successful!");
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Log In Failed with Error: " + e.getMessage()).setTitle("Alert");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        userPasswordInput.setText("");
                    }
                }
            });

        }
    }

    public void UserSignUp() {
        Log.i(LOGIN, "UserSignUp fired");
        EditText userNameInput = (EditText) findViewById(R.id.signupEmail);
        EditText passwordInput = (EditText) findViewById(R.id.signupPassword);
        EditText confirmPassInput = (EditText) findViewById(R.id.signupConfPassword);
        String userName = userNameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPassInput.getText().toString();

        Log.i(LOGIN, "email= " + userName + " Pass= " + password + " ConfPass= " + confirmPassword);

        if (password.isEmpty() && confirmPassword.isEmpty() && userName.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("All Fields Must Be Filled In").setTitle("Alert");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (!password.equals(confirmPassword)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Passwords Do Not Match").setTitle("Alert");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            ParseUser user = new ParseUser();
            user.setUsername(userName);
            user.setPassword(password);
            user.setEmail(userName);

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i(LOGIN, "Sign up successful!");
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Sign Up Failed with Error: " + e.getMessage()).setTitle("Alert");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }
    }

    public void PasswordReset() {
        Log.i(LOGIN, "Reset password fired.");
        final EditText resetEmailinput = (EditText) findViewById(R.id.resetEmail);
        String email = resetEmailinput.getText().toString();

        if (email.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Please Input Your Account Email Address").setTitle("Alert");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            ParseUser.requestPasswordResetInBackground(email,
                new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // An email was successfully sent with reset instructions.
                            Log.i(LOGIN, "Reset password successful.");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,loginFragment).commit();

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage("Reset Failed with Error: " + e.getMessage()).setTitle("Alert");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            resetEmailinput.setText("");
                        }
                    }
                });
        }

    }
}
