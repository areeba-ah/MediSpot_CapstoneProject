package com.capstone_project.medispot.CommonFiles.LoginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone_project.medispot.Databases.SharedUserPreferences;
import com.capstone_project.medispot.R;
import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VerificationCode extends AppCompatActivity {
    PinView pinView;
    String codeGenerate, phone_no;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_valification_code);

        TextView textview = findViewById(R.id.phoneVerify);
        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(VerificationCode.this, SharedUserPreferences.forgot);
        HashMap<String, String> userInfo = sharedUserPreferences.getForgotSessionInfo();

        String Phone = userInfo.get(sharedUserPreferences.verifyPhone);
        textview.setText("An OTP has been sent to the mobile number "+Phone);

        pinView = findViewById(R.id.pin);

        Intent intent = getIntent();
        phone_no = intent.getStringExtra("phone");

        sendOTP(phone_no);
    }

    private void sendOTP(String phone_no) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone_no)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new
            PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeGenerate = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if(code != null){
                        pinView.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(VerificationCode.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeGenerate,code);
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(),ResetPassword.class);
                            intent.putExtra("phone", phone_no);
                            startActivity(intent);
                            finish();


                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerificationCode.this,"Incomplete Verification! Try again", Toast.LENGTH_SHORT).show();
                            }
                        }

                });
    }

    public void back_btn(View view) {
        startActivity(new Intent(this, ForgotPassword.class));
        finish();
    }

    public void verify(View view) {
      String code = pinView.getText().toString().trim();
       if(!code.isEmpty()){
           verifyCode(code);
       }
    }
}