package com.das.tirtha.sensordatacollector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

public class login extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView mTitle,createNewAccount;
    private EditText loginEmail,loginPassword;
    private ProgressBar loginProgressBar;
    private Button login;
    private static String url_register = "http://192.168.0.22:3000/api/loginUser";
    private AwesomeValidation awesomeValidation;
    final String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

            }
        });
    }

    private  void goToSignUp(){
        Intent intent = new Intent(this,signUp.class);
        startActivity(intent);
    }


    private  void signIn(){

        // show progress bar and hide login button
        loginProgressBar.setVisibility(View.VISIBLE);
        login.setVisibility(View.GONE);

        // getting the values
        final String fullname= this.loginEmail.getText().toString().trim();
        final String username= this.loginPassword.getText().toString().trim();

        // form validation
        awesomeValidation.addValidation(login.this, R.id.Email, android.util.Patterns.EMAIL_ADDRESS, R.string.email_err);
        awesomeValidation.addValidation(login.this, R.id.Password, regexPassword, R.string.password_err);

    }
}
