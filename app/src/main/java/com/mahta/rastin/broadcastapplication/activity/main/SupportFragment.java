package com.mahta.rastin.broadcastapplication.activity.main;


import android.app.Fragment;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupportFragment extends Fragment {

    private EditText edtMessageTitle;
    private EditText edtMessageEmail;
    private EditText edtMessageText;
    private TextViewPlus txtSend;
    private AVLoadingIndicatorView loaderLogo;
    private RelativeLayout btnSend;
    private boolean isLoaded;
    private boolean doesFragmentExists = true;
    private int messageSendResult;

    public SupportFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View parent = inflater.inflate(R.layout.fragment_support, container, false);
        setupToolbar(parent);

        edtMessageTitle = parent.findViewById(R.id.edtMessageTitle);
        edtMessageText = parent.findViewById(R.id.edtMessageText);
        txtSend = parent.findViewById(R.id.txtSend);
        loaderLogo = parent.findViewById(R.id.loaderLogo);

        edtMessageEmail = parent.findViewById(R.id.edtMessageEmail);
        btnSend = parent.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (G.isNetworkAvailable(getActivity())){
                    if (validate(parent)){
                        sendMessage();
                    }
                }else
                    G.toastLong(G.getStringFromResource(R.string.no_internet, getActivity()), getActivity());

            }
        });

        return parent;
    }

    private boolean validate(View view){
        boolean validated = true;

        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(edtMessageEmail.getText().toString());
        if (edtMessageEmail.getText().toString().isEmpty() || !m.find())
        {
            validated = false;
            view.findViewById(R.id.txtMessageEmailError).setVisibility(View.VISIBLE);
            view.findViewById(R.id.lnlEmail)
                    .setBackgroundColor(G.getColorFromResource(R.color.colorRedLighter, getActivity()));
        }else{
            view.findViewById(R.id.txtMessageEmailError).setVisibility(View.GONE);
            view.findViewById(R.id.lnlEmail).setBackgroundColor(Color.WHITE);
        }
        //-------------------------------------------------------------------------------------------
        if (edtMessageTitle.getText().toString().isEmpty()
                || edtMessageTitle.getText().toString().length() < 4
                || edtMessageTitle.getText().toString().length() > 20 )
        {
            validated = false;
            view.findViewById(R.id.txtMessageTitleError).setVisibility(View.VISIBLE);
            view.findViewById(R.id.lnlTitle)
                    .setBackgroundColor(G.getColorFromResource(R.color.colorRedLighter, getActivity()));
        }else{
            view.findViewById(R.id.txtMessageTitleError).setVisibility(View.GONE);
            view.findViewById(R.id.lnlTitle).setBackgroundColor(Color.WHITE);
        }
        //-------------------------------------------------------------------------------------------
        if (edtMessageText.getText().toString().isEmpty()
                || edtMessageText.getText().toString().length() < 10
                || edtMessageText.getText().toString().length() > 300 )
        {
            validated = false;
            view.findViewById(R.id.txtMessageTextError).setVisibility(View.VISIBLE);
            view.findViewById(R.id.lnlText)
                    .setBackgroundColor(G.getColorFromResource(R.color.colorRedLighter, getActivity()));
        }else{
            view.findViewById(R.id.txtMessageTextError).setVisibility(View.GONE);
            view.findViewById(R.id.lnlText).setBackgroundColor(Color.WHITE);
        }

        return validated;
    }

    private void sendMessage() {
        ContentValues params = new ContentValues();
        params.put(Keys.KEY_TITLE,edtMessageTitle.getText().toString());
        params.put(Keys.KEY_MESSAGE,edtMessageText.getText().toString());
        params.put(Keys.KEY_EMAIL,edtMessageEmail.getText().toString());
        params.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());
        changeState(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (doesFragmentExists){
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

        new HttpCommand(HttpCommand.COMMAND_SEND_TICKET,params)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        if (doesFragmentExists) {
                            messageSendResult = JSONParser.getResultCodeFromJson(result);
                            changeState(2);
                            if (messageSendResult == Keys.RESULT_SUCCESS) {
                                G.toastShort(G.getStringFromResource(R.string.sent_message_successfully,
                                        getActivity()), getActivity());
                            } else
                                G.toastShort(G.getStringFromResource(R.string.sent_message_not_successfully,
                                        getActivity()), getActivity());
                        }
                    }
                }).execute();

    }

    private void setupToolbar(View parent){
        Toolbar toolbar = parent.findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.imgToggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).openDrawerLayout();
            }
        });

        ((TextView)toolbar.findViewById(R.id.txtTitle)).setText(R.string.support);
    }

    private void changeState(int state){
        if (state == 0){
            isLoaded = false;
            loaderLogo.setVisibility(View.VISIBLE);
            txtSend.setVisibility(View.GONE);
            btnSend.setEnabled(false);
        }else {
            isLoaded = !(state == 1);
            loaderLogo.setVisibility(View.GONE);
            txtSend.setVisibility(View.VISIBLE);
            btnSend.setEnabled(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doesFragmentExists = false;
    }
}
