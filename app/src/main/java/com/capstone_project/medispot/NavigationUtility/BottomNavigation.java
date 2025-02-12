package com.capstone_project.medispot.NavigationUtility;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.capstone_project.medispot.R;
import com.capstone_project.medispot.RegisteredUser.RegisteredUserHomePage;
import com.capstone_project.medispot.RegisteredUser.SaveDrugPage;
import com.capstone_project.medispot.RegisteredUser.SearchHistory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigation {

    public static void setupBottomNavigation(Activity activity, BottomNavigationView bottomNavigationView, int selectedItemId) {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleNavigation(activity, item);
            return true;
        });

        // Check if the selectedItemId exists in the menu
        if (bottomNavigationView.getMenu().findItem(selectedItemId) != null) {
            bottomNavigationView.setSelectedItemId(selectedItemId);
        } else {
            // Remove selection from all items
            bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
            for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
                bottomNavigationView.getMenu().getItem(i).setChecked(false);
            }
            bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
        }
    }

    private static void handleNavigation(Activity activity, MenuItem item) {
        Class<?> targetActivity = null;

        switch (item.getItemId()) {
            case R.id.home:
                targetActivity = RegisteredUserHomePage.class;
                break;
            case R.id.saveDrugs:
                targetActivity = SaveDrugPage.class;
                break;
            case R.id.history:
                targetActivity = SearchHistory.class;
                break;
            default:
                return; // Do nothing if the item is not recognized
        }

        if (targetActivity != null && activity.getClass() != targetActivity) {
            Intent intent = new Intent(activity, targetActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(intent);
        }
    }
}