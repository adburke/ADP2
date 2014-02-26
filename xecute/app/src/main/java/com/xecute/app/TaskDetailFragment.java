/*
 * Project:		Xecute
 *
 * Package:		app
 *
 * Author:		aaronburke
 *
 * Date:		 	2 25, 2014
 */

package com.xecute.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aaronburke on 2/25/14.
 */
public class TaskDetailFragment extends Fragment {

    Context mContext;
    MainActivity mainActivity;

    LinearLayout taskDetailLayout;
    TextView taskName;
    TextView projectName;
    TextView dueDate;
    TextView taskDescription;
    ProgressBar taskProgressBar;
    ListView commentList;

    CommentListAdapter commentListAdapter;

    ParseObject selectedTask;
    ParseObject taskProject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mContext = getActivity();
        mainActivity = (MainActivity)getActivity();
        selectedTask = mainActivity.selectedTask;

        setHasOptionsMenu(true);
        taskDetailLayout = (LinearLayout) inflater.inflate(R.layout.task_detail,null);

        taskName = (TextView) taskDetailLayout.findViewById(R.id.taskName);
        projectName = (TextView) taskDetailLayout.findViewById(R.id.projectName);
        dueDate = (TextView) taskDetailLayout.findViewById(R.id.dueDate);
        taskDescription = (TextView) taskDetailLayout.findViewById(R.id.taskDescription);
        taskProgressBar = (ProgressBar) taskDetailLayout.findViewById(R.id.taskProgressBar);

        commentList = (ListView) inflater.inflate(R.layout.comment_list,null);

        commentList.addHeaderView(taskDetailLayout);

        int percentCompleted = selectedTask.getInt("percentCompleted");
        taskProgressBar.setMax(100);
        taskProgressBar.setProgress(percentCompleted);

        taskName.setText(selectedTask.getString("taskName"));

        selectedTask.getParseObject("parentProject").fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject != null && e == null) {
                    taskProject = parseObject;
                    projectName.setText(taskProject.getString("projectName"));
                }
            }
        });

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date date = selectedTask.getDate("dueDate");
        String dateStr = df.format(date);
        dueDate.setText(dateStr);

        taskDescription.setText(selectedTask.getString("taskDescription"));

        commentListAdapter = new CommentListAdapter(mContext, selectedTask);
        commentList.setAdapter(commentListAdapter);
        commentListAdapter.loadObjects();

        return commentList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_edit:
                Log.i("MAIN", "Edit Task selected.");
                return true;
            case R.id.action_comment:
                Log.i("MAIN", "Comment on Task selected.");
                createNewComment();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void createNewComment() {
        AlertDialog.Builder projectBuilder = new AlertDialog.Builder(mContext);
        projectBuilder.setTitle(R.string.new_comment);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View view = inflater.inflate(R.layout.create_comment, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        projectBuilder.setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
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

                EditText commentText = (EditText) view.findViewById(R.id.commentText);
                TextView errorMessage = (TextView) view.findViewById(R.id.project_name_error);

                final ParseObject comment = new ParseObject("comments");
                if (commentText.getText().toString().isEmpty()) {
                    Log.i("Save Comment", "Comment is empty!");
                    errorMessage.setVisibility(View.VISIBLE);

                } else {

                    comment.put("relatedTask", selectedTask);
                    comment.put("commentText", commentText.getText().toString());
                    comment.put("owner", ParseUser.getCurrentUser());

                    ParseACL commentACL = new ParseACL(ParseUser.getCurrentUser());
                    commentACL.setPublicReadAccess(true);
                    comment.setACL(commentACL);

                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
//                                Log.i("MAIN", "Error saving comment: " + e.getMessage());
//                                try {
//                                    comment.delete();
//                                } catch (ParseException e1) {
//                                    e1.printStackTrace();
//                                }
                            } else {

                                commentListAdapter.loadObjects();

                            }
                        }
                    });
                    dialog.dismiss();
                }

            }
        });

    }

}
