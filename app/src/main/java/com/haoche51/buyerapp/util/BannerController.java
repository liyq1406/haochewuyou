package com.haoche51.buyerapp.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.LoginActivity;
import com.haoche51.buyerapp.activity.WebBrowserActivity;
import com.haoche51.buyerapp.entity.HCBannerEntity;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;
import com.haoche51.custom.ImageCycleView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理Banner
 */
public class BannerController {

  private final static String TAG = "hcBannerController";
  private float mBannerRate = 750F / 180.0F;

  public void fillBannerData(final List<HCBannerEntity> mBannerEntities,
      final ImageCycleView mImageCyView, final Activity mActivity, float defaultRate) {

    final ArrayList<HCBannerEntity> mData = removeInvalidData(mBannerEntities);

    if (HCUtils.isListEmpty(mData)) return;

    if (mActivity == null || mActivity.isFinishing()) return;

    if (mImageCyView == null) return;

    mImageCyView.setVisibility(View.VISIBLE);

    if (defaultRate > 0) {
      mBannerRate = defaultRate;
    }

    HCBannerEntity entity = mBannerEntities.get(0);
    float rate = HCUtils.str2Float(entity.getPic_rate());
    if (rate > 0F) {
      mBannerRate = rate;
    }

    mImageCyView.setImageResources(mData, new ImageCycleView.ImageCycleViewListener() {
      @Override public void onImageClick(int position, View imageView) {
        HCBannerEntity entity = mData.get(position);
        String url = entity.getLink_url();
        if (!TextUtils.isEmpty(url)) {
          url = url.trim();
        } else {
          return;
        }

        String title = entity.getTitle();

        HCLog.d(TAG, "picRate " + entity.getPic_rate());

        String picRate = entity.getPic_rate();
        float rate = HCUtils.str2Float(picRate);
        if (rate > 0F) {
          mBannerRate = rate;
        }

        boolean shouldCheckLogin = entity.getLogin_check() == 1;
        boolean needLogin = shouldCheckLogin && TextUtils.isEmpty(HCSpUtils.getUserPhone());
        url = shouldCheckLogin ? putParams(url) : url;
        Class<?> cls = needLogin ? LoginActivity.class : WebBrowserActivity.class;
        Intent intent = new Intent();
        intent.setClass(mActivity, cls);
        Bundle bundle = new Bundle();
        bundle.putSerializable(HCConsts.INTENT_KEY_BANNER, entity);
        intent.putExtras(bundle);
        intent.putExtra(HCConsts.INTENT_KEY_TITLE, title);
        intent.putExtra(HCConsts.INTENT_KEY_URL, url);
        mActivity.startActivity(intent);

        //统计banner
        HCStatistic.bannerClick(position);
        HCSensorsUtil.homePageClick(entity.getTitle());
      }

      @Override public void displayImage(String imageURL, final ImageView imageView) {
        final int width = HCUtils.getScreenWidthInPixels();
        final int height = (int) HCArithUtil.div(width, mBannerRate, 3);

        ImageSize targetImageSize = new ImageSize(width, height);
        imageURL = HCUtils.convertImageURL(imageURL, width, height);

        DisplayImageOptions options =
            ImageLoaderHelper.getBannerOptions(R.drawable.default_template);

        SimpleImageLoadingListener imageListener = new SimpleImageLoadingListener() {
          @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            super.onLoadingComplete(imageUri, view, loadedImage);

            if (imageView != null && mImageCyView != null) {
              FrameLayout.LayoutParams rl = new FrameLayout.LayoutParams(width, height);
              mImageCyView.setLayoutParams(rl);
              mImageCyView.setBackgroundResource(R.color.hc_transcolor);

              imageView.setImageBitmap(loadedImage);
            }
          }
        };

        ImageLoader.getInstance().loadImage(imageURL, targetImageSize, options, imageListener);
      }
    });
  }

  private ArrayList<HCBannerEntity> removeInvalidData(List<HCBannerEntity> input) {
    ArrayList<HCBannerEntity> mRealEntities = new ArrayList<>();

    if (HCUtils.isListEmpty(input)) return mRealEntities;

    for (HCBannerEntity banner : input) {
      String pic_url = banner.getPic_url();
      if (!TextUtils.isEmpty(pic_url)) {
        mRealEntities.add(banner);
      }
    }

    return mRealEntities;
  }

  private String putParams(String url) {
    String phone = HCSpUtils.getUserPhone();
    StringBuilder sb = new StringBuilder(url);
    String prefix = !url.contains("?") ? "?" : "&";
    sb.append(prefix);
    sb.append("udid");
    sb.append("=");
    sb.append(HCUtils.getUserDeviceId());
    if (!TextUtils.isEmpty(phone)) {
      sb.append("&");
      sb.append("phone");
      sb.append("=");
      sb.append(phone);
    }
    return sb.toString();
  }
}
