package com.das.tirtha.sensordatacollector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView mTitle,createNewAccount;
    private EditText loginEmail,loginPassword;
    private ProgressBar loginProgressBar;
    private Button login;
    private SharedPreferences sp;
    private AwesomeValidation awesomeValidation;
    private int mStatusCode;


    final String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        sp = getSharedPreferences("login",MODE_PRIVATE);

        if(sp.getBoolean("logged",false)){
            final String user=sp.getString("UserName","");
            gotToMainActivity(user);
        }

        // bind components
        toolbar=findViewById(R.id.toolbar);
        mTitle =  toolbar.findViewById(R.id.toolbar_title);
        createNewAccount=findViewById(R.id.goToSignUp);
        loginEmail=findViewById(R.id.loginEmail);
        loginPassword=findViewById(R.id.loginPassword);
        loginProgressBar=findViewById(R.id.loginProgressBar);
        login=findViewById(R.id.login);
        awesomeValidation=new AwesomeValidation(ValidationStyle.BASIC);




        // set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign In");
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);




        // set up click listeners

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private  void goToSignUp(){
        Intent intent = new Intent(this,signUp.class);
        startActivity(intent);
    }


    private  void signIn(){
        String  ip = getResources().getString(R.string.IP);
        String url_login = ip+"api/loginUser";
        // show progress bar and hide login button
        loginProgressBar.setVisibility(View.VISIBLE);
        login.setVisibility(View.GONE);

        // getting the values
        final String loginEmail= this.loginEmail.getText().toString().trim();
        final String loginPassword= this.loginPassword.getText().toString().trim();

        // form validation
        awesomeValidation.addValidation(login.this, R.id.loginEmail, android.util.Patterns.EMAIL_ADDRESS, R.string.email_err);
        awesomeValidation.addValidation(login.this, R.id.loginPassword, regexPassword, R.string.password_err);


        // login functionality

        if(awesomeValidation.validate()){

            // setting the volley request and listener

            StringRequest request= new StringRequest(Request.Method.POST, url_login,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject responseObject=new JSONObject(response);
                                String message=responseObject.getString("message");
//                                int Status=responseObject.getInt(status)
                                if(mStatusCode==200){
                                    String UserID=responseObject.getString("UserID");
                                    String UserName=responseObject.getString("UserName");
                                    Toast.makeText(login.this,message,Toast.LENGTH_SHORT).show();
                                    sp.edit().putBoolean("logged",true).apply();
                                    sp.edit().putString("UserName",UserName).apply();
                                    gotToMainActivity(UserName);
                                }
                                else if(mStatusCode==201 || mStatusCode==202 ) {
                                    Toast.makeText(login.this,message,Toast.LENGTH_SHORT).show();
                                    loginProgressBar.setVisibility(View.GONE);
                                    login.setVisibility(View.VISIBLE);
                                }
                                else if(mStatusCode==400) {
                                    Toast.makeText(login.this,message,Toast.LENGTH_SHORT).show();
                                    loginProgressBar.setVisibility(View.GONE);
                                    login.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(login.this,"login error"+e.toString(),Toast.LENGTH_SHORT).show();
                                loginProgressBar.setVisibility(View.GONE);
                                login.setVisibility(View.VISIBLE);

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(login.this,"login error"+error.toString(),Toast.LENGTH_SHORT).show();
                    loginProgressBar.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params=new HashMap<>();
                    params.put("loginEmail",loginEmail);
                    params.put("loginPassword",loginPassword);
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
        else {
            loginProgressBar.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }


    }

    public void gotToMainActivity( final  String UserName){
        Intent intent = new Intent(login.this,MainActivity.class);
        intent.putExtra("UserName",UserName);
        startActivity(intent);
    }
}
