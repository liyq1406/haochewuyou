package com.haoche51.buyerapp.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.SellVehicleActivity;
import com.haoche51.buyerapp.activity.WebBrowserActivity;
import com.haoche51.buyerapp.adapter.HomeBrandGridViewAdapter;
import com.haoche51.buyerapp.adapter.HomeForumAdapter;
import com.haoche51.buyerapp.entity.HCBannerEntity;
import com.haoche51.buyerapp.entity.HCCityEntity;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.HCHomeCityDataEntity;
import com.haoche51.buyerapp.entity.HCHomeDialogDataEntity;
import com.haoche51.buyerapp.entity.HCHomeLiveEntity;
import com.haoche51.buyerapp.entity.HCPromoteEntity;
import com.haoche51.buyerapp.entity.HomeBrandEntity;
import com.haoche51.buyerapp.entity.HomeForumEntity;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.BannerController;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.FragmentController;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCPullToRefresh;
import com.haoche51.custom.ImageCycleView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 显示首页的Fragment
 */
public class HomePageFragment extends HCBaseFragment
    implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

  private final String allHost = AllGoodVehiclesFragment.class.getSimpleName();
  /**
   * 标识只有第一次onResume才显示PromoteDialg
   */
  private boolean isFirstLoadPromote = true;

  private HCPullToRefresh mPullToRefresh;

  private View mHeaderView;

  /** 控制顶部搜索布局 */
  private FrameLayout mTopSearchParent;
  private TextView mTopCityTv;
  private int mTopSearchParentHeight = -HCUtils.getDimenPixels(R.dimen.px_48dp);
  private boolean isTopSearchParentShowing = false;

  /** 顶部图片和中间搜索布局 */
  private TextView mEnterCityTv;
  private TextView mEnterCountTv;
  private TextView mEnterHintTv;
  /** 直播 */
  private ImageView mLiveIv;

  /** 直营店滚动图 */
  private ImageCycleView mDirectView;
  /** 快速选车模块 */
  private GridView mChooseBrandGv;
  /** 今日新上模块 */
  private RelativeLayout mNewArrivalLayout;
  private TextView mNewArrivalCountTv;
  /** 服务保障模块 */
  private TextView mExplainCountTv;
  /** 显示Banner控件 */
  private ImageCycleView mBannerView;
  private FrameLayout mBannerLayout;
  /** 头条布局 */
  private LinearLayout mForumLinear;

  private boolean isCityAboutFinished = false;
  private boolean isFirstShowDialog = true;

  /** 头条数据源 */
  private List<HomeForumEntity> mForumDatas = new ArrayList<>();
  private HomeForumAdapter mForumAdapter;

  /** 城市变化时,如果当前界面不可见,不刷新,直到可见时才刷新 */
  private int mLastVisibleCityId = HCDbUtil.getSavedCityId();

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_home_page;
  }

  @Override void doInitViewOrData() {

    initTitleBar();

    initPullToRefresh();

    int resLayout = R.layout.fragment_home_page_headerview;
    mHeaderView = LayoutInflater.from(getActivity()).inflate(resLayout, null);

    initListView();

    setUpCityAndSearch();

    initViews();

    setUpHotPrice();

    requestDatas();

    //首页加载完成,通知MainActivity隐藏loading层
    HCEvent.postEvent(HCEvent.ACTION_NOW_LOADED_HOME_PAGE);

    //首页统计
    HCStatistic.homePageShowing();
  }

  private void initTitleBar() {
    mTopSearchParent = (FrameLayout) getActivity().findViewById(R.id.frame_search_home_bar);
    mTopCityTv = (TextView) getActivity().findViewById(R.id.tv_home_title_city);

    HCCityEntity enty = GlobalData.userDataHelper.getCity();
    if (enty != null && !TextUtils.isEmpty(enty.getCity_name())) {
      mTopCityTv.setText(enty.getCity_name());
    }

    getActivity().findViewById(R.id.linear_search_home_title).setOnClickListener(this);
    mTopCityTv.setOnClickListener(this);
    mTopSearchParent.setOnClickListener(null);
  }

  private void initPullToRefresh() {
    mLiveIv = (ImageView) getActivity().findViewById(R.id.iv_home_live);

    mPullToRefresh = (HCPullToRefresh) getActivity().findViewById(R.id.hcptr_home_list);
    //解决左右滑动和上下滑动的冲突
    mPullToRefresh.setNeedDetectXY(true);

    mPullToRefresh.setOnRefreshCallback(new HCPullToRefresh.OnRefreshCallback() {
      @Override public void onPullDownRefresh() {
        if (!HCUtils.isNetAvailable()) {
          mPullToRefresh.finishRefresh();
          HCUtils.showToast(R.string.hc_net_unreachable);
          return;
        }
        requestDatas();
      }

      @Override public void onLoadMoreRefresh() {
      }
    });
  }

  private void initListView() {
    ListView mListView = mPullToRefresh.getListView();
    mListView.setVerticalScrollBarEnabled(false);
    mListView.setDividerHeight(0);
    mListView.addHeaderView(mHeaderView);

    int itemRes = R.layout.lvitem_home_forum;
    mForumAdapter = new HomeForumAdapter(getActivity(), mForumDatas, itemRes);
    mPullToRefresh.setAdapter(mForumAdapter);
    mForumAdapter.notifyDataSetChanged();
    mPullToRefresh.setNoDefaultDivider();
    mPullToRefresh.removeFooter();

    initListViewScrollListener();
  }

  private void initListViewScrollListener() {
    mPullToRefresh.setHCscrollListener(new HCPullToRefresh.HCScrollListener() {
      @Override public void onScrollStateChanged(AbsListView view, int scrollState) {

      }

      @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        if (mDirectView != null && mDirectView.getHeight() > 0) {
          int h = mDirectView.getHeight() + HCUtils.dp2px(50);
          int top = Math.abs(mHeaderView.getTop());
          if (top >= h) {
            if (!isTopSearchParentShowing) {
              isTopSearchParentShowing = true;
              HCViewUtils.animateLayout(mTopSearchParent, mTopSearchParentHeight, 0);
            }
          } else {
            if (isTopSearchParentShowing) {
              isTopSearchParentShowing = false;
              HCViewUtils.animateLayout(mTopSearchParent, 0, mTopSearchParentHeight);
            }
          }
        }
      }
    });
  }

  private void setUpCityAndSearch() {
    mEnterCityTv = (TextView) mHeaderView.findViewById(R.id.tv_home_enter_city);
    mEnterCountTv = (TextView) mHeaderView.findViewById(R.id.tv_home_enter_number);
    mEnterHintTv = (TextView) mHeaderView.findViewById(R.id.tv_home_enter_hint);
    String lastCount = HCSpUtils.getLastTotalCount();
    if (!HCUtils.isNetAvailable()) {
      if (TextUtils.isEmpty(lastCount)) {
        mEnterCountTv.setVisibility(View.GONE);
        mEnterHintTv.setVisibility(View.GONE);
      } else {
        mEnterCountTv.setVisibility(View.VISIBLE);
        mEnterHintTv.setVisibility(View.VISIBLE);
        mEnterCountTv.setText(lastCount);
      }
    }

    HCCityEntity enty = GlobalData.userDataHelper.getCity();
    if (enty != null && !TextUtils.isEmpty(enty.getCity_name())) {
      mEnterCityTv.setText(enty.getCity_name());
    }
    mHeaderView.findViewById(R.id.iv_home_search).setOnClickListener(this);
    mEnterCityTv.setOnClickListener(this);
  }

  private void initViews() {
    /** 直营店模块 */
    mDirectView = (ImageCycleView) mHeaderView.findViewById(R.id.adCycleView_direct);
    mDirectView.getLayoutParams().height = (int) (HCUtils.getScreenWidthInPixels() * 700F / 750F);
    /** 买车卖车模块 */
    mHeaderView.findViewById(R.id.tv_home_entrance_buy).setOnClickListener(this);
    mHeaderView.findViewById(R.id.tv_home_entrance_sell).setOnClickListener(this);
    /** Banner模块 */
    mBannerView = (ImageCycleView) mHeaderView.findViewById(R.id.adCycleView);
    mBannerLayout = (FrameLayout) mHeaderView.findViewById(R.id.frame_home_banner);
    mBannerView.getLayoutParams().height = (int) (HCUtils.getScreenWidthInPixels() * 180F / 750F);
    /** 品牌模块 */
    mChooseBrandGv = (GridView) mHeaderView.findViewById(R.id.gv_home_brand);
    /** 今日新上模块 */
    mNewArrivalLayout = (RelativeLayout) mHeaderView.findViewById(R.id.rel_home_new_arrival);
    mNewArrivalCountTv = (TextView) mHeaderView.findViewById(R.id.iv_home_new_arrival_count);

    /** 服务保障模块 */
    mHeaderView.findViewById(R.id.linear_explain).setOnClickListener(this);
    mExplainCountTv = (TextView) mHeaderView.findViewById(R.id.tv_home_explain_count);
    HCViewUtils.setHomeTextFormat(mExplainCountTv,
        HCUtils.getResString(R.string.hc_home_explain_tv1));
    /** 头条布局 */
    mForumLinear = (LinearLayout) mHeaderView.findViewById(R.id.layout_home_forum);
    mHeaderView.findViewById(R.id.tv_forum_go_all).setOnClickListener(this);
  }

  /** 热门价格 */
  private void setUpHotPrice() {
    RadioGroup rp0 = (RadioGroup) mHeaderView.findViewById(R.id.hot_rg_0);
    rp0.setOnCheckedChangeListener(this);
  }

  private void requestDatas() {
    isCityAboutFinished = false;
    //用是否能获得客服电话判断是不是新用户
    boolean isNewUser = TextUtils.isEmpty(HCSpUtils.getKefuPhone());
    //获取type等于0，标示以前从来没有弹过次弹窗
    boolean isNeedShowQuiz = HCSpUtils.getHomeQuizDialog() == 0;
    requestCityAboutData();
    if (isFirstShowDialog && isNewUser && isNeedShowQuiz) {
      requestDialogData();
    }
  }

  private void requestDialogData() {
    Map<String, Object> dialogParam = HCParamsUtil.getHomeDialogData();
    API.post(new HCRequest(dialogParam, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        isFirstShowDialog = false;
        //弹窗后将客服电话存入SP中.
        HCSpUtils.saveKefuPhone(HCConsts.ADVISORY_FORMAT_PHONE);
        if (!TextUtils.isEmpty(responseJsonString) && isCurentActivityValid()) {
          handleDialogData(responseJsonString);
        }
      }
    }));
  }

  private void handleDialogData(String responseJsonString) {
    HCHomeDialogDataEntity entity = HCJSONParser.parseHomeDialogData(responseJsonString);
    if (entity != null) {
      DialogUtils.showHomeQuizDialog(getActivity(), entity);
    }
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (mBannerView != null) {
      if (hidden) {
        mBannerView.pauseImageCycle();
        mDirectView.pauseImageCycle();
      } else {
        mBannerView.startImageCycle();
        mDirectView.startImageCycle();
        //当前界面可见,对比当前的cityId和上一次可见时的cityid
        //如果发现不一致,刷新城市相关的数据
        int curCityId = HCDbUtil.getSavedCityId();
        if (curCityId != mLastVisibleCityId) {
          doRefresh();
        }
        //首页统计
        HCStatistic.homePageShowing();
      }
    }
  }

  @Override public void onResume() {
    super.onResume();
    ThirdPartInjector.onPageStart(this.getClass().getSimpleName());
  }

  /***
   * 请求城市相关数据
   */
  private void requestCityAboutData() {
    if (!HCUtils.isNetAvailable()) {
      HCUtils.showToast(R.string.hc_net_unreachable);
      mDirectView.setBackgroundResource(R.drawable.home_page_default_direct);
      return;
    }
    Map<String, Object> cityParams = HCParamsUtil.getHomeCityData();
    API.post(new HCRequest(cityParams, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        isCityAboutFinished = true;
        seeIfNeedFinishRefresh();
        if (!TextUtils.isEmpty(responseJsonString) && isCurentActivityValid()) {
          handleCityData(responseJsonString);
        }
      }
    }));
  }

  private void handleCityData(String resp) {

    HCHomeCityDataEntity entity = HCJSONParser.parseHomeCityData(resp);

    //设置运营店
    List<HCBannerEntity> directData = entity.getTop_slider();
    if (!HCUtils.isListEmpty(directData) && isCurentActivityValid()) {
      new BannerController().fillBannerData(directData, mDirectView, getActivity(), 750F / 700F);
    }

    //设置总车辆
    String totalCount = entity.getVehicle_count();
    if (!TextUtils.isEmpty(totalCount)) {
      mEnterCountTv.setVisibility(View.VISIBLE);
      mEnterHintTv.setVisibility(View.VISIBLE);
      mEnterCountTv.setText(totalCount);
      HCSpUtils.saveLastTotalCount(totalCount);
    }

    //设置banner
    List<HCBannerEntity> bannerData = entity.getMid_banner();
    if (!HCUtils.isListEmpty(bannerData) && isCurentActivityValid()) {
      mBannerLayout.setVisibility(View.VISIBLE);
      new BannerController().fillBannerData(bannerData, mBannerView, getActivity(), 750F / 180F);
    } else {
      mBannerLayout.setVisibility(View.GONE);
    }

    //推广图片
    List<HCPromoteEntity> promoteEntities = entity.getPop_images();
    if (!HCUtils.isListEmpty(promoteEntities) && promoteEntities.size() == 1) {
      if (isVisible()) {
        if (isFirstLoadPromote) {
          isFirstLoadPromote = false;
          DialogUtils.showPromoteDialog(getActivity(), promoteEntities.get(0));
        }
      }
    }

    //品牌
    List<HomeBrandEntity> brands = entity.getBrand_list();
    if (!HCUtils.isListEmpty(brands) && brands.size() == 4) {
      setUpBrand(brands);
    }

    //今日新上
    String count = entity.getToday_count();
    if (!TextUtils.isEmpty(count)) {
      mNewArrivalLayout.setVisibility(View.VISIBLE);
      setUpNewArrival(count);
    }

    //更新城市
    List<HCCityEntity> allCities = entity.getAll_city();
    if (!HCUtils.isListEmpty(allCities)) {
      HCSpUtils.setSupportCities(allCities);
    }

    HCViewUtils.setHomeTextFormat(mExplainCountTv, entity.getAccident_check_count());

    //处理论坛模块
    List<HomeForumEntity> forumEntities = entity.getBtm_posts();
    if (!HCUtils.isListEmpty(forumEntities)) {
      mForumLinear.setVisibility(View.VISIBLE);
      mForumDatas.clear();
      mForumDatas.addAll(forumEntities);
      mForumAdapter.notifyDataSetChanged();
    } else {
      mForumLinear.setVisibility(View.GONE);
    }

    //是否有直营店
    String hasDirect = entity.getHas_zhiyingdian();
    HCEvent.postEvent(HCEvent.ACTION_IS_NEED_DORECT, hasDirect);
    HCSpUtils.saveHasDirect(hasDirect);

    //是否有限时活动
    String hasLimitActivity = entity.getActivity_start();
    HCEvent.postEvent(HCEvent.ACTION_IS_LIMIT_ACTIVITY, hasLimitActivity);
    HCSpUtils.saveHasLimitActivity(hasLimitActivity);

    HCHomeLiveEntity liveEntity = entity.getZhibo_btn();
    mLiveIv.setVisibility(View.GONE);
    if (liveEntity != null) {
      final String liveUrl = liveEntity.getLink_url();
      String livePic = liveEntity.getPic_url();
      HCSpUtils.setZhiBoEntity(liveEntity);
      HCEvent.postEvent(HCEvent.ACTION_IS_HAS_LIVE, liveEntity);
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

  private void setUpBrand(List<HomeBrandEntity> brands) {
    if (!isCurentActivityValid()) return;

    int brandRes = R.layout.gridview_item_home_brand;
    HomeBrandGridViewAdapter gvAdapter =
        new HomeBrandGridViewAdapter(getActivity(), brands, brandRes);
    mChooseBrandGv.setAdapter(gvAdapter);
  }

  private void setUpNewArrival(String count) {
    mNewArrivalCountTv.setText(count);
    mNewArrivalLayout.setOnClickListener(this);
  }

  public void onEvent(HCCommunicateEntity entity) {
    if (entity == null || !isCurentActivityValid()) return;

    String action = entity.getAction();

    switch (action) {
      case HCEvent.ACTION_CITYCHANGED: {
        if (isVisible()) {
          doRefresh();
        }
      }
      break;

      case HCEvent.ACTION_DUPLICATE_CLICK_TAB: {
        String value = entity.getStrValue();
        if (FragmentController.HOME_TAG.equals(value)) {
          if (mPullToRefresh != null && mPullToRefresh.getListView().getChildAt(0).getTop() != 0) {
            mPullToRefresh.tryToSmoothScrollUp();
          }
        }
      }
      break;
    }
  }

  private void doRefresh() {

    if (!HCUtils.isNetAvailable()) {
      mPullToRefresh.finishRefresh();
      HCUtils.showToast(R.string.hc_net_unreachable);
      return;
    }

    if (mTopCityTv != null) {
      mTopCityTv.postDelayed(new Runnable() {
        @Override public void run() {
          if (mTopCityTv != null) {
            requestCityAboutData();
          }
        }
      }, 150);
    }

    HCCityEntity cityEntity = GlobalData.userDataHelper.getCity();
    String cityName = cityEntity.getCity_name();
    if (mTopCityTv != null) {
      mTopCityTv.setText(cityName);
      mEnterCityTv.setText(cityName);
    }

    mLastVisibleCityId = cityEntity.getCity_id();
  }

  private void showCityChoose() {
    List<HCCityEntity> allCities = HCSpUtils.getSupportCities();
    if (HCUtils.isListEmpty(allCities)) {
      requestSupportCity();
    } else {
      DialogUtils.showCityDialog(getActivity(), allCities);
    }
  }

  // 更新City
  private void requestSupportCity() {
    if (!HCUtils.isNetAvailable()) {
      HCUtils.showToast(R.string.hc_net_unreachable);
      return;
    }
    Map<String, Object> params = HCParamsUtil.getSupportCity();
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleSupportCity(responseJsonString);
      }
    }));
  }

  private void handleSupportCity(String responseJsonString) {

    if (!TextUtils.isEmpty(responseJsonString)) {
      List<HCCityEntity> allCities = HCJSONParser.parseSupportCity(responseJsonString);
      if (!HCUtils.isListEmpty(allCities)) {
        HCSpUtils.setSupportCities(allCities);
        DialogUtils.showCityDialog(getActivity(), allCities);
      }
    }
  }

  @Override public void onClick(View v) {

    if (v == null) return;

    switch (v.getId()) {
      case R.id.linear_search_home_title:
      case R.id.iv_home_search:
        HCEvent.postEvent(HCEvent.ACTION_GO_SEARCH, "HomePage");
        //统计搜索
        HCStatistic.searchClick();
        HCSensorsUtil.homePageClick("搜索点击");
        break;
      case R.id.tv_home_title_city:
      case R.id.tv_home_enter_city:
        showCityChoose();
        //统计 城市切换
        HCStatistic.cityClick();
        HCSensorsUtil.homePageClick("城市切换");
        break;
      case R.id.tv_home_entrance_buy:
        FilterUtils.resetNormalToDefaultExceptCity();
        HCEvent.postEvent(HCEvent.ACTION_HOME_TO_MAINACT_GOOD_VEHICLES);
        HCEvent.postEvent(FilterUtils.allHost + HCEvent.ACTION_SORT_CHOOSED_CHANGE);
        HCEvent.postEvent(FilterUtils.allHost + HCEvent.ACTION_BRAND_CHOOSED_CHANGE);
        HCEvent.postEvent(FilterUtils.allHost + HCEvent.ACTION_PRICE_CHOOSED_CHANGE);
        HCEvent.postEvent(FilterUtils.allHost + HCEvent.ACTION_MORE_CHOOSED_CHANGE);
        HCStatistic.homePageBuyClick();
        HCSensorsUtil.homePageClick("买车");
        break;
      case R.id.tv_home_entrance_sell:
        startDestAct(SellVehicleActivity.class);
        HCStatistic.homePageSellClick();
        HCSensorsUtil.homePageClick("卖车");
        break;
      case R.id.rel_home_new_arrival:
        HCEvent.postEvent(HCEvent.ACTION_HOME_TO_MAINACT_TODAY_VEHICLES);
        HCSensorsUtil.homePageClick("今日新上");
        break;
      case R.id.linear_explain://服务保障
        WebBrowserActivity.urlToThis(getActivity(), HCUtils.getServiceURL());
        HCStatistic.homeExplainClick();
        HCSensorsUtil.homePageClick("服务保障");
        break;
      case R.id.tv_forum_go_all://论坛查看全部
        Intent mIntent = new Intent(GlobalData.mContext, WebBrowserActivity.class);
        mIntent.putExtra(HCConsts.INTENT_KEY_URL, HCConsts.FORUM_SHARE_URL);
        mIntent.putExtra(HCConsts.INTENT_KEY_FORUM, HCConsts.FORUM_SHARE_URL);
        getActivity().startActivity(mIntent);
        HCSensorsUtil.homePageClick("更多分享");
        break;
    }
  }

  private void handleHotPrice(int checkedId) {
    int[] ids = { R.id.rb_price_0, R.id.rb_price_1, R.id.rb_price_2, R.id.rb_price_3 };

    String[] priceArr = HCUtils.getResArray(R.array.home_hot_price);
    String[] priceArrKey = HCUtils.getResArray(R.array.home_hot_price_key);
    int index = 0;
    for (; index < ids.length; index++) {
      if (ids[index] == checkedId) {
        break;
      }
    }
    FilterUtils.resetNormalToDefaultExceptCity();
    FilterUtils.priceKey2FilterTerm(allHost, priceArr[index]);
    HCSensorsUtil.homePageClick(priceArrKey[index]);
    //通知价格选择变化
    HCEvent.postEvent(allHost + HCEvent.ACTION_PRICE_CHOOSED_CHANGE);

    HCEvent.postEvent(HCEvent.ACTION_HOME_TO_MAINACT_GOOD_VEHICLES);
    HCStatistic.homePriceClick();
  }

  private void seeIfNeedFinishRefresh() {
    if (isCityAboutFinished) {
      if (mPullToRefresh != null) {
        mPullToRefresh.finishRefresh();
      }
    }
  }

  @Override public void onPause() {
    super.onPause();
    ThirdPartInjector.onPageEnd(this.getClass().getSimpleName());
  }

  @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
    if (isCurentActivityValid()) {
      handleHotPrice(checkedId);
      // 为了让每一次都响应点击
      ((RadioButton) group.findViewById(checkedId)).setChecked(false);
    }
  }
}