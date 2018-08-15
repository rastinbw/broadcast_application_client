package com.mahta.rastin.broadcastapplication.activity.startup;


import android.app.Fragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.Student;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ConfirmFragment extends Fragment {


    private EditText edtPhoneNumber;
    private RippleView btnSendPassword;
    private TextView txtSendPassword;
    private TextView txtSeconds;
    private LinearLayout lnlSeconds;

    private Handler handler = new Handler();
    private Student student;
    private boolean shouldStart = false;
    private boolean doesFragmentExists = true;
    private boolean interrupted = false;
    private Thread counter;
    private int seconds = 60;


    public ConfirmFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm, container, false);

        StartupActivity activity = (StartupActivity) getActivity();
        student = activity.getCurrentStudent();
        txtSendPassword = view.findViewById(R.id.txtSendPassword);
        lnlSeconds = view.findViewById(R.id.lnlSeconds);
        txtSeconds = view.findViewById(R.id.txtSeconds);

        ((ImageView)view.findViewById(R.id.imgEntrance))
                .setImageBitmap(G.getBitmapFromResources(getResources(), R.drawable.img_green_board));

        edtPhoneNumber = view.findViewById(R.id.edtPhoneNumber);
        ((TextView)view.findViewById(R.id.txtMessage)).setText(
                Html.fromHtml("<span style=\"color: #388E3C\">"
                        + student.getFirst_name() + " " +
                        student.getLast_name() +
                        "</span>، هویت شما احراز شد. لطفا برای دریافت رمز عبور شماره تماس خود را وارد کنید.")
        );

        btnSendPassword = view.findViewById(R.id.btnSendPassword);
        btnSendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (G.isNetworkAvailable(getActivity())){
                    if (validate()){
                        confirm();
                        btnSendPassword.setBackground(
                                getActivity().getResources().
                                        getDrawable(R.drawable.shape_button_disable)
                        );
                        txtSendPassword.setTextColor(G.getColorFromResource(
                                R.color.colorGrayLight, getActivity())
                        );
                        btnSendPassword.setEnabled(false);
                        lnlSeconds.setVisibility(View.VISIBLE);
                    }
                }else
                    G.toastLong(
                            G.getStringFromResource(R.string.network_error, getActivity()),
                            getActivity()
                    );

            }
        });

        return view;
    }

    private boolean validate() {

        boolean valid = true;
        String phoneNumber = edtPhoneNumber.getText().toString();
        Pattern p = Pattern.compile("^(09{1})+([1-3]{1})+(\\d{8})$");
        Matcher m = p.matcher(phoneNumber);

        if (phoneNumber.isEmpty() || !m.find())
        {
            edtPhoneNumber.setError(G.getStringFromResource(R.string.validation_error_phone_number, getActivity()));
            valid = false;
        }else {
            edtPhoneNumber.setError(null);
        }

        return valid;
    }

    private void confirm(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_PHONE_NUMBER,edtPhoneNumber.getText().toString());
        contentValues.put(Keys.KEY_NATIONAL_CODE,student.getNational_code());
        new HttpCommand(HttpCommand.COMMAND_CONFIRM,contentValues)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        if (doesFragmentExists) {
                            processResult(result);
                        }
                    }
                }).execute();

        counter = new Thread(new Runnable() {
            @Override
            public void run() {
                    while (!interrupted){
                        if (shouldStart){
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
                    }

                    if (!interrupted){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                btnSendPassword.setBackground(
                                        getActivity().getResources().
                                                getDrawable(R.drawable.shape_button)
                                );
                                txtSendPassword.setTextColor(G.getColorFromResource(
                                        R.color.colorPrimaryText, getActivity())
                                );
                                btnSendPassword.setEnabled(true);
                                lnlSeconds.setVisibility(View.GONE);

                                seconds = 60;
                                shouldStart = false;
                            }
                        });
                    }
            }
        });
        counter.start();
    }

    private void processResult(String result) {
        int resultCode = JSONParser.getResultCodeFromJson(result);
        G.i(resultCode+"");
        if (resultCode == Keys.RESULT_SUCCESS){
            shouldStart = true;
            getPassword();
        }else {
            G.toastLong(G.getStringFromResource(
                    R.string.invalid_national_code,
                    getActivity()
            ),getActivity());
            if (counter != null)
                interrupted = true;
        }
    }

    private void getPassword() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_PHONE_NUMBER,edtPhoneNumber.getText().toString());
        contentValues.put(Keys.KEY_NATIONAL_CODE,student.getNational_code());
        new HttpCommand(HttpCommand.COMMAND_GET_PASSWORD,contentValues)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        G.i(result);
                    }
                }).execute();
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
