package com.example.jonathan.myapplication;

/**
 * Created by dave on 11/16/2016.
 */

public class DummyDataSourceConfig {
    final public double movementSpeed;
    final public double connectFailProbability;  // percent chance of simulated connection failure per update
    final public double invalidDataProbability;  // percent chance of an update returning invalid data
    final public boolean invalidLogin;


    public DummyDataSourceConfig(){
        movementSpeed = .00003;
        invalidDataProbability = .05;
        connectFailProbability = 0.0;
        invalidLogin = false;
    }
}
