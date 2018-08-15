package com.mahta.rastin.broadcastapplication.activity.post_display;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.model.Program;

public class ProgramDisplayActivity extends AppCompatActivity {

    private Program currentProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);

        currentProgram = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);
        getFragmentManager().beginTransaction()
                .replace(R.id.frmPostDisplayContainer,new ProgramContentFragment())
                .commit();

    }


    public String getContent() {
        return currentProgram.getContent();
    }

    public Program getCurrentProgram() {
        return currentProgram;
    }
}
