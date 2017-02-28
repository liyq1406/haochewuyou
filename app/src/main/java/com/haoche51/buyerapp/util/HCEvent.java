package com.haoche51.buyerapp.util;

import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import de.greenrobot.event.EventBus;

public class HCEvent {

  public static final int PRIORITY_10 = 10;
  public static final int PRIORITY_9 = 9;
  public static final int PRIORITY_8 = 8;
  public static final int PRIORITY_7 = 7;
  public static final int PRIORITY_6 = 6;
  public static final int PRIORITY_5 = 5;
  public static final int PRIORITY_4 = 4;
  public static final int PRIORITY_3 = 3;
  public static final int PRIORITY_2 = 2;
  public static final int PRIORITY_1 = 1;

  public static final int PRIORITY_NONAL = PRIORITY_2;

  public static final int PRIORITY_HIGH = PRIORITY_10;

  public static final int PRIORITY_CORE = PRIORITY_8;

  //-----------------HomePage --> MainActivity -------------------//
  /** 首页 --> 全部好车 */
  public static final String ACTION_HOME_TO_MAINACT_GOOD_VEHICLES = "homeToMainGoodVehicles";

  /** 首页 --> 直营店 */
  public static final String ACTION_HOME_TO_MAINACT_DIRECT_VEHICLES = "homeToMainDirectVehicles";

  /** 首页 --> 今日新上 */
  public static final String ACTION_HOME_TO_MAINACT_TODAY_VEHICLES = "homeToMainTodayVehicles";

  //-----------------HomePage --> MainActivity -------------------//

  //-----------------MainActivity --> CoreFragment -------------------//
  /** 主Activity跳转到CoreFragment */
  public static final String ACTION_MAINACT_TO_CORE = "mainActToCore";
  public static final String ACTION_MAINACT_SEARCH_TO_CORE = "mainActSearchToCore";
  //-----------------MainActivity --> CoreFragment -------------------//

  /** 通知开始加载首页 */
  public static final String ACTION_NOW_LOADED_HOME_PAGE = "nowNeedLoadHomePage";

  /** 通知是否显示直营店 */
  public static final String ACTION_IS_NEED_DORECT = "isNeedDirect";

  /** 通知是否有限时特价活动 */
  public static final String ACTION_IS_LIMIT_ACTIVITY = "isLimitActivity";

  /** 通知是否有直播 */
  public static final String ACTION_IS_HAS_LIVE = "isHasLive";

  /** 通知是否重新加载全部好车页面 */
  public static final String ACTION_IS_NEED_INIT_ALLGOOD = "isNeedInitAllGood";

  //-----------------CoreFragment --> child -------------------//
  public static final String ACTION_CORE_TO_CHILD_REFRESH = "coreToChildRefresh";
  //-----------------CoreFragment --> child -------------------//

  /** 通知筛选条件变化了 */
  public static final String ACTION_FILTER_CHANGED = "hcFilterChanged";

  /** 通知筛选栏被点击 */
  public static final String ACTION_BRAND_CHOOSED = "hcOnBrandChoosed";

  public static final String ACTION_CAR_SERIES_CHOOSED_RETURN = "hconCarSeriesChooseReturn";

  public static final String ACTION_SORT_CHOOSED = "onSortChoosed";

  public static final String ACTION_PRICE_CHOOSED = "hcOnPriceChoosed";

  public static final String ACTION_MORE_CHOOSED = "hcOnMoreChoosed";

  public static final String ACTION_SORT_CHOOSED_CHANGE = "hcOnSortChoosedChanged";

  public static final String ACTION_BRAND_CHOOSED_CHANGE = "hcOnBrandChoosedChanged";

  public static final String ACTION_PRICE_CHOOSED_CHANGE = "hcOnPriceChoosedChanged";

  public static final String ACTION_MORE_CHOOSED_CHANGE = "hcOnMoreChoosedChanged";

  public static final String ACTION_RESET_PRICE_BAR = "hcActionResetPriceBar";

  public static final String ACTION_SHOW_MORE = "hcOnShowMore";

  /** 通知展开折叠筛选 */
  public static final String ACTION_SHOW_SORT_FRAGMENT = "hcShowSortFragment";
  public static final String ACTION_SHOW_BRAND_FRAGMENT = "hcShowBrandFragment";
  public static final String ACTION_SHOW_PRICE_FRAGMENT = "hcShowPriceFragment";
  public static final String ACTION_SHOW_MORE_FRAGMENT = "hcShowMoreFragment";
  public static final String ACTION_HIDE_SORT_FRAGMENT = "hcHideSortFragment";
  public static final String ACTION_HIDE_BRAND_FRAGMENT = "hcHideBrandFragment";
  public static final String ACTION_HIDE_PRICE_FRAGMENT = "hcHidePriceFragment";
  public static final String ACTION_HIDE_MORE_FRAGMENT = "hcHideMoreFragment";

  public static final String ACTION_GOTO_MANAGER_CLICK = "onGotoMyManagerClick";

  public static final String ACTION_MY_SUBITEM_CLICK = "onMySubscribeItemClick";

  /** 订阅被删除状态变化 */
  public static final String ACTION_SUBSCRIBE_CHANGED = "userSubScribeVehicleChanged";
  public static final String ACTION_ALL_GOOD_FILTER_CHANGED = "userSubScribeVehicleChangedForAllGood";

  /** 收藏状态变化 */
  public static final String ACTION_COLLECTION_CHANGED = "userCollectionVehicleChanged";

  /** 用户登陆状态变化 */
  public static final String ACTION_LOGINSTATUS_CHANGED = "userLoginStatusChanged";

  /** 用户切换到我的页面 */
  public static final String ACTION_CHANGED_TO_PROFILE = "userChangedToProfile";

  /** 首页跳转,到MainActivity,然后再到searchActivity */
  public static final String ACTION_GO_SEARCH = "onGoSearchCalled";

  /** 城市选择改变 action */
  public static final String ACTION_CITYCHANGED = "citychanged";

  /** 搜索返回切换回全部好车 */
  public static final String ACTION_SEARCH_RETURN_TO_ALL_VEHICLE = "searchReturnToAllVehicle";

  /** 回到看过的车页面 */
  public static final String ACTION_SWAPTO_CORE_INNER = "NowInCoreInner";

  /** 通知筛选组件重新设置filterbar */
  public static final String ACTION_RESET_FILTERBAR_COLOR = "resetFilterBarColor";

  /** 重复点击底部tab */
  public static final String ACTION_DUPLICATE_CLICK_TAB = "duplicateClickTab";

  public static final String ACTION_SOLD_DIALOG_CLICK = "soldDialogClick";

  //---------------------remider------------------------//

  /**
   * 意见反馈广播action
   */
  public static final String ACTION_FEEDBACK_REMINDER = "showFeedbackReminder";

  /**
   * 我的优惠券广播action
   */
  public static final String ACTION_COUPON_REMINDER = "showCouponReminder";

  /**
   * 订阅车源个数广播action
   */
  public static final String ACTION_SUBCOUNTS_REMINDER = "showSubcountsReminder";

  /** 收藏action */
  public static final String ACTION_COLLECTION_REMINDER = "showCollectionReminder";

  /** 预定action(已预定,已预约) */
  public static final String ACTION_BOOKING_REMINDER = "showBookingReminder";

  public static final String ACTION_HOME_SUB_REMINDER_CHANGED = "homeSubReminderChanged";

  public static final String ACTION_PROFILE_REMINDER_CHANGED = "profileReminderChanged";

  /** 通知splash的body图片加载完成 */
  public static final String ACTION_SPLASH_BODY_LOADED = "splashBodyLoaded";

  /** 通知splash的foot图片加载完成 */
  public static final String ACTION_SPLASH_FOOT_LOADED = "splashFootLoaded";

  /** 通知WebBroserActivity已经初始化完毕 */
  public static final String ACTION_WEBBROWSER_LOADED = "webBrowserOnResumeNow";

  //---------------------remider------------------------//

  public static void postEvent(HCCommunicateEntity entity) {
    EventBus.getDefault().post(entity);
  }

  public static void postEvent(String action) {
    HCCommunicateEntity entity = new HCCommunicateEntity(action);
    postEvent(entity);
  }

  public static void postEvent(String action, Object obj) {
    HCCommunicateEntity entity = new HCCommunicateEntity(action, obj);
    postEvent(entity);
  }

  public static void postEvent(String action, String strValue) {
    HCCommunicateEntity entity = new HCCommunicateEntity(action, strValue);
    postEvent(entity);
  }

  public static void postEvent(String action, int intValue) {
    HCCommunicateEntity entity = new HCCommunicateEntity(action, intValue);
    postEvent(entity);
  }

  public static void postEvent(String action, int intValue, Object obj) {
    HCCommunicateEntity entity = new HCCommunicateEntity(action, intValue);
    entity.setObjValue(obj);
    postEvent(entity);
  }

  public static void postStickyEvent(HCCommunicateEntity entity) {
    EventBus.getDefault().postSticky(entity);
  }

  public static void cancelDelivery(HCCommunicateEntity communicateEntity) {
    EventBus.getDefault().cancelEventDelivery(communicateEntity);
  }

  public static void register(Object subsriber) {
    if (subsriber == null) return;

    if (!EventBus.getDefault().isRegistered(subsriber)) {
      EventBus.getDefault().register(subsriber, PRIORITY_NONAL);
    }
  }

  public static void register(Object subscriber, int priority) {
    if (subscriber == null) return;

    if (!EventBus.getDefault().isRegistered(subscriber)) {
      EventBus.getDefault().register(subscriber, priority);
    }
  }

  public static void unRegister(Object subscribe) {
    if (subscribe == null) return;

    if (EventBus.getDefault().isRegistered(subscribe)) {
      EventBus.getDefault().unregister(subscribe);
    }
  }
}
