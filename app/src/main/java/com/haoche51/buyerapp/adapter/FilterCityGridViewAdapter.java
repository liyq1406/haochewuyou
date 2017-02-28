package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCCityEntity;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.List;

public class FilterCityGridViewAdapter extends HCCommonAdapter<HCCityEntity> {

  private View.OnClickListener mListener;
  private int mRedColor = HCUtils.getResColor(R.color.home_grx_red);
  private int mBlackColor = HCUtils.getResColor(R.color.font_black);

  public void setOnClickListener(View.OnClickListener l) {
    mListener = l;
  }

  public FilterCityGridViewAdapter(Context context, List<HCCityEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {

    HCCityEntity entity = getItem(position);
    TextView convertView = holder.findTheView(R.id.tv_city_gridview_item);
    convertView.setTag(R.id.view_tag_city, entity);

    int lastCityID = HCDbUtil.getSavedCityId();
    String cityName = HCDbUtil.getCityNameById(String.valueOf(lastCityID));

    convertView.setText(entity.getCity_name());
    if (cityName.equals(entity.getCity_name())) {
      convertView.setTextColor(mRedColor);
      convertView.setText(entity.getCity_name());
    } else {
      convertView.setTextColor(mBlackColor);
      convertView.setText(entity.getCity_name());
    }

    convertView.setOnClickListener(mListener);
  }
}
