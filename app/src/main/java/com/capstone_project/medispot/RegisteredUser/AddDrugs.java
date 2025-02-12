package com.capstone_project.medispot.RegisteredUser;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.capstone_project.medispot.Databases.SharedUserPreferences;
import com.capstone_project.medispot.NavigationUtility.BottomNavigation;
import com.capstone_project.medispot.NavigationUtility.RegisteredUserDrawerNavigation;
import com.capstone_project.medispot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddDrugs extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout contentView;
    TextInputEditText drugName, purpose;
    TextInputLayout name, drugPurpose, drugType;
    AutoCompleteTextView autoCompleteTextView_type;
    Button requestDrug;
    ImageView image;
    ImageView icon;
    ImageView menuIcon;
    Uri resultUri;
    String ImgURL;

    FirebaseFirestore db;
    StorageReference storageReference;

    int imageSize = 224;

    private static final int CROP_IMAGE_REQUEST = 2; // Custom request code for crop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_drugs);

        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);
        contentView = findViewById(R.id.layout);
        drugName = findViewById(R.id.drug_name);
        purpose = findViewById(R.id.drug_purpose);
        menuIcon = findViewById(R.id.menu);
        image = findViewById(R.id.medicine_pic);
        icon = findViewById(R.id.take_pic);
        autoCompleteTextView_type = findViewById(R.id.dropdown_types);
        requestDrug = findViewById(R.id.send_request);

        drugType = findViewById(R.id.Drug_type);
        name = findViewById(R.id.drugName);
        drugPurpose = findViewById(R.id.drugPurpose);

        View blurView = findViewById(R.id.blurView);


        db = FirebaseFirestore.getInstance();

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean pick = true;
                if (pick) {
                    if (!checkCameraPermission())
                        requestCameraPermission();
                    else pickImage();

                } else {
                    if (!checkGalleryPermission())
                        requestGalleryPermission();
                    else pickImage();
                }
            }
        });

        Resources res = getResources();
        String[] medication_types = res.getStringArray(R.array.med_types);

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(AddDrugs.this, R.layout.dropdown_med, medication_types);
        autoCompleteTextView_type.setAdapter(itemAdapter);

        RegisteredUserDrawerNavigation.setupNavigationDrawer(this, drawerLayout, navigationView, menuIcon, contentView, blurView, R.id.addDrug);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, -1);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1); // Use request code 1 for picking image
    }
    private boolean checkCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result1 && result2;
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkGalleryPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestGalleryPermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            cropImage(selectedImageUri); // Call cropImage for cropping
        }

        if (requestCode == CROP_IMAGE_REQUEST && resultCode == RESULT_OK) {
            // Handle the result after cropping
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap croppedImage = extras.getParcelable("data");
                int dimension = Math.min(croppedImage.getWidth(), croppedImage.getHeight());
                croppedImage = ThumbnailUtils.extractThumbnail(croppedImage, dimension, dimension);
                croppedImage = Bitmap.createScaledBitmap(croppedImage, imageSize, imageSize, false);

                // Display the cropped image in the ImageView
                image.setImageBitmap(croppedImage);

                // You can now use 'croppedImage' for further operations (uploading, etc.)
                resultUri = getImageUri(this, croppedImage); // Convert Bitmap to Uri for upload
            }
        }
    }

    private void cropImage(Uri selectedImageUri) {
        Intent cropIntent = new Intent("com.android.activity.CROP");
        cropIntent.setDataAndType(selectedImageUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1); // Aspect ratio for square cropping
        cropIntent.putExtra("outputX", 500); // Max output width
        cropIntent.putExtra("outputY", 500); // Max output height
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, CROP_IMAGE_REQUEST); // Use the constant for crop
    }

    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "croppedImage", null);
        return Uri.parse(path);
    }

    public void requestToAddDrug(View view) {
        String DrugName = drugName.getText().toString().trim();
        String Purpose = purpose.getText().toString().trim();
        String Type = autoCompleteTextView_type.getText().toString();

        if (!validateImage() | !validateDrugName(DrugName) | !validateDrugPurpose(Purpose) | !validateDrugType(Type)) {
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy@hh:mm:ss:ms", Locale.getDefault());
        Date now = new Date();
        String fileName = formatter.format(now);

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();

        String phone = userPhone.get(SharedUserPreferences.Phone);

        storageReference = FirebaseStorage.getInstance().getReference("Drug Requests/").child(phone).child(fileName);
        storageReference.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ImgURL = uri.toString();
                                addRequest(ImgURL);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddDrugs.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddDrugs.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addRequest(String ImgURL) {
        String DrugName = drugName.getText().toString();
        String Purpose = purpose.getText().toString();
        String Type = autoCompleteTextView_type.getText().toString();

        Map<String, Object> Drug_Request = new HashMap<>();
        Drug_Request.put("DrugName", DrugName);
        Drug_Request.put("DrugPurpose", Purpose);
        Drug_Request.put("DrugType", Type);
        Drug_Request.put("DrugImgURL", ImgURL);
        Drug_Request.put("ReferenceID", "");
        Drug_Request.put("Timestamp", FieldValue.serverTimestamp());

        db.collection("Drug_Request")
                .add(Drug_Request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String id = documentReference.getId();
                        documentReference.update("ReferenceID", id, "Timestamp", FieldValue.serverTimestamp());
                        Toast.makeText(AddDrugs.this, "Request sent successfully", Toast.LENGTH_SHORT).show();
                        addRequestNotification();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddDrugs.this, "Failed to send", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addRequestNotification() {
        String DrugName = drugName.getText().toString();
        String Type = autoCompleteTextView_type.getText().toString();

        Map<String, Object> Drug_Request = new HashMap<>();
        Drug_Request.put("DrugName", DrugName);
        Drug_Request.put("DrugType", Type);
        Drug_Request.put("ReferenceID", "");
        Drug_Request.put("Timestamp", FieldValue.serverTimestamp());

        db.collection("Drug_Request_Notifications")
                .add(Drug_Request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String id = documentReference.getId();
                        documentReference.update("ReferenceID", id, "Timestamp", FieldValue.serverTimestamp());
                        homePage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        homePage();
                    }
                });
    }

    public boolean validateImage() {
        if (resultUri == null) {
            Toast.makeText(getApplicationContext(), "Please provide an Image!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean validateDrugName(String DrugName) {
        name = findViewById(R.id.drugName);

        if (DrugName.isEmpty()) {
            name.setError("This is required!!");
            return false;

        } else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validateDrugPurpose(String DrugPurpose) {
        drugPurpose = findViewById(R.id.drugPurpose);

        if (DrugPurpose.isEmpty()) {
            drugPurpose.setError("This is required!!");
            return false;

        } else {
            drugPurpose.setError(null);
            drugPurpose.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validateDrugType(String type) {
        drugType = findViewById(R.id.Drug_type);
        if (type.isEmpty()) {
            drugType.setError("This is required!!");
            return false;
        }
        drugType.setError(null);
        drugType.setErrorEnabled(false);
        return true;
    }

    public void homePage() {
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void cancel(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}