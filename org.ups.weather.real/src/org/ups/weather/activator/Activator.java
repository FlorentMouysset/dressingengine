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
	
	
	public Activator() {
		this.weather = new WeatherImpl();
		
	}

	public void start(BundleContext context) throws Exception {
		
		System.out.println("start weather ... ");
		
		//register weather
		Dictionary<String, String> properties = new Hashtable<String, String>();
		properties.put("name", "org.ups.weather");
		context.registerService(IWeather.class.getName(), this.weather, properties);

		

		
		//get localisation service
		ServiceReference<?>[] references = context.getServiceReferences(ILocation.class.getName(), "(name=*)");

		for (ServiceReference<?> reference : references) {
			((ILocation) context.getService(reference)).addListener(new ILocationListener() {
				
				public void locationChanged(float lan, float lon) {
					
					System.out.println("location change...");
					weather.computedWeather(lan, lon);
					
				}
			});
		}
		
		
		
		
		//thread
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("stop weather");
		
		this.weather = null;
	}

}
