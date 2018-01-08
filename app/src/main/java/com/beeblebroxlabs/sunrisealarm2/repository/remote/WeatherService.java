package com.beeblebroxlabs.sunrisealarm2.repository.remote;

import com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel.CurrentWeather;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by devgr on 02-Dec-17.
 */

public interface WeatherService {

  @GET("/data/2.5/weather?&units=metric&appid=cf9e3211132508b56a16c068278590f0")
  Call<CurrentWeather> getWeatherForLocation(
      @Query("lat") Double latitude,
      @Query("lon") Double longitude);
}
