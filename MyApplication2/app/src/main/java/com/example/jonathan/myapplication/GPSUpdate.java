package com.example.jonathan.myapplication;

import com.google.android.gms.maps.model.LatLng;

/**
 * Interface to be implemented by any class that needs to receive GPS information updates when
 * they are received.  The GPSUpdate method will be called each time there is a GPS update.
 */

public interface GPSUpdate {
    public void receiveUpdate(GPSData data);
}
