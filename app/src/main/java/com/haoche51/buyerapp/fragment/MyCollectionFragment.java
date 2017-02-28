package com.haoche51.buyerapp.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import butterknife.InjectView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.CollectionAdapter;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.HCVehicleItemEntity;
import com.haoche51.buyerapp.entity.HCVehicleListDataEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCPullToRefresh;
import com.haoche51.custom.HCViewClickListener;
import com.haoche51.custom.swipe.SwipeLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 我的收藏列表fragment
 */
public class MyCollectionFragment extends HCBaseFragment {

  @InjectView(R.id.view_loading) View mPreLoadingView;
  @InjectView(R.id.linear_net_refresh) LinearLayout mNetErrLinear;

  @InjectView(R.id.hcptr_collect_list) HCPullToRefresh mPullToRefresh;
  @InjectView(R.id.linear_coll_for_empty) View mEmptyView;

  private CollectionAdapter mAdapter;
  private List<HCVehicleItemEntity> mDatas = new ArrayList<>();
  private int mCurrentPage = 0;
  private int mTotalPage = 0;

  private boolean isInitCompleted = false;

  private HCViewClickListener mClickListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {
      switch (v.getId()) {
        case R.id.btn_coll_find_car:
          HCEvent.postEvent(HCEvent.ACTION_HOME_TO_MAINACT_GOOD_VEHICLES);
          getActivity().finish();
          break;
        case R.id.linear_net_refresh://刷新数据
          if (!HCUtils.isNetAvailable()) {
            mNetErrLinear.setVisibility(View.VISIBLE);
            HCUtils.toastNetError();
          } else {
            HCUtils.hideViewIfNeed(mNetErrLinear);
            mPreLoadingView.setVisibility(View.VISIBLE);
            requestCollectionList();
          }
          break;
      }
    }
  };

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_collection;
  }

  @Override void doInitViewOrData() {

    initPullToRefresh();

    initRefreshListener();

    initClickListener();

    initScrollListener();

    isInitCompleted = true;
  }

  private void initPullToRefresh() {
    int itemRes = R.layout.lvitem_collection_vehicle;
    mAdapter = new CollectionAdapter(getActivity(), mDatas, itemRes);
    mPullToRefresh.setAdapter(mAdapter);
    mAdapter.notifyDataSetChanged();
    mPullToRefresh.setNoDefaultDivider();
    mPullToRefresh.setNeedDetectXY(true);
    requestCollectionList();
  }

  private void initRefreshListener() {

    mPullToRefresh.setOnRefreshCallback(new HCPullToRefresh.OnRefreshCallback() {
      @Override public void onPullDownRefresh() {
        mCurrentPage = 0;
        requestCollectionList();
      }

      @Override public void onLoadMoreRefresh() {
        mCurrentPage++;
        requestCollectionList();
      }
    });

    mAdapter.setOnDeleteListener(new CollectionAdapter.OnDeleteListener() {
      @Override public void onDelete(HCVehicleItemEntity entity, SwipeLayout swipeLayout) {
        swipeLayout.close(true);
        if (!HCUtils.isNetAvailable()) {
          HCUtils.showToast(R.string.hc_net_unreachable);
          return;
        }
        requestDelete(entity);
      }
    });
  }

  private void computeTotal(int count) {
    int PSIZE = HCConsts.PAGESIZE;
    count = count < PSIZE ? 0 : count;
    mTotalPage = (count % PSIZE == 0) ? count / PSIZE : (count / PSIZE) + 1;
    if (mTotalPage == 0 && mDatas.size() > 0) {
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
            ListView pullListView = mPullToRefresh.getListView();
            if (pullListView != null) {
              ps = pullListView.getLastVisiblePosition();
            }
            if (ps > 0) {
              nowCurP = (ps / HCConsts.PAGESIZE) + 1;
            }
            HCUtils.showPageToast(nowCurP, mTotalPage);
          }
        }
      }

      @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
      }
    });
  }

  private void initClickListener() {
    getFragmentContentView().findViewById(R.id.linear_net_refresh)
        .setOnClickListener(mClickListener);
    getFragmentContentView().findViewById(R.id.btn_coll_find_car)
        .setOnClickListener(mClickListener);
  }

  public void requestDelete(final HCVehicleItemEntity entity) {
    DialogUtils.showProgress(getActivity());
    int id = HCUtils.str2Int(entity.getId());
    Map<String, Object> params = HCParamsUtil.cancelCollectVehicle(id);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleDelete(responseJsonString, entity);
      }
    }));
  }

  private void handleDelete(String responseJsonString, HCVehicleItemEntity entity) {

    DialogUtils.dismissProgress();

    if (seeIfResponseEmpty(responseJsonString)) return;

    HCUtils.hideViewIfNeed(mNetErrLinear);

    if (!TextUtils.isEmpty(responseJsonString)) {
      boolean isSucc = HCJSONParser.parseCollectResult(responseJsonString);
      if (isSucc) {
        mDatas.remove(entity);
        mAdapter.notifyDataSetChanged();
        if (mDatas.size() == 0) {
          mPullToRefresh.autoRefresh();
        }
      }
    }
  }

  private void requestCollectionList() {
    Map<String, Object> params = HCParamsUtil.getCollectionList(mCurrentPage);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleCollectionList(responseJsonString);
      }
    }));
  }

  private void handleCollectionList(String resp) {
    if (mPullToRefresh == null) return;

    if (seeIfResponseEmpty(resp)) return;

    HCUtils.hideViewIfNeed(mNetErrLinear);
    HCUtils.hideViewIfNeed(mPreLoadingView);

    HCVehicleListDataEntity entity = HCJSONParser.parseCollectOrTodayList(resp);
    int count = entity.getCount();
    if (0 != count) {
      if (mCurrentPage == 0) {
        mDatas.clear();
      }
      List<HCVehicleItemEntity> list = entity.getVehicles();
      mDatas.addAll(list);
      mPullToRefresh.setFooterStatus(list);
    } else {
      mDatas.clear();
    }
    mAdapter.notifyDataSetChanged();
    mPullToRefresh.finishRefresh();
    seeIfNeedEmptyView(mDatas);
    computeTotal(count);
  }

  private boolean seeIfResponseEmpty(String resp) {
    boolean result = false;
    if (TextUtils.isEmpty(resp)) {
      result = true;
      mPullToRefresh.setFooterStatus(false);
      mPullToRefresh.finishRefresh();
      if (mDatas.isEmpty()) {
        HCUtils.hideViewIfNeed(mPreLoadingView);
        HCUtils.hideViewIfNeed(mEmptyView);
        mNetErrLinear.setVisibility(View.VISIBLE);
      } else {
        HCUtils.toastNetError();
      }
    }

    return result;
  }

  private void seeIfNeedEmptyView(List<HCVehicleItemEntity> mDatas) {
    if (HCUtils.isListEmpty(mDatas)) {
      mEmptyView.setVisibility(View.VISIBLE);
    } else {
      HCUtils.hideViewIfNeed(mEmptyView);
    }
  }

  @Override public void onEvent(HCCommunicateEntity entity) {

    String action = entity.getAction();
    switch (action) {
      case HCEvent.ACTION_COLLECTION_CHANGED:
        if (isInitCompleted) {
          mPullToRefresh.autoRefresh();
        }
        break;
    }
  }

  @Override public void onResume() {
    super.onResume();
    ThirdPartInjector.onPageStart(this.getClass().getSimpleName());
  }

  @Override public void onPause() {
    super.onPause();
    ThirdPartInjector.onPageEnd(this.getClass().getSimpleName());
  }
}
