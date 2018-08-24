package com.mahta.rastin.broadcastapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.helper.DateConverter;
import com.mahta.rastin.broadcastapplication.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplication.model.Post;
import com.mahta.rastin.broadcastapplication.model.Staff;

import io.realm.RealmResults;

public class StaffAdapter extends RealmRecyclerViewAdapter<Staff,StaffAdapter.CustomViewHolder>{

    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public StaffAdapter(Context context, RealmResults<Staff> realmResults) {
        super(context, realmResults);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_staff_adapter_item,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final Staff current = realmResults.get(position); //This is nice
        String name = current.getFirst_name() + " " + current.getLast_name();
        holder.txtName.setText(name);

        holder.imgEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!current.getEmail().equals("null")){
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto",current.getEmail(), null));
                    context.startActivity(Intent.createChooser(emailIntent, "ارسال ایمیل"));
                }else
                    G.toastShort("ایمیلی ثبت نشده", context);

            }
        });

        if(!current.getImage().equals("null")) {

            byte[] decodedString = Base64.decode(current.getImage()
                            .replace("data:image/jpeg;base64,", ""),
                    Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(decodedByte);
        }else
            holder.image.setImageBitmap(G.getBitmapFromResources(context.getResources(), R.drawable.no_image));

    }

    @Override
    public int getItemCount() {
        return realmResults.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtName;
        ImageView image;
        ImageView imgEmail;
        RelativeLayout lnlListItem;

        private CustomViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            lnlListItem = itemView.findViewById(R.id.rtlListItem);
            image = itemView.findViewById(R.id.image);
            imgEmail = itemView.findViewById(R.id.imgEmail);

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