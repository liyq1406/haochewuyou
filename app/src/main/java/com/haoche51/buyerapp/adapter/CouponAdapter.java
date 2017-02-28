package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.WebBrowserActivity;
import com.haoche51.buyerapp.entity.HCCouponEntity;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCFormatUtil;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.List;

/**
 * 我的优惠券适配器
 **/
public class CouponAdapter extends HCCommonAdapter<HCCouponEntity> {

  public CouponAdapter(Context context, List<HCCouponEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  private void go2Detail(HCCouponEntity entity) {
    if (entity != null) {
      String title = HCUtils.getResString(R.string.hc_my_coupon);
      Intent mIntent = new Intent(GlobalData.mContext, WebBrowserActivity.class);
      mIntent.putExtra(HCConsts.INTENT_KEY_URL, HCUtils.getCouponDetailURL(entity));
      mIntent.putExtra(HCConsts.INTENT_KEY_TITLE, title);
      mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      GlobalData.mContext.startActivity(mIntent);
    }
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    final HCCouponEntity entity = getItem(position);
    holder.findTheView(R.id.iv_item_coupon_icon).setRotation(180);
    holder.setTextViewText(R.id.tv_item_coupon_price, String.valueOf(entity.getAmount()));
    holder.setTextViewText(R.id.tv_item_coupon_name, entity.getTitle());

    long from_time = entity.getFrom_time();
    long expire_time = entity.getExpire_time();
    String time = HCFormatUtil.formatCouponTime(from_time, expire_time);
    holder.setTextViewText(R.id.tv_item_coupon_time, time);

    int status = entity.getStatus();//0为初始 1已使用
    ImageView ivStatus = holder.findTheView(R.id.iv_item_coupon_valid);

    if (status == 1) {
      ivStatus.setVisibility(View.VISIBLE);
      ivStatus.setImageResource(R.drawable.icon_coupon_used);
    } else {
      //初始状态,或者已兑换,看 是否过期
      boolean isValid  = (System.currentTimeMillis() / 1000) < expire_time;
      int visible = isValid ? View.INVISIBLE : View.VISIBLE;
      ivStatus.setVisibility(visible);
      ivStatus.setImageResource(R.drawable.icon_coupon_invalid);
    }

    FrameLayout mParentBg = holder.findTheView(R.id.frame_coupon_item_parent);

    if (ivStatus.getVisibility() != View.VISIBLE) {
      mParentBg.setBackgroundResource(R.drawable.bg_valid_coupon);
    } else {
      mParentBg.setBackgroundResource(R.drawable.bg_invalid_coupon);
    }

    View convertView = holder.getConvertView();
    convertView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        go2Detail(entity);
      }
    });
  }
}
