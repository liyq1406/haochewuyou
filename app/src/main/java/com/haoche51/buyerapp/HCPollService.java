package com.haoche51.buyerapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.HCDataIntEntity;
import com.haoche51.buyerapp.entity.HCLocationEntity;
import com.haoche51.buyerapp.entity.HCSyncSubContionEntity;
import com.haoche51.buyerapp.entity.SplashDataEntity;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.push.HCPushMessageHelper;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;
import java.util.List;
import java.util.Map;

/***
 * 这样在MainActivity退出的时候就自动停止service里的轮询了
 */
public class HCPollService extends Service {

  public static final String TAG = "HCPollService";

  private final static int WHAT_COUPON = 0X1011;
  private final static int WHAT_FEEDBACK = 0X1012;

  private final static int WHAT_RETRY_BIND = 0X2010;

  /**
   * 优惠券刷新频率(毫秒)
   */
  private final static long RATE_COUPON = 1000 * 58 * 60 * 2;
  /**
   * 意见反馈刷新频率(毫秒)
   */
  private final static long RATE_FEEDBACK = 1000 * 57 * 60 * 2;

  /**
   * 重试百度推送频率
   */
  private final static long RATE_PUSH = 1000 * 20;

  private int retryCount = 2;

  private static Handler mHandler;

  private HCLocationEntity mLocEntity = null;
  private LocationClient mLocationClient = null;

  public void onCreate() {
    super.onCreate();
    initLocation();
    syncSubscribe();
    sendApp();
    HCEvent.register(this);
  }

  private void sendApp() {
    if (!HCSpUtils.getHasSendApp()) {
      String appStr = HCUtils.getAllApp();
      Map<String, Object> params = HCParamsUtil.getApp(appStr);
      API.post(new HCRequest(params, new HCSimpleCallBack() {
        @Override public void onHttpFinish(String responseJsonString) {
        }
      }));
      HCSpUtils.saveHasSendApp();
    }
  }

  private void initLocation() {

    mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类

    LocationClientOption option = new LocationClientOption();
    option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
    option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
    int span = 0;
    option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
    option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
    option.setOpenGps(true);// 可选，默认false,设置是否使用gps
    option.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
    // option.setIsNeedLocationDescribe(true);//
    // 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
    // option.setIsNeedLocationPoiList(true);//
    // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
    // option.setIgnoreKillProcess(false);//
    // 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
    // option.SetIgnoreCacheException(false);//
    // 可选，默认false，设置是否收集CRASH信息，默认收集
    // option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
    mLocationClient.setLocOption(option);
    mLocationClient.start();

    // 注册监听函数
    mLocationClient.registerLocationListener(new BDLocationListener() {
      @Override public void onReceiveLocation(BDLocation location) {
        if (location != null) {
          String city_name = location.getCity();
          double latitude = location.getLatitude();
          double longitude = location.getLongitude();
          HCLog.d(TAG, "city_name == " + city_name);
          HCLog.d(TAG, "latitude == " + latitude);
          HCLog.d(TAG, "longitude == " + longitude);
          HCLog.d(TAG, "type == " + location.getLocType());
          mLocEntity = new HCLocationEntity(latitude, longitude, city_name);
          if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
            mLocationClient = null;
          }
        }
      }
    });
  }

  @Override public IBinder onBind(Intent intent) {
    startCheck();
    return new HCServiceBinder();
  }

  private void startCheck() {
    if (mHandler == null) {
      mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
          switch (msg.what) {
            case WHAT_COUPON:
              doCheckCoupon();
              break;
            case WHAT_FEEDBACK:
              doCheckFeedBack();
              break;

            case WHAT_RETRY_BIND:
              if (!GlobalData.userDataHelper.isBindToServer()) {
                doRetryPushBind();
              } else {
                if (mHandler != null) {
                  mHandler.removeMessages(WHAT_RETRY_BIND);
                  HCLog.d(HCPushMessageHelper.TAG,
                      "Service doRetryPushBind has bind remove retry  ");
                }
              }
              break;
          }
        }
      };
    }

    mHandler.sendEmptyMessageDelayed(WHAT_RETRY_BIND, RATE_PUSH);

    doCheckCoupon();

    loadUnLoadedPic();
  }

  private void stopCheck() {

    if (mLocationClient != null && mLocationClient.isStarted()) {
      mLocationClient.stop();
      mLocationClient = null;
    }

    if (mHandler != null) {
      mHandler.removeCallbacksAndMessages(null);
      mHandler = null;
    }
  }

  @Override public boolean onUnbind(Intent intent) {
    HCEvent.unRegister(this);
    stopCheck();
    return super.onUnbind(intent);
  }

  public void onEvent(HCCommunicateEntity entity) {
    if (entity != null) {
      String action = entity.getAction();
      if (HCEvent.ACTION_LOGINSTATUS_CHANGED.equals(action)) {
        //登录状态改变了
        boolean isLogined = HCUtils.isUserLogined();
        if (isLogined) {
          stopCheck();
          startCheck();
        } else {
          stopCheck();
        }
      }
    }
  }

  /**
   * 检查意见反馈
   */
  private void doCheckFeedBack() {
    FeedbackAgent mAgent = new FeedbackAgent(this);
    Conversation mComversation = mAgent.getDefaultConversation();
    mComversation.sync(new SyncListener() {
      @Override public void onSendUserReply(List<Reply> replyList) {
      }

      @Override public void onReceiveDevReply(List<Reply> replyList) {
        if (replyList != null && !replyList.isEmpty()) {
          notifyFeedbackReminder();
        }
      }
    });

    if (mHandler != null) {
      mHandler.sendMessageDelayed(mHandler.obtainMessage(WHAT_FEEDBACK), RATE_FEEDBACK);
    }
  }

  private void doRetryPushBind() {

    HCLog.d(HCPushMessageHelper.TAG, "Service doRetryPushBind  ");

    ThirdPartInjector.startPush();

    retryCount--;

    if (mHandler != null) {
      if (retryCount > 0) {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(WHAT_RETRY_BIND), 1000 * 30);
      } else {
        //重试多次均无法绑定.
        //防止正好在请求绑定server,延迟一秒执行
        mHandler.postDelayed(new Runnable() {
          @Override public void run() {
            if (mHandler != null) {
              if (HCUtils.isXiaoMiChannel()) {
                HCPushMessageHelper.requestBindXMServer(HCSpUtils.getXMClientId());
              } else {
                HCPushMessageHelper.requestBindBDServer("", "");
              }
            }
          }
        }, 1000);
        HCLog.d(HCPushMessageHelper.TAG, "Service reach max time  ");
      }
    }
  }

  /**
   * 检查我的优惠券
   */
  private void doCheckCoupon() {
    if (!HCUtils.isUserLogined()) {
      return;
    }
    String phone = HCSpUtils.getUserPhone();
    String last_check_time = HCSpUtils.getLastCheckCouponTime();
    Map<String, Object> params = HCParamsUtil.checkCouponReminder(phone, last_check_time);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        if (!TextUtils.isEmpty(responseJsonString)) {
          HCDataIntEntity entity = HCJSONParser.parseCouponReminderCount(responseJsonString);
          notifyCouponReminder(entity);
        }
      }
    }));

    HCSpUtils.setLastCheckCouponTimeToNow();

    if (mHandler != null) {
      mHandler.sendMessageDelayed(mHandler.obtainMessage(WHAT_COUPON), RATE_COUPON);
    }
  }

  private void notifyCouponReminder(HCDataIntEntity entity) {
    if (entity == null || entity.getData() <= 0) return;
    int count = entity.getData();
    HCSpUtils.setProfileCouponReminder(count);
    HCEvent.postEvent(HCEvent.ACTION_COUPON_REMINDER);
  }

  private void notifyFeedbackReminder() {
    int count = HCSpUtils.getProfileFeedbackReminder() + 1;
    HCSpUtils.setProfileFeedbackReminder(count);
    HCEvent.postEvent(HCEvent.ACTION_FEEDBACK_REMINDER);
  }

  public class HCServiceBinder extends Binder {
    /**
     * 获取百度定位 城市
     */
    public HCLocationEntity getBaiduLocation() {
      return mLocEntity;
    }

    /**
     * 获取当前service对象
     */
    public HCPollService getPollService() {
      return HCPollService.this;
    }
  }

  /**
   * 同步服务器端的订阅数据到本地
   */
  private void syncSubscribe() {
    if (HCUtils.isUserLogined()) {
      Map<String, Object> params = HCParamsUtil.getUserAllSubscribesCondition();
      HCRequest syncSubRequest = new HCRequest(params, new HCSimpleCallBack() {
        @Override public void onHttpFinish(String resp) {
          if (!TextUtils.isEmpty(resp)) {
            final HCSyncSubContionEntity entity = HCJSONParser.parseSyncSubCondition(resp);
            if (entity != null) {
              HCSpUtils.setAllSubscribe(entity.getData());
            }
          }
        }
      });

      API.post(syncSubRequest);
    }
  }

  private void loadUnLoadedPic() {
    if (HCSpUtils.getUnLoadedSplashBodyEntity().getId() > 0) {
      //body有没有加载完的
      SplashDataEntity unLoadedBody = HCSpUtils.getUnLoadedSplashBodyEntity();
      ImageLoaderHelper.handleBodyData(unLoadedBody, 0);
    }

    if (HCSpUtils.getUnLoadedSplashFootEntity().getId() > 0) {
      //foot有没有加载完的
      SplashDataEntity unloadedFoot = HCSpUtils.getUnLoadedSplashFootEntity();
      ImageLoaderHelper.handleFootData(unloadedFoot, 0);
    }
  }
}
