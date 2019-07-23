package com.das.tirtha.sensordatacollector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private Button Button_GoToProjects,logout;
    private Toolbar toolbar;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        logout=findViewById(R.id.logout);
        sp = getSharedPreferences("login",MODE_PRIVATE);
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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }

    private void goToProjects() {
        Intent intent = new Intent(this,ProjectList.class);
        startActivity(intent);
    }

    private void goToLogin() {
        sp.edit().putBoolean("logged",false).apply();
        sp.edit().remove("UserName").apply();
        Intent intent = new Intent(this,login.class);
        startActivity(intent);
    }
}
