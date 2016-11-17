package com.example.jonathan.myapplication;

/**
 * Created by dave on 11/16/2016.
 */

public class DummyDataSourceConfig {
    final public double movementSpeed;
    final public float connectFailProbability;  // percent chance of simulated connection failure per update
    final public float invalidDataProbability;  // percent chance of an update returning invalid data
    final public boolean invalidLogin;


    public DummyDataSourceConfig(){
        movementSpeed = .001;
        invalidDataProbability = 0;
        connectFailProbability = 0;
        invalidLogin = false;
    }
}
