package com.mahta.rastin.broadcastapplication.activity.registration;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andexert.library.RippleView;
import com.mahta.rastin.broadcastapplication.custom.TextViewPlus;
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

public class LoginFragment extends Fragment {
    private EditTextPlus edtNationalCode;
    private EditTextPlus edtPassword;
    private AVLoadingIndicatorView loaderLogo;
    private RippleView btnEnterAsStudent;
    private RippleView btnEnterAsParent;
    private TextViewPlus txtRegister;
    private TextViewPlus txtForgetPassword;
    private boolean doesFragmentExists = true;
    private boolean isLoaded;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setupView(view);

        return view;
    }

    private void setupView(final View view) {
        edtNationalCode = view.findViewById(R.id.edtNationalCode);
        edtPassword = view.findViewById(R.id.edtPassword);
        loaderLogo = view.findViewById(R.id.loaderLogo);

        ((ImageView)view.findViewById(R.id.imgEntrance))
                .setImageBitmap(G.getBitmapFromResources(getResources(), R.drawable.img_green_board));

        btnEnterAsParent = view.findViewById(R.id.btnEnterAsParent);
        btnEnterAsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.hideKeyboardFrom(getActivity(), view);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack("LoginFragment")
                                .replace(R.id.frmRegistrationContainer,new LoginAsParentFragment())
                                .commit();
                    }
                }, Constant.RIPPLE_EFFECT_DELAY);
            }
        });

        btnEnterAsStudent = view.findViewById(R.id.btnEnterAsStudent);
        btnEnterAsStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.hideKeyboardFrom(getActivity(), view);
                if (validate())
                    login();
            }
        });

        txtRegister = view.findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.hideKeyboardFrom(getActivity(), view);
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack("LoginFragment")
                        .replace(R.id.frmRegistrationContainer,new RegisterFragment())
                        .commit();
            }
        });

        txtForgetPassword = view.findViewById(R.id.txtForgetPassword);
        txtForgetPassword.setPaintFlags(txtForgetPassword.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.hideKeyboardFrom(getActivity(), view);
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack("LoginFragment")
                        .replace(R.id.frmRegistrationContainer,new ForgetPasswordFragment())
                        .commit();
            }
        });

    }

    private boolean validate() {

        boolean valid = true;

        String nationalCode = edtNationalCode.getText().toString();

        String password = edtPassword.getText().toString();

        if (password.isEmpty()) {
            edtPassword.setError(G.getStringFromResource(R.string.validation_error_password_login, getActivity()));
            valid = false;
        } else {
            edtPassword.setError(null);
        }

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
                G.realmController.addUserToken(token, false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((RegistrationActivity)getActivity()).loginToMain();
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
            btnEnterAsParent.setEnabled(false);
            txtForgetPassword.setEnabled(false);
            txtRegister.setEnabled(false);

            btnEnterAsStudent.setBackground(
                    getActivity().getResources().
                            getDrawable(R.drawable.shape_button_disable)
            );

            btnEnterAsParent.setBackground(
                    getActivity().getResources().
                            getDrawable(R.drawable.shape_button_disable)
            );
        }else {
            loaderLogo.setVisibility(View.GONE);
            btnEnterAsStudent.setEnabled(true);
            btnEnterAsParent.setEnabled(true);
            txtRegister.setEnabled(true);
            txtForgetPassword.setEnabled(true);
            isLoaded = !(state == 1);

            btnEnterAsStudent.setBackground(
                    getActivity().getResources().
                            getDrawable(R.drawable.shape_button)
            );

            btnEnterAsParent.setBackground(
                    getActivity().getResources().
                            getDrawable(R.drawable.shape_button_secondary)
            );
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doesFragmentExists = false;
    }

}
