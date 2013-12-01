package org.ups.weather.impl;

import java.util.ArrayList;
import java.util.List;

import org.ups.location.ILocation;
import org.ups.weather.IWeather;
import org.ups.weather.IWeatherListener;
import org.ups.weather.WeatherType;

public class RandomWeather implements IWeather, Runnable {

	private List<IWeatherListener> listeners;
	private WeatherType weather;
	private ILocation location;

	//for the "weather" thread management.
	private boolean stopThreadWeather;


	public RandomWeather() {
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
		this.weather=WeatherType.randomWeather();
		return weather;
	}

	public WeatherType getWeather(int nbHoursFromNow) {
		return WeatherType.randomWeather();
	}

	private void updateLocation(){
		fireWeatherChanged(this.weather);
	}

	private void fireWeatherChanged(WeatherType weather){
		for  (IWeatherListener listener: this.listeners){
			listener.weatherChanged(weather);
		}
	}

	public void computedWeather(float lan, float lon){
		this.weather = WeatherType.randomWeather();
		updateLocation();
	}
	
	public void setLocation(ILocation location){
		this.location = location;
	}


	//create a new runnable "weather"
	public void run() {
		System.out.println("Weather bundle : the thread started.");

		while(!this.stopThreadWeather){ //run while the bundle are not stop

			try {
				Thread.sleep(15000); //sleep 15 sec
			} catch (InterruptedException e) {
				if(this.stopThreadWeather){ //"normal" interruption : the bundle need to stop
					break;
				}else{// "Abnormal" interruption
					System.out.println("Weather bundle : The thread refuse to sleep ! See the follow stack trace : ");
					e.printStackTrace();
					break;
				}
			}

			this.computedWeather(this.location.getLatitude(), this.location.getLongitude());//modification of weather -> notify all listeners
			System.out.println("Weather bundle : new weather computed, all listeners are notify.");
		}
		System.out.println("Weather bundle : the thread stoped.");

	}



	/**
	 * Stop properly the "weather" thread.
	 * */
	public void stopThread(){
		this.stopThreadWeather = true; //change the flag value
	}


}
