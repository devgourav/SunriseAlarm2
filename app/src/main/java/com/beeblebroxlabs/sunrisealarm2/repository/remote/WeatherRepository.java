package com.beeblebroxlabs.sunrisealarm2.repository.remote;

import android.arch.lifecycle.LiveData;

/**
 * Created by devgr on 02-Dec-17.
 */

public interface WeatherRepository {
  LiveData<ApiResponse> getWeather(Double latitude,Double longitude);
}
