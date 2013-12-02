package org.ups.location.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ups.location.ILocation;
import org.ups.location.ILocationListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class LocationImpl implements ILocation {


	private static final int WAITING_DELAY = 10000;
	private volatile boolean locating = true;

	private List<ILocationListener> listeners = new ArrayList<ILocationListener>();
	private float latitude;
	private float longitude;

	/**
	 * Creates the location service.
	 */
	public LocationImpl() {
		// Launches a process that generates the location each WAITING_DELAY
		// milliseconds.
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (locating) {
					// Updates the location.
					update();

					// Waits until it can update a new location.
					try {
						Thread.sleep(WAITING_DELAY);
					} catch (InterruptedException e) {
						locating = false;

						// Does not swallow interrupts.
						Thread.currentThread().interrupt();
					}
				}
			}

		}).start();
	}

	@Override
	public float getLatitude() {
		return latitude;
	}

	@Override
	public float getLongitude() {
		return longitude;
	}

	@Override
	public void addListener(ILocationListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(ILocationListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Terminates location process properly.
	 */
	public void terminate() {
		locating = false;
	}

	/**
	 * Updates the location by generating random place on Earth.
	 */
	private void update() {
		// Keeps the old location.
		float oldLatitude = latitude;
		float oldLongitude = longitude;

		//update the latitude&longitude
		getCoordByHttpReq();
	
		// Checks if a change has occurred.
		fireLocationChanged(oldLatitude, oldLongitude, latitude, longitude);
	}

	/**
	 * Update the latitude & longitude with a http request.
	 * */
	private void getCoordByHttpReq() {
		StringBuffer bufferResponse = new StringBuffer();
		try {
			//send the http request
			Integer responseCode = sendHttpRequest("http://freegeoip.net/xml/" ,bufferResponse);
			if(responseCode == 200){
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
				DocumentBuilder parser = factory.newDocumentBuilder();
				Document document =  parser.parse(new InputSource(new ByteArrayInputStream(bufferResponse.toString().getBytes("utf-8"))));
				Element elem = document.getDocumentElement();

				// 2 fields are used
				NodeList nodes = elem.getElementsByTagName("Latitude");
				latitude = new Float(nodes.item(0).getTextContent());
				nodes = elem.getElementsByTagName("Longitude");
				longitude = new Float(nodes.item(0).getTextContent());
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}  
	}


	/**
	 * Send a http GET request to an url and give the string content with a response code.
	 * @param url : a http request url
	 * @param bufferResponse : a {@link StringBuffer} contain the content http response
	 * @return an integer response code, if 200 all is ok. (see the http response code)
	 * **/
	private static Integer sendHttpRequest(String url, StringBuffer bufferResponse){
		Integer responseCode = -1;
		String inputLine;
		URL obj = null;
		try {
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
			while ((inputLine = in.readLine()) != null)
				bufferResponse.append(inputLine);

			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseCode;
	}

	/**
	 * Checks if the location has been changed and if so, fires it.
	 * 
	 * @param oldLatitude
	 *            the old latitude
	 * @param oldLongitude
	 *            the old longitude
	 * @param newLatitude
	 *            the new latitude
	 * @param newLongitude
	 *            the new longitude
	 */
	private void fireLocationChanged(float oldLatitude, float oldLongitude,
			float newLatitude, float newLongitude) {
		if (newLatitude != oldLatitude || newLongitude != oldLongitude) {
			for (ILocationListener listener : listeners) {
				listener.locationChanged(newLatitude, newLongitude);
			}
		}
	}


}
