package com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by devgr on 02-Dec-17.
 */

public class Weather {
  @SerializedName("main")
  @Expose
  public String main;
  @SerializedName("description")
  @Expose
  public String description;

  public String getMain() {
    return main;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return main +" "+ description;
  }
}
