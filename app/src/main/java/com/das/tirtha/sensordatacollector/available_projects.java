package com.das.tirtha.sensordatacollector;

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

public class available_projects extends Fragment {
    private  RecyclerView mrecyclerView;
    private projectsAdapter mprojectsAdapter;
    private ArrayList<projectData> mprojectList;
    private RequestQueue requestQueue;
    private Toolbar toolbar;
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
        getProjectsList();
        return view;
//        return inflater.inflate(R.layout.fragment_available_projects,container,false);
    }

    private void getProjectsList() {
        String ip = getResources().getString(R.string.IP);
        String url=ip+"api/posts";

        JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: hhheeelooo");
                            JSONArray projects= response.getJSONArray("posts");
                            for(int i=0;i<projects.length();i++){
                                JSONObject projectData1=projects.getJSONObject(i);
                                String projectTitle=projectData1.getString("title");
                                String projectDesciption=projectData1.getString("content");
                                Log.d(TAG, "onResponse: "+projectTitle);
                                mprojectList.add(new projectData(projectTitle,projectDesciption));
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