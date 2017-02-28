package com.haoche51.buyerapp;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import com.haoche51.buyerapp.push.XMPushHandler;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCUtils;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.leakcanary.LeakCanary;

public class HCApplication extends Application {

  private static final String HCPKNAME = "com.haoche51.buyerapp";

  @Override public void onLowMemory() {
    super.onLowMemory();
    ImageLoader.getInstance().clearMemoryCache();
  }

  @Override public void onCreate() {
    super.onCreate();
    //确保只执行一次
    if (HCPKNAME.equals(getAppNameByPID(this, Process.myPid()))) {
      doAppInit();
    }
  }

  private void doAppInit() {
    GlobalData.init(getApplicationContext());
    initImageLoader(getApplicationContext());
    HCCrashHandler crashHandler = HCCrashHandler.getInstance();
    crashHandler.init(getApplicationContext());
    if (HCUtils.isXiaoMiChannel() && !GlobalData.userDataHelper.isBindToXMServer()) {
      XMPushHandler.getInstance();
    }
    /** 神策初始化 */
    HCSensorsUtil.init(getApplicationContext());

    if (BuildConfig.DEBUG) {
      LeakCanary.install(this);
    }
  }

  private void initImageLoader(Context context) {
    ImageLoaderConfiguration config =
        new ImageLoaderConfiguration.Builder(context).threadPoolSize(3)// 线程池内加载的数量
            .threadPriority(Thread.NORM_PRIORITY - 1)
            .denyCacheImageMultipleSizesInMemory()
            .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
            .tasksProcessingOrder(QueueProcessingType.LIFO)
            .build();
    ImageLoader.getInstance().init(config);// 全局初始化此配置
  }

  public static String getAppNameByPID(Context context, int pid) {
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
      if (processInfo.pid == pid) {
        return processInfo.processName;
      }
    }
    return "";
  }
}
