package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCCityEntity;

import com.haoche51.buyerapp.util.HCUtils;
import java.util.List;

/***
 * 首页 城市选择
 */
public class CityGridViewAdapter extends HCCommonAdapter<HCCityEntity> {

  private View.OnClickListener mListener;
  private String name;

  public void setOnClickListener(View.OnClickListener l) {
    mListener = l;
  }

  public CityGridViewAdapter(Context context, List<HCCityEntity> data, int layoutid, String name) {
    super(context, data, layoutid);
    this.name = name;
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {

    HCCityEntity entity = getItem(position);
    TextView convertView = holder.findTheView(R.id.tv_city_gridview_item);
    convertView.setId(R.id.city_gv_item);

    convertView.setText(entity.getCity_name());
    if (entity.getCity_name().equals(name)) {
      convertView.setTextColor(HCUtils.getResColor(R.color.reminder_red));
    }

    convertView.setTag(R.id.view_tag_city, entity);
    convertView.setOnClickListener(mListener);
  }
}
