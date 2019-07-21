package com.das.tirtha.sensordatacollector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class signUp extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView mTitle;
    private EditText fullname,username,email,password,confirmpasswprd;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button register;
    private ProgressBar progressBar;
    private static String url_register = "http://192.168.0.22:3000/api/addUser";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        /*
        ** Bind the views
        */
        toolbar=findViewById(R.id.toolbar);
        mTitle =  toolbar.findViewById(R.id.toolbar_title);
        fullname=findViewById(R.id.FullName);
        username=findViewById(R.id.Username);
        email=findViewById(R.id.Email);
        password=findViewById(R.id.Password);
        confirmpasswprd=findViewById(R.id.ConfirmPassword);
        radioGroup = findViewById(R.id.Radio);
        register=findViewById(R.id.Register);
        progressBar=findViewById(R.id.progressBar);



        /*
         ** set up toolbar
         */
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*
         ** set up click listener
         */

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButton =  findViewById(selectedId);

//                Toast.makeText(signUp.this,
//                        radioButton.getText(), Toast.LENGTH_SHORT).show();
                final String gender=radioButton.getText().toString();
                addNewUSer(gender);

        }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void addNewUSer(final String gender){
        // hiding the button and showing the progress bar
        progressBar.setVisibility(View.VISIBLE);
        register.setVisibility(View.GONE);

        // getting the values
        final String fullname= this.fullname.getText().toString().trim();
        final String username= this.username.getText().toString().trim();
        final String email= this.email.getText().toString().trim();
        final String password= this.password.getText().toString().trim();
        final String confirmPassword= this.confirmpasswprd.getText().toString().trim();

        // setting the volley request and listener

        StringRequest request= new StringRequest(Request.Method.POST, url_register,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject=new JSONObject(response);
                            String message=responseObject.getString("message");
                            if(message.equals("1")){
                                String UserID=responseObject.getString("UserID");
                                Toast.makeText(signUp.this,"successfully added user"+UserID,Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(signUp.this,ProjectList.class);
                                intent.putExtra("UserName",username);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(signUp.this,"sign up error",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                register.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(signUp.this,"sign up error"+e.toString(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            register.setVisibility(View.VISIBLE);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(signUp.this,"sign up error"+error.toString(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("Fullname",fullname);
                params.put("Username",username);
                params.put("Email",email);
                params.put("Password",password);
                params.put("Gender",gender);

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);



    }

}
