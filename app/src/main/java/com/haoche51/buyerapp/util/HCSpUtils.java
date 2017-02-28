package com.haoche51.buyerapp.util;

import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.entity.HCBrandEntity;
import com.haoche51.buyerapp.entity.HCCityEntity;
import com.haoche51.buyerapp.entity.HCHomeLiveEntity;
import com.haoche51.buyerapp.entity.SplashDataEntity;
import com.haoche51.buyerapp.entity.SubConditionDataEntity;
import java.util.ArrayList;
import java.util.List;

public class HCSpUtils {

  private static Gson mGson = new Gson();

  private static final String SHAREDPREFERENCE_NAME = "haoche51buyerapp";

  /** 客服热线 */
  private static final String KEFU_PHONE = "kefudianhua";

  public static void saveKefuPhone(String phone) {
    saveData(KEFU_PHONE, phone);
  }

  public static String getKefuPhone() {
    return getDefaultString(KEFU_PHONE);
  }

  /**
   * 标记 profile意见反馈提醒
   */
  private final static String REMINDER_FOR_FEEDBACK = "hcreminderForFeedback";
  /**
   * 标记 profile我的优惠券提醒
   */
  private final static String REMINDER_FOR_COUPON = "hcreminderForCoupon";

  /**
   * 标记profile我的订阅红点
   */
  private final static String REMINDER_FOR_SUBSCRIBE = "hcreminderForSubscribe";

  /** 标记profile收藏红点 */
  private final static String REMINDER_FOR_COLLECTION = "hcreminderForCollection";

  /** 标记profile预定红点 */
  private final static String REMINDER_FOR_BOOKING = "hcreminderForBooking";

  /**
   * 记录用户id
   */
  private final static String USER_UID = "hcUserUid";

  /**
   * 记录上一次检查用户优惠券时间
   */
  private final static String LAST_CHECK_COUPON_TIME = "lastCheckCouponTime";

  /**
   * 记录上一次进入订阅列表的时间
   */

  private final static String LAST_ENTER_SUBSCRIBE_LIST_TIME = "lastEnterSubscribeListTime";

  /**
   * 存储订阅条件
   */
  public static final String SPKEY_ALLSUBCRIBES = "hcAllSubcribes";

  /** 绑定百度推送返回的channelId */
  public static final String BAIDU_CHANNEL_ID = "baidu_channelId";

  /** 绑定百度推送返回的channelId */
  public static final String BAIDU_USER_ID = "baidu_userId";

  public static void saveBDUserId(String userId) {
    saveData(BAIDU_USER_ID, userId);
  }

  public static String getBDUserId() {
    return getDefaultString(BAIDU_USER_ID);
  }

  public static void saveBDChannelId(String channelId) {
    saveData(BAIDU_CHANNEL_ID, channelId);
  }

  public static String getBDChannelId() {
    return getDefaultString(BAIDU_CHANNEL_ID);
  }

  /** 绑定小米推送返回 clientId */
  public static final String XIAOMI_CLIENT_ID = "xiaomi_clientId";

  public static void saveXMClientId(String clientId) {
    saveData(XIAOMI_CLIENT_ID, clientId);
  }

  public static String getXMClientId() {
    return getDefaultString(XIAOMI_CLIENT_ID);
  }

  /** 标记是否导入过车系表数 */
  public static final String HAS_IMPORT_SERIES_TABLE = "has_imported_series_table";

  public static boolean hasImportedcariesTable() {
    return getDefaultBoolean(HAS_IMPORT_SERIES_TABLE);
  }

  public static void setImportedcariesTable() {
    saveData(HAS_IMPORT_SERIES_TABLE, true);
  }

  /** 标记是否导入过品牌表数据 */
  public static final String HAS_IMPORT_BRAND_TABLE = "has_imported_brand_table";

  public static boolean hasImportedbrandTable() {
    return getDefaultBoolean(HAS_IMPORT_BRAND_TABLE);
  }

  public static void setImportedbrandTable() {
    saveData(HAS_IMPORT_BRAND_TABLE, true);
  }

  /** 获取指定key的值,找不到返回空字符串 */
  public static String getDefaultString(String key) {
    return getString(key, "");
  }

  //public static long getDefaultLong(String key) {
  //  return getLong(key, 0L);
  //}

  public static String getString(String key, String defaultValue) {
    return getHCSP().getString(key, defaultValue);
  }

  /***
   * 获取指定key的boolean值,默认返回false
   */
  public static boolean getDefaultBoolean(String key) {
    return getBoolean(key, false);
  }

  public static int getDefaultInt(String key) {
    return getInt(key, -1);
  }

  public static boolean getBoolean(String key, boolean defaultValue) {
    return getHCSP().getBoolean(key, defaultValue);
  }

  public static int getInt(String key, int defaultValue) {
    return getHCSP().getInt(key, defaultValue);
  }

  //public static long getLong(String key, long defaultValue) {
  //  return getHCSP().getLong(key, defaultValue);
  //}

  private static SharedPreferences getHCSP() {
    return GlobalData.mContext.getSharedPreferences(SHAREDPREFERENCE_NAME, 0);
  }

  public static void saveData(String key, Object value) {
    SharedPreferences sp = getHCSP();
    SharedPreferences.Editor editor = sp.edit();
    if (value instanceof String) {
      editor.putString(key, value.toString());
    } else if (value instanceof Boolean) {
      editor.putBoolean(key, (Boolean) value);
    } else if (value instanceof Float) {
      editor.putFloat(key, HCUtils.str2Float(value.toString()));
    } else if (value instanceof Integer) {
      editor.putInt(key, HCUtils.str2Int(value.toString()));
    } else if (value instanceof Long) {
      editor.putLong(key, Long.parseLong(value.toString()));
    } else {
      editor.putString(key, value.toString());
    }
    editor.apply();
  }

  // -------------------profile reminder status--------------------------//

  public static void setProfileFeedbackReminder(int feedbackCounts) {
    getHCSP().edit().putInt(REMINDER_FOR_FEEDBACK, feedbackCounts).apply();
  }

  public static int getProfileFeedbackReminder() {
    return getHCSP().getInt(REMINDER_FOR_FEEDBACK, 0);
  }

  public static void setProfileCouponReminder(int couponCounts) {
    getHCSP().edit().putInt(REMINDER_FOR_COUPON, couponCounts).apply();
  }

  public static void setProfileCollectionReminder(int count) {
    getHCSP().edit().putInt(REMINDER_FOR_COLLECTION, count).apply();
  }

  public static int getProfileCollectionReminder() {
    return getDefaultInt(REMINDER_FOR_COLLECTION);
  }

  public static void setProfileBookingReminder(int count) {
    getHCSP().edit().putInt(REMINDER_FOR_BOOKING, count).apply();
  }

  public static int getProfileBookingReminder() {
    return getDefaultInt(REMINDER_FOR_BOOKING);
  }

  public static int getProfileCouponReminder() {
    return getHCSP().getInt(REMINDER_FOR_COUPON, 0);
  }

  public static int getProfileSubscribeReminder() {
    return getHCSP().getInt(REMINDER_FOR_SUBSCRIBE, 0);
  }

  public static void setProfileSubscribeReminder(int subscribeCounts) {
    getHCSP().edit().putInt(REMINDER_FOR_SUBSCRIBE, subscribeCounts).apply();
  }

  public static int getAllReminderCounts() {
    return getProfileCouponReminder() + getProfileSubscribeReminder();
  }

  // -------------------profile reminder status--------------------------//

  /** 记录用户手机号码 */
  private final static String USER_PHONE = "hcUserPhone";

  public static void setUserPhone(String phone) {
    getHCSP().edit().putString(USER_PHONE, phone).apply();
  }

  public static String getUserPhone() {
    return getHCSP().getString(USER_PHONE, "");
  }

  public static String getUserHintPhone() {
    String phone = getUserPhone();
    if (!TextUtils.isEmpty(phone)) {
      String start = phone.substring(0, 3);
      String end = phone.substring(7);
      phone = start + "****" + end;
    }
    return phone;
  }

  public static String getUserUid() {
    return getHCSP().getString(USER_UID, "");
  }

  public static void setUserUid(String uid) {
    getHCSP().edit().putString(USER_UID, uid).apply();
  }

  public static String getLastCheckCouponTime() {
    String defValue = "0";
    return getHCSP().getString(LAST_CHECK_COUPON_TIME, defValue);
  }

  public static void setLastCheckCouponTimeToNow() {
    String now = String.valueOf(HCUtils.now() / 1000);
    getHCSP().edit().putString(LAST_CHECK_COUPON_TIME, now).apply();
  }

  public static String getLastEnterSubscribeListTime() {
    String defValue = String.valueOf(System.currentTimeMillis() / 1000);
    return getHCSP().getString(LAST_ENTER_SUBSCRIBE_LIST_TIME, defValue);
  }

  public static void setLastEnterSubscribeListTimeToNow() {
    String now = String.valueOf(System.currentTimeMillis() / 1000);
    getHCSP().edit().putString(LAST_ENTER_SUBSCRIBE_LIST_TIME, now).apply();
  }

  /** 记录推广接口返回的id,如果两次不一致才显示并加载图 */
  public static final String PROMOTE_ID = "hc_promote_id";

  public static void setPromoteId(int id) {
    getHCSP().edit().putInt(PROMOTE_ID, id).apply();
  }

  public static int getPromoteId() {
    return getDefaultInt(PROMOTE_ID);
  }

  /** 记录splash foot */
  private final static String SPLASH_FOOT = "splashFootData";

  /** 记录splash body */
  private final static String SPLASH_BODY = "splashBodyData";

  /** 记录未完成的splash foot */
  private final static String UN_LOADED_SPLASH_FOOT = "unLoadedsplashFootData";

  /** 记录未完成的splash body */
  private final static String UN_LOADED_SPLASH_BODY = "unLoadedsplashBodyData";

  public static void setSplashBodyEntity(SplashDataEntity entity) {
    if (entity != null) {
      String jsonStr = mGson.toJson(entity);
      getHCSP().edit().putString(SPLASH_BODY, jsonStr).apply();
    }
  }

  public static SplashDataEntity getSplashBodyEntity() {
    String jsonStr = getDefaultString(SPLASH_BODY);
    if (TextUtils.isEmpty(jsonStr)) {
      return new SplashDataEntity();
    } else {
      return mGson.fromJson(jsonStr, SplashDataEntity.class);
    }
  }

  /** 记录首页问答弹窗type */
  private final static String HOME_QUIZ_DIALOG = "homeQuizDialog";

  public static void setHomeQuizDialog(int type) {
    if (type != 0) {
      getHCSP().edit().putInt(HOME_QUIZ_DIALOG, type).apply();
    }
  }

  public static int getHomeQuizDialog() {
    return getHCSP().getInt(HOME_QUIZ_DIALOG, 0);
  }

  public static void setUnLoadedSplashBodyEntity(SplashDataEntity entity) {
    if (entity != null) {
      String jsonStr = mGson.toJson(entity);
      getHCSP().edit().putString(UN_LOADED_SPLASH_BODY, jsonStr).apply();
    }
  }

  public static SplashDataEntity getUnLoadedSplashBodyEntity() {
    String jsonStr = getDefaultString(UN_LOADED_SPLASH_BODY);
    if (TextUtils.isEmpty(jsonStr)) {
      return new SplashDataEntity();
    } else {
      return mGson.fromJson(jsonStr, SplashDataEntity.class);
    }
  }

  public static void setSplashFootEntity(SplashDataEntity entity) {
    if (entity != null) {
      String jsonStr = mGson.toJson(entity);
      getHCSP().edit().putString(SPLASH_FOOT, jsonStr).apply();
    }
  }

  public static SplashDataEntity getSplashFootEntity() {
    String jsonStr = getDefaultString(SPLASH_FOOT);
    if (TextUtils.isEmpty(jsonStr)) {
      return new SplashDataEntity();
    } else {
      return mGson.fromJson(jsonStr, SplashDataEntity.class);
    }
  }

  public static void setUnLoadedSplashFootEntity(SplashDataEntity entity) {
    if (entity != null) {
      String jsonStr = mGson.toJson(entity);
      getHCSP().edit().putString(UN_LOADED_SPLASH_FOOT, jsonStr).apply();
    }
  }

  public static SplashDataEntity getUnLoadedSplashFootEntity() {
    String jsonStr = getDefaultString(UN_LOADED_SPLASH_FOOT);
    if (TextUtils.isEmpty(jsonStr)) {
      return new SplashDataEntity();
    } else {
      return mGson.fromJson(jsonStr, SplashDataEntity.class);
    }
  }

  /** 在sharedPreference中记录最后一次操作(home_brand,home_price,home_search) */
  private static String LAST_STATISTIC_CORE_OPERATION = "statisticCoreOperation";

  public static void setLastCoreOperation(String str) {
    getHCSP().edit().putString(LAST_STATISTIC_CORE_OPERATION, str).apply();
  }

  public static String getLastCoreOperation() {
    String result = getDefaultString(LAST_STATISTIC_CORE_OPERATION);
    if (TextUtils.isEmpty(result)) {
      result = "0";
    }
    return result;
  }

  /** ------------------------ 订阅相关 ---------------------- */

  public static void saveSubscribe(SubConditionDataEntity entity) {
    List<SubConditionDataEntity> lists = getAllSubscribe();
    if (!lists.contains(entity)) {
      lists.add(entity);
    } else {
      lists.remove(entity);
      lists.add(entity);
    }
    setAllSubscribe(lists);
  }

  public static void removeSubscribe(SubConditionDataEntity entity) {
    List<SubConditionDataEntity> lists = getAllSubscribe();

    if (lists.contains(entity)) {
      lists.remove(entity);
      setAllSubscribe(lists);
    }
  }

  private final static String HOME_HOTS_BRANDS = "homeHotsBrands";

  public static void setHotsBrands(List<HCBrandEntity> brands) {

    if (HCUtils.isListEmpty(brands)) return;

    String str = mGson.toJson(brands, new TypeToken<List<HCBrandEntity>>() {
    }.getType());

    saveData(HOME_HOTS_BRANDS, str);
  }

  public static List<HCBrandEntity> getHotsBrands() {
    List<HCBrandEntity> list;
    String str = getDefaultString(HOME_HOTS_BRANDS);

    list = mGson.fromJson(str, new TypeToken<List<HCBrandEntity>>() {
    }.getType());

    return list == null ? new ArrayList<HCBrandEntity>() : list;
  }

  public static void setAllSubscribe(List<SubConditionDataEntity> lists) {
    String saveStr = "";
    if (!HCUtils.isListEmpty(lists)) {
      saveStr = mGson.toJson(lists, new TypeToken<List<SubConditionDataEntity>>() {
      }.getType());
    }

    saveData(SPKEY_ALLSUBCRIBES, saveStr);
  }

  public static List<SubConditionDataEntity> getAllSubscribe() {
    String str = getDefaultString(SPKEY_ALLSUBCRIBES);
    ArrayList<SubConditionDataEntity> lists;
    if (str.isEmpty()) {
      lists = new ArrayList<>();
    } else {
      lists = mGson.fromJson(str, new TypeToken<List<SubConditionDataEntity>>() {
      }.getType());
    }
    return lists;
  }

  /** ------------------------ 订阅相关 ---------------------- */

  //---------------------------normal sth------------------------------//

  //--------------------搜索热词推荐缓存-------------------//
  /** 搜索热词推荐 */
  private final static String HOT_KEY_SEARCH = "hotKeySearch";

  public static String getHotKeyFromCache() {
    return getDefaultString(HOT_KEY_SEARCH);
  }

  public static void setHotKeySearch(String resp) {
    getHCSP().edit().putString(HOT_KEY_SEARCH, resp).apply();
  }

  //--------------------搜索历史记录缓存-------------------//
  /** 搜索历史记录 */
  private final static String SEARCH_HISTORY = "searchHistory";

  private static void setSearchHistory(List<String> historyData) {
    if (historyData == null) return;
    String saveStr = mGson.toJson(historyData, new TypeToken<List<String>>() {
    }.getType());
    getHCSP().edit().putString(SEARCH_HISTORY, saveStr).apply();
  }

  public static List<String> getSearchHistory() {
    String str = getDefaultString(SEARCH_HISTORY);
    ArrayList<String> lists;
    if (TextUtils.isEmpty(str)) {
      lists = new ArrayList<>();
    } else {
      lists = mGson.fromJson(str, new TypeToken<List<String>>() {
      }.getType());
    }
    return lists;
  }

  public static void saveHistory(String history) {

    if (TextUtils.isEmpty(history)) return;

    List<String> lists = getSearchHistory();

    if (lists.contains(history)) {
      lists.remove(history);
    }

    lists.add(0, history);

    //判断最多缓存20条
    if (lists.size() > 20) {
      lists = lists.subList(0, 20);
    }

    setSearchHistory(lists);
  }

  //-----------------出售爱车缓存------------------//
  /** 出售爱车页面服务数据统计 */
  private final static String HC_SALE_SERVICE = "hc_sale_service";

  public static String getSaleService() {
    return getDefaultString(HC_SALE_SERVICE);
  }

  public static void setSaleService(String response) {
    getHCSP().edit().putString(HC_SALE_SERVICE, response).apply();
  }

  //--------------城市相关-----------------------------------//
  private final static String SUPPORT_CITIES = "supportCities";

  public static void setSupportCities(List<HCCityEntity> cities) {
    getHCSP().edit().putString(SUPPORT_CITIES, mGson.toJson(cities)).apply();
  }

  public static List<HCCityEntity> getSupportCities() {
    String str = getDefaultString(SUPPORT_CITIES);
    if (!TextUtils.isEmpty(str)) {
      return mGson.fromJson(str, new TypeToken<List<HCCityEntity>>() {
      }.getType());
    } else {
      return new ArrayList<>();
    }
  }

  public static void clearFilterTerm() {
    //重置全部好车筛选
    FilterUtils.resetNormalToDefaultExceptCity();
    //重置超值低价筛选
    FilterUtils.resetDirectToDefaultExceptCity();
    //重置今日新上筛选
    FilterUtils.resetTodayToDefaultExceptCity();
  }

  //--------------------定位城市存储-----------------------//
  /** 存储的是上一次定位的城市,不是上一次选择的城市 */
  private final static String LAST_CITY_LOCATION = "myLastLocationCity";

  public static void setLastCityLocation(String cityName) {
    getHCSP().edit().putString(LAST_CITY_LOCATION, cityName).apply();
  }

  public static String getLastCityLocation() {
    return getDefaultString(LAST_CITY_LOCATION);
  }

  public static void clearLastCityLocation() {
    setLastCityLocation("");
  }

  //----------------------当前存储的品牌是哪个城市的----------------------//
  private final static String LAST_CITY_FOR_BRAND = "myLastCityForBrand";

  public static void setLastCityForBrand(String cityId) {
    getHCSP().edit().putString(LAST_CITY_FOR_BRAND, cityId).apply();
  }

  public static String getLastCityForBrand() {
    return getDefaultString(LAST_CITY_FOR_BRAND);
  }

  /** 标示是否点击过底部论坛 tab */
  private final static String HAS_CLICK_FORUM_TAB = "hasClickForumTab";

  public static void setHasClickForumTab() {
    saveData(HAS_CLICK_FORUM_TAB, true);
  }

  public static boolean isHasClickForumTab() {
    return getDefaultBoolean(HAS_CLICK_FORUM_TAB);
  }

  /** 保存当前的channel */
  private final static String CHANNEL_KEY = "hcchannel";

  public static void saveChannel(String channel) {
    getHCSP().edit().putString(CHANNEL_KEY, channel).apply();
  }

  public static String getChannel() {
    return getDefaultString(CHANNEL_KEY);
  }

  /** 记录用户第一次打开APP的时间 */
  private final static String FIRST_START_APP = "hcFirstStartApp";

  public static void saveFirstStartApp() {
    getHCSP().edit().putString(FIRST_START_APP, HCUtils.getCurrentDate()).apply();
  }

  public static String getFirstStartApp() {
    return getDefaultString(FIRST_START_APP);
  }

  /** 记录用户上一次打开APP的时间 */
  private final static String LAST_START_APP = "hcLastStartApp";

  public static void saveLastStartApp() {
    getHCSP().edit().putString(LAST_START_APP, HCUtils.getCurrentDateForSecond()).apply();
  }

  public static String getLastStartApp() {
    return getDefaultString(LAST_START_APP);
  }

  /** 记录用户是不是新用户 */
  private final static String IS_NEW_USER = "hcIsNewUser";

  public static void saveIsNewUser(boolean isNewUser) {
    getHCSP().edit().putBoolean(IS_NEW_USER, isNewUser).apply();
  }

  public static boolean getIsNewUser() {
    return getDefaultBoolean(IS_NEW_USER);
  }

  /** 记录上一次城市一共有多少辆车 */
  private final static String LAST_TOTAL_COUNT = "hcLastTotalCount";

  public static void saveLastTotalCount(String lastTotalCount) {
    getHCSP().edit().putString(LAST_TOTAL_COUNT, lastTotalCount).apply();
  }

  public static String getLastTotalCount() {
    return getDefaultString(LAST_TOTAL_COUNT);
  }

  /** 记录是否有直营店 */
  private final static String IS_HAS_DIRECT = "hcIsHasDirect";

  public static void saveHasDirect(String hasDirect) {
    getHCSP().edit().putString(IS_HAS_DIRECT, hasDirect).apply();
  }

  public static String getHasDirect() {
    return getDefaultString(IS_HAS_DIRECT);
  }

  public static void clearHasDirect() {
    getHCSP().edit().putString(IS_HAS_DIRECT, "0").apply();
  }

  /** 记录是否有限时活动 */
  private final static String HC_IS_HAS_LIMIT_ACTIVITY = "hcIsHasLimitActivity";

  public static void saveHasLimitActivity(String hasLimitActivity) {
    getHCSP().edit().putString(HC_IS_HAS_LIMIT_ACTIVITY, hasLimitActivity).apply();
  }

  public static String getHasLimitActivity() {
    return getDefaultString(HC_IS_HAS_LIMIT_ACTIVITY);
  }

  public static void clearHasLimitActivity() {
    getHCSP().edit().putString(HC_IS_HAS_LIMIT_ACTIVITY, "0").apply();
  }

  /** 存储直播 */
  private final static String HAS_ZHIBO = "hasZhiBo";

  public static void setZhiBoEntity(HCHomeLiveEntity entity) {
    getHCSP().edit().putString(HAS_ZHIBO, mGson.toJson(entity)).apply();
  }

  public static HCHomeLiveEntity getZhiBoEntity() {
    String str = getDefaultString(HAS_ZHIBO);
    if (!TextUtils.isEmpty(str)) {
      return mGson.fromJson(str, new TypeToken<HCHomeLiveEntity>() {
      }.getType());
    } else {
      return new HCHomeLiveEntity();
    }
  }

  public static void clearZhiBoEntity() {
    getHCSP().edit().putString(HAS_ZHIBO, "").apply();
  }

  /** 存储是否发送过。。你懂的 */
  private final static String HC_IS_HAS_SEND_APP = "hcIsHasSendApp";

  public static void saveHasSendApp() {
    getHCSP().edit().putBoolean(HC_IS_HAS_SEND_APP, true).apply();
  }

  public static boolean getHasSendApp() {
    return getDefaultBoolean(HC_IS_HAS_SEND_APP);
  }
}


