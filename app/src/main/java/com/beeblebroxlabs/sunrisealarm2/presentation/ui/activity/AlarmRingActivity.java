package com.beeblebroxlabs.sunrisealarm2.presentation.ui.activity;

import static java.lang.Boolean.FALSE;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.beeblebroxlabs.sunrisealarm2.R;
import com.beeblebroxlabs.sunrisealarm2.logic.AlarmBroadcastReceiver;
import com.beeblebroxlabs.sunrisealarm2.logic.AlarmRingtonePlayingService;
import com.beeblebroxlabs.sunrisealarm2.logic.util.AlarmRingUtil;
import com.beeblebroxlabs.sunrisealarm2.repository.local.Alarm;
import com.beeblebroxlabs.sunrisealarm2.repository.local.AlarmDatabase;
import timber.log.BuildConfig;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 *
 */
public class AlarmRingActivity extends AppCompatActivity {


  private static final int SNOOZE_TIME = 2 * 60 * 1000;
  private static final int SNOOZE_TIME_MIN = SNOOZE_TIME / 60000;//2 Minutes
  protected static WakeLock wakeLock = null;
  @BindView(R.id.ringLabelText)
  TextView ringLabelText;
  @BindView(R.id.ringTextClock)
  TextClock ringTextClock;
  @BindView(R.id.silentButton)
  Button silentButton;
  @BindView(R.id.snoozeButton)
  Button snoozeButton;
  Intent alarmRingtoneService;
  Alarm alarm;
  AlarmRingUtil alarmRingUtil;
  Context context;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //For full screen
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
    getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
    getWindow().addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);

    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");
    wakeLock.acquire();

    context = getApplicationContext();



    setContentView(R.layout.activity_alarm_ring);
    ButterKnife.bind(this);
    if (BuildConfig.DEBUG) {
      Timber.plant(new DebugTree());
    }

    Bundle bundle = getIntent().getBundleExtra("DATA");
    alarm = bundle.getParcelable("ALARM");

    Timber.d(alarm.toString());
    alarmRingtoneService = new Intent(getApplicationContext(), AlarmRingtonePlayingService.class);
    alarmRingtoneService.putExtra("tunePath",alarm.getTunePath());
    startService(alarmRingtoneService);

    if(alarm.getLabel() != null){
      ringLabelText.setText(alarm.getLabel());
    }else{
      ringLabelText.setText(" ");
    }

    snoozeButton.setText(getString(R.string.snooze_text, SNOOZE_TIME_MIN));

  }

  @OnClick(R.id.snoozeButton)
  public void snoozeButtonListener(){
    stopService(alarmRingtoneService);
    Alarm newAlarm = alarm;
    newAlarm.setRingTime(alarm.getRingTime()+SNOOZE_TIME);

    Intent newAlarmIntent = new Intent(AlarmRingActivity.this,AlarmBroadcastReceiver.class);

    Bundle args = new Bundle();
    args.putParcelable("ALARM",newAlarm);
    newAlarmIntent.putExtra("DATA",args);


    new DatabaseUpdate(this,alarm).execute();

    alarmRingUtil = new AlarmRingUtil(getApplicationContext(),newAlarm);
    alarmRingUtil.setSingleAlarm();

   startMainActivity();
  }

  @OnClick(R.id.silentButton)
  public void silentButtonListener(){
    stopService(alarmRingtoneService);

    if(alarm.getRepeated()==0){
      alarm.setEnabled(FALSE);
      new DatabaseDelete(this,alarm).execute();
    }

    startMainActivity();

  }

  public void startMainActivity(){
    Intent intent = new Intent(AlarmRingActivity.this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    wakeLock.release();
  }

  private static class DatabaseDelete extends AsyncTask<Void,Void,Void> {

    Alarm alarm;
    AlarmDatabase alarmDatabase;
    Context context;


    public DatabaseDelete(Context context, Alarm alarm) {
      this.alarm = alarm;
      this.context = context.getApplicationContext();
    }

    @Override
    protected Void doInBackground(Void... voids) {
      alarmDatabase = AlarmDatabase.getInstance(context);
      alarmDatabase.alarmDao().deleteObject(alarm);
      return null;
    }
  }

  private static class DatabaseUpdate extends AsyncTask<Void,Void,Void> {
      Alarm alarm;
      Context mContext;
      AlarmDatabase alarmDatabase;


      public DatabaseUpdate(Context context,Alarm alarm) {
        this.alarm = alarm;
        this.mContext = context.getApplicationContext();
      }

      @Override
      protected Void doInBackground(Void... voids) {
        alarmDatabase = AlarmDatabase.getInstance(mContext);
        alarmDatabase.alarmDao().update(alarm);
        return null;
      }
  }


}
