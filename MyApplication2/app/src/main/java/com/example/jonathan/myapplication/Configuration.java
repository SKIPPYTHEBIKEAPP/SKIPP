package com.example.jonathan.myapplication;

/**
 * Created by dave on 11/18/2016.
 */

public class Configuration {
    private static Object ConfigurationLock = new Object();
    private static LocationHandler locationHandler = null;
    public static final long defaultAutomaticRefresh = 10000;      // in ms

    public static LocationHandler getLocationHandler(){
        return Configuration.locationHandler;
    }

    public static void setLocationHandler(LocationHandler locationHandler){
        Configuration.locationHandler = locationHandler;
    }

    public static void setLocationHandlerIfNull(LocationHandler locationHandler){
        synchronized (ConfigurationLock){
            if (Configuration.locationHandler == null)
                Configuration.locationHandler = locationHandler;
        }
    }
}
