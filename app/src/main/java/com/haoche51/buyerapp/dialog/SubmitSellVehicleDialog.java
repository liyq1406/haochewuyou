package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.LoginActivity;
import com.haoche51.buyerapp.activity.MySoldVehiclesActivity;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCUtils;

public class SubmitSellVehicleDialog {
  private Activity activity;
  private String title;
  private String desc;

  public SubmitSellVehicleDialog(Activity activity, String title, String desc) {
    this.activity = activity;
    this.title = title;
    this.desc = desc;
  }

  public void showSubmitSellVehicleDialog() {
    final Activity[] mAct = new Activity[1];
    mAct[0] = activity;
    final Dialog dialog = new Dialog(mAct[0], R.style.normal_dialog);
    View root = View.inflate(mAct[0], R.layout.dialog_sale_submit, null);
    TextView tv_title = (TextView) root.findViewById(R.id.hc_submit_status_title);
    TextView tv_desc = (TextView) root.findViewById(R.id.hc_submit_status_desc);
    tv_title.setText(title != null ? title : "");
    tv_desc.setText(desc != null ? desc : "");
    /**跳转按钮*/
    root.findViewById(R.id.btn_go_sale_status).setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View v) {
        dialog.dismiss();
        Intent intent = new Intent();
        if (HCUtils.isUserLogined()) {
          intent.setClass(activity, MySoldVehiclesActivity.class);
        } else {
          intent.setClass(activity, LoginActivity.class);
          intent.putExtra(HCConsts.INTENT_KEY_LOGIN_DEST, MySoldVehiclesActivity.class);
        }
        activity.startActivity(intent);
      }
    });
    /**取消按钮*/
    root.findViewById(R.id.iv_sale_submit_cancel).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
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
        mAct[0] = null;
      }
    });
  }
}
