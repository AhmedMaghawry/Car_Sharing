package com.tarek.carsharing.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.EditText;
import android.widget.ImageView;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.tarek.carsharing.Control.Constants.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener , RoutingListener, GoogleMap.OnMarkerClickListener {


    public static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";
    private boolean startTrip = false ;
    public static Uri mImageUri;
    static String path;
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
    public  String address;
    private long startTime;
    private long endTime;
    private String code;



    private ImageView damagee ;
    private String pathFile;
    //  private  Button imageDamage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        LayoutInflater inflater1 = MapsActivity.this.getLayoutInflater();
        final View mView2 = inflater1.inflate(R.layout.layout_dialog, null);
        damagee = mView2.findViewById(R.id.damage);


        if(Build.VERSION.SDK_INT >=23){
            requestPermissions(new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},2);

        }



        Utils.showLoading(this);

        Intent intent = getIntent();
        carGet = (Car) intent.getSerializableExtra("car");

        //TODO: Change here
        if (carGet == null) {
            //String type, String number, String image, String color, String location, int mangle1, int mangle2, int temp, String songs, int gaslevel, CarStatus status
            carGet = new Car("Nissan", "52415"
                    , "https://firebasestorage.googleapis.com/v0/b/mytestauthentication-392d1.appspot.com/o/cars%2FIcon-512.png?alt=media&token=c3984cb9-a3e8-4be0-a5c6-8bcf2273dd4e",
                    "Black", "29.954643, 31.230067", 50, 60, 22, "1,2,3", 45, CarStatus.OFF);
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
        //  getBluetoothMacAddress(MapsActivity.this);
        //    Log.i("Tarook", address);
        Toast.makeText(MapsActivity.this, mBluetoothAdapter.getAddress(), Toast.LENGTH_SHORT).show();

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
        //  imageDamage=findViewById(R.id.imageDamage);
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

    public static String getBluetoothMacAddress(Context mContext) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // BluetoothAdapter.getDefaultAdapter().DEFAULT_MAC_ADDRESS;
        // if device does not support Bluetooth
        if (mBluetoothAdapter == null) {
            Log.i("Tarook", "device does not support bluetooth");
            return null;
        }

        String address = mBluetoothAdapter.getAddress();
        if (address.equals("02:00:00:00:00:00")) {

            //  System.out.println(">>>>>G fail to get mac address " + address);

            try {
                ContentResolver mContentResolver = mContext.getContentResolver();
                address = Settings.Secure.getString(mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDRESS);

            } catch (Exception e) {
                Log.i("Tarook", "device does not support bluetooth");

            }

        } else {

            // System.out.println(">>>>>G sucess to get mac address " + address);
        }
        return address;
    }

    private String getBluetoothMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothMacAddress = "";
        try {
            Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
            mServiceField.setAccessible(true);

            Object btManagerService = mServiceField.get(bluetoothAdapter);


            if (btManagerService != null) {
                bluetoothMacAddress = (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
            }
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {

        }
        return bluetoothMacAddress;
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
        carCrash();
    }



    private void bluetooth() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, 1);
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
                                    String replyy = feedback.getText().toString().trim();
                                    Toast.makeText(MapsActivity.this, replyy, Toast.LENGTH_SHORT).show();

                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                }
                                carCrash3();
                            }
                        })
                .setNegativeButton("Skip",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog2,
                                                int id) {
                                dialog2.cancel();
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


                Toast.makeText(MapsActivity.this, "photochoose", Toast.LENGTH_SHORT).show();
                photoChoose();
            }
        });

        builder3.setView(mView2)
                .setPositiveButton("Send",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog3,
                                                int id) {

                                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                emailIntent.setType("application/image");
                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"m7mdtarek44@gmail.com"});
                                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Test Subject");
                                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");
                                //Log.i("Tarekk",path);
                                //emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + path));
                                emailIntent.putExtra(Intent.EXTRA_STREAM, mImageUri);
                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                            }
                        })
                .setNegativeButton("Skip",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog3,
                                                int id) {
                                dialog3.cancel();
                            }
                        });


        AlertDialog alertDialog2 = builder3.create();
        alertDialog2.show();

    }

    private void carCrash() {
//        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
                        //bluetooth();
                        startTrip();
                        dialog.cancel();
                    }
                });


        AlertDialog alert = builder.create();
        alert.show();

    }

    ////**************************

    public void  photoChoose() {

        Intent takepic = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        // if (takepic.resolveActivity(getPackageManager()) != null) {
        startActivityForResult(takepic, 4);

   /*      File photoFile ;
          photoFile = createPhotoFile();

           if (photoFile != null) {

                pathFile = photoFile.getAbsolutePath();
           Uri photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
              takepic.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            pic = photoUri;
            Log.i("Tarook", pic.toString());
               Log.i("Tarook", pic.getPath());


            }
        }*/
    }
    private File createPhotoFile() {

        String name =new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storage = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image= null;
        try {
            image = File.createTempFile(name,".jpg",storage);
        }
        catch (IOException e) {
            Log.d("log", e.toString());
        }
        return image;

    }
    //************************* end

    private void endTrip() {
        endTime = System.nanoTime();
        trip.setEnd(mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
        trip.setStatus(TripStatus.FINISHED);
        int fare = getFare(endTime - startTime , trip.getStart(), trip.getEnd());
        trip.setFare(fare);
        trip.setTime(getFormatedTime(endTime - startTime));
        trip.updateTrip();
        startTrip = false ;
        carGet.setStatus(CarStatus.OFF);
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

    private int getFare(long duration, String start, String end) {
        //TODO:Change here
        return 50;
    }

    private String getFormatedTime(long l) {
        int seconds = (int) (l / 1000000000);
        int mins = seconds / 60;
        int hours = mins / 60;

        return hours + ":" + (mins - hours * 60) + ":" + (seconds - mins * 60);
    }
    public File savebitmap(Bitmap bmp) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "testimage.jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        //    path = f.getAbsolutePath();
        //   mImageUri = Uri.fromFile(f);
        Log.i("Tarekk",this.getApplicationContext().getPackageName() + ".Control.GenericFileProvider");
        mImageUri = FileProvider.getUriForFile(MapsActivity.this, this.getApplicationContext().getPackageName() + ".Control.GenericFileProvider", f);
        return f;
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
            case 4:

                if (resultCode != RESULT_CANCELED) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    damagee.setImageBitmap(image);

                    try {
                        savebitmap(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Log.i("TAREK", data.getData().toString());
                } else
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                //   }

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

    private void startTrip() {
        Toast.makeText(MapsActivity.this, "Code is Correct", Toast.LENGTH_SHORT).show();
        generateTrip();
        startTrip = true ;
        startTime = System.nanoTime();
        unlock.setVisibility(View.GONE);
        end.setVisibility(View.VISIBLE);
    }

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
