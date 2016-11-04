package com.example.jonathan.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user clicks the Send button */
    public void DisplaySetting(View view) {
        Intent intent = new Intent(this, Settings.class);

        startActivity(intent);
    }
    /** Called when the user clicks the click button
    public void DisplayLocation(View view) {
        Intent intent = new Intent(this, Location.class);

        startActivity(intent);
    }

    /** Called when the user clicks the Send button */
    public void DisplayLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
    }
    /** Called when the user clicks the Send button */
    public void DisplayLocation(View view) {
        Intent intent = new Intent(this, Location.class);

        startActivity(intent);
    }
    /** Called when the user clicks the Send button */
    public void DisplayBattery(View view) {
        Intent intent = new Intent(this, Location.class);

        startActivity(intent);
    }





}
