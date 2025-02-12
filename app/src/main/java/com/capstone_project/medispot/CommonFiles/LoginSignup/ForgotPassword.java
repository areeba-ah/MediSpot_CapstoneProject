package com.capstone_project.medispot.CommonFiles.LoginSignup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.capstone_project.medispot.Databases.SharedUserPreferences;
import com.capstone_project.medispot.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class ForgotPassword extends AppCompatActivity {
    TextInputLayout phone;
    String Phone, update_pass;
    CountryCodePicker countryCodePicker;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password);


        countryCodePicker = findViewById(R.id.country_code);
        phone = findViewById(R.id.phone_number);

    }

    public void next_btn(View view) {

        if(!validatePhone()){return;}


        Phone = getIntent().getStringExtra("phone");

        String phoneProvided = phone.getEditText().getText().toString().trim();
        String phone_no = "+"+countryCodePicker.getSelectedCountryCode()+phoneProvided;


        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(ForgotPassword.this, SharedUserPreferences.forgot);
        sharedUserPreferences.createForgotSession(phone_no);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query chkUser = reference.orderByChild("phone").equalTo(phone_no);

        chkUser.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    phone.setError(null);
                    phone.setErrorEnabled(false);
                    Intent intent = new Intent(getApplicationContext(), VerificationCode.class);
                    intent.putExtra("phone", phone_no);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(ForgotPassword.this, "No such user exists!",Toast.LENGTH_SHORT).show();
                    phone.setError("No such user exists!");

                    phone.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public boolean validatePhone(){

        phone = findViewById(R.id.phone_num);
        String Phone = phone.getEditText().getText().toString().trim();
        String chk1 = "[0-9]{9}";

        if(Phone.isEmpty()){
            phone.setError("Please enter your phone number");
            return false; }

        else if(!Phone.matches(chk1)){
            phone.setError("Please enter a valid phone number.");
            return false;
        }
        else {phone.setError(null);
            phone.setErrorEnabled(false); return true;}
    }


    public void back_btn(View view) {
        startActivity(new Intent(this,Login.class));
        finish();
    }

    public void signup(View view) {
        startActivity(new Intent(this, SignScreen.class));
        finish();

    }

}