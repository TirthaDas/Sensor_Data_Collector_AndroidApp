package com.das.tirtha.sensordatacollector;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class projectDetails extends AppCompatActivity {
    private String[] data = new String[2];
    private TextView project_detail_title, mTitle;
    private ArrayList<Sensors> SensorList = new ArrayList<>();
    private SensorsListAdapter sensorsListAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ActionMode actionMode;
    private Button startProject;
    private int STORAGE_PERMISSION_CODE = 1;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        data = getExtasFromIntent(savedInstanceState);

        // bind the components
//        project_detail_title = findViewById(R.id.project_detail_title);
        recyclerView = findViewById(R.id.sensor_list_recycler_view);
        toolbar = findViewById(R.id.project_detail_toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        startProject = findViewById(R.id.start_project);


        //set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sensors  List");
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        project_detail_title.setText(data[0]);
//        project_detail_description.setText(data[1]);

//        project_detail_description.setText(sense.get(0).toString());

//        Toast.makeText(projectDetails.this,"hello  there "+data[0]+data[1],Toast.LENGTH_SHORT).show();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        sensorsListAdapter = new SensorsListAdapter(this, SensorList);
        recyclerView.setAdapter(sensorsListAdapter);
        getAllAvailableSensor();

        startProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // runtime permissions handled first
//              if(ContextCompat.checkSelfPermission(projectDetails.this,Permissions))
                hasStoragePermissions(projectDetails.this, permissions);
                hasSensorsPermissions(projectDetails.this,permissions);


                if (sensorsListAdapter.getSelected().size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < sensorsListAdapter.getSelected().size(); i++) {
                        stringBuilder.append(sensorsListAdapter.getSelected().get(i).getName());
                        stringBuilder.append("\n");
                    }
                    showToast(stringBuilder.toString());
                } else {
                    showToast("No Selection");
                }
            }
        });


    }

    public String[] getExtasFromIntent(Bundle savedInstanceState) {
        String[] data = new String[2];
        String projectTitle;
        String projectDescription;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                projectTitle = null;
                projectDescription = null;
                data[0] = projectTitle;
                data[1] = projectDescription;
                Toast.makeText(projectDetails.this, "no extras found", Toast.LENGTH_SHORT).show();

            } else {
                projectTitle = extras.getString("Project_title");
                projectDescription = extras.getString("Project_Description");
                data[0] = projectTitle;
                data[1] = projectDescription;
//                Toast.makeText(projectDetails.this,"welcome here"+projectTitle+projectDescription,Toast.LENGTH_SHORT).show();

            }
        } else {
            projectTitle = (String) savedInstanceState.getSerializable("Project_title");
            projectDescription = (String) savedInstanceState.getSerializable("Project_Description");
            data[0] = projectTitle;
            data[1] = projectDescription;
//            Toast.makeText(projectDetails.this,"welcome here 1 "+projectTitle+projectDescription,Toast.LENGTH_SHORT).show();

        }
        return data;
    }

    public void getAllAvailableSensor() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        SensorList = new ArrayList<>();
        for (int x = 0; x < sensors.size(); x++) {
            Sensors sensor = new Sensors();
            sensor.setName(sensors.get(x).getName());
            sensor.setType(sensors.get(x).getType());
            sensor.setVendor(sensors.get(x).getVendor());
            SensorList.add(sensor);
        }
        sensorsListAdapter.setmSensorList(SensorList);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void hasStoragePermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("the app needs to access device storage to record sensor data")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(projectDetails.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

                                }
                            }).create().show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                }
            }
        }

    }
    public void hasSensorsPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BODY_SENSORS)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Required")
                            .setMessage("the app needs to access hardware sensors of this device to collect sensor data")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(projectDetails.this, new String[]{Manifest.permission.BODY_SENSORS}, STORAGE_PERMISSION_CODE);

                                }
                            }).create().show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, STORAGE_PERMISSION_CODE);
                }
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Permission Granted");
            } else {
                showToast("Permission Denied");
            }
        }
    }
}
