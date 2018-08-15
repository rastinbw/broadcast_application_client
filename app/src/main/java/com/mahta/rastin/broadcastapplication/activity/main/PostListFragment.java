package com.mahta.rastin.broadcastapplication.activity.main;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplication.custom.ButtonPlus;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.post_display.PostDisplayActivity;
import com.mahta.rastin.broadcastapplication.adapter.PostAdapter;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.helper.RealmController;
import com.mahta.rastin.broadcastapplication.interfaces.EndlessRecyclerViewScrollListener;
import com.mahta.rastin.broadcastapplication.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.Post;

import java.util.List;


public class PostListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private boolean noMorePost = false;

    public SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton btnShowFavoritePost;
    private EndlessRecyclerViewScrollListener scrollListener;
    private PostAdapter adapter;
    private TextView txtNoPosts;
    public SearchView searchView;
    private String searchPhrase = "null";
    private boolean isLoaded;
    private LinearLayout lnlLoading;
    private LinearLayout lnlNoNetwork;
    private ButtonPlus btnTryAgain;
    private boolean isFirstLoad = true;
    private boolean doesFragmentExists = true;
    private boolean isLoading;


    public PostListFragment() {
        clearPosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        final RecyclerView rcvPosts = view.findViewById(R.id.rcvPosts);
        setupToolbar(view);

        btnShowFavoritePost = view.findViewById(R.id.btnShowFavoritePost);
        view.findViewById(R.id.btnShowFavoritePost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.frmMainContainer,new FavoritePostListFragment())
                        .commit();
            }
        });

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
                loadPosts(Constant.POST_REQUEST_COUNT,0,0, searchPhrase);
            }
        });

        txtNoPosts = view.findViewById(R.id.txtNoPost);
        lnlLoading = view.findViewById(R.id.lnlLoading);
        lnlNoNetwork = view.findViewById(R.id.lnlNoNetwork);

        adapter = new PostAdapter(getActivity(), RealmController.getInstance().getAllPosts());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                if (G.isNetworkAvailable(getActivity())){
                    Intent intent = new Intent(getActivity(), PostDisplayActivity.class);
                    intent.putExtra(Keys.KEY_EXTRA_FLAG,RealmController.getInstance().getAllPosts().get(position));
                    startActivity(intent);
                }else {
                    G.toastLong(G.getStringFromResource(R.string.no_internet, getActivity()), getActivity());
                }

            }
        });
        rcvPosts.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvPosts.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                G.i("YOOOHO");

                G.i("passed threshold p: " + page + " activity: " + totalItemsCount);
                if (!noMorePost){
                    loadPosts(Constant.POST_REQUEST_COUNT,totalItemsCount,page, searchPhrase);
                }else
                    noMorePost = false;

            }

            @Override
            public void onScroll(RecyclerView view, int dx, int dy) {
                if (dy > 0 && btnShowFavoritePost.getVisibility() == View.VISIBLE) {
                    btnShowFavoritePost.hide();
                } else if (dy < 0 && btnShowFavoritePost.getVisibility() != View.VISIBLE) {
                    btnShowFavoritePost.show();
                }
            }
        };
        // Adds the scroll listener to RecyclerView
        rcvPosts.addOnScrollListener(scrollListener);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadPosts(Constant.POST_REQUEST_COUNT,0,0, searchPhrase);
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
                    //Closing the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    View view = getActivity().getCurrentFocus();
                    if (view == null) {
                        view = new View(getActivity());
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    callSearch(query);
                    return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchView.getQuery().length() == 0) {
                    reset();
                    searchPhrase = "null";
                    loadPosts(Constant.POST_REQUEST_COUNT, 0, 0, "null");
                }
                return false;
            }

            void callSearch(String query) {
                reset();
                searchPhrase = query;
                loadPosts(Constant.POST_REQUEST_COUNT, 0, 0, query);
//                //Make it visible
//                prgWait.setVisibility(View.VISIBLE);
            }

        });

    }

    private void loadPosts(final int count, final int start, final int page, String searchPhrase){
        isLoading = true;

        if (isFirstLoad){
            changeLoadingResource(1);
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
        }

        new HttpCommand(HttpCommand.COMMAND_GET_POSTS,null,Constant.TYPE_HTML, count+"",page+"", searchPhrase, "null")
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        if (doesFragmentExists) {
                            isLoading = false;
                            if (isFirstLoad) {
                                isLoaded = true;
                                isFirstLoad = false;
                                changeLoadingResource(2);
                            }

                            List<Post> posts = JSONParser.parsePosts(result);
                            if (posts != null) {
                                for (Post post : posts) {
                                    G.i(post.getId() + "");
                                    RealmController.getInstance().addPost(post);
                                }
                                adapter.notifyItemRangeInserted(start - 1, posts.size());

                                if (posts.size() < count)
                                    noMorePost = true;

                            } else {
                                G.i("No more Post is returned");
                            }

                            if (adapter.getItemCount() <= 0)
                                txtNoPosts.setVisibility(View.VISIBLE);
                            else
                                txtNoPosts.setVisibility(View.GONE);
                        }
                    }
                }).execute();
    }

    private void changeLoadingResource(int state) {
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }

        switch (state) {
            case 0:
                txtNoPosts.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.VISIBLE);
                lnlLoading.setVisibility(View.GONE);
                break;
            case 1:
                txtNoPosts.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.VISIBLE);
                break;
            default:
                txtNoPosts.setVisibility(View.GONE);
                lnlNoNetwork.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                lnlLoading.setVisibility(View.GONE);
                break;
        }
    }


    private void reset() {
        clearPosts();
        isFirstLoad = true;
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }

    private void clearPosts(){
        RealmController.getInstance().clearAllPosts();
    }

    @Override
    public void onRefresh() {
        if (!isLoading){
            reset();
            loadPosts(Constant.POST_REQUEST_COUNT, 0, 0, searchPhrase);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doesFragmentExists = false;
    }
}
