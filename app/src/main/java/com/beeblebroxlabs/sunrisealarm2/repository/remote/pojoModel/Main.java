package com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by devgr on 02-Dec-17.
 */

public class Main {
  @SerializedName("temp")
  @Expose
  public double temp;

  public double getTemp() {
    return temp;
  }

  @Override
  public String toString() {
    return String.valueOf(temp);
  }
}
