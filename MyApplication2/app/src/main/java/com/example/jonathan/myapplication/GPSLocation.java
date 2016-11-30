package com.example.jonathan.myapplication;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
                if (Configuration.getLocationHandler() != null) {
                    try {
                        Configuration.getLocationHandler().forceUpdate();
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
        if (Configuration.getLocationHandler() != null) {
            Configuration.getLocationHandler().subscribeUpdates(this);
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
        if (Configuration.getLocationHandler() != null)
            receiveUpdate(Configuration.getLocationHandler().retrieveLastGPSData());
    }

    // GPSUpdate interface:

    public void receiveUpdate(GPSData data){
        if ((this.mMap != null && data != null) && data.valid) {
            mMap.clear();
            double lat = data.lat;
            double lon = data.lon;

            if (data.latDir == 'S')
                lat *= -1;
            if (data.lonDir == 'W')
                lon *= -1;

            LatLng currentLocation = new LatLng(lat, lon);

            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
                    mMap.getCameraPosition().zoom));
        }
    }

    public void gpsDisconnected(){
        //Toast.makeText(this, "GPS Location Service Failure", Toast.LENGTH_LONG).show();
    }

}
