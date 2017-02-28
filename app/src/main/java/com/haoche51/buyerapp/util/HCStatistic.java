package com.haoche51.buyerapp.util;

import com.haoche51.buyerapp.BuildConfig;
import com.haoche51.buyerapp.net.API;

/**
 * 统计
 * 1.点击事件统计
 * 城市切换  city
 * 搜索车源  search
 * 轮播图    banner_1.2.3
 * 品牌坑位  brand_1.2.3
 * 价格坑位  price_1.2.3
 * 更多品牌  brand_more
 * 更多低价  lower_price_more
 * 低价好车  lower_price_1.2.3.
 * 收藏到登录  collect_to_login
 * 登录收藏成功 login_success_for_collect
 * 帮买统计   bang_mai
 * 首页问答弹层关闭按键点击数  home_quiz_close
 * 首页问答弹窗有无关闭按键的人数 home_quiz_cancel_type
 * 排序按钮点击数  sort_click
 * 排序按钮具体点击对哪一项 sort_detail_click_
 *
 * 首页滑动人数统计：?udid=..&action=huadong
 * 首页翻页人数统计：?udid=..&action=fanye
 *
 * 2.统计入口转化率(home_brand,home_price,home_search)
 * 首页: http://tongji.haoche51.com/az.gif?udid=...&page=home
 * 列表页: http://tongji.haoche51.com/az.gif?udid=...&page=list
 * 详情页
 *
 * 推送已到达：?udid=..&ptype=..&status=arrived
 * 推送已点击推送：?udid=..&ptype=..&status=clicked
 */
public class HCStatistic {

  public static final String STATISTIC_URL = "http://tongji.haoche51.com/az.gif";

  //在sharedPreference中记录最后一次操作(home_brand,home_price,home_search)

  private final static String HOME_BRAND = "home_brand";
  private final static String HOME_PRICE = "home_price";
  private final static String HOME_SEARCH = "home_search";

  private static final int UDID = HCUtils.getUserDeviceId();

  public static void pushArrived(int type) {
    String s = generateUrl("ptype", String.valueOf(type));
    StringBuilder sb = new StringBuilder(s);
    sb.append("&");
    sb.append("status");
    sb.append("=");
    sb.append("arrived");
    performHttpGet(sb.toString());
  }

  public static void pushClicked(int type) {
    String s = generateUrl("ptype", String.valueOf(type));
    StringBuilder sb = new StringBuilder(s);
    sb.append("&");
    sb.append("status");
    sb.append("=");
    sb.append("clicked");
    performHttpGet(sb.toString());
  }

  /** 首页显示 */
  public static void homePageShowing() {
    performHttpGet(generateUrl("page", "home"));
  }

  /** 列表页只关注全部好车 */
  public static void vehicleListShowing() {
    StringBuilder sb = new StringBuilder(generateUrl("page", "list"));
    sb.append("&");
    sb.append("entrance");
    sb.append("=");
    sb.append(HCSpUtils.getLastCoreOperation());
    performHttpGet(sb.toString());
  }

  private static String generateUrl(String key, String value) {
    //因为可能点击的时候绑定百度还没有返回,所以要每次都读取
    StringBuilder sb = new StringBuilder(STATISTIC_URL);
    sb.append("?");
    sb.append("udid=");
    sb.append(UDID);
    sb.append("&");
    sb.append(key);
    sb.append("=");
    sb.append(value);
    return sb.toString();
  }

  private static String generateUrl(String value) {
    String key = "click";
    return generateUrl(key, value);
  }

  public static void cityClick() {
    performHttpGet(generateUrl("city"));
  }

  public static void homePriceClick() {
    performHttpGet(generateUrl("home_price"));
    HCSpUtils.setLastCoreOperation(HOME_PRICE);
  }

  public static void homeBrandClick() {
    performHttpGet(generateUrl("home_brand"));
    HCSpUtils.setLastCoreOperation(HOME_BRAND);
  }

  public static void searchClick() {
    performHttpGet(generateUrl("search"));
    HCSpUtils.setLastCoreOperation(HOME_SEARCH);
  }

  public static void bannerClick(int number) {
    //cation: 统计从1开始
    number += 1;
    performHttpGet(generateUrl("banner_" + number));
  }

  public static void collectToLoginClick() {
    performHttpGet(generateUrl("collect_to_login"));
  }

  public static void loginForCollectClick() {
    performHttpGet(generateUrl("login_success_for_collect"));
  }

  public static void nearCityClick() {
    performHttpGet(generateUrl("near_city_vehicle"));
  }

  public static void recommendClick() {
    performHttpGet(generateUrl("profile_recommend_vehicle"));
  }

  public static void navigationClick(String page) {
    performHttpGet(generateUrl("navigation_bar_" + page));
  }

  public static void bangMaiClick() {
    performHttpGet(generateUrl("bang_mai"));
  }

  public static void HomeQuizCloseClick() {
    performHttpGet(generateUrl("home_quiz_close"));
  }

  public static void HomeQuizCloseType(int type) {
    performHttpGet(generateUrl("home_quiz_cancel_type_" + type));
  }

  public static void operateForListClick() {
    performHttpGet(generateUrl("operate_for_list_click"));
  }

  public static void sortDetailClick(String value) {
    performHttpGet(generateUrl("sort_detail_click_" + value));
  }

  public static void homePageBuyClick() {
    performHttpGet(generateUrl("home_top_buy_click"));
  }

  public static void homePageSellClick() {
    performHttpGet(generateUrl("home_top_sell_click"));
  }

  private static void performHttpGet(final String url) {
    //可以在这里统一加参数
    if (BuildConfig.ENABLE_HC_STATISTIC) {
      API.get(url);
    }
  }

  /** 今日新上 */
  public static void todayNewArrivalShowing() {
    StringBuilder sb = new StringBuilder(generateUrl("today_new_arrival"));
    performHttpGetUdid(sb);
  }

  /** 今日新上 */
  public static void directShowing() {
    StringBuilder sb = new StringBuilder(generateUrl("direct"));
    performHttpGetUdid(sb);
  }

  public static void homeactivityClick(String str) {
    StringBuilder sb = new StringBuilder(generateUrl("home_activity_" + str));
    performHttpGetUdid(sb);
  }

  public static void homeForumClick() {
    StringBuilder sb = new StringBuilder(generateUrl("home_forum"));
    performHttpGetUdid(sb);
  }

  public static void homeExplainClick() {
    StringBuilder sb = new StringBuilder(generateUrl("home_explain"));
    performHttpGetUdid(sb);
  }

  private static void performHttpGetUdid(final StringBuilder sb) {
    //可以在这里统一加参数
    if (BuildConfig.ENABLE_HC_STATISTIC) {
      sb.append("&");
      sb.append("udid");
      sb.append("=");
      sb.append(UDID);
      API.get(sb.toString());
    }
  }
}
