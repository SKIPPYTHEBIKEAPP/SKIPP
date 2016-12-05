package com.example.jonathan.myapplication;

/**
 * Created by dave on 11/16/2016.
 */

public class DummyDataSourceConfig {
    final public double movementSpeed;
    final public double connectFailProbability;  // percent chance of simulated connection failure per update
    final public double invalidDataProbability;  // percent chance of an update returning invalid data
    final public boolean invalidLogin;           // causes dummy data source to simulate an invalid login
    final public int intervalBigJump;            // if range is between 0-59, will cause a large
                                                 // jump in location when current time
    //                                           // minutes % intervalBigJump == 0


    public DummyDataSourceConfig(){
        //movementSpeed = .000045;
        movementSpeed = 0;
        invalidDataProbability = .05;
        connectFailProbability = 0.0;
        invalidLogin = false;
        intervalBigJump = 15;
    }
}
