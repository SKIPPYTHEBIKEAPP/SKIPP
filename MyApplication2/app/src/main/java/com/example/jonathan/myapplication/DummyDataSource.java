package com.example.jonathan.myapplication;

import android.content.Context;

import java.util.Date;
import java.util.Random;

/**
 * Implementation of LocationDataSource that returns fake data
 */

public class DummyDataSource implements LocationDataSource {
    DummyDataSourceConfig dummyConfig;
    private double lat;
    private char latDir;
    private double lon;
    private char lonDir;
    private Random random;

    public DummyDataSource(DummyDataSourceConfig dummyConfig){
        this.dummyConfig = dummyConfig;
        lat = 47.6062;
        lon = 122.3321;
        latDir = 'N';
        lonDir = 'W';
        random = new Random();
    }

    public void init(Context context) {

    }

    public void login() throws Exception {
        if (dummyConfig.invalidLogin)
            throw new Exception ("Simulated invalid credential error");
    }

    public void logout() {

    }

    public GPSData getUpdate() throws Exception {
        if (random.nextDouble() < dummyConfig.connectFailProbability)
            throw new Exception("Simulated connection failure.");

        // Randomly move west/east or north/south
        int latMovementDirection = random.nextBoolean() ? 1 : -1;
        int lonMovementDirection = random.nextBoolean() ? 1 : -1;

        // Change current location
        lat += random.nextFloat() * dummyConfig.movementSpeed * latMovementDirection;
        lon += random.nextFloat() * dummyConfig.movementSpeed * lonMovementDirection;

        Date time = new Date();
        if (time.getMinutes() % dummyConfig.intervalBigJump == 0)
            lat += 1;

        if (random.nextFloat() < dummyConfig.invalidDataProbability)
            return GPSData.invalidData();
        else
            return new GPSData(lat, lon, latDir, lonDir, random.nextDouble()*100, new Date(), true);
    }
}
