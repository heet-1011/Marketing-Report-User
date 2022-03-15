package com.hp.marketingreportuser;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Time;

public class userTimelineModel {
    String storeName;
    String storeMobNo;
    String empName;
    GeoPoint location;
    Timestamp date;
    public userTimelineModel(){

    }

    public userTimelineModel(String storeName, String storeMobNo, String empName, GeoPoint location, Timestamp date) {
        this.storeName = storeName;
        this.storeMobNo = storeMobNo;
        this.empName = empName;
        this.date = date;
        this.location = location;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreMobNo() {
        return storeMobNo;
    }

    public void setStoreMobNo(String storeMobNo) {
        this.storeMobNo = storeMobNo;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
