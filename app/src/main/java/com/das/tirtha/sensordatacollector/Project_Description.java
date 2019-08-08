package com.das.tirtha.sensordatacollector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Project_Description extends AppCompatActivity {
    private TextView projectDescription,mTitle;
    private String[] data = new String[2];
    private Toolbar toolbar;
    private Button selectSensors;
    private String projectTitle;
    private String projectDescriptionContent;
    private ArrayList<String> sensorList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project__description);

        // bind components to ids
        projectDescription=findViewById(R.id.project_description_Textview);
        toolbar=findViewById(R.id.project_description_toolbar);
        mTitle =  toolbar.findViewById(R.id.toolbar_title);
        selectSensors=findViewById(R.id.got_to_sensors);

        // set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Project Details");
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //get data from shared preference
//        data,sensorList = getExtasFromIntent(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        projectTitle = extras.getString("Project_title");
        projectDescriptionContent = extras.getString("Project_Description");
        sensorList=extras.getStringArrayList("sensorList");
//        final String Project_desc=data[1];
//        final String Project_title=data[0];
//        final ArrayList<String> sensorList=data[2];
        projectDescription.setText(projectDescriptionContent);


        selectSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Project_Description.this,projectDetails.class);
                intent.putExtra("Project_title",projectTitle);
                intent.putExtra("Project_Description",projectDescriptionContent);
                intent.putExtra("sensorList",sensorList);

               startActivity(intent);
            }
        });



    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public String[] getExtasFromIntent(Bundle savedInstanceState) {
        String[] data = new String[2];
        String projectTitle;
        String projectDescription;
        ArrayList<String> sensorList;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                projectTitle = null;
                projectDescription = null;
                data[0] = projectTitle;
                data[1] = projectDescription;
                sensorList= null;
//                Toast.makeText(Project_Description.this, "no extras found", Toast.LENGTH_SHORT).show();

            } else {
                projectTitle = extras.getString("Project_title");
                projectDescription = extras.getString("Project_Description");
                sensorList=extras.getStringArrayList("sensorList");
                data[0] = projectTitle;
                data[1] = projectDescription;
                Toast.makeText(this,"welcome here"+sensorList,Toast.LENGTH_SHORT).show();

            }
        } else {
            projectTitle = (String) savedInstanceState.getSerializable("Project_title");
            projectDescription = (String) savedInstanceState.getSerializable("Project_Description");
            data[0] = projectTitle;
            data[1] = projectDescription;
            sensorList=null;

//            Toast.makeText(projectDetails.this,"welcome here 1 "+projectTitle+projectDescription,Toast.LENGTH_SHORT).show();

        }
        return data;
    }

}
