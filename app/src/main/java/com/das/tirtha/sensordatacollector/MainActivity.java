package com.das.tirtha.sensordatacollector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Button Button_GoToProjects,logout;
    private TextView nav_header_user;
    private  Toolbar toolbar;
    private SharedPreferences sp;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind elements with ids
        toolbar=findViewById(R.id.mainActivityToolbar);
        drawer=findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.nav_view);

        // get the shared preference instance
        sp = getSharedPreferences("login",MODE_PRIVATE);

        // set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Projects");

        // set up drawer navigation
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawer,toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // showing this fragment in the start
        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new available_projects()).commit();
            navigationView.setCheckedItem(R.id.nav_menu_project_list);
        }

        // get all extras passed to main activity
        String User=getAllExtras(savedInstanceState);

        // set username in nav header
        View nav_view=navigationView.getHeaderView(0);
        nav_header_user= nav_view.findViewById(R.id.nav_header_user);
        nav_header_user.setText(User);
    }
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
//            super.onBackPressed();
        }
    }
   private String getAllExtras(Bundle savedInstanceState){
        String username;
       if (savedInstanceState == null) {
           Bundle extras = getIntent().getExtras();
           if(extras == null) {
               username= "";
           } else {
               username= extras.getString("UserName");
           }
       } else {
           username= (String) savedInstanceState.getSerializable("UserName");
       }
       return username;
   }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_menu_project_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new available_projects()).commit();
                break;
            case R.id.nav_menu_active_projects:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new active_projects()).commit();
                break;
            case R.id.nav_menu_sign_out:
                signOut();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void signOut() {
        sp.edit().putBoolean("logged",false).apply();
        sp.edit().remove("UserName").apply();
        Toast.makeText(MainActivity.this,"Signed Out !!!!!!!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,login.class);
        startActivity(intent);
    }
}


/*
old code before creating navigation drawer

inside onCreate:

toolbar=findViewById(R.id.toolbar);
        logout=findViewById(R.id.logout);

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


*/