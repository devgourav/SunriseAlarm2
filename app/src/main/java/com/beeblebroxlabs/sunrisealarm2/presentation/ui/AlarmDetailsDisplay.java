package com.beeblebroxlabs.sunrisealarm2.presentation.ui;

import android.util.Log;
import java.util.Calendar;

/**
 * Created by devgr on 17-Sep-17.
 */

public class AlarmDetailsDisplay {
  public static final long MIN_IN_MILLIS = 60000;
  public static final long HOUR_IN_MILLIS = MIN_IN_MILLIS * 60;
  public static final long DAYS_IN_MILLIS = HOUR_IN_MILLIS * 24;

  private Long ringTime;
  private Boolean isRepeating;

  public AlarmDetailsDisplay(Long ringTime,Boolean isRepeating){
    this.ringTime = ringTime;
    this.isRepeating = isRepeating;
  }

  public String getAlarmDetailsText(){
    Calendar currentTime = Calendar.getInstance();
    Calendar alarmTime = Calendar.getInstance();
    long timeDifference, hourLeft, minuteLeft,dayLeft,currTimeInMillis,alarmTimeInMillis;


    currTimeInMillis = currentTime.getTimeInMillis();
    alarmTime.setTimeInMillis(ringTime);
    alarmTimeInMillis = alarmTime.getTimeInMillis();


    if(alarmTimeInMillis>=currTimeInMillis){
      timeDifference = alarmTimeInMillis-currTimeInMillis;
    }
    else{
      alarmTime.set(Calendar.DATE,currentTime.get(Calendar.DATE)+1);
      timeDifference = alarmTime.getTimeInMillis()-currTimeInMillis;
    }

    dayLeft = timeDifference / DAYS_IN_MILLIS;
    hourLeft = (timeDifference % DAYS_IN_MILLIS) / HOUR_IN_MILLIS;
    minuteLeft = (timeDifference % HOUR_IN_MILLIS) / MIN_IN_MILLIS;

    String isRepeat = isRepeating?"Repeating":"Once";

    if(dayLeft>0){
      return "Alarm in "+dayLeft+" days "+hourLeft+" hours "+minuteLeft+" minutes " +isRepeat;
    }else{
      return "Alarm in "+hourLeft+" hours "+minuteLeft+" minutes " +isRepeat;
    }
  }

}
