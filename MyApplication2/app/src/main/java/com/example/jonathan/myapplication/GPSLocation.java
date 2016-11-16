package com.example.jonathan.myapplication;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
    private LocationHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslocation);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        handler = MainActivity.getHandler();
        //SkippyLoginInformation login = new SkippyLoginInformation("dwongyee@gmail.com", "123456");
        //LocationDataSource source = new SkippyLocation();
        //LocationDataSource source = new DummyDataSource();
        //handler = new LocationHandler(source, 10000, login, this);
        handler.subscribeUpdates(this);
        //handler.start();
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
        receiveUpdate(handler.retrieveLastGPSData());
    }

    // GPSUpdate interface:

    public void receiveUpdate(GPSData data){
        if ((this.mMap != null && data != null) && data.valid) {
            double lat = data.lat;
            double lon = data.lon;

            if (data.latDir == 'S')
                lat *= -1;
            if (data.lonDir == 'W')
                lon *= -1;

            LatLng currentLocation = new LatLng(lat, lon);

            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
    }

    public void gpsDisconnected(){
        Toast.makeText(this, "GPS Location Service Failure", Toast.LENGTH_LONG).show();
    }

}
