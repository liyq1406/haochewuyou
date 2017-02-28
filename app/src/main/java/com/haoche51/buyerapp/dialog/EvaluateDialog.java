package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.util.HCUtils;

public class EvaluateDialog {
  private Activity activity;

  public EvaluateDialog(Activity activity) {
    this.activity = activity;
  }

  public void showEvaluateDialog() {
    final Activity[] mAct = new Activity[1];
    mAct[0] = activity;
    final Dialog dialog = new Dialog(mAct[0], R.style.normal_dialog);
    View root = View.inflate(mAct[0], R.layout.dialog_show_evaluate, null);

    root.findViewById(R.id.iv_evaluate_cancel).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
      }
    });

    root.findViewById(R.id.btn_evaluate_ok).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        gotoMarket();
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

  private void gotoMarket() {
    String qihoo =
        "http://zhushou.360.cn/detail/index/soft_id/2368571?recrefer=SE_D_%E5%A5%BD%E8%BD%A6%E6%97%A0%E5%BF%A7";
    Intent mIntent = new Intent(Intent.ACTION_VIEW);
    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    mIntent.setData(Uri.parse("market://details?id=" + GlobalData.mContext.getPackageName()));
    PackageManager mPm = GlobalData.mContext.getPackageManager();
    if (mIntent.resolveActivity(mPm) == null) {
      mIntent.setData(Uri.parse(qihoo));
    }
    GlobalData.mContext.startActivity(mIntent);
  }
}
