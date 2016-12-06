package com.example.jonathan.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class LockService extends Service implements GPSUpdate {
    private GPSData initialGPSLocation = null;
    private boolean isRunning = false;
    private long normalPollingInterval = 0;
    private final long rapidPollingInterval = 5000;         // in ms
    private boolean initialPolling = true;                  // flag indicating alarm is establishing
                                                            // initial location
    private boolean confirmMovement = false;                // flag indicating alarm believes that
                                                            // device has moved, and is attempting
                                                            // to confirm

    private int initialLocationCount = 0;                   // Counters for location averaging
    private int confirmLocationCount = 0;
    private final int initialLocationCountTarget = 3;       // Target number of size of data to average
    private final int confirmLocationCountTarget = 3;

    private double tempLon;                                 // Temporary variables used in location
    private double tempLat;                                 // averaging

    PowerManager.WakeLock wakeLock = null;
    WifiManager.WifiLock wifiLock = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Log.d("LockService", "starting lock service " + this.toString());
        LocationHandler locationHandler = Configuration.getLocationHandler();
        if (locationHandler != null){
            locationHandler.subscribeUpdates(this);
            locationHandler.setAutomaticUpdates(true);
            Toast.makeText(this, "Alarm Set", Toast.LENGTH_LONG).show();
            this.isRunning = true;
            Configuration.setLockService(this);
            normalPollingInterval = locationHandler.getCheckInterval();
            locationHandler.setCheckInterval(rapidPollingInterval);
            locationHandler.placeNotification();
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Skippy Secure Alarm");
            wakeLock.acquire();
            WifiManager wifiManager = (WifiManager) getSystemService(this.WIFI_SERVICE);
            if (wifiManager != null) {
                wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "Skippy Secure Alarm");
                wifiLock.acquire();
            }
        } else {
            onDestroy();
        }
        return START_STICKY;
    }

    public boolean getRunning(){
        return this.isRunning;
    }

    public void receiveUpdate(GPSData data){
        Log.d("LockService", this.toString()+ " GPS update");
        // Just in case the initial data was not available when lock was set
        LocationHandler locationHandler = Configuration.getLocationHandler();
        if (initialPolling) {
            initialLocationCount++;
            double newLon = tempLon * (initialLocationCount - 1) / initialLocationCount;
            tempLon = newLon + data.lon / initialLocationCount;

            double newLat = tempLat * (initialLocationCount - 1) / initialLocationCount;
            tempLat = newLat + data.lat / initialLocationCount;
            if (initialLocationCount == initialLocationCountTarget) {
                initialPolling = false;
                initialLocationCount = 0;
                if (locationHandler != null)
                    locationHandler.setCheckInterval(normalPollingInterval);

                initialGPSLocation = new GPSData(tempLat, tempLon, data.latDir, data.lonDir,
                        data.battery, data.timeStamp, data.valid);

                Log.d("LockService", "Alarm initial location set: " + initialGPSLocation);
            }
        } else if (confirmMovement) {
            confirmLocationCount++;
            if (data.distanceTo(initialGPSLocation) < Configuration.acceptableMovement)
            {
                // Device has returned to acceptable range
                if (locationHandler != null)
                    locationHandler.setCheckInterval(normalPollingInterval);
                Log.d("LockService", "Device within range.  Cancelling alarm.");
                confirmLocationCount = 0;
                confirmMovement = false;
            } else if (confirmLocationCount == confirmLocationCountTarget && confirmMovement) {
                // Device has not returned to acceptable range
                if (locationHandler != null)
                    locationHandler.setCheckInterval(normalPollingInterval);
                Log.d("LockService", "Device outside range.  Sounding alarm.");
                confirmLocationCount = 0;
                confirmMovement = false;
                if (Configuration.getMainActivity() != null)
                    Configuration.getMainActivity().alarmTrigger();
            } else {
                Log.d("LockService", "Device outside range, checking to see if it returns.  " +
                        "Current distance: " + data.distanceTo(initialGPSLocation));
            }
        } else {
            if (data.distanceTo(initialGPSLocation) > Configuration.acceptableMovement){
                confirmMovement = true;
                confirmLocationCount = 0;
                if (locationHandler != null)
                    locationHandler.setCheckInterval(rapidPollingInterval);

                receiveUpdate(data);        // recursively call to include this location into
                                            // location averaging
            }
        }
    }

    public void gpsDisconnected(){
        Log.d("LockService", toString() + " received GPS disconnect signal.");
        //Toast.makeText(this, "GPS Disconnected", Toast.LENGTH_LONG).show();
        this.stopSelf();
        //onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LockService", "Destroying: " + this.toString());
        this.isRunning = false;
        Toast.makeText(this, "Alarm Deactivated", Toast.LENGTH_LONG).show();
        LocationHandler locationHandler = Configuration.getLocationHandler();
        if (locationHandler != null) {
            locationHandler.setCheckInterval(normalPollingInterval);
            locationHandler.setAutomaticUpdates(false);
            locationHandler.unsubscribeUpdates(this);

            // reset polling interval in case alarm is being turned off while the polling interval
            // has been set short.
            locationHandler.setCheckInterval(normalPollingInterval);
        }
        Configuration.setLockService(null);
        Configuration.setLockIntent(null);
        if (locationHandler != null)
            locationHandler.placeNotification();
        if (wakeLock != null)
            wakeLock.release();
        if (wifiLock != null)
            wifiLock.release();
    }
    
}
