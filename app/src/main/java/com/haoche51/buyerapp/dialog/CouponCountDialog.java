package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.CouponListActivity;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;

public class CouponCountDialog {
  private Activity activity;
  private int counts;

  public CouponCountDialog(Activity activity, int counts) {
    this.activity = activity;
    this.counts = counts;
  }

  public void showCouponCountDialog() {
    final Activity[] mAct = new Activity[1];
    mAct[0] = activity;
    final Dialog dialog = new Dialog(mAct[0], R.style.normal_dialog);
    View root = View.inflate(mAct[0], R.layout.dialog_show_coupon, null);
    TextView countsTv = (TextView) root.findViewById(R.id.tv_coupon_count);
    String str = mAct[0].getString(R.string.hc_coupon_counts, counts);
    countsTv.setText(str);
    int index = str.indexOf(String.valueOf(counts));
    HCViewUtils.changeTextViewColor(countsTv, HCUtils.getResColor(R.color.reminder_red), index,
        index + String.valueOf(counts).length());

    root.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
      }
    });

    root.findViewById(R.id.btn_look).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        HCSpUtils.setProfileCouponReminder(0);
        Intent intent = new Intent(GlobalData.mContext, CouponListActivity.class);
        mAct[0].startActivity(intent);
        dialog.dismiss();
      }
    });

    dialog.setContentView(root);
    Window window = dialog.getWindow();
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.width = (int) (HCUtils.getScreenWidthInPixels() * 4F / 5);
    dialog.show();

    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override public void onDismiss(DialogInterface dialog) {
        HCSpUtils.setProfileCouponReminder(0);
        mAct[0] = null;
      }
    });
  }
}
