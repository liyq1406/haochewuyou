package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import java.util.List;

/**
 * 历史搜索记录,搜索建议adapter
 */
public class HCSimpleItemAdapter extends HCCommonAdapter<String> {
  public HCSimpleItemAdapter(Context context, List<String> data, int layoutid) {
    super(context, data, layoutid);
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    String data = getItem(position);
    holder.setTextViewText(R.id.tv_search_history_item, data);
    if (mPaddingLeft != -1) {
      TextView mTv = holder.findTheView(R.id.tv_search_history_item);
      mTv.setPadding(mPaddingLeft, 0, 0, 0);
    }
  }

  private int mPaddingLeft = -1;

  public void setPaddingLeft(int paddingLeft) {
    this.mPaddingLeft = paddingLeft;
  }
}
