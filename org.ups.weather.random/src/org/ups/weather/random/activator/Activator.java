package org.ups.weather.random.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ups.location.ILocation;
import org.ups.location.ILocationListener;
import org.ups.weather.IWeather;
import org.ups.weather.impl.RandomWeather;

public class Activator implements BundleActivator{

	private IWeather weather = null;
	private ILocation location = null;
	private ILocationListener locationListener = null;
	private Runnable thread = null;


	public Activator() {
		this.weather = new RandomWeather();
		//make a runnable and launch the "location" thread
		//launch the thread
	//	System.out.println("ici const weather random bisd");
		
		//Runnable tmp = (Runnable) weather;
		//System.out.println("ici 2");
		this.thread =  new Thread((Runnable) weather);
		//System.out.println("thread lauch");
		//this.weather = (IWeather) this.thread; ERREUR !!
		//System.out.println("ici 4");
	}

	/**
	 * Start the "weather" bundle and launch the "weather thread".
	 * */
	public void start(BundleContext context) throws Exception {

		//register weather bundle
		Dictionary<String, String> properties = new Hashtable<String, String>();
		properties.put("name", "org.ups.weatherrandom");
		context.registerService(IWeather.class.getName(), this.weather, properties);

		System.out.println("Weather bundle : registration done !");


		//get location service (random version)
		//TODO filtrage !
		//ServiceReference<?>[] references = context.getServiceReferences(ILocation.class.getName(), "(name=org.ups.locationrandom)");
		ServiceReference<?>[] references = context.getServiceReferences(ILocation.class.getName(), "(name=org.ups.locationrandom)");

		//System.out.println("check0 = " + references.length);
		ServiceReference<?> reference = references[0];
		//System.out.println("check1 = " + reference);
		//register by location services
		this.location = ((ILocation) context.getService(reference));
		//System.out.println("check2 = " + location);
		this.locationListener = new ILocationListener() {

			public void locationChanged(float lan, float lon) {
				System.out.println("Weather bundle : locationChanged CallBack.");
				//System.out.println("Current weather: " + );
				((RandomWeather) weather).computedWeather(lan, lon);
			}
		};
		this.location.addListener(this.locationListener);
		((RandomWeather) this.weather).setLocation(this.location);
		
		((Thread) this.thread).start();
	//	System.out.println("fin start acti loc random");
	}

	/**
	 * Stop the "weather" bundle -> stop the "weather" thread.
	 * */
	public void stop(BundleContext context) throws Exception {
		((RandomWeather) this.weather).stopThread();

		//System.out.println("stopThread ok loca");
		this.location.removeListener(this.locationListener);
		//System.out.println("remove listener location ok");
		this.weather = null;
		this.location = null;
		System.out.println("Weather bundle : stop, goodbye !");
	}


}
