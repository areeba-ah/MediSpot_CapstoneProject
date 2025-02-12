package com.capstone_project.medispot.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.capstone_project.medispot.NavigationUtility.DrawerNavigation;
import com.capstone_project.medispot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReportHelp extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentView;
    ImageView menuIcon;

    TextInputEditText email, subject, content;
    TextInputLayout emailAdd, emailSub, emailCont;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_report_help);

        menuIcon = findViewById(R.id.menu);
        contentView = findViewById(R.id.layout);
        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);

        email = findViewById(R.id.Email);
        subject = findViewById(R.id.Subject);
        content = findViewById(R.id.Content);

        emailAdd = findViewById(R.id.emailAddress);
        emailSub = findViewById(R.id.emailSubject);
        emailCont = findViewById(R.id.emailContent);

        db = FirebaseFirestore.getInstance();

        View blurView = findViewById(R.id.blurView);

        DrawerNavigation.setupNavigationDrawer(this, drawerLayout, navigationView, menuIcon, contentView, blurView, R.id.reportHelp);

    }

    public void reportHelp(View view) {

        String Email = email.getText().toString();
        String Subject = subject.getText().toString();
        String Content = content.getText().toString();

        if(!validateEmail() |!validateSubject() | !validateContent()){
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

                            Toast.makeText(ReportHelp.this, "Request sent successfully", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ReportHelp.this, "Failed to send", Toast.LENGTH_SHORT).show();

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

                        Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private boolean validateEmail(){

        String Email = emailAdd.getEditText().getText().toString().trim();
        String chk1 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(Email.isEmpty()){
            emailAdd.setError("Please enter your email address");
            return false; }

        else if(!Email.matches(chk1)){
            emailAdd.setError("Please enter a valid email address.");
            return false;
        }

        else {
            emailAdd.setError(null);
            emailAdd.setErrorEnabled(false);}

        return true;

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
        Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

}