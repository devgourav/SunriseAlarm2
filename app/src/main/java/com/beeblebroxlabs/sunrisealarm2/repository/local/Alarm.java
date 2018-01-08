package com.beeblebroxlabs.sunrisealarm2.repository.local;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by devgr on 02-Dec-17.
 */


@Entity(tableName = "alarm")
public class Alarm implements Parcelable {

  @PrimaryKey(autoGenerate = true)
  private int id;
  private String label;
  private long setTime;
  private long ringTime;
  private String tunePath;
  private Boolean isRepeated;
  private Boolean isEnabled;

  public Alarm() {}

  protected Alarm(Parcel in) {
    id = in.readInt();
    label = in.readString();
    setTime = in.readLong();
    ringTime = in.readLong();
    tunePath = in.readString();
    byte tmpIsRepeated = in.readByte();
    isRepeated = tmpIsRepeated == 0 ? null : tmpIsRepeated == 1;
    byte tmpIsEnabled = in.readByte();
    isEnabled = tmpIsEnabled == 0 ? null : tmpIsEnabled == 1;
  }

  public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
    @Override
    public Alarm createFromParcel(Parcel in) {
      return new Alarm(in);
    }

    @Override
    public Alarm[] newArray(int size) {
      return new Alarm[size];
    }
  };

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public long getSetTime() {
    return setTime;
  }

  public void setSetTime(long setTime) {
    this.setTime = setTime;
  }

  public long getRingTime() {
    return ringTime;
  }

  public void setRingTime(long ringTime) {
    this.ringTime = ringTime;
  }

  public String getTunePath() {
    return tunePath;
  }

  public void setTunePath(String tunePath) {
    this.tunePath = tunePath;
  }

  public Boolean getRepeated() {
    return isRepeated;
  }

  public void setRepeated(Boolean repeated) {
    isRepeated = repeated;
  }

  public Boolean getEnabled() {
    return isEnabled;
  }

  public void setEnabled(Boolean enabled) {
    isEnabled = enabled;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeInt(id);
    parcel.writeString(label);
    parcel.writeLong(setTime);
    parcel.writeLong(ringTime);
    parcel.writeString(tunePath);
    parcel.writeByte((byte) (isRepeated == null ? 0 : isRepeated ? 1 : 2));
    parcel.writeByte((byte) (isEnabled == null ? 0 : isEnabled ? 1 : 2));
  }

  public String toString(){
    return "Id"+getId()+"Label"+getLabel()+"RingTime"+getRingTime()+"SetTime"+getSetTime()+"Emabled"+getEnabled()+"Repeated"+getRepeated()+"tunePath"+getTunePath();
  }
}
