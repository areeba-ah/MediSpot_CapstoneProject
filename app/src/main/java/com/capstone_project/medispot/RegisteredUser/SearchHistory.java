
package com.capstone_project.medispot.RegisteredUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone_project.medispot.CommonFiles.savedDrugs;
import com.capstone_project.medispot.Databases.SharedUserPreferences;
import com.capstone_project.medispot.HelperClasses.searchHistoryAdapter;
import com.capstone_project.medispot.NavigationUtility.BottomNavigation;
import com.capstone_project.medispot.NavigationUtility.RegisteredUserDrawerNavigation;
import com.capstone_project.medispot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchHistory extends AppCompatActivity implements searchHistoryAdapter.onClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout contentView;
    ProgressBar progressBar;
    ImageView menuIcon;
    LinearLayout hiddenLayout;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    searchHistoryAdapter adapter;
    ArrayList<savedDrugs> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search_history);

        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);
        contentView = findViewById(R.id.mainlayout);
        hiddenLayout = findViewById(R.id.hiddenLayout);
        menuIcon = findViewById(R.id.menu);
        progressBar = findViewById(R.id.progress);

        recyclerView = findViewById(R.id.view);
        db = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<savedDrugs>();
        adapter = new searchHistoryAdapter(this,arrayList,this, this);
        recyclerView.setAdapter(adapter);

        View blurView = findViewById(R.id.blurView);

        loadHistory();

        RegisteredUserDrawerNavigation.setupNavigationDrawer(this, drawerLayout, navigationView, menuIcon, contentView, blurView, -1);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, R.id.history);


    }

    public void loadHistory() {

        recyclerView = findViewById(R.id.view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        arrayList = new ArrayList<savedDrugs>();
        adapter = new searchHistoryAdapter(this,arrayList,this, this);
        recyclerView.setAdapter(adapter);

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();

        String phone = userPhone.get(SharedUserPreferences.Phone);

        db.collection("Search_History").document(phone).collection("UserCollection")
                .orderBy("Timestamp", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot document : list) {
                                arrayList.add(document.toObject(savedDrugs.class));
                            }
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);

                        } else {
                            recyclerView.setVisibility(View.GONE);
                            hiddenLayout.setVisibility(View.VISIBLE);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                recyclerView.setVisibility(View.GONE);
                hiddenLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onClick(int position) {
        savedDrugs savedDrug = arrayList.get(position);
        Log.d("LOTTIE", "onClick: Clicked");

        Intent search = new Intent(getApplicationContext(), RegisteredUserResultPage.class);
        search.putExtra("source", "searchHistory");
        search.putExtra("history", savedDrug.getReferenceInDrugList());
        search.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(search);
    }

    @Override
    public void onDelete(int position) {

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();
        String phone = userPhone.get(SharedUserPreferences.Phone);

        savedDrugs savedDrug = arrayList.get(position);
        db.collection("Search_History").document(phone).collection("UserCollection")
                .document(savedDrug.getReferenceID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SearchHistory.this, "Removed!!", Toast.LENGTH_SHORT).show();
                        removeItem(position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LOTTIE", "Error deleting document", e);
                    }
                });

    }

    public void removeItem(int position) {
        arrayList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

}