package com.beeblebroxlabs.sunrisealarm2.presentation.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;
import com.beeblebroxlabs.sunrisealarm2.R;


public class DeleteAlarmDialogFragment extends DialogFragment {

  DeleteDialogFragmentListener listener;
  int position;

  public DeleteAlarmDialogFragment() {
    // Required empty public constructor
  }


  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Bundle bundle = getArguments();
    position = bundle.getInt("position");

    listener = (DeleteDialogFragmentListener) getActivity();

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(getString(R.string.delete_alarm))
        .setPositiveButton(getString(R.string.delete_alarm_positive), (dialog, id) -> {
          listener.onClickDeleteDialogListener(Activity.RESULT_OK,position);
          Toast.makeText(getActivity().getApplicationContext(),
              getString(R.string.delete_alarm_toast),
              Toast.LENGTH_SHORT).show();
        })
        .setNegativeButton(getString(R.string.delete_alarm_negative),
            (dialog, id) -> listener.onClickDeleteDialogListener(Activity.RESULT_CANCELED,position));
    return builder.create();
  }

  public interface DeleteDialogFragmentListener {
    void onClickDeleteDialogListener(int result, int position);
  }
}

