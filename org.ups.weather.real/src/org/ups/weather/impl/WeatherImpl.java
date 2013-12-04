package org.ups.weather.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ups.location.ILocation;
import org.ups.location.ILocationListener;
import org.ups.weather.IWeather;
import org.ups.weather.IWeatherListener;
import org.ups.weather.WeatherType;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WeatherImpl implements IWeather {

    private static final String URL_1 = "http://api.openweathermap.org/data/2.5/weather?lat=";
    private static final String URL_2 = "&lon=";
    private static final String URL_3 = "&mode=xml";

    private static final int WAITING_DELAY = 20000;
    private volatile boolean running = true;

    private List<IWeatherListener> listeners = new ArrayList<IWeatherListener>();
    private NavigableMap<Date, WeatherType> weathers = new TreeMap<Date, WeatherType>();
    private ILocation location;
    private ILocationListener listener;

    private Thread currentThread = null;

    /**
     * Creates the weather service according to the location one.
     * 
     * @param location
     *            the location service
     */
    public WeatherImpl(ILocation location) {
        this.location = location;

        // Needs the location to work properly.
        if (this.location != null) {
            // Listens to the location service. For each change, a thread is
            // allocated until a new change occurs.
            this.location.addListener(listener = new ILocationListener() {

                @Override
                public void locationChanged(final float lan, final float lon) {
                    // Interrupts the previous thread if necessary.
                    if (currentThread != null) {
                        currentThread.interrupt();
                    }

                    // Stores the current weather.
                    update(lan, lon);

                    // Allocates a new thread dedicated to the current location.
                    currentThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            while (running) {
                                try {
                                    Thread.sleep(WAITING_DELAY);
                                    update(lan, lon);
                                } catch (InterruptedException e) {
                                    running = false;

                                    // Does not swallow interrupts.
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }

                    });

                    // Launches new process.
                    running = true;
                    currentThread.start();
                }

            });
        }
    }

    @Override
    public void addListener(IWeatherListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(IWeatherListener listener) {
        listeners.remove(listener);
    }

    @Override
    public WeatherType getCurrentWeather() {
        return weathers.isEmpty() ? WeatherType.UNKNOWN : weathers.lastEntry()
                .getValue();
    }

    @Override
    public WeatherType getWeather(int nbHoursFromNow) {
        // Compute the target value.
        long ref = new Date().getTime() - nbHoursFromNow * 60 * 60 * 1000;

        // Goes through the map looking for the closest entry.
        Iterator<Entry<Date, WeatherType>> it = weathers.entrySet().iterator();
        boolean found = false;
        long min = Long.MAX_VALUE;
        WeatherType weather = WeatherType.UNKNOWN;
        while (it.hasNext() && !found) {
            Entry<Date, WeatherType> entry = it.next();

            // Compute the difference between the two dates.
            final long diff = Math.abs(entry.getKey().getTime() - ref);

            // Detects if we have a closest value than the older one.
            if (diff < min) {
                min = diff;
                weather = entry.getValue();
            } else {
                // Stops the loop in order to optimize the algorithm.
                found = true;
            }
        }

        return weather;
    }

    /**
     * Terminates weather process properly.
     */
    public void terminate() {
        location.removeListener(listener);
        running = false;
    }

    /**
     * Updates the weather randomly.
     * 
     * @param latitude
     *            the latitude
     * @param longitude
     *            the longitude
     */
    private void update(float latitude, float longitude) {
        // Keeps the old weather.
        WeatherType oldWeather = weathers.isEmpty() ? WeatherType.UNKNOWN
                : weathers.lastEntry().getValue();

        // Sets the weather from coordinates.
        weathers.put(new Date(), getWeatherFromCoords(latitude, longitude));

        // Checks if a change has occurred.
        fireWeatherChanged(oldWeather, weathers.lastEntry().getValue());
    }

    private WeatherType getWeatherFromCoords(float latitude, float longitude) {
        try {
            // Uses Open Weather Map API with XML mode.
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            WeatherHandler handler = new WeatherHandler();
            saxParser.parse(new InputSource(new URL(URL_1 + latitude + URL_2
                    + longitude + URL_3).openStream()), handler);

            return handler.getWeather();
        } catch (Exception e) {
            return WeatherType.UNKNOWN;
        }
    }

    /**
     * Checks if the weather has been changed and if so, fires it.
     * 
     * @param oldWeather
     *            the old weather
     * @param newWeather
     *            the new weather
     */
    private void fireWeatherChanged(WeatherType oldWeather,
            WeatherType newWeather) {
        if (newWeather != oldWeather) {
            for (IWeatherListener listener : listeners) {
                listener.weatherChanged(newWeather);
            }
        }
    }

    private class WeatherHandler extends DefaultHandler {

        private WeatherType weather = WeatherType.UNKNOWN;

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            if (qName.equalsIgnoreCase("weather")) {
                // Gets the icon indicating the weather. See
                // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
                String icon = attributes.getValue("icon");

                if (icon.startsWith("01") || icon.startsWith("02")) {
                    weather = WeatherType.SHINY;
                } else if (icon.startsWith("03") || icon.startsWith("04")) {
                    weather = WeatherType.CLOUDY;
                } else if (icon.startsWith("09")) {
                    weather = WeatherType.SHOWERS;
                } else if (icon.startsWith("10")) {
                    weather = WeatherType.RAINY;
                } else if (icon.startsWith("11")) {
                    String id = attributes.getValue("number");

                    if (id.equals("200") || id.equals("201")
                            || id.equals("230") || id.equals("231")) {
                        weather = WeatherType.RAINY;
                    } else if (id.equals("202") || id.equals("232")) {
                        weather = WeatherType.SHOWERS;
                    } else {
                        weather = WeatherType.CLOUDY;
                    }
                } else if (icon.startsWith("13")) {
                    weather = WeatherType.SNOW;
                }
            }
        }

        public WeatherType getWeather() {
            return weather;
        }

    }

}
