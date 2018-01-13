package com.beeblebroxlabs.sunrisealarm2.presentation.ui;

import com.beeblebroxlabs.sunrisealarm2.R;
import com.beeblebroxlabs.sunrisealarm2.SunriseApplication;
import java.util.Calendar;

/**
 * Created by devgr on 17-Sep-17.
 */

public class AlarmDetailsDisplay {

  private static final long MIN_IN_MILLIS = 60000;
  private static final long HOUR_IN_MILLIS = MIN_IN_MILLIS * 60;
  private static final long DAYS_IN_MILLIS = HOUR_IN_MILLIS * 24;

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

//    dayLeft = timeDifference / DAYS_IN_MILLIS;
    hourLeft = (timeDifference % DAYS_IN_MILLIS) / HOUR_IN_MILLIS;
    minuteLeft = (timeDifference % HOUR_IN_MILLIS) / MIN_IN_MILLIS;

//    String timeLeftText="";

//    if(dayLeft>0){
//      timeLeftText = "Alarm in "+dayLeft+" days "+hourLeft+" hours "+minuteLeft+" minutes ";
//    }else{
    String timeLeftText = SunriseApplication.getAppContext().getResources()
        .getString(R.string.timeLeftText, hourLeft, minuteLeft);
//    }
    return timeLeftText;

  }

  public String getAlarmRepeatText(){
    return SunriseApplication.getAppContext().getResources().getStringArray(R.array.repeat_array)[repeated];
  }

}
