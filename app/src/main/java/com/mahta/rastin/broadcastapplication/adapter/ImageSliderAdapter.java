package com.mahta.rastin.broadcastapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.interfaces.OnItemClickListener;

import java.util.List;

public class ImageSliderAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> imageList;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public ImageSliderAdapter(Context context, List<String> list) {
        mContext = context;
        imageList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        ViewGroup imageLayout = (ViewGroup) inflater.inflate(R.layout.layout_slider_item, collection, false);

        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClicked(v, position);
            }
        });

        byte[] decodedString = Base64.decode(imageList.get(position)
                .replace("data:image/jpeg;base64,", ""),
                Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        ((ImageView) imageLayout.findViewById(R.id.imageView)).setImageBitmap(decodedByte);
        collection.addView(imageLayout);
        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}