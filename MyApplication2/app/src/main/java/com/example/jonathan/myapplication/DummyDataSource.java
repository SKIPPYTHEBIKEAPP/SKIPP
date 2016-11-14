package com.example.jonathan.myapplication;

import android.content.Context;

import java.util.Date;

/**
 * Implementation of LocationDataSource that returns fake data
 */

public class DummyDataSource implements LocationDataSource {
    public void init(Context context) {

    }

    public void login(LoginInformation loginInformation) {

    }

    public GPSData getUpdate() {
        return new GPSData(47.6062, 122.3321, 'N', 'W', 100, new Date(), true);
    }
}
