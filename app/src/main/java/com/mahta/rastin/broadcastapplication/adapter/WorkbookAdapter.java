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
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.model.WorkbookTuple;

import java.util.List;

public class WorkbookAdapter extends RecyclerView.Adapter<WorkbookAdapter.CustomViewHolder> {

    private LayoutInflater inflater;
    private List<WorkbookTuple> data;
    private Context context;

    public WorkbookAdapter(Context context, List<WorkbookTuple> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_workbook_adapter_item,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final WorkbookTuple current = data.get(position); //This is nice

        try {
            int scale = current.getScale();
            if (Integer.parseInt(current.getGrade()) <= (scale/4)*3 &&
                    Integer.parseInt(current.getGrade()) > (scale/4)*2) {
                holder.txtGrade.setTextColor(G.getColorFromResource(R.color.colorSecondary, context));
            }else if (Integer.parseInt(current.getGrade()) <= (scale/4)*2 &&
                    Integer.parseInt(current.getGrade()) > (scale/4)){
                holder.txtGrade.setTextColor(G.getColorFromResource(R.color.colorSecondaryDark, context));
            }else if (Integer.parseInt(current.getGrade()) <= (scale/4)){
                holder.txtGrade.setTextColor(G.getColorFromResource(R.color.colorRed, context));
            }

            holder.txtLesson.setText(current.getLesson());
            if (scale == 100) {
                String grade = current.getGrade() + " ٪";
                holder.txtGrade.setText(grade);
            }
            else
                holder.txtGrade.setText(current.getGrade());
        }catch (Exception e){
            e.printStackTrace();
        }

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