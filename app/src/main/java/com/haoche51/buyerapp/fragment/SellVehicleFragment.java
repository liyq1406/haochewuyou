package com.haoche51.buyerapp.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.SaleServiceEntity;
import com.haoche51.buyerapp.entity.SaleSubmitResEntity;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCEditText;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

/***
 * 出售爱车fragment
 */
public class SellVehicleFragment extends HCBaseFragment {

  /**
   * 记录上一次界面可见时的cityid
   */
  private int mLastCityId = -1;

  @InjectView(R.id.hc_sale_scrollview) ScrollView mScrowView;

  /**
   * 顶部banner
   **/
  @InjectView(R.id.hc_sell_banner) ImageView mBannerImage;

  /**
   * 服务人群layout 展示
   **/
  @InjectView(R.id.hc_service_status_ll) LinearLayout mServiceStatusLL;
  /**
   * 服务人数
   **/
  @InjectView(R.id.service_seller_count) TextView mSSellerCount;
  /**
   * 卖车电话输入
   **/
  @InjectView(R.id.et_seller_phone) HCEditText mSellerPhoneEt;
  /**
   * 卖车电话提交按钮
   **/
  @InjectView(R.id.btn_seller_submint) Button mSubmitBtn;
  /**
   * 400提醒布局1
   **/
  @InjectView(R.id.hc_contact_phone_layout) LinearLayout mContactPhoneLayout;
  /**
   * 400提醒1
   **/
  @InjectView(R.id.hc_contact_phone) TextView mContactPhone;
  /**
   * 400提醒布局2
   **/
  @InjectView(R.id.hc_contact_phone2_layout) LinearLayout mContactPhone2Layout;
  /**
   * 400提醒2
   **/
  @InjectView(R.id.hc_contact_phone2) TextView mContactPhone2;
  /**
   * 点击后输入电话获取焦点
   **/
  @InjectView(R.id.hc_contact_tip) TextView mContactTip;
  @InjectView(R.id.invalid_phone_tip) TextView invalidPhoneTip;
  private float mBannerRate = 260.0F / 640.0F;

  @InjectView(R.id.sell_vehicle_bottom01) LinearLayout mBottom1Layout;
  @InjectView(R.id.iv_sell_vehicle_bottom01_one) ImageView mOneIv;
  @InjectView(R.id.iv_sell_vehicle_bottom01_two) ImageView mTwoIv;
  @InjectView(R.id.iv_sell_vehicle_bottom01_three) ImageView mThreeIv;
  @InjectView(R.id.iv_sell_vehicle_bottom01_four) ImageView mFourIv;

  @InjectView(R.id.tv_sell_vehicle_bottom01_one) TextView mOneTv;
  @InjectView(R.id.tv_sell_vehicle_bottom01_two) TextView mTwoTv;
  @InjectView(R.id.tv_sell_vehicle_bottom01_three) TextView mThreeTv;
  @InjectView(R.id.tv_sell_vehicle_bottom01_four) TextView mFourTv;

  @InjectView(R.id.view_loading) View mLoadingView;

  private View.OnClickListener saleServiceClick = new View.OnClickListener() {

    @Override public void onClick(View v) {
      switch (v.getId()) {
        case R.id.btn_seller_submint: // 提交手机号
          if (mSellerPhoneEt != null) {
            String phone = mSellerPhoneEt.getText().toString().trim();
            if (phoneValidation(phone)) {
              requestSubmitSellerPhone(phone);
            }
          }
          break;
        case R.id.hc_contact_tip://提示输入手机号，滚动到顶部
          if (mScrowView != null) {
            mScrowView.fullScroll(ScrollView.FOCUS_UP);
            mSellerPhoneEt.requestFocus();
          }
          break;
      }
    }
  };

  @Override boolean isNeedBindEventBus() {
    return false;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_sell_vehicle;
  }

  @Override void doInitViewOrData() {
    if (mSubmitBtn != null) {

      mSubmitBtn.setOnClickListener(saleServiceClick);
      mContactTip.setOnClickListener(saleServiceClick);

      requestSellerVehicle();

      mSellerPhoneEt.setOnTouchListener(new View.OnTouchListener() {
        @Override public boolean onTouch(View v, MotionEvent event) {

          int scrollY = mScrowView.getScrollY();
          int focusSlop = (int) (HCUtils.getScreenWidthInPixels() * mBannerRate);
          if (scrollY < focusSlop) {
            mScrowView.smoothScrollTo(0, focusSlop);
          }
          return false;
        }
      });
    }
  }

  @Override public void onResume() {
    super.onResume();
    ThirdPartInjector.onPageStart(this.getClass().getSimpleName());
    //在这里加载进去图片
    if (mOneIv != null && mOneIv.getDrawable() == null) {

      final Activity mAct = getActivity();
      new Thread() {
        @Override public void run() {
          if (mAct != null && !mAct.isFinishing()) {
            mAct.runOnUiThread(new Runnable() {
              @Override public void run() {
                loadFastSellImage(mOneIv, mOneTv, R.drawable.sell_vehicle_bottom01_image_one);
                loadFastSellImage(mTwoIv, mTwoTv, R.drawable.sell_vehicle_bottom01_image_two);
                loadFastSellImage(mThreeIv, mThreeTv, R.drawable.sell_vehicle_bottom01_image_three);
                loadFastSellImage(mFourIv, mFourTv, R.drawable.sell_vehicle_bottom01_image_four);
                mBottom1Layout.setVisibility(View.VISIBLE);
              }
            });
          }
        }
      }.start();
    }
  }

  /**
   * 加载快速卖好车四张图片
   */
  private void loadFastSellImage(ImageView iv, TextView tv, int resId) {
    if (iv == null || resId == 0) return;
    int w = HCUtils.getScreenWidthInPixels();
    int desth = (int) (300F * w / 688);

    iv.getLayoutParams().width = w;
    iv.getLayoutParams().height = desth;
    iv.setImageResource(resId);

    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.sell_vehicle_triangle);
    animation.setFillAfter(true);
    tv.startAnimation(animation);
    tv.setVisibility(View.VISIBLE);
  }

  /**
   * 获取售车页信息
   */
  private void requestSellerVehicle() {
    if (!HCUtils.isNetAvailable()) {
      //GetDataFrom Cache
      setUpPageData(HCSpUtils.getSaleService());
    } else {
      Map<String, Object> params = HCParamsUtil.sellVehicle();
      API.post(new HCRequest(params, new HCSimpleCallBack() {
        @Override public void onHttpFinish(String responseJsonString) {
          handleSellerVehicle(responseJsonString);
        }
      }));
    }
  }

  private void handleSellerVehicle(String responseJsonString) {
    setUpPageData(responseJsonString);
    HCSpUtils.setSaleService(responseJsonString);
  }

  /**
   * 设置页面展示信息
   */
  private void setUpPageData(String response) {

    if (mScrowView == null) return;
    long jsonTime = HCUtils.now();

    SaleServiceEntity entity = HCJSONParser.parseSaleService(response);
    HCLog.d(TAG, "json time == " + HCUtils.nowDiff(jsonTime));
    if (entity != null) {
      //设置服务人数及销售电话号码
      mServiceStatusLL.setVisibility(View.VISIBLE);
      mSSellerCount.setText(String.valueOf(entity.getSeller_num()));

      String sellPhone = entity.getSell_phone();
      sellPhone = HCUtils.toHCString(sellPhone);
      mContactPhone.setText(sellPhone);
      mContactPhone2.setText(sellPhone);

      mContactPhone.setTag(sellPhone);
      mContactPhone2.setTag(sellPhone);

      setUpBanner(entity.getCover_img());
    } else {
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(HCUtils.getScreenWidthInPixels(),
          (int) (HCUtils.getScreenWidthInPixels() * mBannerRate));
      lp.setMargins(0, 0, 0, 15);
      mBannerImage.setLayoutParams(lp);
      mServiceStatusLL.setVisibility(View.GONE);
      mContactPhoneLayout.setVisibility(View.GONE);
      mContactPhone2Layout.setVisibility(View.GONE);
      HCUtils.hideViewIfNeed(mLoadingView);
    }
  }

  @SuppressWarnings("unused") @OnClick({ R.id.hc_contact_phone, R.id.hc_contact_phone2 })
  public void onDialPhone(View view) {
    if (view.getTag() != null && view.getTag() instanceof String) {
      String phone = (String) view.getTag();
      HCUtils.diaPhone(phone);
    }
  }

  /**
   * 设置banner 图片
   */
  private void setUpBanner(String url) {

    if (TextUtils.isEmpty(url) || mBannerImage == null) return;

    int width = HCUtils.getScreenWidthInPixels();
    int height = (int) (width * mBannerRate);

    mBannerImage.getLayoutParams().width = width;
    mBannerImage.getLayoutParams().height = height;

    url = HCUtils.convertImageURL(url, width, height);

    HCLog.d(TAG, "banner url = " + url);

    DisplayImageOptions options =
        ImageLoaderHelper.getSellVehicleOptions(R.drawable.banner_sale_service);
    ImageLoader.getInstance().loadImage(url, options, new SimpleImageLoadingListener() {
      @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        super.onLoadingComplete(imageUri, view, loadedImage);
        if (mBannerImage != null && loadedImage != null) {
          mBannerImage.setImageBitmap(loadedImage);
        }
      }
    });
    HCUtils.hideViewIfNeed(mLoadingView);
  }

  /**
   * 提交线索
   */
  private void requestSubmitSellerPhone(String phone) {
    if (!HCUtils.isNetAvailable()) {
      HCUtils.toastNetError();
      return;
    }
    Map<String, Object> params = HCParamsUtil.applySell(phone);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleSubmitSellerPhone(responseJsonString);
      }
    }));

    DialogUtils.showProgress(getActivity());
  }

  /**
   * 电话号码校验
   */
  private boolean phoneValidation(String phone) {
    if (!HCUtils.isPhoneNumberValid(phone)) {
      invalidPhoneTip.setVisibility(View.VISIBLE);
      return false;
    }
    invalidPhoneTip.setVisibility(View.GONE);
    return true;
  }

  /*提交线索响应*/
  private void handleSubmitSellerPhone(String response) {
    DialogUtils.dismissProgress();
    SaleSubmitResEntity entity = HCJSONParser.parseSaleSubimtRes(response);

    if (entity != null) {
      DialogUtils.showSubmitSellVehicle(getActivity(), entity.getTitle(), entity.getDescription());
    }
  }

  @Override public void onEvent(HCCommunicateEntity entity) {

  }

  @SuppressWarnings("unused")
  @OnClick({ R.id.iv_spread0, R.id.hc_service_status_ll, R.id.hc_sell_banner })
  public void onDescPicClick(View v) {
    HCUtils.hideKeyboard(mSellerPhoneEt);
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);

    if (!hidden && mLastCityId > 0) {
      if (mLastCityId != HCDbUtil.getSavedCityId()) {
        requestSellerVehicle();
        HCLog.d(TAG, "cityChanged   re request ");
      }
    }

    if (hidden) {
      mLastCityId = HCDbUtil.getSavedCityId();
      HCLog.d(TAG, "now hidden  record cityid ");
    }
  }

  @Override public void onPause() {
    HCUtils.hideKeyboard(mSellerPhoneEt);
    super.onPause();
    ThirdPartInjector.onPageEnd(this.getClass().getSimpleName());
  }
}
