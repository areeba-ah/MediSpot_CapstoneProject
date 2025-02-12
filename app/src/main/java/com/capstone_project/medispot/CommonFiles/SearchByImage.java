package com.capstone_project.medispot.CommonFiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone_project.medispot.NavigationUtility.BottomNavigation;
import com.capstone_project.medispot.R;
import com.capstone_project.medispot.RegisteredUser.RegisteredUserHomePage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

public class SearchByImage extends AppCompatActivity{
    ImageView drugImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search_by_image);

        drugImage = findViewById(R.id.image);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, -1);

        Intent intent = getIntent();
        String URI =  intent.getStringExtra("uri");

        Uri uri = Uri.parse(URI);
        Picasso.get().load(uri).into(drugImage);

    }



    public void cancel(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void search(View view) {

    }


}