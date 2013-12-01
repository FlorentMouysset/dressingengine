package org.ups.weather.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.ups.location.ILocation;
import org.ups.location.ILocationListener;
import org.ups.weather.IWeather;
import org.ups.weather.IWeatherListener;
import org.ups.weather.WeatherType;

public class WeatherImpl implements IWeather {

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
                public void locationChanged(float lan, float lon) {
                    // Interrupts the previous thread if necessary.
                    if (currentThread != null) {
                        currentThread.interrupt();
                    }

                    // Stores the current weather.
                    update();

                    // Allocates a new thread dedicated to the current location.
                    currentThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            while (running) {
                                try {
                                    Thread.sleep(WAITING_DELAY);
                                    update();
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
     */
    private void update() {
        // Keeps the old weather.
        WeatherType oldWeather = weathers.isEmpty() ? WeatherType.UNKNOWN
                : weathers.lastEntry().getValue();

        // Generates the new random weather.
        weathers.put(new Date(),
                WeatherType.values()[(int) (Math.random() * WeatherType
                        .values().length)]);

        // Checks if a change has occurred.
        fireWeatherChanged(oldWeather, weathers.lastEntry().getValue());
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

}
