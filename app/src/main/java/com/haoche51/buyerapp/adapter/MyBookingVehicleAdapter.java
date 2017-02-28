package com.haoche51.buyerapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.UseCouponActivity;
import com.haoche51.buyerapp.activity.VehicleDetailActivity;
import com.haoche51.buyerapp.entity.HCBookOrderEntity;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCFormatUtil;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.List;

/**
 * 我的预约适配器
 */
public class MyBookingVehicleAdapter extends HCCommonAdapter<HCBookOrderEntity> {

  public MyBookingVehicleAdapter(Context context, List<HCBookOrderEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  private View.OnClickListener mClicklistener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      switch (v.getId()) {
        case R.id.tv_booking_coupon:
          if (v.getTag() == null) {
            int status = (int) v.getTag(R.id.view_for_sub_dim);
            DialogUtils.showCouponIntro((Activity) mContext, status);
          } else {
            HCBookOrderEntity order = (HCBookOrderEntity) v.getTag();
            Intent intent = new Intent(GlobalData.mContext, UseCouponActivity.class);
            intent.putExtra(HCConsts.INTENT_KEY_ORDERID, order.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            GlobalData.mContext.startActivity(intent);
          }
          break;

        case R.id.rel_sub_booking_parent:
          if (v.getTag() != null) {
            HCBookOrderEntity orderEntity = (HCBookOrderEntity) v.getTag();
            String vehicleSourceId = orderEntity.getVehicle_source_id();
            VehicleDetailActivity.idToThis(GlobalData.mContext, vehicleSourceId, "预定");
          }
          break;
      }
    }
  };

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    HCBookOrderEntity entity = getItem(position);
    //名字
    String vehicle_name = entity.getVehicle_name();
    holder.setTextViewText(R.id.tv_booking_carname, vehicle_name);
    //价格
    String strPrice = entity.getPrice();
    if (!TextUtils.isEmpty(strPrice)) {
      float price = Float.valueOf(strPrice);
      Spanned spannedPrice = Html.fromHtml(HCFormatUtil.getSoldPriceFormat(price));
      holder.setTextViewText(R.id.tv_booking_car_price, spannedPrice);
    }

    //上牌,里程,变速箱类型
    String detail = HCFormatUtil.getOrderDetail(entity);
    holder.setTextViewText(R.id.tv_booking_car_detail, detail);

    //图片
    holder.loadHttpImage(R.id.iv_booking_image, entity.getImage());

    int status = entity.getStatus();
    //使用优惠券
    //判断成交状态,显示成交图标
    boolean isSucc = isOrderSucc(status);
    int visibleStatus = isSucc ? View.VISIBLE : View.GONE;
    holder.findTheView(R.id.iv_booking_status).setVisibility(visibleStatus);
    holder.findTheView(R.id.iv_sold_status).setVisibility(View.GONE);

    TextView orderStatustv = holder.findTheView(R.id.tv_booking_status);
    if (!isSucc) {
      //未成交才设置状态
      orderStatustv.setTextColor(getColor(status));
      orderStatustv.setText(getStatusDesc(status));
      orderStatustv.setVisibility(View.VISIBLE);
    } else {
      orderStatustv.setVisibility(View.GONE);
    }

    //使用优惠券按钮
    View useCouponTv = holder.findTheView(R.id.tv_booking_coupon);
    useCouponTv.setVisibility(View.GONE);
    //已使用优惠券描述
    TextView useCouponDescTv = holder.findTheView(R.id.tv_booking_coupon_used_desc);
    useCouponDescTv.setVisibility(View.GONE);

    //除了已售出和已使用隐藏使用优惠券按钮
    if (status == 3 || status == 5 || status == 7) {
      useCouponTv.setVisibility(View.GONE);

      if (status == 5 || status == 7) {
        useCouponDescTv.setVisibility(View.VISIBLE);
        String coupon_amount = entity.getCoupon_amount();
        if (!TextUtils.isEmpty(coupon_amount)) {
          String formatStr =
              String.format(mContext.getString(R.string.hc_has_used_coupon), coupon_amount);
          useCouponDescTv.setText(formatStr);
        }
      }
      if (status == 3) {
        //设置已出售图片
        holder.findTheView(R.id.iv_sold_status).setVisibility(View.VISIBLE);
      }
    } else {
      useCouponTv.setVisibility(View.VISIBLE);
      HCBookOrderEntity tagEntity = null;
      if (status == 6 || status == 8) {
        tagEntity = entity;
      }
      useCouponTv.setTag(tagEntity);
      useCouponTv.setTag(R.id.view_for_sub_dim, status);
      useCouponTv.setOnClickListener(mClicklistener);
    }

    long time = entity.getTime();
    String formatedTime = HCFormatUtil.formatOrderTime(time);
    TextView timeTv = holder.findTheView(R.id.tv_booking_time);
    View lineTime = holder.findTheView(R.id.line_time);
    if (!TextUtils.isEmpty(formatedTime)) {
      timeTv.setVisibility(View.VISIBLE);
      lineTime.setVisibility(View.VISIBLE);
      formatedTime = formatedTime + " " + entity.getType();
      timeTv.setText(formatedTime);
    } else {
      timeTv.setVisibility(View.GONE);
      lineTime.setVisibility(View.GONE);
    }

    String place = entity.getPlace();
    View linePlace = holder.findTheView(R.id.line_address);
    TextView placeTv = holder.findTheView(R.id.tv_booking_address);
    if (!TextUtils.isEmpty(place)) {
      placeTv.setVisibility(View.VISIBLE);
      linePlace.setVisibility(View.VISIBLE);
      placeTv.setText(place);
    } else {
      placeTv.setVisibility(View.GONE);
      linePlace.setVisibility(View.GONE);
    }

    String man_desc = entity.getDesc();
    String man_name = entity.getName();
    TextView manTv = holder.findTheView(R.id.tv_booking_man);
    if (!TextUtils.isEmpty(man_desc) && !TextUtils.isEmpty(man_name)) {
      manTv.setVisibility(View.VISIBLE);
      String str = man_desc + ":" + man_name;
      manTv.setText(str);
    } else {
      manTv.setVisibility(View.GONE);
    }

    //电话号码
    String strPhone = entity.getPhone();
    TextView phoneTv = holder.findTheView(R.id.tv_booking_phone);
    if (!TextUtils.isEmpty(strPhone)) {
      phoneTv.setVisibility(View.VISIBLE);
      phoneTv.setText(strPhone);
    } else {
      phoneTv.setVisibility(View.GONE);
    }

    if (manTv.getVisibility() == View.GONE && phoneTv.getVisibility() == View.GONE) {
      holder.findTheView(R.id.linear_booking).setVisibility(View.GONE);
    } else {
      holder.findTheView(R.id.linear_booking).setVisibility(View.VISIBLE);
    }

    String comment = entity.getComment();
    TextView commentTv = holder.findTheView(R.id.tv_booking_desc);
    if (!TextUtils.isEmpty(comment)) {
      commentTv.setVisibility(View.VISIBLE);
      commentTv.setText(comment);
    } else {
      commentTv.setVisibility(View.GONE);
    }

    View parent = holder.findTheView(R.id.rel_sub_booking_parent);
    parent.setTag(entity);
    parent.setOnClickListener(mClicklistener);
  }

  //已预约  绿色
  //已预定  蓝色
  //其余灰色
  public int getColor(int status) {
    int color = R.color.font_gray;
    if (status == HCConsts.ORDER_HAS_RESERVE) {
      color = R.color.color_green;
    } else if (status == HCConsts.ORDER_HAS_ORDAIN) {
      color = R.color.reminder_red;
    }
    return HCUtils.getResColor(color);
  }

  public String getStatusDesc(int status) {
    String[] arr = HCUtils.getResArray(R.array.status);
    return arr[status];
  }

  /**
   * 判断用户订单是否已经成交
   */
  public boolean isOrderSucc(int status) {
    return status >= 5;
  }
}

