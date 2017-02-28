package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;
import com.haoche51.buyerapp.R;
import java.util.List;

public class AutoCompleteAdapter extends HCCommonAdapter<String> implements Filterable {

  public AutoCompleteAdapter(Context context, List data, int layoutid) {
    super(context, data, layoutid);
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    holder.setTextViewText(R.id.tv_search_history_item, getItem(position));
  }

  @Override public Filter getFilter() {
    return new AutoFilter(this);
  }
}
