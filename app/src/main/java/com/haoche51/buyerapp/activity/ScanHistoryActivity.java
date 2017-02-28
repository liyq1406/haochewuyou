package com.haoche51.buyerapp.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.ScanHistoryAdapter;
import com.haoche51.buyerapp.entity.ScanHistoryEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCPullToRefresh;
import com.haoche51.custom.HCViewClickListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 浏览记录
 */
public class ScanHistoryActivity extends HCCommonTitleActivity {

  @InjectView(R.id.view_loading) View mLoadingView;
  @InjectView(R.id.linear_net_refresh) LinearLayout mNetErrLinear;

  @InjectView(R.id.pdv_history) HCPullToRefresh mPullToRefresh;
  @InjectView(R.id.rel_history_empty) RelativeLayout mEmptyParentRel;

  private List<ScanHistoryEntity> mHistoryData = new ArrayList<>();
  private ScanHistoryAdapter mHistoryAdapter;
  private long last_update_time;
  private long first_req_time = System.currentTimeMillis() / 1000;

  @Override void initViews() {

    initPullToRefresh();

    initRefreshListener();

    initClickListener();
  }

  private void initClickListener() {
    findViewById(R.id.btn_history_empty).setOnClickListener(mClickListener);
    findViewById(R.id.linear_net_refresh).setOnClickListener(mClickListener);
  }

  private void initPullToRefresh() {
    mHistoryAdapter = new ScanHistoryAdapter(this, mHistoryData, R.layout.lvitem_history);
    mPullToRefresh.setAdapter(mHistoryAdapter);
    mPullToRefresh.setEmptyView(mEmptyParentRel);
    last_update_time = first_req_time;
    requestHistoryData(last_update_time);
  }

  private void initRefreshListener() {
    mPullToRefresh.setOnRefreshCallback(new HCPullToRefresh.OnRefreshCallback() {
      @Override public void onPullDownRefresh() {
        last_update_time = first_req_time;
        requestHistoryData(last_update_time);
      }

      @Override public void onLoadMoreRefresh() {
        requestHistoryData(last_update_time);
      }
    });

    mPullToRefresh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= mHistoryData.size()) return;
        ScanHistoryEntity entity = mHistoryData.get(position);
        int vehicleSourceId = HCUtils.str2Int(entity.getId());
        if (vehicleSourceId > 0) {
          VehicleDetailActivity.idToThis(GlobalData.mContext, String.valueOf(vehicleSourceId),
              "浏览记录");
        }
      }
    });
  }

  private void requestHistoryData(long time) {
    Map<String, Object> params = HCParamsUtil.getScanHistory(time);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String resp) {
        if (mPullToRefresh != null) {
          handleHistoryData(resp);
        }
      }
    }));
  }

  private void handleHistoryData(String resp) {

    if (seeIfResponseEmpty(resp)) return;

    HCUtils.hideViewIfNeed(mNetErrLinear);

    if (!TextUtils.isEmpty(resp)) {
      List<ScanHistoryEntity> list = HCJSONParser.parseScanHistory(resp);
      list = removeValid(list);
      int size = list.size();
      if (size > 0) {
        if (last_update_time == first_req_time) {
          mHistoryData.clear();
        }
        removeRepeat(mHistoryData, list);
        mPullToRefresh.setFooterStatus(list);
        // 记录最后一条记录的时间
        last_update_time = list.get(size - 1).getCreate_time();
      } else {
        mPullToRefresh.setFooterStatus(true);
      }
    }
    mHistoryAdapter.notifyDataSetChanged();
    mPullToRefresh.finishRefresh();
    HCUtils.hideViewIfNeed(mLoadingView);
  }

  private boolean seeIfResponseEmpty(String resp) {
    boolean result = false;
    if (TextUtils.isEmpty(resp)) {
      result = true;
      mPullToRefresh.setFooterStatus(false);
      mPullToRefresh.finishRefresh();
      if (mHistoryData.isEmpty()) {
        HCUtils.hideViewIfNeed(mLoadingView);
        HCUtils.hideViewIfNeed(mEmptyParentRel);
        mNetErrLinear.setVisibility(View.VISIBLE);
      } else {
        HCUtils.toastNetError();
      }
    }

    return result;
  }

  private HCViewClickListener mClickListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {
      switch (v.getId()) {
        case R.id.btn_history_empty:
          FilterUtils.resetNormalToDefaultExceptCity();
          HCEvent.postEvent(HCEvent.ACTION_HOME_TO_MAINACT_GOOD_VEHICLES);
          finish();
          break;
        case R.id.linear_net_refresh://刷新
          if (!HCUtils.isNetAvailable()) {
            mNetErrLinear.setVisibility(View.VISIBLE);
            HCUtils.toastNetError();
          } else {
            HCUtils.hideViewIfNeed(mNetErrLinear);
            mLoadingView.setVisibility(View.VISIBLE);
            requestHistoryData(last_update_time);
          }
          break;
      }
    }
  };

  private void removeRepeat(List<ScanHistoryEntity> allData, List<ScanHistoryEntity> subData) {
    for (ScanHistoryEntity entity : subData) {
      if (!allData.contains(entity)) {
        allData.add(entity);
      }
    }
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    titleTv.setText(R.string.hc_p_history);
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_scan_history;
  }

  //CATION: 过滤掉一些脏数据.
  public List<ScanHistoryEntity> removeValid(List<ScanHistoryEntity> lists) {
    List<ScanHistoryEntity> results = new ArrayList<>();
    for (ScanHistoryEntity scan : lists) {
      if (!TextUtils.isEmpty(scan.getVehicle_name()) && HCUtils.str2Int(scan.getId()) > 0
          && HCUtils.str2Float(scan.getSeller_price()) > 0) {
        results.add(scan);
      }
    }
    return results;
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
