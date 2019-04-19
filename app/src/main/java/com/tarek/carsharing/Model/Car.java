package com.tarek.carsharing.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Car implements Serializable {

    private String type, number, image, color, location, songs, code;
    private int gaslevel, mangle1, mangle2, temp;
    private CarStatus status;

    public Car() {}

    public Car(String type, String number, String image, String color, String location, int mangle1, int mangle2, int temp, String songs, int gaslevel, CarStatus status) {
        this.type = type;
        this.number = number;
        this.image = image;
        this.color = color;
        this.location = location;
        this.mangle1 = mangle1;
        this.mangle2 = mangle2;
        this.temp = temp;
        this.songs = songs;
        this.gaslevel = gaslevel;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        updateCar();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
        updateCar();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        updateCar();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        updateCar();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        updateCar();
    }

    public int getMangle1() {
        return mangle1;
    }

    public void setMangle1(int mangle1) {
        this.mangle1 = mangle1;
        updateCar();
    }

    public int getMangle2() {
        return mangle2;
    }

    public void setMangle2(int mangle2) {
        this.mangle2 = mangle2;
        updateCar();
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
        updateCar();
    }

    public String getSongs() {
        return songs;
    }

    public void setSongs(String songs) {
        this.songs = songs;
        updateCar();
    }

    public int getGaslevel() {
        return gaslevel;
    }

    public void setGaslevel(int gaslevel) {
        this.gaslevel = gaslevel;
        updateCar();
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
        updateCar();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        updateCar();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("number", number);
        map.put("image", image);
        map.put("color", color);
        map.put("location", location);
        map.put("mangle1", mangle1);
        map.put("mangle2", mangle2);
        map.put("temp", temp);
        map.put("songs", songs);
        map.put("gaslevel", gaslevel);
        map.put("status", status);
        map.put("code", code);
        return map;
    }

    public void deleteCar () {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + number, null);
        mDatabase.getReference("Cars").updateChildren(childUpdates);
    }

    private void updateCar() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> userValues = toMap();
        childUpdates.put("/" + number, userValues);
        mDatabase.getReference("Cars").updateChildren(childUpdates);
    }

    public boolean addCar() {
        try {
            DatabaseReference base = FirebaseDatabase.getInstance().getReference("Cars");
            base.child(number).setValue(this);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

//FirebaseAuth.getInstance().getCurrentUser().getUid()
