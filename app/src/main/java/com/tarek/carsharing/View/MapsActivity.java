package com.tarek.carsharing.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.tarek.carsharing.Model.CarStatus;
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

import java.util.ArrayList;

import static com.tarek.carsharing.Control.Constants.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener , RoutingListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private String TAG = "so47492459";
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Trip trip;
    private Car carGet;
    private User currentUser;
    private View bottomLayout;
    private TextView carName, carColor, carDistance, carDuration;
    private Button unlock, end;
    private BottomSheetBehavior behavior;
    private Marker carMarker;

    private String mConnectedDeviceName = null;
    private StringBuffer mOutStringBuffer;
    private MapsActivity self;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;

    private long startTime;
    private long endTime;
    private String code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Utils.showLoading(this);

        Intent intent = getIntent();
        carGet = (Car) intent.getSerializableExtra("car");

        //TODO: Change here
        if (carGet == null) {
            //String type, String number, String image, String color, String location, int mangle1, int mangle2, int temp, String songs, int gaslevel, CarStatus status
            carGet = new Car("Nissan", "52415"
                    , "https://firebasestorage.googleapis.com/v0/b/mytestauthentication-392d1.appspot.com/o/cars%2FIcon-512.png?alt=media&token=c3984cb9-a3e8-4be0-a5c6-8bcf2273dd4e",
                    "Black", "29.954643, 31.230067",50, 60, 22, "1,2,3", 45, CarStatus.OFF);
        }

        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Users");

        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentUser = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(User.class);
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
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

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)           // on connected onconectedsuspended
                .addOnConnectionFailedListener(this)    // device doestnt have gps
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        if(getApplicationContext()!=null){

            mLastLocation = location;
            mMap.clear();


            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("My location").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

            String loc = carGet.getLocation();
            String locs[] = loc.split(",");

            LatLng carLocation = new LatLng(Double.parseDouble(locs[0].trim()),Double.parseDouble(locs[1].trim()));
            MarkerOptions marker = new MarkerOptions().position(carLocation).title("car").icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
            carMarker = mMap.addMarker(marker);


            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

            /*String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("carsAvailable");

            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));*/

            getRouteToMarker(carLocation);
        }
    }

    private void getRouteToMarker(LatLng car) {
        Routing routing = new Routing.Builder()
                .key("AIzaSyDPdyQ0DKwxdHuZQOFGIBBpz_CyRVDuhdE")
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()) , car)

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
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
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

    private void unlockCar() {

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, 1);
    }

    private void endTrip() {
        endTime = System.nanoTime();
        trip.setEnd(mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
        trip.setStatus(TripStatus.FINISHED);
        int fare = getFare(endTime - startTime , trip.getStart(), trip.getEnd());
        trip.setFare(fare);
        trip.setTime(getFormatedTime(endTime - startTime));
        carGet.setStatus(CarStatus.OFF);
        Utils.showMessage(this, "Trip Completed your fair is : " + fare + " LE");
        end.setVisibility(View.GONE);
    }

    private int getFare(long l, String start, String end) {
        //TODO:Change here
        return 50;
    }

    private String getFormatedTime(long l) {
        int seconds = (int) (l / 1000000000);
        int mins = seconds / 60;
        int hours = mins / 60;

        return hours + ":" + (mins - hours * 60) + ":" + (seconds - mins * 60);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        }
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
                        Toast.makeText(MapsActivity.this, "Code is Correct", Toast.LENGTH_SHORT).show();
                        generateTrip();
                        startTime = System.nanoTime();
                        unlock.setVisibility(View.GONE);
                        end.setVisibility(View.VISIBLE);
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

    private void generateTrip() {
        trip = new Trip(carGet.getNumber(), mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
        trip.addTrip();
        trip.setCode(code);
        carGet.setMangle1(currentUser.getMangle1());
        carGet.setMangle2(currentUser.getMangle2());
        carGet.setStatus(CarStatus.ON);
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
