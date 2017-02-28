package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCCouponEntity;
import com.haoche51.buyerapp.util.HCFormatUtil;
import java.util.List;

/**
 * 选择优惠券__适配器
 */
public class UseCouponAdapter extends HCCommonAdapter<HCCouponEntity> {
  public UseCouponAdapter(Context context, List<HCCouponEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  private int mLastPos = -1;

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    final HCCouponEntity entity = getItem(position);
    holder.findTheView(R.id.iv_item_coupon_icon).setVisibility(View.GONE);

    holder.setTextViewText(R.id.tv_item_coupon_price, String.valueOf(entity.getAmount()));
    holder.setTextViewText(R.id.tv_item_coupon_name, entity.getTitle());

    long from_time = entity.getFrom_time();
    long expire_time = entity.getExpire_time();
    String time = HCFormatUtil.formatCouponTime(from_time, expire_time);
    holder.setTextViewText(R.id.tv_item_coupon_time, time);

    boolean isValid  = (System.currentTimeMillis() / 1000) < expire_time;
    int visible = isValid ? View.INVISIBLE : View.VISIBLE;
    holder.findTheView(R.id.iv_item_coupon_valid).setVisibility(visible);
    //判断是否使用,设置相应图标

    FrameLayout mParentBg = holder.findTheView(R.id.frame_coupon_item_parent);

    if (visible != View.VISIBLE) {
      mParentBg.setBackgroundResource(R.drawable.bg_valid_coupon);
    } else {
      mParentBg.setBackgroundResource(R.drawable.bg_invalid_coupon);
    }

    //设置选择状态
    ImageView ivChoose = holder.findTheView(R.id.iv_use_coupon);
    int res = position == mLastPos ? R.drawable.icon_sub_choosed : R.drawable.icon_sub_unchoosed;
    ivChoose.setImageResource(res);

    ivChoose.setTag(position);
    ivChoose.setTag(R.id.view_tag_choosecoupon, res);
    ivChoose.setOnClickListener(mCLickListener);

    View frame = holder.findTheView(R.id.frame_coupon_item_parent);
    frame.setTag(position);
    frame.setTag(R.id.view_tag_choosecoupon, res);
    frame.setOnClickListener(mCLickListener);
  }

  private View.OnClickListener mCLickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {

      switch (v.getId()) {
        case R.id.frame_coupon_item_parent:
        case R.id.iv_use_coupon:
          int pos = (Integer) v.getTag();
          int res = (Integer) v.getTag(R.id.view_tag_choosecoupon);
          mLastPos = res != R.drawable.icon_sub_choosed ? pos : -1;
          notifyDataSetChanged();
          HCCouponEntity sEntity = mLastPos == -1 ? null : getItem(mLastPos);
          if (mOnCouponChoosedListener != null) {
            mOnCouponChoosedListener.onChoosed(sEntity);
          }
          break;
      }
    }
  };

  public void setOnCouponChoosedListener(onCouponChoosedListener l) {
    this.mOnCouponChoosedListener = l;
  }

  private onCouponChoosedListener mOnCouponChoosedListener;

  public interface onCouponChoosedListener {
    void onChoosed(HCCouponEntity entity);
  }
}
