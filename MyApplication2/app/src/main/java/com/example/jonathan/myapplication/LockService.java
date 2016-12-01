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
            Toast.makeText(this, "Alarm Set", Toast.LENGTH_LONG).show();
            this.isRunning = true;
            Configuration.setLockService(this);
        }
        return START_STICKY;
    }

    public boolean getRunning(){
        return this.isRunning;
    }

    public void receiveUpdate(GPSData data){
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);

        // Just in case the initial data was not available when lock was set
        if (initialGPSLocation == null || initialGPSLocation.valid == false)
            initialGPSLocation = data;

        double movedDistance = data.distanceTo(initialGPSLocation);
        Log.d("Alarm Service", "Meters moved since alarm was set: " + Double.toString(movedDistance));
        //sounds alarm if location moves
        if (movedDistance > Configuration.acceptableMovement) {
            //Call back to MainActivity for alarm popup
            if (Configuration.getMainActivity() != null)
                Configuration.getMainActivity().alarmTrigger();
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
