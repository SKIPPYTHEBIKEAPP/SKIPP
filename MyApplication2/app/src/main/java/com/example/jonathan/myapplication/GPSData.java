package com.example.jonathan.myapplication;

import java.util.Date;

/**
 * Struct object to contain necessary data for GPS information
 */

public class GPSData {
    final double lat;       // Lat in degrees
    final double lon;       // Lon in degrees
    final char latDir;      // Lat direction
    final char lonDir;      // Lon direction
    final double battery;   // Battery in percentage remaining
    final Date timeStamp;   // Time stamp for this data
    final boolean valid;    // Flag indicating if this data is valid
                            // If this flag is set, the data should not be used.  This flag might
                            // be set in the case of an invalid checksum or any other issue
                            // that causes the data not to parse correctly.

    public GPSData(double lat, double lon, char latDir, char lonDir, double battery,
                   Date timeStamp, boolean valid){
        this.lat = lat;
        this.lon = lon;
        this.latDir = latDir;
        this.lonDir = lonDir;
        this.battery = battery;
        this.timeStamp = timeStamp;
        this.valid = valid;
    }

    public static GPSData invalidData(){
        return new GPSData(0, 0, (char) 0, (char) 0, 0, null, false);
    }
}
