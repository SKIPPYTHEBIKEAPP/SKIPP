package com.example.jonathan.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class AlarmActive extends AppCompatActivity {
    AlertDialog alertDialog;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("AlarmDialog", "Activating alarm "+ this.toString());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_active);

        /**
         * Called when gps moves in armed mode
         */

        mp = MediaPlayer.create(this, R.raw.alarm);

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                Intent intent = new Intent(AlarmActive.this, MainActivity.class);
                startActivity(intent);
            }
        });

        alertDialog.setTitle("Alarm Triggered!!!");
        alertDialog.setMessage("The location of your device has changed. " +
                "Press okay OK to halt alarm sound. ");
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mp.start();

        //"OK" stops sound, at least it should...
        alertDialog.setButton(alertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mp.isPlaying()) {
                            mp.stop();
                        }
                        Log.d("AlarmDialog", "Deactivating alarm "+ this.toString());
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        Log.d("AlarmDialog", "onStop dismissing alarm.");
        super.onDestroy();
        if (alertDialog != null) {
            alertDialog.dismiss();
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }
}
