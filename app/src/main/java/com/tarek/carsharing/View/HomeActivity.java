package com.tarek.carsharing.View;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tarek.carsharing.Control.Utils;
import com.tarek.carsharing.Model.Car;
import com.tarek.carsharing.Model.CarStatus;
import com.tarek.carsharing.Model.User;
import com.tarek.carsharing.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {
    //(view, image ,text)--> 3 1 for every car
    View box1;      // linear layout
    View box2;
    View box3;
    ImageView carImage1;  // image of the car
    ImageView carImage2;
    ImageView carImage3;
    TextView carModel1;  // type of the  car
    TextView carModel2;
    TextView carModel3;
    TextView carColor1;  //color of the car
    TextView carColor2;
    TextView carColor3;
    TextView plateNumber1; // plate number of the car
    TextView plateNumber2;
    TextView plateNumber3;
    ImageView nav_imageView;  // picture in navigation view (user picture)
    TextView nav_rate;
    TextView nav_name;// name in navigator view  ( user name )
    ArrayList<Car> allCars = new ArrayList<>(); // arraylist takes "car" dynamcaly changing

    protected LocationManager locationManager;     // loza
    protected LocationListener locationListener;  // loza
    protected double myLat = 0;    //default latitude
    protected double myLong = 0;   // default  longititude
    private static DecimalFormat df2 = new DecimalFormat("#.#");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.showLoading(HomeActivity.this);
        setContentView(R.layout.activity_home);

        //**************************** opening home
        // id of each box
        box1 = findViewById(R.id.f1);
        box2 = findViewById(R.id.f2);
        box3 = findViewById(R.id.f3);
        carImage1 = box1.findViewById(R.id.ivProfilePic);
        carImage2 = box2.findViewById(R.id.ivProfilePic);
        carImage3 = box3.findViewById(R.id.ivProfilePic);
        carModel1 = box1.findViewById(R.id.name);
        carModel2 = box2.findViewById(R.id.name);
        carModel3 = box3.findViewById(R.id.name);
        carColor1 = box1.findViewById(R.id.color);
        carColor2 = box2.findViewById(R.id.color);
        carColor3 = box3.findViewById(R.id.color);
        plateNumber1 = box1.findViewById(R.id.carNumber);
        plateNumber2 = box2.findViewById(R.id.carNumber);
        plateNumber3 = box3.findViewById(R.id.carNumber);



        //Toast.makeText(this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();

        try {  // loza
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } catch (Exception io) {
            //checkLoc();
            io.printStackTrace();
        }


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Cars"); // car database
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // for  loop to get all cars information and  added to allCars arraylist
                for(DataSnapshot carData : dataSnapshot.getChildren()){

                    final Car carsInformation = carData.getValue(Car.class);
                    if (carsInformation.getStatus() != CarStatus.ON ) {

                        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Users");

                        mData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                              User  currentUser = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(User.class);
                                if((currentUser.getRate()>3)&&currentUser.getActivation())
                                    allCars.add(carsInformation);
                                else if((currentUser.getRate()>1)&&currentUser.getActivation()){
                                    if(carsInformation.getType().equals("Lada"))
                                    {

                                        allCars.add(carsInformation);
                                    }

                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(HomeActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                }

                updateUI(); // loza
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        nav_imageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        nav_name = navigationView.getHeaderView(0).findViewById(R.id.name);
        nav_rate = navigationView.getHeaderView(0).findViewById(R.id.rating);
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User userProfileData = dataSnapshot.getValue(User.class);

                nav_name.setText(userProfileData.getName());
                nav_rate.setText(df2.format(userProfileData.getRate())+"");
                try {
                    Picasso.with(getApplication()).load(userProfileData.getImage())
                            .placeholder(R.drawable.pla)
                            .into(nav_imageView);
                } catch (Exception e) {
                    Picasso.with(getApplication())
                            .load(R.drawable.pla)
                            .into(nav_imageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void startMap(Car cars) {
        Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
        intent.putExtra("car", cars);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_profile) {

            startActivity(new Intent(this, ProfileActivity.class));

        }  else if (id == R.id.nav_history) {

            startActivity(new Intent(this, HistoryActivity.class));

        } else if (id == R.id.nav_about) {

            startActivity(new Intent(this, AboutActivity.class));

        } else if (id == R.id.nav_contact) {

            startActivity(new Intent(this, ContactUsActivity.class));

        }
        else if (id == R.id.nav_promocode){
            startActivity(new Intent(this, Promocode.class));


        }

        else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private double distance(double lat1, double lat2, double lon1,
                            double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);

    }

    @Override
    public void onLocationChanged(Location location) {
        myLat = location.getLatitude();
        myLong = location.getLongitude();
        updateUI();
    }

    private void updateUI() {

        int firstmin = Integer.MAX_VALUE;
        int secmin = Integer.MAX_VALUE;
        int thirdmin = Integer.MAX_VALUE;
        int firstminIndex = 0;
        int secminIndex = 0;
        int thirdminIndex = 0;

        for (int i = 0 ; i < allCars.size(); i++)
        {
                /* Check if current element is less than
                firstmin, then update first, second and
                     third */

            String loc = allCars.get(i).getLocation();
            String[] locs = loc.split(",");

            double dist = distance(Double.valueOf(locs[0].trim()), myLat,Double.valueOf(locs[1].trim()), myLong,0.0,0.0);


            if (((int)dist) < firstmin)
            {
                thirdmin = secmin;
                secmin = firstmin;
                firstmin = (int)dist;
                firstminIndex = i;
            }

                /* Check if current element is less than
                secmin then update second and third */
            else if (((int)dist) < secmin)
            {
                thirdmin = secmin;
                secmin = ((int)dist);
                secminIndex = i;
            }

                /* Check if current element is less than
                then update third */
            else if (((int)dist) < thirdmin) {
                thirdmin = ((int) dist);
                thirdminIndex = i;
            }
            else{

            }
        }



        if (allCars.size() >= 3) {
            String trial1 = allCars.get(firstminIndex).getImage();
            String trial2 = allCars.get(secminIndex).getImage();
            String trial3 = allCars.get(thirdminIndex).getImage();

            Picasso.with(getApplication()).load(trial1)
                    .into(carImage1);
            carModel1.setText(allCars.get(firstminIndex).getType());
            carColor1.setText(allCars.get(firstminIndex).getColor());
            plateNumber1.setText(allCars.get(firstminIndex).getNumber());


            Picasso.with(getApplication()).load(trial2)
                    .into(carImage2);
            carModel2.setText(allCars.get(secminIndex).getType());
            carColor2.setText(allCars.get(secminIndex).getColor());
            plateNumber2.setText(allCars.get(secminIndex).getNumber());

            Picasso.with(getApplication()).load(trial3)
                    .into(carImage3);
            carModel3.setText(allCars.get(thirdminIndex).getType());
            carColor3.setText(allCars.get(thirdminIndex).getColor());
            plateNumber3.setText(allCars.get(thirdminIndex).getNumber());

            final int finalFirstminIndex = firstminIndex;
            box1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMap(allCars.get(finalFirstminIndex));
                }
            });

            final int finalSecminIndex = secminIndex;
            box2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMap(allCars.get(finalSecminIndex));
                }
            });

            final int finalThirdminIndex = thirdminIndex;
            box3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMap(allCars.get(finalThirdminIndex));
                }
            });
            box1.setVisibility(View.VISIBLE);
            box2.setVisibility(View.VISIBLE);
            box3.setVisibility(View.VISIBLE);
        } else if (allCars.size() == 2) {
            String trial1 = allCars.get(firstminIndex).getImage();
            String trial2 = allCars.get(secminIndex).getImage();

            Picasso.with(getApplication()).load(trial1)
                    .into(carImage1);
            carModel1.setText(allCars.get(firstminIndex).getType());
            carColor1.setText(allCars.get(firstminIndex).getColor());
            plateNumber1.setText(allCars.get(firstminIndex).getNumber());


            Picasso.with(getApplication()).load(trial2)
                    .into(carImage2);
            carModel2.setText(allCars.get(secminIndex).getType());
            carColor2.setText(allCars.get(secminIndex).getColor());
            plateNumber2.setText(allCars.get(secminIndex).getNumber());

            final int finalFirstminIndex = firstminIndex;
            box1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMap(allCars.get(finalFirstminIndex));
                }
            });

            final int finalSecminIndex = secminIndex;
            box2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMap(allCars.get(finalSecminIndex));
                }
            });
            box1.setVisibility(View.VISIBLE);
            box2.setVisibility(View.VISIBLE);
            box3.setVisibility(View.GONE);
        } else if (allCars.size() == 1) {
            String trial1 = allCars.get(firstminIndex).getImage();

            Picasso.with(getApplication()).load(trial1)
                    .into(carImage1);
            carModel1.setText(allCars.get(firstminIndex).getType());
            carColor1.setText(allCars.get(firstminIndex).getColor());
            plateNumber1.setText(allCars.get(firstminIndex).getNumber());
            final int finalFirstminIndex = firstminIndex;
            box1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMap(allCars.get(finalFirstminIndex));
                }
            });
    box1.setVisibility(View.VISIBLE);
            box2.setVisibility(View.GONE);
            box3.setVisibility(View.GONE);


        } else {
            box1.setVisibility(View.GONE);
            box2.setVisibility(View.GONE);
            box3.setVisibility(View.GONE);

        }

        Utils.hideLoading();
    }

    @Override
    public void onProviderDisabled(String provider) {
        checkLoc();

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private void checkLoc() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Enable GPS provider")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            checkLoc();
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissionsLoc1();
        checkPermissionsLoc2();
        //checkPermissionsCam();
        //checkLoc();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //checkLoc();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkLoc();
    }

    private void checkPermissionsLoc1() {

        if  (   // check for permissions ( bluetooth /  Camera / location
                ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            //  when if is true it gives means all permissions are available
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else { // get permession from user
            Toast.makeText(HomeActivity.this, "Loc1 Permision already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionsLoc2() {

        if  (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
        else { // get permession from user
            Toast.makeText(HomeActivity.this, "Loc2 Permision already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionsCam() {

        if  ( ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 3);
        }
        else { // get permession from user
            Toast.makeText(HomeActivity.this, "Cam Permision already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(HomeActivity.this, "Location 1  Good", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                } else {
                    Toast.makeText(HomeActivity.this, "Location 1  Bad", Toast.LENGTH_SHORT).show();
                    onDestroy();
                }
                break;
            case 2 :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(HomeActivity.this, "Location 2  Good", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                } else {
                    Toast.makeText(HomeActivity.this, "Location 2  Bad", Toast.LENGTH_SHORT).show();
                    onDestroy();
                }
                break;
        }
    }
}
/*
ArrayList<String> permissions=new ArrayList<>();
PermissionUtils permissionUtils;

permissionUtils=new PermissionUtils(MyLocationUsingLocationAPI.this);

permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);
 */