package com.example.jonathan.myapplication;

import android.content.Context;

import java.util.Date;

/**
 * Implementation of LocationDataSource that returns fake data
 */

public class DummyDataSource implements LocationDataSource {
    DummyDataSourceConfig dummyConfig;
    private double lat;
    private char latDir;
    private double lon;
    private char lonDir;

    public DummyDataSource(DummyDataSourceConfig dummyConfig){
        this.dummyConfig = dummyConfig;
        lat = 47.6062;
        lon = 122.3321;
        latDir = 'N';
        lonDir = 'W';
    }

    public void init(Context context) {

    }

    public void login() {

    }

    public void logout() {

    }

    public GPSData getUpdate() {
        lat += dummyConfig.movementspeed;
        lon += dummyConfig.movementspeed;
        return new GPSData(lat, lon, latDir, lonDir, 100, new Date(), true);
    }
}
