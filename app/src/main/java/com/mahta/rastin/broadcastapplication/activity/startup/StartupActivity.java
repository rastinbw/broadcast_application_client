package com.mahta.rastin.broadcastapplication.activity.startup;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.activity.main.MainActivity;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.model.Student;

public class StartupActivity extends AppCompatActivity {
    private Student currentStudent;
    public boolean registerFromInside = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        registerFromInside = getIntent().hasExtra(Keys.KEY_EXTRA_FLAG);

        if (G.isUserSignedIn()) {
            Intent intent = new Intent(StartupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frmStartupContainer, new EntranceFragment())
                    .commit();
        }

    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(Student currentStudent) {
        this.currentStudent = currentStudent;
    }

    public void bringConfirmFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.frmStartupContainer, new ConfirmFragment())
                .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}