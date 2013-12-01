package org.ups.dressingengine.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ups.dressingengine.IDressingSuggestion;
import org.ups.dressingengine.impl.DressingSuggestion;
import org.ups.weather.IWeather;
import org.ups.weather.IWeatherListener;
import org.ups.weather.WeatherType;


public class Activator implements BundleActivator {

	private IDressingSuggestion dressingSuggestion = null;
	private IWeather weather = null;
	private IWeatherListener weatherListener = null;
	
	
	public Activator(){
		this.dressingSuggestion = new DressingSuggestion();
	}


	/**
	 * Start the "weather" bundle and launch the "weather thread".
	 * */
	public void start(BundleContext context) throws Exception {

		//get weather service (random version)
		//TODO filtrage !
		ServiceReference<?>[] references = context.getServiceReferences(IWeather.class.getName(), "(name=org.ups.weatherrandom)");

		ServiceReference<?> reference = references[0];
		//register by location services
		this.weather = ((IWeather) context.getService(reference));
		//System.out.println("check2 = " + location);
		this.weatherListener = new IWeatherListener() {
			public void weatherChanged(WeatherType newWeather) {
				System.out.println("Dressing is notify");
				((DressingSuggestion) Activator.this.dressingSuggestion).setWeather(newWeather);
			}
		};
		this.weather.addListener(this.weatherListener);
		System.out.println("ok start dressing");
	}

	/**
	 * Stop the "weather" bundle -> stop the "weather" thread.
	 * */
	public void stop(BundleContext context) throws Exception {
		this.weather.removeListener(this.weatherListener);
		this.weather = null;
		this.weatherListener = null;
		this.dressingSuggestion = null;
		System.out.println("Weather bundle : stop, goodbye !");
	}

}
