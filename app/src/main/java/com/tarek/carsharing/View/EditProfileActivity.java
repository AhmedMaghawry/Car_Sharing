package com.tarek.carsharing.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tarek.carsharing.Control.Utils;
import com.tarek.carsharing.Model.User;
import com.tarek.carsharing.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener   {


    private static final int CHOOSE_IMAGE1 = 1001;

    private Uri uriProfileImage;
    private String profileImageUrl1;

    private ImageView ivProfilePic;
    private TextView profileName, profileAge,ivNumber;
    private Button btnProfileUpdate;
    private User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);



        profileName = (TextView) findViewById(R.id.tvProfileName);
        profileAge = (TextView) findViewById(R.id.tvProfileAge);
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        btnProfileUpdate = (Button) findViewById(R.id.btnProfileUpdate);
        ivNumber=(TextView) findViewById(R.id.ivNumber);
        findViewById(R.id.btnProfileUpdate).setOnClickListener(this);

        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Users");

        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User userProfileData = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(User.class);
                profileName.setText(userProfileData.getName());
                profileAge.setText(userProfileData.getAge()+"");
                ivNumber.setText(userProfileData.getPhone());
                try {
                    Picasso.with(getApplication()).load(userProfileData.getImage())
                            .placeholder(R.drawable.pla)
                            .into(ivProfilePic);
                } catch (Exception e) {
                    Picasso.with(getApplication())
                            .load(R.drawable.pla)
                            .into(ivProfilePic);
                }

                user = userProfileData;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });



        ivProfilePic.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                showImageChooser();           // method permit user to select image
            }
        });

        //loadUserInformation();

        findViewById(R.id.btnProfileUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUser();
            }
        });
    }

//_____________________________________________________________________


    private void editUser() {

        String name = encrypt(profileName.getText().toString());
        String old  = profileAge.getText().toString();
        String number = ivNumber.getText().toString();

        if((TextUtils.isEmpty(name)) || (TextUtils.isEmpty(old))  || (TextUtils.isEmpty(number)) )
     {
            Toast.makeText(this, "please Fill all the information needed", Toast.LENGTH_LONG).show();
        }
        else {
            if (Integer.parseInt(old) >= 70 || Integer.parseInt(old) <= 18 || (Integer.parseInt(number) / 10) == 0) //check for age validation
            {
                Toast.makeText(this, "please enter a valid age", Toast.LENGTH_LONG).show();
                profileAge.requestFocus();
            }
            else { //check for number validation
                if (number.length() != 11) {
                    Toast.makeText(this, "please enter a valid phone number", Toast.LENGTH_LONG).show();
                    ivNumber.requestFocus();
                }
                else {
                    user.setAge(Integer.parseInt(old));
                    user.setName(name);
                    user.setPhone(number);
                    if (profileImageUrl1 != null) {
                        user.setImage(profileImageUrl1);
                    }
                    else {

                    }
                    user.updateUser();
                    Toast.makeText(this, "information edited ", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(this, ProfileActivity.class));
                }
            }
        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        // check for the image selected
        if (requestCode == CHOOSE_IMAGE1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                ivProfilePic.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


// uplods to firebase the image

    /* */
    private void uploadImageToFirebaseStorage() {
        // storage


        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis()  + ".jpg");
        // system.currenttimemillis , provide unique name
        // 3andy image
        if (uriProfileImage != null) {
            Utils.showLoading(this);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        // check uplod successful or not
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EditProfileActivity.this, "Edit Prof", Toast.LENGTH_SHORT).show();
                            Utils.hideLoading();
                            profileImageUrl1 = taskSnapshot.getDownloadUrl().toString(); // get the url as user informations


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utils.hideLoading();
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    //  selects image of the user

    /* */
    private void showImageChooser() {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); // get the image
        startActivityForResult(Intent.createChooser(intent,"profile picture update"), CHOOSE_IMAGE1);


    }

    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";

    public static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeToString(encrypted,0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(encrypted,0));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }



    @Override // same as in main 1 buttonsignup  --> methods register user      //sign up
    //                   textviewlogin --> end acti2, start active 1  // login
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnProfileUpdate:
                editUser();
                break;


        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.hideLoading();
    }


}