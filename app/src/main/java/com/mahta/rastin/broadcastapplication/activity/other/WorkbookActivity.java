package com.mahta.rastin.broadcastapplication.activity.other;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mahta.rastin.broadcastapplication.R;
import com.mahta.rastin.broadcastapplication.adapter.WorkbookAdapter;
import com.mahta.rastin.broadcastapplication.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.global.Keys;
import com.mahta.rastin.broadcastapplication.helper.DateConverter;
import com.mahta.rastin.broadcastapplication.model.Workbook;
import com.mahta.rastin.broadcastapplication.model.WorkbookTuple;

import java.util.ArrayList;
import java.util.List;

public class WorkbookActivity extends AppCompatActivity {

    private Workbook workbook;
    private String[] grades;
    private String[] lessons;
    private int scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workbook);

        workbook = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);
        try {
            grades = workbook.getGrades().split("\\|");
            lessons = workbook.getLessons().split("\\|");
            scale = workbook.getScale();

            G.i(workbook.getGrades());
            for (int i = 0; i < grades.length; i++) {
                G.i(lessons[i] + ": " + grades[i]);
            }

            setupToolbar();
            fillWorkbook();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DateConverter converter = new DateConverter();
        String[] created_date = workbook.getCreated_at().split(" ")[0].split("-");
        String[] updated_date = workbook.getUpdated_at().split(" ")[0].split("-");

        ((TextViewPlus)findViewById(R.id.txtInsertDate)).setText(converter.GregorianToPersian(
                Integer.parseInt(created_date[0]),
                Integer.parseInt(created_date[1]),
                Integer.parseInt(created_date[2])).toString());

        ((TextViewPlus)findViewById(R.id.txtUpdateDate)).setText(converter.GregorianToPersian(
                Integer.parseInt(updated_date[0]),
                Integer.parseInt(updated_date[1]),
                Integer.parseInt(updated_date[2])).toString());

        ((TextViewPlus)findViewById(R.id.txtScale)).setText(scale+"");
    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.findViewById(R.id.imgBack).setVisibility(View.VISIBLE);

        String title = G.getStringFromResource(R.string.workbook, this)
                + " " +
                workbook.getMonth() + " " + workbook.getYear();
        ((TextViewPlus)toolbar.findViewById(R.id.txtTitle)).setText(title);
    }

    private void fillWorkbook() {
        //preparing data
        List<WorkbookTuple> workbookTupleList = new ArrayList<>();
        for (int i=0; i<lessons.length; i++){
            workbookTupleList.add(new WorkbookTuple(lessons[i], grades[i], scale));
        }
        //setting up recycler view
        RecyclerView recyclerView = findViewById(R.id.rcvWorkbook);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        WorkbookAdapter adapter = new WorkbookAdapter(this, workbookTupleList);
        recyclerView.setAdapter(adapter);
    }

}
