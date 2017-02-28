package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCCityEntity;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCUtils;

public class BangMaiCityChangeDialog {

  private Activity mAct;
  private String cityName;
  private int type;

  public BangMaiCityChangeDialog(Activity mAct, String cityName, int type) {
    this.mAct = mAct;
    this.cityName = cityName;
    this.type = type;
  }

  public void showBangMaiDialog() {
    String message = HCUtils.getResString(R.string.hc_bangmai_city_change_message, cityName);
    try {
      Dialog dialog = new AlertDialog.Builder(mAct).
          setMessage(message).setPositiveButton("切换", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
          HCEvent.postEvent(HCEvent.ACTION_MAINACT_TO_CORE, type);
          HCCityEntity entity = HCDbUtil.queryByCityName(cityName);
          if (entity != null) {
            GlobalData.userDataHelper.setCity(entity);
            HCEvent.postEvent(HCEvent.ACTION_CITYCHANGED);
          }
        }
      }).setNegativeButton("不切换", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
          HCEvent.postEvent(HCEvent.ACTION_MAINACT_TO_CORE, type);
        }
      }).create();
      dialog.setCanceledOnTouchOutside(false);
      dialog.show();
    } catch (Exception e) {
      HCLog.d("BangMaiCityChangeDialog", e.getMessage());
    }
  }
}
