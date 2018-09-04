package com.mahta.rastin.broadcastapplication.activity.main;


import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.post_display.PostDisplayActivity;
import com.mahta.rastin.broadcastapplication.adapter.PostAdapter;
import com.mahta.rastin.broadcastapplication.adapter.StaffAdapter;
import com.mahta.rastin.broadcastapplication.custom.ButtonPlus;
import com.mahta.rastin.broadcastapplication.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.EndlessRecyclerViewScrollListener;
import com.mahta.rastin.broadcastapplication.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.Staff;
import com.mahta.rastin.broadcastapplication.model.StaffUpdated;
import org.json.JSONArray;
import java.util.LinkedList;
import java.util.List;

public class StaffListFragment extends Fragment{

    private StaffAdapter adapter;
    private RecyclerView rcvStaff;

    private TextView txtNoStaff;
    private LinearLayout lnlLoading;
    private LinearLayout lnlNoNetwork;
    private ButtonPlus btnTryAgain;
    private boolean doesFragmentExists = true;
    private boolean isLoading;
    private boolean isLoaded;


    public StaffListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_staff_list, container, false);
        setupToolbar(view);

        rcvStaff = view.findViewById(R.id.rcvStaff);
        int numberOfColumns = 2;
        rcvStaff.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        adapter = new StaffAdapter(getActivity(), G.realmController.getAllStaff());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                showDialog(position);
            }
        });


        btnTryAgain = view.findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showData();
            }
        });

        txtNoStaff = view.findViewById(R.id.txtNoStaff);
        lnlLoading = view.findViewById(R.id.lnlLoading);
        lnlNoNetwork = view.findViewById(R.id.lnlNoNetwork);

        showData();

        return view;
    }

    private void showData() {
        isLoading = true;
        changeLoadingResource(1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (doesFragmentExists) {
                    if (!isLoaded) {
                        changeLoadingResource(0);
                        isLoading = false;
                    }
                }
            }
        }, Constant.TIME_OUT);

        if (!G.realmController.hasStaff())
            loadStaffs(new LinkedList<Integer>());
        else
            checkUpdated();
    }

    void setupToolbar(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.imgToggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).openDrawerLayout();
            }
        });
        ((TextViewPlus)view.findViewById(R.id.txtTitle)).setText(G.getStringFromResource(R.string.members, getActivity()));

    }

    private void showDialog(int position) {
        final Staff staff = G.realmController.getAllStaff().get(position);

        final Dialog settingsDialog = new Dialog(getActivity());
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View v = getActivity().getLayoutInflater().inflate(R.layout.layout_description_dialog
                , null);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        String html = "<div dir='rtl'>" + staff.getDescription() + "</div>";

        WebView wbvContent = v.findViewById(R.id.wbvContent);
        wbvContent.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                v.findViewById(R.id.lnlLoading).setVisibility(View.GONE);
            }
        });

        wbvContent.getSettings().setLoadWithOverviewMode(true);
        wbvContent.getSettings().setBuiltInZoomControls(true);
        wbvContent.getSettings().setDisplayZoomControls(false);
        wbvContent.loadDataWithBaseURL("", html, mimeType, encoding, "");

        String name = staff.getFirst_name() + " " + staff.getLast_name();
        ((TextView)v.findViewById(R.id.txtName)).setText(name);
        ((TextView)v.findViewById(R.id.txtProfession)).setText(staff.getProfession());

        v.findViewById(R.id.imgSendEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!staff.getEmail().equals("null")){
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto",staff.getEmail(), null));
                    getActivity().startActivity(Intent.createChooser(emailIntent, "ارسال ایمیل"));
                }else
                    G.toastShort("ایمیلی ثبت نشده", getActivity());
            }
        });

        v.findViewById(R.id.imgClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog.dismiss();
            }
        });

        settingsDialog.setContentView(v);
        settingsDialog.show();
    }

    private void checkUpdated(){
        final List<Integer> ilist = new LinkedList<>();
        final List<Staff> sList = G.realmController.getAllStaff();

        new HttpCommand(HttpCommand.COMMAND_GET_STAFF_UPDATED,null)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        int result_code = JSONParser.getResultCodeFromJson(result);
                        if (result_code == Keys.RESULT_SUCCESS) {
                            List<StaffUpdated> list = JSONParser.parseStaffUpdated(result);
                            if (list!=null){
                                for (StaffUpdated s: list){
                                    // we have staff in web but not in local (new staff)
                                    if (!G.realmController.hasStaff(s.getId()))
                                        ilist.add(s.getId());
                                    else {
                                        Staff staff = G.realmController.getStaff(s.getId());
                                        // we have staff in web and in local but it is updated (updated staff)
                                        if (s.getUpdated_at().after(staff.getUpdated_at())){
                                            ilist.add(s.getId());
                                            G.realmController.removeStaff(staff.getId());
                                        }
                                    }
                                }

                                boolean remove;
                                for (Staff staff: sList) {
                                    remove = true;
                                    for (StaffUpdated s : list) {
                                        if (staff.getId() == s.getId()) {
                                            remove = false;
                                            break;
                                        }
                                    }

                                    if (remove) {
                                        G.realmController.removeStaff(staff.getId());
                                    }

                                }


                                if (ilist.size() != 0)
                                    loadStaffs(ilist);
                                else {
                                    setAdapter();
                                    G.i("Nothing changed in staff");
                                }

                            }else {
                                G.realmController.clearAllStaffs();
                                setAdapter();
                                G.i("empty staff u");
                            }

                        }
                    }
                }).execute();
    }

    private void loadStaffs(List<Integer> required_id_list){
        JSONArray arr = new JSONArray();
        for (Integer id: required_id_list)
            arr.put(id);

        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_REQUIRED_ID_LIST, arr.toString());

        new HttpCommand(HttpCommand.COMMAND_GET_STAFF,contentValues)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        int result_code = JSONParser.getResultCodeFromJson(result);
                        if (result_code == Keys.RESULT_SUCCESS) {
                            List<Staff> list = JSONParser.parseStaff(result);
                            if (list!=null){
                                for (Staff s: list){
                                    G.realmController.addStaff(s);
                                }

                            }else {
                                G.i("empty staff");
                            }

                            setAdapter();

                        }
                    }
                }).execute();
    }

    private void setAdapter(){
        isLoading = false;
        isLoaded = true;

        rcvStaff.setAdapter(adapter);
        changeLoadingResource(2);

        if (adapter.getItemCount() <= 0)
            txtNoStaff.setVisibility(View.VISIBLE);
        else
            txtNoStaff.setVisibility(View.GONE);
    }

    private void log(){
        List<Staff> list = G.realmController.getAllStaff();
        for (Staff staff: list)
            G.i(staff.getId() + ": " + staff.getFirst_name() + " " + staff.getLast_name());
    }

    private void changeLoadingResource(int state) {
        switch (state) {
            case 0:
                txtNoStaff.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.VISIBLE);
                lnlLoading.setVisibility(View.GONE);
                break;
            case 1:
                txtNoStaff.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.VISIBLE);
                break;
            default:
                txtNoStaff.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doesFragmentExists = false;
    }

}
