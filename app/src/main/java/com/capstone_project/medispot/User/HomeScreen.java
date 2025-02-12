package com.capstone_project.medispot.User;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone_project.medispot.HelperClasses.SearchOptionsAdapter.searchAdapter;
import com.capstone_project.medispot.HelperClasses.SearchOptionsAdapter.searchPageHelper;
import com.capstone_project.medispot.NavigationUtility.DrawerNavigation;
import com.capstone_project.medispot.R;
import com.capstone_project.medispot.ml.Model;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity{

    RecyclerView features;
    RecyclerView.Adapter adapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentView;
    ImageView menuIcon;

    TextInputEditText keyword;

    int imageSize = 224;

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;

    private static final int CROP_IMAGE_REQUEST = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_screen);

        features = findViewById(R.id.features);
        menuIcon = findViewById(R.id.menu);
        contentView = findViewById(R.id.layout);
        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);
        keyword = findViewById(R.id.searchText);

        View blurView = findViewById(R.id.blurView);


        DrawerNavigation.setupNavigationDrawer(this, drawerLayout, navigationView, menuIcon, contentView, blurView, R.id.home);
        featureRecycler();
    }

    private void featureRecycler(){
        features.setHasFixedSize(true);
        features.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        ColorDrawable color1 = new ColorDrawable(0xDDD3E4CD);
        ColorDrawable color2 = new ColorDrawable(0xBBBCD3C2);
        ColorDrawable color3 = new ColorDrawable(0xAAADC2A9);

        ArrayList<searchPageHelper> featuresArray = new ArrayList<>();
        featuresArray.add(new searchPageHelper(R.raw.alarm, "Set Pill Reminder", "Set pill reminders to take your medications on time!", color1));
        featuresArray.add(new searchPageHelper(R.raw.save_drug, "Save Drug for future reference!", "",color2));
        featuresArray.add(new searchPageHelper(R.raw.medicine_bottle, "Request to add a missing drug!", "",color3));

        adapter = new searchAdapter(featuresArray);
        features.setAdapter(adapter);
    }


    public void textSearch(View view) {
        GetImage();
    }

    public void imageSearch(View view) {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 1);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    public void qrSearch(View view) {
        Intent intent = new Intent(getApplicationContext(), QRScanner.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void barcodeSearch(View view) {
        Intent intent = new Intent(getApplicationContext(), BarcodeReaderScanner.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void GetImage() {
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

    private boolean checkCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return result1 && result2;
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkGalleryPermission() {
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return result2;
    }

    private void requestGalleryPermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    public void classifyImage(Bitmap image) {
        String drugName = "";
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; //RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
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
                    "Glutathione", "Methycobal", "Metrolag", " Panadol COLD+FLU", "ParaSustain",
                    "RANOXYL", "SUPER BURNER extra", "Trivit", "Tropex", "UNIFED Expectorant",
                    "Ventol", "VIRTIGENE 16", "VISCODRIL", "Vitamin B Complex", "Vitamin D-3", "Voltaren"};

            if (maxConfidence > 0.4) {
                drugName = drugs[maxPos];
            }

            // Releases model resources if no longer used.
            model.close();

            Intent search = new Intent(getApplicationContext(), ResultPage.class);
            search.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            search.putExtra("source", "ByImage");
            search.putExtra("Image", drugName);
            startActivity(search);

        } catch (IOException e) {
            e.printStackTrace();
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

            if (requestCode == CROP_IMAGE_REQUEST) {
                // Handle the cropped image
                Bundle extras = data.getExtras();
                Bitmap croppedImage = (Bitmap) extras.get("data");
                classifyImage(croppedImage); // Send cropped image for classification
            }
        }
    }

    private void pickImage() {
        // Open the gallery to select an image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);  // Request code 1 to pick the image
    }

    private void cropImage(Uri imageUri) {
        try {
            // Open the crop intent
            Intent cropIntent = new Intent("com.android.activity.crop");
            cropIntent.setDataAndType(imageUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);  // Aspect ratio (width)
            cropIntent.putExtra("aspectY", 1);  // Aspect ratio (height)
            cropIntent.putExtra("outputX", 500);  // Output width
            cropIntent.putExtra("outputY", 500);  // Output height
            cropIntent.putExtra("return-data", true);  // Return the cropped image as a data object
            startActivityForResult(cropIntent, CROP_IMAGE_REQUEST);  // Request code for cropping
        } catch (ActivityNotFoundException e) {
            // Handle the exception when the device doesn't support the crop intent
            Toast.makeText(this, "Your device doesn't support image cropping", Toast.LENGTH_SHORT).show();
        }
    }

    public void search(View view) {
        String Keyword = keyword.getText().toString().trim();
        Intent search = new Intent(getApplicationContext(), ResultPage.class);
        search.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        search.putExtra("source", "ByKeyword");
        search.putExtra("keyword", Keyword);
        startActivity(search);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
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