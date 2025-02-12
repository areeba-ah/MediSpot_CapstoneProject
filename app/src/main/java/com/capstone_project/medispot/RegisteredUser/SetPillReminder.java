package com.capstone_project.medispot.RegisteredUser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.capstone_project.medispot.Databases.SharedUserPreferences;
import com.capstone_project.medispot.HelperClasses.AlarmReceiver;
import com.capstone_project.medispot.NavigationUtility.BottomNavigation;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SetPillReminder extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView_type;
    AutoCompleteTextView autoCompleteTextView_inst;
    AutoCompleteTextView autoCompleteTextView_schd;
    AppCompatButton setReminder;
    TimePicker timePicker;
    Calendar calendar;
    TextInputLayout medication_name, quantity, type, schedule, instructions;
    TextInputEditText med_name, med_quantity;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentView;
    ImageView menuIcon;
    TextView textView, textTime, current_date;
    ImageView image;
    ImageView icon;
    Uri resultUri;
    String ImgURL;
    String format = "";

    FirebaseFirestore db;
    StorageReference storageReference;

    int imageSize = 224;

    private static final int CROP_IMAGE_REQUEST = 2; // Custom request code for crop

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pill_reminder);

        setReminder = findViewById(R.id.add_reminder);
        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.nav_view);
        contentView = findViewById(R.id.recycler_layout);
        menuIcon = findViewById(R.id.menu);
        autoCompleteTextView_type = findViewById(R.id.dropdown_types);
        autoCompleteTextView_inst = findViewById(R.id.dropdown_inst);
        autoCompleteTextView_schd = findViewById(R.id.dropdown_schd);
        textView = findViewById(R.id.dropdown_med);
        image = findViewById(R.id.medicine_pic);
        icon = findViewById(R.id.take_pic);
        medication_name =findViewById(R.id.medication_name);
        med_name =findViewById(R.id.med_name);
        med_quantity =findViewById(R.id.med_quantity);
        quantity =findViewById(R.id.quantity);
        type = findViewById(R.id.med_type);
        schedule = findViewById(R.id.medication_instruction);
        instructions = findViewById(R.id.schedule);

        timePicker = (TimePicker) findViewById(R.id.time);
        textTime = (TextView) findViewById(R.id.timeText);
        current_date = (TextView) findViewById(R.id.date);

        db = FirebaseFirestore.getInstance();


        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean pick = true;
                if(pick){
                    if(!checkCameraPermission())
                        requestCameraPermission();
                    else pickImage();

                }else{
                    if(!checkGalleryPermission())
                        requestGalleryPermission();
                    else pickImage();
                }
            }
        });

        Resources res = getResources();
        String[] medication_types = res.getStringArray(R.array.med_types);
        String[] medication_instructions = res.getStringArray(R.array.med_instructions);
        String[] medication_schedule = res.getStringArray(R.array.med_schedule);

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<> (SetPillReminder.this, R.layout.dropdown_med,medication_types);
        autoCompleteTextView_type.setAdapter(itemAdapter);

        ArrayAdapter<String> instructionAdapter = new ArrayAdapter<> (SetPillReminder.this, R.layout.dropdown_med,medication_instructions);
        autoCompleteTextView_inst.setAdapter(instructionAdapter);

        ArrayAdapter<String> scheduleAdapter = new ArrayAdapter<> (SetPillReminder.this, R.layout.dropdown_med,medication_schedule);
        autoCompleteTextView_schd.setAdapter(scheduleAdapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, -1);

        createNotificationChannel();


        calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        showTime(hour, min);


    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "MediSpot";
            String description = "Alarm manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("android", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private boolean checkCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return result1 && result2;
    }

    private void requestCameraPermission() {
        requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkGalleryPermission() {
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return result2;
    }

    private void requestGalleryPermission() {
        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // If you want to select an image from the gallery and crop it
            Uri selectedImageUri = data.getData();
            cropImage(selectedImageUri); // Call the method to crop the selected image
        }

        if (requestCode == CROP_IMAGE_REQUEST && resultCode == RESULT_OK) {
            // Handle the result of the cropping
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap croppedImage = extras.getParcelable("data");

                // Ensure the cropped image is square (optional, based on your app's requirement)
                int dimension = Math.min(croppedImage.getWidth(), croppedImage.getHeight());
                croppedImage = ThumbnailUtils.extractThumbnail(croppedImage, dimension, dimension);

                // Optionally, resize the image (for your use case, imageSize can be a constant like 300px)
                croppedImage = Bitmap.createScaledBitmap(croppedImage, imageSize, imageSize, false);

                // Now, you can use the cropped image (for example, uploading to Firebase or displaying in the UI)
                Picasso.get().load(getImageUri(this, croppedImage)).into(image); // Loading the cropped image into ImageView
            }
        }
    }

    private void pickImage() {
        // Open the gallery to select an image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1); // Request code 1 to pick the image
    }

    private void cropImage(Uri selectedImageUri) {
        // Using Android's built-in crop functionality (you can use other cropping libraries as well)
        Intent cropIntent = new Intent("com.android.activity.CROP");
        cropIntent.setDataAndType(selectedImageUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1); // Square aspect ratio (optional)
        cropIntent.putExtra("outputX", 500); // Max output width
        cropIntent.putExtra("outputY", 500); // Max output height
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, CROP_IMAGE_REQUEST); // CROP_IMAGE_REQUEST is a request code for cropping
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        // Convert Bitmap to Uri for Picasso to load it
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "cropped_image", null);
        return Uri.parse(path);
    }

    public void addReminder(View view){

        String medication = med_name.getText().toString().trim();
        String quantity = med_quantity.getText().toString().trim();
        String type = autoCompleteTextView_type.getText().toString();
        String instructions = autoCompleteTextView_inst.getText().toString();
        String schedule = autoCompleteTextView_schd.getText().toString();

        if(!validateImage()| !validateDrugName(medication) | !validatequantity(quantity) |  !validateDrugType(type)
            | !validateDrugInst(instructions) | !validateDrugSchedule(schedule)){return;}

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();

        String phone = userPhone.get(SharedUserPreferences.Phone);


        SimpleDateFormat formatter = new SimpleDateFormat("hhmmssms", Locale.getDefault());

        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("Pill Reminders/").child(phone).child(fileName);
        storageReference.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ImgURL = uri.toString();
                                add(ImgURL, fileName);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void add(String ImgURL, String filename) {

        int hour = timePicker.getHour();
        int min = timePicker.getMinute();

        showTime(hour, min);

        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        Date current = new Date();
        String DATE = date.format(current);
        current_date.setText(DATE);

        String medication = med_name.getText().toString().trim();
        String quantity = med_quantity.getText().toString().trim();
        String type = autoCompleteTextView_type.getText().toString();
        String instructions = autoCompleteTextView_inst.getText().toString();
        String schedule = autoCompleteTextView_schd.getText().toString();
        String Time = textTime.getText().toString();
        String Current_Date = current_date.getText().toString();

        Map<String, Object> Reminder = new HashMap<>();
        Reminder.put("DrugName", medication);
        Reminder.put("DrugQuantity", quantity);
        Reminder.put("DrugType", type);
        Reminder.put("DrugInstructions", instructions);
        Reminder.put("DrugSchedule", schedule);
        Reminder.put("DrugImgURL", ImgURL);
        Reminder.put("DrugImgName", filename);
        Reminder.put("Time", Time);
        Reminder.put("Date", Current_Date);
        Reminder.put("ReferenceID", "");
        Reminder.put("Timestamp", FieldValue.serverTimestamp());

        SharedUserPreferences sharedUserPreferences = new SharedUserPreferences(this, SharedUserPreferences.isLoggedIn);
        HashMap<String, String> userPhone = sharedUserPreferences.getSessionInfo();

        String phone = userPhone.get(SharedUserPreferences.Phone);

        db.collection("Drug_Reminders").document(phone).collection("UserCollection")

                .add(Reminder)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        String id = documentReference.getId();
                        documentReference.update("ReferenceID", id, "Timestamp", FieldValue.serverTimestamp());

                        startAlarm(hour,min, filename, schedule);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to send", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void showTime(int hour, int min) {

        if(hour == 0){ hour += 12; format = "AM";}
        else if (hour == 12){ format = "PM";}
        else if (hour >12){hour -= 12; format = "PM";}
        else {format = "AM";}

        if(min <10)
        {textTime.setText(new StringBuilder().append(hour).append(":").append("0" + min).append(" ").append(format));}

        else
        {textTime.setText(new StringBuilder().append(hour).append(":").append(min).append(" ").append(format));}
    }

    private void  startAlarm(int hour, int min, String filename, String schedule){

        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY,hour);
        c.set(Calendar.MINUTE,min);
        c.set(Calendar.SECOND, 0);
        int requestCode = Integer.parseInt(filename);

        if(schedule.equals("Everyday")){
            if (c.getTime().compareTo(new Date()) < 0)
            {c.add(Calendar.DAY_OF_MONTH, 1);}
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,requestCode,intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        else {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

            if (c.before(Calendar.getInstance())) {
                c.add(Calendar.DATE, 1);
            }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
        changePage();
    }

    private void changePage(){
        Intent intent = new Intent(getApplicationContext(), PillReminderPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Reminder set", Toast.LENGTH_SHORT).show();
    }

    public boolean validateImage(){

        if (resultUri == null) {
            Toast.makeText(getApplicationContext(), "Please provide an Image!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean validateDrugName(String medication){
        medication_name =findViewById(R.id.medication_name);

        if (medication.isEmpty()) {
            medication_name.setError("This is required!!");
            return false;

        }else {
            medication_name.setError(null);
            medication_name.setErrorEnabled(false);
            return true;
        }

    }

    public boolean validatequantity(String Quantity){
        quantity = findViewById(R.id.quantity);

        if (Quantity.isEmpty()) {
            quantity.setError("This is required!!");
            return false;

        }else {
            quantity.setError(null);
            quantity.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validateDrugType(String Type){
        type = findViewById(R.id.med_type);

        if(Type.isEmpty()){
            type.setError("This is required!!");
            return false;

        }
        type.setError(null);
        type.setErrorEnabled(false);
        return true;

    }

    public boolean validateDrugInst(String Instructions){
        instructions = findViewById(R.id.schedule);

        if(Instructions.isEmpty()){
            instructions.setError("This is required!!");
            return false;

        }
        instructions.setError(null);
        instructions.setErrorEnabled(false);
        return true;

    }

    public boolean validateDrugSchedule(String Schedule){
        schedule = findViewById(R.id.medication_instruction);

        if(Schedule.isEmpty()){
            schedule.setError("This is required!!");
            return false;

        }
        schedule.setError(null);
        schedule.setErrorEnabled(false);
        return true;

    }

    public void back_btn(View view) {
        super.onBackPressed();

    }

    public void cancel(View view) {
        Intent intent = new Intent(getApplicationContext(), PillReminderPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}