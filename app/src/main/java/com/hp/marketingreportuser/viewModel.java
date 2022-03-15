package com.hp.marketingreportuser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class viewModel extends ViewModel {
    private final MutableLiveData<String> itemView = new MutableLiveData<String>();
    private final MutableLiveData<Double> latitudeView = new MutableLiveData<Double>();
    private final MutableLiveData<Double> longitudeView = new MutableLiveData<Double>();
    private final MutableLiveData<Integer> totalStores = new MutableLiveData<Integer>();
    private final MutableLiveData<Integer> totalVisits = new MutableLiveData<Integer>();
    private final MutableLiveData<Integer> todayVisits = new MutableLiveData<Integer>();
    private final MutableLiveData<HashMap<String,Float>> monthlyDataCount = new MutableLiveData<HashMap<String,Float>>();
    private final MutableLiveData<String> notification = new MutableLiveData<String>();

    public void setItem(String item){
        itemView.setValue(item);
    }
    public LiveData<String> getItem(){
        return itemView;
    }

    public void setNotification(String notificationData){
        notification.setValue(String.valueOf(notificationData));
    }
    public LiveData<String> getNotification(){
        return notification;
    }

    public void setLongitude(Double longitude){
        longitudeView.setValue(longitude);
    }
    public LiveData<Double> getLongitude(){
        return longitudeView;
    }

    public void setLatitude(Double latitude){
        latitudeView.setValue(latitude);
    }
    public LiveData<Double> getLatitude(){
        return latitudeView;
    }

    public void setTotalStores(Integer item) {
        totalStores.setValue(item);
    }

    public LiveData<Integer> getTotalStores() {
        return totalStores;
    }

    public void setTotalVisits(Integer item) {
        totalVisits.setValue(item);
    }

    public LiveData<Integer> getTotalVisits() {
        return totalVisits;
    }

    public void setTodayVisits(Integer item) {
        todayVisits.setValue(item);
    }

    public LiveData<Integer> getTodayVisits() {
        return todayVisits;
    }

    public void setMonthlyDataCount(HashMap<String,Float> item) {
        monthlyDataCount.setValue(item);
    }

    public LiveData<HashMap<String,Float>> getMonthlyDataCount() {
        return monthlyDataCount;
    }
}
