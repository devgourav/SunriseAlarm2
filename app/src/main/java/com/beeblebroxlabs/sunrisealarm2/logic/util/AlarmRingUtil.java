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
 * Sets AlarmManger with the alarm Time and a pending Intent
 */

public class AlarmRingUtil {

  private Context mContext;
  Alarm alarm;
  Intent alarmIntent;
  AlarmManager alarmManager;
  PendingIntent pendingIntent;

  public AlarmRingUtil(Context mContext, Alarm alarm) {
    this.mContext = mContext;
    this.alarm = alarm;
  }

  public void setAlarmIntent() {
    System.out.println("AlarmRingUtil:" + alarm.toString());
    alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
    alarmIntent = new Intent(mContext, AlarmBroadcastReceiver.class);

    int requestCode = alarm.getId();
    Bundle args = new Bundle();
    args.putParcelable("ALARM", alarm);
    alarmIntent.putExtra("DATA", args);

    pendingIntent = PendingIntent
        .getBroadcast(mContext, requestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
  }

  public void setSingleAlarm() {
    setAlarmIntent();
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getRingTime(),
          pendingIntent);
    } else {
      alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getRingTime(), pendingIntent);
    }
  }

  public void setRepeatingAlarm(long interval) {
    setAlarmIntent();
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getRingTime(),interval,
            pendingIntent);
  }
}
