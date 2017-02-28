package com.haoche51.buyerapp.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.InjectView;

import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCViewClickListener;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.SinglePicVehicleAdapter;
import com.haoche51.buyerapp.entity.HCVehicleItemEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.custom.HCPullToRefresh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 新的推荐车源列表
 */
public class RecommendVehicleActivity extends HCCommonTitleActivity {

  @InjectView(R.id.view_loading) View mPreLoadingView;
  @InjectView(R.id.linear_net_refresh) LinearLayout mNetErrLinear;
  @InjectView(R.id.hcptr_recommand_list) HCPullToRefresh mPullToRefresh;
  private List<HCVehicleItemEntity> mVehicleDatas = new ArrayList<>();
  private SinglePicVehicleAdapter mVehicleAdapter;

  @Override void initViews() {
    int itemRes = R.layout.lvitem_singlepic_vehicle;
    mVehicleAdapter = new SinglePicVehicleAdapter(this, mVehicleDatas, itemRes);
    mPullToRefresh.setCanPull(false);
    mPullToRefresh.setNoDefaultDivider();
    mPullToRefresh.setAdapter(mVehicleAdapter);

    mPullToRefresh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < mVehicleDatas.size()) {
          HCVehicleItemEntity curEntity = mVehicleDatas.get(position);
          String curId = curEntity.getId();
          VehicleDetailActivity.idToThis(RecommendVehicleActivity.this, curId, "推荐");
        }
      }
    });

    initClickListener();

    requestRecommandVehicle();
  }

  private void initClickListener() {
    mNetErrLinear.setOnClickListener(mClickListener);
  }

  private void requestRecommandVehicle() {
    if (!HCUtils.isNetAvailable()) {
      HCUtils.hideViewIfNeed(mPreLoadingView);
      mNetErrLinear.setVisibility(View.VISIBLE);
      HCUtils.toastNetError();
      return;
    }

    Map<String, Object> params = HCParamsUtil.getRecommendVehicles();
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleRecommandVehicle(responseJsonString);
      }
    }));
  }

  private void handleRecommandVehicle(String responseJsonString) {
    if (mPullToRefresh == null) return;

    HCUtils.hideViewIfNeed(mNetErrLinear);
    HCUtils.hideViewIfNeed(mPreLoadingView);

    if (!TextUtils.isEmpty(responseJsonString)) {
      List<HCVehicleItemEntity> vehicles = HCJSONParser.parseRecommendList(responseJsonString);
      if (!HCUtils.isListEmpty(vehicles)) {
        mVehicleDatas.clear();
        mVehicleDatas.addAll(vehicles);
        mPullToRefresh.setFooterStatus(true);
        mVehicleAdapter.notifyDataSetChanged();
      }
    }
  }

  private HCViewClickListener mClickListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {
      mPreLoadingView.setVisibility(View.VISIBLE);
      requestRecommandVehicle();
    }
  };

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    titleTv.setText(R.string.hc_p_recommand);
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_recommand_vehicle;
  }

  @Override protected void onResume() {
    super.onResume();

    doThirdPartyResume();
  }

  @Override protected void onPause() {
    super.onPause();

    doThirdPartyPause();
  }

  private void doThirdPartyResume() {
    String name = this.getClass().getSimpleName();
    ThirdPartInjector.onPageStart(name);
    ThirdPartInjector.onResume(this);
  }

  private void doThirdPartyPause() {
    String name = this.getClass().getSimpleName();
    ThirdPartInjector.onPageEnd(name);
    ThirdPartInjector.onPause(this);
  }
}
