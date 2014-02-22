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


import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by aaronburke on 2/19/14.
 */
public class CreateTaskDialogFragment extends DialogFragment implements View.OnClickListener {

    Context mContext;

    CreateTaskDialogListener mCallback;
    private ActionMode mActionMode;

    Button chooseDateBtn;
    Button taskUserBtn;
    TextView dateChosenTxt;
    EditText taskName;
    EditText taskDescription;
    ListView userList;

    DatePicker mDatePicker;

    int monthSelected, daySelected, yearSelected;
    Date taskDueDate;

    ArrayList<ParseUser> taskedUsers;
    UserListAdapter userListAdapter;


    public interface CreateTaskDialogListener {
        public void onTaskCreate(String taskNameStr, Date date , ArrayList<ParseUser> users, String taskDescriptionStr);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LinearLayout createTaskView = (LinearLayout) inflater.inflate(R.layout.create_task, container, false);

        mContext = getActivity();

        chooseDateBtn = (Button) createTaskView.findViewById(R.id.chooseDateBtn);
        chooseDateBtn.setOnClickListener(this);
        taskUserBtn = (Button) createTaskView.findViewById(R.id.taskUsersBtn);
        taskUserBtn.setOnClickListener(this);

        dateChosenTxt = (TextView) createTaskView.findViewById(R.id.dateChosen);
        taskName = (EditText) createTaskView.findViewById(R.id.taskName);
        taskDescription = (EditText) createTaskView.findViewById(R.id.taskDescription);
        userList = (ListView) createTaskView.findViewById(R.id.userList);

        taskedUsers = new ArrayList<ParseUser>();
        userListAdapter = new UserListAdapter(mContext,taskedUsers);
        userList.setAdapter(userListAdapter);

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
                    saveTaskData();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (CreateTaskDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CreateTaskDialogListener");
        }
    }


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

                final DatePickerDialog datePickerDialog;
                if (daySelected != 0 && monthSelected != 0 && yearSelected != 0) {
                    datePickerDialog = new DatePickerDialog(
                            mContext, null, yearSelected, monthSelected-1, daySelected) {
                    };

                } else {
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                    datePickerDialog = new DatePickerDialog(mContext, null, mYear, mMonth, mDay) {
                    };
                }


                mDatePicker = datePickerDialog.getDatePicker();

                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_NEGATIVE) {
                                    dialog.cancel();
                                }
                            }
                        });

                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Done",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    setDate(mDatePicker);
                                    dialog.dismiss();
                                }
                            }
                        });

                datePickerDialog.setCancelable(true);
                datePickerDialog.show();
                break;

            case R.id.taskUsersBtn:
                Log.i("CREATE_TASK", "Task User btn");
                taskUser();
                break;
        }
    }

    void taskUser() {
        AlertDialog.Builder projectBuilder = new AlertDialog.Builder(mContext);
        projectBuilder.setTitle(R.string.user_name_input);
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
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", userName.getText().toString());
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                updateTaskedUsersList(objects.get(0));
                                dialog.dismiss();
                            } else {
                                Log.i("Query_USER", "FAILED due to: " + e.getMessage());
                                dialog.dismiss();
                            }
                        }
                    });
                }

            }
        });
    }

    public void setDate(DatePicker datePicker) {
        monthSelected = datePicker.getMonth();
        daySelected = datePicker.getDayOfMonth();
        yearSelected = datePicker.getYear();

        dateChosenTxt.setText(monthSelected+1 + "/" + daySelected + "/" + yearSelected);
        Calendar calendar = Calendar.getInstance();
        calendar.set(yearSelected,monthSelected,daySelected);
        taskDueDate = calendar.getTime();
    }

    public void saveTaskData() {
        String taskNameStr = taskName.getText().toString();
        Date date = taskDueDate;
        String taskDescriptionStr = taskDescription.getText().toString();


        mCallback.onTaskCreate(taskNameStr,date,this.taskedUsers,taskDescriptionStr);
    }

    public void updateTaskedUsersList(ParseUser user) {
        Log.i("PARSE_USER_ADD", user.getUsername());
        taskedUsers.add(user);
        userListAdapter.notifyDataSetChanged();
    }

}
