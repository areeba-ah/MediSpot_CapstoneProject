package com.capstone_project.medispot.RegisteredUser;

import static com.airbnb.lottie.L.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.capstone_project.medispot.HelperClasses.savedListAdapter;
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

public class SaveDrugPage extends AppCompatActivity implements savedListAdapter.onClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout contentView;
    RecyclerView recyclerView;

    ProgressBar progressBar;

    View layout;
    ImageView menuIcon;
    FirebaseFirestore db;

    savedListAdapter adapter;
    ArrayList<savedDrugs> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_save_drug_page);

        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);
        contentView = findViewById(R.id.mainlayout);
        contentView = findViewById(R.id.mainlayout);
        progressBar = findViewById(R.id.progress);

        layout = findViewById(R.id.layout);
        menuIcon = findViewById(R.id.menu);
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        arrayList = new ArrayList<savedDrugs>();
        adapter = new savedListAdapter(this,arrayList,this, this);
        recyclerView.setAdapter(adapter);

        View blurView = findViewById(R.id.blurView);

        RegisteredUserDrawerNavigation.setupNavigationDrawer(this, drawerLayout, navigationView, menuIcon, contentView, blurView, -1);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, R.id.saveDrugs);

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();

        String phone = userPhone.get(SharedUserPreferences.Phone);

        db.collection("Saved_Drugs").document(phone).collection("UserCollection")
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
                        } else {
                            layout.setVisibility(View.VISIBLE);
                        }

                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                layout.setVisibility(View.VISIBLE);

            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onClick(int position) {
        savedDrugs savedDrug = arrayList.get(position);
        Log.d("LOTTIE", "onClick: Clicked");

        arrayList.get(position);
        Intent search = new Intent(getApplicationContext(), RegisteredUserResultPage.class);
        search.putExtra("source", "SavedDrug");
        search.putExtra("saved", savedDrug.getReferenceInDrugList());
        search.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(search);
    }

    @Override
    public void onDelete(int position) {
        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();
        String phone = userPhone.get(SharedUserPreferences.Phone);

        savedDrugs savedDrug = arrayList.get(position);
        db.collection("Saved_Drugs").document(phone).collection("UserCollection")
        .document(savedDrug.getReferenceID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SaveDrugPage.this, "Removed from Saved Drugs!", Toast.LENGTH_SHORT).show();
                        removeItem(position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void removeItem(int position) {
        arrayList.remove(position);
        adapter.notifyItemRemoved(position);

        if(arrayList.isEmpty()){
            layout.setVisibility(View.VISIBLE);
        }
    }
}