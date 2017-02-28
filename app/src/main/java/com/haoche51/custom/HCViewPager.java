package com.haoche51.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class HCViewPager extends ViewPager {
  private boolean isCanScroll = true;// 标记是否可以滑动翻页

  public HCViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  /**
   * 设置参数控制是否可以滑动翻页
   *
   * @param isCan boolean
   */
  public void setIsCanScroll(boolean isCan) {
    this.isCanScroll = isCan;
  }

  @Override protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
    if (v instanceof HCSeekBarPressure) {
      return true;
    }
    return super.canScroll(v, checkV, dx, x, y);
  }

  //private float downX, downY;
  //
  //@Override
  //public boolean onInterceptTouchEvent(MotionEvent ev) {
  //    if (!isCanScroll) {
  //        return false;// 在这里可以阻止左右滑动翻页
  //    }
  //    try {
  //        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
  //            downX = ev.getX();
  //            downY = ev.getY();
  //        } else {
  //            if (Math.pow(ev.getX() - downX, 2) < Math.pow(ev.getY() - downY, 2)) {
  //                getParent().requestDisallowInterceptTouchEvent(false);
  //                return false;
  //            }
  //        }
  //        getParent().requestDisallowInterceptTouchEvent(true);
  //        return super.onInterceptTouchEvent(ev);
  //    } catch (IllegalArgumentException e) {
  //        e.printStackTrace();
  //        return false;
  //    }
  //}

  @Override public void scrollTo(int x, int y) {
    super.scrollTo(x, y);
  }
}
