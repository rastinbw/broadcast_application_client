package com.mahta.rastin.broadcastapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.model.DrawerItem;

public class DrawerItemCustomAdapter extends ArrayAdapter<DrawerItem> {

    private Context mContext;
    private int layoutResourceId;
    private DrawerItem data[];

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, DrawerItem[] data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = listItem.findViewById(R.id.textViewName);

        DrawerItem item = data[position];

        if (item.icon != 0)
            imageViewIcon.setImageResource(item.icon);
        textViewName.setText(item.name);

//        if (position == 2){
//            listItem.findViewById(R.id.imgDivider).setVisibility(View.VISIBLE);
//        }

        return listItem;
    }
}