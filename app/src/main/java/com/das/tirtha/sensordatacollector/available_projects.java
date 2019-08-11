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

public class available_projects extends Fragment {
    private  RecyclerView mrecyclerView;
    private projectsAdapter mprojectsAdapter;
    private ArrayList<projectData> mprojectList;
    private RequestQueue requestQueue;
    private Toolbar toolbar;
    private SharedPreferences sp;
    private static final  String TAG="Project Request Test";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_projects,container,false);
        mrecyclerView = view.findViewById(R.id.recyclerViewProjectList);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mprojectList = new ArrayList<>();
        requestQueue= Volley.newRequestQueue(getActivity());
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Available Projects");
//        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        sp =  (getActivity()).getSharedPreferences("login", MODE_PRIVATE);

        getProjectsList();

        return view;
//        return inflater.inflate(R.layout.fragment_available_projects,container,false);
    }

    private void getProjectsList() {
        String ip = getResources().getString(R.string.IP);
        String url=ip+"api/posts";
        String msg="hello sir";

        JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
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
                                        continue label;
                                    }
                                }
                                mprojectList.add(new projectData(id,projectTitle,projectDesciption,listdata));
                            }
                            mprojectsAdapter=new projectsAdapter(getActivity(),mprojectList);
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
