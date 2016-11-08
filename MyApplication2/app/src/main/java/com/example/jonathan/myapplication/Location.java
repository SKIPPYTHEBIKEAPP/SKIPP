package com.example.jonathan.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Location extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
    }

    private double Longitude = 0;
    private double Latitude =0;

    public double getLong() {

        return Longitude;
    }

    public double getLat(){
        return Latitude;


    }
    private void setLongitude(double Long){
        this.Longitude = Long;

    }

    private void setLatitude(double Lat){
        this.Latitude = Lat;
}
}
