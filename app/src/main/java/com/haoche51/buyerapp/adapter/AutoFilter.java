package com.haoche51.buyerapp.adapter;

import android.widget.Filter;
import java.util.ArrayList;
import java.util.List;

public class AutoFilter extends Filter {
  public AutoCompleteAdapter mAdapter;

  public AutoFilter(AutoCompleteAdapter adapter) {
    this.mAdapter = adapter;
  }

  @Override protected FilterResults performFiltering(CharSequence constraint) {
    FilterResults results = new FilterResults();
    List<String> data = mAdapter.getBaseData();
    List<String> fd = new ArrayList<>();
    if (data != null) {
      if (constraint != null) {
        String s = constraint.toString();
        for (String inner : data) {
          if (inner.contains(s)) {
            fd.add(inner);
          }
        }
      }
    }

    results.values = fd;
    results.count = fd.size();
    return results;
  }

  @Override protected void publishResults(CharSequence constraint, FilterResults results) {
    if (results != null) {
      if (results.count > 0) {
        List<String> values = (List<String>) results.values;
        mAdapter.getBaseData().clear();
        mAdapter.getBaseData().addAll(values);
        mAdapter.notifyDataSetChanged();
      }
    }
  }
}
