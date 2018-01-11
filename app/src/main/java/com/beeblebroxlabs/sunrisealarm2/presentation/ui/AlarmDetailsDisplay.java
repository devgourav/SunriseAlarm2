package com.beeblebroxlabs.sunrisealarm2.presentation.ui;

import java.util.Calendar;

/**
 * Created by devgr on 17-Sep-17.
 */

public class AlarmDetailsDisplay {
  public static final long MIN_IN_MILLIS = 60000;
  public static final long HOUR_IN_MILLIS = MIN_IN_MILLIS * 60;
  public static final long DAYS_IN_MILLIS = HOUR_IN_MILLIS * 24;

  private Long ringTime;
  private int repeated;

  public AlarmDetailsDisplay(Long ringTime,int repeated){
    this.ringTime = ringTime;
    this.repeated = repeated;
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

    String repeatText="";

    switch (repeated){
      case 0:
        repeatText="Once";
        break;
      case 1:
        repeatText="MON|TUE|WED|THUR|FRI";
        break;
      case 2:
        repeatText="SAT|SUN";
        break;
    }

    if(dayLeft>0){
      return "Alarm in "+dayLeft+" days "+hourLeft+" hours "+minuteLeft+" minutes " + repeatText;
    }else{
      return "Alarm in "+hourLeft+" hours "+minuteLeft+" minutes " +repeatText;
    }
  }

}
