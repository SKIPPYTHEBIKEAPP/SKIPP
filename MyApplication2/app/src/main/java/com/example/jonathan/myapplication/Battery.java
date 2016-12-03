package com.example.jonathan.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Battery extends AppCompatActivity implements GPSUpdate {


    LinearLayout mLinearLayout;
    private LocationHandler handler;
    private double battery = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_battery);

        onStart();

        //used to manipulate the background color to black
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(000000);


        // Create a LinearLayout in which to add the ImageView
        mLinearLayout = new LinearLayout(this);


        // Instantiate an ImageView and define its properties
        ImageView i = new ImageView(this);


        while (Configuration.getLocationHandler() == null | battery==-1 )
            receiveUpdate(Configuration.getLocationHandler().retrieveLastGPSData());

        if(battery>= 100){
            i.setImageResource(R.drawable.batteryfull);
        }else if(battery<99 & battery >=75){
            i.setImageResource(R.drawable.battery75);
        }else if(battery<75 & battery >=50){
            i.setImageResource(R.drawable.battery50);
        }else if(battery<50 & battery >=25){
            i.setImageResource(R.drawable.battery25);
        }else{

        }

        i.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions



        // Add the ImageView to the layout and set the layout as the content view
        mLinearLayout.addView(i);
        setContentView(mLinearLayout);

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (Configuration.getLocationHandler() != null) {
            Configuration.getLocationHandler().subscribeUpdates(this);
        }

    }

    public void receiveUpdate(GPSData data){
        if ( data.valid) {

            battery =  data.battery;

        }
    }
    public void gpsDisconnected(){
        Toast.makeText(this, "GPS Location Service Failure", Toast.LENGTH_LONG).show();
    }


}
