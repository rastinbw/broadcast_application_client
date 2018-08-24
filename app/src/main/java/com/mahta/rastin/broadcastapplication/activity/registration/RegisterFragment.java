package com.mahta.rastin.broadcastapplication.activity.registration;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.andexert.library.RippleView;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.other.ChangePasswordActivity;
import com.mahta.rastin.broadcastapplication.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplication.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.Group;
import com.wang.avi.AVLoadingIndicatorView;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {
    private EditTextPlus edtNationalCode;
    private EditTextPlus edtPhoneNumber;
    private EditTextPlus edtFirstName;
    private EditTextPlus edtLastName;
    private EditTextPlus edtPassword;
    private EditTextPlus edtPasswordRepeat;
    private NiceSpinner spnGroups;

    private Integer selectedGroupId = null;

    private AVLoadingIndicatorView loaderLogo;
    private RippleView btnContinue;

    private boolean doesFragmentExists = true;
    private boolean isLoaded;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        setupView(view);

        return view;
    }

    private void setupView(final View view) {
        edtNationalCode = view.findViewById(R.id.edtNationalCode);
        edtFirstName = view.findViewById(R.id.edtFirstName);
        edtLastName = view.findViewById(R.id.edtLatName);
        edtPhoneNumber = view.findViewById(R.id.edtPhoneNumber);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtPasswordRepeat = view.findViewById(R.id.edtPasswordRepeat);
        spnGroups =  view.findViewById(R.id.spnGroups);
        loaderLogo = view.findViewById(R.id.loaderLogo);

        prepareGroupSpinner();
        setupToolbar(view);

        btnContinue = view.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continu(view);
            }
        });

        edtFirstName.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_key_24dp), null, null, null);
    }

    private void continu(View view) {
        G.hideKeyboardFrom(getActivity(), view);
        if (G.isNetworkAvailable(getActivity())){
            if (validate())
                register();
        }else
            G.toastLong(G.getStringFromResource(R.string.no_internet,
                    getActivity()),
                    getActivity());
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

            spnGroups.setSelectedIndex(0);
            selectedGroupId = data.get(0).getId();
        }
    }

    private void setupToolbar(final View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        toolbar.findViewById(R.id.imgBack).setVisibility(View.VISIBLE);

        toolbar.findViewById(R.id.imgCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continu(view);
            }
        });

        ((TextViewPlus)toolbar.findViewById(R.id.txtTitle))
                .setText(G.getStringFromResource(R.string.register, getActivity()));
    }


    private boolean validate() {
        boolean valid = true;

        String nationalCode = edtNationalCode.getText().toString().trim();
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String passwordRepeat = edtPasswordRepeat.getText().toString().trim();

        if (nationalCode.isEmpty() || nationalCode.length() != 10) {
            edtNationalCode.setError(G.getStringFromResource(R.string.validation_error_national_code, getActivity()));
            valid = false;
        } else {
            edtNationalCode.setError(null);
        }

        if (firstName.isEmpty() || firstName.length() > 20) {
            edtFirstName.setError(G.getStringFromResource(R.string.validation_error_first_name, getActivity()));
            valid = false;
        } else {
            edtFirstName.setError(null);
        }

        if (lastName.isEmpty() || lastName.length() > 30) {
            edtLastName.setError(G.getStringFromResource(R.string.validation_error_last_name, getActivity()));
            valid = false;
        } else {
            edtLastName.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 12) {
            edtPassword.setError(G.getStringFromResource(R.string.validation_error_password_register, getActivity()));
            valid = false;
        } else {
            edtPassword.setError(null);
        }

        if (passwordRepeat.isEmpty() || !passwordRepeat.equals(password)) {
            edtPasswordRepeat.setError(G.getStringFromResource(R.string.validation_error_password_repeat, getActivity()));
            valid = false;
        } else {
            edtPasswordRepeat.setError(null);
        }

        if (selectedGroupId == null) {
            spnGroups.setError(G.getStringFromResource(R.string.validation_error_group, getActivity()));
            valid = false;
        } else {
            spnGroups.setError(null);
        }

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

    private void register(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_GROUP_ID,selectedGroupId);
        contentValues.put(Keys.KEY_FIRST_NAME,edtFirstName.getText().toString().trim());
        contentValues.put(Keys.KEY_LAST_NAME,edtLastName.getText().toString().trim());
        contentValues.put(Keys.KEY_NATIONAL_CODE,edtNationalCode.getText().toString().trim());
        contentValues.put(Keys.KEY_PHONE_NUMBER,edtPhoneNumber.getText().toString().trim());
        contentValues.put(Keys.KEY_PASSWORD,edtPassword.getText().toString().trim());
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

        new HttpCommand(HttpCommand.COMMAND_REGISTER,contentValues)
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
                G.i("register_ok");
                ConfirmFragment fragment = new ConfirmFragment();
                fragment.setSelectedNationalCode(edtNationalCode.getText().toString().trim());
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.frmRegistrationContainer, fragment, "CONFIRM")
                        .commit();
                break;
            case Keys.RESULT_REPETITIVE_NATIONAL_CODE:
                G.toastLong(G.getStringFromResource(
                        R.string.register_repeated_national_code_error,
                        getActivity()
                ), getActivity());
                break;
            case Keys.RESULT_REPETITIVE_PHONE_NUMBER:
                G.toastLong(G.getStringFromResource(
                        R.string.register_repeated_phone_number_error,
                        getActivity()
                ), getActivity());
                break;
        }
    }

    private void changeState(int state){
        if (state == 0){
            isLoaded = false;
            loaderLogo.setVisibility(View.VISIBLE);
            btnContinue.setEnabled(false);
            btnContinue.setBackground(
                    getActivity().getResources().
                            getDrawable(R.drawable.shape_button_disable)
            );

        }else {
            isLoaded = !(state == 1);
            loaderLogo.setVisibility(View.GONE);
            btnContinue.setEnabled(true);
            btnContinue.setBackground(
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
