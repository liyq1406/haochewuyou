package com.haoche51.custom;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/***
 * 在scrollView内部view发生变化时,不让其自动滚动
 * 竖直方向和水平方向事件冲突
 */
public class HCScrollView extends ScrollView {

  public HCScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public HCScrollView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HCScrollView(Context context) {
    this(context, null, 0);
  }

  @Override protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
    //		return super.computeScrollDeltaToGetChildRectOnScreen(rect);
    return 0;
  }

  // 滑动距离及坐标
  private float xDistance, yDistance, xLast, yLast;

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        xDistance = yDistance = 0f;
        xLast = ev.getX();
        yLast = ev.getY();
        break;
      case MotionEvent.ACTION_MOVE:
        final float curX = ev.getX();
        final float curY = ev.getY();

        xDistance += Math.abs(curX - xLast);
        yDistance += Math.abs(curY - yLast);
        xLast = curX;
        yLast = curY;

        if (xDistance > yDistance) {
          return false; // 表示向下传递事件
        }
    }

    return super.onInterceptTouchEvent(ev);
  }
}
