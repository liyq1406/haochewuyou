package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.KeyValueEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.List;

public class PriceAdapter extends HCCommonAdapter<KeyValueEntity> {

  private View.OnClickListener mClickListener;

  private String host;

  private int mRedColor = HCUtils.getResColor(R.color.home_grx_red);
  private int mBlackColor = HCUtils.getResColor(R.color.home_hot_text);

  public PriceAdapter(Context context, List<KeyValueEntity> data, int layoutid, String host,
      View.OnClickListener listener) {
    super(context, data, layoutid);
    this.host = host;
    this.mClickListener = listener;
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    FilterTerm term = FilterUtils.getFilterTerm(host);
    KeyValueEntity entity = getItem(position);
    TextView nameTv = holder.findTheView(R.id.tv_price_item);
    ImageView nameIv = holder.findTheView(R.id.iv_price_item);
    nameTv.setText(entity.getKey());
    nameTv.setTag(entity);
    nameTv.setOnClickListener(mClickListener);
    String priceKey = FilterUtils.getFilterTermString(term, HCConsts.FILTER_PRICE);
    setConverViewColor(nameTv, nameIv, priceKey, entity.getKey());
    FilterUtils.setFilterTerm(host, term);
  }

  private void setConverViewColor(TextView convertView, ImageView imageView, String chooseKey,
      String key) {
    if (chooseKey.equals(key)) {
      convertView.setTextColor(mRedColor);
      imageView.setVisibility(View.VISIBLE);
    } else {
      convertView.setTextColor(mBlackColor);
      imageView.setVisibility(View.GONE);
    }
  }
}
