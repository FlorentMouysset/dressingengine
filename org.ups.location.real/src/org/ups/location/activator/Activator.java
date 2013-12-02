package org.ups.location.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ups.location.ILocation;
import org.ups.location.impl.LocationImpl;

public class Activator implements BundleActivator {

    private ILocation location = null;

    @Override
    public void start(BundleContext context) throws Exception {
        // Initializes the location service.
        System.out.println("Initializing the real location service...");
        location = new LocationImpl();
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("name", "org.ups.location.real");
        context.registerService(ILocation.class.getName(), location, properties);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // Releases the service.
        System.out.println("Releasing the real location service...");
        ((LocationImpl) location).terminate();
        location = null;
    }

}
