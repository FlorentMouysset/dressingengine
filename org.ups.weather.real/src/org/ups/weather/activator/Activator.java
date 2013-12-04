package org.ups.weather.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ups.location.ILocation;
import org.ups.weather.IWeather;
import org.ups.weather.impl.WeatherImpl;

public class Activator implements BundleActivator {
    
    private IWeather weather = null;
    
    @Override
    public void start(BundleContext context) throws Exception {
        // Gets location service.
        ServiceReference<?> reference = context.getServiceReference(ILocation.class.getName());
        
        // Initializes the weather service.
        System.out.println("Initializing the real weather service...");
        weather = new WeatherImpl((ILocation) context.getService(reference));
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("name", "org.ups.weather.random");
        context.registerService(IWeather.class.getName(), weather, properties);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // Releases the service.
        System.out.println("Releasing the real weather service...");
        ((WeatherImpl) weather).terminate();
        weather = null;
    }

}
