package com.tarek.carsharing.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Trip implements Serializable{

    private String carid, start, end, time, id, code;
    private int fare;
    private TripStatus status;

    public Trip() {}

    public Trip(String carid, String start) {
        this.carid = carid;
        this.start = start;
        this.status = TripStatus.INPROGRESS;
        this.fare = 0;
        this.end = "";
        this.time = "";
        this.id = "";
        this.code = "";
    }

    public String getCarid() {
        return carid;
    }

    public void setCarid(String carid) {
        this.carid = carid;
        //updateTrip();
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
        //   updateTrip();
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
        // updateTrip();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        //  updateTrip();
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
        // updateTrip();
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
        //updateTrip();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        // updateTrip();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        // updateTrip();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("carid", carid);
        map.put("start", start);
        map.put("end", end);
        map.put("time", time);
        map.put("fare", fare);
        map.put("status", status);
        map.put("code", code);
        map.put("id", id);
        return map;
    }

    public void deleteTrip () {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + id, null);
        mDatabase.getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(childUpdates);
    }

    public void updateTrip() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> userValues = toMap();
        childUpdates.put("/" + id, userValues);
        mDatabase.getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(childUpdates);
    }

    public boolean addTrip() {
        try {
            DatabaseReference base = FirebaseDatabase.getInstance().getReference("Trips");
            this.id = base.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
            // updateTrip();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
