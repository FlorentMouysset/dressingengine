package org.ups.weather.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ups.location.ILocation;
import org.ups.location.ILocationListener;
import org.ups.weather.IWeather;
import org.ups.weather.IWeatherCompute;
import org.ups.weather.impl.WeatherImpl;

public class Activator implements BundleActivator{

	private IWeatherCompute weather = null;
	private ILocation location = null;
	private ILocationListener locationListener = null;

	//for the "weather" thread management.
	private boolean stopThreadWeather;
	private Thread weatherThread;

	public Activator() {
		this.weather = new WeatherImpl();
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
		ServiceReference<?>[] references = context.getServiceReferences(ILocation.class.getName(), "(name=*)");

		System.out.println("check0 = " + references.length);
		ServiceReference<?> reference = references[0];
		System.out.println("check1 = " + reference);
		//register by location services
		this.location = ((ILocation) context.getService(reference));
		System.out.println("check2 = " + location);
		this.locationListener = new ILocationListener() {

			public void locationChanged(float lan, float lon) {
				System.out.println("Weather bundle : locationChanged CallBack.");
				weather.computedWeather(lan, lon);
			}
		};
		this.location.addListener(this.locationListener);

		
		//make a runnable and launch the "location" thread
		runWeatherThread();
	}

	/**
	 * Stop the "weather" bundle -> stop the "weather" thread.
	 * */
	public void stop(BundleContext context) throws Exception {
		stopThread();
		this.location.removeListener(this.locationListener);
		this.weather = null;
		this.location = null;
		System.out.println("Weather bundle : stop, goodbye !");
	}

	
	
	/**
	 * Build and run properly the "weather" thread.
	 * */
	private void runWeatherThread(){
		Activator.this.stopThreadWeather = false;
		
		//create a new runnable "weather"
		Runnable weatherRunnable = new Runnable() {
			public void run() {
				System.out.println("Weather bundle : the thread started.");
				
				while(!Activator.this.stopThreadWeather){ //run while the bundle are not stop
					
					try {
						Thread.sleep(10000); //sleep 10 sec
					} catch (InterruptedException e) {
						if(Activator.this.stopThreadWeather){ //"normal" interruption : the bundle need to stop
							break;
						}else{// "Abnormal" interruption
							System.out.println("Weather bundle : The thread refuse to sleep ! See the follow stack trace : ");
							e.printStackTrace();
							break;
						}
					}
					
					Activator.this.weather.computedWeather(Activator.this.location.getLatitude(), Activator.this.location.getLongitude());//modification of weather -> notify all listeners
					System.out.println("Weather bundle : new weather computed, all listeners are notify.");
				}
				System.out.println("Weather bundle : the thread stoped.");
			}
		};

		//launch the thread
		this.weatherThread = new Thread(weatherRunnable);
		this.weatherThread.start();
	}

	/**
	 * Stop properly the "weather" thread.
	 * */
	private void stopThread(){
		this.stopThreadWeather = true; //change the flag value
		this.weatherThread.interrupt(); // and interrupt the thread
		this.weatherThread = null;
	}
}
