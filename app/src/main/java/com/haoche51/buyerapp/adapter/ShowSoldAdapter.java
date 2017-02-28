package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.List;

public class ShowSoldAdapter extends HCCommonAdapter<String> {

  public ShowSoldAdapter(Context context, List<String> data, int layoutid) {
    super(context, data, layoutid);
  }

  private int mLastPos = 0;

  public void setLastPos(int pos) {
    this.mLastPos = pos;
    this.notifyDataSetChanged();
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    ImageView iv = holder.findTheView(R.id.iv_ss);
    TextView tv = holder.findTheView(R.id.tv_ss);
    if (position == mLastPos) {
      iv.setVisibility(View.VISIBLE);
      tv.setTextColor(HCUtils.getResColor(R.color.reminder_red));
    } else {
      iv.setVisibility(View.GONE);
      tv.setTextColor(HCUtils.getResColor(R.color.font_black));
    }

    String name = getItem(position);
    name = TextUtils.isEmpty(name) ? HCUtils.getResString(R.string.hc_zanweishangxian) : name;
    holder.setTextViewText(R.id.tv_ss, name);
  }
}
