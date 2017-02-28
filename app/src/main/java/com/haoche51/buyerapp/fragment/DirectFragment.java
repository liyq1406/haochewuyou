package com.haoche51.buyerapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.DirectAdapter;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.HCTodayNewArrivalEntity;
import com.haoche51.buyerapp.entity.HCVehicleItemEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.FragmentController;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCPullToRefresh;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 直营店
 */
public class DirectFragment extends HCBaseFragment {

  public static boolean isInitCompleted = false;

  private String host;

  @InjectView(R.id.linear_net_refresh) LinearLayout mNetErrLinear;

  @InjectView(R.id.hcptr_direct_list) HCPullToRefresh mPullToRefresh;

  @InjectView(R.id.linear_buycar_empty_parent_direct) LinearLayout mEmptyView;

  private DirectAdapter mDirectAdapter;
  private List<HCVehicleItemEntity> mDirectDatas = new ArrayList<>();
  private int mCurrentPage = 0;
  private int mTotalPage = 0;

  @InjectView(R.id.linear_direct_parent) LinearLayout mFilterParentLinear;

  private FilterTerm mLastTerm;

  /** 标记如果为排序引起的变化,不弹出提示 */
  private boolean isSortChangedData = false;

  private Handler timeHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      mDirectAdapter.timeCurrent = System.currentTimeMillis() / 1000;
      mDirectAdapter.notifyDataSetChanged();
      timeHandler.sendEmptyMessageDelayed(0, 1000);
    }
  };

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_direct;
  }

  @Override void doInitViewOrData() {

    if (!isDirectListVisible2User()) return;
    initCommonFilter();
    initPullToRefresh();
    initRefreshListener();
    initScrollListener();
    isInitCompleted = true;

    HCStatistic.directShowing();
  }

  private boolean isDirectListVisible2User() {
    CoreFragment coreFragmentXT = (CoreFragment) getParentFragment();
    if (coreFragmentXT == null) return false;
    int page = coreFragmentXT.getCurrentVisiblePage();
    return coreFragmentXT.isVisible() && (page == CoreFragment.PAGE_DIRECT);
  }

  private void initCommonFilter() {
    int top = HCUtils.getDimenPixels(R.dimen.px_45dp);
    ViewGroup.MarginLayoutParams mp =
        (ViewGroup.MarginLayoutParams) mFilterParentLinear.getLayoutParams();
    mp.setMargins(0, top, 0, 0);

    host = this.getClass().getSimpleName();
    CommonFilterFragment mFilterFragment = new CommonFilterFragment();
    Bundle bundle = new Bundle();
    bundle.putString(CommonFilterFragment.KEY_FOR_HOST, host);
    mFilterFragment.setArguments(bundle);
    FragmentTransaction trans = getChildFragmentManager().beginTransaction();
    trans.add(R.id.frame_direct_parent, mFilterFragment, host);
    trans.commitAllowingStateLoss();
    getChildFragmentManager().executePendingTransactions();
  }

  private void initPullToRefresh() {
    if (mPullToRefresh == null) return;
    int itemRes = R.layout.lvitem_singlepic_vehicle;
    mDirectAdapter = new DirectAdapter(getActivity(), mDirectDatas, itemRes);
    mPullToRefresh.setAdapter(mDirectAdapter);
    mPullToRefresh.setEmptyView(mEmptyView);
    mDirectAdapter.notifyDataSetChanged();
    mPullToRefresh.setFirstAutoRefresh();
    mPullToRefresh.setNoDefaultDivider();
    if ("1".equals(HCSpUtils.getHasLimitActivity())) {
      timeHandler.sendEmptyMessageDelayed(0, 1000);
    }
  }

  private void initRefreshListener() {
    mPullToRefresh.setOnRefreshCallback(new HCPullToRefresh.OnRefreshCallback() {
      @Override public void onPullDownRefresh() {
        mCurrentPage = 0;
        requestDirectVehicles(mCurrentPage);
      }

      @Override public void onLoadMoreRefresh() {
        requestDirectVehicles(++mCurrentPage);
      }
    });
  }

  private void computeTotal(int count) {
    int PSIZE = HCConsts.PAGESIZE;
    count = count < PSIZE ? 0 : count;
    mTotalPage = (count % PSIZE == 0) ? count / PSIZE : (count / PSIZE) + 1;
    if (mTotalPage == 0 && mDirectDatas.size() > 0) {
      mTotalPage = 1;
    }
  }

  private void initScrollListener() {
    mPullToRefresh.setHCscrollListener(new HCPullToRefresh.HCScrollListener() {
      @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == HCPullToRefresh.SCROLL_STATE_TOUCH_SCROLL
            || scrollState == HCPullToRefresh.SCROLL_STATE_FLING) {
          if (mTotalPage > 0) {
            int nowCurP = 0;
            int ps = 0;
            try {
              ListView pullListView = mPullToRefresh.getListView();
              if (pullListView != null) {
                ps = pullListView.getLastVisiblePosition();
              }
              if (ps > 0) {
                nowCurP = (ps / HCConsts.PAGESIZE) + 1;
              }
              HCUtils.showPageToast(nowCurP, mTotalPage);
            } catch (Exception e) {
              HCLog.d(TAG, "Direct onScrollStateChanged is crash");
            }
          }
        }
      }

      @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
      }
    });
  }

  private void onFilterChanged() {
    if (mPullToRefresh == null) return;
    if (!FilterUtils.getDirectFilterTerm().equals(mLastTerm)) {
      mPullToRefresh.autoRefresh();
    }
  }

  private void requestDirectVehicles(final int page) {
    Map<String, Object> params = HCParamsUtil.getDirectVehicleSouceList(page);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleDirectVehicles(responseJsonString);
      }
    }));

    mLastTerm = FilterUtils.getDirectFilterTerm();
  }

  private synchronized void handleDirectVehicles(String responseJsonString) {

    if (mPullToRefresh == null) return;

    if (seeIfResponseEmtpty(responseJsonString)) return;
    HCUtils.hideViewIfNeed(mNetErrLinear);

    int count = 0;
    if (!TextUtils.isEmpty(responseJsonString)) {
      try {
        List<HCVehicleItemEntity> datas = new ArrayList<>();
        HCTodayNewArrivalEntity bhcv = HCJSONParser.parseTodayNewArrivalList(responseJsonString);
        if (bhcv != null) {
          count = HCUtils.str2Int(bhcv.getCount());
          datas = bhcv.getVehicles();
        }
        if (mCurrentPage == 0) {
          mDirectDatas.clear();
          mPullToRefresh.tryToSmoothScrollUp();
          if (!isSortChangedData && count > 0 && isDirectListVisible2User()) {
            HCUtils.showCountToast(count);
          }
        }
        if (!HCUtils.isListEmpty(datas)) {
          datas = removeDuplicateData(mDirectDatas, datas);
          mDirectDatas.addAll(datas);
          boolean isNoMoreData = datas.size() < HCConsts.PAGESIZE;
          mPullToRefresh.setFooterStatus(isNoMoreData, R.string.hc_home_no_more_forum);
        } else {
          mPullToRefresh.setFooterStatus(true, R.string.hc_home_no_more_forum);
        }
        mDirectAdapter.notifyDataSetChanged();
      } catch (Exception e) {
        HCLog.d(TAG, "handleDirectVehicles is crash ...");
      }
    }
    mPullToRefresh.finishRefresh();
    HCSensorsUtil.viewClassifyPage("直营店", FilterUtils.getDirectFilterTerm());
    computeTotal(count);
  }

  private boolean seeIfResponseEmtpty(String responseJsonString) {
    boolean result = false;
    if (TextUtils.isEmpty(responseJsonString)) {
      result = true;
      mPullToRefresh.setFooterStatus(false);
      mPullToRefresh.finishRefresh();
      if (mDirectDatas.isEmpty()) {
        mNetErrLinear.setVisibility(View.VISIBLE);
      } else {
        HCUtils.toastNetError();
      }
    }
    return result;
  }

  /**
   * 查看全部直营店
   */
  @SuppressWarnings("unused")
  @OnClick({ R.id.linear_net_refresh, R.id.btn_buycar_empty_seeallcar_direct })
  public void onSeeAllClick(View v) {
    if (mPullToRefresh == null) return;
    if (!HCUtils.isNetAvailable()) {
      mNetErrLinear.setVisibility(View.VISIBLE);
      HCUtils.toastNetError();
    } else {
      FilterUtils.resetDirectToDefaultExceptCity();
      HCEvent.postEvent(HCEvent.ACTION_RESET_FILTERBAR_COLOR);
      HCEvent.postEvent(host + HCEvent.ACTION_BRAND_CHOOSED_CHANGE);
      HCEvent.postEvent(host + HCEvent.ACTION_PRICE_CHOOSED_CHANGE);
      mPullToRefresh.autoRefresh();
      mPullToRefresh.setVisibility(View.VISIBLE);
    }
  }

  /**
   * 试试帮买服务
   */
  @OnClick(R.id.btn_buycar_empty_helpbuy_direct) @SuppressWarnings("unused")
  public void onHelpbuyClick(View v) {
    goToHelpBuyPage();
  }

  @Override public void onEvent(HCCommunicateEntity entity) {
    if (entity == null || mPullToRefresh == null) return;
    String action = entity.getAction();

    switch (action) {
      case HCEvent.ACTION_CITYCHANGED:
        if (isInitCompleted) {
          mPullToRefresh.autoRefresh();
        }
        break;

      case HCEvent.ACTION_SWAPTO_CORE_INNER:
        if (CoreFragment.PAGE_DIRECT == entity.getIntValue()) {
          if (!isInitCompleted) {
            doInitViewOrData();
          }
        }
        break;

      case HCEvent.ACTION_DUPLICATE_CLICK_TAB: {
        String tab = entity.getStrValue();
        if (FragmentController.CORE_VEHICLE_TAG.equals(tab)) {
          if (getParentFragment() instanceof CoreFragment) {
            CoreFragment coreFragmentXT = (CoreFragment) getParentFragment();
            int page = coreFragmentXT.getCurrentVisiblePage();
            if (CoreFragment.PAGE_DIRECT == page) {
              mPullToRefresh.tryToScrollUp();
            }
          }
        }
      }
      break;
      case HCEvent.ACTION_IS_LIMIT_ACTIVITY:
        String hasLimitActivity = entity.getStrValue();
        if (timeHandler != null) {
          timeHandler.removeCallbacksAndMessages(null);
          if ("1".equals(hasLimitActivity)) {
            if (mDirectAdapter != null) {
              mDirectAdapter.timeCurrent = System.currentTimeMillis() / 1000;
              timeHandler.sendEmptyMessageDelayed(0, 1000);
            }
          }
        }
        break;
    }

    handleChangedEvent(entity);
  }

  private void handleChangedEvent(HCCommunicateEntity communicateEntity) {
    String action = communicateEntity.getAction();
    if (action.equals(host + HCEvent.ACTION_FILTER_CHANGED)) {
      isSortChangedData = false;
      onFilterChanged();
      abordEvent(communicateEntity);
    }
    if (action.equals(host + HCEvent.ACTION_SORT_CHOOSED)) {
      //排序引起的变化
      isSortChangedData = true;
      onFilterChanged();
      abordEvent(communicateEntity);
    }
  }

  private List<HCVehicleItemEntity> removeDuplicateData(List<HCVehicleItemEntity> total,
      List<HCVehicleItemEntity> data) {
    List<HCVehicleItemEntity> result = new ArrayList<>();
    if (total == null || data == null) return result;
    for (HCVehicleItemEntity entity : data) {
      if (!total.contains(entity)) {
        result.add(entity);
      }
    }
    return result;
  }

  @Override public void onResume() {
    super.onResume();
    ThirdPartInjector.onPageStart(this.getClass().getSimpleName());
  }

  @Override public void onPause() {
    super.onPause();
    ThirdPartInjector.onPageEnd(this.getClass().getSimpleName());
  }

  @Override public void onDestroy() {
    timeHandler.removeCallbacksAndMessages(null);
    super.onDestroy();
  }
}
