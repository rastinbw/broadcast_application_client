package com.mahta.rastin.broadcastapplication.activity.registration;


import android.app.Fragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andexert.library.RippleView;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.UserToken;
import com.wang.avi.AVLoadingIndicatorView;

public class ForgetPasswordFragment extends Fragment {
    private EditTextPlus edtNationalCode;
    private AVLoadingIndicatorView loaderLogo;
    private RippleView btnSendLink;
    private boolean doesFragmentExists = true;
    private boolean isLoaded;

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);
        setupView(view);

        return view;
    }

    private void setupView(final View view) {
        edtNationalCode = view.findViewById(R.id.edtNationalCode);
        loaderLogo = view.findViewById(R.id.loaderLogo);

        ((ImageView)view.findViewById(R.id.imgEntrance))
                .setImageBitmap(G.getBitmapFromResources(getResources(), R.drawable.img_green_board));

        btnSendLink = view.findViewById(R.id.btnSendLink);
        btnSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.hideKeyboardFrom(getActivity(), view);
                if (validate())
                    send();
            }
        });

    }

    private boolean validate() {
        boolean valid = true;

        String nationalCode = edtNationalCode.getText().toString();

        if (nationalCode.isEmpty() || nationalCode.length() != 10) {
            edtNationalCode.setError(G.getStringFromResource(R.string.validation_error_national_code, getActivity()));
            valid = false;
        } else {
            edtNationalCode.setError(null);
        }

        return valid;
    }

    private void send(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_NATIONAL_CODE,edtNationalCode.getText().toString());
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

        new HttpCommand(HttpCommand.COMMAND_FORGET_PASSWORD,contentValues)
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
            case 0:
                G.i("Link is being sent");
                getActivity().getFragmentManager().popBackStack();
                break;
            case Keys.RESULT_INVALID_REQUEST:
                G.i("Invalid request");
                break;
            case Keys.RESULT_INVALID_NATIONAL_CODE:
                G.toastLong(G.getStringFromResource(
                        R.string.invalid_national_code,
                        getActivity()
                ), getActivity());
                break;

        }
    }

    private void changeState(int state){
        if (state == 0){
            isLoaded = false;
            loaderLogo.setVisibility(View.VISIBLE);
            btnSendLink.setEnabled(false);

            btnSendLink.setBackground(
                    getActivity().getResources().
                            getDrawable(R.drawable.shape_button_disable)
            );
        }else {
            loaderLogo.setVisibility(View.GONE);
            btnSendLink.setEnabled(true);
            isLoaded = !(state == 1);

            btnSendLink.setBackground(
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
