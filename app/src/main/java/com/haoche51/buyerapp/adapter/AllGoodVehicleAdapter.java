package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.VehicleDetailActivity;
import com.haoche51.buyerapp.entity.HCBangMaiEntity;
import com.haoche51.buyerapp.entity.HCVehicleItemEntity;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCFormatUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import com.haoche51.custom.HCViewClickListener;
import java.util.List;
import java.util.Map;

/**
 * 全部好车
 */
public class AllGoodVehicleAdapter extends HCCommonAdapter<HCVehicleItemEntity> {

  public long timeCurrent = System.currentTimeMillis() / 1000;

  private boolean isNeedShowGuessView = false;

  public void setNeedShowGuessView(boolean needShowGuessView) {
    isNeedShowGuessView = needShowGuessView;
  }

  public AllGoodVehicleAdapter(Context context, List<HCVehicleItemEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    HCVehicleItemEntity entity = getItem(position);

    View normalItemView = holder.findTheView(R.id.rel_singlepic_parent);
    ImageView mAderIv = holder.findTheView(R.id.iv_allgood_ad);
    View nearcityView = holder.findTheView(R.id.view_nearcity);
    View bangmaiView = holder.findTheView(R.id.view_bangmai);
    View divider = holder.findTheView(R.id.view_allgood_divider);
    View adDivider = holder.findTheView(R.id.view_allgood_ad_divider);

    //判断是广告还是正常的item
    boolean isAdver = entity.getPromote() == 1;
    boolean isBangmai = entity.getBang_count() > 0;
    boolean isNearcityDesc = !TextUtils.isEmpty(entity.getNear_city());

    if (isAdver) {
      //推广广告
      mAderIv.setVisibility(View.GONE);
      adDivider.setVisibility(View.GONE);
      divider.setVisibility(View.VISIBLE);
      normalItemView.setVisibility(View.GONE);
      bangmaiView.setVisibility(View.GONE);
      nearcityView.setVisibility(View.GONE);
      setupAdverView(entity, holder);
    } else if (isBangmai) {
      //帮买
      bangmaiView.setVisibility(View.VISIBLE);
      mAderIv.setVisibility(View.GONE);
      adDivider.setVisibility(View.GONE);
      divider.setVisibility(View.GONE);
      normalItemView.setVisibility(View.GONE);
      nearcityView.setVisibility(View.GONE);
      setupBangmaiView(entity, bangmaiView);
    } else if (isNearcityDesc) {
      //附近车源说明
      bangmaiView.setVisibility(View.GONE);
      mAderIv.setVisibility(View.GONE);
      adDivider.setVisibility(View.GONE);
      divider.setVisibility(View.GONE);
      normalItemView.setVisibility(View.GONE);
      nearcityView.setVisibility(View.VISIBLE);
      setupNearcityDesc(entity, nearcityView);
    } else {
      //此时为正常item,判断是否为补位车源.

      normalItemView.setVisibility(View.VISIBLE);
      divider.setVisibility(View.VISIBLE);
      mAderIv.setVisibility(View.GONE);
      adDivider.setVisibility(View.GONE);
      bangmaiView.setVisibility(View.GONE);
      nearcityView.setVisibility(View.GONE);

      setUpNormalItem(entity, holder);
    }
  }

  /**
   * 帮买视图数据填充
   */
  private void setupBangmaiView(HCVehicleItemEntity entity, View view) {
    if (view != null) {
      TextView titleTv = (TextView) view.findViewById(R.id.tv_bangmai_title);
      TextView countTv = (TextView) view.findViewById(R.id.tv_bangmai_count);
      TextView helpBuyTv = (TextView) view.findViewById(R.id.tv_helpme_buy);
      View guessView = view.findViewById(R.id.view_allgood_guess);
      if (isNeedShowGuessView) {
        guessView.setVisibility(View.VISIBLE);
      } else {
        HCUtils.hideViewIfNeed(guessView);
      }
      final EditText conditionEt = (EditText) view.findViewById(R.id.et_bangmai_condition);
      final EditText inputEt = (EditText) view.findViewById(R.id.et_bangmai_phone);

      titleTv.getPaint().setFakeBoldText(true);

      int count = entity.getBang_count();
      if (count > 0) {
        countTv.setText(HCUtils.getResString(R.string.hc_bangmai_count, count));
      }

      final String str = HCFormatUtil.formatFilterTerm(FilterUtils.getNormalFilterterm());
      conditionEt.setText(str);
      if (HCUtils.isUserLogined()) {
        inputEt.setText(HCSpUtils.getUserPhone());
      }

      helpBuyTv.setOnClickListener(new HCViewClickListener() {
        @Override public void performViewClick(View v) {
          String input = inputEt.getText().toString().trim();
          if (TextUtils.isEmpty(input) || input.length() != 11) {
            HCUtils.showToast(R.string.hc_show_phone_error);
          } else {
            requestBangMai(input, str);
          }
        }
      });

      view.setOnClickListener(null);
    }
  }

  /**
   * 提交帮买信息
   */
  private void requestBangMai(String phone, String word) {

    if (!HCUtils.isNetAvailable()) {
      HCUtils.showToast(R.string.hc_net_unreachable);
      return;
    }

    HCStatistic.bangMaiClick();
    Map<String, Object> params = HCParamsUtil.bangMai(phone, word, FilterUtils.TYPE_NORMAL);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleBangMai(responseJsonString);
      }
    }));
  }

  /**
   * 帮买结果解析
   */
  private void handleBangMai(String responseJsonString) {
    if (TextUtils.isEmpty(responseJsonString)) return;

    HCBangMaiEntity bangMaiEntity = HCJSONParser.parseBangMai(responseJsonString);
    if (bangMaiEntity != null) {
      int errno = bangMaiEntity.getErrno();
      if (errno >= 0) {
        HCUtils.showBangMaiToast();
      } else {
        HCUtils.showToast(R.string.hc_bangmai_defeat);
      }
    }
  }

  /**
   * 附近城市视图数据填充
   */
  private void setupNearcityDesc(HCVehicleItemEntity entity, View view) {
    if (view != null) {
      String city = entity.getNear_city();
      city = HCUtils.getResString(R.string.hc_nearcity_match, city);
      TextView tv = (TextView) view.findViewById(R.id.tv_nearcity_match);
      tv.setText(city);
      tv.getPaint().setFakeBoldText(true);
      if (!TextUtils.isEmpty(city)) {
        int start = city.indexOf("[");
        int end = city.indexOf("]");
        if (start > -1 && end > -1) {
          int color = HCUtils.getResColor(R.color.reminder_red);
          HCViewUtils.changeTextViewColor(tv, color, start + 1, end);
        }
      }

      view.setOnClickListener(null);
    }
  }

  /**
   * 广告位数据展示
   */
  private void setupAdverView(final HCVehicleItemEntity entity, HCCommonViewHolder holder) {
    ImageView mAdverIv = holder.findTheView(R.id.iv_allgood_ad);
    ImageLoaderHelper.loadAllGoodAdverPic(mAdverIv, entity);
  }

  /**
   * 正常车源展示
   */
  private void setUpNormalItem(final HCVehicleItemEntity entity, HCCommonViewHolder holder) {
    if (entity != null) {
      //设置名字
      String vehicleName = HCFormatUtil.getVehicleName(entity.getVehicle_name());
      TextView mNameTv = holder.findTheView(R.id.tv_singlepic_vehicle_name);

      boolean isNearCity = false;
      boolean isDirect = HCUtils.str2Int(entity.getZhiyingdian()) == 1;
      String city = entity.getCity_name();
      if (!TextUtils.isEmpty(city)) {
        isNearCity = !city.equals(HCDbUtil.getSavedCityName());
      }

      if (isDirect && isNearCity) {
        setDirectAndNearCityName(mNameTv, city, vehicleName);
      } else if (isDirect) {
        setDirectName(mNameTv, vehicleName);
      } else if (isNearCity) {
        setNearCityName(mNameTv, city, vehicleName);
      } else {
        mNameTv.setText(vehicleName);
      }
      RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mNameTv.getLayoutParams();
      if (isDirect) {
        params.topMargin = HCUtils.getDimenPixels(R.dimen.px_m7);
      } else {
        params.topMargin = HCUtils.getDimenPixels(R.dimen.px_m05);
      }

      // 设置价格
      float price = HCUtils.str2Float(entity.getSeller_price());
      Spanned spannedPrice = Html.fromHtml(HCFormatUtil.getSoldPriceFormat(price));
      holder.setTextViewText(R.id.tv_singlepic_vehicle_price, spannedPrice);

      //设置首付
      if (TextUtils.isEmpty(entity.getShoufu_price())) {
        holder.findTheView(R.id.tv_singlepic_vehicle_price_pay).setVisibility(View.GONE);
      } else {
        holder.findTheView(R.id.tv_singlepic_vehicle_price_pay).setVisibility(View.VISIBLE);
        float pricePay = HCUtils.str2Float(entity.getShoufu_price());
        Spanned spannedPayPrice = Html.fromHtml(HCFormatUtil.getPayPriceFormat(pricePay));
        holder.setTextViewText(R.id.tv_singlepic_vehicle_price_pay, spannedPayPrice);
      }

      //设置质保
      boolean isWarranty = HCUtils.str2Int(entity.getZhibao()) == 1;
      int warrantyStatus = isWarranty ? View.VISIBLE : View.GONE;
      holder.findTheView(R.id.tv_singlepic_vehicle_warranty).setVisibility(warrantyStatus);

      //设置detail
      String time = entity.getRegister_time();
      String mile = entity.getMiles();
      String gearbox = entity.getGeerbox_type();

      String detail = HCFormatUtil.getVehicleFormat(time, mile, gearbox);
      holder.setTextViewText(R.id.tv_singlepic_vehicle_detail, detail);

      //设置图片
      String image_url = entity.getCover_pic();
      if (!TextUtils.isEmpty(image_url)) {
        holder.loadHttpImage(R.id.iv_singlepic_vehicle_image, image_url);
      }

      //设置超值
      boolean isCheap = HCUtils.str2Int(entity.getSuitable()) == 1;
      int cheapStatus = isCheap ? View.VISIBLE : View.GONE;
      holder.findTheView(R.id.iv_singlepic_vehicle_cheap_image).setVisibility(cheapStatus);

      //设置预售
      boolean isPreSell = HCUtils.str2Int(entity.getYushou()) == 1;
      int preSellStatus = isPreSell ? View.VISIBLE : View.GONE;
      holder.findTheView(R.id.iv_singlepic_vehicle_presell_image).setVisibility(preSellStatus);

      String leftTopUrl = entity.getLeft_top();
      String leftTopRate = entity.getLeft_top_rate();
      ImageView leftTopIv = holder.findTheView(R.id.iv_singlepic_vehicle_left_top_image);
      if (!TextUtils.isEmpty(leftTopUrl) && !TextUtils.isEmpty(leftTopRate)) {
        leftTopIv.setVisibility(View.VISIBLE);
        int leftTopWh = HCUtils.dp2px(120F / HCUtils.str2Float(leftTopRate));
        leftTopIv.getLayoutParams().width = leftTopWh;
        leftTopIv.getLayoutParams().height = leftTopWh;
        holder.loadHttpImage(leftTopIv, leftTopUrl);
      } else {
        HCUtils.hideViewIfNeed(leftTopIv);
      }

      String leftBottomUrl = entity.getLeft_bottom();
      String leftBottomRate = entity.getLeft_bottom_rate();
      ImageView leftBottomIv = holder.findTheView(R.id.iv_singlepic_vehicle_left_bottom_image);
      if (!TextUtils.isEmpty(leftBottomUrl) && !TextUtils.isEmpty(leftBottomRate)) {
        leftBottomIv.setVisibility(View.VISIBLE);
        int leftBottomWh = HCUtils.dp2px(120F / HCUtils.str2Float(leftBottomRate));
        leftBottomIv.getLayoutParams().width = leftBottomWh;
        leftBottomIv.getLayoutParams().height = leftBottomWh;
        holder.loadHttpImage(leftBottomIv, leftBottomUrl);
      } else {
        HCUtils.hideViewIfNeed(leftBottomIv);
      }

      //设置是否已经售出标签
      View soldIv = holder.findTheView(R.id.iv_singlepic_vehicle_sold);
      int status = HCUtils.str2Int(entity.getStatus());
      boolean isSold = HCUtils.isVehicleSold(status);
      int visibleStatus = isSold ? View.VISIBLE : View.GONE;
      soldIv.setVisibility(visibleStatus);

      //设置活动
      boolean isOther = !TextUtils.isEmpty(entity.getAct_pic());
      boolean isQiang = entity.getQianggou() != 0;
      int vis = isOther || isQiang ? View.VISIBLE : View.GONE;
      RelativeLayout activityLayout = holder.findTheView(R.id.layout_singlepic_vehicle_activity);
      activityLayout.setVisibility(vis);
      if (isOther || isQiang) {
        LinearLayout limitLayout = holder.findTheView(R.id.linear_singlepic_activity_layout);
        ImageView activityIv = holder.findTheView(R.id.iv_singlepic_vehicle_activity_icon);
        if (isOther) {
          holder.loadHttpImage(activityIv, entity.getAct_pic());
        } else {
          activityIv.setBackgroundResource(R.drawable.icon_activity_limit);
        }

        String text = isQiang ? "距结束" : entity.getAct_txt();
        holder.setTextViewText(R.id.tv_singlepic_vehicle_activity_common, text);
        limitLayout.setVisibility(View.GONE);
        if (isQiang) {
          limitLayout.setVisibility(View.VISIBLE);
          TextView timeOneTv = holder.findTheView(R.id.tv_singlepic_vehicle_activity_time_one);
          TextView timeOneHintTv =
              holder.findTheView(R.id.tv_singlepic_vehicle_activity_time_one_hint);
          TextView timeTwoTv = holder.findTheView(R.id.tv_singlepic_vehicle_activity_time_two);
          TextView timeTwoHintTv =
              holder.findTheView(R.id.tv_singlepic_vehicle_activity_time_two_hint);
          long timeNow = entity.getQianggou() - timeCurrent;
          if (timeNow < HCFormatUtil.MAX10) {
            updateTextView(timeNow, timeOneTv, timeOneHintTv, timeTwoTv, timeTwoHintTv,
                activityLayout);
          } else {
            //倒计时时间超过指定时间就不显示
            activityLayout.setVisibility(View.GONE);
          }
        }
      }

      final boolean finalIsNearCity = isNearCity;
      holder.getConvertView().setOnClickListener(new HCViewClickListener() {
        @Override public void performViewClick(View v) {
          String id = entity.getId();
          VehicleDetailActivity.idToThis(mContext, id, "全部");
          if (finalIsNearCity) {
            //统计附近车源的点击量
            HCStatistic.nearCityClick();
          }
        }
      });
    }
  }

  private void updateTextView(long time, TextView one, TextView oneHint, TextView two,
      TextView twoHint, View v) {
    if (time > 86400) {
      //天为最高单位
      oneHint.setText("天");
      twoHint.setText("时");
      long day = time / 86400;
      long hour = (time % 86400) / 3600;
      String dayStr;
      if (day < 10) {
        dayStr = "0" + day;
      } else {
        dayStr = "" + day;
      }
      one.setText(dayStr);
      String hourStr;
      if (hour < 10) {
        hourStr = "0" + hour;
      } else {
        hourStr = "" + hour;
      }
      two.setText(hourStr);
    } else if (time > 3600) {
      //小时为最高单位
      oneHint.setText("时");
      twoHint.setText("分");
      long hour = time / 3600;
      long minute = (time % 3600) / 60;
      String hourStr;
      if (hour < 10) {
        hourStr = "0" + hour;
      } else {
        hourStr = "" + hour;
      }
      one.setText(hourStr);
      String minuteStr;
      if (minute < 10) {
        minuteStr = "0" + minute;
      } else {
        minuteStr = "" + minute;
      }
      two.setText(minuteStr);
    } else if (time > 0) {
      //分钟为最高单位
      oneHint.setText("分");
      twoHint.setText("秒");
      long minute = time / 60;
      long second = time % 60;
      String minuteStr;
      if (minute < 10) {
        minuteStr = "0" + minute;
      } else {
        minuteStr = "" + minute;
      }
      one.setText(minuteStr);
      String secondStr;
      if (second < 10) {
        secondStr = "0" + second;
      } else {
        secondStr = "" + second;
      }
      two.setText(secondStr);
    } else {
      //隐藏
      v.setVisibility(View.GONE);
    }
  }

  /** 设置车源名字 */
  private void setDirectAndNearCityName(TextView tv, String city, String vehicleName) {
    String name = "<img src='" + R.drawable.icon_direct + "'/>" + " [" +
        "<font color='#ff2626'>" + city + "</font>" + "]" + vehicleName;
    try {
      tv.setText(Html.fromHtml(name, HCFormatUtil.getImageGetterInstance(), null));
    } catch (Exception e) {
      setNearCityName(tv, city, vehicleName);
    }
  }

  private void setDirectName(TextView tv, String vehicleName) {
    String name = "<img src='" + R.drawable.icon_direct + "'/> " + vehicleName;
    try {
      tv.setText(Html.fromHtml(name, HCFormatUtil.getImageGetterInstance(), null));
    } catch (Exception e) {
      tv.setText(vehicleName);
    }
  }

  private void setNearCityName(TextView tv, String city, String vehicleName) {
    String name = HCUtils.getResString(R.string.hc_nearcity_name, city, vehicleName);
    tv.setText(name);
    int start = name.indexOf("[");
    int end = name.indexOf("]");
    if (start > -1 && end > -1) {
      int color = HCUtils.getResColor(R.color.reminder_red);
      HCViewUtils.changeTextViewColor(tv, color, start + 1, end);
    }
  }
}
