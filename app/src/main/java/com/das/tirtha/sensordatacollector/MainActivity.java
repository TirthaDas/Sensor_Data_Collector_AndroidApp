package com.das.tirtha.sensordatacollector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button Button_GoToProjects;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Button_GoToProjects= findViewById(R.id.GoTOProjects);
        Button_GoToProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProjects();
            }
        });
    }

    private void goToProjects() {
        Intent intent = new Intent(this,ProjectList.class);
        startActivity(intent);
    }
}
