package com.haoche51.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.haoche51.buyerapp.R;

public class NavigationButton extends RadioButton {

  private boolean reDraw = false;
  private int radius;
  private Paint mPaint;

  public NavigationButton(Context context) {
    super(context);
    init(context);
  }

  public NavigationButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public NavigationButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  private void init(Context context) {
    mPaint = new Paint();
    // mPaint.setAntiAlias(true);
    mPaint.setColor(Color.RED);
    radius = getResources().getDimensionPixelSize(R.dimen.indicator_radius);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (reDraw) {
      canvas.drawCircle(getWidth() / 2 + 3 * radius, radius, radius, mPaint);
    }
  }

  public void hideIndicator() {
    reDraw = false;
    invalidate();
  }

  public void showIndicator() {
    reDraw = true;
    invalidate();
  }
}
