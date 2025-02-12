package com.capstone_project.medispot.CommonFiles.LoginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone_project.medispot.R;

public class PasswordUpdated extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_password_updated);
    }

    public void login(View view) {
        startActivity(new Intent(this, Login.class));
        finish();
    }
}