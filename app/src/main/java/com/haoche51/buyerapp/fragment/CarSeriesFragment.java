package com.haoche51.buyerapp.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.SeriesAdapter;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.SeriesEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import java.util.ArrayList;
import java.util.List;

/***
 * 显示车系的fragment
 */
public class CarSeriesFragment extends HCBaseFragment {

  public static CarSeriesFragment newInstance() {
    return new CarSeriesFragment();
  }

  private SeriesEntity unlimited = new SeriesEntity(-1, -1);

  private String host;

  public void setHost(String host) {
    this.host = host;
  }

  @InjectView(R.id.tv_carseries_back) TextView mBackTv;
  @InjectView(R.id.tv_carseries_for_click) TextView mForClickTv;
  @InjectView(R.id.lv_carseries) ListView mContentLv;
  @InjectView(R.id.rel_carseries_parent) View mCarseriesParent;
  @InjectView(R.id.tv_series_name) TextView mSeriesTv;

  private ImageView mLogoIv;
  private TextView mUnLimitTv;

  private SeriesAdapter mAdapter;
  private List<SeriesEntity> mSeriesData = new ArrayList<>();

  private View.OnClickListener mClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      SeriesEntity entity = null;

      switch (v.getId()) {
        case R.id.tv_carseries_back:
          break;
        case R.id.tv_carseries_item:
        case R.id.tv_series_un_limit:
          Object o = v.getTag();
          if (o != null && o instanceof SeriesEntity) {
            entity = (SeriesEntity) o;
          }
          break;
      }
      HCEvent.postEvent(host + HCEvent.ACTION_CAR_SERIES_CHOOSED_RETURN, entity);
    }
  };

  @Override boolean isNeedBindEventBus() {
    return false;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.filter_car_series;
  }

  @Override void doInitViewOrData() {
    if (mContentLv == null) return;

    int width = (int) (HCUtils.getScreenWidthInPixels() * 110F / 750F);
    mForClickTv.getLayoutParams().width = width;
    mCarseriesParent.getLayoutParams().width = HCUtils.getScreenWidthInPixels() - width;
    // 不写这个点击空白处 brandFragment会响应点击事件
    mCarseriesParent.setOnClickListener(null);
    mSeriesTv.setOnClickListener(null);

    mBackTv.setOnClickListener(mClickListener);

    setHeaderView();

    int itemRes = R.layout.lvitem_car_series;
    mAdapter = new SeriesAdapter(getActivity(), mSeriesData, itemRes, host, mClickListener);
    mContentLv.setAdapter(mAdapter);
  }

  @Override public void onEvent(HCCommunicateEntity entity) {

  }

  private void setHeaderView() {
    View headView =
        LayoutInflater.from(getActivity()).inflate(R.layout.filter_series_headerview, null);
    mLogoIv = (ImageView) headView.findViewById(R.id.iv_carseries_logo);
    mUnLimitTv = (TextView) headView.findViewById(R.id.tv_series_un_limit);
    mUnLimitTv.setOnClickListener(mClickListener);
    mContentLv.addHeaderView(headView);
  }

  public void setCarSeries(int brand_id, String brand_name, List<SeriesEntity> seriesData) {
    if (mLogoIv == null) return;
    this.mSeriesData.clear();
    unlimited.setBrand_id(brand_id);
    unlimited.setBrand_name(brand_name);
    this.mSeriesData.addAll(seriesData);
    HCViewUtils.setIconById(mLogoIv, brand_id, R.drawable.empty_brand);
    mAdapter.notifyDataSetChanged();
    mUnLimitTv.setTag(unlimited);
    FilterTerm term = FilterUtils.getFilterTerm(host);
    if (term.getBrand_id() == brand_id && term.getClass_id() <= 0) {
      mUnLimitTv.setTextColor(HCUtils.getResColor(R.color.home_grx_red));
    }
  }
}
