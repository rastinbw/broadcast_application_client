package com.mahta.rastin.broadcastapplication.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.model.WorkbookTuple;

import java.util.List;

public class WorkbookAdapter extends RecyclerView.Adapter<WorkbookAdapter.CustomViewHolder> {

    private LayoutInflater inflater;
    private List<WorkbookTuple> data;

    public WorkbookAdapter(Context context, List<WorkbookTuple> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_workbook_adapter_item,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final WorkbookTuple current = data.get(position); //This is nice
        holder.txtLesson.setText(current.getLesson());
        holder.txtGrade.setText(current.getGrade());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView txtLesson;
        TextView txtGrade;
        LinearLayout lnlListItem;

        private CustomViewHolder(View itemView) {
            super(itemView);
            txtLesson = itemView.findViewById(R.id.txtLesson);
            txtGrade = itemView.findViewById(R.id.txtGrade);
        }
    }

}