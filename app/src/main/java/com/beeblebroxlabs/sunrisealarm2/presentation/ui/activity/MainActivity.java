package com.beeblebroxlabs.sunrisealarm2.presentation.ui.activity;

import static com.google.android.gms.location.LocationRequest.PRIORITY_LOW_POWER;
import static java.lang.Boolean.FALSE;

import android.Manifest.permission;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.beeblebroxlabs.sunrisealarm2.R;
import com.beeblebroxlabs.sunrisealarm2.presentation.ui.RecyclerItemTouchHelper;
import com.beeblebroxlabs.sunrisealarm2.presentation.ui.WeatherDetailsDisplay;
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

public class MainActivity extends AppCompatActivity implements
    RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, DeleteDialogFragmentListener {

  private static final int LOCATION_NORMAL_INTERVAL = 6000 * 1000;
  private static final int LOCATION_FASTEST_INTERVAL = 1500 * 1000;
  private static final int REQUEST_FINE_LOCATION = 100;


  @BindView(R.id.weatherText)
  TextView WeatherText;
  @BindView(R.id.DateText)
  TextView dateText;
  @BindView(R.id.clockText)
  TextClock textClock;

  //Location related variables
  private FusedLocationProviderClient mFusedLocationProviderClient;
  private LocationRequest locationRequest;
  private LocationCallback mLocationCallback;


  List<Alarm> alarmList;

  @BindView(R.id.alarm_recycler_view)
  RecyclerView recyclerView;
  AlarmListAdapter mAdapter;
  RecyclerView.LayoutManager mLayoutManager;
  DividerItemDecoration dividerItemDecoration;

  Alarm deletedAlarm;
  int deletedIndex ;


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

    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    mLocationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        if (locationResult.getLocations().size() > 0) {
          fetchWeatherDetails(locationResult.getLocations().get(0));
        } else {
          Timber.d("Weather not fetched for location:");
        }
      }
    };

    buildLocationRequest();
    startLocationService();
    checkDateTimeFormat();

    checkNetworkConnection();
    setApplicationCache();

    alarmList = new ArrayList<>();
    prepareAlarmList();

    mLayoutManager = new LinearLayoutManager(MainActivity.this.getApplicationContext());
    mAdapter = new AlarmListAdapter(this, alarmList);
    dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
        DividerItemDecoration.VERTICAL);

    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.addItemDecoration(dividerItemDecoration, 0);
    recyclerView.setAdapter(mAdapter);

  }


  /*Gets the weather Details from the remote API*/
  private void fetchWeatherDetails(Location location) {
    ShowWeatherViewModel viewModel;
    viewModel = ViewModelProviders.of(this).get(ShowWeatherViewModel.class);
    viewModel.getWeather(location.getLatitude(), location.getLongitude())
        .observe(this, apiResponse -> {
          if (apiResponse.getError() != null) {
            Timber.i(apiResponse.getError());
//          WeatherText.setText(apiResponse.getError().toString());
          } else {
            CurrentWeather weather = apiResponse.getWeather();
            Timber.i(weather.toString());
            WeatherDetailsDisplay weatherDetails = new WeatherDetailsDisplay(weather, this);
            WeatherText.setText(weatherDetails.getFormattedWeather());

            SharedPreferences pref = getApplicationContext()
                .getSharedPreferences("MyPref", MODE_PRIVATE);
            Editor editor = pref.edit();
            if (pref.contains("sunriseTime")) {
              editor.remove("sunriseTime");
            }
            editor.putLong("sunriseTime", weather.getSys().getSunrise());
            editor.apply();
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
  private void startLocationService() {
    if ((VERSION.SDK_INT >= 23) && (
        ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)) {
      ActivityCompat.requestPermissions(this,
          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
          REQUEST_FINE_LOCATION);
    } else {
      mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
          location -> {
            if (location != null) {
              fetchWeatherDetails(location);
            } else {
              mFusedLocationProviderClient
                  .requestLocationUpdates(locationRequest, mLocationCallback, null);
            }
          });
    }
  }

  public void prepareAlarmList() {
    ShowAlarmViewModel viewModel;

    viewModel = ViewModelProviders.of(this).get(ShowAlarmViewModel.class);
    viewModel.getAlarms().observe(this, new Observer<List<Alarm>>() {
      @Override
      public void onChanged(@Nullable List<Alarm> alarms) {
        if (alarms.size() > 0) {
          Timber.i("alarms set currently:%s", alarms.size());
          alarmList.clear();
          alarmList.addAll(alarms);
          mAdapter.notifyDataSetChanged();
        } else {
          Timber.i("No alarms set currently");
        }
      }
    });

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
        new RecyclerItemTouchHelper(0,ItemTouchHelper.RIGHT, this);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
    itemTouchHelper.attachToRecyclerView(recyclerView);
  }


  @Override
  public void onClickDeleteDialogListener(int result, int pos) {
    switch (result) {
      case (RESULT_OK):
        new DatabaseDelete(this, alarmList.get(pos)).execute();
        mAdapter.remove(deletedIndex);
        mAdapter.notifyItemRemoved(deletedIndex);
        mAdapter.notifyDataSetChanged();
        break;
      case (RESULT_CANCELED):
        mAdapter.remove(deletedIndex);
        mAdapter.add(deletedIndex,deletedAlarm);
        mAdapter.notifyDataSetChanged();
        break;
    }
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
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
      Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  private void checkDateTimeFormat() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    Boolean is24HourTimeFormat = sharedPreferences.getBoolean("24hourClock", FALSE);
    if (is24HourTimeFormat) {
      textClock.setFormat12Hour("HH:mm");
    } else {
      textClock.setFormat24Hour("hh:mm");
    }

    Locale current = getResources().getConfiguration().locale;
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, current);
    dateText.setText(dateFormat.format(Calendar.getInstance().getTime()));
  }

  public boolean isOnline() {
    ConnectivityManager connectivityManager = (ConnectivityManager) this
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
    return (netInfo != null && netInfo.isConnected());
  }

  @Override
  public void onSwiped(ViewHolder viewHolder, int direction, int position) {
    if (viewHolder instanceof AlarmListAdapter.ViewHolder) {
      // remove the item from recycler view
      DeleteAlarmDialogFragment deleteAlarmDialogFragment = new DeleteAlarmDialogFragment();
      Bundle bundle = new Bundle();
      bundle.putInt("position", position);
      deleteAlarmDialogFragment.setArguments(bundle);
      FragmentManager manager = getSupportFragmentManager();
      deleteAlarmDialogFragment.show(manager, "DeleteAlarm");

      deletedAlarm = alarmList.get(viewHolder.getAdapterPosition());
      deletedIndex = viewHolder.getAdapterPosition();
    }
  }

  public void checkNetworkConnection() {
    if (!isOnline()) { //check if data is enabled or not
      new Builder(this).setTitle("Unable to connect")
          .setMessage("Enable data to fetch Weather Details")
          .setPositiveButton(android.R.string.yes,
              (dialog, i) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
          .setNegativeButton(android.R.string.no, (dialog, i) -> Toast
              .makeText(MainActivity.this, "Weather Details could not be fetched",
                  Toast.LENGTH_SHORT).show())
          .show();
    } else {
      Timber.i("DATA IS ON");
    }
  }

  public void setApplicationCache() {
    try {
      File httpCacheDir = new File(this.getCacheDir(), "http");
      long httpCacheSize = 1 * 1024 * 1024; // 1 MiB
      HttpResponseCache.install(httpCacheDir, httpCacheSize);
    } catch (IOException e) {
      Timber.i(e);
    }
  }

  private static class DatabaseDelete extends AsyncTask<Void, Void, Void> {

    Alarm alarm;
    Context mContext;
    AlarmDatabase alarmDatabase;


    public DatabaseDelete(Context mContext, Alarm alarm) {
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
