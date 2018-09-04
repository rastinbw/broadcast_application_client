package com.mahta.rastin.broadcastapplication.activity.main;


import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.adapter.MessageAdapter;
import com.mahta.rastin.broadcastapplication.custom.ButtonPlus;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.Message;
import com.mahta.rastin.broadcastapplication.model.ReadPost;

import java.util.List;

public class MessageListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    public SwipeRefreshLayout swipeRefreshLayout;
    private MessageAdapter adapter;
    private TextView txtNoMessages;
    private boolean isLoaded;
    private RelativeLayout rtlLoader;
    private LinearLayout lnlLoading;
    private LinearLayout lnlNoNetwork;
    private ButtonPlus btnTryAgain;
    private boolean doesFragmentExists = true;
    private boolean isLoading;

    public MessageListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        final RecyclerView rcvPosts = view.findViewById(R.id.rcvMessage);
        setupToolbar(view);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent
        );
        swipeRefreshLayout.setOnRefreshListener(this);


        btnTryAgain = view.findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.i("pressed");
                loadMessages();
            }
        });

        txtNoMessages = view.findViewById(R.id.txtNoMessage);
        lnlLoading = view.findViewById(R.id.lnlLoading);
        rtlLoader = view.findViewById(R.id.rtlLoader);
        lnlNoNetwork = view.findViewById(R.id.lnlNoNetwork);

        adapter = new MessageAdapter(getActivity(), G.realmController.getAllMessages());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                if (G.isNetworkAvailable(getActivity())){
                    Message message = G.realmController.getAllMessages().get(position);

                    // MAKE IT AS READ POST
                    if (!G.realmController.hasReadPost(message.getId(), Constant.CATEGORY_ID_MESSAGE)) {
                        G.realmController.addReadPost(new ReadPost(message.getId(),
                                Constant.CATEGORY_ID_MESSAGE)
                        );
                        G.i("adding");
                    }

                    loadDialog(message);

                }else {
                    G.toastLong(G.getStringFromResource(R.string.no_internet, getActivity()), getActivity());
                }

            }
        });
        rcvPosts.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvPosts.setLayoutManager(linearLayoutManager);


        return view;

    }

    private void loadDialog(Message message) {
        final Dialog settingsDialog = new Dialog(getActivity());
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View v = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.layout_message_dialog,null);

        ((TextView)v.findViewById(R.id.txtTitle)).setText(message.getTitle());
        WebView webView = v.findViewById(R.id.wbvContent);

        webView.setVerticalScrollBarEnabled(true);

        String content = "<html>\n" +
                " <head></head>\n" +
                " <body style=\"text-align:justify;color:#757575;\">\n" +
                message.getContent() +
                " </body>\n" +
                "</html>";
        webView.loadData(content,
                "text/html; charset=utf-8", "utf-8");
        settingsDialog.setContentView(v);
        settingsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                adapter.notifyDataSetChanged();
            }
        });
        settingsDialog.show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMessages();
    }

    private void loadMessages() {
        changeLoadingResource(1);

        isLoading = true;
        isLoaded = false;

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

        ContentValues contentValues = new ContentValues();
        contentValues.put(Keys.KEY_TOKEN, G.realmController.getUserToken().getToken());

        new HttpCommand(HttpCommand.COMMAND_GET_MESSAGES,contentValues)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        int result_code = JSONParser.getResultCodeFromJson(result);
                        if (result_code == Keys.RESULT_SUCCESS) {
                            txtNoMessages.setVisibility(View.GONE);
                            isLoading = false;
                            isLoaded = true;
                            changeLoadingResource(2);

                            List<Message> list = JSONParser.parseMessages(result);
                            if (list!=null){
                                for (Message message: list){
                                    G.realmController.addMessage(message);
                                    adapter.notifyDataSetChanged();
                                }

                            }else {
                                txtNoMessages.setVisibility(View.VISIBLE);
                                G.i("empty message");
                            }

                        }
                    }
                }).execute();
    }

    void setupToolbar(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.imgToggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).openDrawerLayout();
            }
        });

        ((TextView)toolbar.findViewById(R.id.txtTitle)).setText(
                G.getStringFromResource(R.string.messages, getActivity())
        );
    }

    private void changeLoadingResource(int state) {
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }

        switch (state) {
            case 0:
                rtlLoader.setVisibility(View.VISIBLE);
                lnlNoNetwork.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.VISIBLE);
                lnlLoading.setVisibility(View.GONE);
                break;
            case 1:
                rtlLoader.setVisibility(View.VISIBLE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.VISIBLE);
                break;
            default:
                rtlLoader.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (!isLoading){

            loadMessages();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doesFragmentExists = false;
    }
}
