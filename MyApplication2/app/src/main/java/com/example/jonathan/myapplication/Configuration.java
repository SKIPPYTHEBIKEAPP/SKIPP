package com.example.jonathan.myapplication;

import android.content.Intent;
import android.util.Log;

/**
 * Created by dave on 11/18/2016.
 */

public class Configuration {
    private static Object ConfigurationLock = new Object();
    private static LocationHandler locationHandler = null;
    public static final long defaultAutomaticRefresh = 200000;       // in ms
    public static final long invalidDataRecheckInterval = 5000;     // in ms
    public static final long acceptableMovement = 10;               // in meters
    private static LockService lockService = null;
    private static Intent lockIntent = null;
    private static MainActivity mainActivity = null;

    public static LocationHandler getLocationHandler(){
        return Configuration.locationHandler;
    }

    public static void setLocationHandler(LocationHandler locationHandler){
        Log.d("Configuration", " setting locationHandler");
        synchronized (ConfigurationLock){
            Configuration.locationHandler = locationHandler;
        }
    }

    public static void setLocationHandlerIfNull(LocationHandler locationHandler){
        synchronized (ConfigurationLock){
            if (Configuration.locationHandler == null)
                Log.d("Configuration", " setting locationHandler");
                Configuration.locationHandler = locationHandler;
        }
    }

    public static LockService getLockService(){
        return Configuration.lockService;
    }

    public static void setLockService(LockService lockService){
        Log.d("Configuration", " setting lockservice");
        Configuration.lockService = lockService;
    }

    public static MainActivity getMainActivity() { return Configuration.mainActivity; }

    public static void setMainActivity(MainActivity mainActivity){
        synchronized (ConfigurationLock) {
            if (Configuration.mainActivity != mainActivity && Configuration.mainActivity != null) {
                LocationHandler handler = Configuration.locationHandler;
                if (handler != null)
                    handler.unsubscribeUpdates(Configuration.mainActivity);
            }
            Log.d("Configuration", " setting mainActivity");
            Configuration.mainActivity = mainActivity;
        }
    }

    public static Intent getLockIntent() { return Configuration.lockIntent; }

    public static void setLockIntent(Intent lockIntent) {
        Log.d("Configuration", " setting lockIntent");
        Configuration.lockIntent = lockIntent;
    }

}
