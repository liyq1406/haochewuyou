package com.haoche51.buyerapp.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.UseCouponAdapter;
import com.haoche51.buyerapp.entity.HCCouponEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCPullToRefresh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 选择优惠券
 */
public class UseCouponActivity extends HCCommonTitleActivity {

  private int mCurrentPage;
  private HCPullToRefresh mPullToRefresh;
  private TextView mUseNowTv;
  private TextView mCouponDetailTv;
  private String mOrderId;

  private UseCouponAdapter mCouponAdapter;
  private List<HCCouponEntity> mCouponDatas = new ArrayList<>();

  private View.OnClickListener mClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      switch (v.getId()) {
        case R.id.tv_coupon_usage://使用说明
          DialogUtils.showCouponUsage(UseCouponActivity.this);
          break;

        case R.id.tv_use_coupon_now://使用优惠券
          if (v.getTag() != null) {
            HCCouponEntity entity = (HCCouponEntity) v.getTag();
            String url = HCUtils.getUseCouponURL(entity, mOrderId);
            Intent moreIntent = new Intent(GlobalData.mContext, WebBrowserActivity.class);
            moreIntent.putExtra(HCConsts.INTENT_KEY_URL, url);
            startActivity(moreIntent);
            finish();
          }
          break;
      }
    }
  };

  @Override void initViews() {

    mOrderId = getIntent().getStringExtra(HCConsts.INTENT_KEY_ORDERID);

    mPullToRefresh = (HCPullToRefresh) findViewById(R.id.hcptr_choose_coupon);
    mCouponAdapter = new UseCouponAdapter(this, mCouponDatas, R.layout.lvitem_use_coupon);
    mPullToRefresh.getListView().setAdapter(mCouponAdapter);
    mPullToRefresh.getListView().setDividerHeight(0);
    mPullToRefresh.setFirstAutoRefresh();

    mPullToRefresh.setOnRefreshCallback(new HCPullToRefresh.OnRefreshCallback() {
      @Override public void onPullDownRefresh() {
        mCurrentPage = 0;
        requestCouponDatas();
      }

      @Override public void onLoadMoreRefresh() {
        mCurrentPage++;
        requestCouponDatas();
      }
    });

    mCouponAdapter.setOnCouponChoosedListener(new UseCouponAdapter.onCouponChoosedListener() {
      @Override public void onChoosed(HCCouponEntity entity) {
        boolean clickable = entity != null;
        int res = entity != null ? R.drawable.selector_rect_red : R.drawable.bg_rect_gray;
        mUseNowTv.setClickable(clickable);
        mUseNowTv.setBackgroundResource(res);
        if (entity != null) {
          mCouponDetailTv.setText(entity.getTitle());
          mUseNowTv.setTag(entity);
        } else {
          mCouponDetailTv.setText(R.string.hc_no_choose_coupon);
        }
      }
    });

    mUseNowTv = (TextView) findViewById(R.id.tv_use_coupon_now);
    mCouponDetailTv = (TextView) findViewById(R.id.tv_coupon_detail);
    mUseNowTv.setOnClickListener(mClickListener);
    findViewById(R.id.tv_coupon_usage).setOnClickListener(mClickListener);
  }

  private void requestCouponDatas() {
    Map<String, Object> params = HCParamsUtil.getAvailableCoupons(mCurrentPage);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleCouponDatas(responseJsonString);
      }
    }));
  }

  private void handleCouponDatas(String responseJsonString) {
    if (mPullToRefresh == null) return;

    if (!TextUtils.isEmpty(responseJsonString)) {
      List<HCCouponEntity> entities = HCJSONParser.parseCouponList(responseJsonString);
      if (mCurrentPage == 0) {
        mCouponDatas.clear();
      }
      mCouponDatas.addAll(entities);
      boolean isNoMoreData = entities.size() < HCConsts.PAGESIZE;
      mPullToRefresh.setFooterStatus(isNoMoreData, R.string.hc_no_more_coupon);
    }

    mPullToRefresh.finishRefresh();
    mCouponAdapter.notifyDataSetChanged();
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    titleTv.setText(R.string.hc_choose_coupon);
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_use_coupon;
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

