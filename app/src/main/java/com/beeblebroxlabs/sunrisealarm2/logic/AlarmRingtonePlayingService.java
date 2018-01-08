package com.beeblebroxlabs.sunrisealarm2.logic;

import static java.lang.Boolean.TRUE;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.beeblebroxlabs.sunrisealarm2.logic.util.RingtoneNamePathUtil;
import java.io.IOException;
import timber.log.Timber;

/**
 *
 */

public class AlarmRingtonePlayingService extends Service {
  private MediaPlayer mediaPlayer;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent,int flags, int startId) {

    RingtoneNamePathUtil pathUtil = new RingtoneNamePathUtil(getApplicationContext());
    mediaPlayer = new MediaPlayer();

    String alarmTunePath = intent.getExtras().getString("tunePath");

    Timber.d("alarmTunePath:"+alarmTunePath);

    if(alarmTunePath!=null){
      Uri uri = Uri.parse(alarmTunePath);
      alarmTunePath = pathUtil.getRingtonePathFromContentUri(uri);
    }else{
      Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
      alarmTunePath = uri.toString();
    }

    try {
      mediaPlayer.setDataSource(alarmTunePath);
      mediaPlayer.setLooping(TRUE);
      mediaPlayer.prepareAsync();
      mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mediaPlayer.stop();
    mediaPlayer.reset();
  }
}
