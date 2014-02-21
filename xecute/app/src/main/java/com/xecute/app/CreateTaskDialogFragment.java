/*
 * Project:		Xecute
 *
 * Package:		app
 *
 * Author:		aaronburke
 *
 * Date:		 	2 19, 2014
 */

package com.xecute.app;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by aaronburke on 2/19/14.
 */
public class CreateTaskDialogFragment extends DialogFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    Context mContext;
    private ActionMode mActionMode;
    Button chooseDateBtn;
    Button taskUserBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LinearLayout createTaskView = (LinearLayout) inflater.inflate(R.layout.create_task, container, false);

        mContext = getActivity();

        chooseDateBtn = (Button) createTaskView.findViewById(R.id.chooseDateBtn);
        chooseDateBtn.setOnClickListener(this);
        taskUserBtn = (Button) createTaskView.findViewById(R.id.taskUsersBtn);
        taskUserBtn.setOnClickListener(this);


        return createTaskView;
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Start the CAB using the ActionMode.Callback defined above
        mActionMode = getActivity().startActionMode(mActionModeCallback);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.task_context_menu, menu);
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
                case R.id.item_save:
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
            Log.i("CREATE_TASK", "Exited!");
            getActivity().getSupportFragmentManager().popBackStack();
        }
    };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseDateBtn:
                Log.i("CREATE_TASK", "Choose date btn");
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        mContext, this, mYear, mMonth, mDay) {

                };

                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_NEGATIVE) {
                                    dialog.cancel();
                                }
                            }
                        });

                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
                break;

            case R.id.taskUsersBtn:
                Log.i("CREATE_TASK", "Task User btn");
                taskUser();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }

    void taskUser() {
        AlertDialog.Builder projectBuilder = new AlertDialog.Builder(mContext);
        projectBuilder.setTitle(R.string.action_new);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View view = inflater.inflate(R.layout.task_user, null);

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
            public void onClick(View v) {
                final Boolean[] wantToCloseDialog = {false};

                EditText userName = (EditText) view.findViewById(R.id.user_name);
                TextView errorMessage = (TextView) view.findViewById(R.id.user_name_error);
                if (userName.getText().toString().isEmpty()) {
                    Log.i("Save Project", "Username is empty!");
                    errorMessage.setVisibility(View.VISIBLE);

                } else {
                
                }

            }
        });
    }
}
