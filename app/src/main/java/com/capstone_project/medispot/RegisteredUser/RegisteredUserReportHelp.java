package com.capstone_project.medispot.RegisteredUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.capstone_project.medispot.Databases.SharedUserPreferences;
import com.capstone_project.medispot.NavigationUtility.BottomNavigation;
import com.capstone_project.medispot.NavigationUtility.RegisteredUserDrawerNavigation;
import com.capstone_project.medispot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisteredUserReportHelp extends AppCompatActivity{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout contentView;
    TextInputEditText email, subject, content;
    TextInputLayout emailSub, emailCont;
    ImageView menuIcon;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registered_user_report_help);

        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);
        contentView = findViewById(R.id.layout);
        menuIcon = findViewById(R.id.menu);
        email = findViewById(R.id.Email);
        subject = findViewById(R.id.Subject);
        content = findViewById(R.id.Content);

        emailSub = findViewById(R.id.emailSubject);
        emailCont = findViewById(R.id.emailContent);

        db = FirebaseFirestore.getInstance();

        View blurView = findViewById(R.id.blurView);


        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userDetails = sharedUserPreferences.getSessionInfo();

        email.setText(userDetails.get(SharedUserPreferences.Email));

        RegisteredUserDrawerNavigation.setupNavigationDrawer(this, drawerLayout, navigationView, menuIcon, contentView, blurView, R.id.reportHelp);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, -1);
    }

    public void reportHelp(View view) {

        String Email = email.getText().toString();
        String Subject = subject.getText().toString();
        String Content = content.getText().toString();

        if(!validateSubject() | !validateContent()){

            return;
        }


        else{

            emailSub.setError(null);
            emailSub.setErrorEnabled(false);

            emailCont.setError(null);
            emailCont.setErrorEnabled(false);

            Map<String, Object> Help_Request = new HashMap<>();
            Help_Request.put("EmailAddress", Email);
            Help_Request.put("EmailSubject", Subject);
            Help_Request.put("EmailContent", Content);
            Help_Request.put("ReferenceID", "");
            Help_Request.put("Timestamp", FieldValue.serverTimestamp());

            db.collection("Help_Request")
                    .add(Help_Request)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            String id = documentReference.getId();
                            documentReference.update("ReferenceID", id, "Timestamp", FieldValue.serverTimestamp());
                            reportHelpNotifications();

                            Toast.makeText(RegisteredUserReportHelp.this, "Request sent successfully", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisteredUserReportHelp.this, "Failed to send", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

    public void reportHelpNotifications() {
        String Email = email.getText().toString();
        String Subject = subject.getText().toString();

        Map<String, Object> Help_Request = new HashMap<>();
        Help_Request.put("EmailAddress", Email);
        Help_Request.put("EmailSubject", Subject);
        Help_Request.put("ReferenceID", "");
        Help_Request.put("Timestamp", FieldValue.serverTimestamp());

        db.collection("Help_Request_Notifications")
                .add(Help_Request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        String id = documentReference.getId();
                        documentReference.update("ReferenceID", id, "Timestamp", FieldValue.serverTimestamp());

                        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private boolean validateSubject() {

        subject = findViewById(R.id.Subject);
        String emailSubject = subject.getText().toString().trim();;

        if (emailSubject.isEmpty()) {
            emailSub.setError("This is required!!");
            return false;

        }else {
            emailSub.setError(null);
            emailSub.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateContent() {

        content = findViewById(R.id.Content);
        String emailContent = content.getText().toString().trim();;

        if (emailContent.isEmpty()) {
            emailCont.setError("This is required!!");
            return false;

        }else {
            emailCont.setError(null);
            emailCont.setErrorEnabled(false);
        }

        return true;
    }

    public void cancel(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
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