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
import com.mahta.rastin.broadcastapplication.model.Message;
import com.mahta.rastin.broadcastapplication.model.Post;

import io.realm.RealmResults;

public class MessageAdapter extends RealmRecyclerViewAdapter<Message,MessageAdapter.CustomViewHolder>{
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public MessageAdapter(Context context, RealmResults<Message> realmResults) {
        super(context, realmResults);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public MessageAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_message_adapter_item,parent,false);
        return new MessageAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.CustomViewHolder holder, int position) {
        final Message current = realmResults.get(position); //This is nice
        holder.txtTitle.setText(current.getTitle());
        holder.txtContent.setText(current.getContent());

        try {
            DateConverter converter = new DateConverter();
            String[] date = current.getCreated_at().split(" ")[0].split("-");
            holder.txtDate.setText(converter.GregorianToPersian(
                    Integer.parseInt(date[0]),
                    Integer.parseInt(date[1]),
                    Integer.parseInt(date[2])
            ).toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        if (G.realmController.hasReadPost(current.getId(), Constant.CATEGORY_ID_MESSAGE)) {
            holder.lnlListItem.setBackgroundColor(Color.rgb(237, 237, 237));
        }else {
            holder.lnlListItem.setBackgroundColor(Color.rgb(255, 255, 255));
        }

    }

    @Override
    public int getItemCount() {
        return realmResults.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtTitle;
        TextView txtContent;
        TextView txtDate;
        LinearLayout lnlListItem;

        private CustomViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtDate = itemView.findViewById(R.id.txtDate);
            lnlListItem = itemView.findViewById(R.id.lnlListItem);

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
