package com.das.tirtha.sensordatacollector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.util.Log;
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

public class ProjectList extends AppCompatActivity {
    private TextView ProjectList;
    private RequestQueue mQueue;
    private static final  String TAG="TirthaTest";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        ProjectList=findViewById(R.id.projectList);
        mQueue= Volley.newRequestQueue(this);
        Log.d(TAG, "onResponse: hello");
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("project lists");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                            Log.d(TAG, "onResponse: hhheeelooo");
                            JSONArray projects= response.getJSONArray("posts");
                            for(int i=0;i<projects.length();i++){
                                JSONObject projectData=projects.getJSONObject(i);
                                String projectTitle=projectData.getString("title");
                                String projectDesciption=projectData.getString("content");
                                Log.d(TAG, "onResponse: "+projectTitle);
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
