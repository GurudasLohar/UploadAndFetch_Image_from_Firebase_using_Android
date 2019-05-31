package com.example.firebaeimageapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSelect,btnSave,btnLoad;
    ImageView imgImage;
    EditText txtImage;
    ProgressBar progressImage;

    private static final int IMAGE_REQUEST = 1;
    private Uri uriImage;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("Images");
        storageReference = FirebaseStorage.getInstance().getReference("Images");

        imgImage = findViewById(R.id.imgImage);
        txtImage = findViewById(R.id.txtImage);
        progressImage = findViewById(R.id.progressImage);
        btnSelect = findViewById(R.id.btnSelect);
        btnSave = findViewById(R.id.btnSave);
        btnLoad = findViewById(R.id.btnLoad);

        btnSelect.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnLoad.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnSelect:  selectFile();
                break;

            case R.id.btnSave:
                if(uploadTask!=null && uploadTask.isInProgress()){
                    Toast.makeText(this, "Uploading in progress", Toast.LENGTH_SHORT).show();
                }else {
                    saveFile();
                }
                break;

            case R.id.btnLoad:
                    Intent intent = new Intent(MainActivity.this,imageLoader.class);
                    startActivity(intent);
                break;
        }
    }

    private void selectFile() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            uriImage = data.getData();
            Picasso.with(MainActivity.this).load(uriImage).into(imgImage);
        }
    }

    public String getFileExtension(Uri uriImage){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uriImage));
    }

    private void saveFile() {

        final String imageName = txtImage.getText().toString().trim();

        if(imageName.isEmpty()){
            txtImage.setError("Please enter the Image name");
            txtImage.requestFocus();
            return;
        }

        StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(uriImage));

        reference.putFile(uriImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Toast.makeText(MainActivity.this, "Image saved successfully", Toast.LENGTH_SHORT).show();

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!urlTask.isSuccessful());
                        Uri downloadUrl = urlTask.getResult();

                        uploadImage uploadImage = new uploadImage(imageName,downloadUrl.toString());
                        String uploadID = databaseReference.push().getKey();
                        databaseReference.child(uploadID).setValue(uploadImage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(MainActivity.this, "Image failed to Save", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
