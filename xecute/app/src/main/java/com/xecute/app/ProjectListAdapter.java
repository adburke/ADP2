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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public ProjectListAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery<ParseObject> create() {
                ParseQuery query = new ParseQuery("Project");
                query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
                return query;
            }
        });
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.projects_list, null);
        }

        super.getItemView(object, v, parent);

        ParseImageView colorImage = (ParseImageView) v.findViewById(R.id.icon);
        ParseObject color = object.getParseObject("color");
        ParseFile colorFile = color.getParseFile("colorImage");
        if (colorFile != null) {
            colorImage.setParseFile(colorFile);
            colorImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }

        TextView projectName = (TextView) v.findViewById(R.id.text1);
        projectName.setText(object.getString("projectName"));

        TextView projectDate = (TextView) v.findViewById(R.id.created_date);
        projectDate.setText((CharSequence) object.getDate("createdAt"));

        TextView projectStatus = (TextView) v.findViewById(R.id.project_status);
        projectStatus.setText(object.getString("status"));

        return v;
    }
}
