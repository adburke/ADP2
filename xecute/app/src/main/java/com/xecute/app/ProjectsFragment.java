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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by aaronburke on 2/12/14.
 */
public class ProjectsFragment extends ListFragment {

    Context mContext;
    ProjectListAdapter projectListAdapter;
    ListView projectList;

    ProjectsFragmentListener mCallback;

    private ActionMode mActionMode;

    int itemPosition;
    View itemRow;


    public interface ProjectsFragmentListener {
        public void onItemSelected(ListView l, View v, int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);
        mContext = getActivity();

        projectListAdapter = new ProjectListAdapter(mContext);
        projectListAdapter.setAutoload(false);

        setListAdapter(projectListAdapter);
        projectListAdapter.loadObjects();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.projects_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ProjectsFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ProjectsFragmentListener");
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mCallback.onItemSelected(l, v, position);
    }

    public void createNewProject() {
        AlertDialog.Builder projectBuilder = new AlertDialog.Builder(mContext);
        projectBuilder.setTitle(R.string.action_new_project);
        // Get the layout inflater
        //LayoutInflater inflater = this.getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
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

                    ParseUser user = ParseUser.getCurrentUser();
                    newProject.put("createdBy", user);

                    ParseObject color = ParseObject.createWithoutData("color", "ESp9ejI3iO");
                    newProject.put("color", color);

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
                                projectListAdapter.loadObjects();
                            }
                        }
                    });
                }
            }
        });

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

                ParseObject projectToDelete = projectListAdapter.getItem(position);

                projectToDelete.deleteInBackground(new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.i("MAIN", "Error Deleting Project: " + e.getMessage());

                        } else {
                            projectListAdapter.loadObjects();
                        }
                    }
                });


            }
        });
        animation.setDuration(300);
        row.startAnimation(animation);
    }


}
