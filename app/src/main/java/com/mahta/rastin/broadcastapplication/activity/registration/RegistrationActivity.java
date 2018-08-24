package com.mahta.rastin.broadcastapplication.activity.registration;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.main.MainActivity;
import com.mahta.rastin.broadcastapplication.activity.main.ProfileFragment;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.model.Student;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    public boolean registerFromInside = false;
    private boolean doubleBackToExitPressedOnce = false;

    private SmsVerifyCatcher smsVerifyCatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerFromInside = getIntent().hasExtra(Keys.KEY_EXTRA_FLAG);

        if (G.isUserSignedIn()) {
            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frmRegistrationContainer, new LoginFragment())
                    .commit();
        }

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                ConfirmFragment fragment = (ConfirmFragment) getFragmentManager()
                        .findFragmentByTag("CONFIRM");
                if (fragment != null && fragment.isVisible()) {
                    G.i(message);
                    Pattern p = Pattern.compile("-?\\d+");
                    Matcher m = p.matcher(message);
                    List<String> codes = new LinkedList<>();
                    while (m.find()) {
                        codes.add(m.group());
                    }
                    fragment.setPinText(codes.get(0));
                }
            }
        });


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                12);


    }

    public void loginToMain(){
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(intent);
        G.toastLong(G.getStringFromResource(R.string.login_ok, RegistrationActivity.this),
                RegistrationActivity.this);
        RegistrationActivity.this.finish();
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();

        Fragment fragment = fm.findFragmentByTag("CONFIRM");
        if(fragment != null)
            fm.beginTransaction().remove(fragment).commit();

        if (fm.getBackStackEntryCount() > 0) {
            G.i("pop");
            fm.popBackStack();
        } else {
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}