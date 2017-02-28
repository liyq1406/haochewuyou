package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCBrandEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import java.util.List;

public class FilterBrandGridViewAdapter extends HCCommonAdapter<HCBrandEntity> {

  public FilterBrandGridViewAdapter(Context context, List<HCBrandEntity> data, int layoutid,
      String host) {
    super(context, data, layoutid);
    this.host = host;
  }

  private View.OnClickListener mListener;
  private String host;

  public void setmListener(View.OnClickListener mListener) {
    this.mListener = mListener;
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    HCBrandEntity entity = getItem(position);
    setBrand(entity, holder);
  }

  private void setBrand(final HCBrandEntity entity, HCCommonViewHolder holder) {
    String brandName = entity.getBrand_name();
    int brandId = entity.getBrand_id();
    int resId = HCViewUtils.getBrandResID(brandId, R.drawable.empty_brand);
    TextView mBrandTv = holder.findTheView(R.id.tv_filter_brand_item);
    LinearLayout layout = holder.findTheView(R.id.linear_filter_brand_item);
    int mTextColor = (R.color.home_hot_text);
    if (FilterUtils.getFilterTerm(host).getBrand_id() == brandId) {
      mTextColor = R.color.reminder_red;
    }
    int color = HCUtils.getResColor(mTextColor);
    mBrandTv.setTextColor(color);
    mBrandTv.setText(brandName);
    Drawable logo = null;
    try {
      logo = HCUtils.getResDrawable(resId);
    } catch (Exception e) {
      HCLog.d("FilterBrandGridViewAdapter", "FilterBrandGridViewAdapter is crash ... ");
    }
    if (logo != null) {
      logo.setBounds(0, 0, (int) (logo.getIntrinsicWidth() * 70F / 120F),
          (int) (logo.getIntrinsicHeight() * 70F / 120F));
      mBrandTv.setCompoundDrawables(null, logo, null, null);
    }
    layout.setTag(entity);
    layout.setOnClickListener(mListener);
  }
}
