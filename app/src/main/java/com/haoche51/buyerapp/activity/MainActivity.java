package com.haoche51.buyerapp.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import butterknife.InjectView;
import com.haoche51.buyerapp.HCPollService;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.dao.SeriesDAO;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.HCBrandEntity;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.SeriesEntity;
import com.haoche51.buyerapp.entity.push.PushMsgDataEntity;
import com.haoche51.buyerapp.fragment.BrandFragment;
import com.haoche51.buyerapp.fragment.CarSeriesFragment;
import com.haoche51.buyerapp.fragment.CoreFragment;
import com.haoche51.buyerapp.fragment.MoreFilterFragment;
import com.haoche51.buyerapp.fragment.PriceFilterFragment;
import com.haoche51.buyerapp.fragment.SortFilterFragment;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.push.HCPushMessageHelper;
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
import com.haoche51.custom.DoubleClickListener;
import com.haoche51.custom.NavigationButton;
import com.umeng.analytics.MobclickAgent;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 应用主要MainActivity,底部tab切换Fragment都在这管理
 */
public class MainActivity extends HCCommonTitleActivity {

  /**
   * 在我的,和首页检查 优惠券弹层
   */
  private boolean isShowedHomeCouponDialog;

  private boolean isShowedProfileCouponDialog;

  @InjectView(R.id.view_loading) View mLoadingView;

  /** 底部tab 父控件 */
  @InjectView(R.id.navigation_bar) RadioGroup mNavigatorRp;
  /** 底部tab 论坛 */
  @InjectView(R.id.rb_tab_forum) NavigationButton forumRb;
  /** 底部tab 我的 */
  @InjectView(R.id.rb_tab_profile) NavigationButton profileRb;

  /**
   * 记录上一次fragment的tag
   */
  private String mLastFragmentTag = "";

  private ServiceConnection mServiceConnection;
  private HCPollService.HCServiceBinder mServiceBinder;

  private boolean hasLoadedHomePage = false;

  private boolean isNormalInitial = true;

  /** 标识当前Activity是否存活 */
  private boolean isActive = true;

  /** 是否需要先初始化WebBrowserAct */
  private boolean isNeedFirstInitWeb = false;

  /**
   * 延迟加载homePage.如果从splashAct直接跳转到WebBrowserAct
   * 相应增加延迟时间.防止在HomePage的初始化影响WebBrowserAct的初始化
   */

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    hideTitleBar();
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_main;
  }

  @Override void initViews() {

    HCEvent.register(this, HCEvent.PRIORITY_HIGH);

    initListener();

    seeIfFromSplashToWebBrowser();

    seeIfFromPush(getIntent());

    bindPollService();

    //清空存储的筛选条件
    HCSpUtils.clearFilterTerm();
    //清空是否有直营店
    HCSpUtils.clearHasDirect();
    //清空限时活动
    HCSpUtils.clearHasLimitActivity();
    //清空直播
    HCSpUtils.clearZhiBoEntity();
  }

  private void initListener() {
    findViewById(R.id.rb_tab_home_page).setOnClickListener(mDoubleClickListener);
    findViewById(R.id.rb_tab_core_vehicle).setOnClickListener(mDoubleClickListener);
    mNavigatorRp.setOnCheckedChangeListener(mOnCheckListener);
  }

  private DoubleClickListener mDoubleClickListener = new DoubleClickListener() {
    @Override public void performDoubleClick(View v) {
      int currentId = v.getId();

      if (currentId == R.id.rb_tab_core_vehicle) {
        HCEvent.postEvent(HCEvent.ACTION_DUPLICATE_CLICK_TAB, FragmentController.CORE_VEHICLE_TAG);
      }

      if (currentId == R.id.rb_tab_home_page) {
        HCEvent.postEvent(HCEvent.ACTION_DUPLICATE_CLICK_TAB, FragmentController.HOME_TAG);
      }
    }
  };

  /***
   * 如果从splash点击进入,并需要跳转到WebBrowserActivity.
   * 延迟MainActivity的初始化.
   * 直到到WebBrowserActivity执行完onResume了以后
   */
  private void seeIfFromSplashToWebBrowser() {
    Intent mIntent = getIntent();
    if (mIntent.hasExtra(HCConsts.INTENT_KEY_URL)) {
      isNeedFirstInitWeb = true;
      String url = mIntent.getStringExtra(HCConsts.INTENT_KEY_URL);
      WebBrowserActivity.urlToThis(this, url);
    }
  }

  // 启动轮询service
  private void bindPollService() {
    if (mServiceBinder == null) {
      Intent service = new Intent(this, HCPollService.class);
      mServiceConnection = new ServiceConnection() {
        @Override public void onServiceDisconnected(ComponentName name) {
        }

        @Override public void onServiceConnected(ComponentName name, IBinder service) {
          //Caused by: java.lang.ClassCastException:
          // android.os.BinderProxy cannot be cast to com.haoche51.buyerapp.HCPollService$HCServiceBinder
          if (service != null && service instanceof HCPollService.HCServiceBinder) {
            mServiceBinder = (HCPollService.HCServiceBinder) service;
          }
        }
      };
      bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    //延迟一秒加载，为了让服务绑定成功，防止请求不到数据的情况。
    if (mNavigatorRp != null) {
      mNavigatorRp.postDelayed(new Runnable() {
        @Override public void run() {
          DialogUtils.showLocationChangeDialog(MainActivity.this);
        }
      }, 1000);
    }
  }

  private OnCheckedChangeListener mOnCheckListener = new OnCheckedChangeListener() {
    @Override public void onCheckedChanged(RadioGroup group, int checkedId) {

      switch (checkedId) {
        case R.id.rb_tab_home_page:// 首页
        {
          changeFragmentByTag(FragmentController.HOME_TAG);

          if (HCUtils.isUserLogined()) {

            if (!isShowedHomeCouponDialog) {
              DialogUtils.showCouponCountDialog(MainActivity.this);
              isShowedHomeCouponDialog = true;
            }
          }
          break;
        }

        case R.id.rb_tab_core_vehicle:// 买车
        {
          changeFragmentByTag(FragmentController.CORE_VEHICLE_TAG);
          break;
        }

        case R.id.rb_tab_forum:// 论坛
        {
          changeFragmentByTag(FragmentController.FORUM_TAG);
          if (!HCSpUtils.isHasClickForumTab()) {
            HCSpUtils.setHasClickForumTab();
          }
          if (forumRb != null) {
            forumRb.hideIndicator();
          }
          break;
        }

        case R.id.rb_tab_profile:// 我的
        {
          if (!mLastFragmentTag.equals(FragmentController.PROFILE_TAG)) {
            HCEvent.postEvent(HCEvent.ACTION_CHANGED_TO_PROFILE);
          }
          changeFragmentByTag(FragmentController.PROFILE_TAG);
          if (HCUtils.isUserLogined()) {
            if (!isShowedProfileCouponDialog) {
              DialogUtils.showCouponCountDialog(MainActivity.this);
              isShowedProfileCouponDialog = true;
            }
          }
          break;
        }
      }
    }
  };

  private void changeFragmentByTag(String fragmentTag) {

    //java.lang.IllegalStateException: Activity has been destroyed
    //caution || MainActivity.this.isFinishing()
    if (!isActive) return;

    if (fragmentTag.equals(mLastFragmentTag)) {
      return;
    }
    try {
      FragmentManager mFragmentManager = getSupportFragmentManager();
      FragmentTransaction mTransaction = mFragmentManager.beginTransaction();

      if (mFragmentManager.findFragmentByTag(fragmentTag) != null) {
        Fragment currentFragment = mFragmentManager.findFragmentByTag(fragmentTag);
        mTransaction.show(currentFragment);
      } else {
        Fragment currentFragment = FragmentController.newInstance(fragmentTag);
        mTransaction.add(R.id.fragment_container, currentFragment, fragmentTag);
      }

      Fragment lastFragment = mFragmentManager.findFragmentByTag(mLastFragmentTag);
      if (lastFragment != null) {
        mTransaction.hide(lastFragment);
      }
      mTransaction.commitAllowingStateLoss();
      mFragmentManager.executePendingTransactions();

      mLastFragmentTag = fragmentTag;
    } catch (Exception e) {
      HCLog.d(TAG, "changeFragmentByTag .." + fragmentTag + "Exception");
    }
    String tabName = "";
    switch (fragmentTag) {
      case FragmentController.HOME_TAG:
        tabName = "HomePage";
        break;
      case FragmentController.CORE_VEHICLE_TAG:
        tabName = "BuyVehiclePage";
        break;
      case FragmentController.FORUM_TAG:
        tabName = "ForumPage";
        break;
      case FragmentController.PROFILE_TAG:
        tabName = "MyPage";
        break;
    }
    HCSensorsUtil.tabBarClick(tabName);
    //底部导航栏添加统计
    HCStatistic.navigationClick(fragmentTag);
  }

  public void onEvent(HCCommunicateEntity communicateEntity) {
    if (communicateEntity != null) {

      handleHomeReturnActions(communicateEntity);

      handleFilterEvent(communicateEntity);

      handleFilterOpenAndCloseEvent(communicateEntity);

      String action = communicateEntity.getAction();

      switch (action) {
        case HCEvent.ACTION_GO_SEARCH: //在HomePageFragment中或者CoreFragment中点击搜索
          Intent intent = new Intent(this, HCSearchActivity.class);
          String from = communicateEntity.getStrValue();
          intent.putExtra("FromPlace", from);
          startActivityForResult(intent, HCConsts.REQUESTCODE_FOR_SEARCH);
          overridePendingTransition(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
          HCEvent.cancelDelivery(communicateEntity);
          break;

        case HCEvent.ACTION_COUPON_REMINDER:
        case HCEvent.ACTION_FEEDBACK_REMINDER:
        case HCEvent.ACTION_SUBCOUNTS_REMINDER:
          // 显示底部tab红点
          profileRb.showIndicator();
          break;

        case HCEvent.ACTION_HOME_SUB_REMINDER_CHANGED:
        case HCEvent.ACTION_PROFILE_REMINDER_CHANGED:
          seeIfNeedReminder();
          break;

        case HCEvent.ACTION_NOW_LOADED_HOME_PAGE://首页加载完毕,关闭loading
          HCUtils.hideViewIfNeed(mLoadingView);
          hasLoadedHomePage = true;
          break;

        case HCEvent.ACTION_WEBBROWSER_LOADED:
          //webbrowserActivity已经初始化,如果首页还没有初始化,则开始初始化
          if (!hasLoadedHomePage && mNavigatorRp != null) {
            mNavigatorRp.check(R.id.rb_tab_home_page);
          }
          break;
      }
    }
  }

  /**
   * 处理首页返回相关跳转
   */
  private void handleHomeReturnActions(HCCommunicateEntity communicateEntity) {
    String action = communicateEntity.getAction();
    switch (action) {
      case HCEvent.ACTION_HOME_TO_MAINACT_GOOD_VEHICLES://选购好车
        doMainToCore(CoreFragment.PAGE_ALL_VEHICLES, communicateEntity);
        break;

      case HCEvent.ACTION_HOME_TO_MAINACT_DIRECT_VEHICLES://超值低价
        doMainToCore(CoreFragment.PAGE_DIRECT, communicateEntity);
        break;

      case HCEvent.ACTION_HOME_TO_MAINACT_TODAY_VEHICLES://今日新上
        doMainToCore(CoreFragment.PAGE_TODAY_VEHICLES, communicateEntity);
        break;
    }
  }

  private void doMainToCore(int page, HCCommunicateEntity communicateEntity) {
    if (mNavigatorRp != null) {
      mNavigatorRp.check(R.id.rb_tab_core_vehicle);
      HCEvent.postEvent(HCEvent.ACTION_MAINACT_TO_CORE, page);
      //取消事件传播
      HCEvent.cancelDelivery(communicateEntity);
    }
  }

  private void seeIfNeedReminder() {
    if (profileRb != null) {
      int totalCounts = HCSpUtils.getAllReminderCounts();
      if (totalCounts == 0) {
        profileRb.hideIndicator();
      } else {
        profileRb.showIndicator();
      }
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == HCConsts.REQUESTCODE_FOR_SEARCH) {
      if (data != null && data.hasExtra(HCConsts.KEY_FOR_SEARCH_KEY) && mNavigatorRp != null) {
        mNavigatorRp.check(R.id.rb_tab_core_vehicle);
        String keyword = data.getStringExtra(HCConsts.KEY_FOR_SEARCH_KEY);
        //带着搜索关键字到CoreFragment并告诉CoreFragment切换到全部好车
        HCEvent.postEvent(HCEvent.ACTION_MAINACT_SEARCH_TO_CORE, keyword);
      }
    }
  }

  public HCPollService.HCServiceBinder getServiceBinder() {
    return mServiceBinder;
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    seeIfFromPush(intent);
  }

  @Override protected void onResume() {

    super.onResume();

    MobclickAgent.onResume(this);

    isActive = true;

    seeIfNeedDelayHomePage();

    seeIfNeedReminder();

    seeIfNeedForumIndicator();
  }

  @Override protected void onPause() {
    super.onPause();
    MobclickAgent.onPause(this);
  }

  private void seeIfNeedForumIndicator() {
    if (forumRb != null) {
      if (HCSpUtils.isHasClickForumTab()) {
        forumRb.hideIndicator();
      } else {
        forumRb.showIndicator();
      }
    }
  }

  private void seeIfNeedDelayHomePage() {
    if (!hasLoadedHomePage && isNormalInitial) {
      mNavigatorRp.postDelayed(new Runnable() {
        @Override public void run() {
          if (!isNeedFirstInitWeb && mNavigatorRp != null) {
            mNavigatorRp.check(R.id.rb_tab_home_page);
          }
        }
      }, 100);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    //super.onSaveInstanceState(outState);
    //for bug 内存不足时 暂时全部重新创建
  }

  @Override protected void onDestroy() {
    doRelease();
    super.onDestroy();
  }

  private void doRelease() {

    isActive = false;

    HCEvent.unRegister(this);

    if (mServiceConnection != null) {
      unbindService(mServiceConnection);
    }
    if (mServiceBinder != null) {
      mServiceBinder.getPollService().stopSelf();
    }
  }

  private long mLastKeyBackTime;

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    long now = System.currentTimeMillis();
    boolean isExit = now - mLastKeyBackTime <= 2000;
    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && !isExit) {
      mLastKeyBackTime = now;
      HCUtils.showToast(R.string.hc_back_again_exit);
    }
    return !isExit || super.onKeyDown(keyCode, event);
  }

  private void seeIfFromPush(Intent inputIntent) {
    Intent mIntent = inputIntent == null ? getIntent() : inputIntent;
    if (mIntent != null && mIntent.hasExtra(HCConsts.INKENT_KEY_PUSH_TYPE)) {
      boolean hasLogined = HCUtils.isUserLogined();
      int type = mIntent.getIntExtra(HCConsts.INKENT_KEY_PUSH_TYPE, 0);

      switch (type) {

        case HCPushMessageHelper.TYPE_URL:
          String keyUrl = HCConsts.INTEKTN_KEY_PUSH_DATA;
          if (mIntent.getSerializableExtra(keyUrl) != null) {
            Serializable se = mIntent.getSerializableExtra(keyUrl);
            if (se != null && se instanceof PushMsgDataEntity) {
              PushMsgDataEntity dataEntity = (PushMsgDataEntity) se;
              String param = dataEntity.getParams();
              if (!TextUtils.isEmpty(param)) {
                WebBrowserActivity.urlToThis(this, param);
              }
            }
          }
          break;

        case HCPushMessageHelper.TYPE_BANG_MAI_ALL_GOOD:
          mNavigatorRp.check(R.id.rb_tab_core_vehicle);
          isNormalInitial = false;
          String allGoodQuery = HCConsts.INTEKTN_KEY_PUSH_DATA;
          if (mIntent.getSerializableExtra(allGoodQuery) != null) {
            Serializable se = mIntent.getSerializableExtra(allGoodQuery);
            if (se != null && se instanceof PushMsgDataEntity) {
              PushMsgDataEntity pbm = (PushMsgDataEntity) se;
              FilterTerm normalTerm = FilterUtils.bangMaiToFilterTerm(pbm);
              FilterUtils.setFilterTerm(FilterUtils.allHost, normalTerm);

              if (HCDbUtil.getSavedCityId() != pbm.getCity()) {
                String cityName = HCDbUtil.getCityNameById(pbm.getCity() + "");
                DialogUtils.showBangMaiCityChangeDialog(MainActivity.this, cityName,
                    CoreFragment.PAGE_ALL_VEHICLES);
              } else {
                mNavigatorRp.postDelayed(new Runnable() {
                  @Override public void run() {
                    HCEvent.postEvent(HCEvent.ACTION_MAINACT_TO_CORE,
                        CoreFragment.PAGE_ALL_VEHICLES);
                  }
                }, 100);
              }
            }
          }
          break;

        case HCPushMessageHelper.TYPE_RECOMMEND: //推荐
          Intent remmendIntent = new Intent(this, RecommendVehicleActivity.class);
          startActivity(remmendIntent);
          break;
        case HCPushMessageHelper.TYPE_SUBSCRIBE://我的订阅
          isNormalInitial = false;
          if (hasLogined) {
            startActivity(new Intent(this, MySubscribeVehiclesActivity.class));
          } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(HCConsts.INTENT_KEY_LOGIN_TITLE, R.string.hc_action_from_subscribe);
            intent.putExtra(HCConsts.INTENT_KEY_IS_FOR_LOGIN, true);
            intent.putExtra(HCConsts.INTENT_KEY_LOGIN_DEST, MySubscribeVehiclesActivity.class);
            startActivity(intent);
          }
          break;

        case HCPushMessageHelper.TYPE_ORDAIN: //已预定推送信息
        case HCPushMessageHelper.TYPE_RESERVE://已预约推送信息
          if (hasLogined) {
            startActivity(new Intent(this, MyBookingVehicleActivity.class));
          } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(HCConsts.INTENT_KEY_LOGIN_DEST, MyBookingVehicleActivity.class);
            startActivity(intent);
          }
          break;

        case HCPushMessageHelper.TYPE_COLLECTION_OFFLINE://收藏车源下线推送
          isNormalInitial = false;
          if (hasLogined) {
            startActivity(new Intent(this, MyCollectionActivity.class));
          } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(HCConsts.INTENT_KEY_LOGIN_DEST, MyCollectionActivity.class);
            startActivity(intent);
          }
          break;
        case HCPushMessageHelper.TYPE_COLLECTION_SOLD://收藏车源售出推送
        case HCPushMessageHelper.TYPE_COLLECTION_REDUCTION://收藏车源降价推送
        case HCPushMessageHelper.TYPE_COLLECTION_RESERVATION://收藏车源被预约推送
          String key = HCConsts.INTEKTN_KEY_PUSH_DATA;
          if (mIntent.getSerializableExtra(key) != null) {
            Serializable se = mIntent.getSerializableExtra(key);
            if (se != null && se instanceof PushMsgDataEntity) {
              PushMsgDataEntity pd = (PushMsgDataEntity) se;
              int id = pd.getVehicle_source_id();
              if (id > 0) {
                String vid = String.valueOf(id);
                VehicleDetailActivity.idToThis(this, vid, "推送");
              }
            }
          }
          break;
      }
    }
  }
  /**  ======================== 以下是筛选栏相关的东西 ==============================  */
  /** 标示是否正在请求数据 */
  private boolean isRequestBrandData = false;

  public String BRAND_TAG;
  private String SERIES_TAG;
  private String SORT_TAG;
  private String PRICE_TAG;
  private String MORE_TAG;

  private int mFrameLayoutId = R.id.frame_core_filter;
  private String host;

  private void initTags(String host) {
    this.host = host;
    BRAND_TAG = host + BrandFragment.class.getName();
    SERIES_TAG = host + CarSeriesFragment.class.getName();
    SORT_TAG = host + SortFilterFragment.class.getName();
    PRICE_TAG = host + PriceFilterFragment.class.getName();
    MORE_TAG = host + MoreFilterFragment.class.getName();
  }

  private void requestSupportBrands() {
    if (!HCUtils.isNetAvailable()) {
      HCUtils.showToast(R.string.hc_net_unreachable);
      isRequestBrandData = false;
      return;
    }
    Map<String, Object> params = HCParamsUtil.getSupportBrand();
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleSupportBrands(responseJsonString);
      }
    }));
  }

  private void handleSupportBrands(String responseJsonString) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      List<HCBrandEntity> brands = HCJSONParser.parseBrand(responseJsonString);
      HCDbUtil.updateBrand(brands);
      HCSpUtils.setLastCityForBrand(HCDbUtil.getSavedCityId() + "");
      isRequestBrandData = false;
      removeBrandFragment();
      showBrandFragment();
    }
  }

  private void removeBrandFragment() {
    try {
      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      BrandFragment brandFragment;
      if (getSupportFragmentManager().findFragmentByTag(BRAND_TAG) != null) {
        brandFragment = (BrandFragment) getSupportFragmentManager().findFragmentByTag(BRAND_TAG);
        mTransaction.remove(brandFragment);
      }
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    } catch (Exception e) {
      HCLog.d(TAG, "removeBrandFragment is crash ...");
    }
  }

  /** 显示品牌选择Fragment */
  public void showBrandFragment() {
    //如果当前正在请求次brandFragment的data，返回
    if (isRequestBrandData) return;
    hideSortFragment();
    hidePriceFragment();
    hideMoreFragment();
    String cityId = HCSpUtils.getLastCityForBrand();
    if (TextUtils.isEmpty(cityId) || !cityId.equals(HCDbUtil.getSavedCityId() + "")) {
      isRequestBrandData = true;
      requestSupportBrands();
    } else {
      try {
        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
        BrandFragment brandFragment;
        if (getSupportFragmentManager().findFragmentByTag(BRAND_TAG) == null) {
          brandFragment = BrandFragment.newInstance();
          brandFragment.setHost(host);
          mTransaction.add(mFrameLayoutId, brandFragment, BRAND_TAG);
        } else {
          brandFragment = (BrandFragment) getSupportFragmentManager().findFragmentByTag(BRAND_TAG);
          brandFragment.setHost(host);
          mTransaction.show(brandFragment);
        }
        mTransaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
        brandFragment.showDimView();
      } catch (Exception e) {
        HCLog.d(TAG, "showBrandFragment is crash ...");
      }
    }
  }

  private void hideBrandFragment() {
    hideCarSeriesFragment();
    if (getSupportFragmentManager().findFragmentByTag(BRAND_TAG) != null) {
      try {
        Fragment brandFragment = getSupportFragmentManager().findFragmentByTag(BRAND_TAG);
        if (brandFragment != null && brandFragment instanceof BrandFragment) {
          BrandFragment bf = (BrandFragment) brandFragment;
          bf.hideDimView();
        }
        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
        mTransaction.hide(brandFragment);
        mTransaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
      } catch (Exception e) {
        HCLog.d(TAG, "hideBrandFragment is crash ...");
      }
    }
  }

  private void showCarSeriesFragment(HCBrandEntity mBrand) {
    try {
      int brand_id = mBrand.getBrand_id();
      String brand_name = mBrand.getBrand_name();
      List<SeriesEntity> seriesData = SeriesDAO.getInstance().findSeriesById(mBrand.getSeries());
      FragmentManager mManager = getSupportFragmentManager();
      FragmentTransaction mTransaction = mManager.beginTransaction();
      CarSeriesFragment showCariesFragment;
      showCariesFragment = CarSeriesFragment.newInstance();
      showCariesFragment.setHost(host);
      mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
      if (!showCariesFragment.isAdded()) {
        mTransaction.add(mFrameLayoutId, showCariesFragment, SERIES_TAG);
      }

      mTransaction.show(showCariesFragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
      showCariesFragment.setCarSeries(brand_id, brand_name, seriesData);
    } catch (Exception e) {
      HCLog.d(TAG, "showCarSeriesFragment is crash ...");
    }
  }

  private void hideCarSeriesFragment() {
    if (getSupportFragmentManager().findFragmentByTag(SERIES_TAG) != null) {
      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
      Fragment fragment = getSupportFragmentManager().findFragmentByTag(SERIES_TAG);
      mTransaction.hide(fragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    }
  }

  /** 显示排序的Fragment */
  private void showSortFragment() {
    hideBrandFragment();
    hidePriceFragment();
    hideMoreFragment();
    FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
    mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
    SortFilterFragment sortFilterFragment;
    if (getSupportFragmentManager().findFragmentByTag(SORT_TAG) == null) {
      sortFilterFragment = SortFilterFragment.newInstance();
      sortFilterFragment.setHost(host);
      mTransaction.add(mFrameLayoutId, sortFilterFragment, SORT_TAG);
    } else {
      sortFilterFragment =
          (SortFilterFragment) getSupportFragmentManager().findFragmentByTag(SORT_TAG);
      sortFilterFragment.setHost(host);
      mTransaction.show(sortFilterFragment);
    }
    mTransaction.commitAllowingStateLoss();
    getSupportFragmentManager().executePendingTransactions();
    sortFilterFragment.showDimView();
  }

  private void hideSortFragment() {
    if (getSupportFragmentManager().findFragmentByTag(SORT_TAG) != null) {
      Fragment sortFragment = getSupportFragmentManager().findFragmentByTag(SORT_TAG);

      if (sortFragment != null && sortFragment instanceof SortFilterFragment) {
        SortFilterFragment sf = (SortFilterFragment) sortFragment;
        sf.hideDimView();
      }

      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
      mTransaction.hide(sortFragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    }
  }

  /** 显示价格的Fragment */
  private void showPriceFragment() {
    hideSortFragment();
    hideBrandFragment();
    hideMoreFragment();
    FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
    mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
    PriceFilterFragment priceFilterFragment;
    if (getSupportFragmentManager().findFragmentByTag(PRICE_TAG) == null) {
      priceFilterFragment = PriceFilterFragment.newInstance();
      priceFilterFragment.setHost(host);
      mTransaction.add(mFrameLayoutId, priceFilterFragment, PRICE_TAG);
    } else {
      priceFilterFragment =
          (PriceFilterFragment) getSupportFragmentManager().findFragmentByTag(PRICE_TAG);
      priceFilterFragment.setHost(host);
      mTransaction.show(priceFilterFragment);
    }
    mTransaction.commitAllowingStateLoss();
    getSupportFragmentManager().executePendingTransactions();
    priceFilterFragment.showDimView();
  }

  private void hidePriceFragment() {
    if (getSupportFragmentManager().findFragmentByTag(PRICE_TAG) != null) {
      Fragment priceFragment = getSupportFragmentManager().findFragmentByTag(PRICE_TAG);

      if (priceFragment != null && priceFragment instanceof PriceFilterFragment) {
        PriceFilterFragment pf = (PriceFilterFragment) priceFragment;
        pf.hideDimView();
      }

      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
      mTransaction.hide(priceFragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    }
  }

  /** 显示更多的Fragment */
  private void showMoreFragment() {
    hideSortFragment();
    hidePriceFragment();
    hideBrandFragment();
    FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
    mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
    MoreFilterFragment moreFilterFragment;
    if (getSupportFragmentManager().findFragmentByTag(MORE_TAG) == null) {
      moreFilterFragment = MoreFilterFragment.newInstance();
      moreFilterFragment.setHost(host);
      mTransaction.add(mFrameLayoutId, moreFilterFragment, MORE_TAG);
    } else {
      moreFilterFragment =
          (MoreFilterFragment) getSupportFragmentManager().findFragmentByTag(MORE_TAG);
      moreFilterFragment.setHost(host);
      mTransaction.show(moreFilterFragment);
    }
    mTransaction.commitAllowingStateLoss();
    getSupportFragmentManager().executePendingTransactions();
    moreFilterFragment.showDimView();
    //只要打开该界面，就发送通知到MoreFilterFragment去请求数据
    HCEvent.postEvent(HCEvent.ACTION_SHOW_MORE, host);
  }

  private void hideMoreFragment() {
    if (getSupportFragmentManager().findFragmentByTag(MORE_TAG) != null) {
      Fragment moreFragment = getSupportFragmentManager().findFragmentByTag(MORE_TAG);

      if (moreFragment != null && moreFragment instanceof MoreFilterFragment) {
        MoreFilterFragment mf = (MoreFilterFragment) moreFragment;
        mf.hideDimView();
      }

      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
      mTransaction.hide(moreFragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    }
  }

  private void removeAllFilterFragment() {
    hideCarSeriesFragment();
    hideBrandFragment();
    hideSortFragment();
    hidePriceFragment();
    hideMoreFragment();
  }

  private void handleFilterOpenAndCloseEvent(HCCommunicateEntity communicateEntity) {
    String action = communicateEntity.getAction();
    switch (action) {
      case HCEvent.ACTION_SHOW_BRAND_FRAGMENT:
        if (!TextUtils.isEmpty(communicateEntity.getStrValue())) {
          initTags(communicateEntity.getStrValue());
        }
        showBrandFragment();
        break;
      case HCEvent.ACTION_HIDE_BRAND_FRAGMENT:
        hideBrandFragment();
        break;

      case HCEvent.ACTION_SHOW_SORT_FRAGMENT:
        if (!TextUtils.isEmpty(communicateEntity.getStrValue())) {
          initTags(communicateEntity.getStrValue());
        }
        showSortFragment();
        break;
      case HCEvent.ACTION_HIDE_SORT_FRAGMENT:
        hideSortFragment();
        break;
      case HCEvent.ACTION_SHOW_PRICE_FRAGMENT:
        if (!TextUtils.isEmpty(communicateEntity.getStrValue())) {
          initTags(communicateEntity.getStrValue());
        }
        showPriceFragment();
        break;
      case HCEvent.ACTION_HIDE_PRICE_FRAGMENT:
        hidePriceFragment();
        break;

      case HCEvent.ACTION_SHOW_MORE_FRAGMENT:
        if (!TextUtils.isEmpty(communicateEntity.getStrValue())) {
          initTags(communicateEntity.getStrValue());
        }
        showMoreFragment();
        break;
      case HCEvent.ACTION_HIDE_MORE_FRAGMENT:
        hideMoreFragment();
        break;
      case HCEvent.ACTION_IS_NEED_DORECT:
        removeAllFilterFragment();
        break;
    }
  }

  private void handleFilterEvent(HCCommunicateEntity communicateEntity) {
    if (communicateEntity != null) {
      String action = communicateEntity.getAction();
      String brandAction = host + HCEvent.ACTION_BRAND_CHOOSED;
      String seriesReturnAction = host + HCEvent.ACTION_CAR_SERIES_CHOOSED_RETURN;
      String sortAction = host + HCEvent.ACTION_SORT_CHOOSED;
      String priceAction = host + HCEvent.ACTION_PRICE_CHOOSED;
      String moreAction = host + HCEvent.ACTION_MORE_CHOOSED;

      if (action.equals(brandAction)) {
        HCEvent.cancelDelivery(communicateEntity);
        //品牌选择
        Object mBrand = communicateEntity.getObjValue();
        if (mBrand == null) {
          FilterUtils.saveBrandFilterTerm(host, 0, 0);
          hideBrandFragment();
          delayedNotify();
        } else {
          HCBrandEntity convertBrand = (HCBrandEntity) mBrand;
          showCarSeriesFragment(convertBrand);
        }
      } else if (action.equals(seriesReturnAction)) {
        HCEvent.cancelDelivery(communicateEntity);
        //车系选择返回
        hideCarSeriesFragment();
        Object objValue = communicateEntity.getObjValue();
        if (objValue != null) {
          hideBrandFragment();
          SeriesEntity seriesEntity = (SeriesEntity) objValue;
          int brand_id = seriesEntity.getBrand_id();
          int series_id = seriesEntity.getId();
          FilterUtils.saveBrandFilterTerm(host, brand_id, series_id);
          HCEvent.postEvent(host + HCEvent.ACTION_BRAND_CHOOSED_CHANGE);
          delayedNotify();
        }
      } else if (action.equals(sortAction)) {
        hideSortFragment();
        HCEvent.postEvent(HCEvent.ACTION_RESET_FILTERBAR_COLOR);
      } else if (action.equals(priceAction)) {
        HCEvent.cancelDelivery(communicateEntity);
        hidePriceFragment();
        delayedNotify();
      } else if (action.equals(moreAction)) {
        HCEvent.cancelDelivery(communicateEntity);
        hideMoreFragment();
        delayedNotify();
      }
    }
  }

  private void delayedNotify() {
    HCEvent.postEvent(HCEvent.ACTION_RESET_FILTERBAR_COLOR);
    //通知筛选条件变化了
    HCEvent.postEvent(host + HCEvent.ACTION_FILTER_CHANGED);
  }
}
