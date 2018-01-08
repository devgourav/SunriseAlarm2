package com.beeblebroxlabs.sunrisealarm2.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.beeblebroxlabs.sunrisealarm2.presentation.ui.activity.AlarmRingActivity;
import com.beeblebroxlabs.sunrisealarm2.repository.local.Alarm;

/**
 *
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {

    System.out.println("AlarmBroadcastReceiver:onReceive");
    Bundle bundle = intent.getBundleExtra("DATA");
    Alarm alarm = bundle.getParcelable("ALARM");

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
