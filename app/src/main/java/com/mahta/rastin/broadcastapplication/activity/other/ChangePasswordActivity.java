package com.mahta.rastin.broadcastapplication.activity.other;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.wang.avi.AVLoadingIndicatorView;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtCurrentPassword;
    private EditText edtNewtPassword;
    private EditText edtNewPasswordRepeat;
    private TextView txtPasswordError;
    private TextView txtNewPasswordError;
    private TextView txtNewPasswordRepeatError;
    private TextView txtConfirm;

    private AVLoadingIndicatorView loaderLogo;
    private RelativeLayout btnConfirm;

    private boolean isLoaded;
    private int messageSendResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setupToolbar();

        btnConfirm = findViewById(R.id.btnConfirm);
        txtConfirm = findViewById(R.id.txtConfirm);
        loaderLogo = findViewById(R.id.loaderLogo);

        edtCurrentPassword = findViewById(R.id.edtPassword);
        edtNewtPassword = findViewById(R.id.edtNewPassword);
        edtNewPasswordRepeat = findViewById(R.id.edtNewPasswordRepeat);
        txtPasswordError = findViewById(R.id.txtMessagePasswordError);
        txtNewPasswordError = findViewById(R.id.txtMessageNewPasswordError);
        txtNewPasswordRepeatError = findViewById(R.id.txtMessageNewPasswordRepeatError);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });
    }

    private void confirm() {
        G.hideKeyboard(ChangePasswordActivity.this);
        if (G.isNetworkAvailable(ChangePasswordActivity.this)){
            if (validate()){
                changePassword();
            }
        }else
            G.toastLong(G.getStringFromResource(R.string.no_internet,
                    ChangePasswordActivity.this),
                    ChangePasswordActivity.this);
    }

    private boolean validate() {

        boolean valid = true;

        String password = edtCurrentPassword.getText().toString();
        String newPassword = edtNewtPassword.getText().toString();
        String newPasswordRepeat = edtNewPasswordRepeat.getText().toString();

        if (password.isEmpty()) {
            txtPasswordError.setVisibility(View.VISIBLE);
            findViewById(R.id.lnlPassword).setBackgroundColor(G.getColorFromResource
                    (R.color.colorRedLighter, ChangePasswordActivity.this)
            );
            valid = false;
        } else {
            txtPasswordError.setVisibility(View.GONE);
            findViewById(R.id.lnlPassword).setBackgroundColor(Color.WHITE);
        }


        if (newPassword.isEmpty() || newPassword.length() < 6 || newPassword.length() > 18) {
            txtNewPasswordError.setVisibility(View.VISIBLE);
            findViewById(R.id.lnlNewPassword).setBackgroundColor(G.getColorFromResource
                    (R.color.colorRedLighter, ChangePasswordActivity.this)
            );
            valid = false;
        } else {
            txtNewPasswordError.setVisibility(View.GONE);
            findViewById(R.id.lnlNewPassword).setBackgroundColor(Color.WHITE);
        }


        if (newPasswordRepeat.isEmpty() || !newPasswordRepeat.equals(newPassword)) {
            txtNewPasswordRepeatError.setVisibility(View.VISIBLE);
            findViewById(R.id.lnlNewPasswordRepeat).setBackgroundColor(G.getColorFromResource
                    (R.color.colorRedLighter, ChangePasswordActivity.this)
            );
            valid = false;
        } else {
            txtNewPasswordRepeatError.setVisibility(View.GONE);
            findViewById(R.id.lnlNewPasswordRepeat).setBackgroundColor(Color.WHITE);
        }

        return valid;

    }

    private void changePassword() {
        ContentValues params = new ContentValues();
        params.put(Keys.KEY_PASSWORD, edtCurrentPassword.getText().toString());
        params.put(Keys.KEY_NEW_PASSWORD,edtNewtPassword.getText().toString());
        params.put(Keys.KEY_TOKEN, G.realmController.getUserToken().getToken());
        changeState(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLoaded) {
                    G.toastLong(
                            G.getStringFromResource(R.string.network_error, ChangePasswordActivity.this),
                            ChangePasswordActivity.this
                    );
                    changeState(1);
                }
            }
        }, Constant.TIME_OUT);

        new HttpCommand(HttpCommand.COMMAND_CHANGE_PASSWORD,params)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        messageSendResult = JSONParser.getResultCodeFromJson(result);
                        changeState(2);
                        switch (messageSendResult) {
                            case Keys.RESULT_SUCCESS:
                                G.toastShort(G.getStringFromResource(R.string.change_password_successfully,
                                        getApplicationContext()), getApplicationContext());
                                finish();
                                break;
                            case Keys.RESULT_INVALID_PASSWORD:
                                G.toastShort(G.getStringFromResource(R.string.wrong_password,
                                        getApplicationContext()), getApplicationContext());
                                break;
                            default:
                                G.toastShort(G.getStringFromResource(R.string.change_password_not_successfully,
                                        getApplicationContext()), getApplicationContext());
                                break;
                        }
                    }
                }).execute();
    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar.findViewById(R.id.imgCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        ((TextView)toolbar.findViewById(R.id.txtTitle)).setText(R.string.change_password);
    }

    private void changeState(int state){
        if (state == 0){
            isLoaded = false;
            loaderLogo.setVisibility(View.VISIBLE);
            txtConfirm.setVisibility(View.GONE);
            btnConfirm.setEnabled(false);
        }else {
            isLoaded = !(state == 1);
            loaderLogo.setVisibility(View.GONE);
            txtConfirm.setVisibility(View.VISIBLE);
            btnConfirm.setEnabled(true);
        }
    }

}
