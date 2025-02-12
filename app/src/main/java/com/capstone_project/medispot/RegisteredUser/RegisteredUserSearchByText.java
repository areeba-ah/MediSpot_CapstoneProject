package com.capstone_project.medispot.RegisteredUser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone_project.medispot.NavigationUtility.BottomNavigation;
import com.capstone_project.medispot.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class RegisteredUserSearchByText extends AppCompatActivity{

    TextInputEditText text;
    ImageView drugImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registered_user_search_by_text);

        drugImage = findViewById(R.id.image);
        text = findViewById(R.id.textSearch);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        BottomNavigation.setupBottomNavigation(this, bottomNavigationView, -1);

        Intent intent = getIntent();
        String URI =  intent.getStringExtra("uri");

        Uri uri = Uri.parse(URI);
        Picasso.get().load(uri).into(drugImage);

        getText(getApplicationContext(),uri);

    }

    public void getText(Context context, Uri uri){

        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        try{
            InputImage image = InputImage.fromFilePath(context, uri);

            Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text visionText) {
                                    text.setText(visionText.getText());

                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });
        }catch (IOException e){e.printStackTrace();};


    }

    public void cancel(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisteredUserHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void search(View view) {

        String Text = text.getText().toString().trim();
        Intent search = new Intent(getApplicationContext(), RegisteredUserResultPage.class);
        search.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        search.putExtra("source", "ByText");
        search.putExtra("Text", Text);
        startActivity(search);

    }

}