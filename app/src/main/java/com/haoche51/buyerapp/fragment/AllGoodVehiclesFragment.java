package com.haoche51.buyerapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.LoginActivity;
import com.haoche51.buyerapp.adapter.AllGoodVehicleAdapter;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.BHCVehicleItemEntity;
import com.haoche51.buyerapp.entity.HCBSubscribeIdEntity;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.HCQueryEntity;
import com.haoche51.buyerapp.entity.HCSearchEntity;
import com.haoche51.buyerapp.entity.HCSubcribeEntity;
import com.haoche51.buyerapp.entity.HCVehicleItemEntity;
import com.haoche51.buyerapp.entity.SubConditionDataEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.FragmentController;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCPullToRefresh;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AllGoodVehiclesFragment extends HCBaseFragment {

  public static boolean isInitCompleted = false;

  private String mHost;

  @InjectView(R.id.view_loading) View mViewForLoading;

  @InjectView(R.id.linear_net_refresh) LinearLayout mNetErrLinear;

  @InjectView(R.id.rel_choose_parent) RelativeLayout mConditionParent;

  /**
   * 订阅按钮
   */
  @InjectView(R.id.tv_filter_subscribe) TextView mSubVehicleTv;

  @InjectView(R.id.linear_for_animate) LinearLayout mLinearForAnimate;

  @InjectView(R.id.hcptr_buy_list) HCPullToRefresh mPullToRefresh;

  @InjectView(R.id.linear_filter_choosed) LinearLayout mLinearCondition;

  private List<HCVehicleItemEntity> mVehicleDatas = new ArrayList<>();
  private AllGoodVehicleAdapter allGoodVehicleAdapter;
  private int mCurrentPage = 0;
  private int mTotalPage = 0;

  private int mConditionParentHeight = HCUtils.getDimenPixels(R.dimen.px_50dp);
  private boolean scrollFlag = false;
  private int lastVisibleItemPosition;
  /**
   * 标识是否为搜索结果返回
   */
  private boolean isSearchResult = false;

  /** 表示是否正在展示订阅条件 */
  private boolean isConditionShowing = false;

  /**
   * 记录上一次搜索词
   */
  private String mLastSearchWord = "";

  /**
   * 记录上一次筛选条件
   */
  private FilterTerm mLastTerm;

  private boolean isSortViewShowing = true;

  /** 标记如果为排序引起的变化,不弹出提示 */
  private boolean isSortChangedData = false;
  private View headerView;
  private ListView listView;

  private Handler timeHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      allGoodVehicleAdapter.timeCurrent = System.currentTimeMillis() / 1000;
      allGoodVehicleAdapter.notifyDataSetChanged();
      timeHandler.sendEmptyMessageDelayed(0, 1000);
    }
  };

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_all_good_vehicles;
  }

  @Override void doInitViewOrData() {
    if (!isNowListVisible2User()) return;
    doInitViewOrDataTemp();
    //统计 全部好车
    HCStatistic.vehicleListShowing();
  }

  private void doInitViewOrDataTemp() {
    initCommonFilter();
    initPullToRefresh();
    initRefreshListener();
    initScrollListener();
    initHeaderView();
    isInitCompleted = true;
  }

  private void initHeaderView() {
    int layoutRes = R.layout.empty_all_good_vehicle_list_headerview;
    headerView = LayoutInflater.from(getActivity()).inflate(layoutRes, null);
    listView = mPullToRefresh.getListView();
  }

  private void initCommonFilter() {
    mHost = this.getClass().getSimpleName();
    CommonFilterFragment mFilterFragment = new CommonFilterFragment();
    Bundle bundle = new Bundle();
    bundle.putString(CommonFilterFragment.KEY_FOR_HOST, mHost);
    mFilterFragment.setArguments(bundle);
    FragmentTransaction trans = getChildFragmentManager().beginTransaction();
    trans.add(R.id.frame_parent_vehicle_list, mFilterFragment);
    trans.commitAllowingStateLoss();
    getChildFragmentManager().executePendingTransactions();
  }

  private void initPullToRefresh() {
    int itemRes = R.layout.lvitem_all_good_vehicle;
    allGoodVehicleAdapter = new AllGoodVehicleAdapter(getActivity(), mVehicleDatas, itemRes);
    mPullToRefresh.setAdapter(allGoodVehicleAdapter);
    allGoodVehicleAdapter.notifyDataSetChanged();
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
        requestData();
      }

      @Override public void onLoadMoreRefresh() {
        mCurrentPage++;
        requestData();
      }
    });
  }

  private void initScrollListener() {
    mPullToRefresh.setHCscrollListener(new HCPullToRefresh.HCScrollListener() {
      @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == HCPullToRefresh.SCROLL_STATE_TOUCH_SCROLL
            || scrollState == HCPullToRefresh.SCROLL_STATE_FLING) {
          scrollFlag = true;
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
              HCLog.d(TAG, "AllGood onScrollStateChanged is crash");
            }
          }
        } else {
          scrollFlag = false;
        }
      }

      @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        if (scrollFlag) {
          if (firstVisibleItem > lastVisibleItemPosition) {
            if (isSortViewShowing) {
              hideFilterBar();
            }
          }
          if (firstVisibleItem < lastVisibleItemPosition) {
            if (!isSortViewShowing) {
              showFilterBar();
            }
          }
          if (firstVisibleItem == lastVisibleItemPosition) {
            return;
          }
          lastVisibleItemPosition = firstVisibleItem;
        }
      }
    });
  }

  private void showFilterBar() {
    if (mPullToRefresh == null) return;
    mPullToRefresh.postDelayed(new Runnable() {
      @Override public void run() {
        isSortViewShowing = true;
      }
    }, 150);
    mPullToRefresh.setFilterBarVisible(true);
    if (isConditionShowing) {
      HCViewUtils.expand(mLinearForAnimate);
      HCViewUtils.animateLayout(mPullToRefresh, 0, mConditionParentHeight);
    }
  }

  private void hideFilterBar() {
    if (mPullToRefresh == null) return;
    if (isConditionShowing) {
      HCViewUtils.collapse(mLinearForAnimate);
      HCViewUtils.animateLayout(mPullToRefresh, mConditionParentHeight, 0);
    }
    mPullToRefresh.postDelayed(new Runnable() {
      @Override public void run() {
        isSortViewShowing = false;
      }
    }, 150);
    mPullToRefresh.setFilterBarVisible(false);
  }

  private void requestData() {
    if (allGoodVehicleAdapter != null) {
      allGoodVehicleAdapter.setNeedShowGuessView(false);
    }
    if (isSearchResult) {
      requestSearchData();
    } else {
      requestVehicleSource();
    }
    mLastTerm = FilterUtils.getNormalFilterterm();
  }

  private void requestVehicleSource() {
    Map<String, Object> params = HCParamsUtil.getVehicleSourceList(mCurrentPage);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleVehicleSource(responseJsonString);
      }
    }));
  }

  private void handleVehicleSource(String responseJsonString) {

    if (mPullToRefresh == null) return;

    if (seeIfResponseEmtpty(responseJsonString)) return;

    HCUtils.hideViewIfNeed(mNetErrLinear);
    HCUtils.hideViewIfNeed(mViewForLoading);

    showConditionView();

    int count = 0;
    int showCount;
    int realCount;
    if (!TextUtils.isEmpty(responseJsonString)) {
      try {
        BHCVehicleItemEntity bhcv = HCJSONParser.parseGetVehicleSourceList(responseJsonString);
        count = HCUtils.str2Int(bhcv.getCount());
        showCount = HCUtils.str2Int(bhcv.getShow_count());
        seeIfNeedScrollUp(showCount);

        List<HCVehicleItemEntity> datas = bhcv.getVehicles();
        if (!HCUtils.isListEmpty(datas)) {
          removeEmptyHeadView(datas.get(0));
          setEmptyHeadView(datas.get(0));
          realCount = getRealCount(datas);
          boolean isNoMoreData = realCount < HCConsts.PAGESIZE;
          mVehicleDatas.addAll(datas);
          mPullToRefresh.setFooterStatus(isNoMoreData);
          if (isNoMoreData && !HCUtils.isListEmpty(bhcv.getRecommend())) {
            allGoodVehicleAdapter.setNeedShowGuessView(true);
            mVehicleDatas.addAll(bhcv.getRecommend());
          }
          if (!HCUtils.isListEmpty(mVehicleDatas)) {
            int size = mVehicleDatas.size();
            int bangCount = mVehicleDatas.get(size - 1).getBang_count();
            if (bangCount > 0) {
              mPullToRefresh.hideFooter();
            }
          }
          allGoodVehicleAdapter.notifyDataSetChanged();
        }
      } catch (Exception e) {
        HCLog.d(TAG, "handleVehicleSource is crash ... ");
      }
    }
    computeTotal(count);
    mPullToRefresh.finishRefresh();
    HCSensorsUtil.viewClassifyPage("全部", FilterUtils.getNormalFilterterm());
  }

  private void computeTotal(int count) {
    int PSIZE = HCConsts.PAGESIZE;
    count = count < PSIZE ? 0 : count;
    mTotalPage = (count % PSIZE == 0) ? count / PSIZE : (count / PSIZE) + 1;
    if (mTotalPage == 0 && mVehicleDatas.size() > 0) {
      mTotalPage = 1;
    }
  }

  private void seeIfNeedScrollUp(int count) {
    if (mCurrentPage == 0) {
      mVehicleDatas.clear();
      mPullToRefresh.postDelayed(new Runnable() {
        @Override public void run() {
          if (mPullToRefresh != null) {
            mPullToRefresh.tryToSmoothScrollUp();
          }
        }
      }, 10);

      if (!isSortChangedData && count > 0 && isNowListVisible2User()) {
        HCUtils.showCountToast(count);
      }
    }
  }

  private boolean seeIfResponseEmtpty(String responseJsonString) {
    boolean result = false;
    if (TextUtils.isEmpty(responseJsonString)) {
      result = true;
      mPullToRefresh.setFooterStatus(false);
      mPullToRefresh.finishRefresh();
      if (mVehicleDatas.isEmpty()) {
        mNetErrLinear.setVisibility(View.VISIBLE);
      } else {
        HCUtils.toastNetError();
      }
    }
    return result;
  }

  private int getRealCount(List<HCVehicleItemEntity> datas) {
    int real = 0;
    if (!HCUtils.isListEmpty(datas)) {
      for (HCVehicleItemEntity item : datas) {
        if (item != null) {
          if (!TextUtils.isEmpty(item.getVehicle_name())) {
            real++;
          }
        }
      }
    }
    return real;
  }

  private void setEmptyHeadView(HCVehicleItemEntity entity) {
    if (entity != null && headerView != null) {
      if (isNormalVehicleIsNull(entity)) {
        if (listView != null && listView.getHeaderViewsCount() == 0) {
          listView.addHeaderView(headerView);
        }
      }
    }
  }

  private void removeEmptyHeadView(HCVehicleItemEntity entity) {
    if (entity != null && headerView != null) {
      if (mCurrentPage == 0 && !TextUtils.isEmpty(entity.getVehicle_name())) {
        if (listView != null) {
          listView.removeHeaderView(headerView);
        }
      }
    }
  }

  private boolean isNormalVehicleIsNull(HCVehicleItemEntity entity) {
    return mCurrentPage == 0 && TextUtils.isEmpty(entity.getVehicle_name());
  }

  private void requestSearchData() {
    if (TextUtils.isEmpty(mLastSearchWord)) return;

    final Map<String, Object> params = HCParamsUtil.getSearchResult(mLastSearchWord, mCurrentPage);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleSearchData(responseJsonString);
      }
    }));
  }

  private void handleSearchData(String responseJsonString) {

    if (mPullToRefresh == null) return;
    if (seeIfResponseEmtpty(responseJsonString)) return;
    HCSearchEntity entity = HCJSONParser.parseSearchResult(responseJsonString);
    if (entity != null) {
      HCQueryEntity queryEntity = entity.getQuery();
      FilterUtils.saveQueryToFilterTerm(queryEntity);
      HCEvent.postEvent(HCEvent.ACTION_RESET_FILTERBAR_COLOR);
      requestVehicleSource();
    }
  }

  private void onFilterChanged() {
    if (mPullToRefresh == null) return;

    boolean isFilterChanged = !FilterUtils.getNormalFilterterm().equals(mLastTerm);
    if (isFilterChanged) {
      mPullToRefresh.autoRefresh();
    }

    HCEvent.postEvent(HCEvent.ACTION_RESET_FILTERBAR_COLOR);
  }

  public void onEvent(HCCommunicateEntity communicateEntity) {
    if (communicateEntity == null || mPullToRefresh == null) return;

    String action = communicateEntity.getAction();

    handleChangedEvent(communicateEntity);

    switch (action) {
      case HCEvent.ACTION_SEARCH_RETURN_TO_ALL_VEHICLE://搜索返回
        isSearchResult = true;
        isSortChangedData = false;
        handleSearchResult(communicateEntity);
        break;

      case HCEvent.ACTION_CORE_TO_CHILD_REFRESH:
        isSearchResult = false;
        handleCoreEvent(communicateEntity);
        break;

      case HCEvent.ACTION_CITYCHANGED:
        //城市变化重新请求数据
        if (isInitCompleted) {
          isSearchResult = false;
          mPullToRefresh.autoRefresh();
        }
        break;

      case HCEvent.ACTION_DUPLICATE_CLICK_TAB: {
        String tab = communicateEntity.getStrValue();
        if (FragmentController.CORE_VEHICLE_TAG.equals(tab)) {
          if (getParentFragment() instanceof CoreFragment) {
            CoreFragment coreFragmentXT = (CoreFragment) getParentFragment();
            int page = coreFragmentXT.getCurrentVisiblePage();
            if (CoreFragment.PAGE_ALL_VEHICLES == page) {
              mPullToRefresh.tryToScrollUp();
              if (!isSortViewShowing) {
                showFilterBar();
              }
            }
          }
        }
      }
      break;

      case HCEvent.ACTION_SWAPTO_CORE_INNER:
        if (CoreFragment.PAGE_ALL_VEHICLES == communicateEntity.getIntValue()) {
          if (!isInitCompleted) {
            doInitViewOrData();
          }
        }
        break;

      case HCEvent.ACTION_IS_NEED_INIT_ALLGOOD:
        doInitViewOrDataTemp();
        break;
      case HCEvent.ACTION_ALL_GOOD_FILTER_CHANGED:
        setSubTvStatus();
        break;
      case HCEvent.ACTION_IS_LIMIT_ACTIVITY:
        String hasLimitActivity = communicateEntity.getStrValue();
        if (timeHandler != null) {
          timeHandler.removeCallbacksAndMessages(null);
          if ("1".equals(hasLimitActivity)) {
            if (allGoodVehicleAdapter != null) {
              allGoodVehicleAdapter.timeCurrent = System.currentTimeMillis() / 1000;
              timeHandler.sendEmptyMessageDelayed(0, 1000);
            }
          }
        }
        break;
    }
  }

  private void handleCoreEvent(HCCommunicateEntity entity) {
    if (entity.getIntValue() == CoreFragment.PAGE_ALL_VEHICLES) {
      //刷新界面
      isSortChangedData = false;
      onFilterChanged();
    }
  }

  private void handleChangedEvent(HCCommunicateEntity communicateEntity) {

    String action = communicateEntity.getAction();

    if (action.equals(mHost + HCEvent.ACTION_FILTER_CHANGED)) {
      isSortChangedData = false;
      onFilterChanged();
      abordEvent(communicateEntity);
    }
    if (action.equals(mHost + HCEvent.ACTION_SORT_CHOOSED)) {
      //排序引起的变化
      isSortChangedData = true;
      onFilterChanged();
      abordEvent(communicateEntity);
    }
  }

  private void handleSearchResult(HCCommunicateEntity communicateEntity) {

    if (mPullToRefresh == null) return;
    mLastSearchWord = communicateEntity.getStrValue();
    mPullToRefresh.autoRefresh();
    HCEvent.postEvent(HCEvent.ACTION_RESET_FILTERBAR_COLOR);
  }

  private void doSubscribeCheck() {
    //获取当前筛选对应的订阅条件
    SubConditionDataEntity sub = FilterUtils.getSubscribeByFilterTerm();
    List<SubConditionDataEntity> allSub = HCSpUtils.getAllSubscribe();
    int size = allSub.size();
    if (size >= HCConsts.SUBSCRIBE_LIMIT) {
      //订阅条件
      showToast(R.string.hc_sub_till_limit);
    } else {
      if (allSub.contains(sub)) {
        showToast(R.string.hc_sub_duplicate);
        setSubTvStatus();
      } else {
        requestSubscribe(sub);
      }
    }
  }

  private void setSubTvStatus() {
    if (mSubVehicleTv == null) return;
    //根据当前条件控制订阅按钮可否点击
    boolean isSubed = FilterUtils.isCurrentSubscribed();
    if (isSubed) {
      mSubVehicleTv.setText(R.string.hc_has_subbed);
      mSubVehicleTv.setClickable(false);
      mSubVehicleTv.setBackgroundColor(getResColor(R.color.diff_deep_gray_));
    } else {
      mSubVehicleTv.setClickable(true);
      mSubVehicleTv.setText(R.string.filter_subscribe);
      mSubVehicleTv.setBackgroundResource(R.drawable.bg_filter_condition);
    }
  }

  /**
   * 订阅车辆
   */
  private void requestSubscribe(final SubConditionDataEntity condition) {
    if (!HCUtils.isNetAvailable()) {
      showToast(R.string.hc_net_unreachable);
      return;
    }

    Map<String, Object> params = HCParamsUtil.subscribeVehicleByCondition(condition);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleSubscribe(responseJsonString, condition);
      }
    }));

    DialogUtils.showProgress(getActivity());
  }

  private void handleSubscribe(String responseJsonString, SubConditionDataEntity condition) {
    DialogUtils.dismissProgress();
    if (!TextUtils.isEmpty(responseJsonString)) {
      HCBSubscribeIdEntity rSub = HCJSONParser.paseSubcribeId(responseJsonString);
      HCSubcribeEntity entity = rSub.getData();
      int errno = rSub.getErrno();
      if (errno == 0 && entity != null) {
        String sub_id = entity.getSub_id();
        if (!TextUtils.isEmpty(sub_id)) {
          condition.setId(sub_id);
          condition.setCity_id(String.valueOf(HCDbUtil.getSavedCityId()));
          HCSpUtils.saveSubscribe(condition);
          showHcToast();
          HCEvent.postEvent(HCEvent.ACTION_SUBSCRIBE_CHANGED);
          setSubTvStatus();
        }
      } else if (errno == 1802) {
        showToast(R.string.hc_sub_duplicate);
        setSubTvStatus();
      }
    }
  }

  private void showHcToast() {
    Toast toast = new Toast(GlobalData.mContext);
    toast.setGravity(Gravity.CENTER, 0, 0);

    int res = R.layout.layout_for_sub_toast;
    View toastView = LayoutInflater.from(getActivity()).inflate(res, null);

    toast.setDuration(Toast.LENGTH_SHORT);
    toast.setView(toastView);
    toast.show();
  }

  private void doLogin() {
    startDestAct(LoginActivity.class);
  }

  /**
   * 把筛选条件展示出来
   */
  private void showConditionView() {

    //  这里怎么可能有为null的情况
    if (mConditionParent == null) return;

    if (FilterUtils.isCurrentDefaultCondition()) {
      //默认条件  不显示
      setConditionViewGone();
    } else {
      mLinearCondition.removeAllViews();

      TreeMap<Integer, String> map = FilterUtils.getConditions();
      for (Map.Entry<Integer, String> entry : map.entrySet()) {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity())
            .inflate(R.layout.filter_choosed_item, mLinearCondition, false);
        TextView textView = (TextView) (layout.findViewById(R.id.tv_filter_choosed_item));
        textView.setText(entry.getValue());
        textView.setTag(R.id.filter_choose_condition, entry.getKey());
        textView.setOnClickListener(mDeleteConditionListener);
        mLinearCondition.addView(layout);
      }
      if (mLinearCondition.getChildCount() != 0) {
        if (mConditionParent.getVisibility() != View.VISIBLE) {
          setConditionViewVisible();
        }
        setSubTvStatus();
      } else {
        setConditionViewGone();
      }
    }
  }

  private void setConditionViewVisible() {
    if (mConditionParent == null || mPullToRefresh == null) return;
    mConditionParent.setVisibility(View.VISIBLE);
    ViewGroup.MarginLayoutParams lp =
        (ViewGroup.MarginLayoutParams) mPullToRefresh.getLayoutParams();
    lp.topMargin = mConditionParentHeight;
    lp.bottomMargin = -mConditionParentHeight;
    isConditionShowing = true;
  }

  private void setConditionViewGone() {
    if (mConditionParent == null || mPullToRefresh == null) return;
    mConditionParent.setVisibility(View.GONE);
    ViewGroup.MarginLayoutParams lp =
        (ViewGroup.MarginLayoutParams) mPullToRefresh.getLayoutParams();
    lp.topMargin = 0;
    lp.bottomMargin = 0;
    isConditionShowing = false;
  }

  private View.OnClickListener mDeleteConditionListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      handleDeleteCondition(v);
    }
  };

  private void handleDeleteCondition(View v) {

    FilterTerm term = FilterUtils.getNormalFilterterm();

    int curViewTag = (int) v.getTag(R.id.filter_choose_condition);

    switch (curViewTag) {
      case HCConsts.FILTER_BRAND:
        term.setBrand_id(0);
        term.setClass_id(0);
        break;
      case HCConsts.FILTER_SERIES:
        term.setClass_id(0);
        break;
      case HCConsts.FILTER_PRICE:
        term.setHighPrice(0);
        term.setLowPrice(0);
        HCEvent.postEvent(mHost + HCEvent.ACTION_RESET_PRICE_BAR);
        break;
      case HCConsts.FILTER_CAR_AGE:
        term.setFrom_year(0);
        term.setTo_year(0);
        break;
      case HCConsts.FILTER_DISTANCE:
        term.setFrom_miles(0);
        term.setTo_miles(0);
        break;
      case HCConsts.FILTER_SPEED_BOX:
        term.setGearboxType(0);
        break;
      case HCConsts.FILTER_STANDARD:
        term.setStandard(0);
        break;
      case HCConsts.FILTER_CAR_TYPE:
        term.setStructure(0);
        break;
      case HCConsts.FILTER_EMISSION:
        term.setFrom_emission(0);
        term.setTo_emission(0);
        break;
      case HCConsts.FILTER_COUNTRY:
        term.setCounty(0);
        break;
      case HCConsts.FILTER_COLOR:
        term.setColor(0);
        break;
    }

    isSearchResult = false;

    FilterUtils.setFilterTerm(FilterUtils.allHost, term);

    //通知筛选条件变化，刷新数据
    HCEvent.postEvent(mHost + HCEvent.ACTION_FILTER_CHANGED);

    //caution: 这里要直接调用
    mPullToRefresh.autoRefresh();

    //通知更多选择变化
    HCEvent.postEvent(mHost + HCEvent.ACTION_MORE_CHOOSED_CHANGE);
    HCEvent.postEvent(mHost + HCEvent.ACTION_PRICE_CHOOSED_CHANGE);
    HCEvent.postEvent(mHost + HCEvent.ACTION_BRAND_CHOOSED_CHANGE);
  }

  /**
   * 订阅按钮
   */
  @SuppressWarnings("unused") @OnClick(R.id.tv_filter_subscribe) public void onSubscribeClick(
      View v) {
    HCSensorsUtil.vehiclesSubscribe(FilterUtils.getNormalFilterterm());
    if (!HCUtils.isUserLogined()) {
      doLogin();
    } else {
      doSubscribeCheck();
    }
  }

  /**
   * 没有数据时的刷新按钮
   */
  @OnClick(R.id.linear_net_refresh) @SuppressWarnings("unused") public void onSeeAllClick(View v) {
    if (mPullToRefresh == null) return;
    if (!HCUtils.isNetAvailable()) {
      mNetErrLinear.setVisibility(View.VISIBLE);
      HCUtils.toastNetError();
    } else {
      isSearchResult = false;
      mPullToRefresh.setVisibility(View.VISIBLE);
      mLastSearchWord = "";
      mPullToRefresh.autoRefresh();
    }
  }

  private boolean isNowListVisible2User() {
    CoreFragment coreFrag = (CoreFragment) getParentFragment();
    if (coreFrag == null) return false;
    int page = coreFrag.getCurrentVisiblePage();
    return coreFrag.isVisible() && (page == CoreFragment.PAGE_ALL_VEHICLES);
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
