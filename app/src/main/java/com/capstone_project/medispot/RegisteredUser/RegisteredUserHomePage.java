package com.capstone_project.medispot.RegisteredUser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone_project.medispot.NavigationUtility.BottomNavigation;
import com.capstone_project.medispot.NavigationUtility.RegisteredUserDrawerNavigation;
import com.capstone_project.medispot.R;
import com.capstone_project.medispot.ml.Model;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RegisteredUserHomePage extends AppCompatActivity{

    RecyclerView features;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout contentView;
    ImageView menuIcon;

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;

    TextInputEditText keyword;
    String url;
    int imageSize = 224;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registered_user_home_page);

        menuIcon = findViewById(R.id.menu);
        contentView = findViewById(R.id.layout);
        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);

        View blurView = findViewById(R.id.blurView);
        RegisteredUserDrawerNavigation.setupNavigationDrawer(this, drawerLayout, navigationView, menuIcon, contentView, blurView, R.id.home);


        keyword = findViewById(R.id.searchText);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, R.id.home);

    }

    public void textSearch(View view) {
        GetImage();
    }

    public void imageSearch(View view) {
        if (checkCameraPermission()) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    public void qrSearch(View view) {
        Intent intent = new Intent(getApplicationContext(), QRcodeScanner.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void barcodeSearch(View view) {
        Intent intent = new Intent(getApplicationContext(), BarcodeScanner.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


    public void GetImage() {
        boolean pickFromCamera = true; // Toggle this based on your preference (true = camera, false = gallery)

        if (pickFromCamera) {
            if (!checkCameraPermission()) {
                requestCameraPermission();
            } else {
                pickImageFromCamera();
            }
        } else {
            if (!checkGalleryPermission()) {
                requestGalleryPermission();
            } else {
                pickImageFromGallery();
            }
        }
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

    private void pickImageFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
    }

    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
    }

    public void classifyImage(Bitmap image) {
        String drugName = "";
        try {
            Model model = Model.newInstance(getApplicationContext());

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float[] confidence = outputFeature0.getFloatArray();

            int maxPos = 0;
            float maxConfidence = 0;

            for (int i = 0; i < confidence.length; i++) {
                if (confidence[i] > maxConfidence) {
                    maxConfidence = confidence[i];
                    maxPos = i;
                }
            }

            String[] drugs = {"Artiz", "Atorlip", "BECLOMET NASEL AQUA", "Diclogesic", "Dulcolax",
                    "Flagyl", "Fludrex", "Flutab Sinus", "Gasec - 20", "GENGIGEL", "Gloclav",
                    "Glutathione", "Methycobal", "Metrolag", "Panadol COLD+FLU", "ParaSustain",
                    "RANOXYL", "SUPER BURNER extra", "Trivit", "Tropex", "UNIFED Expectorant",
                    "Ventol", "VIRTIGENE 16", "VISCODRIL", "Vitamin B Complex", "Vitamin D-3", "Voltaren"};

            if (maxConfidence > 0.4) {
                drugName = drugs[maxPos];
            }

            model.close();

            Intent search = new Intent(getApplicationContext(), RegisteredUserResultPage.class);
            search.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            search.putExtra("source", "ByImage");
            search.putExtra("Image", drugName);
            startActivity(search);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error during image classification.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }

            if (requestCode == REQUEST_CODE_GALLERY) {
                Uri imageUri = data.getData();
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                    classifyImage(image);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error while picking image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void search(View view) {
        String Keyword = keyword.getText().toString().trim();
        Intent search = new Intent(getApplicationContext(), RegisteredUserResultPage.class);
        search.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        search.putExtra("source", "ByKeyword");
        search.putExtra("keyword", Keyword);
        startActivity(search);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else{
            super.onBackPressed();
            finishAffinity();
        }
    }


}