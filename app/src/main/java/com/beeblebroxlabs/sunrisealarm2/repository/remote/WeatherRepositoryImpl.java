package com.beeblebroxlabs.sunrisealarm2.repository.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import com.beeblebroxlabs.sunrisealarm2.SunriseApplication;
import com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel.CurrentWeather;
import java.io.IOException;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.internal.cache.CacheInterceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Created by devgr on 02-Dec-17.
 */

public class WeatherRepositoryImpl implements WeatherRepository{

  private static final String BASE_URL = "http://api.openweathermap.org";
  private static final int MAX_AGE = 1*60*60; //1 hour

  private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
    @Override public okhttp3.Response intercept(Chain chain) throws IOException {
      okhttp3.Response originalResponse = chain.proceed(chain.request());
      return originalResponse.newBuilder()
          .header("Cache-Control", "max-age="+MAX_AGE)
          .build();
    }
  };

  private WeatherService weatherService;
  private int cacheSize = 1 * 1024 * 1024; // 1 MB
  private Cache cache = new Cache(SunriseApplication.getAppContext().getCacheDir(), cacheSize);



  public WeatherRepositoryImpl() {
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY))
        .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
        .cache(cache)
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
        if(response.raw().cacheResponse()!=null){
          Timber.d("Response from cache...");
        }else if(response.raw().networkResponse()!=null){
          Timber.d("Response from server...");
        }
        liveData.setValue(new ApiResponse(response.body()));
      }

      @Override
      public void onFailure(Call<CurrentWeather> call, Throwable t) {
        liveData.setValue(new ApiResponse(t));
      }
    });
    return (LiveData<ApiResponse>) liveData;
  }
}
