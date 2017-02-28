package com.haoche51.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCUtils;
import com.nineoldandroids.animation.ValueAnimator;

public class HCProfileListView extends ListView {
    public HCProfileListView(Context context) {
        super(context);
    }

    public HCProfileListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HCProfileListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void addHeaderView(View v) {
        super.addHeaderView(v);
    }

    private RelativeLayout rLayout;
    private LinearLayout.LayoutParams layoutParams;
    private int width, height;
    private float beforeY = 0;
    private final static String TAG = "HCProfileListView";
    private boolean flag = true;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (rLayout == null) {
            View view = getChildAt(0);
            if (view != null) {
                rLayout = (RelativeLayout) view.findViewById(R.id.relative_profile_top);
                height = rLayout.getLayoutParams().height;
                width = HCUtils.getScreenWidthInPixels();
                HCLog.d(TAG, "height == " + height + "width == " + width);
                layoutParams = new LinearLayout.LayoutParams(width, height);
            }
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录按下的位置
                beforeY = ev.getY();
                HCLog.d(TAG, "beforeY == " + beforeY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (flag) {
                    beforeY = ev.getY();
                    flag = false;
                }
                float dy = ev.getY() - beforeY; // 得到y轴的移动距离
                HCLog.d(TAG, "dy == " + dy);
                if (dy > 0 && rLayout != null) {
                    int destH = (int) (dy / 2 + height);
                    layoutParams.height = destH;
                    layoutParams.width = width;
                    HCLog.d(TAG, "destH == " + destH + "width == " + width);
                    rLayout.setLayoutParams(layoutParams);
                }
                break;
            case MotionEvent.ACTION_UP:
                //手指松开，还原图片
                if (rLayout != null) {
                    animateLayout(rLayout, rLayout.getLayoutParams().height, height);
                    flag = true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void animateLayout(final View view, int from, int to) {
        if (view == null) return;
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int now = (int) animation.getAnimatedValue();
                lp.height = now;
                view.requestLayout();
            }
        });
        animator.start();
    }

}
