package com.capstone_project.medispot.CommonFiles.LoginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone_project.medispot.HelperClasses.SignUpHelper;
import com.capstone_project.medispot.R;
import com.capstone_project.medispot.RegisteredUser.RegisteredUserHomePage;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class SignUpPage extends AppCompatActivity {

    EditText PHONE, PASSWORD;
    TextInputLayout phone,password,conPass;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up_page);

        countryCodePicker = findViewById(R.id.country_code_pick);
        phone = findViewById(R.id.phone_number);
        password = findViewById(R.id.Password);
        conPass = findViewById(R.id.conPass);

        PHONE= findViewById(R.id.phone_editText);
        PASSWORD= findViewById(R.id.pass_editText);


        PHONE.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                phone.setHelperText("9 digit phone number");

            } else { phone.setHelperText(null);}
        });


        PASSWORD.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                password.setHelperText("Use combination of at least 8 special characters,numbers and letters (upper & lowercase)");

            } else {password.setHelperText(null);}
        });
    }


    private boolean validatePhone(){

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


    private boolean validatePassword(){

        String pswd = password.getEditText().getText().toString().trim();

        //At least 1 digit, At least 1 Uppercase,At least 8 character,
        String validPass = "^"+"(?=.*[0-9])"+"(?=.*[A-Z])"+"(?=.*[A-Za-z0-9])"+"(?=.*[@#$%^&._])"+"(?=.\\S+$)"+".{8,}"+"$";

        if(pswd.isEmpty()){
            password.setError("Please enter the password");
            return false; }

        else if(pswd.length() < 8){password.setError("Invalid Password"); return false;}

        else if(!pswd.matches(validPass)){
            password.setError("Please enter valid password");
            return false;
        }
        else {password.setError(null);
            password.setErrorEnabled(false); return true;}

    }

    private boolean conPass(){

        String conpswd= conPass.getEditText().getText().toString().trim();


        //At least 1 digit, At least 1 Uppercase,At least 8 character,
        String validPass = "^"+"(?=.*[0-9])"+"(?=.*[A-Z])"+"(?=.*[A-Za-z0-9])"+"(?=.*[@#$%^&._])"+"(?=.\\S+$)"+".{8,}"+"$";

        if(conpswd.isEmpty()){
            conPass.setError("Please enter the password");
            return false; }

        else if(conpswd.length() < 8){conPass.setError("Invalid Password"); return false;}

        else if(!conpswd.matches(validPass)){
            conPass.setError("Please enter valid password");
            return false;
        }
        else {
            conPass.setError(null);
            conPass.setErrorEnabled(false);} return true;
    }


    public void back_btn(View view) {
        onBackPressed();
    }

    public void sign_up(View view) {


        String pswd = password.getEditText().getText().toString().trim();
        String conpswd = conPass.getEditText().getText().toString().trim();

        if (!validatePhone() | !validatePassword() | !conPass()) {
            Toast.makeText(SignUpPage.this, "please enter valid data", Toast.LENGTH_SHORT).show();
            return;
        }

        else if(!pswd.equals(conpswd)){
            conPass.setError("Password does not match");
            return;

        }

        String phoneProvided = phone.getEditText().getText().toString().trim();
        String phone_no = "+"+countryCodePicker.getSelectedCountryCode()+phoneProvided;

        Intent intent = getIntent();
        String Name = intent.getStringExtra("name");
        String Email = intent.getStringExtra("email");
        String date = intent.getStringExtra("userAge");
        String Pass = password.getEditText().getText().toString();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query chkPhone = reference.orderByChild("phone").equalTo(phone_no);

        chkPhone.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    phone.setError("An account has been registered with this phone number.");
                    phone.requestFocus();
                    return;}

                else { phone.setErrorEnabled(false);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    SignUpHelper signUpHelper = new SignUpHelper(Name, date, phone_no, Email, Pass);
                    reference.child(phone_no).setValue(signUpHelper);

                    Toast.makeText(SignUpPage.this, "Successfully Registered!!",Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getApplicationContext(), RegisteredUserHomePage.class));

                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void login(View view) {
        startActivity(new Intent(this, Login.class));
        finish();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}