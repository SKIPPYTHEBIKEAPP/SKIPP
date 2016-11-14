package com.example.jonathan.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;

import java.text.DecimalFormat;
import java.util.Date;

import io.particle.android.sdk.cloud.*;

public class MainActivity extends AppCompatActivity implements GPSUpdate {
    private static LocationHandler handler;
    private double lon = 0;
    private double lat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            public void run() {

                setContentView(R.layout.activity_main);
            }
        }).start();
    }

    @Override
    protected void onStart() {

        super.onStart();
        LoginInformation login = new LoginInformation("dwongyee@gmail.com", "123456");
        //LocationDataSource source = new SkippyLocation();
        LocationDataSource source = new DummyDataSource();
        handler = new LocationHandler(source, 10000, login, this);
        handler.subscribeUpdates(this);
        handler.start();

    }

    public static LocationHandler getHandler(){
        return handler;
    }

    /**
     * Called when the user clicks the Send button
     */
    public void DisplaySetting(View view) {
        Intent intent = new Intent(this, Settings.class);


        startActivity(intent);
    }


    /**
     * Called when the user clicks the Send button
     */
    public void DisplayLocation(View view) {
        final Intent intent = new Intent(this, GPSLocation.class);

        //if location is set to zero no new location has been reported
        if (this.lat == 0 && this.lon==0) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Not found!");
            alertDialog.setMessage("Last location reported will be shown");


            alertDialog.setButton(alertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(intent);
                        }
                    });
            alertDialog.show();


        }
        //if location has been found a dialog message will be shown stating that the gps is active and found
        else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Found!");
            alertDialog.setMessage("Current location is shown");


            alertDialog.setButton(alertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    }


    /**
     * Called when the user clicks the Send button
     */
    public void DisplayBattery(View view) {
        Intent intent = new Intent(this, Battery.class);

        startActivity(intent);
    }



    
    public void receiveUpdate(GPSData data){
        if (data.valid){
            this.lat = data.lat;
            this.lon = data.lon;
        }

    }


}
