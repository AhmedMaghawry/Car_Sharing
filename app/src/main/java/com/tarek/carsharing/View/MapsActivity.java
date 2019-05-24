package com.tarek.carsharing.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.tarek.carsharing.Control.BluetoothChatService;
import com.tarek.carsharing.Control.TripCodeGenerator;
import com.tarek.carsharing.Control.Utils;
import com.tarek.carsharing.Control.onAction;
import com.tarek.carsharing.Model.Car;
import com.tarek.carsharing.Model.CarAcquireKey;
import com.tarek.carsharing.Model.CarStatus;
import com.tarek.carsharing.Model.CarTrip;
import com.tarek.carsharing.Model.Trip;
import com.tarek.carsharing.Model.TripStatus;
import com.tarek.carsharing.Model.User;
import com.tarek.carsharing.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;





import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.tarek.carsharing.Control.Constants.*;

public class MapsActivity extends FragmentActivity implements  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener , RoutingListener, GoogleMap.OnMarkerClickListener {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public String reply;
    public static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";
    private boolean startTrip = false ;
    public  Uri tempUri;
    private GoogleMap mMap;
    private String TAG = "so47492459";
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Trip trip;
    private Car carGet;
    private User currentUser, oldUser;
    private View bottomLayout;
    private TextView carName, carColor, carDistance, carDuration;
    private Button unlock, end , startEnd;
    private BottomSheetBehavior behavior;
    private Marker carMarker;

    private String mConnectedDeviceName = null;
    private StringBuffer mOutStringBuffer;
    private MapsActivity self;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    public  String address;
    private long startTime;
    private long endTime;
    private String code;
    int flag =0;
    boolean b = true;
    String rateId;

    private ImageView damagee ;
    private String pathFile;
    //  private  Button imageDamage;
    float i=0;
    private Route rr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        LayoutInflater inflater1 = MapsActivity.this.getLayoutInflater();
        final View mView2 = inflater1.inflate(R.layout.layout_dialog, null);
        damagee = mView2.findViewById(R.id.damage);


        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

        }


        Utils.showLoading(this);

        Intent intent = getIntent();
        carGet = (Car) intent.getSerializableExtra("car");

        //TODO: Change here
        if (carGet == null) {
            //String type, String number, String image, String color, String location, int mangle1, int mangle2, int temp, String songs, int gaslevel, CarStatus status
            carGet = new Car("Nissan", "52415"
                    , "https://firebasestorage.googleapis.com/v0/b/mytestauthentication-392d1.appspot.com/o/cars%2FIcon-512.png?alt=media&token=c3984cb9-a3e8-4be0-a5c6-8bcf2273dd4e",
                    "Black", "29.954643, 31.230067", 50, 60, 22, "1,2,3", 45, CarStatus.OFF, CarAcquireKey.LOCK, CarTrip.END, null );
        }

        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Users");

        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                currentUser = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(User.class);
                oldUser = dataSnapshot.child(rateId).getValue(User.class);


                Utils.hideLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                Utils.hideLoading();
            }
        });



        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        self = this;
        //  getBluetoothMacAddress(MapsActivity.this);
        //    Log.i("Tarook", address);
       // Toast.makeText(MapsActivity.this, mBluetoothAdapter.getAddress(), Toast.LENGTH_SHORT).show();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        carGet.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        rateId = carGet.getId();
        Toast.makeText(self, rateId, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
       startActivity(new Intent(MapsActivity.this,HomeActivity.class));
       finish();
    }

    @SuppressLint("ResourceAsColor")
    private void setupBottom(Route route) {

        bottomLayout = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomLayout);
        carName = findViewById(R.id.car_name);
        carColor = findViewById(R.id.car_color);
        carDistance = findViewById(R.id.car_dist);
        carDuration = findViewById(R.id.car_dur);
        unlock = findViewById(R.id.unlock);
        startEnd = findViewById(R.id.startend);
        end = findViewById(R.id.end);

        carName.setText(carGet.getType());
        carColor.setText(carGet.getColor());
        carDistance.setText(route.getDistanceText());
        carDuration.setText(route.getDurationText());

        if (route.getDistanceValue() < 300) {
            unlock.setClickable(true);
            unlock.setEnabled(true);
            unlock.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            unlock.setClickable(false);
            unlock.setEnabled(false);
            unlock.setBackgroundColor(getResources().getColor(R.color.texBackgroundColor));
        }


        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockCar();
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                endTrip();
            }
        });

        startEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTrip();
            }
        });
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        mMap.setOnMarkerClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)           // on connected onconectedsuspended
                .addOnConnectionFailedListener(this)    // device doestnt have gps
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        if (getApplicationContext() != null) {

            mLastLocation = location;
            mMap.clear();


            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if(!startTrip)
                mMap.addMarker(new MarkerOptions().position(latLng).title("My location").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

            String loc = carGet.getLocation();
            String locs[] = loc.split(",");

            getCurrentLocation(new onAction() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish(Object object) {
                    String v = (String) object;
                    String[] res = v.split(",");
                    LatLng carLocation = new LatLng(Double.parseDouble(res[0].trim()), Double.parseDouble(res[1].trim()));
                    MarkerOptions marker = new MarkerOptions().position(carLocation).title("car").icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                    carMarker = mMap.addMarker(marker);
                    getRouteToMarker(carLocation);
                }
            });


            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

           /* String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("carsAvailable");

            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));*/
        }
    }

    private void getCurrentLocation(final onAction action) {

        FirebaseDatabase.getInstance().getReference("Cars").child(carGet.getNumber()).child("location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String v = dataSnapshot.getValue(String.class);
                action.onFinish(v);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getRouteToMarker(LatLng car) {
        Routing routing = new Routing.Builder()
                .key("AIzaSyDPdyQ0DKwxdHuZQOFGIBBpz_CyRVDuhdE")
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), car)

                .build();
        routing.execute();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(15000);  //ms
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};


    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        int colorIndex = shortestRouteIndex % COLORS.length;

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(COLORS[colorIndex]));
        polyOptions.width(10 + shortestRouteIndex * 3);
        polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
        Polyline polyline = mMap.addPolyline(polyOptions);
        //polylines.add(polyline);
        rr = route.get(shortestRouteIndex);
        setupBottom(route.get(shortestRouteIndex));
        //Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.equals(carMarker)) {
            if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            else
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        return true;
    }

    //************************** dana start




    private void unlockCar() {
        if (flag == 0) {
            carCrash();
            b = false;
        }

        else {
            if(b == true ){
                unlock.setText("LOCK");
                carGet.setAcquirekey(CarAcquireKey.UNLOCK);
                carGet.updateCar();
                b = false;
            }
            else{
                unlock.setText("UNLOCK");
                carGet.setAcquirekey(CarAcquireKey.LOCK);
                carGet.updateCar();

                b = true;
            }

        }
    }





    private void startsButton() {
        startEnd.setClickable(true);
        startEnd.setEnabled(true);
        startEnd.setBackgroundColor(getResources().getColor(R.color.green_100));


    }

    private void unlockbuttonstarts(){

        unlock.setText("LOCK");
        carGet.setAcquirekey(CarAcquireKey.UNLOCK);
        carGet.updateCar();
    }



    private void carCrash2() {

        Toast.makeText(MapsActivity.this, "carcrush2  on ", Toast.LENGTH_SHORT).show();


        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setMessage("Please give your feedback");

        LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
        final View mView = inflater.inflate(R.layout.layout_dialig, null);
        final EditText feedback = mView.findViewById(R.id.editText);
        builder2.setView(mView)
                .setPositiveButton("Send",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog2,
                                                int id) {

                                try {
                                    reply = feedback.getText().toString().trim();
                                    Toast.makeText(MapsActivity.this, reply, Toast.LENGTH_SHORT).show();
                                    carCrash3();

                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                }

                            }
                        })
                .setNegativeButton("Skip",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog2,
                                                int id) {
                                dialog2.cancel();
                                carCrash3();
                            }

                        });

        AlertDialog alertDialog = builder2.create();
        alertDialog.show();

    }

    //*************************


    private void carCrash3(){
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
        builder3.setMessage("Please upload a picture of the damage");
        LayoutInflater inflater1 = MapsActivity.this.getLayoutInflater();
        final View mView2 = inflater1.inflate(R.layout.layout_dialog, null);
        final Button imageDamage = mView2.findViewById(R.id.imageDamage);
        damagee = mView2.findViewById(R.id.damage);

        imageDamage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                }
                Toast.makeText(MapsActivity.this, "photochoose", Toast.LENGTH_SHORT).show();
                // photoChoose();
            }
        });

        builder3.setView(mView2)
                .setPositiveButton("Send",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog3,
                                                int id) {
                                dialog3.cancel();
                                sendMessage(tempUri);

                                carCrash4();


                            }
                        })
                .setNegativeButton("Skip",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog3,
                                                int id) {
                                dialog3.cancel();
                                carCrash4();
                            }
                        });


        AlertDialog alertDialog2 = builder3.create();
        alertDialog2.show();

    }



    private void carCrash4(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you still want to use it ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startsButton();
                        unlockbuttonstarts();
                        Toast.makeText(MapsActivity.this, "carcrush", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        carGet.setStatus(CarStatus.BROKEN);
                        carGet.updateCar();
                        dialog.cancel();
                        goHome();
                    }
                });


        AlertDialog alert = builder.create();
        alert.show();


    }

    private void goHome(){
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }


    private void carCrash() {
        flag=1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Check carefully the car before unlocking it, Did the previous user harm the car? would you like to give a feedback? ")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //   carGet.setStatus(CarStatus.enpanne);
                        carCrash2();
                        Toast.makeText(MapsActivity.this, "carcrush", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startsButton();
                        unlockbuttonstarts();
                        // startTrip();
                        dialog.cancel();
                    }
                });


        AlertDialog alert = builder.create();
        alert.show();

    }

    ////**************Rating



    public void tripSize(){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Trips").child(rateId);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot trips : dataSnapshot.getChildren()) {
                    i = i+1 ;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void RatePreviousUser() {

        tripSize();
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setMessage("Please rate the previous user ");
        LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
        final View mView = inflater.inflate(R.layout.rating, null);
        final RatingBar simpleRatingBar = mView.findViewById(R.id.simpleRatingBar);
        builder2.setView(mView).setNeutralButton("Rate",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2,
                                        int id) {


                        try {
                            float oldRating=oldUser.getRate();
                            String totalStars = "Total Stars:: " + simpleRatingBar.getNumStars();
                            float rating = simpleRatingBar.getRating();
                            // Toast.makeText(self,String.valueOf(rating), Toast.LENGTH_LONG).show();
                            float newRating =( (oldRating * i ) + rating )/(i+1);



                            Toast.makeText(self,String.valueOf(newRating) + "  "  + String.valueOf(i), Toast.LENGTH_LONG).show();
                            //  Toast.makeText(self,String.valueOf(newRating), Toast.LENGTH_LONG).show();


                            oldUser.setRate(newRating);

                            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                            Map<String, Object> childUpdates = new HashMap<>();
                            Map<String, Object> userValues = oldUser.toMap();
                            childUpdates.put("/" + rateId, userValues);
                            mDatabase.getReference("Users").updateChildren(childUpdates);

                            carGet.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            carGet.updateCar();


                        }


                        catch(Exception e){
                            e.printStackTrace();
                        }

                    }
                });


        AlertDialog alertDialog = builder2.create();
        alertDialog.show();







    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000,true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }


    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    //************************* end

    private void endTrip() {
        endTime = System.nanoTime();
        trip.setEnd(mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
        trip.setStatus(TripStatus.FINISHED);
        int fare = getFare(endTime - startTime , rr.getDistanceValue());
        trip.setFare(fare);
        trip.setTime(getFormatedTime(endTime - startTime));
        trip.updateTrip();
        startTrip = false ;
        carGet.setStatus(CarStatus.OFF);
        carGet.setAcquirekey(CarAcquireKey.LOCK);
        carGet.setCarstartend(CarTrip.END);
        carGet.updateCar();
        SweetAlertDialog
                pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText("Fare");
        pDialog.setContentText("Trip Completed your fair is : " + fare + " LE");
        pDialog.setConfirmText("OK");
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Utils.launchActivity(MapsActivity.this,HomeActivity.class,null);
                finish();
            }
        });
        pDialog.show();
        end.setVisibility(View.GONE);
    }

    private int getFare(long duration, int dist) {
        //TODO:Change here
       int fare= (int) (duration*50+dist*100);
        return fare;
    }

    private String getFormatedTime(long l) {
        int seconds = (int) (l / 1000000000);
        int mins = seconds / 60;
        int hours = mins / 60;

        return hours + ":" + (mins - hours * 60) + ":" + (seconds - mins * 60);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Toast.makeText(this, "Not enable", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case CAMERA_REQUEST:

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                damagee.setImageBitmap(photo);
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                tempUri = getImageUri(getApplicationContext(), photo);

                break;

        }
    }

    private void sendMessage(Uri uri) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/image");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"m7mdtarek44@gmail.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Complaint");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, reply);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device, secure);
    }

    //handler to manage the chat service
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    Toast.makeText(getApplicationContext(), writeMessage + " Dodo Write", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(getApplicationContext(), readMessage + " Dodo Read", Toast.LENGTH_SHORT).show();
                    //blue.setText(readMessage);
                    //TODO: Read Message
                    if (readMessage.equals(code)) {
                        startTrip();
                    } else {
                        Toast.makeText(MapsActivity.this, "Failed" + readMessage, Toast.LENGTH_SHORT).show();
                        self.sendMessage(readMessage);
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                    //TODO:Send the message here
                    TripCodeGenerator tg = new TripCodeGenerator(new onAction() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish(Object object) {
                            code = (String) object;
                            self.sendMessage(code);
                        }
                    });
                    tg.execute();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();

                    //blue.setText(msg.getData().getString("toast"));
                    break;
            }
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void startTrip() {

        RatePreviousUser();
        Toast.makeText(MapsActivity.this, "Code is Correct", Toast.LENGTH_SHORT).show();
        generateTrip();
        startTrip = true ;
        startTime = System.nanoTime();
        carGet.setCarstartend(CarTrip.START);
        carGet.updateCar();
        startEnd.setVisibility(View.GONE);
        end.setVisibility(View.VISIBLE);


    }

    private void generateTrip() {
        trip = new Trip(carGet.getNumber(), mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
        trip.addTrip();
        trip.setCode(code);
        carGet.setMangle1(currentUser.getMangle1());
        carGet.setMangle2(currentUser.getMangle2());
        carGet.setStatus(CarStatus.ON);
        carGet.updateCar();
    }

    private void sendMessage(String message) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "Not conected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);


            mOutStringBuffer.setLength(0);
        }
    }

    private void setupChat() {
        mChatService = new BluetoothChatService(this, mHandler);
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatService != null) mChatService.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 3);
        } else {
            if (mChatService == null) setupChat();
        }
    }


}
