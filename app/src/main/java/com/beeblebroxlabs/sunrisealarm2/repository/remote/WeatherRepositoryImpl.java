package com.beeblebroxlabs.sunrisealarm2.repository.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import com.beeblebroxlabs.sunrisealarm2.SunriseApplication;
import com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel.CurrentWeather;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.http.BridgeInterceptor;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by devgr on 02-Dec-17.
 */

public class WeatherRepositoryImpl implements WeatherRepository{

  private static final String BASE_URL = "http://api.openweathermap.org";
  private static final int MAX_AGE = 60*60; //1 hour
  private WeatherService weatherService;
  private int cacheSize = 1024 * 1024;//1MB

  private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {
    okhttp3.Response originalResponse = chain.proceed(chain.request());
      return originalResponse.newBuilder()
          .header("Cache-Control", "public, max-age=" + MAX_AGE)
          .build();
  };


  public WeatherRepositoryImpl() {
    Cache cache = new Cache(SunriseApplication.getAppContext().getCacheDir(), cacheSize);
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .addInterceptor(new ConnectivityInterceptor(SunriseApplication.getAppContext()))
        .addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY))
        .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
        .cache(cache)
        .connectTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build();

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build();

    weatherService = retrofit.create(WeatherService.class);
  }


  @Override
  public LiveData<ApiResponse> getWeather(Double latitude, Double longitude) {
    final MutableLiveData<ApiResponse> liveData = new MutableLiveData<>();

    Call<CurrentWeather> currentWeatherCall = weatherService
        .getWeatherForLocation(latitude, longitude);

    currentWeatherCall.enqueue(new Callback<CurrentWeather>() {
      @Override
      public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
        liveData.setValue(new ApiResponse(response.body()));
      }

      @Override
      public void onFailure(Call<CurrentWeather> call, Throwable t) {
        liveData.setValue(new ApiResponse(t));
      }
    });
    return liveData;
  }
}
