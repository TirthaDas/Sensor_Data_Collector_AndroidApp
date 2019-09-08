package com.das.tirtha.sensordatacollector;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Project_Description extends AppCompatActivity {
    private TextView projectDescription,mTitle,projectTitleInProjectDescription;
    private String[] data = new String[2];
    private Toolbar toolbar;
    private Button selectSensors;
    private String projectTitle;
    private String projectId;
    private String duration;
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
        projectTitleInProjectDescription=findViewById(R.id.projectTitleInProjectDescription);
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

        //get extras
        projectId=extras.getString("projectId");
        projectTitle = extras.getString("Project_title");
        projectTitleInProjectDescription.setText(projectTitle);
        duration=extras.getString("duration");
        projectDescriptionContent = extras.getString("Project_Description");
        sensorList=extras.getStringArrayList("sensorList");
        Log.d("HELLO", "onCreate: PRIDDDD"+projectId);
//        final String Project_desc=data[1];
//        final String Project_title=data[0];
//        final ArrayList<String> sensorList=data[2];
        projectDescription.setText(projectDescriptionContent);


        selectSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sensorList.size()==0){
                    new AlertDialog.Builder(Project_Description.this)
                            .setTitle("Sensor data not required")
                            .setMessage("This project does not need sensor data, instead it has a questionnaire. press ok to participate")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    Log.d("PRESSSS", "onClick: OK PRESSED");
                                    Intent intent= new Intent(Project_Description.this,Questions_Activity.class);
                                    intent.putExtra("ProjectId",projectId);
                                    startActivity(intent);
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                else {
                    Intent intent = new Intent(Project_Description.this, projectDetails.class);
                    intent.putExtra("Project_title", projectTitle);
                    intent.putExtra("Project_Description", projectDescriptionContent);
                    intent.putExtra("sensorList", sensorList);
                    intent.putExtra("projectId", projectId);
                    intent.putExtra("duration", duration);


                    startActivity(intent);
                }
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
