package com.haoche51.buyerapp.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 生成好车无忧自己的设备唯一标识
 * 该id的组成为当前渠道号 + 设备唯一标识 然后取md5指纹
 * 设备唯一标识的生成方式:
 * 尝试读取 网卡地址,IMEI,序列号
 * 如果都读不到使用应用唯一的Installation
 * 卸载了就变了这个东西。
 */
public class HCDeviceIDGenerator {

  private final static String TAG = "HCDeviceIDGenerator";

  private static final String NOT_READED_IMEI = "000000000000000";

  public static String id(Context context) {

    String result = getInstallID(context);

    //先imei
    try {
      TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      String imei = tm.getDeviceId();
      //序列号（sn）
      String sn = tm.getSimSerialNumber();
      //wifi mac地址
      WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      WifiInfo info = wifi.getConnectionInfo();

      if (!NOT_READED_IMEI.equals(imei) && !TextUtils.isEmpty(imei)) {
        if (TextUtils.isDigitsOnly(imei)) {
          result = imei;
          HCLog.d(TAG, "READ imei " + imei);
        }
      } else if (!TextUtils.isEmpty(sn)) {
        result = sn;
        HCLog.d(TAG, "READ sn " + sn);
      } else if (info != null && !TextUtils.isEmpty(info.getMacAddress())) {
        result = info.getMacAddress();
        HCLog.d(TAG, "READ wifi mac " + result);
      }
    } catch (Exception e) {
      result = Installation.id(context);
    } finally {
      return md5(result);
    }
  }

  private static String md5(String input) {
    String str = Build.MANUFACTURER + Build.DISPLAY + Build.BOARD + HCUtils.getScreenWidthInPixels()
        + HCUtils.getScreenHeightPixels() + HCUtils.getCurDPI();
    input = str + input;
    HCLog.d(TAG, "before md5 " + input);
    HCLog.d(TAG, "After md5  " + MD5Utils.GetMD5Code(input));
    return MD5Utils.GetMD5Code(input);
  }

  /**
   * 得到应用唯一的installID.
   */
  private static String getInstallID(Context context) {
    String result = "";
    try {
      result = Installation.id(context);
    } catch (Exception e) {

    }
    return result;
  }
}
