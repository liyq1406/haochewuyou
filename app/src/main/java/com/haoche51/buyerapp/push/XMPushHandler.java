package com.haoche51.buyerapp.push;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class XMPushHandler extends Handler {

  private static XMPushHandler INSTANCE;

  /** 保证只有一个XMPushHandler实例 */
  private XMPushHandler() {
  }

  /** 获取XMPushHandler实例 ,单例模式 */
  public static XMPushHandler getInstance() {

    if (INSTANCE == null) {
      synchronized (XMPushHandler.class) {
        if (INSTANCE == null) {
          INSTANCE = new XMPushHandler();
        }
      }
    }

    return INSTANCE;
  }

  @Override public void handleMessage(Message msg) {
    String mi_client_id = (String) msg.obj;
    if (!TextUtils.isEmpty(mi_client_id)) {
      HCPushMessageHelper.requestBindXMServer(mi_client_id);
    }
  }
}
