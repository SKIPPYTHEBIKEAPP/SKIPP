package com.example.jonathan.myapplication;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dave on 11/4/2016.
 *
 * This class defines how the location information is communicated to the Android app.
 * It uses these interfaces/classes to facilitate this interaction:
 *
 * Interface: GPSUpdate
 *      This interface is to be implemented by any object that would like to subscribe
 *      to location updates.  The method receiveUpdate will be called each time new
 *      location information becomes available.
 *
 * Interface: LocationDataSource
 *      This interface defines the operations that are used by this class in order to
 *      retrieve location information.  See LocationDataSource interface code file
 *      for specific details.
 *
 * Class: GPSData
 *      This class acts as a struct that contains the GPS location details and battery
 *      state information of the device.
 *
 * Classes relating to the LocationDataSource interface and/or its implementations:
 *
 * Class: SkippyLoginInformation
 *      Contains login credentials to be used by the SkippyLocation class.
 *
 * Class: SkippyLocation
 *      Implementation of LocationDataSource interface that uses the Skippy (Particle device)
 *      for its location information.
 *
 * Class: DummyDataSource
 *      Implementation of LocationDataSource interface that provides fake data for the
 *      purpose of being able to instantiate the LocationHandler without a GPS device.
 *
 *
  */

public class LocationHandler {
    private LocationDataSource locationDataSource;  // instance of LocationDataSource to be used
    private Long checkInterval;                     // how often (in ms) to check for location update
    private Context context;                        // context for android app (used by Particle API
                                                    // as well as sending tasks to UI thread
    private Thread updateThread;                    // thread used for checking for location updates
    private final Set<GPSUpdate> notificationSet;   // collection of objects to notify upon GPS
                                                    // updates
    private final Object notificationSetLock = new Object();
                                                    // concurrency lock for notificationSet
    private GPSData lastData = null;                // last GPS data relieved
    private boolean periodicUpdatesEnabled;         // Flag for whether automatic updates are enabled
    private int minutesUntilStale;                  // Number of minutes before data is considered stale;
    private volatile boolean connected;             // Flag to indicate if data source is connected
    private volatile boolean fatalThreadError;      // Flag to indicate data source is not working
    private volatile String threadErrorMessage;     // Error message in case of thread error
    private final Object threadErrorLock = new Object();
                                                    // concurrency lock for thread errors

    public LocationHandler (LocationDataSource locationDataSource, long checkInterval,
                            int minutesUntilStale, Context context) {
        this.locationDataSource = locationDataSource;
        this.checkInterval = checkInterval;
        this.context = context;
        this.notificationSet = new HashSet<GPSUpdate>();
        this.lastData = GPSData.invalidData();
        this.minutesUntilStale = minutesUntilStale;
        this.connected = false;
        this.fatalThreadError = false;
        this.threadErrorMessage = "";
    }

    // Set the automatic GPS location update interval (in ms)
    public void setCheckInterval (long checkInterval) {
        this.checkInterval = checkInterval;
    }

    // Cause an immediate GPS location update
    public void forceUpdate() throws Exception {
        if (this.connected && this.updateThread.isAlive())
            this.updateThread.interrupt();
        else
            this.start();
    }

    // Subscribe an object to be notified of GPS updates
    public void subscribeUpdates(GPSUpdate newListener){
        synchronized (this.notificationSetLock){
            this.notificationSet.add(newListener);
        }
    }

    // Begin the automated GPS location thread
    public void start() throws Exception {
        this.updateThread = new Thread(new updateServiceThread(context,
                locationDataSource, checkInterval));
        this.updateThread.start();
        while (this.connected == false && this.fatalThreadError == false) {
            // wait to see if connection is established
            try {
                Thread.sleep(300);
                Log.d("GPS Connect", "Waiting for connection");
            } catch (Exception e) {// sleep interrupted}
            }
        }
        if (this.fatalThreadError == true)
            synchronized (this.threadErrorLock) {
                throw new Exception("Unable to connect: " + this.threadErrorMessage);
            }
        if (this.connected == false)
            throw new Exception ("No error, but not connected.  This shouldn't happen.");
    }

    public void logout() {
        this.connected = false;
        if (updateThread != null && updateThread.isAlive())
            updateThread.interrupt();
        while (updateThread.isAlive()){
            try {
                Thread.sleep(300);
                Log.d("GPS Connect", "Waiting for disconnection");
            } catch (Exception e) {// sleep interrupted}
            }
        }
    }

    public void setAutomaticUpdates(boolean periodicUpdates) {
        this.periodicUpdatesEnabled = periodicUpdates;
    }

    public boolean isConnected(){
        return this.connected;
    }

    public boolean isDataStale(){
        if (this.lastData != null && this.lastData.valid != false){
            long diffTimeInMs = this.lastData.timeStamp.getTime() - new Date().getTime(); // in ms
            long diffTimeInMinutes = Math.abs(diffTimeInMs / (1000 * 60));  // convert ms to minutes
            return (diffTimeInMinutes > this.minutesUntilStale);
        }
        return true;
    }

    /* retrieveLastGPSData
     * Return the most recent GPS data.  This method will return null if no valid information
     * has yet been relieved. */
    public GPSData retrieveLastGPSData(){
        return this.lastData;
    }

    // Private helper class for thread to update GPS information.  Must be implemented as
    // a separate thread because the IO calls are blocking and cannot be done on the UI thread
    private class updateServiceThread implements Runnable {
        private SkippyLoginInformation skippyLoginInformation;
        private Context context;
        private LocationDataSource locationDataSource;
        private long checkInterval;

        public updateServiceThread(Context context,
                                   LocationDataSource locationDataSource, long checkInterval) {
            this.context = context;
            this.locationDataSource = locationDataSource;
            this.checkInterval = checkInterval;
        }

        // Begin thread to update GPS information
        @Override
        public void run() {
            connected = false;
            try {
                this.locationDataSource.init(this.context);
            } catch (Exception e) {
                synchronized (threadErrorLock) {
                    fatalThreadError = true;
                    threadErrorMessage = "Unable to initialize data source: " + e.getMessage();
                }
            }
            try {
                this.locationDataSource.login();
            } catch (Exception e) {
                synchronized (threadErrorLock) {
                    fatalThreadError = true;
                    threadErrorMessage = "Unable to login: " + e.getMessage();
                }
            }
            connected = true;
            while (connected) {
                try {
                    final GPSData data = this.locationDataSource.getUpdate();
                    if (data.valid)     // Save result for future retrieval if data is valid
                        lastData = data;
                    // Notify all listeners of this new data if valid
                    ((AppCompatActivity) this.context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (notificationSetLock) {
                                for (GPSUpdate listener : notificationSet)
                                    listener.receiveUpdate(data);
                            }
                        }
                    });
                } catch (Exception e) {
                    synchronized (threadErrorLock) {
                        fatalThreadError = true;
                        threadErrorMessage = "Error getting update: " + e.getMessage();
                        connected = false;
                    }
                }
                try {
                    // Sleep until timeout interval or until interrupted
                    while (!periodicUpdatesEnabled)
                        Thread.sleep(this.checkInterval);
                } catch (Exception e) {
                    // Thread interrupted for an immediate update
                }
            }
            this.locationDataSource.logout();
            ((AppCompatActivity) this.context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (notificationSetLock) {
                        for (GPSUpdate listener : notificationSet)
                            listener.gpsDisconnected();
                    }
                }
            });
        }
    }
}
