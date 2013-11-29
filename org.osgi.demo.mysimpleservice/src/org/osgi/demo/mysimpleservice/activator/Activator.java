package org.osgi.demo.mysimpleservice.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.demo.mysimpleservice.IMySimpleService;
import org.osgi.demo.mysimpleservice.impl.MySimpleServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private IMySimpleService service = null;

	public Activator() {
		service = new MySimpleServiceImpl();
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		System.out.println("OSGi: org.osgi.demo.mysimpleservice.Activator.start()");

		Dictionary<String, String> properties = new Hashtable<String, String>();
		properties.put("name", "org.osgi.demo.mysimpleservice");
		context.registerService(IMySimpleService.class.getName(), service, properties);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		System.out.println("OSGi: org.osgi.demo.mysimpleservice.Activator.stop()");

		service = null;
	}
}
