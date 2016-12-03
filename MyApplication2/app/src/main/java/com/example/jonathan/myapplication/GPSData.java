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
                   Date timeStamp, boolean valid) {
        this.lat = lat;
        this.lon = lon;
        this.latDir = latDir;
        this.lonDir = lonDir;
        this.battery = battery;
        this.timeStamp = timeStamp;
        this.valid = valid;
    }

    public static GPSData invalidData() {
        return new GPSData(0, 0, (char) 0, (char) 0, 0, new Date(), false);
    }

    @Override
    public String toString() {
        return (Double.toString(lat) + " " + latDir + " by " + Double.toString(lon) + " " +
                lonDir + " Battery status: " + battery + " timestamp " + timeStamp +
                " Valid: " + valid);
    }

    public double distanceTo(GPSData other) {
        if (other == null)
            return 0;
        return distance(convertLatLon(this.lat, this.latDir),
                        convertLatLon(other.lat, other.latDir),
                        convertLatLon(this.lon, this.lonDir),
                        convertLatLon(other.lon, other.lonDir), 0, 0);
    }

    public static double convertLatLon(double latlon, char dir) {
        if (dir == 'S' || dir == 'W')
            latlon *= -1;
        return latlon;
    }
 /*
 * Shamelessly stolen from:
 * http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
 *
 * Calculate distance between two points in latitude and longitude taking
 * into account height difference. If you are not interested in height
 * difference pass 0.0. Uses Haversine method as its base.
 *
 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
 * el2 End altitude in meters
 * @returns Distance in Meters
 */
    private static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
