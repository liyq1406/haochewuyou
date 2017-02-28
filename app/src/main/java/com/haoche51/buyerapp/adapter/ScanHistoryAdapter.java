package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.ScanHistoryEntity;
import com.haoche51.buyerapp.util.HCFormatUtil;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.List;

/**
 * 浏览记录适配器
 */
public class ScanHistoryAdapter extends HCCommonAdapter<ScanHistoryEntity> {

  public ScanHistoryAdapter(Context context, List<ScanHistoryEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    ScanHistoryEntity entity = getItem(position);

    //已售出图标
    ImageView iconSoldIv = holder.findTheView(R.id.iv_history_car_sold);
    int status = HCUtils.str2Int(entity.getStatus());
    boolean isSold = HCUtils.isVehicleSold(status);
    int visibleStatus = isSold ? View.VISIBLE : View.GONE;
    iconSoldIv.setVisibility(visibleStatus);

    // 设置价格
    Spanned price =
        Html.fromHtml(HCFormatUtil.getSoldPriceFormat(HCUtils.str2Float(entity.getSeller_price())));
    holder.setTextViewText(R.id.tv_history_car_price, price);

    String time = entity.getRegister_time();
    String miles = entity.getMiles();
    String gearbox = entity.getGeerbox_type();
    String detail = HCFormatUtil.getVehicleFormat(time, miles, gearbox);
    holder.setTextViewText(R.id.tv_history_car_detail, detail);

    String name = HCFormatUtil.getVehicleName(entity.getVehicle_name());
    holder.setTextViewText(R.id.tv_history_car_name, name);

    String url = entity.getCover_pic();
    holder.loadHttpImage(R.id.iv_history_car_image, url);

    //设置图片水印
    String leftTopUrl = entity.getLeft_top();
    String leftTopRate = entity.getLeft_top_rate();
    ImageView leftTopIv = holder.findTheView(R.id.iv_history_car_left_top_image);
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
    ImageView leftBottomIv = holder.findTheView(R.id.iv_history_car_left_bottom_image);
    if (!TextUtils.isEmpty(leftBottomUrl) && !TextUtils.isEmpty(leftBottomRate)) {
      leftBottomIv.setVisibility(View.VISIBLE);
      int leftBottomWh = HCUtils.dp2px(120F / HCUtils.str2Float(leftBottomRate));
      leftBottomIv.getLayoutParams().width = leftBottomWh;
      leftBottomIv.getLayoutParams().height = leftBottomWh;
      holder.loadHttpImage(leftBottomIv, leftBottomUrl);
    } else {
      HCUtils.hideViewIfNeed(leftBottomIv);
    }
  }
}
