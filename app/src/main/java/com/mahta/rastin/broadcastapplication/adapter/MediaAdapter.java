package com.mahta.rastin.broadcastapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.helper.DateConverter;
import com.mahta.rastin.broadcastapplication.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplication.model.Media;

import io.realm.RealmResults;

public class MediaAdapter extends RealmRecyclerViewAdapter<Media,MediaAdapter.CustomViewHolder>{
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public MediaAdapter(Context context, RealmResults<Media> realmResults) {
        super(context, realmResults);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_media_adapter_item,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final Media current = realmResults.get(position); //This is nice
        holder.txtTitle.setText(current.getTitle());

        try {
            DateConverter converter = new DateConverter();
            String[] date = current.getDate().split(" ")[0].split("-");
            holder.txtDate.setText(converter.GregorianToPersian(
                    Integer.parseInt(date[0]),
                    Integer.parseInt(date[1]),
                    Integer.parseInt(date[2])
            ).toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        if (G.realmController.hasReadPost(current.getId(), Constant.CATEGORY_ID_MEDIA)) {
            holder.lnlListItem.setBackgroundColor(Color.rgb(237, 237, 237));
        }else {
            holder.lnlListItem.setBackgroundColor(Color.rgb(255, 255, 255));
        }

        holder.imgLogo.setImageBitmap(G.getBitmapFromResources(context.getResources(), R.drawable.img_media));
    }

    @Override
    public int getItemCount() {
        return realmResults.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtTitle;
        TextView txtDate;
        LinearLayout lnlListItem;
        ImageView imgLogo;

        private CustomViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            lnlListItem = itemView.findViewById(R.id.lnlListItem);
            imgLogo = itemView.findViewById(R.id.imgLogo);
            lnlListItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClicked(view,getAdapterPosition());
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}