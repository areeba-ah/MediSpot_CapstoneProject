package com.capstone_project.medispot.RegisteredUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.capstone_project.medispot.CommonFiles.LoginSignup.Login;
import com.capstone_project.medispot.Databases.SharedUserPreferences;
import com.capstone_project.medispot.NavigationUtility.BottomNavigation;
import com.capstone_project.medispot.NavigationUtility.RegisteredUserDrawerNavigation;
import com.capstone_project.medispot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MyAccount extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout contentView;
    ImageView menuIcon;
    TextInputEditText email, new_pass, con_pass, editEmail;
    TextInputLayout updateEmail, newPass, conPass;
    TextView name;
    LinearLayout updateLayout, update_Email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_account);

        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);
        contentView = findViewById(R.id.mainlayout);
        menuIcon = findViewById(R.id.menu);
        name = findViewById(R.id.name);
        email = findViewById(R.id.Email);
        updateEmail = findViewById(R.id.UpdateEmail);
        editEmail = findViewById(R.id.editEmail);

        newPass = findViewById(R.id.newPassword);
        conPass = findViewById(R.id.conPass);

        new_pass = findViewById(R.id.newPass_editText);
        con_pass = findViewById(R.id.conPass_editText);

        updateLayout = findViewById(R.id.updateLayout);
        update_Email = findViewById(R.id.update_Email);

        View blurView = findViewById(R.id.blurView);


        RegisteredUserDrawerNavigation.setupNavigationDrawer(this, drawerLayout, navigationView, menuIcon, contentView, blurView, R.id.account);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, -1);

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userDetails = sharedUserPreferences.getSessionInfo();

        name.setText("Welcome " + userDetails.get(SharedUserPreferences.Name) + ",");
        email.setText(userDetails.get(SharedUserPreferences.Email));
        editEmail.setText(userDetails.get(SharedUserPreferences.Email));

        new_pass.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                newPass.setHelperText("Use combination of at least 8 special characters,numbers and letters (upper & lowercase)");

            } else {
                newPass.setHelperText(null);
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

    public void cancel(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


    public void updateMail(View view) {

        updateLayout.setVisibility(View.GONE);
        update_Email.setVisibility(View.VISIBLE);
    }

    public void update(View view) {
        if (!validateEmail()) {
            return;
        } else {
            String Email = updateEmail.getEditText().getText().toString().trim();

            SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
            HashMap<String, String> userDetails = sharedUserPreferences.getSessionInfo();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userDetails.get(SharedUserPreferences.Phone));
            reference.child("email").setValue(Email);

            Toast.makeText(MyAccount.this, "Email address updated !!", Toast.LENGTH_SHORT).show();

            update_Email.setVisibility(View.GONE);
            updateLayout.setVisibility(View.VISIBLE);

            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        }

    }

    private boolean validateEmail() {
        updateEmail = findViewById(R.id.UpdateEmail);

        String Email = updateEmail.getEditText().getText().toString().trim();
        String chk1 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (Email.isEmpty()) {
            updateEmail.setError("Please provide an email address");
            return false;
        } else if (!Email.matches(chk1)) {
            updateEmail.setError("Please enter a valid email address.");
            return false;
        } else {
            updateEmail.setError(null);
            updateEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {

        String pswd = newPass.getEditText().getText().toString().trim();

        //At least 1 digit, At least 1 Uppercase,At least 8 character,
        String validPass = "^" + "(?=.*[0-9])" + "(?=.*[A-Z])" + "(?=.*[A-Za-z0-9])" + "(?=.*[@#$%^&._])" + "(?=.\\S+$)" + ".{8,}" + "$";

        if (pswd.isEmpty()) {
            newPass.setError("Please enter the password");
            return false;
        } else if (pswd.length() < 8) {
            newPass.setError("Invalid Password");
            return false;
        } else if (!pswd.matches(validPass)) {
            newPass.setError("Please enter valid password");
            return false;
        } else {
            newPass.setError(null);
            newPass.setErrorEnabled(false);
            return true;
        }

    }

    private boolean conPass() {

        String conpswd = conPass.getEditText().getText().toString().trim();

        //At least 1 digit, At least 1 Uppercase,At least 8 character,
        String validPass = "^" + "(?=.*[0-9])" + "(?=.*[A-Z])" + "(?=.*[A-Za-z0-9])" + "(?=.*[@#$%^&._])" + "(?=.\\S+$)" + ".{8,}" + "$";

        if (conpswd.isEmpty()) {
            conPass.setError("Please enter the password");
            return false;
        } else if (conpswd.length() < 8) {
            conPass.setError("Invalid Password");
            return false;
        } else if (!conpswd.matches(validPass)) {
            conPass.setError("Please enter valid password");
            return false;
        } else {
            conPass.setError(null);
            conPass.setErrorEnabled(false);
        }
        return true;
    }

    public void updatePass(View view) {

        String pswd = newPass.getEditText().getText().toString().trim();
        String conpswd = conPass.getEditText().getText().toString().trim();

        if (!validatePassword() | !conPass()) {
            return;
        }

        else if (!pswd.equals(conpswd)) {
            conPass.setError("Password does not match");
            return;
        }
        else {
            SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
            HashMap<String, String> userDetails = sharedUserPreferences.getSessionInfo();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(userDetails.get(SharedUserPreferences.Phone));

                reference.child("password").setValue(pswd);

            Toast.makeText(MyAccount.this, "Password updated !!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        }

    }

}

