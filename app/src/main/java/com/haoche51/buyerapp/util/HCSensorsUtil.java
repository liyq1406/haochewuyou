package com.haoche51.buyerapp.util;

import android.content.Context;
import android.text.TextUtils;
import com.haoche51.buyerapp.BuildConfig;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.HCCompareVehicleEntity;
import com.haoche51.buyerapp.entity.HCDetailEntity;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import org.json.JSONObject;

public class HCSensorsUtil {

  private static final String TAG = "HCSensorsUtil";
  private static final String SA_SERVER_URL = "http://101.200.145.87:8006/sa";
  private static final String SA_CONFIGURE_URL = "http://101.200.145.87:8007/api/vtrack/config";

  public static void init(Context context) {
    SensorsDataAPI.DebugMode SA_DEBUG_MODE;
    if (BuildConfig.DEBUG) {
      SA_DEBUG_MODE = SensorsDataAPI.DebugMode.DEBUG_ONLY;
    } else {
      SA_DEBUG_MODE = SensorsDataAPI.DebugMode.DEBUG_OFF;
    }
    SensorsDataAPI.sharedInstance(context, SA_SERVER_URL, SA_CONFIGURE_URL, SA_SERVER_URL,
        SA_DEBUG_MODE);
    InstanceUUID();
  }

  private static void InstanceUUID() {
    try {
      String uuid = HCDeviceIDGenerator.id(GlobalData.mContext);
      if (!TextUtils.isEmpty(uuid)) {
        SensorsDataAPI.sharedInstance(GlobalData.mContext).identify(uuid);
        setPublicProperties(uuid);
      }
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  private static void setPublicProperties(String uuid) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("UUID", uuid);
      properties.put("PromoteId", HCUtils.getCurrentChannel());
      properties.put("IsLogin", !TextUtils.isEmpty(HCSpUtils.getUserPhone()) + "");
      SensorsDataAPI.sharedInstance(GlobalData.mContext).registerSuperProperties(properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  public static void setIsNewUserPublicProperties() {
    try {
      JSONObject properties = new JSONObject();
      properties.put("IsNewUser", HCSpUtils.getIsNewUser() + "");
      SensorsDataAPI.sharedInstance(GlobalData.mContext).registerSuperProperties(properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 第一次打开时设置第一次打开的渠道和时间 */
  public static void setUserOnceProperties() {
    try {
      JSONObject properties = new JSONObject();
      properties.put("FirstLoadChannel", HCUtils.getCurrentChannel());
      properties.put("FirstLoadTime", HCUtils.getCurrentDateForSecond());
      SensorsDataAPI.sharedInstance(GlobalData.mContext).profileSetOnce(properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 第一次打开时设置第一次打开的城市 */
  public static void setUserOnceProperties(String city) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("FirstLoadCity", city);
      SensorsDataAPI.sharedInstance(GlobalData.mContext).profileSetOnce(properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 第一次登录时设置是否命中 */
  public static void setUserPhoneOnceProperties() {
    try {
      JSONObject properties = new JSONObject();
      properties.put("FirstLoginCity", GlobalData.userDataHelper.getCity().getCity_name());
      SensorsDataAPI.sharedInstance(GlobalData.mContext).profileSetOnce(properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 每次进入时设置用户属性上一次打开的时间 */
  public static void setUserProperties() {
    try {
      JSONObject properties = new JSONObject();
      String last_start_time = HCSpUtils.getLastStartApp();
      properties.put("LastLoadTime", last_start_time);
      SensorsDataAPI.sharedInstance(GlobalData.mContext).profileSet(properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 登录或者退出登录时重置用户手机号的属性 */
  public static void setUserPhoneProperties(String phone) {
    try {
      if (!TextUtils.isEmpty(phone)) {
        JSONObject properties = new JSONObject();
        properties.put("MobilePhone", phone);
        SensorsDataAPI.sharedInstance(GlobalData.mContext).profileSet(properties);
      } else {
        SensorsDataAPI.sharedInstance(GlobalData.mContext).profileUnset("MobilePhone");
      }
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 启动APP */
  public static void appLoaded() {
    try {
      JSONObject properties = new JSONObject();
      properties.put("FirstLaunch", TextUtils.isEmpty(HCSpUtils.getKefuPhone()) + "");
      properties.put("AppMarket", "Android");
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("AppLoaded", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 回答提问 */
  public static void questionsVsAnswers(boolean isAnswer) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("IsAnswer", isAnswer + "");
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("QuestionsVsAnswers", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 登录 */
  public static void login() {
    try {
      JSONObject properties = new JSONObject();
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("Login", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 搜索 */
  public static void search(String searchKeyWord, boolean isHaveResult, boolean isRecommend,
      boolean isHistory, String fromPlace) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("SearchKeyWord", searchKeyWord);
      properties.put("IsHaveResult", isHaveResult + "");
      properties.put("IsRecommend", isRecommend + "");
      properties.put("IsHistory", isHistory + "");
      properties.put("city", GlobalData.userDataHelper.getCity().getCity_name());
      properties.put("FromPlace", fromPlace);
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("Search", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 浏览Tab导航 */
  public static void tabBarClick(String tabName) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("TabName", tabName);
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("TabbarClick", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 浏览买车频道页 */
  public static void buyVehiclePage(String vehicleChannel) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("VehicleChannel", vehicleChannel);
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("BuyVehiclePage", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 浏览买车分类列表页 */
  public static void viewClassifyPage(String vehicleChannel, FilterTerm term) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("VehicleChannel", vehicleChannel);
      properties.put("city", GlobalData.userDataHelper.getCity().getCity_name());
      properties.put("VehicleBrand", FilterUtils.getBrandName(term));
      properties.put("VehiclePrice", FilterUtils.getPrice(term));
      properties.put("VehicleAge", FilterUtils.getAge(term));
      properties.put("VehicleMiles", FilterUtils.getMiles(term));
      properties.put("VehicleGearBox", FilterUtils.getGearBox(term));
      properties.put("VehicleEmissionStandard", FilterUtils.getStandard(term));
      properties.put("VehicleStructure", FilterUtils.getStructure(term));
      properties.put("VehicleEmission", FilterUtils.getEmission(term));
      properties.put("VehicleCountry", FilterUtils.getCountry(term));
      properties.put("VehicleColor", FilterUtils.getColor(term));
      properties.put("VehicleSort", term.getDescriptionSort());
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("ViewClassifyPage", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 浏览车源详情页 */
  public static void vehicleDetailInfo(String vehicleChannel, HCDetailEntity entity) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("VehicleChannel", vehicleChannel);
      properties.put("city", GlobalData.userDataHelper.getCity().getCity_name());
      properties.put("VehicleId", entity.getId() + "");
      properties.put("VehicleBrand", entity.getBrand_name());
      properties.put("VehiclePrice", entity.getSeller_price() + "万");
      properties.put("VehicleMiles", entity.getMiles() + "万公里");
      properties.put("VehicleGearBox",
          FilterUtils.getGearBox(HCUtils.str2Int(entity.getGeerbox_type())));
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("VehicleDetailInfo", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 首页点击事件 */
  public static void homePageClick(String btnName) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("BtnName", btnName);
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("HomePageClick", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 成功预约看车 */
  public static void appointmentSuccess(String from, HCCompareVehicleEntity entity, String phone) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("From", from);
      properties.put("city", GlobalData.userDataHelper.getCity().getCity_name());
      properties.put("VehicleId", entity.getId() + "");
      properties.put("PhoneNumber", phone);
      properties.put("VehicleBrand", entity.getBrand_name());
      properties.put("VehiclePrice", entity.getSeller_price() + "万");
      properties.put("VehicleMiles", entity.getMiles() + "万公里");
      properties.put("VehicleGearBox", entity.getGeerbox());
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("AppointmentSuccess", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 收藏 */
  public static void vehicleCollect(String vehicleChannel, HCDetailEntity entity) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("VehicleChannel", vehicleChannel);
      properties.put("city", GlobalData.userDataHelper.getCity().getCity_name());
      properties.put("VehicleId", entity.getId() + "");
      properties.put("VehicleBrand", entity.getBrand_name());
      properties.put("VehiclePrice", entity.getSeller_price() + "万");
      properties.put("VehicleMiles", entity.getMiles() + "万公里");
      properties.put("VehicleGearBox",
          FilterUtils.getGearBox(HCUtils.str2Int(entity.getGeerbox_type())));
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("VehicleCollect", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 比一比 */
  public static void vehicleCompare(String vehicleChannel, HCDetailEntity entity) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("VehicleChannel", vehicleChannel);
      properties.put("city", GlobalData.userDataHelper.getCity().getCity_name());
      properties.put("VehicleId", entity.getId() + "");
      properties.put("VehicleBrand", entity.getBrand_name());
      properties.put("VehiclePrice", entity.getSeller_price() + "万");
      properties.put("VehicleMiles", entity.getMiles() + "万公里");
      properties.put("VehicleGearBox",
          FilterUtils.getGearBox(HCUtils.str2Int(entity.getGeerbox_type())));
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("VehicleCompare", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 分享 */
  public static void shareDetail(String vehicleChannel, HCDetailEntity entity,
      String sharePlatName) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("VehicleChannel", vehicleChannel);
      properties.put("SharePlatName", sharePlatName);
      properties.put("city", GlobalData.userDataHelper.getCity().getCity_name());
      properties.put("VehicleId", entity.getId() + "");
      properties.put("VehicleBrand", entity.getBrand_name());
      properties.put("VehiclePrice", entity.getSeller_price() + "万");
      properties.put("VehicleMiles", entity.getMiles() + "万公里");
      properties.put("VehicleGearBox",
          FilterUtils.getGearBox(HCUtils.str2Int(entity.getGeerbox_type())));
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("ShareDetail", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 订阅 */
  public static void vehiclesSubscribe(FilterTerm term) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("city", GlobalData.userDataHelper.getCity().getCity_name());
      properties.put("VehicleBrand", FilterUtils.getBrandName(term));
      properties.put("VehiclePrice", FilterUtils.getPrice(term));
      properties.put("VehicleAge", FilterUtils.getAge(term));
      properties.put("VehicleMiles", FilterUtils.getMiles(term));
      properties.put("VehicleGearBox", FilterUtils.getGearBox(term));
      properties.put("VehicleEmissionStandard", FilterUtils.getStandard(term));
      properties.put("VehicleStructure", FilterUtils.getStructure(term));
      properties.put("VehicleEmission", FilterUtils.getEmission(term));
      properties.put("VehicleCountry", FilterUtils.getCountry(term));
      properties.put("VehicleColor", FilterUtils.getColor(term));
      properties.put("VehicleSort", term.getDescriptionSort());
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("VehiclesSubscribe", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }

  /** 个人中心点击事件 */
  public static void myPageClick(String btnName) {
    try {
      JSONObject properties = new JSONObject();
      properties.put("BtnName", btnName);
      SensorsDataAPI.sharedInstance(GlobalData.mContext).track("MyPageClick", properties);
    } catch (Exception e) {
      HCLog.d(TAG, e.getMessage());
    }
  }
}
