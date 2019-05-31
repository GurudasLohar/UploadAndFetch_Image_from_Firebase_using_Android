package com.example.firebaeimageapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class imageLoader extends AppCompatActivity {

    private RecyclerView recyclerView;
    private imageAdapter imageAdapter;
    private List<uploadImage> uploadImageList;
    private ProgressBar progressBar;
    private FirebaseStorage firebaseStorage;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_loader);

        progressBar = findViewById(R.id.progressImageLoader);

        recyclerView = findViewById(R.id.recyclerViewImage);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        uploadImageList = new ArrayList<>();

        firebaseStorage = FirebaseStorage.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("Images");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                uploadImageList.clear();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    uploadImage uploadImage = dataSnapshot1.getValue(com.example.firebaeimageapp.uploadImage.class);
                    uploadImage.setImageKey(dataSnapshot1.getKey());
                    uploadImageList.add(uploadImage);
                }
                imageAdapter = new imageAdapter(imageLoader.this,uploadImageList);
                recyclerView.setAdapter(imageAdapter);

                imageAdapter.setOnItemClickListener(new imageAdapter.onItemClickListenerNew() {
                    @Override
                    public void onItemClick(int position) {
                        //String text = uploadImageList.get(position).getImageName();
                        String ImageId = uploadImageList.get(position).getImageKey();
                        Intent intent = new Intent(getApplicationContext(),imageDetails.class);
                        intent.putExtra("ImgId",ImageId);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancel(int position) {
                        Toast.makeText(imageLoader.this, "Cancel is selected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDelete(int position) {

                        uploadImage selectID = uploadImageList.get(position);
                        final String deleteKey = selectID.getImageKey();
                        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(selectID.getImageUrl());

                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child(deleteKey).removeValue();
                                Toast.makeText(imageLoader.this, "Image delete successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(imageLoader.this, "Error : "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
