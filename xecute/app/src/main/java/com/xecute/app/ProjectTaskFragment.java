/*
 * Project:		Xecute
 *
 * Package:		app
 *
 * Author:		aaronburke
 *
 * Date:		 	2 18, 2014
 */

package com.xecute.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import java.util.List;

/**
 * Created by aaronburke on 2/18/14.
 */
public class ProjectTaskFragment extends ListFragment implements ParseQueryAdapter.OnQueryLoadListener<ParseObject> {

    Context mContext;
    TaskListAdapter taskListAdapter;

    public TextView header;
    ListView projectList;
    LinearLayout mainListView;

    ViewStub stub;

    ProjectTaskFragmentListener mCallback;

    private ActionMode mActionMode;

    int itemPosition;
    View itemRow;
    ViewGroup.MarginLayoutParams mlp;

    public interface ProjectTaskFragmentListener {
        public void onProjectTaskSelected(ListView l, View v, int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mainListView = (LinearLayout) inflater.inflate(R.layout.fragment_main_list, container, false);
        stub = (ViewStub) mainListView.findViewById(android.R.id.empty);
        stub.setLayoutResource(R.layout.project_empty_stub);

        header = (TextView) mainListView.findViewById(R.id.header);

        setHasOptionsMenu(true);
        mContext = getActivity();

        return mainListView;
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        getListView().getEmptyView().setVisibility(ListView.GONE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String projectId = bundle.getString("projectId");
            header.setText(bundle.getString("projectName"));

            taskListAdapter = new TaskListAdapter(mContext, projectId);
            taskListAdapter.setAutoload(false);
            setListAdapter(taskListAdapter);
            taskListAdapter.addOnQueryLoadListener(this);
            taskListAdapter.loadObjects();
        }

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("ListLongClick", "Activated onItemLongClick");


                if (mActionMode != null) {
                    return false;
                }
                itemPosition = position;
                itemRow = view;
                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = getActivity().startActionMode(mActionModeCallback);
                view.setSelected(true);

                return true;
            }
        });

    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_delete:
                    removeRow(itemRow, itemPosition);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ProjectTaskFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ProjectTaskFragmentListener");
        }
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(List<ParseObject> parseObjects, Exception e) {
        updateHeader();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mCallback.onProjectTaskSelected(l, v, position);
    }

    public void removeRow(final View row, final int position) {
        Log.i("Project Remove", "Selected item at position: " + position);

        final int initialHeight = row.getHeight();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                int newHeight = (int) (initialHeight * (1 - interpolatedTime));
                if (newHeight > 0) {
                    row.getLayoutParams().height = newHeight;
                    row.requestLayout();
                }
            }
        };
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                getListView().removeViewInLayout(row);
                row.getLayoutParams().height = initialHeight;
                row.requestLayout();

                ParseObject projectToDelete = taskListAdapter.getItem(position);

                projectToDelete.deleteInBackground(new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.i("MAIN", "Error Deleting Project: " + e.getMessage());

                        } else {
                            taskListAdapter.loadObjects();

                        }
                    }
                });

            }
        });
        animation.setDuration(300);
        row.startAnimation(animation);
    }

    void updateHeader() {
        if (taskListAdapter.isEmpty()) {
            header.getLayoutParams().height = 0;
            header.setVisibility(View.GONE);

        } else {
            header.getLayoutParams().height = 60;
            header.setVisibility(View.VISIBLE);
        }
    }

}