package com.beeblebroxlabs.sunrisealarm2.logic.util;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import com.beeblebroxlabs.sunrisealarm2.logic.AlarmBroadcastReceiver;
import com.beeblebroxlabs.sunrisealarm2.repository.local.Alarm;

/**
 *Sets AlarmManger with the alarm Time and a pending Intent
 */

public class AlarmRingUtil {
  private Context mContext;

  public AlarmRingUtil(Context mContext) {
    this.mContext = mContext;
  }

  public void setAlarmRingIntent(Alarm alarm){
    System.out.println("AlarmRingUtil:"+alarm.toString());
    AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
    Intent alarmIntent = new Intent(mContext,AlarmBroadcastReceiver.class);

    int requestCode = alarm.getId();
    Bundle args = new Bundle();
    args.putParcelable("ALARM",alarm);
    alarmIntent.putExtra("DATA",args);

    PendingIntent pendingIntent = PendingIntent
        .getBroadcast(mContext,requestCode,alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

    if(alarm.getRepeated()==0){
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,alarm.getRingTime(),pendingIntent);
      }else{
        alarmManager.set(AlarmManager.RTC_WAKEUP,alarm.getRingTime(),pendingIntent);
      }
    }else{
      alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,alarm.getRingTime(), AlarmManager.INTERVAL_DAY,pendingIntent);
    }
  }
}
