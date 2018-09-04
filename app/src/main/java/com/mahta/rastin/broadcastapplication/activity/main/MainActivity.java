package com.mahta.rastin.broadcastapplication.activity.main;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.other.UpdateInfoActivity;
import com.mahta.rastin.broadcastapplication.adapter.DrawerItemCustomAdapter;
import com.mahta.rastin.broadcastapplication.adapter.ImageSliderAdapter;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.interfaces.OnFragmentActionListener;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.DrawerItem;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity{
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private LinearLayout rightDrawer;
    private boolean doubleBackToExitPressedOnce = false;
    private ViewPager viewPager;
    int currentPage = 0;
    int NUM_PAGES = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        setupViews();
        setupDrawer();
        bringPostsFragment();

        loadSlider();
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
                        ProfileFragment fragment = new ProfileFragment();
                        fragment.setOnUpdateInfo(new OnFragmentActionListener() {
                            @Override
                            public void onAction(View view, Bundle data) {
                                Intent intent = new Intent(MainActivity.this, UpdateInfoActivity.class);
                                startActivityForResult(intent, 1000);
                            }
                        });
                        getFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.frmMainContainer,fragment, "ProfileFragment")
                                .commit();

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
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frmMainContainer,new MessageListFragment())
                                .commit();
                    }
                },Constant.DRAWER_LAYOUT_CLOSING_DELAY);
                break;
            case 5:
                drawerLayout.closeDrawer(rightDrawer);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frmMainContainer,new StaffListFragment())
                                .commit();
                    }
                },Constant.DRAWER_LAYOUT_CLOSING_DELAY);
                break;
            case 6:
                drawerLayout.closeDrawer(rightDrawer);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frmMainContainer, new SupportFragment())
                                .commit();

                    }
                },Constant.DRAWER_LAYOUT_CLOSING_DELAY);
                break;
            case 7:
                drawerLayout.closeDrawer(rightDrawer);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WebFragment fragment = new WebFragment();
                        fragment.setUrl(G.HELP_URL);
                        fragment.setTitle(G.getStringFromResource(R.string.help, MainActivity.this));
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frmMainContainer,fragment)
                                .commit();
                    }
                },Constant.DRAWER_LAYOUT_CLOSING_DELAY);
                break;
            case 8:
                drawerLayout.closeDrawer(rightDrawer);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WebFragment fragment = new WebFragment();
                        fragment.setUrl(G.BASE_URL + "about");
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
        String[] navigationDrawerItemTitles = G.getStringArrayFromResource(
                R.array.navigation_drawer_items_array,
                getApplicationContext()
        );

        //setting drawer layout items
        DrawerItem[] drawerItems = new DrawerItem[9];
        drawerItems[0] = new DrawerItem(R.drawable.img_profile, navigationDrawerItemTitles[0]);
        drawerItems[1] = new DrawerItem(R.drawable.img_posts, navigationDrawerItemTitles[1]);
        drawerItems[2] = new DrawerItem(R.drawable.img_calendar, navigationDrawerItemTitles[2]);
        drawerItems[3] = new DrawerItem(R.drawable.img_media_2, navigationDrawerItemTitles[3]);
        drawerItems[4] = new DrawerItem(R.drawable.img_messages, navigationDrawerItemTitles[4]);
        drawerItems[5] = new DrawerItem(R.drawable.img_member, navigationDrawerItemTitles[5]);
        drawerItems[6] = new DrawerItem(R.drawable.img_support, navigationDrawerItemTitles[6]);
        drawerItems[7] = new DrawerItem(R.drawable.img_help, navigationDrawerItemTitles[7]);
        drawerItems[8] = new DrawerItem(R.drawable.img_about_us, navigationDrawerItemTitles[8]);


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
            case Keys.UPDATE_OK:
                ProfileFragment fragment = (ProfileFragment) getFragmentManager()
                        .findFragmentByTag("ProfileFragment");
                if (fragment != null && fragment.isVisible()) {
                    fragment.loadInfo(fragment.getView());
                }
                break;
        }
    }


    // Slider part ********************************************************************************

    private void loadSlider() {
        if (!G.realmController.hasSlider()) {
            getSliderData();
            G.i("gotcha");
        }
        else {
            G.slider = G.realmController.getSlider();

            new HttpCommand(HttpCommand.COMMAND_GET_SLIDER_UPDATED, null)
                    .setOnResultListener(new OnResultListener() {
                        @Override
                        public void onResult(String result) {
                            int resultCode = JSONParser.getResultCodeFromJson(result);
                            if (resultCode == Keys.RESULT_SUCCESS){
                                try {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                                    Date last_updated = format.parse(G.slider.getUpdated_at());
                                    Date updated = JSONParser.parseSliderUpdated(result);

                                    if (updated != null && last_updated != null){
                                        if (updated.after(last_updated)){
                                            getSliderData();
                                        }else {
                                            setSliderViews();
                                            G.i("old Date: " + G.slider.getUpdated_at());
                                        }
                                    }else
                                        G.e("Wrong date 1");

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    G.e("Wrong date 2");
                                }
                            } else
                                G.e("Wrong user id");

                        }
                    }).execute();
        }

    }

    private void getSliderData(){
        new HttpCommand(HttpCommand.COMMAND_GET_SLIDER, null)
                .setOnResultListener(new OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        int resultCode = JSONParser.getResultCodeFromJson(result);
                        if (resultCode == Keys.RESULT_SUCCESS) {
                            G.slider = JSONParser.parseSlider(result);
                            G.realmController.addSlider(G.slider);
                            setSliderViews();
                            G.i("new Date: " + G.slider.getUpdated_at());
                        }
                        else
                            G.e("Wrong user id");
                    }
                }).execute();
    }

    private void setSliderViews() {
        //Set the pager with an adapter
        if (getImageList().size() == 0)
            return;

        findViewById(R.id.imgHeader).setVisibility(View.GONE);
        findViewById(R.id.rtlSlider).setVisibility(View.VISIBLE);

        viewPager = findViewById(R.id.viewPager);
        ImageSliderAdapter adapter = new ImageSliderAdapter(MainActivity.this,getImageList());

        viewPager.setAdapter(adapter);

        CirclePageIndicator indicator = findViewById(R.id.indicator);

        indicator.setViewPager(viewPager);

        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES =getImageList().size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, Constant.SLIDER_TIME, Constant.SLIDER_TIME);


        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

        (findViewById(R.id.imgRight)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        });

        (findViewById(R.id.imgLeft)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == 0) {
                    currentPage = NUM_PAGES;
                }
                viewPager.setCurrentItem(--currentPage, true);
            }
        });
    }

    private List<String> getImageList(){
        List<String> images = new LinkedList<>();
        if (!G.slider.getImage_1().equals("null")){
            images.add(G.slider.getImage_1());
        }

        if (!G.slider.getImage_2().equals("null")){
            images.add(G.slider.getImage_2());
        }

        if (!G.slider.getImage_3().equals("null")){
            images.add(G.slider.getImage_3());
        }

        if (!G.slider.getImage_4().equals("null")){
            images.add(G.slider.getImage_4());
        }

        return images;
    }



}
