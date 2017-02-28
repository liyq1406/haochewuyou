package com.haoche51.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.haoche51.buyerapp.R;

public class SideBar extends View {
  // 触摸事件
  private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
  // 26个字母
  public static String[] letters = {
      "热", "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
      "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
  };
  //	public static String[] letters = { "A", "B", "C", "D",  "F", "G", "H", "J", "K", "L", "M", "N", "O",  "Q", "R", "S", "W", "X", "Y", "Z", "#" };
  private int choose = -1;// 选中
  private Paint paint = new Paint();

  private TextView mTextDialog;

  public void setTextView(TextView mTextDialog) {
    this.mTextDialog = mTextDialog;
  }

  public SideBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public SideBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SideBar(Context context) {
    super(context);
  }

  /**
   * 重写这个方法
   */
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    // 获取焦点改变背景颜色.
    int height = getHeight();// 获取对应高度
    int width = getWidth(); // 获取对应宽度
    int singleHeight = height / letters.length;// 获取每一个字母的高度
    int textSize = getResources().getDimensionPixelOffset(R.dimen.letter_text_size);

    int normalColor = getResources().getColor(R.color.home_grx_red);
    int choosedColor = getResources().getColor(R.color.light_reminder_red);

    for (int i = 0; i < letters.length; i++) {
      paint.setColor(normalColor);
      //paint.setTypeface(Typeface.DEFAULT_BOLD);
      paint.setAntiAlias(true);
      paint.setTextSize(textSize);
      // 选中的状态
      if (i == choose) {
        //字母表按下滑动时的颜色
        paint.setColor(choosedColor);
        //paint.setFakeBoldText(true);
      }
      // x坐标等于中间-字符串宽度的一半.
      float xPos = width / 2 - paint.measureText(letters[i]) / 2;
      float yPos = singleHeight * i + singleHeight;
      canvas.drawText(letters[i], xPos, yPos, paint);
      paint.reset();// 重置画笔
    }
  }

  @SuppressWarnings("deprecation") @Override public boolean dispatchTouchEvent(MotionEvent event) {
    final int action = event.getAction();
    final float y = event.getY();// 点击y坐标
    final int oldChoose = choose;
    final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
    final int c = (int) (y / getHeight() * letters.length);
    // 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

    switch (action) {
      case MotionEvent.ACTION_UP:
        //setBackgroundDrawable(new ColorDrawable(0x00000000));
        choose = -1;//
        invalidate();
        if (mTextDialog != null) {
          mTextDialog.setVisibility(View.INVISIBLE);
        }
        break;

      default:
        //setBackgroundResource(R.drawable.sidebar_background);
        if (oldChoose != c) {
          if (c >= 0 && c < letters.length) {
            if (listener != null) {
              listener.onTouchingLetterChanged(letters[c]);
            }
            if (mTextDialog != null) {
              mTextDialog.setText(letters[c]);
              mTextDialog.setVisibility(View.VISIBLE);
            }

            choose = c;
            invalidate();
          }
        }

        break;
    }
    return true;
  }

  /**
   * 向外公开的方法
   */
  public void setOnTouchingLetterChangedListener(
      OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
    this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
  }

  /**
   * 接口
   *
   * @author coder
   */
  public interface OnTouchingLetterChangedListener {
    void onTouchingLetterChanged(String s);
  }
}