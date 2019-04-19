package com.tarek.carsharing.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private String name, email, phone,nid, exdate, songs, image;
    private int age, mangle1, mangle2, temp;

    public User() {}

    public User(String name, String email, String phone, String nid, String exdate, int age, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.nid = nid;
        this.exdate = exdate;
        this.age = age;
        this.songs = "";
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateUser();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        updateUser();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        updateUser();
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
        updateUser();
    }

    public String getExdate() {
        return exdate;
    }

    public void setExdate(String exdate) {
        this.exdate = exdate;
        updateUser();
    }

    public String getSongs() {
        return songs;
    }

    public void setSongs(String songs) {
        this.songs = songs;
        updateUser();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        updateUser();
    }

    public int getMangle1() {
        return mangle1;
    }

    public void setMangle1(int mangle1) {
        this.mangle1 = mangle1;
        updateUser();
    }

    public int getMangle2() {
        return mangle2;
    }

    public void setMangle2(int mangle2) {
        this.mangle2 = mangle2;
        updateUser();
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
        updateUser();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        updateUser();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("phone", phone);
        map.put("nid", nid);
        map.put("exdate", exdate);
        map.put("songs", songs);
        map.put("age", age);
        map.put("mangle1", mangle1);
        map.put("mangle2", mangle2);
        map.put("temp", temp);
        map.put("image", image);
        return map;
    }

    public void deleteUser () {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + FirebaseAuth.getInstance().getCurrentUser().getUid(), null);
        mDatabase.getReference("Users").updateChildren(childUpdates);
    }

    private void updateUser() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> userValues = toMap();
        childUpdates.put("/" + FirebaseAuth.getInstance().getCurrentUser().getUid(), userValues);
        mDatabase.getReference("Users").updateChildren(childUpdates);
    }

    public boolean addUser() {
        try {
            DatabaseReference base = FirebaseDatabase.getInstance().getReference("Users");
            base.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(this);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
