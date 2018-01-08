package com.beeblebroxlabs.sunrisealarm2.repository.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel.CurrentWeather;
import com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel.Weather;

/**
 * Created by devgr on 02-Dec-17.
 */

public interface WeatherRepository {
  LiveData<ApiResponse> getWeather(Double latitude,Double longitude);
}
