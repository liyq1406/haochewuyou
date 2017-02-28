package com.haoche51.buyerapp.fragment;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.ManageMySubActivity;
import com.haoche51.buyerapp.activity.VehicleDetailActivity;
import com.haoche51.buyerapp.adapter.SinglePicVehicleAdapter;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.HCSubListEntity;
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
import com.haoche51.buyerapp.util.HCFormatUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCPullToRefresh;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MySubscribeVehiclesFragment extends HCBaseFragment {

  private boolean isInitCompleted = false;

  @InjectView(R.id.linear_net_refresh) LinearLayout mNetErrLinear;

  @InjectView(R.id.rel_subtop_parent) RelativeLayout mSubtopParentRel;
  @InjectView(R.id.iv_subtop_brand) ImageView mSubtopBrandIv;
  @InjectView(R.id.tv_subtop_brand_desc) TextView mSubtopBrandDescTv;
  @InjectView(R.id.tv_subtop_condition_desc) TextView mSubtopConditionDescTv;

  @InjectView(R.id.rel_subcounts_parent) RelativeLayout mSubCountsParentRel;
  @InjectView(R.id.iv_subcounts_brand) ImageView mSubCountsBrandIv;
  @InjectView(R.id.tv_subcounts_counts) TextView mSubCountsCountsTv;

  @InjectView(R.id.hcptr_sub_vehicle) HCPullToRefresh mPullToRefresh;

  private SinglePicVehicleAdapter mAdapter;
  private List<HCVehicleItemEntity> mVehicleDatas = new ArrayList<>();
  private SubConditionDataEntity mLastRequestCondition = null;
  private int mCurrentPage = 0;
  private int mTotalPage = 0;

  @InjectView(R.id.linear_sub_for_empty) View mEmptyView;
  @InjectView(R.id.frame_empty_sub_no_car) View mEmptyViewNoCar;

  @Override void doInitViewOrData() {

    initPullToRefresh();

    initRefreshListener();

    initScrollListener();

    setUpTopByCondition(mLastRequestCondition);

    requestSubscribeData(mLastRequestCondition);

    isInitCompleted = true;
  }

  private void initPullToRefresh() {
    int resLayout = R.layout.lvitem_singlepic_vehicle;
    mAdapter = new SinglePicVehicleAdapter(getActivity(), mVehicleDatas, resLayout);
    mPullToRefresh.setEmptyView(mEmptyViewNoCar);
    mPullToRefresh.setAdapter(mAdapter);
    mAdapter.notifyDataSetChanged();
    mPullToRefresh.setNoDefaultDivider();
  }

  private void initRefreshListener() {

    mPullToRefresh.setOnRefreshCallback(new HCPullToRefresh.OnRefreshCallback() {
      @Override public void onPullDownRefresh() {
        mCurrentPage = 0;
        requestSubscribeData(mLastRequestCondition);
        HCSpUtils.setLastEnterSubscribeListTimeToNow();
      }

      @Override public void onLoadMoreRefresh() {
        mCurrentPage++;
        requestSubscribeData(mLastRequestCondition);
      }
    });

    mPullToRefresh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= mVehicleDatas.size()) return;
        HCVehicleItemEntity entity = mVehicleDatas.get(position);
        int vehicleSourceId = HCUtils.str2Int(entity.getId());
        if (vehicleSourceId > 0) {
          VehicleDetailActivity.idToThis(getActivity(), String.valueOf(vehicleSourceId), "订阅");
        }
      }
    });
  }

  private void computeTotal(int count) {
    int PSIZE = HCConsts.PAGESIZE;
    count = count < PSIZE ? 0 : count;
    mTotalPage = (count % PSIZE == 0) ? count / PSIZE : (count / PSIZE) + 1;
    if (mTotalPage == 0 && mVehicleDatas.size() > 0) {
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

  private void setUpTopByCondition(SubConditionDataEntity entity) {
    if (mSubtopParentRel == null) return;
    if (entity == null) {
      //代表 全部订阅条件
      mSubtopParentRel.setVisibility(View.GONE);
      mSubCountsParentRel.setVisibility(View.VISIBLE);
      //显示订阅条件个数icon
      int size = HCSpUtils.getAllSubscribe().size();
      setCounsIcon(size);
    } else {
      mSubtopParentRel.setVisibility(View.VISIBLE);
      mSubCountsParentRel.setVisibility(View.GONE);
      //显示单独的订阅条件
      setConditionDesc(entity);
    }
  }

  private void setCounsIcon(int size) {
    if (mSubCountsBrandIv == null) return;
    if (size < 1 || size > 5) {
      mSubCountsParentRel.setVisibility(View.GONE);
      mEmptyView.setVisibility(View.VISIBLE);
      return;
    }
    Drawable logo;
    logo = FragmentController.getCountIcon(size);
    if (logo != null) {
      logo.setBounds(0, 0, logo.getIntrinsicWidth(), logo.getIntrinsicHeight());
    }
    mSubCountsBrandIv.setImageDrawable(logo);
    mSubCountsCountsTv.setText(getString(R.string.hc_sub_counts, size));
  }

  private void setConditionDesc(SubConditionDataEntity entity) {

    if (mSubtopBrandDescTv == null || entity == null) return;

    int brand_id = HCUtils.str2Int(entity.getBrand_id());
    String brandStr = HCDbUtil.getBrandNameById(brand_id);
    mSubtopBrandDescTv.setText(brandStr);

    HCViewUtils.setIconById(mSubtopBrandIv, brand_id, R.drawable.icon_brand_unlimit);

    String detailStr = HCFormatUtil.formatSubDetail(entity);
    mSubtopConditionDescTv.setText(detailStr);
  }

  private void requestSubscribeData(final SubConditionDataEntity entity) {

    if (!HCUtils.isUserLogined()) return;

    String sub_id = entity == null ? "0" : entity.getId();

    Map<String, Object> param = HCParamsUtil.getSpecifySubscribeData(sub_id, mCurrentPage);
    API.post(new HCRequest(param, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleSubscribeData(responseJsonString);
      }
    }));
  }

  private void handleSubscribeData(String resp) {
    if (mPullToRefresh == null) return;

    if (TextUtils.isEmpty(resp)) {

      if (mVehicleDatas.isEmpty()) {
        if (mNetErrLinear != null) {
          mNetErrLinear.postDelayed(new Runnable() {
            @Override public void run() {
              mNetErrLinear.setVisibility(View.VISIBLE);
            }
          }, 300);
        }
      } else {
        HCUtils.toastNetError();
      }

      mPullToRefresh.setFooterStatus(false);
      mPullToRefresh.finishRefresh();
      return;
    }

    HCUtils.hideViewIfNeed(mNetErrLinear);

    if (!TextUtils.isEmpty(resp)) {
      HCSubListEntity entity = HCJSONParser.parseSubscribeVehicleData(resp);
      int count = entity.getCount();
      if (count != 0) {
        if (mCurrentPage == 0) {
          mVehicleDatas.clear();
          mPullToRefresh.tryToSmoothScrollUp();
        }
        List<HCVehicleItemEntity> lists = entity.getVehicles();
        if (!HCUtils.isListEmpty(lists)) {
          mVehicleDatas.addAll(lists);
          mPullToRefresh.setFooterStatus(lists);
          mAdapter.notifyDataSetChanged();
          mPullToRefresh.finishRefresh();
        } else {
          mPullToRefresh.setFooterStatus(true);
        }
      } else {
        mVehicleDatas.clear();
        mAdapter.notifyDataSetChanged();
        mPullToRefresh.finishRefresh();
      }
      computeTotal(count);
    }
  }

  @Override public void onEvent(HCCommunicateEntity entity) {
    if (mPullToRefresh == null || entity == null) return;
    String action = entity.getAction();
    switch (action) {
      case HCEvent.ACTION_MY_SUBITEM_CLICK:
        //dialog中点击选择 条件返回
        Object obj = entity.getObjValue();
        mLastRequestCondition = obj == null ? null : (SubConditionDataEntity) obj;
        mPullToRefresh.autoRefresh();
        setUpTopByCondition(mLastRequestCondition);
        break;

      case HCEvent.ACTION_GOTO_MANAGER_CLICK:
        //点击管理我的订阅
        startDestAct(ManageMySubActivity.class);
        break;

      case HCEvent.ACTION_LOGINSTATUS_CHANGED:
        if (isInitCompleted) {
          if (HCUtils.isUserLogined()) {
            setUpTopByCondition(mLastRequestCondition);
            requestSubscribeData(mLastRequestCondition);
          }
        }
        break;
      case HCEvent.ACTION_SUBSCRIBE_CHANGED:

        if (isInitCompleted) {
          setUpTopByCondition(null);
          mPullToRefresh.autoRefresh();
          mLastRequestCondition = null;
        }
        break;
    }
  }

  @SuppressWarnings("unused") @OnClick({ R.id.rel_subtop_parent, R.id.rel_subcounts_parent })
  public void onTopRelClick(View v) {
    DialogUtils.showSubscribeConditionDialog(getActivity(), mLastRequestCondition);
  }

  /** 立即找车 */
  @SuppressWarnings("unused") @OnClick(R.id.btn_sub_find_car) public void onLoginClick(View v) {
    //跳转到全部好车
    FilterUtils.resetNormalToDefaultExceptCity();
    HCEvent.postEvent(HCEvent.ACTION_HOME_TO_MAINACT_GOOD_VEHICLES);
    getActivity().finish();
  }

  @SuppressWarnings("unused") @OnClick(R.id.linear_net_refresh)
  public void onNetRefreshClick(View view) {
    if (mPullToRefresh == null) return;
    if (!HCUtils.isNetAvailable()) {
      mNetErrLinear.setVisibility(View.VISIBLE);
      HCUtils.toastNetError();
    } else {
      setUpTopByCondition(null);
      mPullToRefresh.autoRefresh();
      mLastRequestCondition = null;
    }
  }

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_subscribe_vehicles;
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
