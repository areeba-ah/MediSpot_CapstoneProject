package com.capstone_project.medispot.CommonFiles;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.capstone_project.medispot.CommonFiles.LoginSignup.Login;
import com.capstone_project.medispot.HelperClasses.SliderAdapter;
import com.capstone_project.medispot.R;


public class OnBoarding extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button letsGetStarted;
    Button skipbtn;
    Button nextbtn;
    Button backbtn;
    Animation animation;
    int current_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_boarding);

        //hooks
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        letsGetStarted = findViewById(R.id.get_started_btn);
        skipbtn = findViewById(R.id.skip_btn);
        nextbtn = findViewById(R.id.next_btn);
        backbtn = findViewById(R.id.back_btn);

        //Call adapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }

    public void skip_btn(View view){
        startActivity(new Intent(this, Login.class)); finish();}

    public void back_btn(View view){viewPager.setCurrentItem(current_pos -1);}

    public void next_btn(View view){ viewPager.setCurrentItem(current_pos + 1);}

    public void lets_get_started(View view){
        startActivity(new Intent(this, Login.class)); finish();


    }



    private void addDots(int position){
        dots = new TextView[8];
        dotsLayout.removeAllViews();
        current_pos = position;

        for (int i=0; i<dots.length; i++){

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);

            if(current_pos == 0){ backbtn.setVisibility(View.INVISIBLE);}
            else {backbtn.setVisibility(View.VISIBLE);}

        } if(dots.length >0){ dots[position].setTextColor(getResources().getColor(R.color.GoldenRod));}

    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
           addDots(position);

           if(position != dots.length-1){
               letsGetStarted.setVisibility(View.INVISIBLE);
               skipbtn.setVisibility(View.VISIBLE);
               nextbtn.setVisibility(View.VISIBLE);
           }

           else {
               animation = AnimationUtils.loadAnimation(OnBoarding.this, R.anim.lets_get_started_anim);
               letsGetStarted.setAnimation(animation);
               letsGetStarted.setVisibility(View.VISIBLE);
               skipbtn.setVisibility(View.INVISIBLE);
               nextbtn.setVisibility(View.INVISIBLE);
           }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}