package org.ups.consumer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ups.dressingengine.IDressingSuggestion;
import org.ups.dressingengine.IDressingSuggestionListener;
import org.ups.location.ILocation;
import org.ups.location.ILocationListener;
import org.ups.weather.IWeather;
import org.ups.weather.IWeatherListener;
import org.ups.weather.WeatherType;

public class Activator implements BundleActivator {

    private ILocation location;
    private ILocationListener locationListener;

    private IWeather weather;
    private IWeatherListener weatherListener;

    private IDressingSuggestion suggestion;
    private IDressingSuggestionListener suggestionListener;

    @Override
    public void start(BundleContext context) throws Exception {
        ServiceReference<?> reference = context
                .getServiceReference(ILocation.class.getName());
        location = (ILocation) context.getService(reference);
        location.addListener(locationListener = new ILocationListener() {

            @Override
            public void locationChanged(float lan, float lon) {
                System.out.println("New location: (" + lan + ";" + lon + ")");
            }

        });

        reference = context.getServiceReference(IWeather.class.getName());
        weather = (IWeather) context.getService(reference);
        weather.addListener(weatherListener = new IWeatherListener() {

            @Override
            public void weatherChanged(WeatherType newWeather) {
                System.out.println("New weather: (" + newWeather + ")");
            }

        });

        reference = context.getServiceReference(IDressingSuggestion.class
                .getName());
        suggestion = (IDressingSuggestion) context.getService(reference);
        suggestion
                .addListener(suggestionListener = new IDressingSuggestionListener() {

                    @Override
                    public void dressingSuggestionChanged(
                            IDressingSuggestion newSuggestion) {
                        System.out.println("New suggestion: ("
                                + newSuggestion.sunGlassesNeeded() + ";"
                                + newSuggestion.umbrellaNeeded() + ";"
                                + newSuggestion.coatNeeded() + ")");
                    }

                });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Releasing the consumer...");
        location.removeListener(locationListener);
        weather.removeListener(weatherListener);
        suggestion.removeListener(suggestionListener);
    }

}
