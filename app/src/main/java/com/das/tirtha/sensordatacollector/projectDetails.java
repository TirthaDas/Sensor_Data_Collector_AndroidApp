package com.das.tirtha.sensordatacollector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class projectDetails extends AppCompatActivity {
private String[] data= new String[2];
private TextView project_detail_title,project_detail_description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        data=getExtasFromIntent(savedInstanceState);
        project_detail_description=findViewById(R.id.project_detail_description);
        project_detail_title=findViewById(R.id.project_detail_title);
        project_detail_title.setText(data[0]);
        project_detail_description.setText(data[1]);

//        Toast.makeText(projectDetails.this,"hello  there "+data[0]+data[1],Toast.LENGTH_SHORT).show();


    }
    public String[] getExtasFromIntent(Bundle savedInstanceState){
        String[] data= new String[2];
        String projectTitle;
        String projectDescription;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                projectTitle= null;
                projectDescription=null;
                data[0]=projectTitle;
                data[1]=projectDescription;
                Toast.makeText(projectDetails.this,"no extras found",Toast.LENGTH_SHORT).show();

            } else {
                projectTitle= extras.getString("Project_title");
                projectDescription= extras.getString("Project_Description");
                data[0]=projectTitle;
                data[1]=projectDescription;
//                Toast.makeText(projectDetails.this,"welcome here"+projectTitle+projectDescription,Toast.LENGTH_SHORT).show();

            }
        } else {
            projectTitle= (String) savedInstanceState.getSerializable("Project_title");
            projectDescription= (String) savedInstanceState.getSerializable("Project_Description");
            data[0]=projectTitle;
            data[1]=projectDescription;
//            Toast.makeText(projectDetails.this,"welcome here 1 "+projectTitle+projectDescription,Toast.LENGTH_SHORT).show();

        }
        return data;
    }
}
