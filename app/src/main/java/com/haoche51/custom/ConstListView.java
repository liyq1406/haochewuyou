package com.haoche51.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ConstListView extends ListView {

  public ConstListView(Context context) {
    super(context);
  }

  public ConstListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ConstListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);
  }
}
