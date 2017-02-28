package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.view.View;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.VehicleDetailActivity;
import com.haoche51.buyerapp.entity.HCVehicleItemEntity;
import java.util.List;

public class NewArrivalAdapter extends SinglePicVehicleAdapter {

  public NewArrivalAdapter(Context context, List<HCVehicleItemEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  @Override public void fillOtherData(HCCommonViewHolder holder, final int position) {
    final HCVehicleItemEntity entity = getItem(position);
    if (entity != null) {

      View innerView = holder.findTheView(R.id.rel_singlepic_parent);
      if (innerView != null) {
        innerView.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            String curId = entity.getId();
            VehicleDetailActivity.idToThis(GlobalData.mContext, curId, "新上");
          }
        });
      }
    }
  }
}
