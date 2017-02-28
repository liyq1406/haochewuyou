package com.haoche51.buyerapp.fragment;

import android.graphics.Rect;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.WebBrowserActivity;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.HCHomeLiveEntity;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.custom.HCViewPager;
import com.haoche51.custom.github.FragmentPagerItem;
import com.haoche51.custom.github.FragmentPagerItemAdapter;
import com.haoche51.custom.github.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.umeng.analytics.MobclickAgent;

/**
 * 新的主界面 在这里管理ViewPager
 */
public class CoreFragment extends HCBaseFragment {

  /** 全部好车 */
  public static final int PAGE_ALL_VEHICLES = 0;
  /** 直营店 */
  public static int PAGE_DIRECT = -1;
  /** 今日新上 */
  public static int PAGE_TODAY_VEHICLES = 1;

  /** 顶部相关的变量 */
  private SparseArray<String> mapHosts = new SparseArray<>();
  private FragmentPagerItems mPageItems;
  private FragmentPagerItemAdapter mAdapter;
  private FragmentPagerItem itemDirectVehicle;
  private Class clsDirectVehicle;

  @InjectView(R.id.viewpager_core) HCViewPager mViewPager;
  @InjectView(R.id.smart_vp_tab) SmartTabLayout mSmartTabLayout;
  @InjectView(R.id.tv_core_hot_icon) TextView mHotIcon;
  @InjectView(R.id.iv_core_live) ImageView mLiveIv;

  @Override boolean isNeedBindEventBus() {
    this.setPriority(HCEvent.PRIORITY_CORE);
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_core;
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (!hidden && mViewPager != null) {
      if (mViewPager.getCurrentItem() == PAGE_ALL_VEHICLES) {
        //统计 全部好车
        HCStatistic.vehicleListShowing();
      }
    }
  }

  @Override void doInitViewOrData() {
    setUpPageItems();
    setPageChangeListener();
    if ("1".equals(HCSpUtils.getHasDirect())) {
      addDirectPage();
    }
    setLive(HCSpUtils.getZhiBoEntity());
  }

  @Override public void onResume() {

    super.onResume();

    MobclickAgent.onResume(getActivity());
  }

  @SuppressWarnings("unchecked") private void setUpPageItems() {
    mPageItems = new FragmentPagerItems(getActivity());

    String strAllVehicle = getString(R.string.hc_core_title_all);
    Class clsAllVehicle = AllGoodVehiclesFragment.class;
    FragmentPagerItem itemBuyVehicle = FragmentPagerItem.of(strAllVehicle, clsAllVehicle);

    String strTodayVehicle = getString(R.string.hc_core_title_today);
    Class clsTodayVehicle = TodayNewArrivalFragment.class;
    FragmentPagerItem itemTodayVehicle = FragmentPagerItem.of(strTodayVehicle, clsTodayVehicle);

    String strDirectVehicle = getString(R.string.hc_core_title_direct);
    clsDirectVehicle = DirectFragment.class;
    itemDirectVehicle = FragmentPagerItem.of(strDirectVehicle, clsDirectVehicle);

    mPageItems.add(itemBuyVehicle);
    mPageItems.add(itemTodayVehicle);

    mapHosts.put(PAGE_ALL_VEHICLES, clsAllVehicle.getSimpleName());
    mapHosts.put(PAGE_TODAY_VEHICLES, clsTodayVehicle.getSimpleName());

    FragmentManager mManager = getChildFragmentManager();
    mAdapter = new FragmentPagerItemAdapter(mManager, mPageItems);
    mViewPager.setAdapter(mAdapter);
    mViewPager.setOffscreenPageLimit(mPageItems.size());
    mSmartTabLayout.setViewPager(mViewPager);
    resizeTabWidth(mPageItems.size());

    //发送通知 mainActivity关闭loading
    HCEvent.postEvent(HCEvent.ACTION_NOW_LOADED_HOME_PAGE);
  }

  private void addDirectPage() {
    if (mPageItems.size() == 2) {
      mPageItems.add(1, itemDirectVehicle);
      mapHosts.put(PAGE_DIRECT, clsDirectVehicle.getSimpleName());
      mAdapter.notifyDataSetChanged();
      mViewPager.setOffscreenPageLimit(mPageItems.size());
      mSmartTabLayout.setViewPager(mViewPager);
      AllGoodVehiclesFragment.isInitCompleted = false;
      DirectFragment.isInitCompleted = false;
      TodayNewArrivalFragment.isInitCompleted = false;
      PAGE_DIRECT = 1;
      PAGE_TODAY_VEHICLES = 2;
      mViewPager.setCurrentItem(0);
      HCEvent.postEvent(HCEvent.ACTION_IS_NEED_INIT_ALLGOOD);
      resizeTabWidth(mPageItems.size());
      createReminderLayout();
    }
  }

  private void removeDirectPage() {
    if (mPageItems.size() == 3) {
      mPageItems.remove(itemDirectVehicle);
      mapHosts.remove(PAGE_DIRECT);
      mAdapter.notifyDataSetChanged();
      mViewPager.setOffscreenPageLimit(mPageItems.size());
      mSmartTabLayout.setViewPager(mViewPager);
      AllGoodVehiclesFragment.isInitCompleted = false;
      TodayNewArrivalFragment.isInitCompleted = false;
      PAGE_DIRECT = -1;
      PAGE_TODAY_VEHICLES = 1;
      mViewPager.setCurrentItem(0);
      HCEvent.postEvent(HCEvent.ACTION_IS_NEED_INIT_ALLGOOD);
      resizeTabWidth(mPageItems.size());
      removeReminderLayout();
    }
  }

  private void resizeTabWidth(final int counts) {

    int SW = HCUtils.getScreenWidthInPixels();
    int allW = SW - 2 * HCUtils.getDimenPixels(R.dimen.px_80dp);
    for (int i = 0; i < counts; i++) {
      View v = mSmartTabLayout.getTabAt(i);
      if (v != null) {
        v.getLayoutParams().width = allW / counts;
      }
    }
  }

  private void setPageChangeListener() {

    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override public void onPageSelected(int position) {
        if (position == PAGE_ALL_VEHICLES) {
          //统计 全部好车
          HCStatistic.vehicleListShowing();
        }

        //通知当前是哪个页面
        HCEvent.postEvent(HCEvent.ACTION_SWAPTO_CORE_INNER, position);

        String vehicleChannel = mPageItems.get(position).getTitle().toString();
        HCSensorsUtil.buyVehiclePage(vehicleChannel);
      }

      @Override public void onPageScrollStateChanged(int state) {
      }
    });
  }

  @Override public void onEvent(HCCommunicateEntity communicateEntity) {
    //在这里接收城市变化,并回显到cityTv中
    if (communicateEntity != null) {
      String action = communicateEntity.getAction();
      switch (action) {

        case HCEvent.ACTION_MAINACT_TO_CORE://收到从MainActivity发出的信息
          handleNavigateEvent(communicateEntity);
          break;

        case HCEvent.ACTION_MAINACT_SEARCH_TO_CORE://搜索返回进入core
          mViewPager.setCurrentItem(PAGE_ALL_VEHICLES);
          abordEvent(communicateEntity);
          String keyword = communicateEntity.getStrValue();
          if (!TextUtils.isEmpty(keyword)) {
            HCEvent.postEvent(HCEvent.ACTION_SEARCH_RETURN_TO_ALL_VEHICLE, keyword);
          }
          break;

        case HCEvent.ACTION_IS_NEED_DORECT://控制直营店是否显示
          String hasDirect = communicateEntity.getStrValue();
          if ("1".equals(hasDirect)) {
            addDirectPage();
          } else {
            removeDirectPage();
          }
          break;
        case HCEvent.ACTION_IS_HAS_LIVE://控制直播
          Object o = communicateEntity.getObjValue();
          mLiveIv.setVisibility(View.GONE);
          HCHomeLiveEntity liveEntity = o == null ? null : (HCHomeLiveEntity) o;
          setLive(liveEntity);
          break;
      }
    }
  }

  private void setLive(HCHomeLiveEntity liveEntity) {
    if (liveEntity != null) {
      final String liveUrl = liveEntity.getLink_url();
      String livePic = liveEntity.getPic_url();
      if (!TextUtils.isEmpty(liveUrl) && !TextUtils.isEmpty(livePic)) {
        ImageLoaderHelper.displayNormalImage(livePic, mLiveIv);
        mLiveIv.setVisibility(View.VISIBLE);
        mLiveIv.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            WebBrowserActivity.urlToThis(getActivity(), liveUrl);
          }
        });
      }
    }
  }

  private void handleNavigateEvent(HCCommunicateEntity communicateEntity) {
    if (mViewPager == null) return;
    int page = communicateEntity.getIntValue();
    mViewPager.setCurrentItem(page);
    abordEvent(communicateEntity);
    HCEvent.postEvent(HCEvent.ACTION_CORE_TO_CHILD_REFRESH, page);
  }

  public int getCurrentVisiblePage() {
    return mViewPager.getCurrentItem();
  }

  private void createReminderLayout() {
    mSmartTabLayout.post(new Runnable() {
      @Override public void run() {
        View subView = mSmartTabLayout.getTabAt(PAGE_ALL_VEHICLES);
        if (subView instanceof TextView) {
          Rect textRect = HCUtils.getTextRect(getString(R.string.hc_core_title_direct),
              ((TextView) subView).getTextSize());
          int x = (HCUtils.getScreenWidthInPixels() + textRect.width()) / 2;
          int y = (HCUtils.getDimenPixels(R.dimen.px_32dp) - textRect.height()) / 2;

          FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mHotIcon.getLayoutParams();
          lp.leftMargin = x;
          lp.topMargin = y;
          mHotIcon.setLayoutParams(lp);
          mHotIcon.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  private void removeReminderLayout() {
    mHotIcon.setVisibility(View.GONE);
  }

  /**
   * 点击顶部搜索按钮
   */
  @SuppressWarnings("unused") @OnClick(R.id.tv_core_search) public void onSearchClick(
      TextView searchTv) {
    HCEvent.postEvent(HCEvent.ACTION_GO_SEARCH, "BuyVehiclePage");
  }

  public void onPause() {
    super.onPause();
    MobclickAgent.onPause(getActivity());
  }
}
