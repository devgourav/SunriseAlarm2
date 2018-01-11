package com.beeblebroxlabs.sunrisealarm2.presentation.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import com.beeblebroxlabs.sunrisealarm2.repository.remote.ApiResponse;
import com.beeblebroxlabs.sunrisealarm2.repository.remote.WeatherRepository;
import com.beeblebroxlabs.sunrisealarm2.repository.remote.WeatherRepositoryImpl;

/**
 * Created by devgr on 02-Dec-17.
 */

public class ShowWeatherViewModel extends ViewModel {
  private MediatorLiveData<ApiResponse> mApiResponse;
  private WeatherRepository weatherRepository;

  public ShowWeatherViewModel() {
    mApiResponse = new MediatorLiveData<>();
    weatherRepository = new WeatherRepositoryImpl();
  }
  public LiveData<ApiResponse> getWeather(Double latitude,Double longitude) {
    mApiResponse.addSource(
        weatherRepository.getWeather(latitude,longitude),
        apiResponse -> mApiResponse.setValue(apiResponse)
    );
    return mApiResponse;
  }
}
