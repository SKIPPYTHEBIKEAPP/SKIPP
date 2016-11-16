package com.example.jonathan.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static LocationHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Not sure what this code does?  It manipulated the UI though, so it has to be done
        // on the UI thread, so added I "runOnUiThread" to this.
        this.runOnUiThread(
            new Thread(new Runnable() {
                public void run() {

                    setContentView(R.layout.activity_main);
                }
            }));

        //SkippyLoginInformation login = new SkippyLoginInformation("dwongyee@gmail.com", "123456");
        //LocationDataSource source = new SkippyLocation(login);

        DummyDataSourceConfig dummyConfig = new DummyDataSourceConfig();
        LocationDataSource source = new DummyDataSource(dummyConfig);


        handler = new LocationHandler(source, 30000, 15, this);
        // handler.subscribeUpdates(this);
        try {
            handler.start();
            Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    public static LocationHandler getHandler(){
        return handler;
    }

    /**
     * Called when the user clicks the Send button
     */
    public void DisplaySetting(View view) {
        Intent intent = new Intent(this, Settings.class);


        startActivity(intent);
    }


    /**
     * Called when the user clicks the Send button
     */
    public void DisplayLocation(View view) {
        final Intent intent = new Intent(this, GPSLocation.class);

        //Test to see if there is a recent location update
        if (this.handler.isDataStale()) {
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
}
