package com.mahta.rastin.broadcastapplication.activity.other;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.Group;
import com.mahta.rastin.broadcastapplication.model.Student;
import com.wang.avi.AVLoadingIndicatorView;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;

public class UpdateInfoActivity extends AppCompatActivity {
    private EditTextPlus edtNationalCode;
    private EditTextPlus edtFirstName;
    private EditTextPlus edtLastName;
    private NiceSpinner spnGroups;

    private TextView txtNationalCodeError;
    private TextView txtFirstNameError;
    private TextView txtLastNameError;
    private TextView txtGroupsError;
    private TextView txtConfirm;

    private AVLoadingIndicatorView loaderLogo;
    private RelativeLayout btnConfirm;

    private Student currentStudent;
    private Integer selectedGroupId = null;
    private Integer firstGroupId = null;
    private int messageSendResult;
    private boolean isLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);
        setupToolbar();

        edtNationalCode = findViewById(R.id.edtNationalCode);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLatName);
        spnGroups =  findViewById(R.id.spnGroups);

        txtFirstNameError = findViewById(R.id.txtMessageFirstNameError);
        txtLastNameError = findViewById(R.id.txtMessageLastNameError);
        txtNationalCodeError= findViewById(R.id.txtNationalCodeError);
        txtGroupsError = findViewById(R.id.txtGroupsError);
        txtConfirm = findViewById(R.id.txtConfirm);

        btnConfirm = findViewById(R.id.btnConfirm);
        loaderLogo = findViewById(R.id.loaderLogo);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });

        currentStudent = G.realmController.getStudent();
        edtFirstName.setText(currentStudent.getFirst_name());
        edtLastName.setText(currentStudent.getLast_name());
        edtNationalCode.setText(currentStudent.getNational_code());
        prepareGroupSpinner();

        if (currentStudent.isStudent()) {
            edtNationalCode.setEnabled(false);
            edtNationalCode.setTextColor(G.getColorFromResource(
                    R.color.colorSecondaryText,
                    getApplicationContext())
            );
        }
    }

    private void confirm() {
        G.hideKeyboard(UpdateInfoActivity.this);
        if (G.isNetworkAvailable(UpdateInfoActivity.this)){
            if (validate()){
                changeInfo();
            }
        }else
            G.toastLong(G.getStringFromResource(R.string.no_internet,
                    UpdateInfoActivity.this),
                    UpdateInfoActivity.this);
    }

    private void prepareGroupSpinner() {
        final List<Group> data = G.realmController.getGroupList();
        if (data != null && data.size() > 0){
            List<String> titles = new LinkedList<>();
            for (Group group: data){
                titles.add(group.getTitle());
            }
            spnGroups.attachDataSource(titles);
            spnGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedGroupId = data.get(position).getId();
                    G.i("selected");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedGroupId = null;
                    G.i("Nothing selected");
                }
            });

            selectedGroupId = currentStudent.getGroup_id();
            firstGroupId = currentStudent.getGroup_id();
            spnGroups.setSelectedIndex(getGroupIndex(data, selectedGroupId));
        }
    }

    private boolean validate() {
        boolean valid = true;

        String nationalCode = edtNationalCode.getText().toString().trim();
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();

        if (nationalCode.isEmpty() || nationalCode.length() != 10) {
            txtNationalCodeError.setVisibility(View.VISIBLE);
            findViewById(R.id.lnlNationalCode).setBackgroundColor(G.getColorFromResource
                    (R.color.colorRedLighter, UpdateInfoActivity.this)
            );
            valid = false;
        } else {
            txtNationalCodeError.setVisibility(View.GONE);
            findViewById(R.id.lnlNationalCode).setBackgroundColor(Color.WHITE);
        }


        if (firstName.isEmpty() || firstName.length() > 20) {
            txtFirstNameError.setVisibility(View.VISIBLE);
            findViewById(R.id.lnlFirstName).setBackgroundColor(G.getColorFromResource
                    (R.color.colorRedLighter, UpdateInfoActivity.this)
            );
            valid = false;
        } else {
            txtFirstNameError.setVisibility(View.GONE);
            findViewById(R.id.lnlFirstName).setBackgroundColor(Color.WHITE);
        }


        if (lastName.isEmpty() || lastName.length() > 30) {
            txtLastNameError.setVisibility(View.VISIBLE);
            findViewById(R.id.lnlLastName).setBackgroundColor(G.getColorFromResource
                    (R.color.colorRedLighter, UpdateInfoActivity.this)
            );
            valid = false;
        } else {
            txtLastNameError.setVisibility(View.GONE);
            findViewById(R.id.lnlLastName).setBackgroundColor(Color.WHITE);
        }


        if (selectedGroupId == null) {
            txtGroupsError.setVisibility(View.VISIBLE);
            findViewById(R.id.lnlGroup).setBackgroundColor(G.getColorFromResource
                    (R.color.colorRedLighter, UpdateInfoActivity.this)
            );
            valid = false;
        } else {
            txtGroupsError.setVisibility(View.GONE);
            findViewById(R.id.lnlGroup).setBackgroundColor(Color.WHITE);
        }

        return valid;

    }

    private void changeInfo() {
        ContentValues params = new ContentValues();
        params.put(Keys.KEY_GROUP_ID,selectedGroupId);
        params.put(Keys.KEY_FIRST_NAME,edtFirstName.getText().toString().trim());
        params.put(Keys.KEY_LAST_NAME,edtLastName.getText().toString().trim());
        params.put(Keys.KEY_NATIONAL_CODE,edtNationalCode.getText().toString().trim());
        params.put(Keys.KEY_TOKEN, G.realmController.getUserToken().getToken());
        changeState(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLoaded) {
                    G.toastLong(
                            G.getStringFromResource(R.string.network_error, UpdateInfoActivity.this),
                            UpdateInfoActivity.this
                    );
                    changeState(1);
                }
            }
        }, Constant.TIME_OUT);

        new HttpCommand(HttpCommand.COMMAND_UPDATE_INFO,params)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        messageSendResult = JSONParser.getResultCodeFromJson(result);
                        changeState(2);

                        switch (messageSendResult) {
                            case Keys.RESULT_SUCCESS:
                                if (!selectedGroupId.equals(firstGroupId)){
                                    G.i("Clearing messages");
                                    G.realmController.clearAllMessages();
                                }

                                Student student = new Student();
                                student.setFirst_name(edtFirstName.getText().toString().trim());
                                student.setLast_name(edtLastName.getText().toString().trim());
                                student.setNational_code(edtNationalCode.getText().toString().trim());
                                student.setGroup_id(selectedGroupId);
                                G.realmController.addStudent(student);

                                G.toastShort(G.getStringFromResource(R.string.update_info_successfully,
                                        getApplicationContext()), getApplicationContext());
                                setResult(Keys.UPDATE_OK);
                                finish();
                                break;
                            case Keys.RESULT_REPETITIVE_NATIONAL_CODE:
                                G.toastShort(G.getStringFromResource(R.string.register_repeated_national_code_error,
                                        getApplicationContext()), getApplicationContext());
                                break;
                            default:
                                G.toastShort(G.getStringFromResource(R.string.update_info_not_successfully,
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

        ((TextView)toolbar.findViewById(R.id.txtTitle)).setText(R.string.update_info);
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

    public int getGroupIndex(final List<Group> list, final int id){
        for (Group group: list){
            if (group.getId() == id){
                return list.indexOf(group);
            }
        }
        return 0;
    }
}
