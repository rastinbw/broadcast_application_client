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
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.main.MainActivity;
import com.mahta.rastin.broadcastapplication.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplication.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.UserToken;
import com.wang.avi.AVLoadingIndicatorView;

public class LoginAsParentFragment extends Fragment {
    private EditTextPlus edtParentCode;
    private AVLoadingIndicatorView loaderLogo;
    private RippleView btnEnterAsParent;
    private boolean doesFragmentExists = true;
    private boolean isLoaded;

    public LoginAsParentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_as_parent, container, false);
        setupView(view);

        return view;
    }

    private void setupView(final View view) {
        edtParentCode = view.findViewById(R.id.edtParentCode);
        loaderLogo = view.findViewById(R.id.loaderLogo);

        ((ImageView)view.findViewById(R.id.imgEntrance))
                .setImageBitmap(G.getBitmapFromResources(getResources(), R.drawable.img_green_board));

        btnEnterAsParent = view.findViewById(R.id.btnEnterAsParent);
        btnEnterAsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.hideKeyboardFrom(getActivity(), view);
                if (validate())
                    login();
            }
        });

    }

    private boolean validate() {

        boolean valid = true;

        String parentCode = edtParentCode.getText().toString();

        if (parentCode.isEmpty() || parentCode.length() > 20) {
            edtParentCode.setError(G.getStringFromResource(R.string.validation_error_parent_code, getActivity()));
            valid = false;
        } else {
            edtParentCode.setError(null);
        }

        return valid;
    }

    private void login(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_PARENT_CODE, edtParentCode.getText().toString());
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

        new HttpCommand(HttpCommand.COMMAND_LOGIN_AS_PARENT,contentValues)
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
                String tokenString = JSONParser.parseToken(result);
                UserToken token = new UserToken();
                token.setToken(tokenString);
                G.realmController.addUserToken(token, true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((RegistrationActivity)getActivity()).loginToMain();
                    }
                }, Constant.RIPPLE_EFFECT_DELAY);
                break;
            case Keys.RESULT_INVALID_PARENT_CODE:
                G.toastLong(G.getStringFromResource(
                        R.string.invalid_parent_code,
                        getActivity()
                ), getActivity());
                break;
            case Keys.RESULT_USER_NOT_REGISTERED:
                G.toastLong(G.getStringFromResource(
                        R.string.user_not_registered,
                        getActivity()
                ), getActivity());
                break;
        }
    }

    private void changeState(int state){
        if (state == 0){
            isLoaded = false;
            loaderLogo.setVisibility(View.VISIBLE);
            btnEnterAsParent.setEnabled(false);

            btnEnterAsParent.setBackground(
                    getActivity().getResources().
                            getDrawable(R.drawable.shape_button_disable)
            );
        }else {
            loaderLogo.setVisibility(View.GONE);
            btnEnterAsParent.setEnabled(true);
            isLoaded = !(state == 1);

            btnEnterAsParent.setBackground(
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
