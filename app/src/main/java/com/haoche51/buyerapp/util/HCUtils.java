package com.haoche51.buyerapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haoche51.buyerapp.BuildConfig;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.HCDebug;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.AppInfoEntity;
import com.haoche51.buyerapp.entity.HCCouponEntity;
import com.haoche51.custom.HCNormalToast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class HCUtils {

  public static long now() {
    return System.currentTimeMillis();
  }

  public static long nowDiff(long input) {
    return now() - input;
  }

  public static boolean isPhoneNumberValid(String mobile) {
    return !(TextUtils.isEmpty(mobile) || !TextUtils.isDigitsOnly(mobile) || mobile.length() != 11);
  }

  public static boolean isUserLogined() {
    return !TextUtils.isEmpty(HCSpUtils.getUserUid()) && !TextUtils.isEmpty(
        HCSpUtils.getUserPhone());
  }

  public static void logout() {
    HCSpUtils.setUserPhone("");
    HCSpUtils.setUserUid("");
    //HCSensorsUtil.setUserPhoneProperties("");
  }

  /** 隐藏键盘 */
  public static void hideKeyboard(View view) {
    if (view != null) {
      InputMethodManager imm =
          (InputMethodManager) GlobalData.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  /** 获取当前渠道号 */
  public static String getCurrentChannel() {
    String channel = HCChannelUtil.getChannel(GlobalData.mContext);
    if (TextUtils.isEmpty(channel)) {
      channel = getNormalCurrentChannel();
    }
    return channel;
    //360和小米更新用这个返回.
    //return getNormalCurrentChannel();
  }

  private static String getNormalCurrentChannel() {
    String mPkName = GlobalData.mContext.getPackageName();
    PackageManager mPmanager = GlobalData.mContext.getPackageManager();
    ApplicationInfo mAppInfo;
    try {
      mAppInfo = mPmanager.getApplicationInfo(mPkName, PackageManager.GET_META_DATA);
      Object value = mAppInfo.metaData.get(HCConsts.UMENG_CHANNEL);
      return value.toString();
    } catch (Exception e) {
      return BuildConfig.FLAVOR;
    }
  }

  /** 小米的渠道号 */
  private static final String CHANNEL_XIAOMI_ID = "1105100002";

  public static boolean isXiaoMiChannel() {
    return CHANNEL_XIAOMI_ID.equals(getCurrentChannel());
  }

  public static String getPackageName() {
    return GlobalData.mContext.getPackageName();
  }

  public static Resources getResources() {
    return GlobalData.mContext.getResources();
  }

  public static void toastNetError() {
    HCUtils.showToast(R.string.hc_net_unreachable);
  }

  /** 判断当前是否有可用网络 */
  public static boolean isNetAvailable() {
    ConnectivityManager mConnectManager =
        (ConnectivityManager) GlobalData.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = mConnectManager.getActiveNetworkInfo();
    if (info != null) return info.isAvailable();

    return false;
  }

  /** 获取当前版本号 */
  public static int getAppVersionCode() {
    int versionCode = 0;
    try {
      PackageManager mPackageManager = GlobalData.mContext.getPackageManager();
      PackageInfo mPackageInfo =
          mPackageManager.getPackageInfo(GlobalData.mContext.getPackageName(), 0);
      versionCode = mPackageInfo.versionCode;
    } catch (Exception e) {
    }
    return versionCode;
  }

  /** 获取当前程序版本名称 */
  public static String getAppVersionName() {
    String versionName = "";
    try {
      PackageManager mPackageManager = GlobalData.mContext.getPackageManager();
      PackageInfo mPackageInfo =
          mPackageManager.getPackageInfo(GlobalData.mContext.getPackageName(), 0);
      versionName = mPackageInfo.versionName;
      if (versionName == null || versionName.length() <= 0) {
        versionName = "unknown";
      }
    } catch (Exception e) {
    }
    return versionName;
  }

  /** 显示页码的toast */
  public static void showPageToast(int curPage, int totalPage) {
    curPage = curPage > totalPage ? totalPage : curPage;
    String text = curPage + "/" + totalPage;
    HCNormalToast toast = new HCNormalToast(text);
    toast.show();
  }

  public static void showCountToast(int totalCount) {
    String text = getResString(R.string.hc_total_vehicle, totalCount);
    final Toast toast = Toast.makeText(GlobalData.mContext, text, Toast.LENGTH_SHORT);
    TextView view = new TextView(GlobalData.mContext);
    int lrp = HCUtils.getDimenPixels(R.dimen.px_6dp);
    int tbp = HCUtils.getDimenPixels(R.dimen.px_2dp);
    view.setPadding(lrp, tbp, lrp, tbp);
    view.setGravity(Gravity.CENTER);
    view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
    view.setTextColor(Color.WHITE);
    view.setBackgroundResource(R.drawable.bg_custom_toast);
    view.setText(text);
    toast.setView(view);
    toast.setDuration(Toast.LENGTH_SHORT);
    int yOffset = HCUtils.getDimenPixels(R.dimen.px_100dp);
    toast.setGravity(Gravity.TOP, 0, yOffset);
    toast.show();

    try {
      new Timer().schedule(new TimerTask() {
        @Override public void run() {
          if (toast != null) toast.cancel();
        }
      }, 800);
    } catch (Exception e) {

    }
  }

  public static void showToast(int resString) {
    if (resString <= 0) return;
    String toastStr = getResources().getString(resString);
    showToast(toastStr);
  }

  public static void showToast(String msg) {
    if (TextUtils.isEmpty(msg)) return;

    if (Looper.myLooper() == Looper.getMainLooper()) {
      Toast.makeText(GlobalData.mContext, msg, Toast.LENGTH_SHORT).show();
    } else {
      Looper.prepare();
      Toast.makeText(GlobalData.mContext, msg, Toast.LENGTH_SHORT).show();
      Looper.loop();
    }
  }

  public static int getUserDeviceId() {
    return GlobalData.userDataHelper.getUserDiviceId();
  }

  /** 获取当前屏幕宽度 */
  public static int getScreenWidthInPixels() {
    return getResources().getDisplayMetrics().widthPixels;
  }

  /** 获取当前屏幕高度 */
  public static int getScreenHeightPixels() {
    return getResources().getDisplayMetrics().heightPixels;
  }

  public static int getDimenPixels(int resDimen) {
    return getResources().getDimensionPixelOffset(resDimen);
  }

  /** dp转px */
  public static int dp2px(float dpValue) {
    final float scale = getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /** 获取车辆详情wap地址 */
  public static String getCarDetailURL(int vehicleSourceId) {

    int type = HCSpUtils.getHomeQuizDialog();
    String src1 = "";
    if (type == 1) {
      src1 = "noclose";
    } else if (type == 2) {
      src1 = "hasclose";
    }
    StringBuilder sb = new StringBuilder();
    sb.append(HCDebug.WAP_SERVER);
    sb.append("details/");
    sb.append(vehicleSourceId).append(".html");
    sb.append("?channel=app");
    sb.append("&udid=").append(getUserDeviceId());
    sb.append("&promote_id=").append(HCUtils.getCurrentChannel());
    sb.append("&src=").append(HCSpUtils.getLastCoreOperation());
    sb.append("&src1=").append(src1);
    return sb.toString();
  }

  /** 获取帮买服务地址 */
  public static String getHelpBuyURL() {
    StringBuilder sb = new StringBuilder();
    sb.append(HCDebug.WAP_SERVER).append("bangmai.html").append("?channel=app");
    sb.append("&udid=").append(getUserDeviceId());
    return sb.toString();
  }

  /** 服务保障地址 */
  public static String getServiceURL() {
    StringBuilder sb = new StringBuilder();
    sb.append(HCDebug.WAP_SERVER).append("service.html").append("?channel=app");
    sb.append("&udid=").append(getUserDeviceId());
    return sb.toString();
  }

  /** 拼装优惠券详情wap页 */
  public static String getCouponDetailURL(HCCouponEntity entity) {
    return entity.getUrl();
  }

  /** 拼装使用优惠券wap页 */
  public static String getUseCouponURL(HCCouponEntity entity, String trans_id) {
    StringBuilder sb = new StringBuilder(HCDebug.WAP_SERVER);
    sb.append("coupon/bank_card");
    sb.append("?");
    sb.append("phone");
    sb.append("=");
    sb.append(entity.getPhone());
    sb.append("&");
    sb.append("amount");
    sb.append("=");
    sb.append(entity.getAmount());
    sb.append("&");
    sb.append("coupon_id");
    sb.append("=");
    sb.append(entity.getCoupon_id());
    sb.append("&");
    sb.append("trans_id");
    sb.append("=");
    sb.append(trans_id);
    HCLog.log("use coupon url \n" + sb.toString());
    return sb.toString();
  }

  public static String toHCString(String str) {
    if (TextUtils.isEmpty(str)) return "";

    return str;
  }

  public static String convertImageURL(String url, int w, int h) {
    url = TextUtils.isEmpty(url) ? "" : url;
    String str = new StringBuilder(url).append("?imageView2")
        .append("/1/w/")
        .append(w)
        .append("/h/")
        .append(h)
        .toString();
    return str;
  }

  public static String averageImageURL(String url, int w, int h) {
    url = TextUtils.isEmpty(url) ? "" : url;
    String str = new StringBuilder(url).append("?imageView2")
        .append("/0/w/")
        .append(w)
        .append("/h/")
        .append(h)
        .toString();
    return str;
  }

  public static int str2Int(String str) {
    if (TextUtils.isEmpty(str) || !TextUtils.isDigitsOnly(str)) {
      return 0;
    }
    return Integer.parseInt(str);
  }

  public static float str2Float(String str) {
    if (TextUtils.isEmpty(str)) return 0F;
    return Float.parseFloat(str);
  }

  public static void hideViewIfNeed(View preLoadingView) {
    if (preLoadingView != null && preLoadingView.getVisibility() == View.VISIBLE) {
      preLoadingView.setVisibility(View.GONE);
    }
  }

  public static long getDiffTimeStamp(int diffYear) {
    long cur = System.currentTimeMillis();

    Calendar curCal = Calendar.getInstance();
    curCal.setTimeInMillis(cur);

    int curYear = curCal.get(Calendar.YEAR);

    int resultYear = curYear - diffYear;

    Calendar resultCal = Calendar.getInstance();

    resultCal.set(resultYear, curCal.get(Calendar.MONTH), 1, 0, 0, 0);

    return resultCal.getTimeInMillis();
  }

  public static long[] getYearInterval(int from_year, int to_year) {
    if (to_year == 0 && from_year != 0) {
      to_year = 30;
    }
    long result[] = new long[2];
    result[0] = getDiffTimeStamp(to_year) / 1000;
    result[1] = getDiffTimeStamp(from_year) / 1000;
    return result;
  }

  public static String getOSVersion() {
    return Build.VERSION.RELEASE;
  }

  public static Drawable getResDrawable(int resDrawableID) {
    return getResources().getDrawable(resDrawableID);
  }

  public static String getResString(int resID) {
    return getResources().getString(resID);
  }

  public static String[] getResArray(int res) {
    return getResources().getStringArray(res);
  }

  public static int getResColor(int color) {
    return getResources().getColor(color);
  }

  public static String getResString(int resID, Object... formatArgs) {
    return getResources().getString(resID, formatArgs);
  }

  public static void diaPhone(String phone) {
    if (TextUtils.isEmpty(phone)) return;
    //为了一些平板用户，处理异常
    try {
      Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      GlobalData.mContext.startActivity(intent);
      ThirdPartInjector.statisticsPhone();
    } catch (Exception e) {
    }
  }

  public static boolean isListEmpty(List<?> list) {
    return list == null || list.size() == 0;
  }

  public static boolean isVehicleSold(int status) {
    return status != 1;
  }

  public static String getCurDPI() {
    DisplayMetrics metrics = getResources().getDisplayMetrics();
    int mCurrentDensity = metrics.densityDpi;
    StringBuilder sb = new StringBuilder("now in ");
    if (mCurrentDensity > DisplayMetrics.DENSITY_XHIGH
        && mCurrentDensity <= DisplayMetrics.DENSITY_XXHIGH) {
      //xxh
      sb.append(" xxh ");
    } else if (mCurrentDensity > DisplayMetrics.DENSITY_HIGH
        && mCurrentDensity <= DisplayMetrics.DENSITY_XHIGH) {
      //xh
      sb.append(" xh ");
    } else if (mCurrentDensity <= DisplayMetrics.DENSITY_HIGH) {
      //h
      sb.append(" hdpi ");
    }
    return sb.toString();
  }

  public static void showBangMaiToast() {
    Toast toast = new Toast(GlobalData.mContext);
    toast.setGravity(Gravity.CENTER, 0, 0);

    int res = R.layout.layout_for_bangmai_toast;
    View toastView = LayoutInflater.from(GlobalData.mContext).inflate(res, null);

    toast.setDuration(Toast.LENGTH_SHORT);
    toast.setView(toastView);
    toast.show();
  }

  public static String getNetworkType() {
    String strNetworkType = "NONE";
    ConnectivityManager systemService =
        (ConnectivityManager) GlobalData.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = systemService.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
      if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
        strNetworkType = "WIFI";
      } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
        String strSubTypeName = networkInfo.getSubtypeName();

        // TD-SCDMA   networkType is 17
        int networkType = networkInfo.getSubtype();
        switch (networkType) {
          case TelephonyManager.NETWORK_TYPE_GPRS:
          case TelephonyManager.NETWORK_TYPE_EDGE:
          case TelephonyManager.NETWORK_TYPE_CDMA:
          case TelephonyManager.NETWORK_TYPE_1xRTT:
          case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
            strNetworkType = "2G";
            break;
          case TelephonyManager.NETWORK_TYPE_UMTS:
          case TelephonyManager.NETWORK_TYPE_EVDO_0:
          case TelephonyManager.NETWORK_TYPE_EVDO_A:
          case TelephonyManager.NETWORK_TYPE_HSDPA:
          case TelephonyManager.NETWORK_TYPE_HSUPA:
          case TelephonyManager.NETWORK_TYPE_HSPA:
          case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
          case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
          case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
            strNetworkType = "3G";
            break;
          case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
            strNetworkType = "4G";
            break;
          default:
            //http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
            //TDS_HSDPA 华为3G制式
            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase(
                "WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")
                || strSubTypeName.equalsIgnoreCase("TDS_HSDPA")) {
              strNetworkType = "3G";
            } else {
              strNetworkType = strSubTypeName;
            }
            break;
        }
      }
    }
    return strNetworkType;
  }

  public static String getCurrentDate() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    Date curDate = new Date(System.currentTimeMillis());
    return formatter.format(curDate);
  }

  public static String getCurrentDateForSecond() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    Date curDate = new Date(System.currentTimeMillis());
    return formatter.format(curDate);
  }

  public static Rect getTextRect(String text, float size) {
    Paint pFont = new Paint();
    pFont.setTextSize(size);
    Rect rect = new Rect();
    pFont.getTextBounds(text, 0, text.length(), rect);
    return rect;
  }

  public static String getAllApp() {
    List<AppInfoEntity> infoLists = new ArrayList<>();
    PackageManager manager = GlobalData.mContext.getPackageManager();
    List<PackageInfo> packageInfos = manager.getInstalledPackages(0);
    int count = packageInfos.size();
    String appName;
    String packageName;
    String versionName;
    PackageInfo p;
    for (int i = 0; i < count; i++) {
      p = packageInfos.get(i);
      if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) continue;
      appName = p.applicationInfo.loadLabel(manager).toString();
      packageName = p.packageName;
      versionName = p.versionName;
      if (!TextUtils.isEmpty(appName) && !TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(
          versionName)) {
        infoLists.add(new AppInfoEntity(appName, packageName, versionName));
      }
    }
    String result = new Gson().toJson(infoLists, new TypeToken<List<AppInfoEntity>>() {
    }.getType());

    return Base64Util.encode(result);
  }
}
