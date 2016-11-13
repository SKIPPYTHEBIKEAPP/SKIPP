package com.example.jonathan.myapplication;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;

/**
 * Implementation of LocationDataSource that uses the Particle API to recieve location information
 */

public class SkippyLocation implements LocationDataSource {
    ParticleDevice device = null;

    public void init(Context context) {
        ParticleCloudSDK.init(context);
    }

    public void login(LoginInformation loginInformation) {
        try {
            ParticleCloud cloud = ParticleCloudSDK.getCloud();
            cloud.logIn(loginInformation.username, loginInformation.password);
            device = cloud.getDevices().get(0);
        } catch (Exception e) {}
    }

    public GPSData getUpdate(){
        try {
            String gpsString = device.getStringVariable("gps_data");
            return gpsDataParser(gpsString);
        } catch (Exception e) { return null; }
    }

    public GPSData gpsDataParser(String gpsString) {
        double lat = 0;
        double lon = 0;
        char latDir = (char) 0;
        char lonDir = (char) 0;
        double battery = 0;
        boolean valid = false;
        Date timeStamp = null;

        try {
            // Split off battery life component from string
            String[] batterySplit = gpsString.split("\\$");
            if (batterySplit.length != 2) {
                return GPSData.invalidData();
            }
            battery = Double.parseDouble(batterySplit[0]);

            // Split off checksum component of string
            String[] checksumSplit = batterySplit[1].split("\\*");
            int providedChecksum = Integer.parseInt(checksumSplit[1].trim(), 16);
            int calculatedChecksum = 0;

            // Validate checksum
            for (char c: checksumSplit[0].toCharArray())
                calculatedChecksum ^= c;
            valid = (calculatedChecksum == providedChecksum);

            if (valid) {
                String[] gpsComponents = checksumSplit[0].split(",");

                // Extract lat/lon components from result string
                latDir = gpsComponents[3].charAt(0);
                lonDir = gpsComponents[5].charAt(0);
                double latCalc = Double.parseDouble(gpsComponents[2]);
                double lonCalc = Double.parseDouble(gpsComponents[4]);

                //Format date to UTC and use today's date
                SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss MMddyyyy");
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
                timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                timeStamp = timeFormat.parse(gpsComponents[1].split("\\.")[0] +
                        " " + dateFormat.format(new Date()));

                // Perform arithmetic to GPS coordinates per GPS data spec
                lat = (int)latCalc / 100;
                lon = (int)lonCalc / 100;

                // Take the remainder of the /100 operation and divide by 60, then add back in
                lat += (latCalc % 100) / 60;
                lon += (lonCalc % 100) / 60;
            }
        } catch (Exception e)
        {
            valid = false;
        }

        return new GPSData(lat, lon, latDir, lonDir, battery, timeStamp, valid);
    }
}
