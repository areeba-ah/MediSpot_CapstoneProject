package com.capstone_project.medispot.User;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.capstone_project.medispot.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jsibbold.zoomage.ZoomageView;

public class DrugInfo extends AppCompatActivity {

    TextView drug_name, Drug_name, brand_name, generic_name,drug_desc,drug_dosage, drug_directions,side_effects,drug_precautions, drug_warning;
    LinearLayout name, brand, generic, description, dosage,directions, effects, precautions, warnings;
    ImageView next, back;
    ZoomageView frontImg, backImg, pilImg;
    FirebaseFirestore db;
    ViewFlipper viewFlipper;
    LinearLayout dotsLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drug_info);

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

        db = FirebaseFirestore.getInstance();

        loadData();
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
    Glide.with(DrugInfo.this).load(frontImage).into(frontImg);
    Glide.with(DrugInfo.this).load(backImage).into(backImg);
    Glide.with(DrugInfo.this).load(pilImage).into(pilImg);

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
}

    public void back_btn(View view) {
        onBackPressed();
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
        super.onBackPressed();
    }


    public void back(View view) {
        viewFlipper.showPrevious();
    }

    public void next(View view) {
        viewFlipper.showNext();
    }
}