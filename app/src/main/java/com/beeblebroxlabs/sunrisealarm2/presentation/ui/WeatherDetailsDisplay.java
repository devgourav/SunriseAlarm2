package com.beeblebroxlabs.sunrisealarm2.presentation.ui;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel.CurrentWeather;


/**
 * Created by devgr on 17-Sep-17.
 */

public class WeatherDetailsDisplay {
  public static final String DEGREE  = "\u00b0";
  CurrentWeather currentWeather;
  Context mContext;

  public WeatherDetailsDisplay(CurrentWeather currentWeather,Context mContext){
    this.currentWeather = currentWeather;
    this.mContext = mContext;
  }

  public String getFormattedWeather(){
    Boolean isFahrenheit;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    if(sharedPreferences.contains("temperatureUnit")){
      isFahrenheit = sharedPreferences.getBoolean("temperatureUnit",FALSE);
    }else{
      isFahrenheit=FALSE;
    }

    int temperature = (int)currentWeather.getMain().getTemp();
    String temperatureDetails;
    if(isFahrenheit==TRUE){
      temperature = (int)(temperature*1.8)+32;
      temperatureDetails = Integer.toString(temperature)+DEGREE+"F";

    }else{
      temperatureDetails = Integer.toString(temperature)+DEGREE+"C";
    }

    String city = currentWeather.getName();
    String weatherDetails = currentWeather.getWeather().get(0).getMain();

    return city + ", " + temperatureDetails + " " +weatherDetails;
  }

}
