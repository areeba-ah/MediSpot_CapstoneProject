package com.capstone_project.medispot.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone_project.medispot.CommonFiles.drugs;
import com.capstone_project.medispot.HelperClasses.drugResultAdapter;
import com.capstone_project.medispot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ResultPage extends AppCompatActivity implements drugResultAdapter.onClickListener {
    ProgressBar progressBar;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    drugResultAdapter adapter;
    ArrayList<drugs> arrayList;
    LinearLayout layout_lottie;

    List<String> check = new ArrayList<String>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_result_page);

        progressBar = findViewById(R.id.progress);
        layout_lottie = findViewById(R.id.layout_lottie);

        recyclerView = findViewById(R.id.view);
        db = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<drugs>();
        adapter = new drugResultAdapter(this,arrayList,this);
        recyclerView.setAdapter(adapter);

        Intent search = getIntent();
        String from = search.getStringExtra("source");

        if("SavedDrug".equals(from))
        {
            String reference =  search.getStringExtra("saved");
            fromSavedDrugs(reference);
        }

        else if("searchHistory".equals(from))
        {
            String reference =  search.getStringExtra("history");
            fromSavedDrugs(reference);
        }

        else if ("ByKeyword".equals(from))
        {
            String keyword =  search.getStringExtra("keyword");
            byKeyword(keyword);
        }

        else if ("ByImage".equals(from))
        {
            String image =  search.getStringExtra("Image");
            byImage(image);
        }

        else if ("ByBarcode".equals(from))
        {
            String barcode =  search.getStringExtra("Barcode");
            byBarcode(barcode);
        }

        else if ("ByQRcode".equals(from))
        {
            String qrcode =  search.getStringExtra("QRcode");
            byQRcode(qrcode);
        }

        else if ("ByText".equals(from))
        {
            String text =  search.getStringExtra("Text");

            String[] arrOfStr  = text.trim().split("\\s+");
            for (String a : arrOfStr)
            {
                byText(a);
            }
        }
    }

    private void byImage(String a){

        db.collection("Drug_List").whereEqualTo("DrugName", a).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot document : list) {
                                arrayList.add(document.toObject(drugs.class));

                            }
                            layout_lottie.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {

                            Toast.makeText(getApplicationContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void byText(String a){
        check.add("testing");
        db.collection("Drug_List").orderBy("DrugName").startAt(a).endAt(a + "\uf8ff").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot document : list) {

                                String s = (String) document.get("DrugName");
                                boolean result = check.contains(s);
                                if(!result){
                                    check.add(s);
                                    arrayList.add(document.toObject(drugs.class));
                                }
                            }
                            layout_lottie.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {
                        }
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();}});
    }

    private void byKeyword(String keyword){
        check.add("testing");
        db.collection("Drug_List").orderBy("DrugName").startAt(keyword).endAt(keyword + "\uf8ff").get()

                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot document : list) {

                                String s = (String) document.get("DrugName");
                                boolean result = check.contains(s);
                                if(!result){
                                    check.add(s);
                                    arrayList.add(document.toObject(drugs.class));
                                }
                            }
                            layout_lottie.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {

                            Toast.makeText(getApplicationContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e){
                        Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();}});
    }

    private void byBarcode(String barcode){

        db.collection("Drug_List").whereEqualTo("Barcode", barcode).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot document : list) {
                                arrayList.add(document.toObject(drugs.class));
                            }
                            layout_lottie.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {

                            Toast.makeText(getApplicationContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void byQRcode(String qrcode){
        db.collection("Drug_List").whereEqualTo("QRcode", qrcode).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot document : list) {
                                arrayList.add(document.toObject(drugs.class));
                            }
                            layout_lottie.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {

                            Toast.makeText(getApplicationContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fromSavedDrugs(String reference){

        db.collection("Drug_List").whereEqualTo("Document_ReferenceID", reference).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot document : list) {
                                arrayList.add(document.toObject(drugs.class));
                            }
                            layout_lottie.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {

                            Toast.makeText(getApplicationContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onClick(int position) {
        drugs drug = arrayList.get(position);
        Log.d("LOTTIE", "onClick: Clicked");

        arrayList.get(position);
        Intent intent = new Intent(getApplicationContext(), DrugInfo.class);
        intent.putExtra("referenceID",drug.getDocument_ReferenceID());
        intent.putExtra("drugName",drug.getDrugName());
        intent.putExtra("brandName",drug.getBrandName());
        intent.putExtra("genericName",drug.getGenericName());
        intent.putExtra("description",drug.getDescription());
        intent.putExtra("directions",drug.getDirections());
        intent.putExtra("dosage",drug.getDosage());
        intent.putExtra("precautions",drug.getPrecautions());
        intent.putExtra("sideEffects",drug.getSideEffects());
        intent.putExtra("warnings",drug.getWarnings());
        intent.putExtra("frontImage",drug.getFrontImage());
        intent.putExtra("backImage",drug.getBackImage());
        intent.putExtra("pilImage",drug.getPilImage());
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void back_btn(View view) {
        onBackPressed();
    }
}