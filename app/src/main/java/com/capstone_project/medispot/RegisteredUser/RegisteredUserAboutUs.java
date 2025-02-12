package com.capstone_project.medispot.RegisteredUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.capstone_project.medispot.NavigationUtility.BottomNavigation;
import com.capstone_project.medispot.NavigationUtility.RegisteredUserDrawerNavigation;
import com.capstone_project.medispot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class RegisteredUserAboutUs extends AppCompatActivity{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentView;
    ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registered_user_about_us);

        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);
        contentView = findViewById(R.id.layout);
        menuIcon = findViewById(R.id.menu);
        View blurView = findViewById(R.id.blurView);


        RegisteredUserDrawerNavigation.setupNavigationDrawer(this, drawerLayout, navigationView, menuIcon, contentView, blurView, R.id.aboutUs);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, -1);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

}