package com.beeblebroxlabs.sunrisealarm2.presentation.ui.activity;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.Manifest.permission;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;
import com.beeblebroxlabs.sunrisealarm2.R;
import com.beeblebroxlabs.sunrisealarm2.logic.util.AlarmRingUtil;
import com.beeblebroxlabs.sunrisealarm2.logic.util.RingtoneNamePathUtil;
import com.beeblebroxlabs.sunrisealarm2.repository.local.Alarm;
import com.beeblebroxlabs.sunrisealarm2.repository.local.AlarmDatabase;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import timber.log.BuildConfig;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class SetAlarmActivity extends AppCompatActivity{

  private static final int CONVERT_TO_13UNIX_TIME = 1000;
  private static final int REQUEST_READ_EXTERNAL_STORAGE = 100;

  @BindView(R.id.customTimeSwitch)
  Switch customTimeSwitch;

  @BindView(R.id.sunriseTimeSwitch)
  Switch sunriseTimeSwitch;

  @BindView(R.id.ringtoneButton)
  Button ringtoneButton;

  @BindView(R.id.ringtoneNameTextView)
  TextView ringtoneNameTextView;

  @BindView(R.id.alarmLabelEditText)
  EditText alarmLabelEditText;

  @BindView(R.id.timePicker)
  TimePicker timePicker;

  @BindView(R.id.sunriseTimeTextView)
  TextView sunriseTimeTextView;

  @BindView(R.id.repeatSpinner)
  Spinner spinner;

  Calendar sunriseTime,alarmTime,currentTime;
  Intent ringtoneIntent;
  Uri alarmUri;
  int repeated;
  RingtoneNamePathUtil ringtoneNamePathUtil;
  AlarmRingUtil alarmRingUtil;
  private Menu menu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_set_alarm);
    if (BuildConfig.DEBUG) {
      Timber.plant(new DebugTree());
    }
    ButterKnife.bind(this);
    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    sunriseTime = getSunriseTime();
    Locale current = getResources().getConfiguration().locale;

    alarmTime = Calendar.getInstance(TimeZone.getDefault());
    currentTime = Calendar.getInstance(TimeZone.getDefault());

    alarmUri = null;
    ringtoneNamePathUtil = new RingtoneNamePathUtil(getApplicationContext());

    if(sunriseTime!=null){
      DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT,current);
      sunriseTimeTextView
          .setText(getString(R.string.sunrise_time_text, dateFormat.format(sunriseTime.getTime())));
    }else{
      sunriseTimeTextView.setText(getString(R.string.sunrise_time_unavailable_text));
    }

    sunriseTimeSwitch.setText(getString(R.string.sunrise_switch_text));
    customTimeSwitch.setText(getString(R.string.custom_switch_text));


    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.repeat_array, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);


  }

  private Calendar getSunriseTime(){
    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
    Calendar sunriseTime = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());

    if(pref.contains("sunriseTime")){
      Long sunriseTimeInMillis = pref.getLong("sunriseTime", 0L)*CONVERT_TO_13UNIX_TIME;
      sunriseTime.setTimeInMillis(sunriseTimeInMillis);
      return  sunriseTime;
    }else{
      return null;
    }
  }



  public void setRingtoneIntentData(){
    if (ActivityCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
      Timber.d("Requesting permissions");
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
        requestPermissions(new String[]{permission.READ_EXTERNAL_STORAGE},
            REQUEST_READ_EXTERNAL_STORAGE);
      }
    }else{
      ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
      ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
      ringtoneIntent
          .putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_alarm_tone));
      ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,(Uri) null);
      startActivityForResult(ringtoneIntent,REQUEST_READ_EXTERNAL_STORAGE);
    }
  }

  /*--------------------------------------------------------Event listeners start------------------------------------------------------*/

  @OnCheckedChanged(R.id.sunriseTimeSwitch)
  public void sunriseTimeSwitchListener(boolean isEnabled){
    if(isEnabled){
      customTimeSwitch.setChecked(FALSE);
      timePicker.setEnabled(FALSE);
      showOptionMenu(R.id.okButton);
      showOptionMenu(R.id.cancelButton);

      if(sunriseTime==null){
        sunriseTimeSwitch.setEnabled(FALSE);
      }else{
        sunriseTimeSwitch.setEnabled(TRUE);
        alarmTime.setTimeInMillis(sunriseTime.getTimeInMillis());
      }

      if(alarmTime.compareTo(currentTime) <=0 ){
        alarmTime.add(Calendar.DATE,1);
      }
    }else{
      hideOptionMenu(R.id.okButton);
    }
  }

  @OnCheckedChanged(R.id.customTimeSwitch)
  public void customTimeSwitchListener(boolean isEnabled){
    if(isEnabled){
      sunriseTimeSwitch.setChecked(FALSE);
      timePicker.setEnabled(TRUE);
      showOptionMenu(R.id.okButton);
      showOptionMenu(R.id.cancelButton);

      timePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
        alarmTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
        alarmTime.set(Calendar.MINUTE,minute);
        alarmTime.set(Calendar.AM_PM,hourOfDay < 12 ? Calendar.AM : Calendar.PM);

        if(alarmTime.compareTo(currentTime) <=0 ){
          alarmTime.add(Calendar.DATE,1);
        }
      });
    }else{
      timePicker.setEnabled(FALSE);
      hideOptionMenu(R.id.okButton);
    }
  }

  @OnClick(R.id.ringtoneButton)
  public void onRingtoneButtonClickListener(View v){
    setRingtoneIntentData();
  }


  @OnItemSelected(R.id.repeatSpinner)
  public void repeatSpinnerItemSelectedListener(AdapterView<?> adapterView, View view, int i, long l){
    switch(i){
      case 0:
        repeated=0;
        break;
      case 1:
        repeated=1;
        break;
      case 2:
        repeated=2;
        break;
      case 3:
        repeated=3;
        break;
      default:
        repeated=0;
        break;
    }
  }

  @OnFocusChange(R.id.alarmLabelEditText)
  public void ringtoneFocusChangeListener(View view, boolean b){
    if (view.getId() == R.id.alarmLabelEditText && b) {
      alarmLabelEditText.setCursorVisible(TRUE);
    }else if(view.getId() == R.id.alarmLabelEditText && !b){
//        InputMethodManager inputMethodManager = (InputMethodManager) SetAlarmActivity.this
//            .getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
      alarmLabelEditText.setCursorVisible(FALSE);
    }
  }


  /*------------------------------------Event listeners end-------------------------------------------------------------------------*/


  /*------------------------------------Menu creation/modification code start-------------------------------------------------------*/

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.set_alarm_menu, menu);
    this.menu = menu;
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()){
      case (R.id.okButton):
        if(sunriseTimeSwitch.isEnabled() || customTimeSwitch.isEnabled()){
          Alarm alarm = new Alarm();

          alarm.setLabel(alarmLabelEditText.getText().toString());
          alarm.setSetTime(currentTime.getTimeInMillis());
          alarm.setRingTime(alarmTime.getTimeInMillis());
          alarm.setEnabled(TRUE);
          alarm.setRepeated(repeated);

          if (alarmUri.toString().contains("default") || alarmUri == null) {
            SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
            String sysDefaultAlarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                .toString();
            String alarmUriString = sharedPreferences
                .getString("default_alarmTone", sysDefaultAlarmTone);
            alarmUri = Uri.parse(alarmUriString);
          }
          alarm.setTunePath(alarmUri.toString());


          new DatabaseInsert(this,alarm).execute();

          alarmRingUtil = new AlarmRingUtil(getApplicationContext(),alarm);
          if(repeated==0){
            alarmRingUtil.setSingleAlarm();
          }else{
            alarmRingUtil.setRepeatingAlarm(AlarmManager.INTERVAL_DAY);
          }

        }else{
          Toast.makeText(this, getString(R.string.select_time_request), Toast.LENGTH_SHORT).show();
        }
        break;
      case (R.id.cancelButton):
        break;
    }
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    return super.onOptionsItemSelected(item);

  }


  public void hideOptionMenu(int id){
    MenuItem item = menu.findItem(id);
    item.setVisible(FALSE);
  }


  public void showOptionMenu(int id){
    MenuItem item = menu.findItem(id);
    item.setVisible(TRUE);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

   /*----------------------------------Menu creation/modification code end--------------------------------------------------------*/

  /*-----------------------------------Permissions logic start-----------------------------------*/

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch(requestCode){
      case REQUEST_READ_EXTERNAL_STORAGE:{
        if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
          setRingtoneIntentData();
        }else{
          Toast.makeText(this, getString(R.string.ringtone_error_message), Toast.LENGTH_SHORT)
              .show();
        }
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    String alarmToneName=" ";
    switch (requestCode) {
      case REQUEST_READ_EXTERNAL_STORAGE:
        if (resultCode == RESULT_OK) {
          alarmUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
          if(alarmUri!=null){
            alarmToneName = ringtoneNamePathUtil.getFileName(alarmUri);
          }
          ringtoneNameTextView.setText(alarmToneName);
        }
        break;
    }
  }



  private static class DatabaseInsert extends AsyncTask<Void,Void,Long>{
    Alarm alarm;
    Context mContext;
    AlarmDatabase alarmDatabase;


    public DatabaseInsert(Context mContext,Alarm alarm) {
      this.alarm = alarm;
      this.mContext = mContext.getApplicationContext();
    }

    @Override
    protected Long doInBackground(Void... voids) {
      alarmDatabase = AlarmDatabase.getInstance(mContext);
      return alarmDatabase.alarmDao().insert(alarm);
    }

    @Override
    protected void onPostExecute(Long id) {
      Timber.d("Alarm inserted into db:%s",id);
    }
  }

    /*-----------------------------------Permissions logic end-----------------------------------*/



}
