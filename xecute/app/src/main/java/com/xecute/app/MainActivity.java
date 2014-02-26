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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener,
        ProjectsFragment.ProjectsFragmentListener, ProjectTaskFragment.ProjectTaskFragmentListener,
        CreateTaskDialogFragment.CreateTaskDialogListener, MyTasksFragment.MyTaskFragmentListener{

    Context mContext;

    ActionBar actionBar;
    Spinner navSpinner;

    Boolean appLaunchedStatus;

    String listHeaderValue;

    public ParseObject selectedProject;
    public ParseObject selectedTask;
    String selectedColorStr;

    ProjectsFragment projectsFragment;
    ProjectTaskFragment projectTaskFragment;
    TaskDetailFragment taskDetailFragment;
    MyTasksFragment myTasksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        appLaunchedStatus = false;

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

        projectsFragment = new ProjectsFragment();
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
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        switch (itemPosition) {

            case 0:
                if (appLaunchedStatus) {
                    FragmentManager fragManager = getSupportFragmentManager();
                    FragmentTransaction fragTrans = fragManager.beginTransaction();
                    fragTrans.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.back_enter, R.anim.back_exit);

                    projectsFragment = new ProjectsFragment();
                    fragTrans.replace(R.id.main_container, projectsFragment).commit();

                }
                appLaunchedStatus = true;
                break;

            case 1:
                Log.i("MAIN", "My Tasks Selected");
                FragmentManager fragManager = getSupportFragmentManager();
                FragmentTransaction fragTrans = fragManager.beginTransaction();
                fragTrans.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.back_enter, R.anim.back_exit);
                fragManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                myTasksFragment = new MyTasksFragment();
                fragTrans.replace(R.id.main_container, myTasksFragment).commit();

                break;
        }

        return false;
    }


    @Override
    public void onProjectSelected(ListView l, View v, int position) {
        Log.i("MAIN_ACTIVITY", "Selected Project at position: " + position);
        selectedProject = (ParseObject) l.getItemAtPosition(position);
        String projectName = selectedProject.getString("projectName");
        selectedColorStr = selectedProject.getParseObject("color").getObjectId();
        Log.i("MAIN_ACTIVITY", "Project Name: " + projectName + " Project id: " + selectedProject.getObjectId());
        Log.i("MAIN_ACTIVITY", "Color: " + selectedColorStr);


        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        fragTrans.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.back_enter, R.anim.back_exit);
        projectTaskFragment = new ProjectTaskFragment();

        fragTrans.replace(R.id.main_container, projectTaskFragment)
                .addToBackStack(null).commit();
    }


    @Override
    public void onProjectTaskSelected(ListView l, View v, int position) {
        Log.i("MAIN_ACTIVITY", "Selected Task at position: " + position);
        selectedTask = (ParseObject) l.getItemAtPosition(position);

        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        fragTrans.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.back_enter, R.anim.back_exit);
        taskDetailFragment = new TaskDetailFragment();

        fragTrans.replace(R.id.main_container, taskDetailFragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onTaskCreate(String taskNameStr, Date date, ArrayList<ParseUser> users, String taskDescriptionStr) {

        final ParseObject newTask = new ParseObject("task");
        newTask.put("taskName", taskNameStr);
        newTask.put("taskDescription", taskDescriptionStr);
        ParseObject project = ParseObject.createWithoutData("project", selectedProject.getObjectId());
        newTask.put("parentProject", project);
        newTask.put("usersTasked", users);
        newTask.put("percentCompleted", 0);
        ParseObject color = ParseObject.createWithoutData("color", selectedColorStr);
        newTask.put("color", color);

        if (date != null) {
            Log.i("MAIN_TASK_CREATE", "DATA: " + taskNameStr + " - " + date.toString() + " - " + users.size() + " - " + taskDescriptionStr);
            newTask.put("dueDate", date);
        }
        Log.i("MAIN_TASK_CREATE", "DATA: " + selectedProject.getString("projectName"));

        ParseACL groupACl = new ParseACL();
        for (ParseUser user : users) {
            groupACl.setReadAccess(user, true);
            groupACl.setWriteAccess(user, true);
        }
        groupACl.setReadAccess(ParseUser.getCurrentUser(), true);
        groupACl.setWriteAccess(ParseUser.getCurrentUser(), true);
        newTask.setACL(groupACl);

        newTask.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.i("MAIN", "Error saving Project: " + e.getMessage());
                    try {
                        newTask.delete();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {

                    projectTaskFragment.taskListAdapter.loadObjects();

                }
            }
        });

        if (selectedProject.getString("status").equals("New")) {
            selectedProject.put("status", "Active");
            selectedProject.saveInBackground();
        }

    }

    @Override
    public void onMyTaskSelected(ListView l, View v, int position) {
        Log.i("MAIN_ACTIVITY", "Selected Task at position: " + position);
        selectedTask = (ParseObject) l.getItemAtPosition(position);

        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        fragTrans.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.back_enter, R.anim.back_exit);
        taskDetailFragment = new TaskDetailFragment();

        fragTrans.replace(R.id.main_container, taskDetailFragment)
                .addToBackStack(null).commit();
    }
}
