package com.tarek.carsharing.Control;

public interface Constants {

    int SPLASH_TIME_OUT = 1500;
    String TRIP_TABLE = "Trips";
    String CAR_TABLE = "Cars";
    String USER_TABLE = "Users";
    String FAIL = "Fail";

    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;

    String CODE_SP= "code";
    String DEVICE_NAME = "device_name";

    int REQUEST_CONNECT_DEVICE_SECURE = 1;
    int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    int REQUEST_ENABLE_BT = 3;

    String URL_CODE = "https://us-central1-mytestauthentication-392d1.cloudfunctions.net/code_generator";
}
