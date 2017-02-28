package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.HCPollService;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.MainActivity;
import com.haoche51.buyerapp.entity.HCCityEntity;
import com.haoche51.buyerapp.entity.HCLocationEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.Map;

public class LocationChangeDialog {

  private Activity mAct;
  private String mSaveCityName = GlobalData.userDataHelper.getCity().getCity_name();
  private String mLastLocationCityName = HCSpUtils.getLastCityLocation();
  private HCCityEntity mLocCityEntity;

  public LocationChangeDialog(Activity mAct) {
    this.mAct = mAct;
  }

  public void showLocationChangeDialog() {
    MainActivity mMainAct = (MainActivity) mAct;
    HCPollService.HCServiceBinder binder = mMainAct.getServiceBinder();
    if (binder != null) {
      HCLocationEntity location = binder.getBaiduLocation();
      if (location != null) {
        String city_name = location.getCity_name();
        if (!TextUtils.isEmpty(city_name)) {
          mLocCityEntity = HCDbUtil.queryByCityName(city_name);
          //HCSensorsUtil.setUserOnceProperties(city_name);
        }
        if (mLocCityEntity != null) {
          seeIfNeedDialog(mLocCityEntity);
        } else {
          // 定位到的城市不在开通范围,发送请求该显示哪个城市
          requestServerCity(location);
        }
      }
    }
  }

  private void requestServerCity(HCLocationEntity loc) {
    double latitude = loc.getLatitude();
    double longitude = loc.getlongitude();

    Map<String, Object> params = HCParamsUtil.getNearestCity(latitude, longitude);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleServerCity(responseJsonString);
      }
    }));
  }

  private void handleServerCity(String responseJsonString) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      mLocCityEntity = HCJSONParser.parseNearestCity(responseJsonString);
    }
    if (mLocCityEntity != null) {
      seeIfNeedDialog(mLocCityEntity);
    }
  }

  private void seeIfNeedDialog(final HCCityEntity mLocCityEntity) {
    final String locCityName = mLocCityEntity.getCity_name();
    if (TextUtils.isEmpty(locCityName) || TextUtils.isEmpty(mSaveCityName)) {
      return;
    }
    if (mSaveCityName.equals(locCityName) || mLastLocationCityName.equals(locCityName)) return;

    String message = HCUtils.getResString(R.string.hc_city_change_message, locCityName);
    try {
      Dialog dialog = new AlertDialog.Builder(mAct).setMessage(message)
          .setPositiveButton("切换", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
              GlobalData.userDataHelper.setCity(mLocCityEntity);
              HCSpUtils.clearLastCityLocation();
              HCEvent.postEvent(HCEvent.ACTION_CITYCHANGED);
            }
          })
          .setNegativeButton("不切换", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
              HCSpUtils.setLastCityLocation(locCityName);
            }
          })
          .create();
      dialog.setCanceledOnTouchOutside(false);
      dialog.show();
    } catch (Exception e) {
      HCLog.d("LocationChangeDialog", e.getMessage());
    }
  }
}
