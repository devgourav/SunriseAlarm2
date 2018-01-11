package com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by devgr on 02-Dec-17.
 */

public class CurrentWeather {
  @SerializedName("weather")
  @Expose
  public List<Weather> weather = null;
  @SerializedName("main")
  @Expose
  public Main main;
  @SerializedName("sys")
  @Expose
  public Sys sys;
  @SerializedName("name")
  @Expose
  public String name;

  public List<Weather> getWeather() {
    return weather;
  }

  public Main getMain() {
    return main;
  }

  public Sys getSys() {
    return sys;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return weather.get(0).toString() + ":" + main.toString() + ":" + sys.toString() + ":" + name ;
  }
}
