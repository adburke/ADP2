/*
 * Project:		Xecute
 *
 * Package:		app
 *
 * Author:		aaronburke
 *
 * Date:		 	2 12, 2014
 */

package com.xecute.app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {

    Context mContext;

    ActionBar actionBar;
    Spinner navSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);


        navSpinner = new Spinner(mContext);
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.action_list,
                android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);

        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        fragTrans.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.back_enter, R.anim.back_exit);

        ProjectsFragment projectsFragment = new ProjectsFragment();
        fragTrans.add(R.id.main_container, projectsFragment).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                Log.i("MAIN", "Log Out Selected.");
                ParseUser.logOut();
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                // Removes activity from the stack so we can not navigate back
                finish();
                return true;

            case R.id.action_add_project:
                Log.i("MAIN", "New Project Selected.");
                createNewProject();
                return true;

            case R.id.action_filter_project:
                Log.i("MAIN", "Filter Project Selected.");

                AlertDialog.Builder filterBuilder = new AlertDialog.Builder(mContext);
                filterBuilder.setTitle(R.string.action_filter_project)
                        .setItems(R.array.project_filters, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                            }
                        });
                filterBuilder.create().show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (itemPosition == 0) {
            Log.i("MAIN", "Projects Selected");

        } else {
            Log.i("MAIN", "My Tasks Selected");
        }
        return false;
    }

    public void createNewProject() {
        AlertDialog.Builder projectBuilder = new AlertDialog.Builder(this);
        projectBuilder.setTitle(R.string.action_new_project);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.create_project, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        projectBuilder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        final AlertDialog dialog = projectBuilder.create();
        dialog.show();

        //noinspection ConstantConditions
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final Boolean[] wantToCloseDialog = {false};

                EditText projectName = (EditText) view.findViewById(R.id.project_name);
                TextView errorMessage = (TextView) view.findViewById(R.id.project_name_error);
                if (projectName.getText().toString().isEmpty()) {
                    Log.i("Save Project", "Name is empty!");
                    errorMessage.setVisibility(View.VISIBLE);

                } else {
                    final ParseObject newProject = new ParseObject("project");
                    newProject.put("projectName", projectName.getText().toString());
                    newProject.put("status", "New");

                    ParseObject color = ParseObject.createWithoutData("color", "ESp9ejI3iO");
                    newProject.put( "color", color);

                    newProject.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i("MAIN", "Error saving Project: " + e.getMessage());
                                try {
                                    newProject.delete();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }
}
