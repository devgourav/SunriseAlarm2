package com.beeblebroxlabs.sunrisealarm2.presentation.ui;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import com.beeblebroxlabs.sunrisealarm2.presentation.ui.adapter.AlarmListAdapter;
import com.beeblebroxlabs.sunrisealarm2.presentation.ui.fragment.DeleteAlarmDialogFragment;

/**
 * Created by devgr on 12-Jan-18.
 */

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

  private RecyclerItemTouchHelperListener listener;

  public RecyclerItemTouchHelper(int dragDirs, int swipeDirs,
      RecyclerItemTouchHelperListener listener) {
    super(dragDirs, swipeDirs);
    this.listener = listener;
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
    return false;
  }

  @Override
  public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
    final View foregroundView = ((AlarmListAdapter.ViewHolder) viewHolder).viewForeground;
    getDefaultUIUtil().clearView(foregroundView);
  }

  @Override
  public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
    if (viewHolder != null) {
      final View foregroundView = ((AlarmListAdapter.ViewHolder) viewHolder).viewForeground;
      getDefaultUIUtil().onSelected(foregroundView);
    }
  }

  @Override
  public void onChildDrawOver(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX,
      float dY, int actionState, boolean isCurrentlyActive) {
    final View foregroundView = ((AlarmListAdapter.ViewHolder) viewHolder).viewForeground;
    getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
        actionState, isCurrentlyActive);
  }

  @Override
  public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX,
      float dY, int actionState, boolean isCurrentlyActive) {

    final View foregroundView = ((AlarmListAdapter.ViewHolder) viewHolder).viewForeground;
    getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
        actionState, isCurrentlyActive);
  }

  @Override
  public void onSwiped(ViewHolder viewHolder, int direction) {
    listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
  }

  public interface RecyclerItemTouchHelperListener {

    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
  }
}
