package com.example.jonathan.myapplication;

/**
 * Created by dave on 11/18/2016.
 */

public class Configuration {
    private static Object ConfigurationLock = new Object();
    private static LocationHandler locationHandler = null;
    public static final long defaultAutomaticRefresh = 20000;      // in ms
    private static LockService lockService = null;
    private static MainActivity mainActivity = null;

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

    public static LockService getLockService(){
        return Configuration.lockService;
    }

    public static void setLockService(LockService lockService){
        Configuration.lockService = lockService;
    }

    public static MainActivity getMainActivity() { return Configuration.mainActivity; }

    public static void setMainActivity(MainActivity mainActivity){
        Configuration.mainActivity = mainActivity;
    }
}
