package com.das.tirtha.sensordatacollector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectList extends AppCompatActivity {
    private TextView ProjectList,mTitle;
    private RequestQueue mQueue;
    private static final  String TAG="TirthaTest";
    private Toolbar toolbar;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        ProjectList=findViewById(R.id.projectList);
        mQueue= Volley.newRequestQueue(this);
        Log.d(TAG, "onResponse: hello");
        toolbar=findViewById(R.id.toolbar);
        mTitle =  toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("project lists");
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                username= null;
                Toast.makeText(ProjectList.this,"no username found",Toast.LENGTH_SHORT).show();

            } else {
                username= extras.getString("UserName");
                Toast.makeText(ProjectList.this,"welcome here"+username,Toast.LENGTH_SHORT).show();

            }
        } else {
            username= (String) savedInstanceState.getSerializable("UserName");
            Toast.makeText(ProjectList.this,"welcome here"+username,Toast.LENGTH_SHORT).show();

        }
        getProjectsList();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void getProjectsList() {
//        String url= "https://jsonplaceholder.typicode.com/todos";
        String url= "http://192.168.0.22:3000/api/posts";

        JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: hhheeelooo 123");
                            JSONArray projects= response.getJSONArray("posts");
                            for(int i=0;i<projects.length();i++){
                                JSONObject projectData=projects.getJSONObject(i);
                                String projectTitle=projectData.getString("title");
                                String projectDesciption=projectData.getString("content");
//                                JSONArray sensorList=projectData.getJSONArray("sensorList");
//                                Log.d(TAG, "onResponse000:  sensor list"+sensorList);


                                Log.d(TAG, "onResponse5: "+projectTitle);
                                ProjectList.append("project title: "+ projectTitle + "\n");
                                ProjectList.append("project description: "+ projectDesciption+ "\n\n");

                            }
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
        mQueue.add(request);
    }

}
