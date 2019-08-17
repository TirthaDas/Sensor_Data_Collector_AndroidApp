package com.das.tirtha.sensordatacollector;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class projectDetails extends AppCompatActivity {
    private String[] data = new String[2];
    private TextView project_detail_title, mTitle;
    private ArrayList<Sensors> SensorList = new ArrayList<>();
    private SensorsListAdapter sensorsListAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ActionMode actionMode;
    private Button startProject;
    private SharedPreferences sp;
    private int mStatusCode;

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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        sensorsListAdapter = new SensorsListAdapter(this, SensorList);
        recyclerView.setAdapter(sensorsListAdapter);
        ArrayList<String> sensorList = getSensorList();
        getAllAvailableSensor(sensorList);

        startProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // runtime permissions handled first

                if (!hasSensorsPermissions(projectDetails.this, permissions) || !hasStoragePermissions(projectDetails.this, permissions)) {
                    requestSensorPermission();
                    requestStoragePermission();
                }
                if (hasSensorsPermissions(projectDetails.this, permissions) && hasStoragePermissions(projectDetails.this, permissions)) {

                    //sensor project can be started here now

                    if (sensorsListAdapter.getSelected().size() > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < sensorsListAdapter.getSelected().size(); i++) {
                            stringBuilder.append(sensorsListAdapter.getSelected().get(i).getType());
                            stringBuilder.append("\n");
                        }
                        //  showToast(stringBuilder.toString());
                        //see which number project it is
                        sp = getSharedPreferences("login", MODE_PRIVATE);
                        int myIntValue_active_projects = sp.getInt("active_projects", -1);
                        Log.d("0000000909090009", "ACTIVE PROJECTSSS: " + myIntValue_active_projects);

                        //call the relevant service

//                        switch (myIntValue_active_projects){
//                            case 0:
//                                SharedPreferences.Editor editor= sp.edit();
//                                editor.putInt("active_projects",1).apply();
//                                Intent serviceIntent= new Intent(projectDetails.this,SensorService.class);
//                                serviceIntent.putExtra("sensors",stringBuilder.toString());
//                                serviceIntent.putExtra("projectId",data[2]);
//                                startService(serviceIntent);
//
//                                break;
//
//                            case 1:
//                                Log.d("casee1", "onClick: CASE 1"+sp.getInt("active_projects",0));
//
//                                Intent serviceIntent2= new Intent(projectDetails.this,SensorServiceSecondProject.class);
//                                serviceIntent2.putExtra("sensors",stringBuilder.toString());
//                                serviceIntent2.putExtra("projectId",data[2]);
//                                startService(serviceIntent2);
//                                SharedPreferences.Editor editor1= sp.edit();
//                                editor1.putInt("active_projects",2).apply();
//                                Log.d("casee12333", "onClick: CASE 1"+sp.getInt("active_projects",0));
//
//                                break;
//
//                            case 2:
//                                Log.d("casee2<<<<", "onClick: CASE 1"+sp.getInt("active_projects",0));
//
//                                SharedPreferences.Editor editor2= sp.edit();
//                                editor2.putInt("active_projects",3).apply();
//                                Intent serviceIntent3= new Intent(projectDetails.this,SensorServiceThirdProject.class);
//                                serviceIntent3.putExtra("sensors",stringBuilder.toString());
//                                serviceIntent3.putExtra("projectId",data[2]);
//                                startService(serviceIntent3);
//
//                                break;
//                                default:
//                                    showToast("maximum number of projects already running");
//                        }

                        Intent AvailableProjectsIntent = new Intent(projectDetails.this, MainActivity.class);
                        startActivity(AvailableProjectsIntent);


                        ServiceHelper serviceHelper = new ServiceHelper(data[2], stringBuilder.toString(), myIntValue_active_projects, projectDetails.this);
                        serviceHelper.startService(false);
                        String userId = sp.getString("UserId", "");

//                        addToActiveProjects(stringBuilder.toString(),userId,data[2]);


                    } else {
                        showToast("No Selection");
                    }
                }
            }
        });


    }

    public String[] getExtasFromIntent(Bundle savedInstanceState) {
        String[] data = new String[3];
        String projectTitle;
        String projectDescription;
        String projectId;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                projectTitle = null;
                projectDescription = null;
                data[0] = projectTitle;
                data[1] = projectDescription;
                data[2] = "";
                Toast.makeText(projectDetails.this, "no extras found", Toast.LENGTH_SHORT).show();

            } else {
                projectTitle = extras.getString("Project_title");
                projectDescription = extras.getString("Project_Description");
                projectId = extras.getString("projectId");
                data[0] = projectTitle;
                data[1] = projectDescription;
                data[2] = projectId;
                ArrayList<String> sensorList = extras.getStringArrayList("sensorList");
//                Toast.makeText(projectDetails.this,"welcome here"+projectTitle+projectDescription+sensorList,Toast.LENGTH_SHORT).show();

            }
        } else {
            projectTitle = (String) savedInstanceState.getSerializable("Project_title");
            projectDescription = (String) savedInstanceState.getSerializable("Project_Description");
            data[0] = projectTitle;
            data[1] = projectDescription;
            data[2] = "";
//            Toast.makeText(projectDetails.this,"welcome here 1 "+projectTitle+projectDescription,Toast.LENGTH_SHORT).show();

        }
        return data;
    }

    public ArrayList<String> getSensorList() {
        Bundle extras = getIntent().getExtras();
        ArrayList<String> sensorList = extras.getStringArrayList("sensorList");
        return sensorList;
    }

    public void getAllAvailableSensor(ArrayList<String> sensorList) {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        SensorList = new ArrayList<>();
        for (int h = 0; h < sensorList.size(); h++) {

            String searchKey = sensorList.get(h);
            for (int x = 0; x < sensors.size(); x++) {
                String TAG = "hey ";
//            Log.d(TAG, "getAllAvailableSensor:  XXXXXXXXYYYYYYYY" + sensors.get(x).getStringType().substring(sensors.get(x).getStringType().lastIndexOf('.')));
                if (sensors.get(x).getName().toLowerCase().contains(searchKey.toLowerCase())) {
                    Sensors sensor = new Sensors();
                    sensor.setName(sensors.get(x).getName());
                    sensor.setType(sensors.get(x).getStringType().substring(sensors.get(x).getStringType().lastIndexOf('.') + 1));
                    sensor.setVendor(sensors.get(x).getVendor());
                    SensorList.add(sensor);
                    break;
                }
            }
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

    public boolean hasStoragePermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        }
        return true;

    }

    public boolean hasSensorsPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        }
        return true;

    }


    public void requestSensorPermission() {
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


    public void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Required")
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

    public void addToActiveProjects(final String SensorList, final String userId, final String ProjectId) {
        Log.d("TAAAG", "addToActiveProjects: ");
        String ip = getResources().getString(R.string.IP);
        String url_addToActiveProjects = ip+"api/addToActiveProjects";

        // setting the volley request and listener

        StringRequest request = new StringRequest(Request.Method.POST, url_addToActiveProjects,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            String message = responseObject.getString("message");
                            if (mStatusCode == 200) {
                                String ActiveProjectID = responseObject.getString("_id");
                                Toast.makeText(projectDetails.this, message+ActiveProjectID, Toast.LENGTH_SHORT).show();

                            } else if (mStatusCode == 201) {
                                Toast.makeText(projectDetails.this, message, Toast.LENGTH_SHORT).show();

                            } else if (mStatusCode == 400||mStatusCode==401) {
                                Toast.makeText(projectDetails.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(projectDetails.this, "adding active projects error" + e.toString(), Toast.LENGTH_SHORT).show();


                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(projectDetails.this, "adding active projects  error" + error.toString(), Toast.LENGTH_SHORT).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userId",userId);
                params.put("projectId", ProjectId);
                params.put("sensorList", SensorList);
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }
}
