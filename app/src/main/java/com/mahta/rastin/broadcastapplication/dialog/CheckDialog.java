package com.mahta.rastin.broadcastapplication.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import com.mahta.rastin.broadcastapplication.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplication.R;


public class CheckDialog extends Dialog {

    public Activity activity;
    public Dialog dialog;
    public TextViewPlus txtContent;
    private ImageView imgLogo;
    private String content;
    private View.OnClickListener onConfirmClickListener;

    public CheckDialog(Activity activity, String content, View.OnClickListener onConfirmClickListener) {
        super(activity);
        this.activity = activity;
        this.content = content;
        this.onConfirmClickListener = onConfirmClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_check_dialog);
        imgLogo = findViewById(R.id.imgLogo);
        txtContent = findViewById(R.id.txtContent);

        txtContent.setText(content);

        if (onConfirmClickListener != null)
            findViewById(R.id.btnConfirm).setOnClickListener(onConfirmClickListener);

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public void setOnConfirmClickListener(View.OnClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }
}
