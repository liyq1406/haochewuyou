package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.SubConditionDataEntity;
import com.haoche51.buyerapp.util.HCFormatUtil;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import com.haoche51.custom.swipe.SimpleSwipeListener;
import com.haoche51.custom.swipe.SwipeLayout;
import java.util.List;

/**
 * 订阅管理  adapter
 */
public class ManageMySubAdapter extends HCCommonAdapter<SubConditionDataEntity> {

  private int mLastOpenedPos = -1;
  private SwipeLayout mLastSwipeLayout;

  public ManageMySubAdapter(Context context, List<SubConditionDataEntity> data, int layoutid) {
    super(context, data, layoutid);
  }

  private View.OnClickListener mClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      SubConditionDataEntity entity = (SubConditionDataEntity) v.getTag(R.id.tv_for_delete);
      if (v.getId() == R.id.tv_for_delete) {
        //回调用于改变头部订阅个数
        Object host = v.getTag();
        if (host != null && host instanceof SwipeLayout) {
          SwipeLayout sl = (SwipeLayout) host;
          mCallBack.onItemDelete(entity, sl);
        }
      } else {
        if (mCallBack != null) {
          mCallBack.onItemClickcallBack(entity);
        }
      }
    }
  };

  @Override public void fillViewData(HCCommonViewHolder holder, final int position) {
    final SubConditionDataEntity entity = getItem(position);
    //品牌
    int brand_id = HCUtils.str2Int(entity.getBrand_id());
    int series_id = HCUtils.str2Int(entity.getClass_id());
    String forShow = HCFormatUtil.getShowName(brand_id, series_id);
    holder.setTextViewText(R.id.tv_sub_vehicle_brand, forShow);

    ImageView ivBrand = holder.findTheView(R.id.iv_sub_brand);
    HCViewUtils.setIconById(ivBrand, brand_id, R.drawable.icon_brand_unlimit);

    String otherDetailStr = HCFormatUtil.formatSubDetail(entity);
    TextView forDetailTv = holder.findTheView(R.id.tv_sub_vehicle_detail);
    forDetailTv.setText(otherDetailStr);
    forDetailTv.setSingleLine(!isSwipeLayout);
    forDetailTv.setEllipsize(TextUtils.TruncateAt.END);
    ViewGroup.MarginLayoutParams marginLp =
        (ViewGroup.MarginLayoutParams) forDetailTv.getLayoutParams();

    View mConvertView = holder.getConvertView();
    if (!isSwipeLayout) {
      //不可滑动
      holder.findTheView(R.id.iv_sub_arrow).setVisibility(View.GONE);
      //显示右边的选中按钮
      int viewStatus = (entity.equals(mLastSubscribeEntity)) ? View.VISIBLE : View.GONE;
      holder.findTheView(R.id.cb_sub_chekck).setVisibility(viewStatus);
      mConvertView.setTag(R.id.tv_for_delete, entity);
      mConvertView.setTag(R.id.view_tag_click_condition, entity);
      mConvertView.setOnClickListener(mClickListener);
    } else {
      marginLp.topMargin = HCUtils.getDimenPixels(R.dimen.px_05);
      holder.findTheView(R.id.cb_sub_chekck).setVisibility(View.GONE);
      holder.findTheView(R.id.iv_sub_arrow).setVisibility(View.GONE);
      mConvertView.setOnClickListener(null);
      View deleteView = holder.findTheView(R.id.tv_for_delete);
      deleteView.setTag(R.id.tv_for_delete, entity);
      deleteView.setTag(holder.findTheView(R.id.swipeLayout_parent));
      deleteView.setOnClickListener(mClickListener);

      final SwipeLayout swipe = holder.findTheView(R.id.swipeLayout_parent);
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

  private boolean isSwipeLayout = false;

  public void isSwipeLayout(boolean isSwipeLayout) {
    this.isSwipeLayout = isSwipeLayout;
  }

  private SubConditionDataEntity mLastSubscribeEntity;

  public void setLastSubscribeEntity(SubConditionDataEntity entity) {
    this.mLastSubscribeEntity = entity;
    notifyDataSetChanged();
  }

  private SubCallBack mCallBack;

  public interface SubCallBack {
    void onItemClickcallBack(SubConditionDataEntity entity);

    void onItemDelete(SubConditionDataEntity entity, SwipeLayout swipelayout);
  }

  public void setItemCallBack(SubCallBack callBack) {
    this.mCallBack = callBack;
  }
}
