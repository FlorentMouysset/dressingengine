package org.ups.dressingengine.impl;

import org.ups.dressingengine.IDressingSuggestion;
import org.ups.dressingengine.IDressingSuggestionListener;
import org.ups.weather.WeatherType;

public class DressingSuggestion implements IDressingSuggestion {

	
	private WeatherType weather;
	
	public void addListener(IDressingSuggestionListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeListener(IDressingSuggestionListener listener) {
		// TODO Auto-generated method stub

	}

	public boolean sunGlassesNeeded() {
		return this.weather == WeatherType.SHINY;
	}

	public boolean umbrellaNeeded() {
		return this.weather == WeatherType.SHOWERS || this.weather == WeatherType.RAINY;
	}

	public boolean coatNeeded() {
		return this.weather == WeatherType.SHOWERS || this.weather == WeatherType.RAINY || this.weather == WeatherType.SNOW;
	}

	public void setWeather(WeatherType weather) {
		this.weather = weather;
	}
	
	

}
