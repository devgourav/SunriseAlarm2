package com.beeblebroxlabs.sunrisealarm2.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.beeblebroxlabs.sunrisealarm2.SunriseApplication;
import com.beeblebroxlabs.sunrisealarm2.logic.util.AlarmRingUtil;
import com.beeblebroxlabs.sunrisealarm2.repository.local.Alarm;
import com.beeblebroxlabs.sunrisealarm2.repository.local.AlarmDatabase;
import java.util.List;

/**
 * Created by devgr on 09-Jan-18.
 */

public class AlarmBootReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
      AlarmDatabase alarmDatabase = AlarmDatabase.getInstance(SunriseApplication.getAppContext());
      List<Alarm> alarms = alarmDatabase.alarmDao().loadSync();
      AlarmRingUtil alarmRingUtil = new AlarmRingUtil(SunriseApplication.getAppContext());

      for(Alarm alarm:alarms){
        if(alarm.getEnabled()){
          alarmRingUtil.setAlarmRingIntent(alarm);
        }
      }

    }
  }
}
