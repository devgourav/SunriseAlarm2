package com.beeblebroxlabs.sunrisealarm2.repository.local;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

/**
 * Created by devgr on 02-Dec-17.
 */

@Dao
public interface AlarmDao {

  @Query("select * from alarm")
  LiveData<List<Alarm>> loadAsync();

  @Query("select * from alarm")
  List<Alarm> loadSync();

  @Insert(onConflict = IGNORE)
  long insert(Alarm alarm);

  @Delete
  void deleteObject(Alarm alarm);

  @Query("delete from alarm where id = :id")
  void deleteId(int id);

  @Update
  void update(Alarm alarm);
}
