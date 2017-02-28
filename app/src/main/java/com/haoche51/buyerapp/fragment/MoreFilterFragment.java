package com.haoche51.buyerapp.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.FilterGridViewAdapter;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.HCMoreFilterItemEntity;
import com.haoche51.buyerapp.entity.KeyValueEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCFormatUtil;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import com.haoche51.custom.HCGridView;
import com.haoche51.custom.HCScrollView;
import com.haoche51.custom.HCViewClickListener;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import java.util.List;
import java.util.Map;

public class MoreFilterFragment extends HCBaseFragment {

  @InjectView(R.id.tv_more_for_click) TextView mForClickTv;
  @InjectView(R.id.rel_more_parent) RelativeLayout mMoreParent;
  @InjectView(R.id.tv_filter_more_result_hint) TextView mTvResultHint;
  @InjectView(R.id.tv_filter_more_result) TextView mTvResult;
  @InjectView(R.id.tv_filter_more_reset) TextView mTvReset;
  @InjectView(R.id.sv_filter_more) HCScrollView mScrollView;

  @InjectView(R.id.rel_filter_speed_box) RelativeLayout mSpeedBoxLayout;
  @InjectView(R.id.rel_filter_standard) RelativeLayout mStandardLayout;
  @InjectView(R.id.rel_filter_emissions) RelativeLayout mEmissionsLayout;
  @InjectView(R.id.rel_filter_country) RelativeLayout mCountryLayout;
  @InjectView(R.id.rel_filter_color) RelativeLayout mColorLayout;

  @InjectView(R.id.tv_filter_speed_box) TextView mSpeedBoxTv;
  @InjectView(R.id.tv_filter_standard) TextView mStandardTv;
  @InjectView(R.id.tv_filter_emissions) TextView mEmissionsTv;
  @InjectView(R.id.tv_filter_country) TextView mCountryTv;
  @InjectView(R.id.tv_filter_color) TextView mColorTv;

  @InjectView(R.id.gv_filter_car_type) HCGridView mCarTypeGridView;
  @InjectView(R.id.gv_filter_car_age) HCGridView mCarAgeGridView;
  @InjectView(R.id.gv_filter_distance) HCGridView mDistanceGridView;
  @InjectView(R.id.gv_filter_speed_box) HCGridView mSpeedBoxGridView;
  @InjectView(R.id.gv_filter_standard) HCGridView mStandardGridView;
  @InjectView(R.id.gv_filter_emissions) HCGridView mEmissionsGridView;
  @InjectView(R.id.gv_filter_country) HCGridView mCountryGridView;
  @InjectView(R.id.gv_filter_color) HCGridView mColorGridView;

  private FilterGridViewAdapter mCarTypeAdapter;
  private FilterGridViewAdapter mCarAgeAdapter;
  private FilterGridViewAdapter mDistanceAdapter;
  private FilterGridViewAdapter mSpeedBoxAdapter;
  private FilterGridViewAdapter mStandardAdapter;
  private FilterGridViewAdapter mEmissionsAdapter;
  private FilterGridViewAdapter mCountryAdapter;
  private FilterGridViewAdapter mColorAdapter;

  private int normalColor = HCUtils.getResColor(R.color.home_hot);
  private int redColor = HCUtils.getResColor(R.color.home_grx_red);
  private boolean isInitOtherGv = false;

  /** 筛选所需要的数据 */
  private FilterTerm term;
  //纪录每次打开界面时的当前筛选条件,以便于不正常推出时的重置筛选条件到以前.
  private FilterTerm termTemp;
  private String allHost = AllGoodVehiclesFragment.class.getSimpleName();
  private String todayHost = TodayNewArrivalFragment.class.getSimpleName();

  private String host;

  public void setHost(String host) {
    this.host = host;
  }

  public MoreFilterFragment() {
  }

  public static MoreFilterFragment newInstance() {
    return new MoreFilterFragment();
  }

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.filter_more;
  }

  @Override public void onEvent(HCCommunicateEntity entity) {
    String changeAction = allHost + HCEvent.ACTION_MORE_CHOOSED_CHANGE;
    if (changeAction.equals(entity.getAction())) {
      abordEvent(entity);
      notifyChooseChange();
      notifyChooseChangeForFold();
    }
    if (HCEvent.ACTION_SHOW_MORE.equals(entity.getAction())) {
      String hostTemp = entity.getStrValue();
      if (!TextUtils.isEmpty(hostTemp) && hostTemp.equals(host)) {
        abordEvent(entity);
        termTemp = FilterUtils.getFilterTerm(host);
        requestSearchData(host);
        notifyChooseChange();
        notifyChooseChangeForFold();
      }
    }
  }

  private void notifyChooseChange() {
    if (mCarTypeAdapter == null) return;
    mCarTypeAdapter.notifyDataSetChanged();
    mCarAgeAdapter.notifyDataSetChanged();
    mDistanceAdapter.notifyDataSetChanged();
    mSpeedBoxAdapter.notifyDataSetChanged();
    mStandardAdapter.notifyDataSetChanged();
    mEmissionsAdapter.notifyDataSetChanged();
    mCountryAdapter.notifyDataSetChanged();
    mColorAdapter.notifyDataSetChanged();
  }

  private void notifyChooseChangeForFold() {
    if (mSpeedBoxTv == null) return;
    FilterTerm term = FilterUtils.getFilterTerm(host);
    String speedBox = FilterUtils.getGearBox(term);
    String standard = FilterUtils.getStandard(term);
    String emissions = FilterUtils.getEmission(term);
    String country = FilterUtils.getCountry(term);
    String color = FilterUtils.getColor(term);
    setTvStatus(mSpeedBoxTv, speedBox);
    setTvStatus(mStandardTv, standard);
    setTvStatus(mEmissionsTv, emissions);
    setTvStatus(mCountryTv, country);
    setTvStatus(mColorTv, color);
  }

  private void setTvStatus(TextView tv, String text) {
    if (HCConsts.UNLIMITED.equals(text)) {
      tv.setTextColor(normalColor);
    } else {
      tv.setTextColor(redColor);
    }
    tv.setText(text);
  }

  @Override void doInitViewOrData() {

    initLayout();

    initGridView();

    initRequestDefaultData();

    initClickListener();

    termTemp = FilterUtils.getFilterTerm(host);
  }

  private void initLayout() {
    int width = (int) (HCUtils.getScreenWidthInPixels() * 110F / 750F);
    mForClickTv.getLayoutParams().width = width;
    mForClickTv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //将筛选条件重置为刚打开时的条件
        FilterUtils.setFilterTerm(host, termTemp);
        HCEvent.postEvent(HCEvent.ACTION_HIDE_MORE_FRAGMENT);
      }
    });
    mMoreParent.getLayoutParams().width = HCUtils.getScreenWidthInPixels() - width;
    mMoreParent.setOnClickListener(null);
  }

  private void initGridView() {
    initGridByType(HCConsts.FILTER_CAR_TYPE, mCarTypeGridView);
    initGridByType(HCConsts.FILTER_CAR_AGE, mCarAgeGridView);
    initGridByType(HCConsts.FILTER_DISTANCE, mDistanceGridView);
  }

  private void initClickListener() {
    mTvResult.setOnClickListener(new HCViewClickListener() {
      @Override public void performViewClick(View v) {
        HCEvent.postEvent(host + HCEvent.ACTION_MORE_CHOOSED);
      }
    });
    mTvReset.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        resetQuaryMoreData();
      }
    });
    mSpeedBoxLayout.setOnClickListener(mLayoutListener);
    mStandardLayout.setOnClickListener(mLayoutListener);
    mEmissionsLayout.setOnClickListener(mLayoutListener);
    mCountryLayout.setOnClickListener(mLayoutListener);
    mColorLayout.setOnClickListener(mLayoutListener);
  }

  //重置更多筛选的条件
  private void resetQuaryMoreData() {
    if (mTvReset == null) return;
    term = FilterUtils.getFilterTerm(host);
    //清空term
    term.setStructure(0);
    term.setFrom_year(0);
    term.setTo_year(0);
    term.setFrom_miles(0);
    term.setTo_miles(0);
    term.setGearboxType(0);
    term.setStandard(0);
    term.setFrom_emission(0);
    term.setTo_emission(0);
    term.setCounty(0);
    term.setColor(0);
    FilterUtils.setFilterTerm(host, term);
    notifyChooseChange();
    notifyChooseChangeForFold();
    requestSearchData(host);
  }

  private void initRequestDefaultData() {
    if (mTvResultHint == null) return;
    Spanned SpannedPrice = Html.fromHtml(HCFormatUtil.getMoreResultFormat("~"));
    mTvResultHint.setText(SpannedPrice);
    requestSearchData(host);
  }

  private void initGridByType(int type, HCGridView gv) {
    if (gv == null) return;
    List<KeyValueEntity> data = FilterUtils.getDefaultSortData(type);
    int layoutId = R.layout.gridview_item_filter_common;
    if (HCConsts.FILTER_CAR_TYPE == type) {
      layoutId = R.layout.gridview_item_filter_car_type;
    }
    FilterGridViewAdapter adapter =
        new FilterGridViewAdapter(getActivity(), data, layoutId, type, host);
    adapter.setOnClickListener(mClickListener);
    gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
    gv.setAdapter(adapter);

    switch (type) {
      case HCConsts.FILTER_CAR_TYPE:
        mCarTypeAdapter = adapter;
        break;
      case HCConsts.FILTER_CAR_AGE:
        mCarAgeAdapter = adapter;
        break;
      case HCConsts.FILTER_DISTANCE:
        mDistanceAdapter = adapter;
        break;
      case HCConsts.FILTER_SPEED_BOX:
        mSpeedBoxAdapter = adapter;
        break;
      case HCConsts.FILTER_STANDARD:
        mStandardAdapter = adapter;
        break;
      case HCConsts.FILTER_EMISSION:
        mEmissionsAdapter = adapter;
        break;
      case HCConsts.FILTER_COUNTRY:
        mCountryAdapter = adapter;
        break;
      case HCConsts.FILTER_COLOR:
        mColorAdapter = adapter;
        break;
    }
  }

  private View.OnClickListener mLayoutListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      switch (v.getId()) {
        case R.id.rel_filter_speed_box:
          showGridViewStatus(mSpeedBoxGridView, mSpeedBoxTv);
          break;
        case R.id.rel_filter_standard:
          showGridViewStatus(mStandardGridView, mStandardTv);
          break;
        case R.id.rel_filter_emissions:
          showGridViewStatus(mEmissionsGridView, mEmissionsTv);
          break;
        case R.id.rel_filter_country:
          showGridViewStatus(mCountryGridView, mCountryTv);
          break;
        case R.id.rel_filter_color:
          showGridViewStatus(mColorGridView, mColorTv);
          break;
      }
    }
  };

  private void showGridViewStatus(final HCGridView gv, TextView tv) {
    if (gv.getVisibility() == View.VISIBLE) {
      HCViewUtils.setTextViewDrawable(tv, R.drawable.icon_close, HCConsts.DRAWABLE_RIGHT);
      gv.setVisibility(View.GONE);
    } else {
      HCViewUtils.setTextViewDrawable(tv, R.drawable.icon_open, HCConsts.DRAWABLE_RIGHT);
      gv.setVisibility(View.VISIBLE);
      mScrollView.postDelayed(new Runnable() {
        @Override public void run() {
          if (gv.getAdapter() != null) {
            int row = (gv.getAdapter().getCount() - 1) / 3 + 1;
            mScrollView.smoothScrollTo(0, mScrollView.getScrollY() + HCUtils.dp2px(row * 45 + 5));
          }
        }
      }, 10);
    }
  }

  private View.OnClickListener mClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {

      term = FilterUtils.getFilterTerm(host);
      KeyValueEntity entity = (KeyValueEntity) v.getTag(R.id.filter_gv_item);
      String key = entity.getKey();
      switch (v.getId()) {
        case R.id.filter_more_cartype:
          if (key.equals(FilterUtils.getFilterTermString(term, HCConsts.FILTER_CAR_TYPE))) {
            term.setStructure(0);
          } else {
            FilterUtils.changeFilterTerm(term, HCConsts.FILTER_CAR_TYPE, key);
          }
          mCarTypeAdapter.notifyDataSetChanged();
          break;
        case R.id.filter_more_car_age:
          if (key.equals(FilterUtils.getFilterTermString(term, HCConsts.FILTER_CAR_AGE))) {
            term.setFrom_year(0);
            term.setTo_year(0);
          } else {
            FilterUtils.changeFilterTerm(term, HCConsts.FILTER_CAR_AGE, key);
          }
          mCarAgeAdapter.notifyDataSetChanged();
          break;
        case R.id.filter_more_distance:
          if (key.equals(FilterUtils.getFilterTermString(term, HCConsts.FILTER_DISTANCE))) {
            term.setFrom_miles(0);
            term.setTo_miles(0);
          } else {
            FilterUtils.changeFilterTerm(term, HCConsts.FILTER_DISTANCE, key);
          }
          mDistanceAdapter.notifyDataSetChanged();
          break;
        case R.id.filter_more_speedbox:
          if (key.equals(FilterUtils.getFilterTermString(term, HCConsts.FILTER_SPEED_BOX))) {
            term.setGearboxType(0);
            mSpeedBoxTv.setTextColor(normalColor);
            mSpeedBoxTv.setText(HCConsts.UNLIMITED);
          } else {
            FilterUtils.changeFilterTerm(term, HCConsts.FILTER_SPEED_BOX, key);
            mSpeedBoxTv.setTextColor(redColor);
            mSpeedBoxTv.setText(key);
          }
          mSpeedBoxAdapter.notifyDataSetChanged();
          break;
        case R.id.filter_more_standard:
          if (key.equals(FilterUtils.getFilterTermString(term, HCConsts.FILTER_STANDARD))) {
            term.setStandard(0);
            mStandardTv.setTextColor(normalColor);
            mStandardTv.setText(HCConsts.UNLIMITED);
          } else {
            FilterUtils.changeFilterTerm(term, HCConsts.FILTER_STANDARD, key);
            mStandardTv.setTextColor(redColor);
            mStandardTv.setText(key);
          }
          mStandardAdapter.notifyDataSetChanged();
          break;
        case R.id.filter_more_emissions:
          if (key.equals(FilterUtils.getFilterTermString(term, HCConsts.FILTER_EMISSION))) {
            term.setFrom_emission(0);
            term.setTo_emission(0);
            mEmissionsTv.setTextColor(normalColor);
            mEmissionsTv.setText(HCConsts.UNLIMITED);
          } else {
            FilterUtils.changeFilterTerm(term, HCConsts.FILTER_EMISSION, key);
            mEmissionsTv.setTextColor(redColor);
            mEmissionsTv.setText(key);
          }
          mEmissionsAdapter.notifyDataSetChanged();
          break;
        case R.id.filter_more_country:
          if (key.equals(FilterUtils.getFilterTermString(term, HCConsts.FILTER_COUNTRY))) {
            term.setCounty(0);
            mCountryTv.setTextColor(normalColor);
            mCountryTv.setText(HCConsts.UNLIMITED);
          } else {
            FilterUtils.changeFilterTerm(term, HCConsts.FILTER_COUNTRY, key);
            mCountryTv.setTextColor(redColor);
            mCountryTv.setText(key);
          }
          mCountryAdapter.notifyDataSetChanged();
          break;
        case R.id.filter_more_color:
          if (key.equals(FilterUtils.getFilterTermString(term, HCConsts.FILTER_COLOR))) {
            term.setColor(0);
            mColorTv.setTextColor(normalColor);
            mColorTv.setText(HCConsts.UNLIMITED);
          } else {
            FilterUtils.changeFilterTerm(term, HCConsts.FILTER_COLOR, key);
            mColorTv.setTextColor(redColor);
            mColorTv.setText(key);
          }
          mColorAdapter.notifyDataSetChanged();
          break;
      }
      FilterUtils.setFilterTerm(host, term);
      requestSearchData(host);
    }
  };

  private void requestSearchData(String host) {
    Map<String, Object> params;
    if (allHost.equals(host)) {
      params = HCParamsUtil.getAllGoodMoreFilterCount();
    } else if (todayHost.equals(host)) {
      params = HCParamsUtil.getTodayMoreFilterCount();
    } else {
      params = HCParamsUtil.getDirectMoreFilterCount();
    }
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleSearchData(responseJsonString);
      }
    }));
  }

  private void handleSearchData(String responseJsonString) {
    if (mTvResultHint == null) return;
    if (TextUtils.isEmpty(responseJsonString)) return;

    HCMoreFilterItemEntity entry = HCJSONParser.parseMoreFilterCount(responseJsonString);
    Spanned SpannedPrice = Html.fromHtml(HCFormatUtil.getMoreResultFormat(entry.getCount()));
    mTvResultHint.setText(SpannedPrice);
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

  @Override public void onResume() {
    if (!isInitOtherGv) {
      //第一次可见时(全部滑出来时)再加载最后5个筛选项
      mSpeedBoxGridView.postDelayed(new Runnable() {
        @Override public void run() {
          initGridByType(HCConsts.FILTER_SPEED_BOX, mSpeedBoxGridView);
          initGridByType(HCConsts.FILTER_STANDARD, mStandardGridView);
          initGridByType(HCConsts.FILTER_EMISSION, mEmissionsGridView);
          initGridByType(HCConsts.FILTER_COUNTRY, mCountryGridView);
          initGridByType(HCConsts.FILTER_COLOR, mColorGridView);
          isInitOtherGv = true;
        }
      }, 800);
    }
    super.onResume();
  }
}
