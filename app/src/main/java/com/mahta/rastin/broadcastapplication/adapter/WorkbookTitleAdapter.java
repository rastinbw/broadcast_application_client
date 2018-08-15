package com.mahta.rastin.broadcastapplication.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.interfaces.OnItemClickListener;

import java.util.List;

public class WorkbookTitleAdapter extends RecyclerView.Adapter<WorkbookTitleAdapter.CustomViewHolder> {
    private OnItemClickListener onItemClickListener;
    private LayoutInflater inflater;
    private List<String> data;

    public WorkbookTitleAdapter(Context context, List<String> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public WorkbookTitleAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_workbook_title_adapter_item,parent,false);
        return new WorkbookTitleAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkbookTitleAdapter.CustomViewHolder holder, int position) {
        final String current = data.get(position); //This is nice
        holder.txtTitle.setText(current);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtTitle;
        RelativeLayout rtlListItem;

        private CustomViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            rtlListItem = itemView.findViewById(R.id.rtlListItem);
            rtlListItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClicked(v,getAdapterPosition());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

}