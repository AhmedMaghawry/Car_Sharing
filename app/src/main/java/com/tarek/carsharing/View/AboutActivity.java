package com.tarek.carsharing.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tarek.carsharing.Model.Car;
import com.tarek.carsharing.R;

public class AboutActivity extends AppCompatActivity {
private Button bn;
public Car tarek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        bn = (Button) findViewById(R.id.toggle);
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Cars").child("12346");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Car tarek = dataSnapshot.getValue(Car.class);
                    Intent intent = getIntent();
                    tarek = (Car) intent.getSerializableExtra("car");

                    tarek.setToggle("HAHA");


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            }
        });
    }
}
