package org.osgi.demo.mysimpleclient.activator;

import org.osgi.demo.mysimpleservice.IMySimpleService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

	@Override
	public void start(final BundleContext context) throws Exception {
		ServiceReference<?>[] references = context.getServiceReferences(IMySimpleService.class.getName(), "(name=*)");

		for (ServiceReference<?> reference : references) {
			((IMySimpleService) context.getService(reference)).sayHello();
		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
	}

}
