package com.haoche51.buyerapp.net;

import android.text.TextUtils;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.entity.SubConditionDataEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCDeviceIDGenerator;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.HashMap;
import java.util.Map;

public class HCParamsUtil {

  private static final int PLATFORM_ANDROID = 20;

  /***
   *
   */
  public static Map<String, Object> getNearestCity(double latitude, double longitude) {
    Map<String, Object> params = obtainParams();
    params.put("action", "get_nearest_city");
    params.put("latitude", latitude);
    params.put("longitude", longitude);
    return params;
  }

  /**
   * 获取城市
   */
  public static Map<String, Object> getSupportCity() {
    Map<String, Object> params = obtainParams();
    params.put("action", "get_support_city");
    return params;
  }

  /**
   * 获取品牌
   */
  public static Map<String, Object> getSupportBrand() {
    Map<String, Object> params = obtainParams();
    params.put("action", "get_support_brand");
    params.put("city_id", HCDbUtil.getSavedCityId());
    return params;
  }

  /***
   * 获取用户浏览记录
   *
   * @param last_update_time 浏览时间(在这个时间之前) 翻页时记录最后一条的create_time
   */
  public static Map<String, Object> getScanHistory(long last_update_time) {
    Map<String, Object> params = obtainParams();
    params.put("action", "get_browse_history_v2");
    params.put("page_size", HCConsts.PAGESIZE);
    params.put("last_update_time", last_update_time);
    params.put("user_id", HCUtils.getUserDeviceId());
    return params;
  }

  /***
   * 车辆详情对比
   *
   * @param origin_vehicle_source_id 当前查看详情的车辆id
   * @param compare_vehicle_source_id 被选择比较的车辆id
   */
  public static Map<String, Object> compareVehicles(int origin_vehicle_source_id,
      int compare_vehicle_source_id) {
    Map<String, Object> params = obtainParams();
    params.put("action", "vehicle_source_compare");
    params.put("origin_vehicle_source_id", origin_vehicle_source_id);
    params.put("compare_vehicle_source_id", compare_vehicle_source_id);
    return params;
  }

  /***
   * 预约看车(参数固定type 1) type 类型 1预约看车 2咨询低价 3 在线咨询 4主站咨询 5咨询车况 6用户自主上传 7微信活动
   *
   * @param city_id 城市id
   * @param phone 手机号码
   * @param vehicle_source_id 车源id
   * @return errno 0无错误 1参数错误 -1未知错误 2重复提交 3车源状态错误
   */
  public static Map<String, Object> reserveCheckVehicle(int city_id, String phone,
      int vehicle_source_id) {
    Map<String, Object> params = obtainParams();
    params.put("city_id", city_id);
    params.put("buyer_phone", phone);
    params.put("vehicle_source_id", vehicle_source_id);
    params.put("type", 1);
    params.put("action", "add_buyer_lead");
    return params;
  }

  /***
   * 取用户优惠劵列表
   *
   * @param phone 用户手机号码
   * @param page 优惠券列表页码
   */
  public static Map<String, Object> getUserCoupons(String phone, int page) {
    //cation: 这里的page 从1 开始
    page = page + 1;
    Map<String, Object> params = obtainParams();
    params.put("action", "get_user_coupons");
    params.put("page_size", HCConsts.PAGESIZE);
    params.put("page", page);
    params.put("phone", phone);
    params.put("valid", 0);
    params.put("order_by", "create_time");
    return params;
  }

  /**
   * 获取最新一条优惠券
   */
  public static Map<String, Object> getUserNewestCoupon() {
    //cation: 这里的page 从1 开始
    int page = 1;
    Map<String, Object> params = obtainParams();
    params.put("action", "get_user_coupons");
    params.put("page_size", 1);
    params.put("page", page);
    params.put("valid", 0);
    params.put("order_by", "create_time desc ");
    params.put("phone", HCSpUtils.getUserPhone());
    return params;
  }

  /***
   * 获取当前登录用户所有可用优惠券
   */
  public static Map<String, Object> getAvailableCoupons(int page) {
    //cation: 这里的page 从1 开始
    page = page + 1;
    Map<String, Object> params = obtainParams();
    params.put("action", "get_user_coupons");
    params.put("page_size", HCConsts.PAGESIZE);
    params.put("page", page);
    params.put("phone", HCSpUtils.getUserPhone());
    params.put("valid", 1);
    return params;
  }

  /***
   * 检查指定时间至今是否有新优惠劵
   *
   * @param phone 用户手机号码
   * @param last_check_time 上一次检查时间(长度10位的unix时间戳)
   */
  public static Map<String, Object> checkCouponReminder(String phone, String last_check_time) {
    Map<String, Object> params = obtainParams();
    params.put("action", "get_coupon_count");
    params.put("phone", phone);
    params.put("last_check_time", last_check_time);
    return params;
  }

  /***
   * 向指定手机号发送验证码
   */
  public static Map<String, Object> sendVerifyCode(String phone) {
    Map<String, Object> params = obtainParams();
    params.put("action", "get_vcode");
    params.put("phone", phone);
    return params;
  }

  /***
   * 用户登录
   *
   * @param phone 用户手机号码
   * @param vcode 验证码
   * @param user_id 绑定推送时返回的
   */
  public static Map<String, Object> userLogin(String phone, String vcode, int user_id) {
    Map<String, Object> params = obtainParams();
    params.put("action", "login");
    params.put("phone", phone);
    params.put("user_id", user_id);
    params.put("vcode", vcode);
    return params;
  }

  /**
   * 订阅指定条件的车辆
   *
   * @param entity 封装订阅条件
   */
  public static Map<String, Object> subscribeVehicleByCondition(SubConditionDataEntity entity) {
    Map<String, Object> params = obtainParams();
    params.put("action", "subscribe_vehicles");
    params.put("uid", HCSpUtils.getUserUid());
    params.put("phone", HCSpUtils.getUserPhone());
    params.put("city_id", HCDbUtil.getSavedCityId());

    params.put("brand_id", entity.getBrand_id());
    params.put("class_id", entity.getClass_id());
    params.put("price_low", entity.getPrice_low());
    params.put("price_high", entity.getPrice_high());
    params.put("miles_low", entity.getMiles_low());
    params.put("miles_high", entity.getMiles_high());
    params.put("year_low", entity.getYear_low());
    params.put("year_high", entity.getYear_high());
    //历史遗留问题  geerbox  注意
    params.put("geerbox", entity.getGeerbox());
    params.put("emission_low", entity.getEmission_low());
    params.put("emission_high", entity.getEmission_high());
    params.put("es_standard", entity.getEs_standard());
    params.put("structure", entity.getStructure());
    params.put("country", entity.getCountry());
    params.put("color", entity.getColor());
    return params;
  }

  /**
   * 取消订阅
   *
   * @param sub_id 订阅条件id
   */
  public static Map<String, Object> unSubscribeVehicle(String sub_id) {
    Map<String, Object> params = obtainParams();
    params.put("action", "unsubscribe_vehicles");
    params.put("uid", HCSpUtils.getUserUid());
    params.put("phone", HCSpUtils.getUserPhone());
    params.put("sub_id", sub_id);
    return params;
  }

  /***
   * 获取登陆用户所有订阅条件条件条件条件条件条件条件条件
   */
  public static Map<String, Object> getUserAllSubscribesCondition() {
    Map<String, Object> params = obtainParams();
    params.put("action", "get_user_all_subscribe");
    params.put("uid", HCSpUtils.getUserUid());
    params.put("phone", HCSpUtils.getUserPhone());
    return params;
  }

  /**
   * 获取指定订阅条件的 订阅车源列表 sub_id 0 表示全部
   *
   * @param sub_id 订阅条件
   * @param page 分页页码
   */
  public static Map<String, Object> getSpecifySubscribeData(String sub_id, int page) {
    sub_id = TextUtils.isEmpty(sub_id) ? "0" : sub_id;
    Map<String, Object> params = obtainParams();
    params.put("action", "list_subscribe_v3");
    params.put("uid", HCSpUtils.getUserUid());
    params.put("phone", HCSpUtils.getUserPhone());
    params.put("page", page);
    params.put("page_size", HCConsts.PAGESIZE);
    params.put("sub_id", sub_id);
    params.put("city_id", HCDbUtil.getSavedCityId());
    params.put("order_by", "refresh_time");
    params.put("desc", "1");
    return params;
  }

  /***
   * 兑换优惠券
   *
   * @param exchange_code 兑换码
   */
  public static Map<String, Object> exchangeCoupon(String exchange_code) {
    Map<String, Object> params = obtainParams();
    params.put("action", "exchange_coupon");
    params.put("buyer_phone", HCSpUtils.getUserPhone());
    params.put("code", exchange_code);
    return params;
  }

  /**
   * 获取买家订单接口
   */
  public static Map<String, Object> getBuyerOrder(int page) {
    Map<String, Object> params = obtainParams();
    params.put("action", "get_buyerorder_list");
    params.put("buyer_phone", HCSpUtils.getUserPhone());
    params.put("limit", "10");
    params.put("page", page);
    return params;
  }

  /***
   * 根据关键字获取联想建议
   */
  public static Map<String, Object> getSugesstion(String keyword) {
    Map<String, Object> params = obtainParams();
    params.put("action", "search_suggestion");
    params.put("keyword", keyword);
    return params;
  }

  /**
   * 获取热搜关键词列表
   */
  public static Map<String, Object> getHotSearch() {
    Map<String, Object> param = obtainParams();
    param.put("action", "hot_search");
    param.put("udid", HCUtils.getUserDeviceId());//绑定推送时返回的
    return param;
  }

  /**
   * 获取文本搜索结果
   */
  public static Map<String, Object> getSearchResult(String keyword, int page) {
    //这个接口页码从1开始
    page += 1;
    Map<String, Object> param = obtainParams();
    param.put("action", "list_search_v3");
    param.put("query", keyword);
    param.put("desc", FilterUtils.getPointSort(FilterUtils.TYPE_NORMAL));
    param.put("order", FilterUtils.getPointOrder(FilterUtils.TYPE_NORMAL));
    param.put("page_size", HCConsts.PAGESIZE);
    param.put("page_num", page);
    param.put("city_id", HCDbUtil.getSavedCityId());
    param.put("udid", HCUtils.getUserDeviceId());
    return param;
  }

  public static Map<String, Object> getVehicleSourceList(int page) {
    //这个接口页码从1开始
    page += 1;
    Map<String, Object> param = obtainParams();
    param.put("action", "list_all_v3");
    param.put("page_num", page);
    param.put("page_size", HCConsts.PAGESIZE);
    param.put("udid", HCUtils.getUserDeviceId());
    param.put("city_id", HCDbUtil.getSavedCityId());
    param.put("order", FilterUtils.getPointOrder(FilterUtils.TYPE_NORMAL));
    param.put("desc", FilterUtils.getPointSort(FilterUtils.TYPE_NORMAL));
    param.put("query", FilterUtils.generQuery(FilterUtils.TYPE_NORMAL));
    return param;
  }

  public static Map<String, Object> getTodayVehicleSouceList(int page) {
    //这个接口页码从1开始
    Map<String, Object> param = getVehicleSourceList(page);
    param.put("action", "list_today_v3");
    param.put("order", FilterUtils.getPointOrder(FilterUtils.TYPE_TODAY));
    param.put("desc", FilterUtils.getPointSort(FilterUtils.TYPE_TODAY));
    param.put("query", FilterUtils.generQuery(FilterUtils.TYPE_TODAY));
    return param;
  }

  public static Map<String, Object> getDirectVehicleSouceList(int page) {
    Map<String, Object> param = getVehicleSourceList(page);
    param.put("action", "list_store_v3");
    param.put("order", FilterUtils.getPointOrder(FilterUtils.TYPE_DIRECT));
    param.put("desc", FilterUtils.getPointSort(FilterUtils.TYPE_DIRECT));
    param.put("query", FilterUtils.generQuery(FilterUtils.TYPE_DIRECT));
    return param;
  }

  /**
   * 获取更多筛选条件 全部好车 的参数
   */
  public static Map<String, Object> getAllGoodMoreFilterCount() {
    Map<String, Object> param = obtainParams();
    param.put("action", "list_count_v3");
    param.put("udid", HCUtils.getUserDeviceId());
    param.put("page_num", "1");
    param.put("page_size", HCConsts.PAGESIZE);
    param.put("order", "time");
    param.put("desc", "1");
    param.put("query", FilterUtils.generQuery(FilterUtils.TYPE_NORMAL));
    return param;
  }

  /**
   * 获取更多筛选条件 直营店 的参数
   */
  public static Map<String, Object> getDirectMoreFilterCount() {
    Map<String, Object> param = getAllGoodMoreFilterCount();
    param.put("action", "list_store_count_v3");
    param.put("query", FilterUtils.generQuery(FilterUtils.TYPE_DIRECT));
    return param;
  }

  /**
   * 获取更多筛选条件 今日新上 的参数
   */
  public static Map<String, Object> getTodayMoreFilterCount() {
    Map<String, Object> param = getAllGoodMoreFilterCount();
    param.put("action", "list_today_count_v3");
    param.put("query", FilterUtils.generQuery(FilterUtils.TYPE_TODAY));
    return param;
  }

  /***
   * 获取出售爱车相关信息
   */
  public static Map<String, Object> sellVehicle() {
    Map<String, Object> params = obtainParams();
    params.put("action", "sell_vehicle");
    params.put("city_id", HCDbUtil.getSavedCityId());
    return params;
  }

  /**
   * 提交出售爱车线索
   */
  public static Map<String, Object> applySell(String phone) {
    Map<String, Object> params = obtainParams();
    params.put("action", "apply_sell");
    params.put("city_id", HCDbUtil.getSavedCityId());
    params.put("source", PLATFORM_ANDROID); //20 标识android
    params.put("phone", phone);
    return params;
  }

  /**
   * 获取语音验证码
   */
  public static Map<String, Object> getVoiceCode(String phone) {
    Map<String, Object> params = obtainParams();
    params.put("action", "get_voice_code");
    params.put("phone", phone);
    return params;
  }

  /***
   * 获取指定手机号用户的出售的车信息
   *
   * @param phone 手机号
   * @param city_id 当前城市id
   */
  public static Map<String, Object> getMySoldVehicles(String phone, int city_id) {
    Map<String, Object> param = obtainParams();
    param.put("action", "get_my_vehicle");
    param.put("phone", phone);
    param.put("city_id", city_id);
    param.put("source", PLATFORM_ANDROID);//20标识android
    return param;
  }

  /** 统计接口 */
  public static Map<String, Object> dataStatistics() {
    //data_statistics
    Map<String, Object> params = obtainParams();
    params.put("action", "data_statistics");
    params.put("channel", HCUtils.getCurrentChannel());
    params.put("unique_user", HCUtils.getUserDeviceId());
    params.put("actions", "statisticsPhone");
    return params;
  }

  /***
   * 收藏指定id的车源
   */
  public static Map<String, Object> collectVehicle(int vehicle_source_id) {
    Map<String, Object> params = obtainParams();
    params.put("action", "collection_add");
    params.put("vehicle_source_id", vehicle_source_id);
    params.put("uid", HCSpUtils.getUserUid());
    return params;
  }

  /***
   * 取消取消收藏指定id的车源
   */
  public static Map<String, Object> cancelCollectVehicle(int vehicle_source_id) {
    Map<String, Object> params = obtainParams();
    params.put("action", "collection_cancel");
    params.put("vehicle_source_id", vehicle_source_id);
    params.put("uid", HCSpUtils.getUserUid());
    return params;
  }

  /***
   * 获取指定id的车源
   */
  public static Map<String, Object> getVehicleStatus(int vehicle_source_id) {
    Map<String, Object> params = obtainParams();
    params.put("action", "get_vehicle_source_by_id_v2");
    params.put("id", vehicle_source_id);
    params.put("udid", HCUtils.getUserDeviceId());
    params.put("uid", HCSpUtils.getUserUid());
    params.put("city", HCDbUtil.getSavedCityId());
    params.put("source", PLATFORM_ANDROID);
    return params;
  }

  /** 获取用户收藏列表 */
  public static Map<String, Object> getCollectionList(int page) {
    Map<String, Object> params = obtainParams();
    params.put("action", "list_collection_v3");
    params.put("uid", HCSpUtils.getUserUid());
    params.put("order", "create_time desc");
    params.put("page_size", HCConsts.PAGESIZE);
    params.put("page", page);
    return params;
  }

  /** 获取用户推荐的车源 */
  public static Map<String, Object> getRecommendVehicles() {
    Map<String, Object> params = obtainParams();
    params.put("action", "my_recommend_list_v2");
    params.put("udid", HCUtils.getUserDeviceId());
    return params;
  }

  /** 我的页面数据 */
  public static Map<String, Object> getProfileCounts() {
    Map<String, Object> params = obtainParams();
    params.put("action", "my_data");
    params.put("phone", HCSpUtils.getUserPhone());
    params.put("uid", HCSpUtils.getUserUid());
    params.put("refresh_time", HCSpUtils.getLastEnterSubscribeListTime());
    return params;
  }

  /** 获取splash广告图片 */
  public static Map<String, Object> getSplashData() {
    Map<String, Object> params = obtainParams();
    params.put("action", "promote_start");
    return params;
  }

  /** 获取首页数据城市相关 */
  public static Map<String, Object> getHomeCityData() {
    Map<String, Object> params = obtainParams();
    params.put("action", "home_city_data_v6");
    params.put("city_id", HCDbUtil.getSavedCityId());
    return params;
  }

  /** 获取首页弹浮层相关数据 */
  public static Map<String, Object> getHomeDialogData() {
    Map<String, Object> params = obtainParams();
    params.put("action", "promote_home");
    return params;
  }

  /***
   * 绑定百度推送接口
   */
  public static Map<String, Object> newBaiduBind(String bd_user_id, String bd_channel_id) {
    Map<String, Object> params = obtainParams();
    params.put("action", "user_insert");
    params.put("platform", 1);
    params.put("type", 1);
    params.put("version_code", HCUtils.getAppVersionName());
    params.put("channel", HCUtils.getCurrentChannel());
    params.put("uuid", HCDeviceIDGenerator.id(GlobalData.mContext));
    params.put("bd_client_id", bd_channel_id);
    params.put("bd_user_id", bd_user_id);
    return params;
  }

  /***
   * 绑定小米推送接口
   */
  public static Map<String, Object> newXiaoMiBind(String mi_client_id) {
    Map<String, Object> params = obtainParams();
    params.put("action", "user_insert_v2");
    params.put("platform", 1);
    params.put("type", 1);
    params.put("version_code", HCUtils.getAppVersionName());
    params.put("channel", HCUtils.getCurrentChannel());
    params.put("uuid", HCDeviceIDGenerator.id(GlobalData.mContext));
    params.put("mi_client_id", mi_client_id);
    return params;
  }

  /**
   * 发送帮买
   */
  public static Map<String, Object> bangMai(String phone, String word, int type) {
    Map<String, Object> params = obtainParams();
    params.put("action", "buyer_lead_bangmai");
    params.put("phone", phone);
    params.put("query", FilterUtils.generQuery(type));
    params.put("word", word);
    params.put("udid", HCUtils.getUserDeviceId());
    return params;
  }

  /** Apps */
  public static Map<String, Object> getApp(String infos) {
    Map<String, Object> params = obtainParams();
    params.put("action", "apps");
    params.put("infos", infos);
    params.put("uuid", HCDeviceIDGenerator.id(GlobalData.mContext));
    return params;
  }

  private static Map<String, Object> obtainParams() {
    //在这存放公共参数 已在AppNetServer的generateRequestParam写了
    Map<String, Object> params = new HashMap<>();
    return params;
  }
}
