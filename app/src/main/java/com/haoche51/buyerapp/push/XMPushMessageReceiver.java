package com.haoche51.buyerapp.push;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.entity.push.PushMsgEntity;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import java.util.List;

public class XMPushMessageReceiver extends PushMessageReceiver {
  public static String TAG = "XMPushMessageReceiver";

  /** 用来接受客户端向服务器发送注册命令消息后返回的响应。 */
  @Override public void onReceiveRegisterResult(Context context,
      MiPushCommandMessage miPushCommandMessage) {

    String command = miPushCommandMessage.getCommand();
    List<String> arguments = miPushCommandMessage.getCommandArguments();
    String cmdArg1 = !HCUtils.isListEmpty(arguments) ? arguments.get(0) : null;
    HCLog.d(TAG, "ErrorCode == " + miPushCommandMessage.getResultCode());
    //如果是注册的消息返回
    if (MiPushClient.COMMAND_REGISTER.equals(command)) {
      //如果是注册成功的code
      if (miPushCommandMessage.getResultCode() == ErrorCode.SUCCESS) {
        //cmdArg1即为绑定成功的ID
        if (!TextUtils.isEmpty(cmdArg1)) {
          if (!GlobalData.userDataHelper.isBindToXMServer()) {
            Message msg = Message.obtain();
            msg.obj = cmdArg1;
            XMPushHandler.getInstance().sendMessage(msg);
          }
          HCSpUtils.saveXMClientId(cmdArg1);
          HCLog.d(TAG, "onReceiveRegisterResult ... regId ... " + cmdArg1);
        }
      }
    }
  }

  /** 用来接收服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息） */
  @Override public void onNotificationMessageArrived(Context context, MiPushMessage miPushMessage) {
    if (TextUtils.isEmpty(miPushMessage.getContent())) return;
    String content = HCPushMessageHelper.removeUnUseChars(miPushMessage.getContent());
    String msg = "onNotificationMessageArrived ... description == " + miPushMessage.getDescription()
        + "....title == " + miPushMessage.getTitle() + "....content == " + content;
    HCLog.d(TAG, msg);
    if (!TextUtils.isEmpty(content)) {
      try {
        PushMsgEntity entity = HCPushMessageHelper.parsePushJson(content);
        if (entity != null) {
          int type = entity.getType();
          HCPushMessageHelper.setCollectionAndBookingReminder(type);
          //统计 推送到达
          HCStatistic.pushArrived(type);
        }
      } catch (Exception e) {
        HCLog.d(TAG, "onNotificationArrived  crash ...");
      }
    } else {
      //统计 推送到达
      HCStatistic.pushArrived(0);
    }
  }

  /** 用来接收服务器发来的通知栏消息（用户点击通知栏时触发） */
  @Override public void onNotificationMessageClicked(Context context, MiPushMessage miPushMessage) {
    if (TextUtils.isEmpty(miPushMessage.getContent())) return;
    String content = HCPushMessageHelper.removeUnUseChars(miPushMessage.getContent());
    String msg = "onNotificationMessageClicked ... description == " + miPushMessage.getDescription()
        + "....title == " + miPushMessage.getTitle() + "....content == " + content;
    HCLog.d(TAG, msg);
    PushMsgEntity entity = HCPushMessageHelper.parsePushJson(content);
    HCPushMessageHelper.launchToDest(entity);
    //统计 推送点击
    HCStatistic.pushClicked(entity.getType());
  }

  /** 用来接收服务器发送的透传消息 */
  @Override public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
  }

  /** 用来接收客户端向服务器发送命令消息后返回的响应 */
  @Override public void onCommandResult(Context context,
      MiPushCommandMessage miPushCommandMessage) {
  }
}
