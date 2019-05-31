package com.example.firebaeimageapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class imageDetails extends AppCompatActivity {

    ImageView detailsImage;
    TextView detailsName,detailsInfo;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        databaseReference = FirebaseDatabase.getInstance().getReference("Images");

        detailsImage = findViewById(R.id.detailsImage);
        detailsName = findViewById(R.id.detailsName);
        detailsInfo = findViewById(R.id.detailsInfo);

        String FireId = getIntent().getStringExtra("ImgId");
        detailsName.setText(FireId);
    }
}
