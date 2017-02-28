package com.haoche51.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.Timer;
import java.util.TimerTask;

public class HCNormalToast {

  private WindowManager mWindowManager;
  private String text;
  private View mView;
  private WindowManager.LayoutParams mParams;
  private boolean isShowing;

  public HCNormalToast(String text) {
    mWindowManager = (WindowManager) GlobalData.mContext.getSystemService(Context.WINDOW_SERVICE);
    this.text = text;
    mView = makeView();
    mParams = makeParams();
  }

  private WindowManager.LayoutParams makeParams() {
    WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
    mParams.format = PixelFormat.TRANSLUCENT;
    mParams.windowAnimations = R.style.hc_custom_toast_anim;//设置进入退出动画效果
    mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
    mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
    mParams.gravity = Gravity.BOTTOM;
    mParams.y = HCUtils.getDimenPixels(R.dimen.px_60dp);
    return mParams;
  }

  private View makeView() {
    final Toast toast = Toast.makeText(GlobalData.mContext, text, Toast.LENGTH_SHORT);
    TextView view = new TextView(GlobalData.mContext);
    view.setGravity(Gravity.CENTER);
    view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
    view.setTextColor(Color.WHITE);
    view.setBackgroundResource(R.drawable.bg_custom_toast);
    view.setText(text);
    toast.setView(view);
    return toast.getView();
  }

  public void show() {
    if (!isShowing) {//如果Toast没有显示，则开始加载显示
      isShowing = true;
      try {
        mWindowManager.addView(mView, mParams);//将其加载到windowManager上
      } catch (Exception e) {
      }
      Timer mTimer = new Timer();
      mTimer.schedule(new TimerTask() {
        @Override public void run() {
          try {
            mWindowManager.removeView(mView);
            isShowing = false;
          } catch (Exception e) {
          }
        }
      }, 800);
    }
  }
}
