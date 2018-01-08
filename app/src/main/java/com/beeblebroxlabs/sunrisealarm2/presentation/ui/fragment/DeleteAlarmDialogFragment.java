package com.beeblebroxlabs.sunrisealarm2.presentation.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;


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
    builder.setMessage("Delete")
        .setPositiveButton("OK", (dialog, id) -> {
          listener.onClickDeleteDialogListener(Activity.RESULT_OK,position);
          Toast.makeText(getActivity().getApplicationContext(), "Alarm Deleted",
              Toast.LENGTH_SHORT).show();
        })
        .setNegativeButton("Cancel",
            (dialog, id) -> listener.onClickDeleteDialogListener(Activity.RESULT_CANCELED,position));
    return builder.create();
  }

  public interface DeleteDialogFragmentListener {

    void onClickDeleteDialogListener(int result, int position);
  }
}

