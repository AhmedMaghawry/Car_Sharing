package com.tarek.carsharing.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Warning implements Serializable {
    private String carid, distance, date, location,id;


    public Warning() {
    }

    public String getCarid() {
        return carid;
    }

    public void setCarid(String carid) {
        this.carid = carid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Warning(String carid, String distance, String date, String location) {
        this.carid = carid;
        this.distance = distance;
        this.date = date;
        this.location = location;
        this.id="";
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("carid", carid);
        map.put("distance", distance);
        map.put("date", date);
        map.put("location", location);
        map.put("id",id);
        return map;
    }
    public boolean addWarning() {
        try {
            DatabaseReference base = FirebaseDatabase.getInstance().getReference("Warning");
            this.id = base.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
            // updateTrip();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}


