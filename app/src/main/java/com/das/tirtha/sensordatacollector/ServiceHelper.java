package com.das.tirtha.sensordatacollector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ServiceHelper {
    // member variables
    private String ProjectId;
    private String SensorList;
    int myIntValue_active_projects;
    private Context context;
    SharedPreferences sp;
    private int mStatusCode;
    private int mStatusCode1;


    // constructors
    public ServiceHelper(String ProjectId,Context context) {
        this.ProjectId = ProjectId;
        this.context=context;
    }

    public ServiceHelper(String ProjectId, String SensorList, int myIntValue_active_projects,Context context) {
        this.ProjectId = ProjectId;
        this.SensorList = SensorList;
        this.myIntValue_active_projects=myIntValue_active_projects;
        this.context=context;
    }


    //Start service method
    public void startService(Boolean fromActiveProjectsScreen) {
        sp = context.getSharedPreferences("login", MODE_PRIVATE);
        Log.d("FROM SERVICE HELPER", "ACTIVE PROJECTSSS: " + myIntValue_active_projects);
        String UserId=sp.getString("UserId","noUser");

        //call the relevant service

        switch (myIntValue_active_projects) {
            case 0:
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("active_projects", 1);
                editor.putString("case0",ProjectId);
                editor.apply();
                addToActiveProjects(SensorList,UserId,ProjectId);
                Intent serviceIntent = new Intent(context, SensorService.class);
                serviceIntent.putExtra("sensors", SensorList);
                serviceIntent.putExtra("projectId", ProjectId);
                context.startService(serviceIntent);
//                addToActiveProjects(SensorList,UserId,ProjectId);


                break;

            case 1:
                SharedPreferences.Editor editor1 = sp.edit();
                editor1.putInt("active_projects", 2);
                editor1.putString("case1",ProjectId);
                editor1.apply();
                addToActiveProjects(SensorList,UserId,ProjectId);
                Intent serviceIntent2 = new Intent(context, SensorServiceSecondProject.class);
                serviceIntent2.putExtra("sensors", SensorList);
                serviceIntent2.putExtra("projectId", ProjectId);
                context.startService(serviceIntent2);
//                addToActiveProjects(SensorList,UserId,ProjectId);

                break;

            case 2:
                SharedPreferences.Editor editor2 = sp.edit();
                editor2.putInt("active_projects", 3);
                editor2.putString("case2",ProjectId);
                editor2.apply();
                addToActiveProjects(SensorList,UserId,ProjectId);
                Intent serviceIntent3 = new Intent(context, SensorServiceThirdProject.class);
                serviceIntent3.putExtra("sensors", SensorList);
                serviceIntent3.putExtra("projectId",ProjectId);
                context.startService(serviceIntent3);

                break;
            default:
                showToast("maximum number of projects already running");
        }
//        addToActiveProjects(SensorList,UserId,ProjectId);

        if(!fromActiveProjectsScreen){
            Intent AvailableProjectsIntent = new Intent(context, MainActivity.class);
            context.startActivity(AvailableProjectsIntent);
        }

    }


    public void stopServices(String activeProjectId){
        sp = context.getSharedPreferences("login", MODE_PRIVATE);
        String ProjectInService0=sp.getString("case0","");
        String ProjectInService1=sp.getString("case1","");
        String ProjectInService2=sp.getString("case2","");
        if (ProjectInService0.equals(ProjectId)){
            Intent serviceIntent = new Intent(context, SensorService.class);
            context.stopService(serviceIntent);
            updateActiveProjectStatus(activeProjectId);


        }
        if (ProjectInService1.equals(ProjectId)){
            Intent serviceIntent2 = new Intent(context, SensorServiceSecondProject.class);
            context.stopService(serviceIntent2);
            updateActiveProjectStatus(activeProjectId);


        }
        if (ProjectInService2.equals(ProjectId)){
            Intent serviceIntent3 = new Intent(context, SensorServiceThirdProject.class);
            context.stopService(serviceIntent3);
            updateActiveProjectStatus(activeProjectId);


        }


    }

    //  toast maker
    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
    public void addToActiveProjects(final String SensorList, final String userId, final String ProjectId) {
        Log.d("TAAAG", "addToActiveProjects: ");
        String ip = context.getResources().getString(R.string.IP);
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
                                Toast.makeText(context, message+ActiveProjectID, Toast.LENGTH_SHORT).show();

                            } else if (mStatusCode == 201) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                            } else if (mStatusCode == 400||mStatusCode==401) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "adding active projects error" + e.toString(), Toast.LENGTH_SHORT).show();


                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "adding active projects  error" + error.toString(), Toast.LENGTH_SHORT).show();


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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);

    }

public void updateActiveProjectStatus(final String activeProjectId){
    String ip = context.getResources().getString(R.string.IP);
    String url_addToActiveProjects = ip+"api/updateActiveProjectsStatus";

    // setting the volley request and listener

    StringRequest request = new StringRequest(Request.Method.POST, url_addToActiveProjects,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        String message = responseObject.getString("message");
                        if (mStatusCode1 == 200) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        } else if (mStatusCode1 == 201) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        } else if (mStatusCode1 == 400||mStatusCode1==401) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "adding active projects error" + e.toString(), Toast.LENGTH_SHORT).show();


                    }
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(context, "adding active projects  error" + error.toString(), Toast.LENGTH_SHORT).show();


        }
    }) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            params.put("activeProjectId",activeProjectId);
            return params;
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            mStatusCode1 = response.statusCode;
            return super.parseNetworkResponse(response);
        }
    };
    RequestQueue requestQueue = Volley.newRequestQueue(context);
    requestQueue.add(request);
}

}
