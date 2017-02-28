package com.haoche51.custom;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class DoubleClickListener implements OnClickListener {

  private static final long DEFAULT_MIN_TIME_IN_MILLIS = 300;
  private long mMinDiffTimeInMillis;
  private long mLastClickTimeInMillis;

  public DoubleClickListener() {
    mMinDiffTimeInMillis = DEFAULT_MIN_TIME_IN_MILLIS;
    mLastClickTimeInMillis = 0;
  }

  @Override public void onClick(View v) {
    if ((System.currentTimeMillis() - mLastClickTimeInMillis) < mMinDiffTimeInMillis) {
      if (v != null) {
        performDoubleClick(v);
      }
    }
    mLastClickTimeInMillis = System.currentTimeMillis();
  }

  public abstract void performDoubleClick(View v);
}

