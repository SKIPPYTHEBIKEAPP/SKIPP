package com.example.jonathan.myapplication;

import android.content.Context;
import android.util.Log;

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
    ParticleCloud cloud = null;
    SkippyLoginInformation skippyLoginInformation = null;

    public SkippyLocation(SkippyLoginInformation skippyLoginInformation){
        this.skippyLoginInformation = skippyLoginInformation;
    }

    public void init(Context context) {
        ParticleCloudSDK.init(context);
    }

    public void login() throws Exception {
        this.cloud = ParticleCloudSDK.getCloud();
        try {
            cloud.logIn(this.skippyLoginInformation.username, this.skippyLoginInformation.password);
            // Particle API appears broken, it will appear to have logged in, but no devices will
            // be reachable in the case of a bad username/password.  Coding this as a workaround
            // to check to make sure a device is available to determine if login was successful
            if (!cloud.isLoggedIn() || cloud.getDevices().size() < 1)
                throw new Exception("Invalid Username or Password");
        } catch (Exception e) {
            throw new Exception("Invalid Username or Password");
        }
        try {
            this.device = cloud.getDevices().get(0);
            if (!this.device.isConnected())
                throw new Exception("Device not connected");
        } catch (Exception e) {
            throw new Exception("Unable to access device: " + e.getMessage());
        }
    }

    public void logout() {
        this.cloud.logOut();
    }

    public GPSData getUpdate(){
        try {
            String gpsString = device.getStringVariable("gps_data");
            return gpsDataParser(gpsString);
        } catch (Exception e) {
            Log.d("SkippyLocation", "Exception: " + e.getMessage());
            return GPSData.invalidData();
        }
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
