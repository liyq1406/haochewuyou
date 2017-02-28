package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.KeyValueEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import java.util.List;

/***
 * 筛选栏所有gridview的适配器
 * 通过type区分点击的是什么。
 */
public class FilterGridViewAdapter extends HCCommonAdapter<KeyValueEntity> {

  private View.OnClickListener mListener;
  private int type;
  private String host;

  private final int red = HCUtils.getResColor(R.color.home_grx_red);
  private final int black = HCUtils.getResColor(R.color.home_hot_text);

  public void setOnClickListener(View.OnClickListener l) {
    mListener = l;
  }

  public FilterGridViewAdapter(Context context, List<KeyValueEntity> data, int layoutid, int type,
      String host) {
    super(context, data, layoutid);
    this.type = type;
    this.host = host;
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {

    FilterTerm term = FilterUtils.getFilterTerm(host);
    KeyValueEntity entity = getItem(position);
    FrameLayout layout = holder.findTheView(R.id.frame_filter_gridview_item);
    TextView name = holder.findTheView(R.id.tv_filter_gridview_item);
    FrameLayout layoutClick = holder.findTheView(R.id.frame_filter_gridview_item);
    ImageView redDelete = holder.findTheView(R.id.iv_filter_gridview_item);
    layoutClick.setTag(R.id.filter_gv_item, entity);
    String key = "";
    switch (type) {
      case HCConsts.FILTER_CAR_TYPE:
        key = FilterUtils.getFilterTermString(term, HCConsts.FILTER_CAR_TYPE);
        int carTypeId = HCViewUtils.getCarTypeResID(position + 1);
        if (carTypeId != 0) {
          HCViewUtils.setTextViewDrawable(name, carTypeId, HCConsts.DRAWABLE_TOP);
        }
        layoutClick.setId(R.id.filter_more_cartype);
        break;
      case HCConsts.FILTER_CAR_AGE:
        key = FilterUtils.getFilterTermString(term, HCConsts.FILTER_CAR_AGE);
        layoutClick.setId(R.id.filter_more_car_age);
        break;
      case HCConsts.FILTER_DISTANCE:
        key = FilterUtils.getFilterTermString(term, HCConsts.FILTER_DISTANCE);
        layoutClick.setId(R.id.filter_more_distance);
        break;
      case HCConsts.FILTER_SPEED_BOX:
        key = FilterUtils.getFilterTermString(term, HCConsts.FILTER_SPEED_BOX);
        layoutClick.setId(R.id.filter_more_speedbox);
        break;
      case HCConsts.FILTER_STANDARD:
        key = FilterUtils.getFilterTermString(term, HCConsts.FILTER_STANDARD);
        layoutClick.setId(R.id.filter_more_standard);
        break;
      case HCConsts.FILTER_EMISSION:
        key = FilterUtils.getFilterTermString(term, HCConsts.FILTER_EMISSION);
        layoutClick.setId(R.id.filter_more_emissions);
        break;
      case HCConsts.FILTER_COUNTRY:
        key = FilterUtils.getFilterTermString(term, HCConsts.FILTER_COUNTRY);
        int countryId = HCViewUtils.getCountryResID(position + 1);
        if (countryId != 0) {
          HCViewUtils.setTextViewDrawable(name, countryId, HCConsts.DRAWABLE_LEFT);
        }
        layoutClick.setId(R.id.filter_more_country);
        break;
      case HCConsts.FILTER_COLOR:
        key = FilterUtils.getFilterTermString(term, HCConsts.FILTER_COLOR);
        int colorId = HCViewUtils.getColorResID(position + 1);
        if (colorId != 0) {
          HCViewUtils.setTextViewDrawable(name, colorId, HCConsts.DRAWABLE_LEFT);
        }
        layoutClick.setId(R.id.filter_more_color);
        break;
    }
    name.setText(entity.getKey());
    setTextViewColor(layout, redDelete, name, key, entity.getKey());
    FilterUtils.setFilterTerm(host, term);
    layoutClick.setOnClickListener(mListener);
  }

  private void setTextViewColor(FrameLayout layout, ImageView iv, TextView tv, String chooseKey,
      String key) {
    if (chooseKey.equals(key)) {
      iv.setVisibility(View.VISIBLE);
      tv.setTextColor(red);
      layout.setBackgroundResource(R.drawable.filter_textview_item_choose);
    } else {
      iv.setVisibility(View.GONE);
      tv.setTextColor(black);
      layout.setBackgroundResource(R.drawable.filter_textview_item);
    }
  }
}
