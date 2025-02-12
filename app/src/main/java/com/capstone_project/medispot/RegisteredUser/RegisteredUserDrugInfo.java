package com.capstone_project.medispot.RegisteredUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.capstone_project.medispot.Databases.SharedUserPreferences;
import com.capstone_project.medispot.NavigationUtility.BottomNavigation;
import com.capstone_project.medispot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jsibbold.zoomage.ZoomageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegisteredUserDrugInfo extends AppCompatActivity {

    TextView drug_name, Drug_name, brand_name, generic_name,drug_desc,drug_dosage, drug_directions,side_effects,drug_precautions, drug_warning;
    LinearLayout name, brand, generic, description, dosage,directions, effects, precautions, warnings;
    ImageView save, next, back;
    ImageView nameV, brandV, genericV, descV, dosV, dircV, sideV, precV,warnV;
    ZoomageView frontImg, backImg, pilImg;
    FirebaseFirestore db;
    ViewFlipper viewFlipper;
    LinearLayout dotsLayout;
    TextToSpeech textToSpeech;

    public boolean textToSpeechIsInitialized = false;
    public boolean stop = true;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registered_user_drug_info);

        save = findViewById(R.id.save_btn);
        viewFlipper = findViewById(R.id.flipper);
        dotsLayout = findViewById(R.id.dots);

        next = findViewById(R.id.next);
        back = findViewById(R.id.back);

        frontImg = findViewById(R.id.frontImage);
        backImg = findViewById(R.id.backImage);
        pilImg = findViewById(R.id.pilImage);

        drug_name = findViewById(R.id.drugName);
        Drug_name = findViewById(R.id.Drug_name);
        brand_name = findViewById(R.id.brand_name);
        generic_name = findViewById(R.id.generic_name);
        drug_desc = findViewById(R.id.drug_desc);
        drug_dosage = findViewById(R.id.drug_dosage);
        drug_directions = findViewById(R.id.drug_directions);
        side_effects = findViewById(R.id.side_effects);
        drug_precautions = findViewById(R.id.drug_precautions);
        drug_warning = findViewById(R.id.drug_warning);

        name = findViewById(R.id.name_drop);
        brand = findViewById(R.id.brand_drop);
        generic = findViewById(R.id.generic_drop);
        description = findViewById(R.id.description_drop);
        dosage = findViewById(R.id.dosage_drop);
        directions = findViewById(R.id.dirc_drop);
        effects = findViewById(R.id.effect_drop);
        precautions = findViewById(R.id.prec_drop);
        warnings = findViewById(R.id.warn_drop);

        nameV = findViewById(R.id.nameVoice);
        brandV = findViewById(R.id.brandVoice);
        genericV = findViewById(R.id.genericVoice);
        descV = findViewById(R.id.descVoice);
        dosV = findViewById(R.id.dosageVoice);
        dircV = findViewById(R.id.dirVoice);
        sideV = findViewById(R.id.effectsVoice);
        precV = findViewById(R.id.precVoice);
        warnV = findViewById(R.id.warnVoice);


        db = FirebaseFirestore.getInstance();

        checkSaved();

        loadData();
        checkHistory();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, -1);
    }

    private void loadData() {

    Intent intent = getIntent();
    String reference =  intent.getStringExtra("referenceID");
    String drugName = intent.getStringExtra("drugName");
    String brandName = intent.getStringExtra("brandName");
    String genericName = intent.getStringExtra("genericName");
    String description = intent.getStringExtra("description");
    String directions = intent.getStringExtra("directions");
    String dosage = intent.getStringExtra("dosage");
    String precautions = intent.getStringExtra("precautions");
    String sideEffects = intent.getStringExtra("sideEffects");
    String warnings = intent.getStringExtra("warnings");
    String frontImage = intent.getStringExtra("frontImage");
    String backImage = intent.getStringExtra("backImage");
    String pilImage = intent.getStringExtra("pilImage");


    //Images
    Glide.with(RegisteredUserDrugInfo.this).load(frontImage).into(frontImg);
    Glide.with(RegisteredUserDrugInfo.this).load(backImage).into(backImg);
    Glide.with(RegisteredUserDrugInfo.this).load(pilImage).into(pilImg);

    drug_name.setText(drugName);
    Drug_name.setText(drugName);
    brand_name.setText(brandName);
    generic_name.setText(genericName);
    drug_desc.setText(description);
    drug_directions.setText(directions);
    drug_dosage.setText(dosage);
    drug_precautions.setText(precautions);
    side_effects.setText(sideEffects);
    drug_warning.setText(warnings);

        nameV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             toSpeech("Drug Name",drugName);
            }
        });

        brandV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSpeech("Brand Name", brandName);

            }
        });

        genericV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSpeech("Generic Name",genericName);
            }
        });

        descV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { toSpeech("Descriptions",description);
            }
        });

        dosV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSpeech("Dosage",dosage);
            }
        });

        dircV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSpeech("Directions",directions);
            }
        });

        sideV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSpeech("Side Effects",sideEffects);
            }
        });

        precV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSpeech("Precautions",precautions);
            }
        });

        warnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSpeech("Warning",warnings);
            }
        });
}

    public void toSpeech(String readTitle,String readOut) {

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    textToSpeechIsInitialized = true;  // <--- add this line

                    int result = textToSpeech.setLanguage(Locale.US);
                    //textToSpeech.setPitch(3); // set pitch level

                    textToSpeech.setSpeechRate(0); // set speech speed rate

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    } else {
                        if(textToSpeech.isSpeaking()){
                            textToSpeech.stop();
                            textToSpeech.shutdown();
                        }
                        speakOut(readTitle,readOut);
                    }

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

    }

    public void speakOut(String readTitle, String readOut){
        stop = false;
            String read = readTitle+" "+readOut;
            textToSpeech.speak(read,TextToSpeech.QUEUE_FLUSH,null);

            if(!textToSpeech.isSpeaking()){stop = true;}
    }

    public void back_btn(View view) {

        if(!stop){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        onBackPressed();
    }

    public void addReminder(View view) {
        Intent pillReminder = new Intent(getApplicationContext(), SetPillReminder.class);
        pillReminder.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(pillReminder);
    }


    public void nameDrop(View view) {
        int v = (Drug_name.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        Drug_name.setVisibility(v);
        Drug_name.setKeyListener(null);
    }

    public void brandDrop(View view) {
        int v = (brand_name.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        brand_name.setVisibility(v);
        brand_name.setKeyListener(null);
    }

    public void genericDrop(View view) {
        int v = (generic_name.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        generic_name.setVisibility(v);
        generic_name.setKeyListener(null);
    }

    public void descriptionDrop(View view) {
        int v = (drug_desc.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        drug_desc.setVisibility(v);
        drug_desc.setKeyListener(null);
    }

    public void dosageDrop(View view) {
        int v = (drug_dosage.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        drug_dosage.setVisibility(v);
        drug_dosage.setKeyListener(null);
    }

    public void dircDrop(View view) {
        int v = (drug_directions.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        drug_directions.setVisibility(v);
        drug_directions.setKeyListener(null);
    }

    public void effectDrop(View view) {
        int v = (side_effects.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        side_effects.setVisibility(v);
        side_effects.setKeyListener(null);
    }

    public void precDrop(View view) {
        int v = (drug_precautions.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        drug_precautions.setVisibility(v);
        drug_precautions.setKeyListener(null);
    }

    public void warnDrop(View view) {
        int v = (drug_warning.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        drug_warning.setVisibility(v);
        drug_warning.setKeyListener(null);
    }


    @Override
    public void onBackPressed() {

        if(!stop){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onBackPressed();
    }

    public void checkSaved(){

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getRememberSessionInfo();
        String phone = userPhone.get(SharedUserPreferences.Phone);

        Intent intent = getIntent();
        db.collection("Saved_Drugs").document(phone).collection("UserCollection")
                .whereEqualTo("ReferenceInDrugList", intent.getStringExtra("referenceID"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            save.setVisibility(View.GONE);
                        }
                    } else {

                    }
                });
    }

    public void saveDrug(View view) {

        Intent intent = getIntent();
        String reference =  intent.getStringExtra("referenceID");
        String Image = intent.getStringExtra("frontImage");
        String DrugName = drug_name.getText().toString();

        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        Date current = new Date();
        String DATE = date.format(current);

        save.setVisibility(View.GONE);

        Map<String, Object> Saved_Drug = new HashMap<>();
        Saved_Drug.put("DrugName", DrugName);
        Saved_Drug.put("DrugImgURL", Image);
        Saved_Drug.put("ReferenceID", "");
        Saved_Drug.put("Date", DATE);
        Saved_Drug.put("ReferenceInDrugList",reference );
        Saved_Drug.put("Timestamp", FieldValue.serverTimestamp());

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();
        String phone = userPhone.get(SharedUserPreferences.Phone);

        db.collection("Saved_Drugs").document(phone).collection("UserCollection")
                .add(Saved_Drug)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        String id = documentReference.getId();
                        documentReference.update("ReferenceID", id, "Timestamp", FieldValue.serverTimestamp());

                        Toast.makeText(RegisteredUserDrugInfo.this, "Drug Saved!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisteredUserDrugInfo.this, "Failed to send", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void checkHistory(){
        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getRememberSessionInfo();
        String phone = userPhone.get(SharedUserPreferences.Phone);

        Intent intent = getIntent();
        db.collection("Search_History").document(phone).collection("UserCollection")
                .whereEqualTo("ReferenceInDrugList", intent.getStringExtra("referenceID"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot document : list) {
                                db.collection("Search_History").document(phone).collection("UserCollection").document(document.getId())
                                        .update("Timestamp", FieldValue.serverTimestamp());
                            }

                        } else {
                            addToHistory();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisteredUserDrugInfo.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void addToHistory(){

        Intent intent = getIntent();
        String reference =  intent.getStringExtra("referenceID");
        String DrugName = drug_name.getText().toString();

        Map<String, Object> History = new HashMap<>();
        History.put("DrugName", DrugName);
        History.put("ReferenceID", "");
        History.put("ReferenceInDrugList",reference );
        History.put("Timestamp", FieldValue.serverTimestamp());

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();
        String phone = userPhone.get(SharedUserPreferences.Phone);

        db.collection("Search_History").document(phone).collection("UserCollection")
                .add(History)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        String id = documentReference.getId();
                        documentReference.update("ReferenceID", id, "Timestamp", FieldValue.serverTimestamp());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void back(View view) {
        viewFlipper.showPrevious();
    }

    public void next(View view) {
        viewFlipper.showNext();

    }
}