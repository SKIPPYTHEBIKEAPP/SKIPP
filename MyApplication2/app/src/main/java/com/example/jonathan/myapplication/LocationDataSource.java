package com.example.jonathan.myapplication;

import android.content.Context;

/**
 * Interface to be implemented by a location information data source.  These functions will
 * be called on by LocationHandler, and in turn exposed to the application.
 */

public interface LocationDataSource {
    // This method will be called before login
    public void init(Context context) throws Exception;

    // This method will be called before GPS updates are requested
    public void login(LoginInformation loginInformation) throws Exception;

    // This method will be called to log out of data source
    public void logout();

    // This method is called to request the current location information
    public GPSData getUpdate() throws Exception;
}
