package com.mahta.rastin.broadcastapplication.activity.post_display;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.model.FavoriteProgram;
import com.mahta.rastin.broadcastapplication.model.Program;


public class ProgramContentFragment extends Fragment {
    private Program currentProgram;
    private LikeButton btnFavorite;
    private LinearLayout lnlLoading;
    private boolean isLiked;

    public ProgramContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        currentProgram = ((ProgramDisplayActivity)getActivity()).getCurrentProgram();
        setupToolbar(view);

        lnlLoading = view.findViewById(R.id.lnlLoading);
        WebView wbvContent = view.findViewById(R.id.wbvContent);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";

        String html = "<div dir='rtl'>" +
                    ((ProgramDisplayActivity) getActivity()).getContent() +
                    "</div>";

        wbvContent.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                lnlLoading.setVisibility(View.GONE);
            }
        });

        wbvContent.getSettings().setLoadWithOverviewMode(true);
        wbvContent.getSettings().setBuiltInZoomControls(true);
        wbvContent.getSettings().setDisplayZoomControls(false);
        wbvContent.loadDataWithBaseURL("", html, mimeType, encoding, "");

        return view;
    }

    void setupToolbar(View view){
        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        ((TextViewPlus)view.findViewById(R.id.txtTitle)).setText(currentProgram.getTitle());

        toolbar.findViewById(R.id.imgShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "لینک برنامه کلاسی مدرسه");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, G.PROGRAM_URL + currentProgram.getId());
                startActivity(Intent.createChooser(sharingIntent, G.getStringFromResource(R.string.share_to, getActivity())));
            }
        });

        btnFavorite =  toolbar.findViewById(R.id.btnFavorite);
        if (RealmController.getInstance().hasFavoriteProgram(currentProgram.getId()))
            btnFavorite.setLiked(true);

            btnFavorite.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                G.i("adding " +  (currentProgram.getId()));
                isLiked = true;
                FavoriteProgram favoriteProgram = new FavoriteProgram(currentProgram);
                RealmController.getInstance().addFavoriteProgram(favoriteProgram);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                isLiked = false;
                RealmController.getInstance().removeFavoriteProgram(
                        (currentProgram.getId())
                );
            }
        });
    }

}
