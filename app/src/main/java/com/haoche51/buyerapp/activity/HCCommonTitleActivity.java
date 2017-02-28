package com.haoche51.buyerapp.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.haoche51.buyerapp.R;

/**
 * 公共的标题,顶部按钮有默认finish当前Activity操作
 */
public abstract class HCCommonTitleActivity extends FragmentActivity {

  protected final String TAG = this.getClass().getSimpleName();

  /** 顶部title父布局 */
  private LinearLayout mLinearTitleLayout;

  /**
   * 真正的contentView
   */
  protected FrameLayout mRootView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_common);
    mLinearTitleLayout = (LinearLayout) findViewById(R.id.main_titlebar);
    /*
    顶部返回按钮
   */
    TextView mLeftBackTv = (TextView) findViewById(R.id.tv_common_back);
    /*
    顶部标题文字
   */
    TextView mTitleTv = (TextView) findViewById(R.id.tv_common_title);
    /*
    顶部最后
   */
    TextView mRightTv = (TextView) findViewById(R.id.tv_common_right);
    initTitleBar(mLeftBackTv, mTitleTv, mRightTv);
    mRootView = (FrameLayout) findViewById(R.id.frame_content_container);
    View view = LayoutInflater.from(this).inflate(getContentViewResouceId(), mRootView, false);
    mRootView.addView(view);
    handleBackTv(mLeftBackTv);
    ButterKnife.inject(this, view);
    initViews();
  }

  protected void handleBackTv(TextView tv) {
    tv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
  }

  protected void hideTitleBar() {
    mLinearTitleLayout.setVisibility(View.GONE);
  }

  abstract void initViews();

  /***
   * 默认点击按钮finish当前Activity,默认隐藏最右边TextView
   */
  abstract void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv);

  abstract int getContentViewResouceId();

}
