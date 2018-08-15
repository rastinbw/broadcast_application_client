package com.mahta.rastin.broadcastapplication.activity.startup;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andexert.library.RippleView;
import com.mahta.rastin.broadcastapplication.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplication.dialog.AuthorizationDialog;
import com.mahta.rastin.broadcastapplication.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.main.MainActivity;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.UserToken;
import com.wang.avi.AVLoadingIndicatorView;

public class EntranceFragment extends Fragment {
    private EditTextPlus edtNationalCode;
    private EditTextPlus edtPassword;
    private AVLoadingIndicatorView loaderLogo;
    private RippleView btnEnterAsStudent;
    private RippleView btnEnterAsGuest;
    private TextViewPlus txtAuthorize;
    private boolean doesFragmentExists = true;
    private boolean isLoaded;

    public EntranceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrance, container, false);
        setupView(view);

        return view;
    }

    private void setupView(View view) {
        edtNationalCode = view.findViewById(R.id.edtNationalCode);
        edtPassword = view.findViewById(R.id.edtPassword);
        loaderLogo = view.findViewById(R.id.loaderLogo);

        ((ImageView)view.findViewById(R.id.imgEntrance))
                .setImageBitmap(G.getBitmapFromResources(getResources(), R.drawable.img_green_board));

        btnEnterAsGuest = view.findViewById(R.id.btnEnterAsGuest);
        btnEnterAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }, Constant.RIPPLE_EFFECT_DELAY);
            }
        });
        if (( (StartupActivity)getActivity()).registerFromInside){
            btnEnterAsGuest.setVisibility(View.GONE);
        }

        btnEnterAsStudent = view.findViewById(R.id.btnEnterAsStudent);
        btnEnterAsStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                    login();
            }
        });

        txtAuthorize = view.findViewById(R.id.txtAuthorize);
        txtAuthorize.setPaintFlags(txtAuthorize.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        txtAuthorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthorizationDialog dialog = new AuthorizationDialog(getActivity());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });


    }

    private boolean validate() {

        boolean valid = true;

        String nationalCode = edtNationalCode.getText().toString();

        // TODO: 7/29/18 validate password later
//        String password = edtPassword.getText().toString();
//
//        if (password.isEmpty()) {
//            edtPassword.setError(G.getStringFromResource(R.string.validation_error_password, getActivity()));
//            valid = false;
//        } else {
//            edtPassword.setError(null);
//        }

        if (nationalCode.isEmpty() || nationalCode.length() != 10) {
            edtNationalCode.setError(G.getStringFromResource(R.string.validation_error_national_code, getActivity()));
            valid = false;
        } else {
            edtNationalCode.setError(null);
        }

        return valid;
    }

    private void login(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_NATIONAL_CODE,edtNationalCode.getText().toString());
        contentValues.put(Keys.KEY_PASSWORD,edtPassword.getText().toString());
        changeState(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (doesFragmentExists) {
                    if (!isLoaded) {
                        G.toastLong(
                                G.getStringFromResource(R.string.network_error, getActivity()),
                                getActivity()
                        );
                        changeState(1);
                    }
                }
            }
        }, Constant.TIME_OUT);

        new HttpCommand(HttpCommand.COMMAND_LOGIN,contentValues)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        if (doesFragmentExists) {
                            changeState(2);
                            processResult(result);
                        }
                    }
                }).execute();
    }

    private void processResult(String result) {
        int resultCode = JSONParser.getResultCodeFromJson(result);
        switch (resultCode) {
            case Keys.RESULT_SUCCESS:
                G.i("loginok");
                String tokenString = JSONParser.parseToken(result);
                UserToken token = new UserToken();
                token.setToken(tokenString);
                RealmController.getInstance().addUserToken(token);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (((StartupActivity) getActivity()).registerFromInside) {
                            getActivity().setResult(Keys.LOGIN_OK);
                            getActivity().finish();
                        } else {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            G.toastLong(G.getStringFromResource(R.string.login_ok, getActivity()), getActivity());
                            getActivity().finish();
                        }
                    }
                }, Constant.RIPPLE_EFFECT_DELAY);
                break;
            case Keys.RESULT_INVALID_NATIONAL_CODE:
                G.toastLong(G.getStringFromResource(
                        R.string.invalid_national_code,
                        getActivity()
                ), getActivity());
                break;
            case Keys.RESULT_INVALID_PASSWORD:
                G.toastLong(G.getStringFromResource(
                        R.string.invalid_password,
                        getActivity()
                ), getActivity());
                break;
        }
    }

    private void changeState(int state){
        if (state == 0){
            isLoaded = false;
            loaderLogo.setVisibility(View.VISIBLE);
            btnEnterAsStudent.setEnabled(false);
            btnEnterAsGuest.setEnabled(false);
            txtAuthorize.setEnabled(false);

            btnEnterAsStudent.setBackground(
                    getActivity().getResources().
                            getDrawable(R.drawable.shape_button_disable)
            );

            btnEnterAsGuest.setBackground(
                    getActivity().getResources().
                            getDrawable(R.drawable.shape_button_disable)
            );
        }else {
            loaderLogo.setVisibility(View.GONE);
            btnEnterAsStudent.setEnabled(true);
            btnEnterAsGuest.setEnabled(true);
            txtAuthorize.setEnabled(true);
            isLoaded = !(state == 1);

            btnEnterAsStudent.setBackground(
                    getActivity().getResources().
                            getDrawable(R.drawable.shape_button)
            );

            btnEnterAsGuest.setBackground(
                    getActivity().getResources().
                            getDrawable(R.drawable.shape_button)
            );
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doesFragmentExists = false;
    }

}
