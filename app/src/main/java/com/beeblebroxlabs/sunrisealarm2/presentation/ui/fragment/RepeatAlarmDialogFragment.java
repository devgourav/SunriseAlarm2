package com.beeblebroxlabs.sunrisealarm2.presentation.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class RepeatAlarmDialogFragment extends DialogFragment {

  RepeatDialogFragmentListener dialogFragmentListener;
  public RepeatAlarmDialogFragment() {}

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    dialogFragmentListener = (RepeatDialogFragmentListener) getActivity();

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage("Repeat Alarm")
        .setPositiveButton("Repeat",
            (dialog, id) -> dialogFragmentListener.onClickDialogListener(Activity.RESULT_OK))
        .setNegativeButton("Only Once",
            (dialog, id) -> dialogFragmentListener.onClickDialogListener(Activity.RESULT_CANCELED));
    return builder.create();
  }

  public interface RepeatDialogFragmentListener {
    void onClickDialogListener(int result);
  }
}

