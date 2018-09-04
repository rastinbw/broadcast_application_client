package com.mahta.rastin.broadcastapplication.activity.registration;


import android.Manifest;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.chaos.view.PinView;
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
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;
import com.wang.avi.AVLoadingIndicatorView;


public class ConfirmFragment extends Fragment {
    private RippleView btnSendVerification;
    private TextView txtSendVerificationCode;
    private TextView txtSeconds;
    private LinearLayout lnlSeconds;
    private String selectedNationalCode;
    private AVLoadingIndicatorView loaderLogo;
    private PinView pinView;

    private Handler handler = new Handler();
    private boolean doesFragmentExists = true;
    private boolean interrupted = false;
    private Thread counter;
    private int seconds = 60;
    private boolean isLoaded;

    public ConfirmFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_confirm, container, false);

        ((ImageView)view.findViewById(R.id.imgEntrance))
                .setImageBitmap(G.getBitmapFromResources(getResources(), R.drawable.img_green_board));

        loaderLogo = view.findViewById(R.id.loaderLogo);
        txtSendVerificationCode = view.findViewById(R.id.txtSendVerification);
        lnlSeconds = view.findViewById(R.id.lnlSeconds);
        txtSeconds = view.findViewById(R.id.txtSeconds);
        pinView = view.findViewById(R.id.pinView);

        pinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinView.setText("");
            }
        });
        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 4){
                    G.hideKeyboardFrom(getActivity(), view);
                    confirm();
                }
            }
        });

        btnSendVerification = view.findViewById(R.id.btnSendVerification);
        btnSendVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.hideKeyboardFrom(getActivity(), view);
                if (G.isNetworkAvailable(getActivity())){
                        sendVerificationCode();
                        btnSendVerification.setEnabled(false);
                }else
                    G.toastLong(
                            G.getStringFromResource(R.string.network_error, getActivity()),
                            getActivity()
                    );

            }
        });

        return view;
    }

    public void setPinText(String text){
        pinView.setText(text);
    }

    private void sendVerificationCode() {
        loaderLogo.setVisibility(View.VISIBLE);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_NATIONAL_CODE, selectedNationalCode);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (doesFragmentExists) {
                    if (!isLoaded) {
                        loaderLogo.setVisibility(View.GONE);
                        G.toastLong(
                                G.getStringFromResource(R.string.network_error, getActivity()),
                                getActivity()
                        );
                        btnSendVerification.setEnabled(true);
                    }
                }
            }
        }, Constant.TIME_OUT);

        new HttpCommand(HttpCommand.COMMAND_GET_VERIFICATION_CODE, contentValues)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        isLoaded = true;
                        loaderLogo.setVisibility(View.GONE);
                        G.i("result: " + result);
                        if (result.length() > 0) {
                            btnSendVerification.setBackground(
                                    getActivity().getResources().
                                            getDrawable(R.drawable.shape_button_disable)
                            );
                            txtSendVerificationCode.setTextColor(G.getColorFromResource(
                                    R.color.colorGrayLight, getActivity())
                            );
                            btnSendVerification.setEnabled(false);
                            lnlSeconds.setVisibility(View.VISIBLE);
                            startTimer();
                        }else
                            btnSendVerification.setEnabled(true);
                    }
                }).execute();
    }


    private void startTimer() {
        counter = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!interrupted){
                        G.i(""+ seconds);
                        try {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    txtSeconds.setText(seconds+"");
                                }
                            });
                            Thread.sleep(1000);
                            seconds--;
                            if (seconds == 0)
                                break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }

                if (!interrupted){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                                btnSendVerification.setBackground(
                                        getActivity().getResources().
                                                getDrawable(R.drawable.shape_button)
                                );
                                txtSendVerificationCode.setTextColor(G.getColorFromResource(
                                        R.color.colorPrimaryText, getActivity())
                                );
                                btnSendVerification.setEnabled(true);
                                lnlSeconds.setVisibility(View.GONE);

                                seconds = 60;
                        }
                    });
                }
            }
        });
        counter.start();
    }


    private void confirm(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_VERIFICATION_CODE,Integer.parseInt(pinView.getText().toString()));
        contentValues.put(Keys.KEY_NATIONAL_CODE, selectedNationalCode);
        new HttpCommand(HttpCommand.COMMAND_CONFIRM,contentValues)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        if (doesFragmentExists) {
                            processConfirmResult(result);
                        }
                    }
                }).execute();
    }


    private void processConfirmResult(String result) {
        int resultCode = JSONParser.getResultCodeFromJson(result);
        if (resultCode == Keys.RESULT_SUCCESS){
            //launching main activity
            ((RegistrationActivity)getActivity()).loginToMain(result);
        }else if (resultCode == Keys.RESULT_INVALID_VERIFICATION_CODE){
            G.toastLong(G.getStringFromResource(
                    R.string.invalid_verification_code,
                    getActivity()
            ),getActivity());
        }
    }


    public void setSelectedNationalCode(String selectedNationalCode) {
        this.selectedNationalCode = selectedNationalCode;
    }


    @Override
    public void onStop() {
        super.onStop();
        G.i("stop");
        if (counter != null){
            seconds = 60;
            interrupted = true;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        doesFragmentExists = false;
    }
}
