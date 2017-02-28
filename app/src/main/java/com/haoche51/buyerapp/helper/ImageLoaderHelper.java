package com.haoche51.buyerapp.helper;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.WebBrowserActivity;
import com.haoche51.buyerapp.entity.HCSplashEntity;
import com.haoche51.buyerapp.entity.HCVehicleItemEntity;
import com.haoche51.buyerapp.entity.SplashDataEntity;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.util.HCArithUtil;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageLoaderHelper {

  /**
   * 有memoryCache的option
   */
  private static DisplayImageOptions normalOpts =
      new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.vehicle_default)
          .showImageForEmptyUri(R.drawable.vehicle_default)
          .showImageOnFail(R.drawable.vehicle_default)
          .cacheInMemory(true)
          .cacheOnDisk(true)
          .considerExifParams(true)
          .build();

  private static DisplayImageOptions noMemoryOpts =
      new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.vehicle_default)
          .showImageForEmptyUri(R.drawable.vehicle_default)
          .showImageOnFail(R.drawable.vehicle_default)
          .cacheInMemory(false)
          .cacheOnDisk(true)
          .considerExifParams(true)
          .build();

  public static void displayNormalImage(String url, ImageView imageView) {
    url = processPre(url, imageView);
    ImageLoader.getInstance().displayImage(url, imageView, normalOpts);
  }

  public static void displayNoMemoryImage(String url, ImageView imageView) {
    if (imageView != null && !TextUtils.isEmpty(url)) {
      url = processPre(url, imageView);
      ImageLoader.getInstance().displayImage(url, imageView, noMemoryOpts);
    }
  }

  private static String processPre(String url, ImageView imageView) {
    if (imageView != null && !TextUtils.isEmpty(url)) {
      int preW = imageView.getLayoutParams().width;
      int preH = imageView.getLayoutParams().height;

      String splitor = "?imageView2/";
      if (url.contains(splitor)) {
        splitor = "\\" + splitor;
        url = url.split(splitor)[0];
      }
      if (preW >= 0 && preH >= 0) {
        url = HCUtils.convertImageURL(url, preW, preH);
      }
    }
    return url;
  }

  private static DisplayImageOptions justDiskCache =
      new DisplayImageOptions.Builder().cacheInMemory(false)
          .cacheOnDisk(true)
          .imageScaleType(ImageScaleType.EXACTLY)
          .bitmapConfig(Bitmap.Config.RGB_565)
          .build();

  public static void loadAllGoodAdverPic(final ImageView mAdverIv,
      final HCVehicleItemEntity entity) {
    String rate = entity.getPic_rate();
    float picRate = HCUtils.str2Float(rate);
    String url = entity.getImage_url();
    if (picRate != 0 && mAdverIv != null && !TextUtils.isEmpty(url)) {
      int destW = HCUtils.getScreenWidthInPixels();
      int destH = (int) HCArithUtil.div(destW, picRate, 2);
      mAdverIv.getLayoutParams().width = destW;
      mAdverIv.getLayoutParams().height = destH;
      url = HCUtils.convertImageURL(url, destW, destH);

      ImageLoader.getInstance().loadImage(url, normalOpts, new SimpleImageLoadingListener() {
        @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
          super.onLoadingComplete(imageUri, view, loadedImage);
          if (loadedImage != null) {
            mAdverIv.setImageBitmap(loadedImage);
            mAdverIv.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                String url = entity.getRedirect_url();
                WebBrowserActivity.urlToThis(GlobalData.mContext, url);
                HCStatistic.operateForListClick();
              }
            });
            mAdverIv.setVisibility(View.VISIBLE);
          }
        }
      });
    }
  }

  /**
   * Banner的option,不进行内存缓存
   */
  public static DisplayImageOptions getBannerOptions(int defaultImg) {
    return new DisplayImageOptions.Builder().showImageForEmptyUri(defaultImg)
        .showImageOnFail(R.drawable.default_template)
        .resetViewBeforeLoading(false)
        .cacheOnDisk(true)
        .cacheInMemory(true)
        .imageScaleType(ImageScaleType.EXACTLY)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .build();
  }

  public static DisplayImageOptions getSellVehicleOptions(int defaultImg) {
    return new DisplayImageOptions.Builder().showImageForEmptyUri(defaultImg)
        .showImageOnFail(defaultImg)
        .resetViewBeforeLoading(false)
        .cacheOnDisk(true)
        .cacheInMemory(false)
        .imageScaleType(ImageScaleType.EXACTLY)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .build();
  }

  public static void simpleDisplay(String url, ImageView iv) {

    if (iv == null || TextUtils.isEmpty(url)) return;

    ImageLoader.getInstance().displayImage(url, iv, justDiskCache);
  }

  public static void simpleDisplay(String url, ImageView iv, SimpleImageLoadingListener listener) {
    if (iv == null || TextUtils.isEmpty(url)) return;

    ImageLoader.getInstance().displayImage(url, iv, justDiskCache, listener);
  }

  //---------------------首页闪屏------start----------//
  public static void handleSplashData(String resp, long maxLoadTime) {
    HCSplashEntity entity = HCJSONParser.parseSplashData(resp);
    SplashDataEntity bodyData = entity.getBody();
    SplashDataEntity footData = entity.getFoot();
    if (bodyData != null) {
      handleBodyData(bodyData, maxLoadTime);
    }

    if (footData != null) {
      handleFootData(footData, maxLoadTime);
    }
  }

  /***
   * 记录没有下载完的任务,启动应用后在service中加载
   * <p/>
   * sp中存有一个un_loaded的entity  里面记录信息
   * 如果service中的东西下载完成了  把这个un_loaded的实体更新到loaded中
   */

  public static void handleBodyData(final SplashDataEntity body, final long maxLoadTime) {

    int spBodyId = HCSpUtils.getSplashBodyEntity().getId();
    if (spBodyId != body.getId()) {
      String url = body.getImage_url();
      if (!TextUtils.isEmpty(url)) {
        url = url.trim();
        final ImageView mViewForCancel = new ImageView(GlobalData.mContext);
        if (maxLoadTime > 0) {
          mViewForCancel.postDelayed(new Runnable() {
            @Override public void run() {
              ImageLoader.getInstance().cancelDisplayTask(mViewForCancel);
              HCSpUtils.setUnLoadedSplashBodyEntity(body);
            }
          }, maxLoadTime);
        }
        int w = HCUtils.getScreenWidthInPixels();
        int h = HCUtils.getScreenHeightPixels();
        url = HCUtils.averageImageURL(url, w, h);
        DisplayImageOptions optioins = ImageLoaderHelper.justDiskCache;

        SimpleImageLoadingListener mListener = new SimpleImageLoadingListener() {
          @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            super.onLoadingComplete(imageUri, view, loadedImage);
            if (loadedImage != null) {
              HCSpUtils.setSplashBodyEntity(body);

              body.setId(0);
              HCSpUtils.setUnLoadedSplashBodyEntity(body);

              HCEvent.postEvent(HCEvent.ACTION_SPLASH_BODY_LOADED);
            }
          }
        };

        ImageLoader.getInstance().displayImage(url, mViewForCancel, optioins, mListener);
      }
    }
  }

  public static void handleFootData(final SplashDataEntity foot, long maxLoadTime) {
    final int spFootId = HCSpUtils.getSplashFootEntity().getId();
    if (spFootId != foot.getId()) {
      String url = foot.getImage_url();
      if (!TextUtils.isEmpty(url)) {
        url = url.trim();

        final ImageView mViewForCancel = new ImageView(GlobalData.mContext);

        if (maxLoadTime > 0) {
          mViewForCancel.postDelayed(new Runnable() {
            @Override public void run() {
              ImageLoader.getInstance().cancelDisplayTask(mViewForCancel);
              HCSpUtils.setUnLoadedSplashFootEntity(foot);
            }
          }, maxLoadTime);
        }

        int sw = HCUtils.getScreenWidthInPixels();
        //设置图片宽高
        int sloganW = (int) (sw * 768F / 1080);
        int sloganH = (int) (sloganW * 105F / 768);
        DisplayImageOptions optioins = ImageLoaderHelper.justDiskCache;
        url = HCUtils.averageImageURL(url, sloganW, sloganH);

        SimpleImageLoadingListener mListener = new SimpleImageLoadingListener() {
          @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            super.onLoadingComplete(imageUri, view, loadedImage);
            if (loadedImage != null) {
              //显示图片
              HCSpUtils.setSplashFootEntity(foot);
              HCEvent.postEvent(HCEvent.ACTION_SPLASH_FOOT_LOADED);

              foot.setId(0);
              HCSpUtils.setUnLoadedSplashFootEntity(foot);
            }
          }
        };

        ImageLoader.getInstance().displayImage(url, mViewForCancel, optioins, mListener);
      }
    }
  }
  //---------------------首页闪屏--------end--------//
}
