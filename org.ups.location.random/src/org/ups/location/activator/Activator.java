package org.ups.location.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ups.location.ILocation;
import org.ups.location.impl.RandomLocation;


public class Activator implements BundleActivator {

	private ILocation location;
	
	public Activator(){
		this.location = new RandomLocation();
	}


	/**
	 * Start the "location" bundle and launch the "location thread".
	 * */
	public void start(final BundleContext context) throws Exception {

		//register location bundle
		Dictionary<String, String> properties = new Hashtable<String, String>();
		properties.put("name", "org.ups.locationrandom");
		context.registerService(ILocation.class.getName(), this.location, properties);
		System.out.println("Location bundle : registration done !");

		//make a runnable and launch the "location" thread
		new Thread((Runnable) this.location).start();

	}

	/**
	 * Stop the "location" bundle -> stop the "location" thread.
	 * */
	public void stop(final BundleContext context) throws Exception {
		((RandomLocation) this.location).stopThread();
		this.location = null;
		System.out.println("Location bundle : stop, goodbye !");
	}

}
