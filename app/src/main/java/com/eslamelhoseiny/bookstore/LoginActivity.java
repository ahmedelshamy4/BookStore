package com.eslamelhoseiny.bookstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eslamelhoseiny.bookstore.util.ActivityLauncher;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etEmail,etPassword;
    Button butnLogin,btnRegister;
    TextView tvForgetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        intiViews();
    }

    private void intiViews() {
        etEmail=(EditText)findViewById(R.id.et_email);
        etPassword=(EditText)findViewById(R.id.et_password);
        butnLogin=(Button)findViewById(R.id.btn_login);
        btnRegister=(Button)findViewById(R.id.btn_register);
        tvForgetPassword=(TextView)findViewById(R.id.tv_forget_password);
        butnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                break;
            case R.id.btn_register:
                ActivityLauncher.openRegistrationActivity(this);
                break;
            case R.id.tv_forget_password:
                break;
        }
    }
}
