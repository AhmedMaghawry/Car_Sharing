package com.tarek.carsharing.Model;


import java.io.Serializable;

public class Promocodes implements Serializable {

    private String name;
    private int value, times ,used ;

    public Promocodes() {
    }

    public Promocodes(String name, int value, int times, int used) {
        this.name = name;
        this.value = value;
        this.times = times;
        this.used=used;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }
}