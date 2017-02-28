package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.view.View;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.SoldBuyerListEntity;
import java.util.List;

/**
 * 出售进度  适配器
 */
public class SoldVehicleAdapter extends HCCommonAdapter<SoldBuyerListEntity> {

  public SoldVehicleAdapter(Context context, List<SoldBuyerListEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    SoldBuyerListEntity entity = getItem(position);
    holder.setTextViewText(R.id.tv_solditem_text, entity.getText());
    holder.setTextViewText(R.id.tv_solditem_time, entity.getCreate_time());

    View line = holder.findTheView(R.id.view_for_divider);
    //去除最后一条的分割线
    if (position == getCount() - 1) {
      line.setVisibility(View.INVISIBLE);
    } else {
      line.setVisibility(View.VISIBLE);
    }
  }
}
