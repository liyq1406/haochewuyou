package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.WebBrowserActivity;
import com.haoche51.buyerapp.entity.HomeForumEntity;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCStatistic;
import java.util.List;

public class HomeForumAdapter extends HCCommonAdapter<HomeForumEntity> {

  private final static String HTTP = "http";

  public HomeForumAdapter(Context context, List<HomeForumEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  @Override public void fillViewData(HCCommonViewHolder holder, int position) {
    final HomeForumEntity entity = getItem(position);

    if (entity != null) {
      String image_url = entity.getPic_url();
      holder.setTextViewText(R.id.tv_head_line_title, entity.getTitle());
      holder.setTextViewText(R.id.tv_head_line_time, entity.getUpdated_at());
      if (!TextUtils.isEmpty(image_url)) {
        holder.loadHttpImage(R.id.iv_head_line, image_url);
      }
      holder.findTheView(R.id.rel_forum_parent).setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          String url = entity.getLink_url();
          if (!TextUtils.isEmpty(url)) {
            url = url.trim();
            if (url.startsWith(HTTP)) {
              Intent mIntent = new Intent(GlobalData.mContext, WebBrowserActivity.class);
              mIntent.putExtra(HCConsts.INTENT_KEY_URL, url);
              mIntent.putExtra(HCConsts.INTENT_KEY_FORUM, url);
              mContext.startActivity(mIntent);
              HCSensorsUtil.homePageClick(entity.getTitle());
            }
          }
          HCStatistic.homeForumClick();
        }
      });

      if (position == getCount() - 1) {
        holder.findTheView(R.id.home_forum_bottom1).setVisibility(View.VISIBLE);
        holder.findTheView(R.id.home_forum_bottom2).setVisibility(View.VISIBLE);
      }
    }
  }
}
