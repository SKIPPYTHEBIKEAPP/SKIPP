package com.example.jonathan.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class LockService extends Service implements GPSUpdate {
    private GPSData initialGPSLocation = null;
    private boolean isRunning = false;

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
            this.isRunning = true;
            Configuration.setLockService(this);
        }
        return START_STICKY;
    }

    public boolean getRunning(){
        return this.isRunning;
    }

    public void receiveUpdate(GPSData data){
        Toast.makeText(this, "GPS Update Received", Toast.LENGTH_LONG).show();
        
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);

        //sounds alarm if location moves
        //data +/- 0.0003 is based off of mapping a reasonable move distance
        //currently crashes app.
        if (((data.lat + 0.0003) > this.initialGPSLocation.lat || (data.lon + 0.0003) > this.initialGPSLocation.lon)
                || ((data.lat - 0.0003) < this.initialGPSLocation.lat || (data.lon - 0.0003) < this.initialGPSLocation.lon)){



            //Call back to MainActivity for alarm popup
            if (Configuration.getMainActivity() != null)
                Configuration.getMainActivity().alarmTrigger();
        }
    }

    public void gpsDisconnected(){
        Toast.makeText(this, "GPS Disconnected", Toast.LENGTH_LONG).show();
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        if (Configuration.getLocationHandler() != null)
            Configuration.getLocationHandler().setAutomaticUpdates(false);
        Configuration.setLockService(null);
        Configuration.getLocationHandler().unsubscribeUpdates(this);
    }
    
}
