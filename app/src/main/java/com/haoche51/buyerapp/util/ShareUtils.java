package com.haoche51.buyerapp.util;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCDetailEntity;
import com.haoche51.onekeyshare.CustomShareFieldsPage;
import com.haoche51.onekeyshare.OnekeyShare;
import com.haoche51.onekeyshare.OnekeyShareTheme;
import com.haoche51.onekeyshare.ShareContentCustomizeCallback;
import java.util.HashMap;

public class ShareUtils {
  public static void share(Activity mAct, String shareTitle, String shareText, String shareLinkUrl,
      String sharePicUrl) {
    if (!TextUtils.isEmpty(shareText) && !TextUtils.isEmpty(shareLinkUrl) && !TextUtils.isEmpty(
        sharePicUrl)) {

      if (mAct == null || mAct.isFinishing()) return;

      ShareSDK.initSDK(mAct);
      final OnekeyShare oks = new OnekeyShare();
      oks.setNotification(R.drawable.ic_launcher, mAct.getString(R.string.app_name));
      oks.setTitle(shareTitle);
      oks.setTitleUrl(shareLinkUrl);
      oks.setText(shareText);
      oks.setImageUrl(sharePicUrl);
      // url仅在微信（包括好友和朋友圈）中使用
      oks.setUrl(shareLinkUrl);
      String theme = CustomShareFieldsPage.getString("theme", "classic");
      if (OnekeyShareTheme.SKYBLUE.toString().toLowerCase().equals(theme)) {
        oks.setTheme(OnekeyShareTheme.SKYBLUE);
      } else {
        oks.setTheme(OnekeyShareTheme.CLASSIC);
      }

      // 令编辑页面显示为Dialog模式
      oks.setDialogMode();

      // 在自动授权时可以禁用SSO方式
      // if(!CustomShareFieldsPage.getBoolean("enableSSO", true))
      oks.disableSSOWhenAuthorize();

      // 设置kakaoTalk分享链接时，点击分享信息时，如果应用不存在，跳转到应用的下载地址
      oks.setInstallUrl("http://www.mob.com");
      // 设置kakaoTalk分享链接时，点击分享信息时，如果应用存在，打开相应的app
      oks.setExecuteUrl("kakaoTalkTest://starActivity");
      oks.setShareContentCustomizeCallback(
          new HCShareContentCustomize(shareText + shareLinkUrl, shareText));
      oks.show(mAct);
    }
  }

  /**
   * 快捷分享项目现在添加为不同的平台添加不同分享内容的方法。
   */
  public static class HCShareContentCustomize implements ShareContentCustomizeCallback {
    private String shareText;
    private String shareTitle;

    public HCShareContentCustomize(String shareText, String shareTitle) {
      this.shareText = shareText;
      this.shareTitle = shareTitle;
    }

    @Override public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
      if (SinaWeibo.NAME.equals(platform.getName())) {
        paramsToShare.setText(shareText);
      } else if (WechatMoments.NAME.equals(platform.getName())) {
        paramsToShare.setTitle(shareTitle);
      }
    }
  }

  private static View view;
  private static Dialog dialog;
  private static MyPlatformActionListener listener = new MyPlatformActionListener();

  private static void initDialog(Activity activity, String vehicleName, String formatShare,
      String imgeUrl, String mLoadURL, HCDetailEntity mDetailEntity, String vehicleChannel) {
    view = LayoutInflater.from(activity).inflate(R.layout.sharesdk_dialog, null);
    dialog = new Dialog(activity, R.style.sub_condition_dialog);
    dialog.setContentView(view);
    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.gravity = Gravity.BOTTOM;
    dialog.show();
    ClickListener l = new ClickListener(vehicleName, formatShare, imgeUrl, mLoadURL, mDetailEntity,
        vehicleChannel);
    view.findViewById(R.id.sharesdk_wechat).setOnClickListener(l);
    view.findViewById(R.id.sharesdk_wechatmoments).setOnClickListener(l);
    view.findViewById(R.id.sharesdk_sinaweibo).setOnClickListener(l);
    view.findViewById(R.id.sharesdk_qq).setOnClickListener(l);
  }

  public static void shareVehicle(Activity mAct, String vehicleName, String formatShare,
      String imageUrl, String mLoadURL, HCDetailEntity mDetailEntity, String vehicleChannel) {
    ShareSDK.initSDK(mAct);
    initDialog(mAct, vehicleName, formatShare, imageUrl, mLoadURL, mDetailEntity, vehicleChannel);
  }

  static class ClickListener implements View.OnClickListener {
    private String vehicleName;
    private String imgeUrl;
    private String mLoadURL;
    private String formatShare;
    private HCDetailEntity mDetailEntity;
    private String vehicleChannel;

    public ClickListener(String vehicleName, String formatShare, String imgeUrl, String mLoadURL,
        HCDetailEntity mDetailEntity, String vehicleChannel) {
      this.vehicleName = vehicleName;
      this.formatShare = formatShare;
      this.imgeUrl = imgeUrl;
      this.mLoadURL = mLoadURL;
      this.mDetailEntity = mDetailEntity;
      this.vehicleChannel = vehicleChannel;
    }

    @Override public void onClick(View v) {
      switch (v.getId()) {
        case R.id.sharesdk_wechat:
          Wechat.ShareParams wechatShareParams = new Wechat.ShareParams();

          Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
          shareTo(wechatShareParams, wechat, vehicleName, formatShare, imgeUrl, mLoadURL);
          HCSensorsUtil.shareDetail(vehicleChannel, mDetailEntity, "微信好友");
          break;
        case R.id.sharesdk_wechatmoments:
          WechatMoments.ShareParams wechatMomentsShareParams = new WechatMoments.ShareParams();
          Platform wechatmoments = ShareSDK.getPlatform(WechatMoments.NAME);
          shareTo(wechatMomentsShareParams, wechatmoments, vehicleName, formatShare, imgeUrl,
              mLoadURL);
          HCSensorsUtil.shareDetail(vehicleChannel, mDetailEntity, "微信朋友圈");
          break;
        case R.id.sharesdk_sinaweibo:
          SinaWeibo.ShareParams sinaShareParams = new SinaWeibo.ShareParams();
          Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
          shareTo(sinaShareParams, sina, vehicleName, formatShare, imgeUrl, mLoadURL);
          HCSensorsUtil.shareDetail(vehicleChannel, mDetailEntity, "新浪微博");
          break;
        case R.id.sharesdk_qq:
          QQ.ShareParams qqShareParams = new QQ.ShareParams();
          Platform qq = ShareSDK.getPlatform(QQ.NAME);
          shareTo(qqShareParams, qq, vehicleName, formatShare, imgeUrl, mLoadURL);
          HCSensorsUtil.shareDetail(vehicleChannel, mDetailEntity, "QQ好友");
          break;
      }
    }
  }

  private static void shareTo(Platform.ShareParams params, Platform platform, String vehicleName,
      String formatShare, String imgeUrl, String mLoadURL) {
    initShareData(params, vehicleName, formatShare, imgeUrl, mLoadURL);
    platform.setPlatformActionListener(listener);
    platform.share(params);
    dialog.dismiss();
  }

  private static void initShareData(Platform.ShareParams params, String vehicleName,
      String formatShare, String imgeUrl, String mLoadURL) {
    if (params instanceof QQ.ShareParams || params instanceof SinaWeibo.ShareParams) {
      params.setTitleUrl(mLoadURL);
      params.setTitle(vehicleName);
      params.setText(formatShare + mLoadURL);
      params.setImageUrl(imgeUrl);
    } else if (params instanceof WechatMoments.ShareParams
        || params instanceof Wechat.ShareParams) {
      params.setShareType(Platform.SHARE_WEBPAGE);
      params.setTitleUrl(mLoadURL);
      params.setText(formatShare + mLoadURL);
      params.setTitle(vehicleName);
      params.setImageUrl(imgeUrl);
      params.setUrl(mLoadURL);
    }
  }

  /**
   * 分享事件回调
   */

  static class MyPlatformActionListener implements PlatformActionListener {
    @Override public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
      HCUtils.showToast("分享成功");
    }

    @Override public void onError(Platform platform, int i, Throwable throwable) {
      if ("cn.sharesdk.wechat.utils.WechatClientNotExistException".equals(throwable.toString())) {
        HCUtils.showToast("你还没有安装微信");
      }
    }

    @Override public void onCancel(Platform platform, int i) {
      HCUtils.showToast("你取消了分享");
    }
  }
}
