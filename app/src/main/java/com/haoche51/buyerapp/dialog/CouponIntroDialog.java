package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.util.HCUtils;

public class CouponIntroDialog {
  private Activity mAct;
  private int status;

  public CouponIntroDialog(Activity mAct, int status) {
    this.mAct = mAct;
    this.status = status;
  }

  public void showCouponIntro() {
    final Dialog sDialog = new Dialog(mAct, R.style.normal_dialog);
    View root = View.inflate(mAct, R.layout.dialog_coupon_intro, null);
    if (status == 9) {
      TextView tv = (TextView) root.findViewById(R.id.tv_intro_content);
      tv.setText(R.string.hc_coupon_uncomplate);
    }
    sDialog.setContentView(root);
    root.findViewById(R.id.btn_known).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        sDialog.dismiss();
      }
    });
    Window window = sDialog.getWindow();
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.width = (int) (HCUtils.getScreenWidthInPixels() * 3F / 4F);
    window.setAttributes(lp);
    sDialog.show();
  }
}
