/*
 * Project:		Xecute
 *
 * Package:		app
 *
 * Author:		aaronburke
 *
 * Date:		 	2 16, 2014
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

/**
 * Created by aaronburke on 2/16/14.
 */
public class ProjectListAdapter extends ParseQueryAdapter<ParseObject> {
    Context mContext;
    public ProjectListAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery<ParseObject> create() {
                ParseQuery query = new ParseQuery("project");
                query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
                Log.i("QUERY", "Query = " + query);
                return query;
            }
        });
        mContext = context;
        Log.i("PARSEADAPTER", "context = " + mContext);
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        Log.i("getItemView", "fired");
        if (v == null) {
            v = View.inflate(mContext, R.layout.projects_list, null);
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

//        if (colorFile != null) {
//            colorImage.setParseFile(colorFile);
//            colorImage.loadInBackground(new GetDataCallback() {
//                @Override
//                public void done(byte[] data, ParseException e) {
//                    // nothing to do
//                }
//            });
//        }

        TextView projectName = (TextView) v.findViewById(R.id.text1);
        projectName.setText(object.getString("projectName"));
        Log.i("QUERY", "ProjectName = " + object.getString("projectName"));

        TextView projectDate = (TextView) v.findViewById(R.id.created_date);
        projectDate.setText((CharSequence) object.getDate("createdAt"));
        Log.i("QUERY", "createdAt = " + object.getDate("createdAt"));

        TextView projectStatus = (TextView) v.findViewById(R.id.project_status);
        projectStatus.setText(object.getString("status"));
        Log.i("QUERY", "status = " + object.getString("status"));

        return v;
    }
}
