package com.mahta.rastin.broadcastapplication.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.adapter.GroupAdapter;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplication.model.Group;

import java.util.List;


public class GroupListDialog extends Dialog {

    public Activity activity;
    public Dialog dialog;
    private com.mahta.rastin.broadcastapplication.interfaces.OnDismissListener onDismissListener;
    private String groupId = "null";
    private String groupTitle = "همه گروه ها";

    public GroupListDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_group_list_dialog);

        final List<Group> list = G.realmController.getGroupList();
        GroupAdapter adapter = new GroupAdapter(activity, list);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                groupId = Integer.toString(list.get(position).getId());
                groupTitle = list.get(position).getTitle();
                dismiss();
            }
        });

        findViewById(R.id.btnAllGroups).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupId = "null";
                groupTitle = "همه گروه ها";
                dismiss();
            }
        });


        final RecyclerView rcvGroups = findViewById(R.id.rcvGroups);
        rcvGroups.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        rcvGroups.setLayoutManager(linearLayoutManager);

        if (onDismissListener != null)
            setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onDismissListener.onDismiss(groupId, groupTitle);
                }
            });

    }

    public void setOnDismissListener(com.mahta.rastin.broadcastapplication.interfaces.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}