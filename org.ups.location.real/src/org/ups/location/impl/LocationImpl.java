package org.ups.location.impl;

import java.util.ArrayList;
import java.util.List;

import org.ups.location.ILocation;
import org.ups.location.ILocationListener;

public class LocationImpl implements ILocation {

 
    private static final int WAITING_DELAY = 10000;
    private volatile boolean locating = true;

    private List<ILocationListener> listeners = new ArrayList<ILocationListener>();
    private float latitude;
    private float longitude;

    /**
     * Creates the location service.
     */
    public LocationImpl() {
       
    }

    @Override
    public float getLatitude() {
        return latitude;
    }

    @Override
    public float getLongitude() {
        return longitude;
    }

    @Override
    public void addListener(ILocationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ILocationListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Terminates location process properly.
     */
    public void terminate() {
        locating = false;
    }

    /**
     * Updates the location by generating random place on Earth.
     */
    private void update() {
        // Keeps the old location.
        float oldLatitude = latitude;
        float oldLongitude = longitude;


        // Checks if a change has occurred.
        fireLocationChanged(oldLatitude, oldLongitude, latitude, longitude);
    }

    /**
     * Checks if the location has been changed and if so, fires it.
     * 
     * @param oldLatitude
     *            the old latitude
     * @param oldLongitude
     *            the old longitude
     * @param newLatitude
     *            the new latitude
     * @param newLongitude
     *            the new longitude
     */
    private void fireLocationChanged(float oldLatitude, float oldLongitude,
            float newLatitude, float newLongitude) {
        if (newLatitude != oldLatitude || newLongitude != oldLongitude) {
            for (ILocationListener listener : listeners) {
                listener.locationChanged(newLatitude, newLongitude);
            }
        }
    }

}
