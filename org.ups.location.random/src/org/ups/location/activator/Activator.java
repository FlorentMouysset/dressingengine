package org.ups.location.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ups.location.ILocation;
import org.ups.location.impl.LocationImpl;


public class Activator implements BundleActivator {

	private ILocation location;
	
	//for the "location" thread management.
	private boolean stopThreadLocation;
	private Thread locationThread;

	public Activator(){
		this.location = new LocationImpl();
	}


	/**
	 * Start the "location" bundle and launch the "location thread".
	 * */
	public void start(final BundleContext context) throws Exception {

		//register location service
		Dictionary<String, String> properties = new Hashtable<String, String>();
		properties.put("name", "org.ups.location");
		context.registerService(ILocation.class.getName(), this.location, properties);
		System.out.println("Location bundle : registration done !");

		//make a runnable and launch the "location" thread
		runLocationThread();

	}

	/**
	 * Stop the "location" bundle -> stop the "location" thread.
	 * */
	public void stop(final BundleContext context) throws Exception {
		stopThread();
		System.out.println("Location bundle : stop, goodbye !");
	}
	

	
	/**
	 * Build and run properly the "location" thread.
	 * */
	private void runLocationThread(){
		Activator.this.stopThreadLocation = false;
		
		//create a new runnable "location"
		Runnable locationRunnable = new Runnable() {
			public void run() {
				System.out.println("Location bundle : the thread started.");
				
				while(!Activator.this.stopThreadLocation){ //run while the bundle are not stop
					
					try {
						Thread.sleep(10000); //sleep 10 sec
					} catch (InterruptedException e) {
						if(Activator.this.stopThreadLocation){ //"normal" interruption : the bundle need to stop
							break;
						}else{// "Abnormal" interruption
							System.out.println("Location bundle : The thread refuse to sleep ! See the follow stack trace : ");
							e.printStackTrace();
							break;
						}
					}
					
					Activator.this.location.getLatitude();//modification of location -> notify all listeners
					System.out.println("Location bundle : new location computed, all listeners are notify.");
				}
				System.out.println("Location bundle : the thread stoped.");
			}
		};

		//launch the thread
		this.locationThread = new Thread(locationRunnable);
		this.locationThread.start();
	}

	/**
	 * Stop properly the "location" thread.
	 * */
	private void stopThread(){
		this.stopThreadLocation = true; //change the flag value
		locationThread.interrupt(); // and interrupt the thread
	}
}
