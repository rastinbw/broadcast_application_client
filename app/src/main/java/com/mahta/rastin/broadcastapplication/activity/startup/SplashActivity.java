package com.mahta.rastin.broadcastapplication.activity.startup;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.registration.RegistrationActivity;
import com.mahta.rastin.broadcastapplication.global.Constant;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.HttpCommand;
import com.mahta.rastin.broadcastapplication.helper.JSONParser;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplication.model.Group;

import java.util.List;
import java.util.Locale;


public class SplashActivity extends AppCompatActivity {

    boolean isSplashDone = false;
    boolean isDataReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(new Locale("en"));
        setContentView(R.layout.activity_splash);


        if (G.isNetworkAvailable(getApplicationContext())) {
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                startService(new Intent(this, NotificationService.class));
//            }
            runSplash();
        }else {
            findViewById(R.id.rtlNoNetwork).setVisibility(View.VISIBLE);
            findViewById(R.id.imgLogo).setVisibility(View.GONE);
        }

        findViewById(R.id.btnTryAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (G.isNetworkAvailable(getApplicationContext())){
                    findViewById(R.id.rtlNoNetwork).setVisibility(View.GONE);
                    findViewById(R.id.imgLogo).setVisibility(View.VISIBLE);
                    runSplash();
                }
            }
        });
    }

    private void runSplash() {
        isSplashDone = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isSplashDone = true;
                if (isDataReceived)
                    go();
                else{
                    findViewById(R.id.rtlNoNetwork).setVisibility(View.VISIBLE);
                    findViewById(R.id.imgLogo).setVisibility(View.GONE);
                }
            }
        }, Constant.SPLASH_TIME);

        checkToken();

        if (!isDataReceived) {
            new HttpCommand(HttpCommand.COMMAND_GET_GROUP_LIST, null)
                    .setOnResultListener(new OnResultListener() {
                        @Override
                        public void onResult(String result) {
                            G.realmController.clearAllGroups();
                            List<Group> list = JSONParser.parseGroups(result);
                            if (list != null) {
                                for (Group group : list) {
                                    G.realmController.addGroup(group);
                                    G.i(group.getTitle());
                                }
                            }

                            isDataReceived = true;
                            if (isSplashDone) {
                                go();
                            }
                        }
                    }).execute();
        }
    }

    private void go(){
        Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
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
                               G.logout(getApplicationContext());
                            }
                        }
                    }).execute();
        }
    }

    @SuppressWarnings("deprecation")
    private void setLocale(Locale locale){
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            configuration.setLocale(locale);
            getApplicationContext().createConfigurationContext(configuration);
        }
        else{
            configuration.locale=locale;
            resources.updateConfiguration(configuration,displayMetrics);
        }
    }


}
