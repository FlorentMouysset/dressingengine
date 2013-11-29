package org.osgi.demo.mysimpleui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class MainWindow implements Runnable {

	private Shell shell;
	private Label label;
	private Display display;

	@Override
	public void run() {

		display = new Display();

		shell = new Shell(display);
		shell.setText("MainWindow");

		shell.setLayout(new FillLayout());

		label = new Label(shell, SWT.BORDER);
		label.setText("A simple label.");

		shell.setMinimumSize(600, 400);
		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
		shell = null;
		display = null;
	}
}
