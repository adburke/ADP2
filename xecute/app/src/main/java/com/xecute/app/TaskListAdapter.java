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

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aaronburke on 2/18/14.
 */
public class TaskListAdapter extends ParseQueryAdapter<ParseObject> {
    Context mContext;

    public TaskListAdapter(Context context, final ParseObject project) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery<ParseObject> create() {
                ParseQuery query = new ParseQuery("task");
                query.whereEqualTo("parentProject", project);
                Log.i("QUERY", "parentProject = " + project.getObjectId());
                return query;
            }
        });
        mContext = context;
        Log.i("PARSEADAPTER", "context = " + mContext);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        Log.i("getItemView", "fired");
        if (v == null) {
            v = View.inflate(mContext, R.layout.project_list_row, null);
            Log.i("PARSEADAPTER", "Layout Inflated");
        }
        super.getItemView(object, v, parent);

        final ParseImageView colorImage = (ParseImageView) v.findViewById(R.id.icon);

        object.getParseObject("color").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                ParseFile colorFile = object.getParseFile("colorImage");
                if (colorFile != null) {
                    colorImage.setParseFile(colorFile);
                    colorImage.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            // nothing to do
                        }
                    });
                }
            }
        });

        TextView projectName = (TextView) v.findViewById(R.id.text1);
        projectName.setText(object.getString("taskName"));
        Log.i("QUERY", "taskName = " + object.getString("taskName"));

        TextView projectDate = (TextView) v.findViewById(R.id.created_date);
        if (object.getDate("dueDate") != null) {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date date = object.getDate("dueDate");
            String dateStr = df.format(date);
            projectDate.setText(dateStr);
            Log.i("QUERY", "dueDate = " + dateStr);
        }

        TextView projectStatus = (TextView) v.findViewById(R.id.project_status);
        String percent = Integer.toString(object.getInt("percentCompleted"));
        projectStatus.setText(object.getInt(percent)+"%");
        Log.i("QUERY", "status = " + object.getString("status"));

        return v;
    }
}