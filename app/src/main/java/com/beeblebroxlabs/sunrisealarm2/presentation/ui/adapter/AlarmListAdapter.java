package com.beeblebroxlabs.sunrisealarm2.presentation.ui.adapter;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.beeblebroxlabs.sunrisealarm2.R;
import java.util.List;
import timber.log.Timber;

/**
 * Created by devgr on 15-Sep-17.
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHolder>{
  private List<String> values;
  private List<Boolean> isEnabled;

  public class ViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.alarmDetailsTextView)
    public TextView alarmDetailsTextView;

    @BindView(R.id.alarmSwitch)
    public Switch alarmSwitch;

    public View layout;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this,itemView);
    }
  }
  public void add(int position, String item) {
    values.add(position, item);
    notifyItemInserted(position);
  }

  public void remove(int position) {
    values.remove(position);
    notifyItemRemoved(position);
  }

  public AlarmListAdapter(List<String> values,List<Boolean> isEnabled) {
    this.values = values;
    this.isEnabled = isEnabled;
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
    holder.alarmDetailsTextView.setText(values.get(position));

    if(isEnabled.get(position)){
      holder.alarmSwitch.setChecked(TRUE);
    }else{
      holder.alarmSwitch.setChecked(FALSE);
    }

    holder.alarmSwitch.setOnCheckedChangeListener(
        (buttonView, isChecked) -> Timber.d("Alarm has been disabled"));
  }

  @Override
  public int getItemCount() {
    return values.size();
  }
}
