package com.haoche51.buyerapp.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.fragment.MyCollectionFragment;
import com.umeng.analytics.MobclickAgent;

public class MyCollectionActivity extends HCCommonTitleActivity {

  private final static String TAG = "MyCollectionActivity";

  @Override void initViews() {
    MyCollectionFragment fragment = new MyCollectionFragment();
    FragmentTransaction mTrans = getSupportFragmentManager().beginTransaction();
    mTrans.add(R.id.frame_my_coll, fragment, TAG);
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
    titleTv.setText(R.string.hc_core_title_collection);
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_my_coll;
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
