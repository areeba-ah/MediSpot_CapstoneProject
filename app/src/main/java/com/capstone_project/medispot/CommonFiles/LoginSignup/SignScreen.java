package com.capstone_project.medispot.CommonFiles.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.capstone_project.medispot.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;

public class SignScreen extends AppCompatActivity {
    EditText NAME;
    TextInputLayout name,email;
    DatePicker userAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_screen);

        name = findViewById(R.id.name);
        email = findViewById(R.id.Email);
        userAge = findViewById(R.id.age);
        NAME= findViewById(R.id.name_editText);


        NAME.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                name.setHelperText("Start with an Uppercase Alphabet");

            } else {
                name.setHelperText(null);
            }
        });
    }

    private boolean validateName(){

        String fullName = name.getEditText().getText().toString().trim();
        String chk = "^[A-Z][a-zA-Z]{3,}(?: [A-Z][a-zA-Z]*){0,2}$";

        if(fullName.isEmpty()){
            name.setError("This field can not be empty");
            return false; }

        else if(!fullName.matches(chk)){
            name.setError("Invalid type");
            return false;
        }

        else if(fullName.length() >35){name.setError("Maximum 35 characters allowed"); return false;}

        else {name.setError(null);
            name.setErrorEnabled(false); return true;}

    }

    private boolean validateEmail(){

        String Email = email.getEditText().getText().toString().trim();
        String chk1 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(Email.isEmpty()){
            email.setError("Please enter your email address");
            return false; }

        else if(!Email.matches(chk1)){
            email.setError("Please enter a valid email address.");
            return false;
        }

        else {
            email.setError(null);
            email.setErrorEnabled(false);}

        return true;

    }

    private boolean validateAge(){

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = userAge.getYear();
        int isValid = currentYear - age;

        if(isValid < 16){
            Toast.makeText(this, "Insufficient age for registration", Toast.LENGTH_SHORT).show();
            return false;
        }else return true;

    }

    public void next_btn(View view) {

        if (!validateName() | !validateEmail() | !validateAge()) {
            return;
        }

        int day = userAge.getDayOfMonth();
        int month = userAge.getMonth();
        int year = userAge.getYear();
        String date = day+"/"+month+"/"+year;

        String FullName = name.getEditText().getText().toString();
        String EmailAddress = email.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query chkUser = reference.orderByChild("email").equalTo(EmailAddress);

        chkUser.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    email.setError("An account has been registered with this email address!");
                    email.requestFocus();
                    return;
                }
                else {email.setErrorEnabled(false);
                    Intent intent = new Intent(SignScreen.this, SignUpPage.class);
                    intent.putExtra("name", FullName);
                    intent.putExtra("email", EmailAddress);
                    intent.putExtra("userAge", date);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void login(View view) {
        startActivity(new Intent(this, Login.class));
        finish();
    }
}