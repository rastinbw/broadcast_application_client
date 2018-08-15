package com.mahta.rastin.broadcastapplication.activity.main;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
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
import com.mahta.rastin.broadcastapplication.activity.post_display.ProgramDisplayActivity;
import com.mahta.rastin.broadcastapplication.adapter.FavoriteProgramAdapter;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplication.model.FavoriteProgram;
import com.mahta.rastin.broadcastapplication.model.Program;

public class FavoriteProgramListFragment extends Fragment {
    public FavoriteProgramAdapter favoriteAdapter;
    public TextView txtNoProgram;
    private RecyclerView rcvPrograms;
    public SearchView searchView;
    private FloatingActionButton btnShowAllProgram;

    private Integer currentId = null;
    private Integer currentPosition= null;

    public FavoriteProgramListFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favorite_program_list, container, false);
        rcvPrograms = view.findViewById(R.id.rcvMedia);
        setupToolbar(view);

        btnShowAllProgram = view.findViewById(R.id.btnShowAllProgram);
        btnShowAllProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).bringProgramsFragment();
            }
        });
        txtNoProgram = view.findViewById(R.id.txtNoProgram);

        favoriteAdapter = new FavoriteProgramAdapter(
                getActivity(),
                RealmController.getInstance().getAllFavoritePrograms()
        );

        favoriteAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                if (G.isNetworkAvailable(getActivity())){
                    FavoriteProgram fp = RealmController.getInstance().getAllFavoritePrograms().get(position);
                    if (fp != null){
                        if (G.isNetworkAvailable(getActivity())){
                            Intent intent = new Intent(getActivity(), ProgramDisplayActivity.class);
                            intent.putExtra(Keys.KEY_EXTRA_FLAG,new Program(fp));
                            currentId = fp.getId();
                            currentPosition = position;
                            startActivity(intent);
                        }else {
                            G.toastLong(G.getStringFromResource(R.string.no_internet, getActivity()), getActivity());
                        }
                    }
                }else {
                    G.toastLong(G.getStringFromResource(R.string.no_internet, getActivity()), getActivity());
                }

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvPrograms.setLayoutManager(linearLayoutManager);
        rcvPrograms.setAdapter(favoriteAdapter);

        rcvPrograms.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && btnShowAllProgram.getVisibility() == View.VISIBLE) {
                    btnShowAllProgram.hide();
                } else if (dy < 0 && btnShowAllProgram.getVisibility() != View.VISIBLE) {
                    btnShowAllProgram.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (favoriteAdapter.getItemCount() <= 0)
            txtNoProgram.setVisibility(View.VISIBLE);
        else
            txtNoProgram.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentId != null){
            if (!RealmController.getInstance().hasFavoriteProgram(currentId)) {
                favoriteAdapter.notifyItemRemoved(currentPosition);
                if (favoriteAdapter.getItemCount() <= 0)
                    txtNoProgram.setVisibility(View.VISIBLE);
            }
            currentId = null;
            currentPosition = null;
        }
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
                    favoriteAdapter.setRealmResults(RealmController.getInstance().getAllFavoritePrograms());
                    favoriteAdapter.notifyDataSetChanged();

                    if (favoriteAdapter.getItemCount() <= 0)
                        txtNoProgram.setVisibility(View.VISIBLE);
                    else
                        txtNoProgram.setVisibility(View.GONE);
                }
                return false;
            }

            void callSearch(String query) {
                favoriteAdapter.setRealmResults(RealmController.getInstance().getFavoriteProgramWithTitle(query));
                favoriteAdapter.notifyDataSetChanged();

                if (favoriteAdapter.getItemCount() <= 0)
                    txtNoProgram.setVisibility(View.VISIBLE);
                else
                    txtNoProgram.setVisibility(View.GONE);
            }

        });

    }



}
