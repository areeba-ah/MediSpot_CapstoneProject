package com.capstone_project.medispot.RegisteredUser;

import static com.airbnb.lottie.L.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone_project.medispot.CommonFiles.PillReminder;
import com.capstone_project.medispot.Databases.SharedUserPreferences;
import com.capstone_project.medispot.HelperClasses.AlarmReceiver;
import com.capstone_project.medispot.HelperClasses.PillReminderAdapter;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PillReminderPage extends AppCompatActivity implements PillReminderAdapter.onClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout contentView;
    ImageView menuIcon;
    RecyclerView recyclerView;
    View layout;
    FirebaseFirestore db;
    StorageReference storageReference;
    PillReminderAdapter adapter;
    ArrayList<PillReminder> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pill_reminder_page);

        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);
        contentView = findViewById(R.id.mainlayout);
        menuIcon = findViewById(R.id.menu);

        layout = findViewById(R.id.layout);
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<PillReminder>();
        adapter = new PillReminderAdapter(this,arrayList,this);
        recyclerView.setAdapter(adapter);

        View blurView = findViewById(R.id.blurView);


        RegisteredUserDrawerNavigation.setupNavigationDrawer(this, drawerLayout, navigationView, menuIcon, contentView, blurView, R.id.pillReminder);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, -1);

        load();

    }

    private void load(){
        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();

        String phone = userPhone.get(SharedUserPreferences.Phone);

        db.collection("Drug_Reminders").document(phone).collection("UserCollection")
                .orderBy("Timestamp", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot document : list) {
                                arrayList.add(document.toObject(PillReminder.class));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            layout.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                layout.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onDelete(int position) {

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();
        String phone = userPhone.get(SharedUserPreferences.Phone);

        PillReminder reminder = arrayList.get(position);
        deleteAlarm(position);

        db.collection("Drug_Reminders").document(phone).collection("UserCollection")
                .document(reminder.getReferenceID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteFromStorage(position, reminder.getDrugImgName());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting Reminder", e);
                    }
                });
    }

    public void deleteAlarm(int position){

        PillReminder reminder = arrayList.get(position);
        int requestCode = Integer.parseInt(reminder.getDrugImgName());
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,requestCode,intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(), "Reminder deleted!!", Toast.LENGTH_SHORT).show();
    }

    public void deleteFromStorage(int position, String filename) {

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();
        String phone = userPhone.get(SharedUserPreferences.Phone);

        storageReference = FirebaseStorage.getInstance().getReference("Pill Reminders/").child(phone).child(filename);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    removeItem(position);
                    Toast.makeText(getApplicationContext(), "Reminder Deleted!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
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

    public void addReminder(View view) {
        Intent intent = new Intent(getApplicationContext(), SetPillReminder.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}