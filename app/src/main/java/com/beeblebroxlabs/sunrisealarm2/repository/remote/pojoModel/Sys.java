package com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by devgr on 02-Dec-17.
 */

public class Sys {
  @SerializedName("message")
  @Expose
  public double message;
  @SerializedName("country")
  @Expose
  public String country;
  @SerializedName("sunrise")
  @Expose
  public long sunrise;
  @SerializedName("sunset")
  @Expose
  public long sunset;

  public double getMessage() {
    return message;
  }

  public String getCountry() {
    return country;
  }

  public long getSunrise() {
    return sunrise;
  }

  public long getSunset() {
    return sunset;
  }

  @Override
  public String toString() {
    return country +" "+sunrise +" "+ sunset +" "+ message;
  }
}
