package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.view.View;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.VehicleDetailActivity;
import com.haoche51.buyerapp.entity.HCVehicleItemEntity;
import com.haoche51.custom.swipe.SimpleSwipeListener;
import com.haoche51.custom.swipe.SwipeLayout;
import java.util.List;

/**
 * 收藏列表  适配器
 */
public class CollectionAdapter extends SinglePicVehicleAdapter implements View.OnClickListener {

  private int mLastOpenedPos = -1;
  private SwipeLayout mLastSwipeLayout;

  public CollectionAdapter(Context context, List<HCVehicleItemEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  public OnDeleteListener mOnDeleteListener;

  public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
    this.mOnDeleteListener = onDeleteListener;
  }

  @Override public void fillOtherData(HCCommonViewHolder holder, final int position) {
    final HCVehicleItemEntity entity = getItem(position);
    if (entity != null) {

      //设置删除
      View deleteView = holder.findTheView(R.id.tv_collection_delete);
      deleteView.setTag(R.id.tv_for_delete, entity);
      deleteView.setTag(holder.findTheView(R.id.swipeLayout_collection));
      deleteView.setOnClickListener(this);

      View innerView = holder.findTheView(R.id.rel_singlepic_parent);
      if (innerView != null) {
        innerView.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            String curId = entity.getId();
            VehicleDetailActivity.idToThis(GlobalData.mContext, curId, "收藏");
            //统计收藏进入详情
            //HCStatistic.pageStatistic(CoreFragment.PAGE_MY_COLLECTION, true);
          }
        });
      }

      final SwipeLayout swipe = holder.findTheView(R.id.swipeLayout_collection);
      swipe.addSwipeListener(new SimpleSwipeListener() {
        @Override public void onStartOpen(SwipeLayout layout) {
          super.onStartOpen(layout);

          if (mLastOpenedPos > -1 && mLastOpenedPos != position) {
            //关闭上一个打开的
            if (mLastSwipeLayout != null) {
              mLastSwipeLayout.close(true);
            }
          }
          mLastOpenedPos = position;
          mLastSwipeLayout = swipe;
        }
      });
    }
  }

  @Override public void onClick(View v) {
    if (mOnDeleteListener != null) {
      Object obj = v.getTag(R.id.tv_for_delete);
      Object host = v.getTag();
      if (obj != null && obj instanceof HCVehicleItemEntity) {
        if (host != null && host instanceof SwipeLayout) {
          SwipeLayout sl = (SwipeLayout) host;
          HCVehicleItemEntity entity = (HCVehicleItemEntity) obj;
          mOnDeleteListener.onDelete(entity, sl);
        }
      }
    }
  }

  public interface OnDeleteListener {
    void onDelete(HCVehicleItemEntity entity, SwipeLayout swipelayout);
  }
}
