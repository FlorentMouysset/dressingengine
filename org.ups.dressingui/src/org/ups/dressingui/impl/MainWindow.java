package org.ups.dressingui.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class MainWindow implements Runnable {


	private Shell shell;
	private Label label;
	private Display display;
	private AdapterWindow adapter;


	public MainWindow(AdapterWindow adapter) {
		this.adapter = adapter;
	}




	@Override
	public void run() {

		display = new Display();

		shell = new Shell(display);
		shell.setText("MainWindow");

		shell.setLayout(new FillLayout());

		label = new Label(shell, SWT.BORDER);
		label.setText("initialisation... patientez svp...");

		shell.setMinimumSize(600, 400);
		shell.pack();
		shell.open();

		//make the 	pollster
		new Thread(new Runnable() {
			public void run() {
				while (! MainWindow.this.adapter.isTerminated() ) { // while the IDressingSuggestion are still listen
					try { Thread.sleep(1000); } catch (Exception e) { } // sleep some ms
					Display.getDefault().asyncExec(new Runnable() { // update the label with a  "SWT safe thread"
						public void run() {
							String currentText = MainWindow.this.adapter.getSuggestionString();
							if(! MainWindow.this.label.getText().equals(currentText) && currentText != null){
								MainWindow.this.label.setText(currentText);
							}
						}
					});
				}
			}
		}).start();


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
