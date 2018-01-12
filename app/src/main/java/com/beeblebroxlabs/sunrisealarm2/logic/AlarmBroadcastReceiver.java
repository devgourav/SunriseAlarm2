package com.beeblebroxlabs.sunrisealarm2.logic;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.beeblebroxlabs.sunrisealarm2.logic.util.AlarmRingUtil;
import com.beeblebroxlabs.sunrisealarm2.presentation.ui.activity.AlarmRingActivity;
import com.beeblebroxlabs.sunrisealarm2.repository.local.Alarm;
import java.util.Calendar;

/**
 *
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {

    System.out.println("AlarmBroadcastReceiver:onReceive");
    Bundle bundle = intent.getBundleExtra("DATA");
    Alarm alarm = bundle.getParcelable("ALARM");
    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_WEEK);

    AlarmRingUtil alarmRingUtil = new AlarmRingUtil(context,alarm);

    int repeat = alarm.getRepeated();

    if(repeat==1){//Repeat on Weekdays
      switch(day){
        case 6:  //Friday
          long interval = 3*24*60*60*1000; //skip saturday and sunday
          alarmRingUtil.setRepeatingAlarm(interval);

        case 2: //Monday
          alarmRingUtil.setRepeatingAlarm(AlarmManager.INTERVAL_DAY);  //Alarm will repeat everyday, until friday
          break;
      }
    }else if(repeat==2){//Repeat on Weekends
      switch(day){
        case 1:  //Sunday
          long interval = 5*24*60*60*1000; //skip Weedays
          alarmRingUtil.setRepeatingAlarm(interval);

        case 7: //Saturday
          alarmRingUtil.setRepeatingAlarm(AlarmManager.INTERVAL_DAY);  //Alarm will repeat on Sat,Sun
          break;
      }
    }

    System.out.println("AlarmBroadcastReceiver:"+alarm.toString());
    Boolean isAlarmEnabled = alarm.getEnabled();

    Intent alarmIntent = new Intent(context, AlarmRingActivity.class);
    Bundle args = new Bundle();
    args.putParcelable("ALARM",alarm);
    alarmIntent.putExtra("DATA",args);
    alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    if(isAlarmEnabled){
      context.startActivity(alarmIntent);
    }
  }
}
