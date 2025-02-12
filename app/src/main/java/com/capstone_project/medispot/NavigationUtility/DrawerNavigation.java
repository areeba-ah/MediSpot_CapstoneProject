package com.capstone_project.medispot.NavigationUtility;

import static com.capstone_project.medispot.NavigationUtility.RegisteredUserDrawerNavigation.animateNavDrawer;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.capstone_project.medispot.CommonFiles.LoginSignup.Login;
import com.capstone_project.medispot.R;
import com.capstone_project.medispot.User.AboutUs;
import com.capstone_project.medispot.User.DrugList;
import com.capstone_project.medispot.User.HomeScreen;
import com.capstone_project.medispot.User.ReportHelp;
import com.google.android.material.navigation.NavigationView;

public class DrawerNavigation {

    public static void setupNavigationDrawer(Activity activity, DrawerLayout drawerLayout, NavigationView navigationView, View menuIcon, View contentView, View blurView, int selectedItemId) {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(item -> {
            boolean handled = handleNavigation(activity, item, navigationView);
            return handled;
        });

        // Clear previous selection
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }

        // Set the new selected item
        if (selectedItemId != -1 && navigationView.getMenu().findItem(selectedItemId) != null) {
            navigationView.getMenu().findItem(selectedItemId).setChecked(true);
        }

        menuIcon.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });

        animateNavDrawer(activity, drawerLayout, contentView, blurView);
    }

    private static boolean handleNavigation(Activity activity, @NonNull MenuItem item, NavigationView navigationView) {
        Class<?> targetActivity = null;
        int selectedItemId = -1; // Default no selection

        switch (item.getItemId()) {
            case R.id.home:
                targetActivity = HomeScreen.class;
                selectedItemId = R.id.home;
                break;
            case R.id.drug_index:
                targetActivity = DrugList.class;
                selectedItemId = R.id.drug_index;
                break;
            case R.id.rateUS:
                Toast.makeText(activity, "No page created!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.share:
                Toast.makeText(activity, "No page created yet!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.login:
                targetActivity = Login.class;
                selectedItemId = R.id.login;
                break;
            case R.id.aboutUs:
                targetActivity = AboutUs.class;
                selectedItemId = R.id.aboutUs;
                break;
            case R.id.reportHelp:
                targetActivity = ReportHelp.class;
                selectedItemId = R.id.reportHelp;
                break;
        }

        if (targetActivity != null) {
            if (activity.getClass() == targetActivity) {
                return true; // Do nothing if already in the correct activity
            }

            Intent intent = new Intent(activity, targetActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("selectedItemId", selectedItemId); // Pass selected item ID
            activity.startActivity(intent);
            activity.finish(); // Ensure proper refresh
        }

        return true;
    }
}