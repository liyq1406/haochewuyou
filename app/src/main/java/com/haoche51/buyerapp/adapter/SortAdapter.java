package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.KeyValueEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.List;

public class SortAdapter extends HCCommonAdapter<KeyValueEntity> {

  private String host;

  public SortAdapter(Context context, List<KeyValueEntity> data, int layoutid, String host) {
    super(context, data, layoutid);
    this.host = host;
  }

  @Override public void fillViewData(HCCommonViewHolder holder, final int position) {
    KeyValueEntity entity = getItem(position);
    final String value = entity.getKey();
    boolean needRedColor = value.equals(FilterUtils.getFilterTerm(host).getDescriptionSort());
    int color = needRedColor ? R.color.home_grx_red : R.color.home_hot_text;
    int visible = needRedColor ? View.VISIBLE : View.GONE;
    holder.findTheView(R.id.iv_sort_item).setVisibility(visible);
    TextView mTextView = holder.findTheView(R.id.tv_sort_item);
    mTextView.setTextColor(HCUtils.getResColor(color));
    mTextView.setText(entity.getKey());
    mTextView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        HCStatistic.sortDetailClick(value);
        FilterUtils.saveSort(host, value);
        notifyDataSetChanged();
        HCEvent.postEvent(host + HCEvent.ACTION_SORT_CHOOSED);
      }
    });
  }
}
