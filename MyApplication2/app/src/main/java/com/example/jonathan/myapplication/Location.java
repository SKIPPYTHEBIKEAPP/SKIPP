
package com.example.jonathan.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Date;

public class Location extends AppCompatActivity implements GPSUpdate{
    private LocationHandler handler;
    public double lon = 0;
    public double lat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

    }
    @Override
    protected void onStart() {
        super.onStart();
        handler = MainActivity.getHandler();
        //LoginInformation login = new LoginInformation("dwongyee@gmail.com", "123456");
        //LocationDataSource source = new SkippyLocation();
        //LocationDataSource source = new DummyDataSource();
        //handler = new LocationHandler(source, 10000, login, this);
        handler.subscribeUpdates(this);
        //handler.start();
    }

    public double getLon(){
        return lon;

    }
    public double getLat(){
        return lat;
    }


    @Override
    public void receiveUpdate(GPSData data) {
        TextView gpsView = (TextView)findViewById(R.id.GPSView);
        String update = "";
        if (data.valid) {
            lat = data.lat;
            lon = data.lon;
            update = lat + " " + lon;
        }

        gpsView.append(update);
    }

    public void gpsDisconnected() {

    }
}
