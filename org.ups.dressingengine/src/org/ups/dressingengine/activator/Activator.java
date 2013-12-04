package org.ups.dressingengine.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ups.dressingengine.IDressingSuggestion;
import org.ups.dressingengine.impl.DressingSuggestionImpl;
import org.ups.weather.IWeather;

public class Activator implements BundleActivator {

    private IDressingSuggestion suggestion = null;

    @Override
    public void start(BundleContext context) throws Exception {
        // Gets weather service.
        ServiceReference<?> reference = context.getServiceReference(IWeather.class.getName());
        
        // Initializes the dressing suggestion service.
        System.out.println("Initializing the dressing suggestion service...");
        suggestion = new DressingSuggestionImpl((IWeather) context.getService(reference));
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("name", "org.ups.dressingengine");
        context.registerService(IDressingSuggestion.class.getName(), suggestion, properties);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // Releases the service.
        System.out.println("Releasing the dressing suggestion service...");
        ((DressingSuggestionImpl) suggestion).terminate();
        suggestion = null;
    }

}
