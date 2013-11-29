package org.ups.location.impl;

import java.util.ArrayList;
import java.util.List;

import org.ups.location.ILocation;
import org.ups.location.ILocationListener;

public class LocationImpl implements ILocation {

	private List<ILocationListener> listeners;
	private float latitude;
	private float longitude;
	

	public LocationImpl(List<ILocationListener> listeners) {
		super();
		this.listeners = new ArrayList<ILocationListener>();
	}

	public float getLatitude() {
		float newLatitude =  (float) Math.random();
		if(this.latitude != newLatitude ){ 
			this.latitude = newLatitude;
			fireLocationChanged(this.latitude, this.longitude);
		}
		return this.latitude;
	}

	public float getLongitude() {
		float newLongitude =  (float) Math.random();
		if(this.longitude != newLongitude ){ 
			this.longitude = newLongitude;
			fireLocationChanged(this.latitude, this.longitude);
		}
		return this.longitude;
	}

	
	private void fireLocationChanged(float latitude, float longitude){
		for  (ILocationListener listener: this.listeners){
			listener.locationChanged(latitude, longitude);
		}
	}
	
	public void addListener(ILocationListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(ILocationListener listener) {
		this.listeners.remove(listener);
	}

}
