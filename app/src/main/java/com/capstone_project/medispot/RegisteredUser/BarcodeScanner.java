package com.capstone_project.medispot.RegisteredUser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.capstone_project.medispot.NavigationUtility.BottomNavigation;
import com.capstone_project.medispot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BarcodeScanner extends AppCompatActivity {

    private ListenableFuture cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private MyImageAnalysis myImageAnalysis;

    int counter =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barcode_scanner);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, -1);

        previewView = findViewById(R.id.previewview);
        this.getWindow().setFlags(1024, 1024);

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        myImageAnalysis = new MyImageAnalysis(getSupportFragmentManager());

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != (PackageManager.PERMISSION_GRANTED))
                    {
                        ActivityCompat.requestPermissions(BarcodeScanner.this,new String[]{Manifest.permission.CAMERA},101);
                    }

                    ProcessCameraProvider processCameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                    bindpreview(processCameraProvider);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }, ContextCompat.getMainExecutor(this));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0) {

            ProcessCameraProvider processCameraProvider = null;

            try {
                processCameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bindpreview(processCameraProvider);
        }

    }

    private void bindpreview(ProcessCameraProvider processCameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageCapture imageCapture = new ImageCapture.Builder().build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280,720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(cameraExecutor,myImageAnalysis);
        processCameraProvider.unbindAll();
        processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
    }

    public class MyImageAnalysis implements ImageAnalysis.Analyzer {

        private FragmentManager fragmentManager;

        public MyImageAnalysis(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;

        }

        @Override
        public void analyze(@NonNull ImageProxy image) {
            scanBarcode(image);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void scanBarcode(ImageProxy image) {

            @SuppressLint("UnsafeOptInUsageError") Image image1 = image.getImage();
            assert image1 != null;
            InputImage inputImage = InputImage.fromMediaImage(image1, image.getImageInfo().getRotationDegrees());

            BarcodeScannerOptions options =
                    new BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_AZTEC, Barcode.FORMAT_DATA_MATRIX, Barcode.FORMAT_PDF417,
                                    Barcode.FORMAT_UPC_E, Barcode.FORMAT_UPC_A, Barcode.FORMAT_ITF, Barcode.FORMAT_EAN_8,
                                    Barcode.FORMAT_EAN_13, Barcode.FORMAT_CODABAR, Barcode.FORMAT_CODE_93,
                                    Barcode.FORMAT_CODE_39, Barcode.FORMAT_EAN_8)
                            .build();

            com.google.mlkit.vision.barcode.BarcodeScanner scanner = BarcodeScanning.getClient(options);

            Task<List<Barcode>> result = scanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            readerBarcodeData(barcodes);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Barcode>> task) {
                            image.close();
                        }
                    });
        }

        private void readerBarcodeData(List<Barcode> barcodes) {
            for (Barcode barcode : barcodes) {
                Rect bounds = barcode.getBoundingBox();
                Point[] corners = barcode.getCornerPoints();

                String rawValue = barcode.getRawValue();
                counter++;
                search(rawValue, counter);
            }
        }

    }

    public void search(String rawValue,  int counter){
        if(counter > 1){return;}

        Toast.makeText(getApplicationContext(), "Barcode ->"+rawValue, Toast.LENGTH_SHORT).show();
        Intent search = new Intent(getApplicationContext(), RegisteredUserResultPage.class);
        search.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        search.putExtra("source", "ByBarcode");
        search.putExtra("Barcode", rawValue.trim());
        startActivity(search);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

}