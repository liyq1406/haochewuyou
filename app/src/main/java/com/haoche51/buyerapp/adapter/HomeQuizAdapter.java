package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import java.util.List;

public class HomeQuizAdapter extends HCCommonAdapter<String> {

  public HomeQuizAdapter(Context context, List<String> data, int layoutid) {
    super(context, data, layoutid);
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    String string = getItem(position);
    TextView convertView = holder.findTheView(R.id.tv_quiz_item);
    convertView.setText(string);
  }
}
