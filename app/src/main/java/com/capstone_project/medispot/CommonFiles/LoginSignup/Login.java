package com.capstone_project.medispot.CommonFiles.LoginSignup;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone_project.medispot.Databases.SharedUserPreferences;
import com.capstone_project.medispot.R;
import com.capstone_project.medispot.RegisteredUser.RegisteredUserHomePage;
import com.capstone_project.medispot.User.HomeScreen;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    //Variables
    ConstraintLayout login_screen;
    CheckBox rememberMe;


    //Animations
    Animation loginAnim;
    TextInputEditText log_phone, log_pass;
    TextInputLayout phone, password;
    CountryCodePicker countryCodePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        Intent open = getIntent();
        String from = open.getStringExtra("stop");

        log_phone = findViewById(R.id.login_phone);
        log_pass = findViewById(R.id.login_password);
        countryCodePicker = findViewById(R.id.country_code_pick);
        phone = findViewById(R.id.phone_number);
        password = findViewById(R.id.Password);
        rememberMe = findViewById(R.id.rememberme);

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.remember);
        if(sharedUserPreferences.chkRemember()){
            HashMap<String, String> rememberInfo = sharedUserPreferences.getRememberSessionInfo();

            log_phone.setText(rememberInfo.get(SharedUserPreferences.rememberPhone));
            log_pass.setText(rememberInfo.get(SharedUserPreferences.rememberPassword));
        }
    }

    private boolean validatePhone(){

        String Phone = phone.getEditText().getText().toString().trim();
        String chk1 = "[0-9]{9}";

        if(Phone.isEmpty()){
            phone.setError("Please enter your phone number");
            return false; }

        else if(!Phone.matches(chk1)){
            phone.setError("Please enter a phone number.");
            return false;
        }
        else {phone.setError(null);
            phone.setErrorEnabled(false); return true;}

    }

    private boolean validatePassword(){

        String pswd = password.getEditText().getText().toString().trim();

        if(pswd.isEmpty()){
            password.setError("Please enter the password");
            return false; }


        else {password.setError(null);
            password.setErrorEnabled(false); return true;}

    }


    public void login(View view) {

        if(!validatePhone() | !validatePassword()){
            return;}

        else{ isUser();}
    }

    private void isUser() {

        String passProvided = password.getEditText().getText().toString().trim();
        String phoneProvided = phone.getEditText().getText().toString().trim();
        String phone_no = "+"+countryCodePicker.getSelectedCountryCode()+phoneProvided;

        if(rememberMe.isChecked()){
            SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.remember);
            sharedUserPreferences.createRememberSession(phoneProvided, passProvided);

        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query chkEmail = reference.orderByChild("phone").equalTo(phone_no);


        chkEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    phone.setError(null);
                    phone.setErrorEnabled(false);

                    String passDB = snapshot.child(phone_no).child("password").getValue(String.class);


                    if(passDB.equals(passProvided)){
                        String nameDB = snapshot.child(phone_no).child("name").getValue(String.class);
                        String phoneDB = snapshot.child(phone_no).child("phone").getValue(String.class);
                        String dobDB = snapshot.child(phone_no).child("dob").getValue(String.class);
                        String emailDB = snapshot.child(phone_no).child("email").getValue(String.class);

                        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(Login.this, SharedUserPreferences.isLoggedIn);
                        sharedUserPreferences.createLoggedSession(nameDB, emailDB, dobDB, phoneDB, passDB);

                        Toast.makeText(Login.this, "Successfully Logged in!",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
                        intent.putExtra("name",nameDB);
                        intent.putExtra("phone",phoneDB);
                        intent.putExtra("dob",dobDB);
                        intent.putExtra("email",emailDB);
                        intent.putExtra("password",passDB);
                        startActivity(intent);
                        finish();

                    }else{password.setError("Incorrect Password");
                        Toast.makeText(Login.this, "Incorrect Password, Please try again!",Toast.LENGTH_SHORT).show();
                        password.requestFocus();}

                }else{phone.setError("No such user Exists");
                    Toast.makeText(Login.this, "No such user Exists!",Toast.LENGTH_SHORT).show();
                    phone.requestFocus(); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(this, ForgotPassword.class));
        finish();
    }

    public void guest(View view) {
        startActivity(new Intent(this, HomeScreen.class));
        finish();

    }

    public void sign_up(View view) {
        Intent intent = new Intent(getApplicationContext(),SignScreen.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.signupnow),"signup_screen");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this,pairs);
        startActivity(intent,options.toBundle());
    }
}