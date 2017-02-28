package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.VehicleDetailActivity;
import com.haoche51.buyerapp.entity.ScanHistoryEntity;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCFormatUtil;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.List;

public class ChooseVehicleForDiffAdapter extends HCCommonAdapter<ScanHistoryEntity> {
  public ChooseVehicleForDiffAdapter(Context context, List<ScanHistoryEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  public OnDiffVehicleChangedListener mChangedListener;
  private int mLastCheckedPos = -1;

  @Override public void fillViewData(HCCommonViewHolder mHolder, int position) {
    ScanHistoryEntity entity = getItem(position);
    // 设置是否售出
    int status = HCUtils.str2Int(entity.getStatus());
    View mSoldIconIv = mHolder.findTheView(R.id.iv_diffcar_car_sold);
    if (status == HCConsts.STATUS_SOLD || status == HCConsts.STATUS_OWNER_SOLD) {
      mSoldIconIv.setVisibility(View.VISIBLE);
    } else {
      mSoldIconIv.setVisibility(View.GONE);
    }

    // 设置价格
    Spanned SpannedPrice =
        Html.fromHtml(HCFormatUtil.getSoldPriceFormat(HCUtils.str2Float(entity.getSeller_price())));
    mHolder.setTextViewText(R.id.tv_diffcar_car_price, SpannedPrice);
    String simpleDetail =
        HCFormatUtil.getSimpleVehicleDetail(entity.getRegister_time(), entity.getMiles());
    mHolder.setTextViewText(R.id.tv_diffcar_car_detail, simpleDetail);
    mHolder.setTextViewText(R.id.tv_diffcar_carname,
        HCFormatUtil.getVehicleName(entity.getVehicle_name()));
    mHolder.loadHttpImage(R.id.iv_diffcar_image, entity.getCover_pic());

    //设置图片水印
    String leftTopUrl = entity.getLeft_top();
    String leftTopRate = entity.getLeft_top_rate();
    ImageView leftTopIv = mHolder.findTheView(R.id.iv_diffcar_left_top_image);
    if (!TextUtils.isEmpty(leftTopUrl) && !TextUtils.isEmpty(leftTopRate)) {
      leftTopIv.setVisibility(View.VISIBLE);
      int leftTopWh = HCUtils.dp2px(120F / HCUtils.str2Float(leftTopRate));
      leftTopIv.getLayoutParams().width = leftTopWh;
      leftTopIv.getLayoutParams().height = leftTopWh;
      mHolder.loadHttpImage(leftTopIv, leftTopUrl);
    } else {
      HCUtils.hideViewIfNeed(leftTopIv);
    }
    String leftBottomUrl = entity.getLeft_bottom();
    String leftBottomRate = entity.getLeft_bottom_rate();
    ImageView leftBottomIv = mHolder.findTheView(R.id.iv_diffcar_left_bottom_image);
    if (!TextUtils.isEmpty(leftBottomUrl) && !TextUtils.isEmpty(leftBottomRate)) {
      leftBottomIv.setVisibility(View.VISIBLE);
      int leftBottomWh = HCUtils.dp2px(120F / HCUtils.str2Float(leftBottomRate));
      leftBottomIv.getLayoutParams().width = leftBottomWh;
      leftBottomIv.getLayoutParams().height = leftBottomWh;
      mHolder.loadHttpImage(leftBottomIv, leftBottomUrl);
    } else {
      HCUtils.hideViewIfNeed(leftBottomIv);
    }

    View mFrame = mHolder.findTheView(R.id.frame_diffcar);
    View mPicIv = mHolder.findTheView(R.id.iv_diffcar_image);
    mFrame.setTag(entity);
    mPicIv.setTag(entity);
    mFrame.setOnClickListener(mListener);
    mPicIv.setOnClickListener(mListener);

    ImageView mChooseStatusIv = mHolder.findTheView(R.id.cb_diffcar);

    mChooseStatusIv.setVisibility(View.VISIBLE);
    int res =
        position == mLastCheckedPos ? R.drawable.icon_sub_choosed : R.drawable.icon_sub_unchoosed;
    mChooseStatusIv.setImageResource(res);
    mChooseStatusIv.setTag(position);
    mChooseStatusIv.setTag(R.id.view_tag_diffvehicle, res);
    mChooseStatusIv.setOnClickListener(mListener);
  }

  private View.OnClickListener mListener = new View.OnClickListener() {

    @Override public void onClick(View v) {
      switch (v.getId()) {
        case R.id.cb_diffcar:
          int pos = (Integer) v.getTag();
          int res = (Integer) v.getTag(R.id.view_tag_diffvehicle);
          mLastCheckedPos = res != R.drawable.icon_sub_choosed ? pos : -1;
          notifyDataSetChanged();
          ScanHistoryEntity sEntity = mLastCheckedPos == -1 ? null : getItem(mLastCheckedPos);
          if (mChangedListener != null) {
            mChangedListener.onVehicleChanged(sEntity);
            if (sEntity != null) {
            }
          }
          break;
        case R.id.iv_diffcar_image:
        case R.id.frame_diffcar:
          ScanHistoryEntity entity = (ScanHistoryEntity) v.getTag();
          VehicleDetailActivity.idToThis(GlobalData.mContext, entity.getId(), "比一比");
          break;
      }
    }
  };

  public void setOnDiffVehicleChangedListener(OnDiffVehicleChangedListener l) {
    this.mChangedListener = l;
  }

  public interface OnDiffVehicleChangedListener {
    void onVehicleChanged(ScanHistoryEntity changedEntity);
  }
}
