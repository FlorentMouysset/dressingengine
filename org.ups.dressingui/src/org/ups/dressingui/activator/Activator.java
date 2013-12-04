package org.ups.dressingui.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ups.dressingengine.IDressingSuggestion;
import org.ups.dressingui.impl.MainWindow;
import org.ups.dressingui.impl.AdapterWindow;

public class Activator implements BundleActivator {

	private  MainWindow window;
	private AdapterWindow adapter;

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Initializing the dressing UI suggestion service...");
		
		// Gets weather service.
		ServiceReference<?> reference = context.getServiceReference(IDressingSuggestion.class.getName());

		//create the adapter window, update the suggestion string
		adapter = new  AdapterWindow((IDressingSuggestion) context.getService(reference));
	
		// Initializes the dressing suggestion service.
		window = new MainWindow(adapter);        

		Thread thread = new Thread(window);
		thread.start();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// Releases the service.
		System.out.println("Releasing the dressing UI suggestion service...");
	    adapter.removeListener();
		window = null;
		adapter= null;
	}

}
