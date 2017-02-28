package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.util.HCUtils;

public class CouponUsageDialog {
  private Activity activity;

  public CouponUsageDialog(Activity activity) {
    this.activity = activity;
  }

  public void showCouponUsage() {
    final Activity[] activities = new Activity[1];
    activities[0] = activity;

    final Dialog sDialog = new Dialog(activities[0], R.style.normal_dialog);
    View root = View.inflate(activity, R.layout.dialog_coupon_usage, null);

    TextView tvTitle = (TextView) root.findViewById(R.id.tv_couponuseage_title);
    tvTitle.getPaint().setFakeBoldText(true);

    TextView tvContent = (TextView) root.findViewById(R.id.tv_coupoun_content);

    String text = tvContent.getText().toString();
    SpannableString spanString = new SpannableString(text);
    ForegroundColorSpan span = new ForegroundColorSpan(HCUtils.getResColor(R.color.reminder_red));
    spanString.setSpan(span, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    StyleSpan fontSp = new StyleSpan(Typeface.BOLD);
    spanString.setSpan(fontSp, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    int index = text.indexOf("2.");
    span = new ForegroundColorSpan(HCUtils.getResColor(R.color.reminder_red));
    spanString.setSpan(span, index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    fontSp = new StyleSpan(Typeface.BOLD);
    spanString.setSpan(fontSp, index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvContent.setText(spanString);

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

    sDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override public void onDismiss(DialogInterface dialog) {
        activities[0] = null;
      }
    });
  }
}
