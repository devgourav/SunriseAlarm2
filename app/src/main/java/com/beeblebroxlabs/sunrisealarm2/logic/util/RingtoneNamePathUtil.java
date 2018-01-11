package com.beeblebroxlabs.sunrisealarm2.logic.util;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

/**
 * Created by devgr on 15-Sep-17.
 */

public class RingtoneNamePathUtil {

  private Context mContext;

  public RingtoneNamePathUtil(Context context){
    mContext = context;
  }


  public String getFileName(Uri uri) throws CursorIndexOutOfBoundsException {
    String fileName = null;
    if (uri.getScheme().equals("content")) {
      Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
      try{
        if (cursor != null && cursor.moveToFirst()) {
          fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }
      }finally {
        cursor.close();
      }
    }
    if (fileName == null) {
      fileName = uri.getPath();
      int cut = fileName.lastIndexOf('/');
      if (cut != -1) {
        fileName = fileName.substring(cut + 1);
      }
    }
    return fileName;
  }

  public String getRingtonePathFromContentUri(Uri contentUri) {
    String[] proj = {MediaStore.Audio.Media.DATA};
    Cursor ringtoneCursor = mContext.getContentResolver().query(contentUri,
        proj, null, null, null);
    ringtoneCursor.moveToFirst();

    String path = ringtoneCursor.getString(ringtoneCursor
        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

    ringtoneCursor.close();
    return path;
  }


//  public String getRingtonePathFromContentUri(Uri contentUri) {
//    Cursor cursor = null;
//    try {
//      String[] proj = { MediaStore.Images.Media.DATA };
//      cursor = mContext.getContentResolver().query(contentUri,  proj, null, null, null);
//      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//      cursor.moveToFirst();
//      return cursor.getString(column_index);
//    } finally {
//      if (cursor != null) {
//        cursor.close();
//      }
//    }
//  }

}
