package com.tarek.carsharing.View;


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


import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {


    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Trip> allTrips = new ArrayList<>();
    ArrayList<Car> car1  = new ArrayList<>();
    public String carid;


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
        Toast.makeText(HistoryActivity.this, "Main A 11 Destroy", Toast.LENGTH_SHORT).show();
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

                    allTrips.add(tripsHistory);

                    Log.i("mohamed","1");
                }

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