package com.mahta.rastin.broadcastapplication.activity.main;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.startup.StartupActivity;
import com.mahta.rastin.broadcastapplication.adapter.DrawerItemCustomAdapter;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.model.DrawerItem;

import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private String[] navigationDrawerItemTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private LinearLayout rightDrawer;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupDrawer();
        bringPostsFragment();

    }

    public void bringPostsFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.frmMainContainer,new PostListFragment(), Keys.POST_LIST_TAG)
                .commit();
    }

    public void bringProgramsFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.frmMainContainer,new ProgramListFragment(), Keys.PROGRAM_LIST_TAG)
                .commit();
    }

    private void setupViews(){
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerList = findViewById(R.id.lstDrawerItems);
        rightDrawer = findViewById(R.id.rightDrawer);

        ((ImageView)findViewById(R.id.imgHeader))
                .setImageBitmap(G.getBitmapFromResources(getResources(), R.drawable.img_green_board));
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        switch (position) {
            case 0:
                drawerLayout.closeDrawer(rightDrawer);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!G.isUserSignedIn()){
                            Intent intent = new Intent(G.currentActivity, StartupActivity.class);
                            intent.putExtra(Keys.KEY_EXTRA_FLAG, 1000);
                            startActivityForResult(intent, 1000);
                        }else {
                            getFragmentManager().beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .replace(R.id.frmMainContainer,new ProfileFragment())
                                    .commit();
                        }

                    }
                },Constant.DRAWER_LAYOUT_CLOSING_DELAY);
                break;
            case 1:
                drawerLayout.closeDrawer(rightDrawer);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frmMainContainer,new PostListFragment())
                                .commit();
                    }
                },Constant.DRAWER_LAYOUT_CLOSING_DELAY);
                break;
            case 2:
                drawerLayout.closeDrawer(rightDrawer);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frmMainContainer,new ProgramListFragment())
                                .commit();
                    }
                },Constant.DRAWER_LAYOUT_CLOSING_DELAY);
                break;
            case 3:
                drawerLayout.closeDrawer(rightDrawer);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frmMainContainer,new MediaListFragment())
                                .commit();
                    }
                },Constant.DRAWER_LAYOUT_CLOSING_DELAY);
                break;
            case 4:
                drawerLayout.closeDrawer(rightDrawer);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO: 7/29/18 check login later 
//                        if (G.isUserSignedIn()) {
//                            getFragmentManager().beginTransaction()
//                                    .replace(R.id.frmMainContainer, new SupportFragment())
//                                    .commit();
//                        }else {
//                            G.toastLong(G.getStringFromResource(
//                                    R.string.pls_login,
//                                    getApplicationContext()),
//                                    getApplicationContext()
//                            );
//                        }
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frmMainContainer, new SupportFragment())
                                .commit();

                    }
                },Constant.DRAWER_LAYOUT_CLOSING_DELAY);
                break;
            case 5:
                drawerLayout.closeDrawer(rightDrawer);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WebFragment fragment = new WebFragment();
                        fragment.setUrl(G.RULES_URL);
                        fragment.setTitle(G.getStringFromResource(R.string.rules, MainActivity.this));
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frmMainContainer,fragment)
                                .commit();
                    }
                },Constant.DRAWER_LAYOUT_CLOSING_DELAY);
                break;
            case 6:
                drawerLayout.closeDrawer(rightDrawer);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WebFragment fragment = new WebFragment();
                        fragment.setUrl(G.ABOUT_US_URL);
                        fragment.setTitle(G.getStringFromResource(R.string.about_us, MainActivity.this));
                        getFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.frmMainContainer,fragment)
                                .commit();
                    }
                },Constant.DRAWER_LAYOUT_CLOSING_DELAY);
                break;
            default:
                break;
        }

        drawerLayout.closeDrawer(rightDrawer);

    }

    public void setupDrawer() {
        if (!G.isUserSignedIn())
            navigationDrawerItemTitles = G.getStringArrayFromResource(
                    R.array.navigation_drawer_items_array,
                    getApplicationContext()
            );
        else
            navigationDrawerItemTitles = G.getStringArrayFromResource(
                    R.array.navigation_drawer_items_array_logged_in,
                    getApplicationContext()
            );

        //setting drawer layout items
        DrawerItem[] drawerItems = new DrawerItem[7];
        drawerItems[0] = new DrawerItem(R.drawable.img_profile, navigationDrawerItemTitles[0]);
        drawerItems[1] = new DrawerItem(R.drawable.img_posts, navigationDrawerItemTitles[1]);
        drawerItems[2] = new DrawerItem(R.drawable.img_calendar, navigationDrawerItemTitles[2]);
        drawerItems[3] = new DrawerItem(R.drawable.img_media_2, navigationDrawerItemTitles[3]);
        drawerItems[4] = new DrawerItem(R.drawable.img_support, navigationDrawerItemTitles[4]);
        drawerItems[5] = new DrawerItem(R.drawable.img_laws, navigationDrawerItemTitles[5]);
        drawerItems[6] = new DrawerItem(R.drawable.img_about_us, navigationDrawerItemTitles[6]);


        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.layout_listview_drawer, drawerItems);
        drawerList.setAdapter(adapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerLayout = findViewById(R.id.drawerLayout);
    }

    public void openDrawerLayout(){
        drawerLayout.openDrawer(rightDrawer);
    }

    
    @Override
    protected void onResume() {
        super.onResume();
        G.currentActivity = this;
    }

    @Override
    public void onBackPressed() {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            G.toastShort(G.getStringFromResource(
                    R.string.click_twice,
                    getApplicationContext()),
                    getApplicationContext()
            );

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2200);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case Keys.LOGIN_OK:
                setupDrawer();
                G.toastLong(G.getStringFromResource(R.string.login_ok, getApplicationContext()), getApplicationContext());
                break;
        }
    }

}
