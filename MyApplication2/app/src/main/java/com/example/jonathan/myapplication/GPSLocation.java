package com.example.jonathan.myapplication;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.Date;


public class GPSLocation extends FragmentActivity implements OnMapReadyCallback, GPSUpdate {

    private GoogleMap mMap;
    private LocationHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslocation);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

/*
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

*/



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Location current = new Location();
        current.onStart();



        // Add a marker in Sydney and move the camera
        LatLng seattle = new LatLng(current.getLat(), current.getLon());


            mMap.addMarker(new MarkerOptions().position(seattle).title("Marker for my home"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(seattle, 15));


    }
    public void receiveUpdate(GPSData data, GoogleMap googleMap){
        mMap = googleMap;
        Location current = new Location();
        current.onStart();



        // Add a marker in Sydney and move the camera
        LatLng seattle = new LatLng(current.getLat(), current.getLon());


        mMap.addMarker(new MarkerOptions().position(seattle).title("Marker for my home"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(seattle, 15));
    }

}
