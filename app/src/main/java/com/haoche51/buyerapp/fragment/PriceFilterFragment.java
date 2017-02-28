package com.haoche51.buyerapp.fragment;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.PriceAdapter;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.KeyValueEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.custom.HCListView;
import com.haoche51.custom.HCSeekBarPressure;
import com.haoche51.custom.HCViewClickListener;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import java.util.ArrayList;
import java.util.List;

public class PriceFilterFragment extends HCBaseFragment {

  @InjectView(R.id.lv_price_main) HCListView mPriceLv;
  @InjectView(R.id.tv_price_for_click) TextView mForClickTv;
  @InjectView(R.id.linear_price_parent) LinearLayout mPriceParent;
  private HCSeekBarPressure mSeekBar;
  private TextView mPriceTv;
  private TextView unlimitedTv;
  private ImageView unlimitedIv;
  private TextView mEnsureTv;

  private PriceAdapter mAdapter;
  private List<KeyValueEntity> mData = new ArrayList<>();
  private int priceLow;
  private int priceHigh;
  private double dProgressLow;
  private double dProgressHigh;

  //自定义seekBar的默认数值，以及第二个滑块默认位置
  private static final int INIT_VALUE = 35;

  private String host;

  public void setHost(String host) {
    this.host = host;
  }

  public PriceFilterFragment() {
  }

  public static PriceFilterFragment newInstance() {
    return new PriceFilterFragment();
  }

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override public void onEvent(HCCommunicateEntity entity) {
    String changeAction = host + HCEvent.ACTION_PRICE_CHOOSED_CHANGE;
    if (changeAction.equals(entity.getAction())) {
      abordEvent(entity);
      if (mAdapter != null) {
        mAdapter.notifyDataSetChanged();
        setUnLimitedStatus();
      }
    }
    if (entity.getAction().equals(host + HCEvent.ACTION_RESET_PRICE_BAR)) {
      resetSeekBar();
    }
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.filter_price;
  }

  @Override void doInitViewOrData() {
    int width = (int) (HCUtils.getScreenWidthInPixels() * 110F / 750F);
    mForClickTv.getLayoutParams().width = width;
    mForClickTv.setOnClickListener(mClickListener);
    mPriceParent.getLayoutParams().width = HCUtils.getScreenWidthInPixels() - width;
    mPriceParent.setOnClickListener(null);
    initPriceListView();
  }

  private void initPriceListView() {
    setHeaderView();
    List<KeyValueEntity> data = FilterUtils.getDefaultSortData(HCConsts.FILTER_PRICE);
    mData.clear();
    mData.addAll(data);
    int layoutId = R.layout.lvitem_price;
    mAdapter = new PriceAdapter(getActivity(), mData, layoutId, host, mClickListener);
    mPriceLv.setAdapter(mAdapter);
    setUnLimitedStatus();
  }

  private void setHeaderView() {
    View headView =
        LayoutInflater.from(getActivity()).inflate(R.layout.filter_price_headerview, null);
    mPriceTv = (TextView) headView.findViewById(R.id.tv_filter_price);
    unlimitedTv = (TextView) headView.findViewById(R.id.tv_price_unlimited);
    unlimitedIv = (ImageView) headView.findViewById(R.id.iv_price_unlimited);
    mEnsureTv = (TextView) headView.findViewById(R.id.tv_seek_bar_ensure);
    unlimitedTv.setOnClickListener(mClickListener);
    mEnsureTv.setOnClickListener(mClickListener);
    mSeekBar = (HCSeekBarPressure) headView.findViewById(R.id.seekbar_filter_price);
    initSeekBar();
    mPriceLv.addHeaderView(headView);
  }

  private HCViewClickListener mClickListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {
      mEnsureTv.setVisibility(View.GONE);
      int vid = v.getId();
      switch (vid) {
        case R.id.tv_price_unlimited:
          FilterUtils.saveTermPrice(host, 0, 0);
          HCEvent.postEvent(host + HCEvent.ACTION_PRICE_CHOOSED);
          setUnLimitedStatus();
          resetSeekBar();
          mAdapter.notifyDataSetChanged();
          break;
        case R.id.tv_price_item:
          KeyValueEntity entity = (KeyValueEntity) v.getTag();
          String key = entity.getKey();
          FilterUtils.priceKey2FilterTerm(host, key);
          HCEvent.postEvent(host + HCEvent.ACTION_PRICE_CHOOSED);
          resetSeekBar();
          mAdapter.notifyDataSetChanged();
          setUnLimitedStatus();
          break;
        case R.id.tv_price_for_click:
          HCEvent.postEvent(HCEvent.ACTION_HIDE_PRICE_FRAGMENT);
          break;
        case R.id.tv_seek_bar_ensure:
          if (priceHigh == INIT_VALUE) {
            priceHigh = 0;
          }
          FilterUtils.saveTermPrice(host, priceLow, priceHigh);
          mAdapter.notifyDataSetChanged();
          setUnLimitedStatus();
          HCEvent.postEvent(host + HCEvent.ACTION_PRICE_CHOOSED);
          break;
      }
    }
  };

  private void setUnLimitedStatus() {
    FilterTerm term = FilterUtils.getFilterTerm(host);
    String priceKey = FilterUtils.getFilterTermString(term, HCConsts.FILTER_PRICE);
    if (HCConsts.UNLIMITED.equals(priceKey)) {
      unlimitedIv.setVisibility(View.VISIBLE);
      unlimitedTv.setTextColor(HCUtils.getResColor(R.color.home_grx_red));
    } else {
      unlimitedIv.setVisibility(View.GONE);
      unlimitedTv.setTextColor(HCUtils.getResColor(R.color.home_hot_text));
    }
  }

  private void resetSeekBar() {
    mSeekBar.setProgressHigh(INIT_VALUE);
    mSeekBar.setProgressLow(0);
    mPriceTv.setTextColor(HCUtils.getResColor(R.color.home_hot_text));
    mPriceTv.setText(HCConsts.UNLIMITED);
  }

  private void initSeekBar() {
    mSeekBar.setDefaultScale(INIT_VALUE);
    mSeekBar.setProgressHigh(INIT_VALUE);
    mSeekBar.setOnSeekBarChangeListener(new HCSeekBarPressure.OnSeekBarChangeListener() {
      @Override public void onProgressBefore() {

      }

      @Override public void onProgressChanged(HCSeekBarPressure seekBar, double progressLow,
          double progressHigh) {
        priceLow = (int) progressLow;
        priceHigh = (int) progressHigh;
        dProgressLow = progressLow;
        dProgressHigh = progressHigh;
        String price = FilterUtils.getPrice(priceLow, priceHigh);
        if (priceHigh == INIT_VALUE) {
          price = FilterUtils.getPrice(priceLow, 0);
        }
        if (HCConsts.UNLIMITED.equals(price)) {
          mPriceTv.setTextColor(HCUtils.getResColor(R.color.home_hot_text));
          mPriceTv.setText(price);
        } else {
          mPriceTv.setTextColor(HCUtils.getResColor(R.color.home_grx_red));
          mPriceTv.setText(price);
        }
      }

      @Override public void onProgressAfter() {
        mSeekBar.setProgressHigh(dProgressHigh);
        mSeekBar.setProgressLow(dProgressLow);
        mAdapter.notifyDataSetChanged();
        mEnsureTv.setVisibility(View.VISIBLE);
      }
    });
  }

  public void hideDimView() {
    if (mForClickTv != null) {
      mForClickTv.setVisibility(View.GONE);
    }
  }

  public void showDimView() {
    if (mForClickTv == null) return;
    mForClickTv.postDelayed(new Runnable() {
      @Override public void run() {
        if (mForClickTv == null) return;
        mForClickTv.setVisibility(View.VISIBLE);
        int toColor = 0x50000000;
        int fromColor = Color.TRANSPARENT;
        ObjectAnimator.ofObject(mForClickTv, "backgroundColor", new ArgbEvaluator(), fromColor,
            toColor).setDuration(300).start();
      }
    }, 150);
  }
}
