package org.ups.weather;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum WeatherType {
	SHINY, CLOUDY, RAINY, SHOWERS, SNOW, UNKNOWN;
	
	
	
	private static final List<WeatherType> VALUES =
    Collections.unmodifiableList(Arrays.asList(values()));
  private static final int SIZE = VALUES.size();
  private static final Random RANDOM = new Random();

  public static WeatherType randomWeather()  {
    return VALUES.get(RANDOM.nextInt(SIZE));
  }
}
