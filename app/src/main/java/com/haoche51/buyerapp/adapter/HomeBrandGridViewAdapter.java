package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.WebBrowserActivity;
import com.haoche51.buyerapp.entity.HomeBrandEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import java.util.List;

public class HomeBrandGridViewAdapter extends HCCommonAdapter<HomeBrandEntity> {

  public HomeBrandGridViewAdapter(Context context, List<HomeBrandEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    HomeBrandEntity entity = getItem(position);
    if (TextUtils.isEmpty(entity.getTitle())) {
      setBrand(entity, holder);
    } else {
      setActivity(entity, holder);
    }
  }

  private void setBrand(final HomeBrandEntity entity, HCCommonViewHolder holder) {
    String brandName = entity.getBrand_name();
    int brandId = HCUtils.str2Int(entity.getBrand_id());
    int resId = HCViewUtils.getBrandResID(brandId, R.drawable.empty_brand);
    TextView mBrandTv = holder.findTheView(R.id.tv_home_brand_item);
    mBrandTv.setText(brandName);
    Drawable logo = null;
    try {
      logo = HCUtils.getResDrawable(resId);
    } catch (Exception e) {
      HCLog.d("HomeBrandGridViewAdapter", "HomeBrandGridViewAdapter is crash ... ");
    }
    if (logo != null) {
      logo.setBounds(0, 0, (int) (logo.getIntrinsicWidth() / 3F),
          (int) (logo.getIntrinsicHeight() / 3F));
      mBrandTv.setCompoundDrawables(logo, null, null, null);
    }

    View convertView = holder.getConvertView();
    convertView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        FilterUtils.resetNormalToDefaultExceptCity();
        HCSensorsUtil.homePageClick(entity.getBrand_name());
        FilterUtils.saveBrandFilterTerm(FilterUtils.allHost, HCUtils.str2Int(entity.getBrand_id()),
            -1);
        HCEvent.postEvent(HCEvent.ACTION_HOME_TO_MAINACT_GOOD_VEHICLES);
        HCEvent.postEvent(FilterUtils.allHost + HCEvent.ACTION_BRAND_CHOOSED_CHANGE);
        HCStatistic.homeBrandClick();
      }
    });
  }

  private void setActivity(final HomeBrandEntity entity, HCCommonViewHolder holder) {
    final String title = entity.getTitle();
    TextView mBrandTv = holder.findTheView(R.id.tv_home_brand_item);
    mBrandTv.setText(title);
    mBrandTv.setCompoundDrawables(null, null, null, null);
    View convertView = holder.getConvertView();
    convertView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        HCSensorsUtil.homePageClick(title);
        WebBrowserActivity.urlToThis(GlobalData.mContext, entity.getUrl());
        if (!TextUtils.isEmpty(title)) {
          HCStatistic.homeactivityClick(title);
        }
      }
    });
  }
}
