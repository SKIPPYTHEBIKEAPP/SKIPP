package com.example.jonathan.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
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
        }
    }

    /**
     * Called when the user clicks the Send button
     */
    public void DisplaySetting(View view) {
        if (Configuration.getLocationHandler() != null){
            Configuration.getLocationHandler().logout();
        }
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
        if (Configuration.getLockService() == null || (Configuration.getLockService() != null &&
                !Configuration.getLockService().getRunning())) {
            this.lockService = new Intent(this, LockService.class);
            startService(this.lockService);

            // arming popup
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Device Armed");
            alertDialog.setMessage("Location will be updated every 10 minutes. " +
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

            //disable popup
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

    /**
     * Called when gps moves in armed mode
     */
    public void AlarmTrigger() {

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alarm Triggered!!!");
        alertDialog.setMessage("The location of your device has changed. " +
                "Press okay OK to halt alarm sound temporarily. " +
                "To prevent further alarms, disable the Armed feature and precede to manually locate your device.");


        mp.start();

        //"OK" stops sound, at least it should...
        alertDialog.setButton(alertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mp.isPlaying()) {
                            mp.stop();
                            mp.release();
                        }
                    }
                });
        alertDialog.show();
        stopService(lockService);
    }
}
