package com.example.jonathan.myapplication;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

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
    private LoginInformation loginInformation;      // remove this
    private Context context;                        // context for android app (used by Particle API
                                                    // as well as sending tasks to UI thread
    private Thread updateThread;                    // thread used for checking for location updates
    private final Set<GPSUpdate> notificationSet;   // collection of objects to notify upon GPS
                                                    // updates
    private final Object notificationSetLock = new Object();
                                                    // concurrency lock for notificationSet
    private GPSData lastData = null;                // last GPS data relieved

    public LocationHandler (LocationDataSource locationDataSource, long checkInterval,
                            LoginInformation loginInformation, Context context) {
        this.locationDataSource = locationDataSource;
        this.checkInterval = checkInterval;
        this.loginInformation = loginInformation;
        this.context = context;
        this.notificationSet = new HashSet<GPSUpdate>();
    }

    // Set the automatic GPS location update interval (in ms)
    public void setCheckInterval (long checkInterval) {
        this.checkInterval = checkInterval;
    }

    // Cause an immediate GPS location update
    public void forceUpdate() {
        if (this.updateThread.isAlive())
            this.updateThread.interrupt();
    }

    // Subscribe an object to be notified of GPS updates
    public void subscribeUpdates(GPSUpdate newListener){
        synchronized (this.notificationSetLock){
            this.notificationSet.add(newListener);
        }
    }

    // Begin the automated GPS location thread
    public void start() {
        this.updateThread = new Thread(new updateServiceThread(loginInformation, context,
                locationDataSource, checkInterval));
        this.updateThread.start();
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
        private LoginInformation loginInformation;
        private Context context;
        private LocationDataSource locationDataSource;
        private long checkInterval;

        public updateServiceThread(LoginInformation loginInformation, Context context,
                                   LocationDataSource locationDataSource, long checkInterval) {
            this.loginInformation = loginInformation;
            this.context = context;
            this.locationDataSource = locationDataSource;
            this.checkInterval = checkInterval;
        }

        // Begin thread to update GPS information
        @Override
        public void run() {
            this.locationDataSource.init(this.context);
            this.locationDataSource.login(this.loginInformation);
            while (true) {
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
                try {
                    // Sleep until timeout interval or until interrupted
                    Thread.sleep(this.checkInterval);
                } catch (Exception e) {
                    // Thread interrupted for an immediate update
                }
            }
        }
    }
}
