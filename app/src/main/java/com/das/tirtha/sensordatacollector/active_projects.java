package com.das.tirtha.sensordatacollector;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class active_projects extends Fragment {
    private Button stopProject1, stopProject2;
    private RecyclerView mrecyclerView;
    private Active_Projects_Adapter mprojectsAdapter;
    private ArrayList<Active_Project_Data> mprojectList;
    private RequestQueue requestQueue;
    private Toolbar toolbar;
    //    private SharedPreferences sp;
    private static final String TAG = "Project Request Test";
    private SharedPreferences sp;
    private int mStatusCode;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Active Projects");
        View view = inflater.inflate(R.layout.fragment_active_projects, container, false);


//        // bind views
//        stopProject1=view.findViewById(R.id.stop_project_1);
//        stopProject2=view.findViewById(R.id.stop_project_2);
//
//
//        // onclick listeners
//        stopProject1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ServiceHelper serviceHelper2=new ServiceHelper("5d4b33931a55de05b5352ddd",(getActivity()));
//                serviceHelper2.stopServices();
//
//            }
//        });
//
//        stopProject2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ServiceHelper serviceHelper2=new ServiceHelper("5d4c41221a55de05b5352dde",(getActivity()));
//                serviceHelper2.stopServices();
//            }
//        });

        mrecyclerView = view.findViewById(R.id.recyclerViewActiveProjectList);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mprojectList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity());
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Active Projects");

        //get shared preference
        sp = (getActivity()).getSharedPreferences("login", MODE_PRIVATE);
        String UserId=sp.getString("UserId","noUser");



//        getActiveProjectsList(UserId);
getVl(UserId);

        return view;
    }

//    public void getActiveProjectsList(final String UserId) {
//
//        String ip = getResources().getString(R.string.IP);
//        String url = ip + "api/getAllActiveProjects";
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            int actiiveProjectCount = 0;
//                            JSONArray activeProjects = response.getJSONArray("activeProjects");
//                            Log.d(TAG, "onResponse: hhheeelooo abcd"+activeProjects.length());
//
//                            for (int i = 0; i < activeProjects.length(); i++) {
//                                JSONObject projectData1 = activeProjects.getJSONObject(i);
//                                Log.d(TAG, "Projects available " + projectData1);
//                                String id = projectData1.getString("_id");
//                                String projectTitle = projectData1.getString("title");
//                                String projectDesciption = projectData1.getString("content");
//                                String SensorList = projectData1.getString("sensorList");
//                                String UserID = projectData1.getString("userId");
//                                String ProjectId = projectData1.getString("projectId");
//                                Log.d(TAG, "onResponse: ******************************id"+id);
//                                Log.d(TAG, "onResponse: ******************************projectTitle"+projectTitle);
//                                Log.d(TAG, "onResponse: ******************************projectDesciption"+projectDesciption);
//                                Log.d(TAG, "onResponse: ******************************SensorList"+SensorList);
//                                Log.d(TAG, "onResponse: ******************************UserID"+UserID);
//                                Log.d(TAG, "onResponse: ******************************ProjectId"+ProjectId);
//                                mprojectList.add(new Active_Project_Data(id, projectTitle, projectDesciption, SensorList,UserID,ProjectId));
//                            }
//
//                            mprojectsAdapter = new Active_Projects_Adapter(getActivity(), mprojectList);
//                            mrecyclerView.setAdapter(mprojectsAdapter);
//                        } catch (JSONException e) {
//                            Log.d(TAG, "onResponse: 3");
//
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params=new HashMap<>();
//                params.put("userId", "99999");
//                return params;
//            }
//            @Override
//            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                mStatusCode = response.statusCode;
//                return super.parseNetworkResponse(response);
//            }
//        };
//
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        requestQueue.add(request);
//
//    }

    public void getVl(final String UserId){
        String ip = getResources().getString(R.string.IP);
        String url_get = ip + "api/getAllActiveProjects";
        StringRequest request= new StringRequest(Request.Method.POST, url_get,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject=new JSONObject(response);
                            String message=responseObject.getString("message");
                            JSONArray activeProjects=responseObject.getJSONArray("activeProjects");
//                                int Status=responseObject.getInt(status)
                            Log.d(TAG, "onResponse: *********&&&&&&"+activeProjects.length());
                            if(mStatusCode==200){

                                Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                                for(int a=0;a<activeProjects.length();a++){
                                    JSONObject singleActiveProject = activeProjects.getJSONObject(a);
                                    String id = singleActiveProject.getString("_id");
                                    String projectTitle = singleActiveProject.getString("title");
                                    String projectDesciption = singleActiveProject.getString("content");
                                    String SensorList = singleActiveProject.getString("sensorList");
                                    String UserID = singleActiveProject.getString("userId");
                                    String ProjectId = singleActiveProject.getString("projectId");
                                    boolean isCurrentlyActive=singleActiveProject.getBoolean("isCurrentlyActive");
                                    Log.d(TAG, "onResponse: ******************************id"+id);
                                    Log.d(TAG, "onResponse: ******************************projectTitle"+projectTitle);
                                    Log.d(TAG, "onResponse: ******************************projectDesciption"+projectDesciption);
                                    Log.d(TAG, "onResponse: ******************************SensorList"+SensorList);
                                    Log.d(TAG, "onResponse: ******************************UserID"+UserID);
                                    Log.d(TAG, "onResponse: ******************************ProjectId"+ProjectId);
                                    mprojectList.add(new Active_Project_Data(id, projectTitle, projectDesciption, SensorList,UserID,ProjectId,isCurrentlyActive));


                                }

                                mprojectsAdapter = new Active_Projects_Adapter(getActivity(), mprojectList);
                                mrecyclerView.setAdapter(mprojectsAdapter);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(),"login error"+e.toString(),Toast.LENGTH_SHORT).show();


                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"login error"+error.toString(),Toast.LENGTH_SHORT).show();


            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("userId",UserId);
                return params;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);



    }


}
