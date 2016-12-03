package com.example.jonathan.myapplication;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private volatile GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslocation);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Button refreashButton = (Button) findViewById(R.id.refreshbutton);
        refreashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LocationHandler locationHandler = Configuration.getLocationHandler();
                if (locationHandler != null) {
                    try {
                        locationHandler.forceUpdate();
                    } catch (Exception e) {
                        // might want to display an error if it can't refresh?
                    }
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        LocationHandler locationHandler = Configuration.getLocationHandler();
        if (locationHandler != null) {
            locationHandler.subscribeUpdates(this);
        }
    }

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

        // Now that map has appeared, get a valid location instead of waiting for the
        // interval timeout
        LocationHandler locationHandler = Configuration.getLocationHandler();
        if (locationHandler != null)
            receiveUpdate(locationHandler.retrieveLastGPSData());
    }

    // GPSUpdate interface:

    public void receiveUpdate(GPSData data){
        if ((this.mMap != null && data != null) && data.valid) {
            mMap.clear();
            double lat = GPSData.convertLatLon(data.lat, data.latDir);
            double lon = GPSData.convertLatLon(data.lon, data.lonDir);

            LatLng currentLocation = new LatLng(lat, lon);

            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));

            // changed from animate camera to movecamera, incoming GPS updates while panning
            // is in progress messed with the map zoom levels
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
                    mMap.getCameraPosition().zoom));
        }
    }

    public void gpsDisconnected(){
        //Toast.makeText(this, "GPS Location Service Failure", Toast.LENGTH_LONG).show();
    }

}
