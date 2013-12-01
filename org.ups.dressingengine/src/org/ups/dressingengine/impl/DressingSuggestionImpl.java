package org.ups.dressingengine.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ups.dressingengine.IDressingSuggestion;
import org.ups.dressingengine.IDressingSuggestionListener;
import org.ups.weather.IWeather;
import org.ups.weather.IWeatherListener;
import org.ups.weather.WeatherType;

public class DressingSuggestionImpl implements IDressingSuggestion {

    private List<IDressingSuggestionListener> listeners = new ArrayList<IDressingSuggestionListener>();
    private IWeather weather;
    
    /**
     * suggestion[0] = sun glasses needed
     * suggestion[1] = umbrella needed
     * suggestion[2] = coat needed
     */
    private boolean[] suggestion = new boolean[3];

    /**
     * Creates the dressing suggestion service with the weather one.
     * 
     * @param weather
     *            the weather service
     */
    public DressingSuggestionImpl(IWeather weather) {
        this.weather = weather;

        if (this.weather != null) {
            // Listens to weather changes.
            this.weather.addListener(new IWeatherListener() {

                @Override
                public void weatherChanged(WeatherType newWeather) {
                    update(newWeather);
                }

            });
        }
    }

    @Override
    public void addListener(IDressingSuggestionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(IDressingSuggestionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean sunGlassesNeeded() {
        return suggestion[0];
    }

    @Override
    public boolean umbrellaNeeded() {
        return suggestion[1];
    }

    @Override
    public boolean coatNeeded() {
        return suggestion[2];
    }

    /**
     * Updates the suggestion according the current weather type.
     * 
     * @param weatherType
     *            the weather type
     */
    private void update(WeatherType weatherType) {
        // Keeps the old suggestion.
        boolean oldSuggestion[] = new boolean[3];
        System.arraycopy(suggestion, 0, oldSuggestion, 0, 3);

        // Updates the new suggestion.
        suggestion[0] = weatherType == WeatherType.SHINY;
        suggestion[1] = weatherType == WeatherType.RAINY
                || weatherType == WeatherType.SHOWERS
                || weatherType == WeatherType.SNOW;
        suggestion[2] = weatherType == WeatherType.SHOWERS
                || weatherType == WeatherType.SNOW;

        // Checks if a change has occurred.
        fireSuggestionChanged(oldSuggestion, suggestion);
    }

    /**
     * Checks if the suggestion has been changed and if so, fires it.
     * 
     * @param oldSuggestion
     *            the old suggestion
     * @param newSuggestion
     *            the new suggestion
     */
    private void fireSuggestionChanged(boolean oldSuggestion[],
            boolean[] newSuggestion) {
        if (!Arrays.equals(newSuggestion, oldSuggestion)) {
            for (IDressingSuggestionListener listener : listeners) {
                listener.dressingSuggestionChanged(this);
            }
        }
    }

}
