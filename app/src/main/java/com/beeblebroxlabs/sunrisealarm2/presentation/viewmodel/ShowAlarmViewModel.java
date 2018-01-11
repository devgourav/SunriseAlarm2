package com.beeblebroxlabs.sunrisealarm2.presentation.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import com.beeblebroxlabs.sunrisealarm2.SunriseApplication;
import com.beeblebroxlabs.sunrisealarm2.repository.local.Alarm;
import com.beeblebroxlabs.sunrisealarm2.repository.local.AlarmDatabase;
import java.util.List;

/**
 * Created by devgr on 19-Dec-17.
 */

public class ShowAlarmViewModel extends ViewModel {


  private LiveData<List<Alarm>> alarms;
  private AlarmDatabase alarmDatabase;

  public ShowAlarmViewModel() {
    alarmDatabase = AlarmDatabase.getInstance(SunriseApplication.getAppContext());
  }

  public LiveData<List<Alarm>> getAlarms(){
    alarms = alarmDatabase.alarmDao().loadAsync();
    return alarms;
  }

}
