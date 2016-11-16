package com.example.jonathan.myapplication;

import com.google.android.gms.maps.model.LatLng;

/**
 * Interface to be implemented by any class that needs to receive GPS information updates when
 * they are received.  The GPSUpdate method will be called each time there is a GPS update.
 * In the event that the data source becomes disconnected, gpsDiconnected will be called.
 */

public interface GPSUpdate {
    public void receiveUpdate(GPSData data);
    public void gpsDisconnected();
}
