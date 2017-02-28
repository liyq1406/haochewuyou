package com.haoche51.buyerapp.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.MyBookingVehicleAdapter;
import com.haoche51.buyerapp.entity.HCBookOrderEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCPullToRefresh;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 我的预约
 */
public class MyBookingVehicleActivity extends HCCommonTitleActivity {

  @InjectView(R.id.view_loading) View mPreLoadingView;

  @InjectView(R.id.tv_book_empty) View mEmptyView;

  @InjectView(R.id.hcptr_book) HCPullToRefresh mPullToRefresh;
  private MyBookingVehicleAdapter mBookAdapter;
  private List<HCBookOrderEntity> mDatas = new ArrayList<>();
  private int mCurrentPage = 0;

  @Override void initViews() {
    mBookAdapter = new MyBookingVehicleAdapter(this, mDatas, R.layout.lvitem_booking_vehicle);
    mPullToRefresh = (HCPullToRefresh) findViewById(R.id.hcptr_book);
    mPullToRefresh.setAdapter(mBookAdapter);
    mPullToRefresh.getListView().setEmptyView(mEmptyView);

    mPullToRefresh.setOnRefreshCallback(new HCPullToRefresh.OnRefreshCallback() {
      @Override public void onPullDownRefresh() {
        mCurrentPage = 0;
        requestOrders();
      }

      @Override public void onLoadMoreRefresh() {
        mCurrentPage++;
        requestOrders();
      }
    });

    requestOrders();
  }

  private void handleOrderResp(String resp) {
    if (mPullToRefresh == null) return;
    if (TextUtils.isEmpty(resp)) {
      mPullToRefresh.finishRefresh();
      return;
    }

    if (!TextUtils.isEmpty(resp)) {
      List<HCBookOrderEntity> data = HCJSONParser.parseBuyerOrder(resp);
      if (mCurrentPage == 0) {
        mDatas.clear();
      }
      mDatas.addAll(data);
      boolean isNoMore = data.size() < HCConsts.PAGESIZE;
      mPullToRefresh.setFooterStatus(isNoMore);
      if (isNoMore) {
        mPullToRefresh.removeFooter();
      }
    }
    mBookAdapter.notifyDataSetChanged();
    mPullToRefresh.finishRefresh();
    HCUtils.hideViewIfNeed(mPreLoadingView);
  }

  private void requestOrders() {
    Map<String, Object> params = HCParamsUtil.getBuyerOrder(mCurrentPage);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleOrderResp(responseJsonString);
      }
    }));
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    titleTv.setText(R.string.hc_book_vehicle);
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_my_book_vehicle;
  }

  private boolean isFirstResume = true;

  @Override protected void onResume() {
    super.onResume();
    doThirdPartyResume();
    if (isFirstResume) {
      isFirstResume = false;
    } else {
      mPullToRefresh.autoRefresh();
    }
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
