package com.tarek.carsharing.View;


import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.tarek.carsharing.Control.Utils;
import com.tarek.carsharing.Control.onAction;
import com.tarek.carsharing.Model.Car;
import com.tarek.carsharing.Model.Trip;
import com.tarek.carsharing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tarek.carsharing.Control.MainAdapter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {


    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Trip> allTrips = new ArrayList<>();
    ArrayList<Car> car1  = new ArrayList<>();
    public String carid;
    Geocoder geocoder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Utils.showLoading(HistoryActivity.this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter= new MainAdapter(allTrips,car1, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        geocoder = new Geocoder(this, Locale.getDefault());

        first(new onAction() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish(Object o) {
                mohamed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Utils.hideLoading();
    }

    public void first (final onAction action) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot trips : dataSnapshot.getChildren()){

                    Trip tripsHistory = trips.getValue(Trip.class);

                    //Log.i("mohamed", tripsHistory.getCarid());


                    String loc = tripsHistory.getStart();
                    String locs[] = loc.split(",");
                    String loc1 = tripsHistory.getEnd();
                    String locs1[] = loc1.split(",");
                    try {


                        String start = geocoder.getFromLocation(Double.parseDouble(locs[0].trim()), Double.parseDouble(locs[1].trim()),1).get(0).getAddressLine(0);
                        String end = geocoder.getFromLocation(Double.parseDouble(locs1[0].trim()), Double.parseDouble(locs1[1].trim()),1).get(0).getAddressLine(0);
                    tripsHistory.setStart(start);
                    tripsHistory.setEnd(end);
                        allTrips.add(tripsHistory);
                    }catch(IOException ex) {
                        //Do something with the exception
                    }


                    Log.i("mohamed","1");
                }

                Collections.reverse(allTrips);

                Log.i("mohamed","4");


                Log.i("mohamed","5");
                action.onFinish(null);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("mohamed","6");
                Toast.makeText(HistoryActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }




     // Here 1 represent max location result to returned, by documents it recommended 1 to 5



    protected  void mohamed() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference("Cars");
        mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int j = 0; j < allTrips.size(); j++) {
                    carid = allTrips.get(j).getCarid();
                    Toast.makeText(HistoryActivity.this, "dddd "+carid, Toast.LENGTH_SHORT).show();
                    Car cars = dataSnapshot.child(carid).getValue(Car.class);
                    car1.add(cars);

                }
                Utils.hideLoading();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}