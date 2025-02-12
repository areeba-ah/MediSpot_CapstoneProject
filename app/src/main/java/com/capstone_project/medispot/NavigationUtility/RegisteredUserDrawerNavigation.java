package com.capstone_project.medispot.NavigationUtility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.capstone_project.medispot.CommonFiles.LoginSignup.Login;
import com.capstone_project.medispot.R;
import com.capstone_project.medispot.RegisteredUser.AddDrugs;
import com.capstone_project.medispot.RegisteredUser.MyAccount;
import com.capstone_project.medispot.RegisteredUser.PillReminderPage;
import com.capstone_project.medispot.RegisteredUser.RegisteredUserAboutUs;
import com.capstone_project.medispot.RegisteredUser.RegisteredUserHomePage;
import com.capstone_project.medispot.RegisteredUser.RegisteredUserReportHelp;
import com.capstone_project.medispot.RegisteredUser.RegisteredUser_DrugIndex;
import com.google.android.material.navigation.NavigationView;

public class RegisteredUserDrawerNavigation {

    public static void setupNavigationDrawer(Activity activity, DrawerLayout drawerLayout, NavigationView navigationView, View menuIcon, View contentView, View blurView, int selectedItemId) {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(item -> handleNavigation(activity, item));

        navigationView.setCheckedItem(selectedItemId);

        menuIcon.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });

        animateNavDrawer(activity, drawerLayout, contentView, blurView);
    }

    public static void animateNavDrawer(Activity activity, DrawerLayout drawerLayout, View contentView, View blurView) {
        int drawableColor = activity.getColor(R.color.Drawable_color);
        drawerLayout.setScrimColor(drawableColor);

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Show blur effect
                blurView.setVisibility(View.VISIBLE);
                blurView.setAlpha(slideOffset); // Adjust transparency

                // Apply blur effect on Android 12+ using RenderEffect
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    float blurRadius = slideOffset * 25f;
                    blurView.setRenderEffect(RenderEffect.createBlurEffect(
                            blurRadius, blurRadius, Shader.TileMode.CLAMP));
                }

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                blurView.setVisibility(View.GONE); // Hide blur
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    blurView.setRenderEffect(null); // Remove blur effect
                }
            }
        });
    }

    private static boolean handleNavigation(Activity activity, @NonNull MenuItem item) {
        Class<?> targetActivity = null;

        switch (item.getItemId()) {

            case R.id.home:
                targetActivity = RegisteredUserHomePage.class;
                break;

            case R.id.drug_index:
                targetActivity = RegisteredUser_DrugIndex.class;
                break;

            case R.id.addDrug:
                targetActivity = AddDrugs.class;
                break;
            case R.id.pillReminder:
                targetActivity = PillReminderPage.class;
                break;
            case R.id.rateUS:
                Toast.makeText(activity, "No page created!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.share:
                Toast.makeText(activity, "No page created yet!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.account:
                targetActivity = MyAccount.class;
                break;
            case R.id.logout:
                targetActivity = Login.class;
                break;
            case R.id.aboutUs:
                targetActivity = RegisteredUserAboutUs.class;
                break;
            case R.id.reportHelp:
                targetActivity = RegisteredUserReportHelp.class;
                break;
        }

        if (targetActivity != null && activity.getClass() != targetActivity) {
            Intent intent = new Intent(activity, targetActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(intent);
        }

        return true;
    }
}
