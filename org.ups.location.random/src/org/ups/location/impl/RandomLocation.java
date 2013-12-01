package org.ups.location.impl;

import java.util.ArrayList;
import java.util.List;

import org.ups.location.ILocation;
import org.ups.location.ILocationListener;

public class RandomLocation implements ILocation, Runnable {

	private List<ILocationListener> listeners;
	private float latitude;
	private float longitude;
	
	
	//for the "location" thread management.
	private boolean stopThreadLocation;
	//private Thread locationThread;
	

	public RandomLocation() {
		super();
		this.listeners = new ArrayList<ILocationListener>();
	}

	public float getLatitude() {
		this.latitude = (float) Math.random();
		return this.latitude;
	}

	public float getLongitude() {
		this.longitude =  (float) Math.random();
		return this.longitude;
	}

	private void updateLocation(){
		getLongitude();
		getLatitude();
		fireLocationChanged(this.latitude, this.longitude);
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
	
	
	

	
	public void run() {
		System.out.println("Location bundle : the thread started.");
		
		while(!this.stopThreadLocation){ //run while the bundle are not stop
			this.updateLocation();//modification of location -> notify all listeners
			System.out.println("Location bundle : new location computed, all listeners are notify.");
			
			try {
				Thread.sleep(15000); //sleep 15 sec
			} catch (InterruptedException e) {
					System.out.println("Location bundle : The thread refuse to sleep ! See the follow stack trace : ");
					e.printStackTrace();
			}
		}
	}

	/**
	 * Stop properly the "location" thread.
	 * */
	public void stopThread(){
		this.stopThreadLocation = true; //change the flag value
	}
	



}
