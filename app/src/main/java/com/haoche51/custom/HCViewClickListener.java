package com.haoche51.custom;

import android.view.View;

import com.haoche51.buyerapp.util.HCUtils;

/**
 * 不允许两次click事件间隔少于MIN_CLICK_INTERVAL
 * 防止view莫名其妙为空的情况
 */
public abstract class HCViewClickListener implements View.OnClickListener {

  private String TAG = "HCViewClickListener";
  /**
   * Click事件最少间隔时间
   */
  private final static long MIN_CLICK_INTERVAL = 500L;
  private long mLastClickTime = 0;

  @Override public void onClick(View v) {
    if (v != null) {
      long deltaTime = HCUtils.now() - mLastClickTime;
      if (deltaTime > MIN_CLICK_INTERVAL) {
        performViewClick(v);
        mLastClickTime = HCUtils.now();
      }
    }
  }

  public abstract void performViewClick(View v);
}
