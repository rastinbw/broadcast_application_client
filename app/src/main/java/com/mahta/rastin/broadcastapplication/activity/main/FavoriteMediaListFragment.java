package com.mahta.rastin.broadcastapplication.activity.main;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.adapter.FavoriteMediaAdapter;
import com.mahta.rastin.broadcastapplication.dialog.PlayerDialog;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplication.model.FavoriteMedia;
import com.mahta.rastin.broadcastapplication.model.Media;

public class FavoriteMediaListFragment extends Fragment {
    private FavoriteMediaAdapter favoriteAdapter;
    public TextView txtNoMedia;
    private RecyclerView rcvPosts;
    public SearchView searchView;
    private FloatingActionButton btnShowAllMedia;

    public FavoriteMediaListFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favorite_media_list, container, false);
        rcvPosts = view.findViewById(R.id.rcvMedia);
        setupToolbar(view);

        btnShowAllMedia  = view.findViewById(R.id.btnShowAllMedia);
        btnShowAllMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.frmMainContainer,new MediaListFragment())
                        .commit();
            }
        });

        txtNoMedia = view.findViewById(R.id.txtNoMedia);

        favoriteAdapter = new FavoriteMediaAdapter(
                getActivity(),
                RealmController.getInstance().getAllFavoriteMedia()
        );

        favoriteAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                if (G.isNetworkAvailable(getActivity())){
                    FavoriteMedia fm = RealmController.getInstance().getAllFavoriteMedia().get(position);
                    if (fm != null){
                        PlayerDialog dialog = new PlayerDialog(
                                getActivity(),
                                txtNoMedia,
                                new Media(fm),
                                favoriteAdapter,
                                position
                        );
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                }else {
                    G.toastLong(G.getStringFromResource(R.string.no_internet, getActivity()), getActivity());
                }

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvPosts.setLayoutManager(linearLayoutManager);
        rcvPosts.setAdapter(favoriteAdapter);

        rcvPosts.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && btnShowAllMedia.getVisibility() == View.VISIBLE) {
                    btnShowAllMedia.hide();
                } else if (dy < 0 && btnShowAllMedia.getVisibility() != View.VISIBLE) {
                    btnShowAllMedia.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (favoriteAdapter.getItemCount() <= 0)
            txtNoMedia.setVisibility(View.VISIBLE);
        else
            txtNoMedia.setVisibility(View.GONE);
    }

    void setupToolbar(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        setupSearchBar(toolbar);
        toolbar.findViewById(R.id.imgToggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).openDrawerLayout();
            }
        });
    }


    private void setupSearchBar(final View parent){
        searchView = parent.findViewById(R.id.searchView);
        searchView.setQueryHint(G.getStringFromResource(R.string.search, getActivity()));
        EditText text = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        text.setTextColor(Color.WHITE);
        text.setHintTextColor(G.getColorFromResource(R.color.colorGray, getActivity()));
        text.setHint(G.getStringFromResource(R.string.search, getActivity()));
        searchView.onActionViewCollapsed();

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchView.isIconified()) {
                    searchView.setIconified(false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (G.isNetworkAvailable(getActivity())){
                    //Closing the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    View view = getActivity().getCurrentFocus();
                    if (view == null) {
                        view = new View(getActivity());
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    callSearch(query);
                    return true;
                }else {
                    G.toastLong(G.getStringFromResource(R.string.no_internet, getActivity()), getActivity());
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchView.getQuery().length() == 0) {
                    favoriteAdapter.setRealmResults(RealmController.getInstance().getAllFavoriteMedia());
                    favoriteAdapter.notifyDataSetChanged();

                    if (favoriteAdapter.getItemCount() <= 0)
                        txtNoMedia.setVisibility(View.VISIBLE);
                    else
                        txtNoMedia.setVisibility(View.GONE);
                }
                return false;
            }

            void callSearch(String query) {
                favoriteAdapter.setRealmResults(RealmController.getInstance().getFavoriteMediaWithTitle(query));
                favoriteAdapter.notifyDataSetChanged();

                if (favoriteAdapter.getItemCount() <= 0)
                    txtNoMedia.setVisibility(View.VISIBLE);
                else
                    txtNoMedia.setVisibility(View.GONE);
            }

        });

    }



}
