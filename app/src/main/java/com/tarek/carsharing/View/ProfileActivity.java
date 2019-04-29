package com.tarek.carsharing.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.tarek.carsharing.Control.Utils;
import com.tarek.carsharing.Model.User;
import com.tarek.carsharing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;




public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView profilePic;
    private TextView profileName, profileAge, profileEmail,ivNumber;
    private Button btnProfileUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Utils.showLoading(this);
        profileName = (TextView) findViewById(R.id.tvProfileName);
        profileAge = (TextView) findViewById(R.id.tvProfileAge);
        profileEmail = (TextView) findViewById(R.id.tvProfileEmail1);
        profilePic = (ImageView) findViewById(R.id.ivProfilePic);
        btnProfileUpdate = (Button) findViewById(R.id.btnProfileUpdate);
        ivNumber = (TextView) findViewById(R.id.ivNumber);


        btnProfileUpdate.setOnClickListener(this);

        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Users");

        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User userProfileData = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(User.class);

                profileName.setText(userProfileData.getName());
                profileAge.setText(userProfileData.getAge()+"");
                profileEmail.setText(userProfileData.getEmail());
                ivNumber.setText(userProfileData.getPhone());
                Picasso.with(getApplication()).load(userProfileData.getImage())
                        .into(profilePic);
                Utils.hideLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                Utils.hideLoading();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnProfileUpdate:
                finish();
                startActivity(new Intent(this, EditProfileActivity.class));
                break;

        }
    }

}