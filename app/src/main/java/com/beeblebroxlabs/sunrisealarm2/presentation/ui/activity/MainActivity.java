package com.beeblebroxlabs.sunrisealarm2.presentation.ui.activity;

import static com.google.android.gms.location.LocationRequest.PRIORITY_LOW_POWER;
import static java.lang.Boolean.FALSE;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.beeblebroxlabs.sunrisealarm2.R;
import com.beeblebroxlabs.sunrisealarm2.presentation.WeatherDetailsDisplay;
import com.beeblebroxlabs.sunrisealarm2.presentation.ui.AlarmDetailsDisplay;
import com.beeblebroxlabs.sunrisealarm2.presentation.ui.adapter.AlarmListAdapter;
import com.beeblebroxlabs.sunrisealarm2.presentation.ui.fragment.DeleteAlarmDialogFragment;
import com.beeblebroxlabs.sunrisealarm2.presentation.ui.fragment.DeleteAlarmDialogFragment.DeleteDialogFragmentListener;
import com.beeblebroxlabs.sunrisealarm2.presentation.viewmodel.ShowAlarmViewModel;
import com.beeblebroxlabs.sunrisealarm2.presentation.viewmodel.ShowWeatherViewModel;
import com.beeblebroxlabs.sunrisealarm2.repository.local.Alarm;
import com.beeblebroxlabs.sunrisealarm2.repository.local.AlarmDatabase;
import com.beeblebroxlabs.sunrisealarm2.repository.remote.pojoModel.CurrentWeather;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import timber.log.BuildConfig;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class MainActivity extends AppCompatActivity implements DeleteDialogFragmentListener {

  private static final int LOCATION_NORMAL_INTERVAL = 6000 * 1000;
  private static final int LOCATION_FASTEST_INTERVAL = 1500 * 1000;
  private static final int REQUEST_FINE_LOCATION = 100;


  @BindView(R.id.weatherText) TextView WeatherText;
  @BindView(R.id.DateText) TextView dateText;
  @BindView(R.id.clockText) TextClock textClock;

  //Location related variables
  private FusedLocationProviderClient mFusedLocationProviderClient;
  private LocationRequest locationRequest;
  private LocationCallback mLocationCallback;

  List<String> alarmDetails;
  List<Boolean> alarmEnableDetails;
  List<Alarm> alarmList;

  @BindView(R.id.alarm_recycler_view)
  RecyclerView recyclerView;
  RecyclerView.Adapter mAdapter;
  RecyclerView.LayoutManager mLayoutManager;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (BuildConfig.DEBUG) {
      Timber.plant(new DebugTree());
    }
    ButterKnife.bind(this);

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(
        view -> startActivity(new Intent(MainActivity.this, SetAlarmActivity.class)));

    try {
      File httpCacheDir = new File(this.getCacheDir(), "http");
      long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
      HttpResponseCache.install(httpCacheDir, httpCacheSize);
    } catch (IOException e) {
      Timber.d(e);
    }

    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    mLocationCallback = new LocationCallback(){
      @Override
      public void onLocationResult(LocationResult locationResult) {
        if(locationResult.getLocations().size()>0) {
          fetchWeatherDetails(locationResult.getLocations().get(0));
        }else{
          Timber.d("Weather not fetched for location:");
        }
      }
    };

    Locale current = getResources().getConfiguration().locale;
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG,current);
    dateText.setText(dateFormat.format(Calendar.getInstance().getTime()));

    buildLocationRequest();
    startLocationService();
    checkTimeFormat();
    showAlarmList();
  }


/*Gets the weather Details from the remote API*/
  private void fetchWeatherDetails(Location location){
    ShowWeatherViewModel viewModel;
    viewModel = ViewModelProviders.of(this).get(ShowWeatherViewModel.class);
      viewModel.getWeather(location.getLatitude(),location.getLongitude()).observe(this,apiResponse -> {
        if (apiResponse.getError() != null) {
          Timber.d(apiResponse.getError());
        } else {
          CurrentWeather weather = apiResponse.getWeather();
          Timber.d(weather.toString());
          WeatherDetailsDisplay weatherDetails = new WeatherDetailsDisplay(weather,this);
          WeatherText.setText(weatherDetails.getFormattedWeather());

          SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
          Editor editor = pref.edit();
          if(pref.contains("sunriseTime")){
            editor.remove("sunriseTime");
          }
          editor.putLong("sunriseTime",weather.getSys().getSunrise());
          editor.commit();
        }
      });
  }


/*Builds a location request object*/
  private synchronized void buildLocationRequest() {
    this.locationRequest = LocationRequest.create()
        .setInterval(LOCATION_NORMAL_INTERVAL)
        .setFastestInterval(LOCATION_FASTEST_INTERVAL)
        .setPriority(PRIORITY_LOW_POWER);
  }


/*starts location service and if gets a location fetches weather Details*/
  private void startLocationService(){
    if ((VERSION.SDK_INT >= 23) && (
        ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)) {
      ActivityCompat.requestPermissions(this,
          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
          REQUEST_FINE_LOCATION);
    }else {
      mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
          location -> {
            if (location != null) {
              fetchWeatherDetails(location);
            } else {
              mFusedLocationProviderClient.requestLocationUpdates(locationRequest,mLocationCallback,null);
            }
          });
    }
  }

  public void showAlarmList(){
    ShowAlarmViewModel viewModel;
    alarmDetails = new ArrayList<>();
    alarmEnableDetails = new ArrayList<>();


    viewModel = ViewModelProviders.of(this).get(ShowAlarmViewModel.class);
    viewModel.getAlarms().observe(this,alarms -> {
      if(alarms.size()>0){
        Timber.d(alarms.size()+"alarms set currently");
        alarmList = alarms;
        for(Alarm alarm:alarmList){
          AlarmDetailsDisplay alarmDetailsDisplay =
              new AlarmDetailsDisplay(alarm.getRingTime(),alarm.getRepeated());
          alarmDetails.add(alarmDetailsDisplay.getAlarmDetailsText());
          alarmEnableDetails.add(alarm.getEnabled());
          recyclerView.setHasFixedSize(true);
          mLayoutManager = new LinearLayoutManager(getApplicationContext());
          recyclerView.setLayoutManager(mLayoutManager);
          mAdapter = new AlarmListAdapter(alarmDetails,alarmEnableDetails);
          recyclerView.setAdapter(mAdapter);
        }
      }else{
        Timber.d("No alarms set currently");
      }
    });

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
          @Override
          public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
              RecyclerView.ViewHolder
                  target) {
            return false;
          }
          @Override
          public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int pos = viewHolder.getAdapterPosition();
            DeleteAlarmDialogFragment deleteAlarmDialogFragment = new DeleteAlarmDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position",pos);
            deleteAlarmDialogFragment.setArguments(bundle);
            FragmentManager manager = getSupportFragmentManager();
            deleteAlarmDialogFragment.show(manager,"DeleteAlarm");

          }
        };
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
    itemTouchHelper.attachToRecyclerView(recyclerView);
  }

  @Override
  public void onClickDeleteDialogListener(int result,int pos) {
    switch (result){
      case (RESULT_OK):
        new DatabaseDelete(this,alarmList.get(pos)).execute();
        alarmDetails.clear();
        mAdapter.notifyItemRemoved(pos);
        mAdapter.notifyDataSetChanged();
        Timber.d("Alarm has been deleted");
        break;
      case (RESULT_CANCELED):
        mAdapter.notifyDataSetChanged();
        break;
    }
  }


  @Override
  protected void onStop() {
    super.onStop();
    HttpResponseCache cache = HttpResponseCache.getInstalled();
    if (cache != null) {
      cache.flush();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_settings_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  private void checkTimeFormat() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    Boolean is24HourTimeFormat = sharedPreferences.getBoolean("24hourClock",FALSE);
    if(is24HourTimeFormat){
      textClock.setFormat12Hour("HH:mm");
    }else{
      textClock.setFormat24Hour("hh:mm");
    }
  }

  private static class DatabaseDelete extends AsyncTask<Void,Void,Void> {
    Alarm alarm;
    Context mContext;
    AlarmDatabase alarmDatabase;


    public DatabaseDelete(Context mContext,Alarm alarm) {
      this.alarm = alarm;
      this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      alarmDatabase = AlarmDatabase.getInstance(mContext);
      alarmDatabase.alarmDao().deleteObject(alarm);
      return null;
    }
  }
}
