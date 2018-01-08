package com.beeblebroxlabs.sunrisealarm2;

import android.app.Application;
import android.content.Context;

/**
 * Created by devgr on 04-Dec-17.
 */

public class SunriseApplication extends Application {

  private static Context context;

  public void onCreate() {
    super.onCreate();
    SunriseApplication.context = getApplicationContext();
  }

  public static Context getAppContext() {
    return SunriseApplication.context;
  }

}
