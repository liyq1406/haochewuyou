package com.haoche51.buyerapp.activity;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.CouponAdapter;
import com.haoche51.buyerapp.entity.HCBSubcribeEntity;
import com.haoche51.buyerapp.entity.HCCouponEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCEditText;
import com.haoche51.custom.HCPullToRefresh;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 我的优惠券 列表
 */
public class CouponListActivity extends HCCommonTitleActivity {

  private View mPreloadingView;

  private HCPullToRefresh mPullToRefresh;
  private List<HCCouponEntity> mCouponDatas;
  private CouponAdapter mCouponAdapter;
  private int mCurrentPage = 0;
  private HCEditText mInputEt;
  private TextView mErrorTv;
  private ListView mListView;

  private View.OnClickListener mClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      switch (v.getId()) {
        case R.id.btn_exchange:
          String input = mInputEt.getText().toString();
          if (TextUtils.isEmpty(input)) {
            mErrorTv.setVisibility(View.VISIBLE);
            mInputEt.requestFocus();
            return;
          } else {
            mErrorTv.setVisibility(View.GONE);
            requestExchange(input);
          }
          break;
      }
    }
  };

  @Override void initViews() {

    mPreloadingView = findViewById(R.id.view_loading);

    findViewById(R.id.btn_exchange).setOnClickListener(mClickListener);
    mInputEt = (HCEditText) findViewById(R.id.et_exchange_code);
    mErrorTv = (TextView) findViewById(R.id.tv_exchange_error);

    mPullToRefresh = (HCPullToRefresh) findViewById(R.id.pdv_coupon);
    mPullToRefresh.setCanPull(false);
    mListView = mPullToRefresh.getListView();
    ListView mCouponLv = mPullToRefresh.getListView();
    mCouponLv.setDividerHeight(0);
    mCouponDatas = new ArrayList<>();
    mCouponAdapter = new CouponAdapter(this, mCouponDatas, R.layout.lvitem_coupon);
    mCouponLv.setAdapter(mCouponAdapter);
    View emptyView = findViewById(R.id.tv_coupon_empty);
    mPullToRefresh.setEmptyView(emptyView);
    mPullToRefresh.setOnRefreshCallback(new HCPullToRefresh.OnRefreshCallback() {
      @Override public void onPullDownRefresh() {
        mCurrentPage = 0;
        requestCouponDatas();
      }

      @Override public void onLoadMoreRefresh() {
        mCurrentPage++;
        requestCouponDatas();
      }
    });

    requestCouponDatas();
  }

  //兑换优惠券
  private void requestExchange(String code) {
    final int[] flag = { 0 };
    Map<String, Object> params = HCParamsUtil.exchangeCoupon(code);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleExchange(responseJsonString, flag);
      }
    }));
    DialogUtils.showProgress(this);
  }

  private void handleExchange(String responseJsonString, int[] flag) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      HCBSubcribeEntity entity = HCJSONParser.parseExchangeCoupon(responseJsonString);
      if (0 == entity.getErrno()) {
        requestNewest();
        flag[0] = 1;
      } else {
        HCUtils.showToast(entity.getErrmsg());
      }
      if (flag[0] == 0) {
        DialogUtils.dismissProgress();
      }
    }
  }

  private void requestNewest() {
    Map<String, Object> params = HCParamsUtil.getUserNewestCoupon();
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleNewest(responseJsonString);
      }
    }));
  }

  private void handleNewest(String responseJsonString) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      List<HCCouponEntity> entities = HCJSONParser.parseCouponList(responseJsonString);
      if (!entities.isEmpty()) {
        final HCCouponEntity entity = entities.get(0);
        if (!mCouponDatas.contains(entity)) {
          mListView.smoothScrollToPosition(0);
          mPullToRefresh.postDelayed(new Runnable() {
            @Override public void run() {
              animateLayout(entity);
            }
          }, 50);
        }
      }
      DialogUtils.dismissProgress();
    }
  }

  private void animateLayout(HCCouponEntity entity) {
    if (mCouponDatas.size() == 0) {
      mCouponDatas.add(entity);
      mCouponAdapter.notifyDataSetChanged();
      return;
    }

    if (mListView.getChildAt(0) != null) {
      int height = mListView.getChildAt(0).getHeight();
      final ImageView imageView = (ImageView) findViewById(R.id.iv_for_animate);
      Bitmap bitmap = getListViewBitmap();
      imageView.setImageBitmap(bitmap);
      imageView.setVisibility(View.VISIBLE);
      TranslateAnimation transanim = new TranslateAnimation(0, 0, 0, height);
      transanim.setDuration(600);
      transanim.setAnimationListener(new Animation.AnimationListener() {
        @Override public void onAnimationEnd(Animation animation) {
          imageView.setVisibility(View.GONE);
        }

        @Override public void onAnimationRepeat(Animation animation) {
        }

        @Override public void onAnimationStart(Animation animation) {
        }
      });
      mCouponDatas.add(0, entity);
      mCouponAdapter.notifyDataSetChanged();
      imageView.startAnimation(transanim);
    }
  }

  private Bitmap getListViewBitmap() {
    mListView.setDrawingCacheEnabled(true);
    Bitmap bitmap = mListView.getDrawingCache();
    Bitmap bitmap1 = Bitmap.createBitmap(bitmap);
    mListView.setDrawingCacheEnabled(false);
    return bitmap1;
  }

  private void requestCouponDatas() {
    String phone = HCSpUtils.getUserPhone();

    Map<String, Object> params = HCParamsUtil.getUserCoupons(phone, mCurrentPage);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleCouponDatas(responseJsonString);
      }
    }));
  }

  private void handleCouponDatas(String responseJsonString) {

    if (mPullToRefresh == null) return;

    if (!TextUtils.isEmpty(responseJsonString)) {
      List<HCCouponEntity> entities = HCJSONParser.parseCouponList(responseJsonString);
      if (mCurrentPage == 0) {
        mCouponDatas.clear();
      }
      mCouponDatas.addAll(entities);
      boolean isNoMoreData = entities.size() < HCConsts.PAGESIZE;
      mPullToRefresh.setFooterStatus(isNoMoreData, R.string.hc_no_more_coupon);
    }
    mPullToRefresh.finishRefresh();
    mCouponAdapter.notifyDataSetChanged();
    HCUtils.hideViewIfNeed(mPreloadingView);
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    titleTv.setText(R.string.hc_my_coupon);
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_coupon_list;
  }

  @Override protected void onResume() {
    super.onResume();

    doThirdPartyResume();
  }

  @Override protected void onPause() {
    super.onPause();

    doThirdPartyPause();
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
