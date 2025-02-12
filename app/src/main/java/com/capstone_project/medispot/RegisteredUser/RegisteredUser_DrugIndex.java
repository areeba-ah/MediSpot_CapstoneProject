package com.capstone_project.medispot.RegisteredUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone_project.medispot.CommonFiles.drugs;
import com.capstone_project.medispot.HelperClasses.drugListAdapter;
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
import java.util.List;

public class RegisteredUser_DrugIndex extends AppCompatActivity implements drugListAdapter.onClickListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout contentView;
    ProgressBar progressBar;
    ImageView menuIcon;
    TextView textView;

    RecyclerView recyclerView;
    FirebaseFirestore db;
    drugListAdapter adapter;
    ArrayList<drugs> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registered_user_drug_index);

        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);
        textView = findViewById(R.id.textView);
        contentView = findViewById(R.id.mainlayout);
        menuIcon = findViewById(R.id.menu);
        progressBar = findViewById(R.id.progress);

        recyclerView = findViewById(R.id.view);
        db = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<drugs>();
        adapter = new drugListAdapter(this,arrayList,this);
        recyclerView.setAdapter(adapter);

        View blurView = findViewById(R.id.blurView);


        RegisteredUserDrawerNavigation.setupNavigationDrawer(this, drawerLayout, navigationView, menuIcon, contentView, blurView, R.id.drug_index);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, -1);

        Intent search = getIntent();
        String from = search.getStringExtra("source");

        fromDrugIndex();
    }

    private void fromDrugIndex(){
        db.collection("Drug_List").orderBy("DrugName", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot document : list) {
                                arrayList.add(document.toObject(drugs.class));
                            }
                            adapter.notifyDataSetChanged();
                        } else {

                            Toast.makeText(RegisteredUser_DrugIndex.this, "No data found in Database", Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(RegisteredUser_DrugIndex.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
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
        drugs drug = arrayList.get(position);
        Log.d("LOTTIE", "onClick: Clicked");

        arrayList.get(position);
        Intent intent = new Intent(getApplicationContext(), RegisteredUserDrugInfo.class);
        intent.putExtra("referenceID",drug.getDocument_ReferenceID());
        intent.putExtra("drugName",drug.getDrugName());
        intent.putExtra("brandName",drug.getBrandName());
        intent.putExtra("genericName",drug.getGenericName());
        intent.putExtra("description",drug.getDescription());
        intent.putExtra("directions",drug.getDirections());
        intent.putExtra("dosage",drug.getDosage());
        intent.putExtra("precautions",drug.getPrecautions());
        intent.putExtra("sideEffects",drug.getSideEffects());
        intent.putExtra("warnings",drug.getWarnings());
        intent.putExtra("frontImage",drug.getFrontImage());
        intent.putExtra("backImage",drug.getBackImage());
        intent.putExtra("pilImage",drug.getPilImage());
        startActivity(intent);
    }
}