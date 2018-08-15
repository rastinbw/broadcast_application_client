package com.mahta.rastin.broadcastapplication.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.mahta.rastin.broadcastapplication.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.startup.StartupActivity;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.Student;
import com.wang.avi.AVLoadingIndicatorView;

public class AuthorizationDialog extends Dialog{

    public Activity activity;
    public Dialog dialog;
    public EditTextPlus edtNationalCode;
    private AVLoadingIndicatorView loaderLogo;
    private ImageView imgLogo;
    private boolean isLoaded;

    public AuthorizationDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_authorize_dialog);

        edtNationalCode = findViewById(R.id.edtNationalCode);
        loaderLogo = findViewById(R.id.loaderLogo);
        imgLogo = findViewById(R.id.imgLogo);

        findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                    authorize();
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private boolean validate() {

        boolean valid = true;

        String nationalCode = edtNationalCode.getText().toString();

        if (nationalCode.isEmpty() || nationalCode.length() != 10) {
            edtNationalCode.setError(G.getStringFromResource(R.string.validation_error_national_code, getContext()));
            valid = false;
        } else {
            edtNationalCode.setError(null);
        }

        return valid;
    }

    private void authorize(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_NATIONAL_CODE,edtNationalCode.getText().toString());
        changeState(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLoaded) {
                    G.toastLong(
                            G.getStringFromResource(R.string.network_error, getContext()),
                            getContext()
                    );
                    changeState(1);
                }
            }
        }, Constant.TIME_OUT);

        new HttpCommand(HttpCommand.COMMAND_AUTHORIZE,contentValues)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        changeState(1);
                        processResult(result);
                    }
                }).execute();
    }

    private void processResult(String result) {
        int resultCode = JSONParser.getResultCodeFromJson(result);
        if (resultCode == Keys.RESULT_SUCCESS){
            Student student = JSONParser.parseStudent(result);
            if (student!=null)
                student.setNational_code(edtNationalCode.getText().toString());

            ((StartupActivity)activity).setCurrentStudent(student);
            ((StartupActivity)activity).bringConfirmFragment();
            dismiss();
        }else if (resultCode == Keys.RESULT_INVALID_NATIONAL_CODE){
            G.toastLong(G.getStringFromResource(
                    R.string.invalid_national_code,
                    getContext()
            ),getContext());
        }
    }

    private void changeState(int state){
        if (state == 0){
            isLoaded = false;
            loaderLogo.setVisibility(View.VISIBLE);
            imgLogo.setVisibility(View.GONE);
        }else {
            isLoaded = true;
            loaderLogo.setVisibility(View.GONE);
            imgLogo.setVisibility(View.VISIBLE);

        }
    }

}
