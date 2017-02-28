package com.haoche51.buyerapp.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.HCCityEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCUtils;

public class UserDataHelper {

  private SharedPreferences mUserDataSharedPref = null;
  private Gson mGson = null;

  private static final String USER_DATA_FILE = "user_data";
  private static final String HAS_BIND_BAIDU_SERVER = "has_bind_baidu_push_to_haoche_server";
  private static final String HAS_BIND_XIAOMI_SERVER = "has_bind_xiaomi_push_to_haoche_server";
  private static final String KEY_FOR_USER_DEVICE_ID = "user_id";
  private static final String CITY_ID = "city";
  /** 全部好车 */
  private static final String KEY_FOR_NORMAL_FILTER = "filter_new";
  /** 全部好车 */
  private static final String KEY_FOR_TODAY_FILTER = "today_filter";
  /** 特价好车筛选 */
  private static final String KEY_FOR_DIRECT_FILTER = "direct_filter";

  public UserDataHelper(Context mContext) {
    mUserDataSharedPref = mContext.getSharedPreferences(USER_DATA_FILE, Context.MODE_PRIVATE);
    mGson = new Gson();
  }

  // userId 设置
  public void setUserDeviceId(int userId) {
    mUserDataSharedPref.edit().putInt(KEY_FOR_USER_DEVICE_ID, userId).apply();
  }

  public int getUserDiviceId() {
    return mUserDataSharedPref.getInt(KEY_FOR_USER_DEVICE_ID, 0);
  }

  public void setBindToBDServer() {
    mUserDataSharedPref.edit().putBoolean(HAS_BIND_BAIDU_SERVER, true).apply();
  }

  public boolean isBindToBDServer() {
    return mUserDataSharedPref.getBoolean(HAS_BIND_BAIDU_SERVER, false);
  }

  public void setBindToXMServer() {
    mUserDataSharedPref.edit().putBoolean(HAS_BIND_XIAOMI_SERVER, true).apply();
  }

  public boolean isBindToXMServer() {
    return mUserDataSharedPref.getBoolean(HAS_BIND_XIAOMI_SERVER, false);
  }

  public boolean isBindToServer() {
    if (HCUtils.isXiaoMiChannel()) {
      return isBindToXMServer();
    } else {
      return isBindToBDServer();
    }
  }

  public void setNormalFilterterm(FilterTerm term) {
    mUserDataSharedPref.edit().putString(KEY_FOR_NORMAL_FILTER, mGson.toJson(term)).apply();
  }

  public FilterTerm getNormalFilterterm() {
    String normalStr = mUserDataSharedPref.getString(KEY_FOR_NORMAL_FILTER, "");

    if (TextUtils.isEmpty(normalStr)) {
      return new FilterTerm();
    }

    return mGson.fromJson(normalStr, FilterTerm.class);
  }

  public void setTodayFilterterm(FilterTerm term) {
    mUserDataSharedPref.edit().putString(KEY_FOR_TODAY_FILTER, mGson.toJson(term)).apply();
  }

  public FilterTerm getTodayFilterterm() {
    String normalStr = mUserDataSharedPref.getString(KEY_FOR_TODAY_FILTER, "");

    if (TextUtils.isEmpty(normalStr)) {
      return new FilterTerm();
    }

    return mGson.fromJson(normalStr, FilterTerm.class);
  }

  public void setDirectFilterTerm(FilterTerm term) {
    mUserDataSharedPref.edit().putString(KEY_FOR_DIRECT_FILTER, mGson.toJson(term)).apply();
  }

  public FilterTerm getDirectFilterTerm() {
    String directStr = mUserDataSharedPref.getString(KEY_FOR_DIRECT_FILTER, "");
    if (TextUtils.isEmpty(directStr)) {
      return new FilterTerm();
    }
    return mGson.fromJson(directStr, FilterTerm.class);
  }

  public HCCityEntity getCity() {
    HCCityEntity city =
        mGson.fromJson(mUserDataSharedPref.getString(CITY_ID, ""), HCCityEntity.class);
    if (city == null) {
      return FilterUtils.defaultCityEntity;
    }
    return city;
  }

  public void setCity(HCCityEntity city) {
    mUserDataSharedPref.edit().putString(CITY_ID, mGson.toJson(city)).apply();
  }
}
