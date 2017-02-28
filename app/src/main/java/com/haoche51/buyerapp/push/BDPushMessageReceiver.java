package com.haoche51.buyerapp.push;

import android.content.Context;
import android.text.TextUtils;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.haoche51.buyerapp.entity.push.PushMsgEntity;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCStatistic;
import java.util.List;

/**
 * 推送
 */
public class BDPushMessageReceiver extends PushMessageReceiver {

  public final static String TAG = "bdMessageReceiver";

  @Override
  public void onBind(Context context, int errorCode, String appId, String userId, String channelId,
      String requestId) {
    String logString =
        "baidupush OnBind: errorCode=" + errorCode + " appid=" + appId + " userId=" + userId
            + " channelId=" + channelId + " requestId =" + requestId;
    HCLog.d(TAG, logString);

    if (errorCode == 0 && !TextUtils.isEmpty(userId) && !TextUtils.isEmpty(channelId)) {
      HCPushMessageHelper.requestBindBDServer(userId, channelId);
      HCSpUtils.saveBDUserId(userId);
      HCSpUtils.saveBDChannelId(channelId);
    }
  }

  @Override public void onUnbind(Context context, int i, String s) {

  }

  @Override
  public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

  }

  @Override
  public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

  }

  @Override public void onListTags(Context context, int i, List<String> list, String s) {

  }

  @Override public void onMessage(Context context, String s, String s1) {

  }

  @Override public void onNotificationClicked(Context context, String title, String description,
      String content) {

    if (TextUtils.isEmpty(content)) return;
    content = HCPushMessageHelper.removeUnUseChars(content);
    String msg = "onNotificationClicked title =  " + title + "\n, description = " + description
        + ",\n content = " + content;
    HCLog.d(TAG, msg);

    PushMsgEntity entity = HCPushMessageHelper.parsePushJson(content);
    HCPushMessageHelper.launchToDest(entity);
    //统计 推送点击
    HCStatistic.pushClicked(entity.getType());
  }

  @Override public void onNotificationArrived(Context context, String title, String description,
      String content) {

    String msg = "onNotificationArrived title =  " + title + "\n, description = " + description
        + ",\n content = " + content;
    HCLog.d(TAG, msg);

    if (!TextUtils.isEmpty(content)) {
      try {
        PushMsgEntity entity = HCPushMessageHelper.parsePushJson(content);
        if (entity != null) {
          int type = entity.getType();
          HCPushMessageHelper.setCollectionAndBookingReminder(type);
          //统计 推送到达
          HCStatistic.pushArrived(type);

          HCLog.d(TAG, "统计推送  到达");
        }
      } catch (Exception e) {
        HCLog.d(TAG, "onNotificationArrived  crash ...");
      }
    } else {
      //统计 推送到达
      HCStatistic.pushArrived(0);
    }
  }
}
