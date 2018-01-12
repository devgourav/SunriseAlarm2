package com.beeblebroxlabs.sunrisealarm2.repository.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by devgr on 12-Jan-18.
 */

public class ConnectivityInterceptor implements Interceptor {

  private Context mContext;

  public ConnectivityInterceptor(Context context) {
    mContext = context;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    if (!isOnline(mContext)) {
      throw new NoConnectivityException();
    }

    Request.Builder builder = chain.request().newBuilder();
    return chain.proceed(builder.build());
  }

  public static boolean isOnline(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
    return (netInfo != null && netInfo.isConnected());
  }

  public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
      return "No connectivity exception";
    }

  }
}
