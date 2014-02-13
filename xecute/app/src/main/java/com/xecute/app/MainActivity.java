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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        fragTrans.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.back_enter, R.anim.back_exit);

        ProjectsFragment projectsFragment = new ProjectsFragment();
        fragTrans.add(R.id.main_container, projectsFragment).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId()) {
            case R.id.action_logout:
                Log.i("MAIN", "Log Out Selected.");
                return true;

            case R.id.action_add_project:
            Log.i("MAIN", "New Project Selected.");
            return true;

            case R.id.action_filter_project:
                Log.i("MAIN", "Filter Project Selected.");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
