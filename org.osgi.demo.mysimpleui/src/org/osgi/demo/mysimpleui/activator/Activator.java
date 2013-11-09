package org.osgi.demo.mysimpleui.activator;

import org.osgi.demo.mysimpleui.swt.MainWindow;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(final BundleContext context) throws Exception {
		MainWindow window = new MainWindow();
		Thread thread = new Thread(window);
		thread.start();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
	}

}
