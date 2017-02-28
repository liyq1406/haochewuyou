package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.ManageMySubAdapter;
import com.haoche51.buyerapp.entity.SubConditionDataEntity;
import com.haoche51.buyerapp.util.FragmentController;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.custom.swipe.SwipeLayout;
import java.util.List;

public class SubscribeConditionDialog {
  private Activity activity;
  private SubConditionDataEntity entity;

  public SubscribeConditionDialog(Activity activity, SubConditionDataEntity entity) {
    this.activity = activity;
    this.entity = entity;
  }

  public void showSubscribeConditionDialog() {
    List<SubConditionDataEntity> mSubDatas = HCSpUtils.getAllSubscribe();

    int subSize = mSubDatas.size();
    if (subSize == 0) return;

    final Activity[] mAct = new Activity[1];
    mAct[0] = activity;

    int layoutId = R.layout.dialog_subscribe_condition;
    View contentView = LayoutInflater.from(mAct[0]).inflate(layoutId, null);

    final Dialog dialog = new Dialog(mAct[0], R.style.sub_condition_dialog);
    dialog.setContentView(contentView);

    ImageView rightIcon = (ImageView) contentView.findViewById(R.id.iv_subcounts_icon);
    if (entity == null) {
      rightIcon.setImageResource(R.drawable.icon_sub_choosed);
    } else {
      rightIcon.setImageResource(R.color.hc_transcolor);
    }

    ImageView leftIcon = (ImageView) contentView.findViewById(R.id.iv_subcounts_brand);
    Drawable drawable = FragmentController.getCountIcon(subSize);
    leftIcon.setImageDrawable(drawable);

    TextView titleTv = (TextView) contentView.findViewById(R.id.tv_subcounts_counts);
    String countsStr = mAct[0].getString(R.string.hc_sub_counts, subSize);
    titleTv.setText(countsStr);
    titleTv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        HCEvent.postEvent(HCEvent.ACTION_MY_SUBITEM_CLICK, null);
        dialog.dismiss();
      }
    });

    TextView modifyTv = (TextView) contentView.findViewById(R.id.tv_dsc_modify);
    modifyTv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        HCEvent.postEvent(HCEvent.ACTION_GOTO_MANAGER_CLICK);
        dialog.dismiss();
      }
    });

    ListView mListView = (ListView) contentView.findViewById(R.id.lv_dsc);
    int itemResId = R.layout.lvitem_my_subscribe_no_swipe;
    ManageMySubAdapter mAdapter = new ManageMySubAdapter(mAct[0], mSubDatas, itemResId);
    mAdapter.isSwipeLayout(false);
    mAdapter.setLastSubscribeEntity(entity);
    mListView.setAdapter(mAdapter);

    mAdapter.setItemCallBack(new ManageMySubAdapter.SubCallBack() {
      @Override public void onItemClickcallBack(SubConditionDataEntity obj) {
        HCEvent.postEvent(HCEvent.ACTION_MY_SUBITEM_CLICK, obj);
        dialog.dismiss();
      }

      @Override public void onItemDelete(SubConditionDataEntity entity, SwipeLayout swipelayout) {

      }
    });

    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.gravity = Gravity.BOTTOM;

    dialog.show();

    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override public void onDismiss(DialogInterface dialog) {
        mAct[0] = null;
      }
    });
  }
}
