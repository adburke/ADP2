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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

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

    ScrollView taskDetailLayout;
    TextView taskName;
    TextView projectName;
    TextView dueDate;
    TextView taskDescription;
    ProgressBar taskProgressBar;

    ParseObject selectedTask;
    ParseObject taskProject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mContext = getActivity();
        mainActivity = (MainActivity)getActivity();
        selectedTask = mainActivity.selectedTask;

        taskDetailLayout = (ScrollView) inflater.inflate(R.layout.task_detail, container, false);

        taskName = (TextView) taskDetailLayout.findViewById(R.id.taskName);
        projectName = (TextView) taskDetailLayout.findViewById(R.id.projectName);
        dueDate = (TextView) taskDetailLayout.findViewById(R.id.dueDate);
        taskDescription = (TextView) taskDetailLayout.findViewById(R.id.taskDescription);
        taskProgressBar = (ProgressBar) taskDetailLayout.findViewById(R.id.taskProgressBar);
        taskProgressBar.setMax(100);
        taskProgressBar.setProgress(75);

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


        return taskDetailLayout;
    }

}
