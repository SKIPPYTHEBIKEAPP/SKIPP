package com.example.jonathan.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LockService extends Service implements GPSUpdate {
    private GPSData initialGPSLocation = null;
    private boolean isRunning = false;
    private long normalPollingInterval = 0;
    private final long rapidPollingInterval = 2000;         // in ms
    private boolean initialPolling = true;                  // flag indicating alarm is establishing
                                                            // initial location
    private boolean confirmMovement = false;                // flag indicating alarm believes that
                                                            // device has moved, and is attempting
                                                            // to confirm

    private int initialLocationCount = 0;                   // Counters for location averaging
    private int confirmLocationCount = 0;
    private final int initialLocationCountTarget = 5;       // Target number of size of data to average
    private final int confirmLocationCountTarget = 10;

    private double tempLon;                                 // Temporary variables used in location
    private double tempLat;                                 // averaging

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        if (Configuration.getLocationHandler() != null){
            Configuration.getLocationHandler().subscribeUpdates(this);
            Configuration.getLocationHandler().setAutomaticUpdates(true);
            Toast.makeText(this, "Alarm Set", Toast.LENGTH_LONG).show();
            this.isRunning = true;
            Configuration.setLockService(this);
            normalPollingInterval = Configuration.getLocationHandler().getCheckInterval();
            Configuration.getLocationHandler().setCheckInterval(rapidPollingInterval);
        }
        return START_STICKY;
    }

    public boolean getRunning(){
        return this.isRunning;
    }

    public void receiveUpdate(GPSData data){
        // Just in case the initial data was not available when lock was set
        if (initialPolling) {
            initialLocationCount++;
            double newLon = tempLon * (initialLocationCount - 1) / initialLocationCount;
            tempLon = newLon + data.lon / initialLocationCount;

            double newLat = tempLat * (initialLocationCount - 1) / initialLocationCount;
            tempLat = newLat + data.lat / initialLocationCount;
            if (initialLocationCount == initialLocationCountTarget) {
                initialPolling = false;
                initialLocationCount = 0;
                if (Configuration.getLocationHandler() != null)
                    Configuration.getLocationHandler().setCheckInterval(normalPollingInterval);

                initialGPSLocation = new GPSData(tempLat, tempLon, data.latDir, data.lonDir,
                        data.battery, data.timeStamp, data.valid);

                Log.d("Alarm Service", "Alarm initial location set: " + initialGPSLocation);
            }
        } else if (confirmMovement) {
            confirmLocationCount++;
            if (data.distanceTo(initialGPSLocation) < Configuration.acceptableMovement)
            {
                // Device has returned to acceptable range
                if (Configuration.getLocationHandler() != null)
                    Configuration.getLocationHandler().setCheckInterval(normalPollingInterval);
                Log.d("Alarm Service", "Device within range.  Cancelling alarm.");
                confirmLocationCount = 0;
                confirmMovement = false;
            } else if (confirmLocationCount == confirmLocationCountTarget && confirmMovement) {
                // Device has not returned to acceptable range
                if (Configuration.getLocationHandler() != null)
                    Configuration.getLocationHandler().setCheckInterval(normalPollingInterval);
                Log.d("Alarm Service", "Device outside range.  Sounding alarm.");
                confirmLocationCount = 0;
                confirmMovement = false;
                if (Configuration.getMainActivity() != null)
                    Configuration.getMainActivity().alarmTrigger();
            } else {
                Log.d("Alarm Service", "Device outside range, checking to see if it returns.  " +
                        "Current distance: " + data.distanceTo(initialGPSLocation));
            }
        } else {
            if (data.distanceTo(initialGPSLocation) > Configuration.acceptableMovement){
                confirmMovement = true;
                confirmLocationCount = 0;
                if (Configuration.getLocationHandler() != null)
                    Configuration.getLocationHandler().setCheckInterval(rapidPollingInterval);

                receiveUpdate(data);        // recursively call to include this location into
                                            // location averaging
            }
        }
    }

    public void gpsDisconnected(){
        //Toast.makeText(this, "GPS Disconnected", Toast.LENGTH_LONG).show();
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
        Toast.makeText(this, "Alarm Deactivated", Toast.LENGTH_LONG).show();
        if (Configuration.getLocationHandler() != null) {
            Configuration.getLocationHandler().setAutomaticUpdates(false);
            Configuration.getLocationHandler().unsubscribeUpdates(this);
        }
        Configuration.setLockService(null);
    }
    
}
