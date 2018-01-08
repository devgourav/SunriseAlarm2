package com.beeblebroxlabs.sunrisealarm2.repository.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by devgr on 02-Dec-17.
 */


@Database(entities = {Alarm.class},version = 1,exportSchema = false)
public abstract class AlarmDatabase extends RoomDatabase {

  private static AlarmDatabase sInstance;

  public abstract AlarmDao alarmDao();

  public static synchronized AlarmDatabase getInstance(Context context) {
    if (sInstance == null) {
      sInstance = Room
          .databaseBuilder(context.getApplicationContext(), AlarmDatabase.class, "test_db")
          .build();
    }
    return sInstance;
  }

}
