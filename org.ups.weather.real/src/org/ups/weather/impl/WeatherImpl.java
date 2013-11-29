package org.ups.weather.impl;

import java.util.ArrayList;
import java.util.List;

import org.ups.weather.IWeather;
import org.ups.weather.IWeatherCompute;
import org.ups.weather.IWeatherListener;
import org.ups.weather.WeatherType;

public class WeatherImpl implements IWeather, IWeatherCompute {

	private List<IWeatherListener> listeners;
	
	public WeatherImpl() {
		super();
		this.listeners = new ArrayList<IWeatherListener>();
	}
	
	public void addListener(IWeatherListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(IWeatherListener listener) {
		this.listeners.remove(listener);
	}

	public WeatherType getCurrentWeather() {
		return WeatherType.randomWeather();
	}

	public WeatherType getWeather(int nbHoursFromNow) {
		return WeatherType.randomWeather();
	}

	public void computedWeather(float latitude, float longitude) {
		
		//faire appel aux WS meteo
	}

	
	
}
