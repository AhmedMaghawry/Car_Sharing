package com.tarek.carsharing.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tarek.carsharing.Control.Utils;
import com.tarek.carsharing.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class SignupImageActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;  //to save the image

    private ImageView imageView;   // Image  icon
    private Button nextBtn, skipBtn;

    private Uri uriProfileImage;      // to save  the image  type
    // uniform resources identifier image storage
    private String profileImageUrl;  // UrL of the  photo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup_image);

        imageView = findViewById(R.id.imageView);
        nextBtn = findViewById(R.id.ButtonNext);
        skipBtn = findViewById(R.id.ButtonSkip);

        nextBtn.setOnClickListener(new View.OnClickListener() {  // when clicking the  next button
            @Override
            public void onClick(View view) {
                if (profileImageUrl != null) { // if  uploaded the  picture
                    Intent prev = getIntent(); //all the previous data in the signup sequence
                    String email = prev.getStringExtra("email");
                    int age = prev.getIntExtra("age", 0);
                    String name = prev.getStringExtra("name");
                    String phone = prev.getStringExtra("phone");

                    Intent intent = new Intent(SignupImageActivity.this, SignupVerificationActivity.class); //mina's activity
                    intent.putExtra("image", profileImageUrl);
                    intent.putExtra("name", name);
                    intent.putExtra("age", age);
                    intent.putExtra("phone", phone);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {// if photo not uploaded/choosen
                    Toast.makeText(SignupImageActivity.this,"Failed to set the image. Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {  // when clicking the  next button
            @Override
            public void onClick(View view) {
                    Intent prev = getIntent(); //all the previous data in the signup sequence
                    String email = prev.getStringExtra("email");
                    int age = prev.getIntExtra("age", 0);
                    String name = prev.getStringExtra("name");
                    String phone = prev.getStringExtra("phone");

                    Intent intent = new Intent(SignupImageActivity.this, SignupVerificationActivity.class); //mina's activity
                    intent.putExtra("image", "");
                    intent.putExtra("name", name);
                    intent.putExtra("age", age);
                    intent.putExtra("phone", phone);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) { showImageChooser(); }  //call method
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check for the image selected
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage(); // upload to firebase the picture

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadImageToFirebaseStorage() {
        // storage
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg"); // in database folder profilepics
        //System.currentTimeMillis(), is random sequence done  by getting time in millis
        if (uriProfileImage != null) { // upload
            Utils.showLoading(this);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        // check uplod successful or not
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Utils.hideLoading();
                            Toast.makeText(SignupImageActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString(); // get the url as user informations
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) { // fail to opload
                            Utils.hideLoading();
                            Toast.makeText(SignupImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    //  selects image of the user

    /* */
    private void showImageChooser() { // create new image  intent ( user select the photo required from gallery)
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); // get the image
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE); //save in CHOOSE_IMAGE
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(SignupImageActivity.this, "Profile Destroy", Toast.LENGTH_SHORT).show();
        Utils.hideLoading();
    }
}