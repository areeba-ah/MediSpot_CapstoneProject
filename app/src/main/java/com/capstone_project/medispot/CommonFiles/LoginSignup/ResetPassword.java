package com.capstone_project.medispot.CommonFiles.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.capstone_project.medispot.HelperClasses.SignUpHelper;
import com.capstone_project.medispot.R;
import com.capstone_project.medispot.User.HomeScreen;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetPassword extends AppCompatActivity {

    EditText reset_password;
    TextInputLayout password, conpass;
    String phone_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reset_password);

        Intent intent = getIntent();
        phone_no = intent.getStringExtra("phone");

        password = findViewById(R.id.password);
        conpass = findViewById(R.id.confirm_password);
        reset_password= findViewById(R.id.newPass_editText);


        reset_password.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                password.setHelperText("Use combination of at least 8 special characters,numbers and letters (upper & lowercase)");

            } else {password.setHelperText(null);}
        });


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

        String conpswd= conpass.getEditText().getText().toString().trim();


        //At least 1 digit, At least 1 Uppercase,At least 8 character,
        String validPass = "^"+"(?=.*[0-9])"+"(?=.*[A-Z])"+"(?=.*[A-Za-z0-9])"+"(?=.*[@#$%^&._])"+"(?=.\\S+$)"+".{8,}"+"$";

        if(conpswd.isEmpty()){
            conpass.setError("Please enter the password");
            return false; }

        else if(conpswd.length() < 8){conpass.setError("Invalid Password"); return false;}

        else if(!conpswd.matches(validPass)){
            conpass.setError("Please enter valid password");
            return false;
        }
        else {
            conpass.setError(null);
            conpass.setErrorEnabled(false);} return true;
    }

    public void UpdatePassword(View view) {

        String NewPassword= password.getEditText().getText().toString().trim();
        String conpswd = conpass.getEditText().getText().toString().trim();

        if (!validatePassword() | !conPass()) {
            conpass.setError("Password does not match");
            return;
        }

        else if(!NewPassword.equals(conpswd)){
            conpass.setError("Password does not match");
            return;

        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(phone_no).child("password").setValue(NewPassword);
        startActivity(new Intent(this, PasswordUpdated.class));
        finish();

    }

    public void back_btn(View view) {
        startActivity(new Intent(this, ForgotPassword.class));
        finish();
    }
}