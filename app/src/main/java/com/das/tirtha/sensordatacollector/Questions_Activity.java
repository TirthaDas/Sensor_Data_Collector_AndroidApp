package com.das.tirtha.sensordatacollector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Questions_Activity extends AppCompatActivity {
    private String ProjectId;
    private Toolbar toolbar;
    private TextView mTitle;
    private Button saveAnswer;
    private SharedPreferences sp;
    private RequestQueue requestQueue;
    private RecyclerView mrecyclerView;
    private Questions_Adapter mQuestionsAdapter;
    private ArrayList<Questions_Data> mQuestionsList;
    private final static String TAG = "Questions message";
    private int mStatusCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_);
        Bundle extras = getIntent().getExtras();
        //bind ids to components
        mrecyclerView = findViewById(R.id.recyclerViewQuestionsList);
        saveAnswer = findViewById(R.id.save_answers);
        toolbar = findViewById(R.id.questions_page_toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Project Questions");
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mQuestionsList = new ArrayList<>();
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mrecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mQuestionsAdapter = new Questions_Adapter(this, mQuestionsList);
        mrecyclerView.setAdapter(mQuestionsAdapter);
//        ArrayList<String> sensorList = getSensorList();
        //get extras
        ProjectId = extras.getString("ProjectId");
        sp = getSharedPreferences("login", MODE_PRIVATE);
        final String UserId = sp.getString("UserId", "");

        // get the questions and answers
        getQuestionsAndAnswers(ProjectId, UserId);

        saveAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAnswers(UserId);
//                Intent ActiveProjectsIntent = new Intent(Questions_Activity.this, active_projects.class);
//                startActivity(ActiveProjectsIntent);
            }
        });
    }


    public void getQuestionsAndAnswers(final String ProjectId, final String UserId) {
        String ip = getResources().getString(R.string.IP);
        String url = ip + "api/getQestions";

        Map<String, String> params = new HashMap<>();
        params.put("userId", UserId);
        params.put("projectId", ProjectId);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            if (response.has("questions")) {
                                JSONArray questions = response.getJSONArray("questions");
                                for (int i = 0; i < questions.length(); i++) {

                                    JSONObject SingleQuestion = questions.getJSONObject(i);
                                    String QuestionId = SingleQuestion.getString("_id");
                                    String QuestionText = SingleQuestion.getString("question");
                                    Log.d(TAG, "onResponse000:  QuestionText" + QuestionText);
                                    Log.d(TAG, "onResponse: Question ID" + QuestionId);
                                    mQuestionsList.add(new Questions_Data(QuestionId, QuestionText, ProjectId, UserId, ""));

                                }
                                mQuestionsAdapter = new Questions_Adapter(Questions_Activity.this, mQuestionsList);
                                mrecyclerView.setAdapter(mQuestionsAdapter);

                            } else if (response.has("answers")) {
                                JSONArray questions = response.getJSONArray("answers");

                                for (int i = 0; i < questions.length(); i++) {
                                    JSONObject SingleQuestion = questions.getJSONObject(i);
                                    String answer = SingleQuestion.getString("answer");
                                    JSONObject PopulatedQuestion = SingleQuestion.getJSONObject("questionId");
                                    String QuestionId = PopulatedQuestion.getString("_id");
                                    String QuestionText = PopulatedQuestion.getString("question");
                                    Log.d(TAG, "onResponse111:  QuestionText" + QuestionText);
                                    Log.d(TAG, "onResponse111: Question ID" + QuestionId);
                                    mQuestionsList.add(new Questions_Data(QuestionId, QuestionText, ProjectId, UserId, answer));
                                }
                                mQuestionsAdapter = new Questions_Adapter(Questions_Activity.this, mQuestionsList);
                                mrecyclerView.setAdapter(mQuestionsAdapter);
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
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Questions_Activity.this);
        requestQueue.add(request);

    }

    public void saveAnswers(final String UserId) {

            Thread saveanswer = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: ********");
                    saveAnswerToServer(UserId);
                }
            });
            saveanswer.start();
        onBackPressed();


    }


    public void saveAnswerToServer(final String UserId) {
        String ip = getResources().getString(R.string.IP);
        String url_get = ip + "api/saveAnswers";
        Gson gson = new Gson();
        final String answerArray= gson.toJson(mQuestionsList) ;
        StringRequest request = new StringRequest(Request.Method.POST, url_get,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject=new JSONObject(response);
                            String message=responseObject.getString("message");
                                if(mStatusCode==200){
                                    Toast.makeText(Questions_Activity.this, message, Toast.LENGTH_SHORT).show();
                                }
                               else if(mStatusCode==400){
                                    Toast.makeText(Questions_Activity.this, message, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(Questions_Activity.this, "Error saving answer", Toast.LENGTH_SHORT).show();

                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Questions_Activity.this,"login error"+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserId", UserId);
                params.put("ProjectId", ProjectId);
                params.put("Answers", answerArray);


                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(Questions_Activity.this);
        requestQueue.add(request);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}