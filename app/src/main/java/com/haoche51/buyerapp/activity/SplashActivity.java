package com.haoche51.buyerapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.haoche51.buyerapp.BuildConfig;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.HCPollService;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCBrandEntity;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.HCSplashEntity;
import com.haoche51.buyerapp.entity.SplashDataEntity;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.AnalyticsConfig;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/** 启动页 */
public class SplashActivity extends HCCommonTitleActivity {

  public static final String TAG = "hcSplashAct";

  /** 加载图片最大时间 */
  private long maxLoadTime = 3 * 1000;

  private static final int REACH_LOAD_MAX_TIME = 0x1010;

  private static final int SHOW_COUNT_DOWN = 0X1011;

  private int MAX_COUNT_TIME = 5;

  /** 标识当前activity是否允许处理事件 */
  private boolean isAllowHandleEvent = true;

  @InjectView(R.id.frame_bottom) FrameLayout mBottomFrame;
  @InjectView(R.id.iv_splash_core_bg) ImageView mCoreIv;
  @InjectView(R.id.iv_splash_bottom_bg) ImageView mBottomIv;
  @InjectView(R.id.tv_splash_jump) TextView mCountTimeTv;

  private SplashHandler mHandler = new SplashHandler(this);
  private SimpleImageLoadingListener mImgListener;

  private static class SplashHandler extends Handler {
    private WeakReference<SplashActivity> mWeakAct;

    SplashHandler(SplashActivity activity) {
      mWeakAct = new WeakReference<>(activity);
    }

    @Override public void handleMessage(Message msg) {

      final SplashActivity mAct = mWeakAct.get();
      if (mAct != null) {
        switch (msg.what) {
          case REACH_LOAD_MAX_TIME:
            mAct.doFinalGoMain(null);
            break;

          case SHOW_COUNT_DOWN:
            if (mAct.mCountTimeTv != null) {
              if (mAct.mCountTimeTv.getVisibility() != View.VISIBLE) {

                SplashDataEntity entity = HCSpUtils.getSplashBodyEntity();
                int time = entity.getShow_time();
                if (time > 0) {
                  mAct.MAX_COUNT_TIME = time;
                }

                mAct.mCountTimeTv.setVisibility(View.VISIBLE);
                mAct.mHandler.removeMessages(REACH_LOAD_MAX_TIME);

                mAct.mCoreIv.setOnClickListener(new View.OnClickListener() {
                  @Override public void onClick(View v) {
                    String redirectUrl = HCSpUtils.getSplashBodyEntity().getRedirect();
                    int jump = HCSpUtils.getSplashBodyEntity().getJump();
                    if (!TextUtils.isEmpty(redirectUrl) && 0 == jump) {
                      redirectUrl = redirectUrl.trim();
                      mAct.doFinalGoMain(redirectUrl);
                    }
                  }
                });
              }

              String str = HCUtils.getResString(R.string.hc_jump_count, mAct.MAX_COUNT_TIME);
              if (mAct.mCountTimeTv != null) {
                mAct.mCountTimeTv.setText(str);
              }

              mAct.MAX_COUNT_TIME--;

              if (mAct.MAX_COUNT_TIME == -1) {
                mAct.doFinalGoMain(null);
                return;
              }

              mAct.mHandler.sendEmptyMessageDelayed(SHOW_COUNT_DOWN, 1000);
            }
            break;
        }
      }
    }
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    hideTitleBar();
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_splash;
  }

  @Override void initViews() {

    HCEvent.register(this, HCEvent.PRIORITY_HIGH);

    startService(new Intent(this, HCPollService.class));

    doResizeBottom();

    requestSplashData();

    doInjectThirdParty();

    doAppUpdateCheck();

    doSeeIfImportedSeries();

    doSeeIfImportedBrand();

    initFirstStartAPP();
  }

  private void initFirstStartAPP() {

    //因为使用的是美团的多渠道打包,所以要设置渠道.
    AnalyticsConfig.setChannel(HCUtils.getCurrentChannel());
    //记录用户第一次打开app的时间
    if (TextUtils.isEmpty(HCSpUtils.getFirstStartApp())) {
      HCSpUtils.saveFirstStartApp();
      //HCSensorsUtil.setUserOnceProperties();
    }
    //存储用户是否是新用户
    if (TextUtils.isEmpty(HCSpUtils.getKefuPhone())) {
      HCSpUtils.saveIsNewUser(true);
    }
    //如果用户是新用户,判断用户成为新用户的时间,来重置用户现在还是不是新用户
    if (HCSpUtils.getIsNewUser() && !HCSpUtils.getFirstStartApp()
        .equals(HCUtils.getCurrentDate())) {
      HCSpUtils.saveIsNewUser(false);
    }
    //设置公共属性是不是新用户
    HCSensorsUtil.setIsNewUserPublicProperties();
    //HCSensorsUtil.setUserProperties();
    //记录本次打开APP的时间
    HCSpUtils.saveLastStartApp();
    HCSensorsUtil.appLoaded();
  }

  private void doResizeBottom() {
    int sw = HCUtils.getScreenWidthInPixels();
    int sh = HCUtils.getScreenHeightPixels();
    mBottomFrame.getLayoutParams().height = (int) (sh * 0.2F);
    mCoreIv.getLayoutParams().width = sw;
    mCoreIv.getLayoutParams().height = sh;

    Drawable bd = mBottomIv.getDrawable();
    if (bd != null) {
      int bw = bd.getIntrinsicWidth();
      int bh = bd.getIntrinsicHeight();
      mBottomIv.getLayoutParams().width = bw;
      mBottomIv.getLayoutParams().height = bh;
    }
  }

  /** 请求图片 */
  private void requestSplashData() {
    if (!HCUtils.isNetAvailable()) {
      doWhenNetUnAvailable();
    } else {
      try {
        Map<String, Object> params = HCParamsUtil.getSplashData();
        API.post(new HCRequest(params, new HCSimpleCallBack() {
          @Override public void onHttpFinish(String responseJsonString) {
            handleSplashData(responseJsonString);
          }
        }));
      } catch (Exception e) {
        HCLog.d(TAG, "requestSplashData is crash ...");
      }

      //开始计时
      mHandler.sendEmptyMessageDelayed(REACH_LOAD_MAX_TIME, maxLoadTime);
    }
  }

  private void doWhenNetUnAvailable() {
    if (HCSpUtils.getSplashBodyEntity().getId() > 0) {
      tryLoadBodyPic();
      tryLoadFootPic();
    }
    mHandler.sendEmptyMessageDelayed(REACH_LOAD_MAX_TIME, maxLoadTime);
  }

  private void handleSplashData(String resp) {
    if (!TextUtils.isEmpty(resp)) {
      HCSplashEntity entity = HCJSONParser.parseSplashData(resp);
      SplashDataEntity bodyData = entity.getBody();
      SplashDataEntity footData = entity.getFoot();

      if (bodyData != null) {
        int spBodyId = HCSpUtils.getSplashBodyEntity().getId();
        if (spBodyId == bodyData.getId()) {
          //说明此时的图片有效
          tryLoadBodyPic();
        }
      }

      if (footData != null) {
        int spFootId = HCSpUtils.getSplashFootEntity().getId();
        if (spFootId == footData.getId()) {
          tryLoadFootPic();
        }
      }

      ImageLoaderHelper.handleSplashData(resp, maxLoadTime);
    }
  }

  // 嵌入第三方
  private void doInjectThirdParty() {
    ThirdPartInjector.enableUMUpdateOnlineConfig();
    ThirdPartInjector.disableUMAutoActivityAnalysis();
    ThirdPartInjector.startPush();
  }

  private void doSeeIfImportedSeries() {
    // 如果没有导入过车系表数据,导入
    if (!HCSpUtils.hasImportedcariesTable()) {
      // 导入车系表数据
      HCDbUtil.insertSeriesData();
    }
  }

  private void doSeeIfImportedBrand() {
    // 如果没有导入过品牌数据,导入
    if (!HCSpUtils.hasImportedbrandTable()) {
      // 导入品牌数据
      requestSupportBrands();
    }
  }

  private void requestSupportBrands() {
    Map<String, Object> params = HCParamsUtil.getSupportBrand();
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleSupportBrands(responseJsonString);
      }
    }));
  }

  private void handleSupportBrands(String responseJsonString) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      List<HCBrandEntity> brands = HCJSONParser.parseBrand(responseJsonString);
      if (!HCUtils.isListEmpty(brands)) {
        HCDbUtil.updateBrand(brands);
        HCSpUtils.setImportedbrandTable();
        HCSpUtils.setLastCityForBrand(HCDbUtil.getSavedCityId() + "");
      }
    }
  }

  private void doAppUpdateCheck() {
    if (BuildConfig.ENABLE_UPDATE) {
      doBaiDuUpdate();
      //doXiaoMiUpdate();
      //do360UpDate();
    }
  }

  private void doBaiDuUpdate() {
    BDAutoUpdateSDK.uiUpdateAction(this, new UICheckUpdateCallback() {
      @Override public void onCheckComplete() {
      }
    });
  }

  //private void doXiaoMiUpdate() {
  //  try {
  //    XiaomiUpdateAgent.setCheckUpdateOnlyWifi(true);
  //    XiaomiUpdateAgent.update(this);
  //  } catch (Exception e) {
  //    HCLog.d(TAG, "又是小米的渣渣更新");
  //  }
  //}

  //private void do360UpDate() {
  //  UpdateManager.checkUpdate(this, true, true, HCUtils.getPackageName(),
  //      new UpdateManager.CheckUpdateListener() {
  //        @Override public void onResult(boolean b, Bundle bundle) {
  //        }
  //      });
  //}

  private void doFinalGoMain(String url) {

    isAllowHandleEvent = false;

    Intent intent = new Intent(GlobalData.mContext, MainActivity.class);
    if (!TextUtils.isEmpty(url)) {
      intent.putExtra(HCConsts.INTENT_KEY_URL, url);
    }

    startActivity(intent);

    finish();
  }

  public void onEvent(HCCommunicateEntity entity) {
    if (!isAllowHandleEvent || mHandler == null) return;
    String action = entity.getAction();
    if (TextUtils.isEmpty(action)) return;

    switch (action) {
      case HCEvent.ACTION_SPLASH_BODY_LOADED:
        tryLoadBodyPic();
        break;

      case HCEvent.ACTION_SPLASH_FOOT_LOADED:
        tryLoadFootPic();
        break;
    }
  }

  private void tryLoadBodyPic() {
    if (mCoreIv != null) {
      String url = HCSpUtils.getSplashBodyEntity().getImage_url();

      mImgListener = new SimpleImageLoadingListener() {
        @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
          super.onLoadingComplete(imageUri, view, loadedImage);
          if (mHandler != null && mCoreIv != null && loadedImage != null) {
            mCoreIv.setImageBitmap(loadedImage);
            //开启倒计时
            mHandler.sendEmptyMessage(SHOW_COUNT_DOWN);
          }
        }
      };

      ImageLoaderHelper.simpleDisplay(url, mCoreIv, mImgListener);
    }
  }

  private void tryLoadFootPic() {
    if (mHandler != null && mBottomIv != null) {
      String url = HCSpUtils.getSplashFootEntity().getImage_url();
      ImageLoaderHelper.simpleDisplay(url, mBottomIv);
    }
  }

  public void releaseBitmap(ImageView iv) {
    if (iv != null) {
      Drawable d = iv.getDrawable();
      if (d != null && d instanceof BitmapDrawable) {
        iv.setImageBitmap(null);
      }
    }
  }

  @Override protected void onDestroy() {
    HCEvent.unRegister(this);
    releaseBitmap(mCoreIv);
    releaseBitmap(mBottomIv);
    mImgListener = null;
    if (mHandler != null) {
      mHandler.removeCallbacksAndMessages(null);
      mHandler = null;
    }
    super.onDestroy();
  }

  @Override protected void onResume() {
    super.onResume();
    doThirdPartyResume();
    isAllowHandleEvent = true;
  }

  @Override protected void onPause() {
    isAllowHandleEvent = false;
    super.onPause();
    doThirdPartyPause();
  }

  @Override protected void onStop() {
    isAllowHandleEvent = false;
    super.onStop();
  }

  @OnClick({ R.id.tv_splash_jump, }) public void onClick(View v) {
    doFinalGoMain(null);
  }

  private void doThirdPartyResume() {
    String name = this.getClass().getSimpleName();
    ThirdPartInjector.onPageStart(name);
    ThirdPartInjector.onResume(this);
  }

  private void doThirdPartyPause() {
    String name = this.getClass().getSimpleName();
    ThirdPartInjector.onPageEnd(name);
    ThirdPartInjector.onPause(this);
  }
}
