package com.mahta.rastin.broadcastapplication.activity.main;


import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mahta.rastin.broadcastapplication.activity.other.UpdateInfoActivity;
import com.mahta.rastin.broadcastapplication.activity.other.ChangePasswordActivity;
import com.mahta.rastin.broadcastapplication.activity.other.WorkbookActivity;
import com.mahta.rastin.broadcastapplication.activity.startup.SplashActivity;
import com.mahta.rastin.broadcastapplication.adapter.WorkbookTitleAdapter;
import com.mahta.rastin.broadcastapplication.custom.ButtonPlus;
import com.mahta.rastin.broadcastapplication.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.registration.RegistrationActivity;
import com.mahta.rastin.broadcastapplication.dialog.CheckDialog;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.OnFragmentActionListener;
import com.mahta.rastin.broadcastapplication.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.Student;
import com.mahta.rastin.broadcastapplication.model.Workbook;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private boolean isLoaded;
    private boolean doesFragmentExists = true;
    private boolean isParent;
    private LinearLayout lnlNoNetwork;
    private LinearLayout lnlLoading;
    private RelativeLayout rtlLoader;
    private OnFragmentActionListener onUpdateInfo;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupToolbar(view);
        CardView crvSignOut = view.findViewById(R.id.crvSignOut);
        CardView crvChangePassword = view.findViewById(R.id.crvChangePassword);
        CardView crvUpdateInfo = view.findViewById(R.id.crvUpdateInfo);

        lnlNoNetwork = view.findViewById(R.id.lnlNoNetwork);
        lnlLoading = view.findViewById(R.id.lnlLoading);
        rtlLoader = view.findViewById(R.id.rtlLoader);
        ButtonPlus btnTryAgain = view.findViewById(R.id.btnTryAgain);

        checkToken();
        isParent = G.realmController.getUserToken().isParent();

        if (isParent){
            crvChangePassword.setVisibility(View.GONE);
            crvUpdateInfo.setVisibility(View.GONE);
        }

        final CheckDialog dialog = new CheckDialog(getActivity(),
                G.getStringFromResource(R.string.sign_out_ensuring_message,
                        getActivity()), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegistration(getActivity());
            }
        });

        crvSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        crvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        crvUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateInfo.onAction(view, null);
            }
        });

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInfo(view);
            }
        });

        loadInfo(view);

        return view;
    }

    private void checkToken(){
        if (G.isUserSignedIn()){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Keys.KEY_TOKEN,G.realmController.getUserToken().getToken());
            new HttpCommand(HttpCommand.COMMAND_CHECK_TOKEN,contentValues)
                    .setOnResultListener(new OnResultListener() {
                        @Override
                        public void onResult(String result) {
                            int resultCode = JSONParser.getResultCodeFromJson(result);
                            if (resultCode == Keys.RESULT_INVALID_TOKEN){
                                goToRegistration(getActivity());
                            }
                        }
                    }).execute();
        }
    }

    private void goToRegistration(Activity activity) {
        G.logout(getActivity());
        Intent intent = new Intent(activity, RegistrationActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void loadInfo(final View view){
        rtlLoader.setVisibility(View.VISIBLE);
        changeLoadingResource(1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_TOKEN,G.realmController.getUserToken().getToken());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (doesFragmentExists) {
                    if (!isLoaded) {
                        changeLoadingResource(0);
                    }
                }
            }
        }, Constant.TIME_OUT);

        new HttpCommand(HttpCommand.COMMAND_GET_INFO,contentValues)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        if (doesFragmentExists) {
                            int result_code = JSONParser.getResultCodeFromJson(result);
                            if (result_code == Keys.RESULT_SUCCESS) {
                                Student student = JSONParser.parseStudent(result);
                                G.realmController.addStudent(student);
                                fillInfo(student, view);
                            } else if (result_code == Keys.RESULT_INVALID_TOKEN) {
                                G.e("Invalid token");
                            }
                        }
                    }
                }).execute();



    }

    private void fillInfo(Student student, View view) {
        try {
            ((TextViewPlus) view.findViewById(R.id.txtName)).setText(
                    student.getFirst_name() + " " + student.getLast_name()
            );

            ((TextViewPlus) view.findViewById(R.id.txtNationalCode)).setText(
                    student.getNational_code()
            );

            ((TextViewPlus) view.findViewById(R.id.txtGroup)).setText(
                    G.realmController
                            .getGroup(student.getGroup_id())
                            .getTitle()
            );

            loadWorkbook(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeLoadingResource(int state) {
        if (state == 0){
            lnlNoNetwork.setVisibility(View.VISIBLE);
            lnlLoading.setVisibility(View.GONE);
        }else {
            lnlNoNetwork.setVisibility(View.GONE);
            lnlLoading.setVisibility(View.VISIBLE);
        }
    }

    private void loadWorkbook(final View view) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_TOKEN,G.realmController.getUserToken().getToken());

        new HttpCommand(HttpCommand.COMMAND_GET_WORKBOOK,contentValues)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        if (doesFragmentExists) {
                            int result_code = JSONParser.getResultCodeFromJson(result);
                            if (result_code == Keys.RESULT_SUCCESS) {
                                isLoaded = true;
                                List<Workbook> workbooks = JSONParser.parseWorkbooks(result);
                                if (workbooks != null) {
                                    fillWorkbookTitles(view, workbooks);
                                }else {
                                    view.findViewById(R.id.txtNoWorkbook).setVisibility(View.VISIBLE);
                                    rtlLoader.setVisibility(View.GONE);
                                }
                            } else if (result_code == Keys.RESULT_INVALID_TOKEN) {
                                G.e("Invalid token");
                            }
                        }
                    }
                }).execute();
    }

    private void fillWorkbookTitles(View view, final List<Workbook> workbooks) {
        List<String> titles = new ArrayList<>();

        for (Workbook workbook : workbooks) {
            titles.add(G.getStringFromResource(R.string.workbook, getActivity())
                    + " " +
                    workbook.getMonth() + " " + workbook.getYear());
        }

        RecyclerView recyclerView = view.findViewById(R.id.rcvWorkbook);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        WorkbookTitleAdapter adapter = new WorkbookTitleAdapter(getActivity(), titles);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(getActivity(), WorkbookActivity.class);
                intent.putExtra(Keys.KEY_EXTRA_FLAG, workbooks.get(position));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        rtlLoader.setVisibility(View.GONE);
    }

    void setupToolbar(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.imgToggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).openDrawerLayout();
            }
        });

        ((TextViewPlus)toolbar.findViewById(R.id.txtTitle)).setText(G.getStringFromResource(R.string.profile, getActivity()));
    }

    public void setOnUpdateInfo(OnFragmentActionListener onUpdateInfo) {
        this.onUpdateInfo = onUpdateInfo;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doesFragmentExists = false;
    }
}
