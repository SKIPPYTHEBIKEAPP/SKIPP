package com.example.jonathan.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class LockService extends Service implements GPSUpdate {
    GPSData initialGPSLocation = null;

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
            this.initialGPSLocation = Configuration.getLocationHandler().retrieveLastGPSData();
            Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        }
        return START_STICKY;
    }

    public void receiveUpdate(GPSData data){
        Toast.makeText(this, "GPS Update Received", Toast.LENGTH_LONG).show();
    }

    public void gpsDisconnected(){
        Toast.makeText(this, "GPS Disconnected", Toast.LENGTH_LONG).show();
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        if (Configuration.getLocationHandler() != null)
            Configuration.getLocationHandler().setAutomaticUpdates(false);
    }
}
