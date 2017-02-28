package com.haoche51.buyerapp.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.fragment.SubscribeConditionsFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * 可以左滑删除的   我的订阅
 */
public class ManageMySubActivity extends HCCommonTitleActivity {

  private final static String TAG = "ManageMySubActivity";

  @Override void initViews() {
    SubscribeConditionsFragment fragment = new SubscribeConditionsFragment();
    FragmentTransaction mTrans = getSupportFragmentManager().beginTransaction();
    mTrans.add(R.id.frame_mysub, fragment, TAG);
    mTrans.commit();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
    if (fragment != null) {
      getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
  }

  /***
   * 默认点击按钮finish当前Activity,默认隐藏最右边TextView
   */
  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    titleTv.setText(R.string.hc_home_enter_subscribe);
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_manage_mysub;
  }

  public void onResume() {
    super.onResume();
    MobclickAgent.onResume(this);       //统计时长
  }

  public void onPause() {
    super.onPause();
    MobclickAgent.onPause(this);
  }
}
