package com.haoche51.custom;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * 下拉小车的刷新，用的时候可以直接添加，勿删
 */
public class HCRefreshHeader extends FrameLayout implements PtrUIHandler {
  private TextView mTitleTextView;
  private ImageView mLoadingIv;

  public HCRefreshHeader(Context context) {
    super(context);
    initViews();
  }

  public HCRefreshHeader(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViews();
  }

  public HCRefreshHeader(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initViews();
  }

  protected void initViews() {
    int resLayout = R.layout.layout_for_refreshheader;
    View header = LayoutInflater.from(getContext()).inflate(resLayout, this);
    mLoadingIv = (ImageView) header.findViewById(R.id.iv_refresh);
    mTitleTextView = (TextView) header.findViewById(R.id.tv_refresh);
  }

  @Override public void onUIReset(PtrFrameLayout frame) {
  }

  @Override public void onUIRefreshPrepare(PtrFrameLayout frame) {
    if (!frame.isPullToRefresh()) {
      mTitleTextView.setText(getResources().getString(R.string.hc_pull_down));
    }
  }

  @Override public void onUIRefreshBegin(PtrFrameLayout frame) {

    Drawable drawable = mLoadingIv.getBackground();
    if (drawable instanceof AnimationDrawable) {
      AnimationDrawable anim = (AnimationDrawable) drawable;
      anim.start();
    }
    mTitleTextView.setText(R.string.hc_refreshing);
  }

  @Override public void onUIRefreshComplete(PtrFrameLayout frame) {

    Drawable drawable = mLoadingIv.getBackground();
    if (drawable instanceof AnimationDrawable) {
      AnimationDrawable anim = (AnimationDrawable) drawable;
      anim.stop();
    }
    mTitleTextView.setText(R.string.hc_refresh_complete);
  }

  @Override public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status,
      PtrIndicator ptrIndicator) {

    final int mOffsetToRefresh = frame.getOffsetToRefresh();
    final int currentPos = ptrIndicator.getCurrentPosY();
    final int lastPos = ptrIndicator.getLastPosY();

    if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
      if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
        crossRotateLineFromBottomUnderTouch(frame);
      }
    } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
      if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
        crossRotateLineFromTopUnderTouch(frame);
      }
    }
  }

  private void crossRotateLineFromTopUnderTouch(PtrFrameLayout frame) {
    if (!frame.isPullToRefresh()) {
      mTitleTextView.setText(R.string.hc_release_to_refresh);
    }
  }

  private void crossRotateLineFromBottomUnderTouch(PtrFrameLayout frame) {
    if (!frame.isPullToRefresh()) {
      mTitleTextView.setText(getResources().getString(R.string.hc_pull_down));
    }
  }
}
