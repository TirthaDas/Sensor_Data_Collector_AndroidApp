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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class active_projects extends Fragment {
    private Button stopProject1,stopProject2;
    private RecyclerView mrecyclerView;
    private Active_Projects_Adapter mprojectsAdapter;
    private ArrayList<projectData> mprojectList;
    private RequestQueue requestQueue;
    private Toolbar toolbar;
    //    private SharedPreferences sp;
    private static final  String TAG="Project Request Test";
    private SharedPreferences sp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Active Projects");
        View view= inflater.inflate(R.layout.fragment_active_projects,container,false);

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
        requestQueue= Volley.newRequestQueue(getActivity());
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Available Projects");

        //get shared preference
        sp =  (getActivity()).getSharedPreferences("login", MODE_PRIVATE);


        getActiveProjectsList();


        return  view;
    }
    public void getActiveProjectsList(){

        String ip = getResources().getString(R.string.IP);
        String url=ip+"api/posts";

        JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int actiiveProjectCount=0;
                            Log.d(TAG, "onResponse: hhheeelooo abcd");
                            JSONArray projects= response.getJSONArray("posts");
                            label:
                            for(int i=0;i<projects.length();i++){
                                JSONObject projectData1=projects.getJSONObject(i);
                                Log.d(TAG, "Projects available "+projectData1);
                                String id=projectData1.getString("_id");
                                String projectTitle=projectData1.getString("title");
                                String projectDesciption=projectData1.getString("content");
                                ArrayList<String> listdata = new ArrayList<String>();
                                JSONArray sensorList=projectData1.getJSONArray("sensorList");
                                if (sensorList != null) {
                                    for (int k=0;k<sensorList.length();k++){
                                        listdata.add(sensorList.getString(k));
                                    }
                                }
                                Log.d(TAG, "onResponse000:  sensor list"+sensorList);
                                Log.d(TAG, "onResponse: pid"+id);


                                String userid=sp.getString("UserId", "noUsr");
                                ArrayList<String> activeUsersList = new ArrayList<String>();
                                JSONArray activeUser=projectData1.getJSONArray("activeUsers");
                                if (activeUser != null) {
                                    for (int k=0;k<activeUser.length();k++){
                                        activeUsersList.add(activeUser.getString(k));
                                    }
                                }

                                for(int a=0;a<activeUsersList.size();a++){

                                    Log.d(TAG, "------------------: "+userid+activeUsersList.get(a));

                                    if(activeUsersList.get(a).equals(userid)){
                                        Log.d(TAG, "++++++++++++++++: "+userid+activeUsersList.get(a));
                                        actiiveProjectCount++;
                                        mprojectList.add(new projectData(id,projectTitle,projectDesciption,listdata));

                                        continue label;
                                    }
                                }
                            }
                            sp = (getActivity()).getSharedPreferences("login",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putInt("active_projects",actiiveProjectCount).apply();
                            mprojectsAdapter=new Active_Projects_Adapter(getActivity(),mprojectList);
                            mrecyclerView.setAdapter(mprojectsAdapter);
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: 3");

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(request);
    }



}
