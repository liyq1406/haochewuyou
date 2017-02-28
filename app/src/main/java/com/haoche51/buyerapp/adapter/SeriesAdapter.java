package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.SeriesEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.List;

/**
 * 车系选择
 */
public class SeriesAdapter extends HCCommonAdapter<SeriesEntity> {

  private View.OnClickListener mClickListener;

  private String host;

  public SeriesAdapter(Context context, List<SeriesEntity> data, int layoutid, String host,
      View.OnClickListener listener) {
    super(context, data, layoutid);
    this.host = host;
    this.mClickListener = listener;
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    SeriesEntity entity = getItem(position);
    TextView nameTv = holder.findTheView(R.id.tv_carseries_item);
    nameTv.setText(entity.getName());
    nameTv.setTag(entity);
    nameTv.setOnClickListener(mClickListener);
    int mTextColor = (R.color.home_hot_text);
    if (FilterUtils.getFilterTerm(host).getClass_id() == entity.getId()) {
      mTextColor = R.color.reminder_red;
    }
    int color = HCUtils.getResColor(mTextColor);
    nameTv.setTextColor(color);
  }
}
