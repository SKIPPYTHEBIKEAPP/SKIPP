package com.example.jonathan.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements GPSUpdate {
    private Intent lockService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Configuration.setMainActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Configuration.getLocationHandler() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            if (Configuration.getLocationHandler().isConnected()) {
                // subscribe to notifications so disconnections can be appropriated dealt with
                Configuration.getLocationHandler().subscribeUpdates(this);
            } else {
                gpsDisconnected();
            }
        }
    }

    /**
     * Called when the user clicks the Send button
     */
    public void DisplaySetting(View view) {
        if (Configuration.getLockService() != null)
            Configuration.getLockService().onDestroy();
        if (Configuration.getLocationHandler() != null)
            Configuration.getLocationHandler().logout();
        Configuration.setLocationHandler(null);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    /**
     * Called when the user clicks the Send button
     */
    public void DisplayLocation(View view) {
        final Intent intent = new Intent(this, GPSLocation.class);

        //Test to see if there is a recent location update
        if (Configuration.getLocationHandler() != null &&
                Configuration.getLocationHandler().isDataStale()) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Not found!");
            alertDialog.setMessage("Last location reported will be shown");


            alertDialog.setButton(alertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(intent);
                        }
                    });
            alertDialog.show();


        }
        //if location has been found a dialog message will be shown stating that the gps is active and found
        else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Found!");
            alertDialog.setMessage("Current location is shown");


            alertDialog.setButton(alertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    }


    /**
     * Called when the user clicks the Send button
     */
    public void DisplayBattery(View view) {
        Intent intent = new Intent(this, Battery.class);

        startActivity(intent);
    }

    /**
     * Called when the user clicks the Send button
     */
    public void ActivateLock(View view) {
        // Check to make sure there's a connection before attempting to start lock service
        if (Configuration.getLocationHandler() == null)
            this.gpsDisconnected();

        final long msToMinutes = 1000 * 60;

        if (Configuration.getLockService() == null || (Configuration.getLockService() != null &&
                !Configuration.getLockService().getRunning())) {
            this.lockService = new Intent(this, LockService.class);
            startService(this.lockService);

            // arming popup
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Device Armed");
            alertDialog.setMessage("Location will be updated every " +
                    Long.toString(Configuration.getLocationHandler().getCheckInterval() /
                            msToMinutes) + " minutes. " +
                    "You can still manually update at any time by tapping the Location button. " +
                    "Changes in location will trigger an alarm on your device.  " +
                    "To exit armed mode, tap the lock again.");


            alertDialog.setButton(alertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //maybe something is supposed to be here?
                            //copy pasta
                        }
                    });
            alertDialog.show();

        } else {
            stopService(this.lockService);
            this.lockService = null;

            //disable popupdumm
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Device Disarmed");
            alertDialog.setMessage("By clicking this, you are disabling the alarm feature.");


            alertDialog.setButton(alertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //maybe something is supposed to be here?
                            //copy pasta

                        }
                    });
            alertDialog.show();
        }
    }

    public void alarmTrigger() {
        stopService(lockService);
        Intent intent = new Intent(this, AlarmActive.class);
        startActivity(intent);
    }

    public void receiveUpdate(GPSData data){
        // do nothing, MainActivity doesn't care about location updates
    }

    public void gpsDisconnected(){
        // Service has been disconnected.  Clear out configuration dealing with connection and
        // prompt user to reconnect.

        Toast.makeText(this, "GPS Location Service has Disconnected.  Please re-login.", Toast.LENGTH_LONG).show();

        Configuration.setLocationHandler(null);
        if (Configuration.getLockService() != null)
            Configuration.getLockService().onDestroy();
        // Restart activity to prompt re-login
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
