package com.beeblebroxlabs.sunrisealarm2.presentation.ui.adapter;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.beeblebroxlabs.sunrisealarm2.R;
import com.beeblebroxlabs.sunrisealarm2.presentation.ui.AlarmDetailsDisplay;
import com.beeblebroxlabs.sunrisealarm2.repository.local.Alarm;
import java.util.List;
import timber.log.Timber;

/**
 * Created by devgr on 15-Sep-17.
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHolder>{

  Context context;
  List<Alarm> alarmList;


  public class ViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.alarmDetailsTextView)
    public TextView alarmDetailsTextView;

    @BindView(R.id.alarmRepeatTextView)
    public TextView alarmRepeatTextView;

    @BindView(R.id.alarmSwitch)
    public Switch alarmSwitch;

    @BindView(R.id.view_background)
    public LinearLayout viewBackground;

    @BindView(R.id.view_foreground)
    public LinearLayout viewForeground;

    public View layout;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this,itemView);
    }
  }
  public void add(int position, Alarm alarm) {
    alarmList.add(position, alarm);
    notifyItemInserted(position);
  }

  public void remove(int position) {
    alarmList.remove(position);
    notifyItemRemoved(position);
  }

  public AlarmListAdapter(Context context,List<Alarm> alarmList) {
    this.context = context;
    this.alarmList = alarmList;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(
        parent.getContext());
    View v =
        inflater.inflate(R.layout.view_alarm_element, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Alarm alarm = alarmList.get(position);

    AlarmDetailsDisplay alarmDetailsDisplay = new AlarmDetailsDisplay(alarm.getRingTime(),alarm.getRepeated());

    holder.alarmDetailsTextView.setText(alarmDetailsDisplay.getAlarmDetailsText());
    holder.alarmRepeatTextView.setText(alarmDetailsDisplay.getAlarmRepeatText());

    if(alarm.getEnabled()){
      holder.alarmSwitch.setChecked(TRUE);
    }else{
      holder.alarmSwitch.setChecked(FALSE);
    }

    holder.alarmSwitch.setOnCheckedChangeListener(
        (buttonView, isChecked) -> Timber.d("Alarm has been disabled"));
  }

  @Override
  public int getItemCount() {
    return alarmList.size();
  }
}
