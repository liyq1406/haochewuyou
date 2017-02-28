package com.haoche51.buyerapp.util;

import android.app.Activity;
import android.text.TextUtils;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.haoche51.buyerapp.BuildConfig;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.push.BDPushMessageReceiver;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.push.XMPushMessageReceiver;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 * 统计,不直接调用umeng提供的API,以防被测试环境数据污染
 *
 * 在这里统一第三方集成
 */
public class ThirdPartInjector {

  /** 百度push key */
  private final static String BD_PUSH_KEY = "h1zaG4axjNbebV85E4uoXiMN";

  /** 小米push app_id */
  private static final String XM_PUSH_APP_ID = "2882303761517322092";

  /** 小米push app_key */
  private static final String XM_PUSH_APP_KEY = "5371732273092";

  public static void startPush() {
    if (HCUtils.isXiaoMiChannel()) {
      MiPushClient.registerPush(GlobalData.mContext, XM_PUSH_APP_ID, XM_PUSH_APP_KEY);
      HCLog.d(XMPushMessageReceiver.TAG,
          "ThirdPart----- start push now ...." + HCUtils.now() + "\n\n");
    } else {
      PushManager.startWork(GlobalData.mContext, PushConstants.LOGIN_TYPE_API_KEY, BD_PUSH_KEY);
      HCLog.d(BDPushMessageReceiver.TAG,
          "ThirdPart----- start push now ...." + HCUtils.now() + "\n\n");
    }
  }

  /** 统计好车无忧的电话拨打 */
  static void statisticsPhone() {
    API.post(new HCRequest(HCParamsUtil.dataStatistics(), new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
      }
    }));
  }

  public static void onEvent(Activity mAct, String event) {

    if (mAct == null || TextUtils.isEmpty(event)) return;

    if (BuildConfig.ENABLE_UMENG_ANALYTICS) {
      MobclickAgent.onEvent(mAct, event);
    }
  }

  public static void onPageStart(String tag) {
    if (TextUtils.isEmpty(tag)) return;

    if (BuildConfig.ENABLE_UMENG_ANALYTICS) {
      MobclickAgent.onPageStart(tag);
    }
  }

  public static void onPageEnd(String tag) {
    if (TextUtils.isEmpty(tag)) return;

    if (BuildConfig.ENABLE_UMENG_ANALYTICS) {
      MobclickAgent.onPageEnd(tag);
    }
  }

  public static void onResume(Activity mAct) {
    if (mAct == null) return;

    if (BuildConfig.ENABLE_UMENG_ANALYTICS) {
      MobclickAgent.onResume(mAct);
    }
  }

  public static void onPause(Activity mAct) {
    if (mAct == null) return;

    if (BuildConfig.ENABLE_UMENG_ANALYTICS) {
      MobclickAgent.onPause(mAct);
    }
  }

  public static void enableUMUpdateOnlineConfig() {
    MobclickAgent.updateOnlineConfig(GlobalData.mContext);
  }

  //禁止默认的页面统计方式，这样将不会再自动统计Activity。 false

  /** 关闭友盟自动页面统计(基于Activity) */
  public static void disableUMAutoActivityAnalysis() {
    MobclickAgent.openActivityDurationTrack(false);
  }

  /** 关闭友盟错误统计 */
  public static void disableUMCrashAnalysis() {
    MobclickAgent.setCatchUncaughtExceptions(false);
  }
}
