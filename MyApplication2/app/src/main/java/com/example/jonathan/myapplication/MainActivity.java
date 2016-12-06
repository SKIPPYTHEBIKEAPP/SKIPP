package com.example.jonathan.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements GPSUpdate {
    private Intent lockService;
    ImageButton imgButton;
    ImageButton imgButton2;
    private double battery = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", this.toString() + " onCreate");
        setContentView(R.layout.activity_main);
        Configuration.setMainActivity(this);
        this.lockService = Configuration.getLockIntent();
        lockButton();
        batteryPercentage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", this.toString() + " onStart");
        LocationHandler locationHandler = Configuration.getLocationHandler();
        if (locationHandler == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            if (locationHandler.isConnected()) {
                // subscribe to notifications so disconnections can be appropriated dealt with
                locationHandler.subscribeUpdates(this);
            } else {
                gpsDisconnected();
            }
        }
        //subscribes to battery info
        if (locationHandler != null) {
            receiveUpdate(locationHandler.retrieveLastGPSData());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLock();
    }

    /**
     * Called when the user clicks the Send button
     */
    public void DisplaySetting(View view) {
        Log.d("MainActivity", this.toString() + " logout button");
        LocationHandler locationHandler = Configuration.getLocationHandler();
        LockService lockService = Configuration.getLockService();
        if (lockService != null)
            lockService.onDestroy();
        if (locationHandler != null)
            locationHandler.logout();
        Configuration.setLocationHandler(null);

    }


    /**
     * Called when the user clicks the Send button
     */
    public void DisplayLocation(View view) {
        Log.d("MainActivity", this.toString() + " map button");
        final Intent intent = new Intent(this, GPSLocation.class);

        //Test to see if there is a recent location update
        LocationHandler locationHandler = Configuration.getLocationHandler();
        if (locationHandler != null && locationHandler.isDataStale()) {
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
    public void ActivateLock(View view) {
        Log.d("MainActivity", this.toString() + " lock button");
        // Check to make sure there's a connection before attempting to start lock service
        LocationHandler locationHandler = Configuration.getLocationHandler();
        if (locationHandler == null)
            this.gpsDisconnected();
        else {

            final long msToMinutes = 1000 * 60;

            if (Configuration.getLockService() == null || (Configuration.getLockService() != null &&
                    !Configuration.getLockService().getRunning())) {
                this.lockService = new Intent(this, LockService.class);
                startService(this.lockService);
                Configuration.setLockIntent(this.lockService);

                // arming popup
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Device Armed");
                alertDialog.setMessage("Location will be updated every " +
                        Long.toString(locationHandler.getCheckInterval() /
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
                Configuration.setLockIntent(this.lockService);

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
    }

    public void alarmTrigger() {
        Log.d("MainActivity", this.toString() + " triggering alarm");
        stopService(lockService);
        this.lockService = null;
        Configuration.setLockIntent(this.lockService);
        Intent intent = new Intent(this, AlarmActive.class);
        startActivity(intent);
    }

    public void receiveUpdate(GPSData data) {
        Log.d("MainActivity", this.toString() + " received gps update");
        if (data.valid) {

            battery = data.battery;
            batteryPercentage();
        }

    }

    public void gpsDisconnected() {
        Log.d("MainActivity", this.toString() + " gps disconnect signal");
        // Service has been disconnected.  Clear out configuration dealing with connection and
        // prompt user to reconnect.

        // Restart activity to prompt re-login
        Toast.makeText(this, "GPS Location Service has Disconnected.  Please re-login.",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void updateLock() {
        if (Configuration.getLockService() != null) {
            imgButton.setVisibility(View.INVISIBLE);
            imgButton2.setVisibility(View.VISIBLE);
        } else {
            imgButton.setVisibility(View.VISIBLE);
            imgButton2.setVisibility(View.INVISIBLE);
        }
    }

    private void lockButton() {


        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);

        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        LinearLayout linearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);
        imgButton = new ImageButton(this);
        imgButton.setImageResource(R.drawable.whiteunlock);
        imgButton.setAdjustViewBounds(true);
        imgButton.setBackgroundColor(Color.TRANSPARENT);
        if (Configuration.getLockService() != null)
            imgButton.setVisibility(View.INVISIBLE);

        imgButton2 = new ImageButton(this);
        imgButton2.setImageResource(R.drawable.redlock);
        imgButton2.setAdjustViewBounds(true);
        imgButton2.setBackgroundColor(Color.TRANSPARENT);
        if (Configuration.getLockService() == null)
            imgButton2.setVisibility(View.INVISIBLE);

        linearLayout3.addView(imgButton2);


        linearLayout2.addView(imgButton);

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgButton.getVisibility() == View.VISIBLE) {
                    imgButton.setVisibility(View.INVISIBLE);
                    imgButton2.setVisibility(View.VISIBLE);
                    ActivateLock(v);

                } else if (imgButton2.getVisibility() == View.VISIBLE) {
                    imgButton.setVisibility(View.VISIBLE);
                    imgButton2.setVisibility(View.INVISIBLE);
                    ActivateLock(v);
                }
            }


        });
        imgButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgButton.getVisibility() == View.VISIBLE) {
                    imgButton.setVisibility(View.INVISIBLE);
                    imgButton2.setVisibility(View.VISIBLE);
                    ActivateLock(v);

                } else if (imgButton2.getVisibility() == View.VISIBLE) {
                    imgButton.setVisibility(View.VISIBLE);
                    imgButton2.setVisibility(View.INVISIBLE);
                    ActivateLock(v);
                }
            }


        });


        ImageView image = new ImageView(MainActivity.this);
    }

    public void batteryPercentage() {

        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);

        ImageView image = new ImageView(MainActivity.this);

        image.setBackgroundResource(R.drawable.movie);

        AnimationDrawable anim = (AnimationDrawable) image.getBackground();

        for (int i = 0; i <= 4; i++) {
            anim.start();

        }
        anim.start();

        if (battery >= 100) {
            linearLayout1.removeAllViews();
            image.setImageResource(R.drawable.batteryfull);
        } else if (battery < 99 & battery >= 75) {
            linearLayout1.removeAllViews();
            image.setImageResource(R.drawable.battery75);
        } else if (battery < 75 & battery >= 50) {
            linearLayout1.removeAllViews();
            image.setImageResource(R.drawable.battery50);
        } else if (battery < 50 & battery >= 25) {
            linearLayout1.removeAllViews();
            image.setImageResource(R.drawable.battery25);
        }else if (battery < 25 & battery >= 10) {
            linearLayout1.removeAllViews();
            image.setImageResource(R.drawable.batterylow);
        }else if (battery < 10 & battery >= 0) {
            linearLayout1.removeAllViews();
            image.setImageResource(R.drawable.batteryzero);
        }
        else {

        }

        linearLayout1.addView(image);


    }
}
