package com.mahta.rastin.broadcastapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.model.Group;

import java.util.ArrayList;


public class GroupSpinnerAdapter extends ArrayAdapter<Group>{

    private ArrayList<Group> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtTitle;
    }

    public GroupSpinnerAdapter(ArrayList<Group> data, Context context) {
        super(context, R.layout.layout_group_adapter_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Group dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_group_adapter_item, parent, false);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        G.i(dataModel.getTitle());
        viewHolder.txtTitle.setText(dataModel.getTitle());
        // Return the completed view to render on screen
        return convertView;
    }
}
