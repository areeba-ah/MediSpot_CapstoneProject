package com.capstone_project.medispot.CommonFiles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone_project.medispot.CommonFiles.LoginSignup.Login;
import com.capstone_project.medispot.R;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIMER = 4000;
    SharedPreferences onBoardingPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){
            @Override
            public void run(){

                onBoardingPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
                boolean isFirstTime = onBoardingPreferences.getBoolean("firstTimeUser", true);

                if(isFirstTime){

                    SharedPreferences.Editor editor = onBoardingPreferences.edit();
                    editor.putBoolean("firstTimeUser", false);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), OnBoarding.class);
                    startActivity(intent);
                    finish();
                }

                else{
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        },SPLASH_TIMER);
    }
}