package com.beeblebroxlabs.sunrisealarm2.repository.remote;

import com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel.CurrentWeather;

/**
 * Created by devgr on 02-Dec-17.
 */

public class ApiResponse {
  private CurrentWeather weather;
  private Throwable error;

  public ApiResponse(CurrentWeather weather) {
    this.weather = weather;
    this.error = null;
  }

  public ApiResponse(Throwable error) {
    this.error = error;
    this.weather = null;
  }

  public CurrentWeather getWeather() {
    return weather;
  }

  public Throwable getError() {
    return error;
  }
}
