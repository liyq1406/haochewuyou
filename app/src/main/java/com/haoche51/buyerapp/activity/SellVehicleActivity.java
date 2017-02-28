package com.haoche51.buyerapp.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.fragment.SellVehicleFragment;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCUtils;
import com.umeng.analytics.MobclickAgent;

public class SellVehicleActivity extends HCCommonTitleActivity {

  private final static String TAG = "SellVehicleActivity";

  @Override void initViews() {
    SellVehicleFragment fragment = new SellVehicleFragment();
    FragmentTransaction mTrans = getSupportFragmentManager().beginTransaction();
    mTrans.add(R.id.frame_sell_vehicle, fragment, TAG);
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
    titleTv.setText(R.string.sold_indicator);
    rightTv.setText(R.string.hc_my_vehicle);
    rightTv.setTextColor(HCUtils.getResColor(R.color.home_grx_red));
    rightTv.setTextSize(15);
    rightTv.setVisibility(View.VISIBLE);
    rightTv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent();
        if (HCUtils.isUserLogined()) {
          intent.setClass(SellVehicleActivity.this, MySoldVehiclesActivity.class);
        } else {
          intent.setClass(SellVehicleActivity.this, LoginActivity.class);
          intent.putExtra(HCConsts.INTENT_KEY_LOGIN_DEST, MySoldVehiclesActivity.class);
        }
        startActivity(intent);
      }
    });
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_sell_vehicle;
  }

  public void onResume() {
    super.onResume();
    MobclickAgent.onResume(this);
  }

  public void onPause() {
    super.onPause();
    MobclickAgent.onPause(this);
  }
}
