package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.BHCAddUserEntity;
import com.haoche51.buyerapp.entity.HCCompareVehicleEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import java.util.Map;

public class ReserveCheckVehicleDialog {
  private Activity activity;
  private String vehicle_name;
  private int vehicle_source_id;
  private HCCompareVehicleEntity diffVehicleEntity;

  public ReserveCheckVehicleDialog(Activity activity, String vehicle_name, int vehicle_source_id,
      HCCompareVehicleEntity diffVehicleEntity) {
    this.activity = activity;
    this.vehicle_name = vehicle_name;
    this.vehicle_source_id = vehicle_source_id;
    this.diffVehicleEntity = diffVehicleEntity;
  }

  public ReserveCheckVehicleDialog(Activity activity, String vehicle_name, int vehicle_source_id) {
    this.activity = activity;
    this.vehicle_name = vehicle_name;
    this.vehicle_source_id = vehicle_source_id;
  }

  public Dialog showReserveCheckVehicleDialog() {
    final Dialog sDialog = new Dialog(activity, R.style.normal_dialog);
    View root = View.inflate(activity, R.layout.dialog_reserve_check_vehicle, null);
    sDialog.setContentView(root);
    final View view = sDialog.getWindow().getDecorView();
    TextView tv = (TextView) view.findViewById(R.id.tv_reserve_vehicle_name);
    tv.getPaint().setFakeBoldText(true);
    tv.setText(vehicle_name);
    sDialog.setCanceledOnTouchOutside(false);

    view.findViewById(R.id.iv_reserve_vehicle_cancel)
        .setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            sDialog.dismiss();
          }
        });
    final EditText mEditText = (EditText) view.findViewById(R.id.et_reserve_phone);
    final ImageView mClearIv = (ImageView) view.findViewById(R.id.iv_input_clear);
    mClearIv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mEditText.setText("");
        mEditText.requestFocus();
      }
    });

    if (!TextUtils.isEmpty(HCSpUtils.getUserPhone())) {
      mEditText.setText(HCSpUtils.getUserPhone());
    }

    mEditText.addTextChangedListener(new TextWatcher() {
      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void afterTextChanged(Editable s) {
        if (s != null) {
          int len = s.toString().length();
          int showStatus = len > 0 ? View.VISIBLE : View.GONE;
          mClearIv.setVisibility(showStatus);
        }
      }
    });

    view.findViewById(R.id.btn_reserve_confirm).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        final String phone = mEditText.getText().toString();
        if (TextUtils.isEmpty(phone) || phone.length() != 11 || !TextUtils.isDigitsOnly(phone)) {
          Animation anim = HCViewUtils.getShakeAnim();
          view.findViewById(R.id.rel_reserve).startAnimation(anim);
        } else {
          if (!HCUtils.isNetAvailable()) {
            HCUtils.showToast(R.string.hc_net_unreachable);
            return;
          }
          final String[] tx = new String[1];
          DialogUtils.showProgress(activity);
          int city_id = HCDbUtil.getSavedCityId();
          Map<String, Object> params =
              HCParamsUtil.reserveCheckVehicle(city_id, phone, vehicle_source_id);
          API.post(new HCRequest(params, new HCSimpleCallBack() {
            @Override public void onHttpFinish(String responseJsonString) {
              if (!TextUtils.isEmpty(responseJsonString)) {
                BHCAddUserEntity entity = HCJSONParser.parseReserveVehicle(responseJsonString);
                if (entity != null) {
                  tx[0] = entity.getErrmsg();
                  if (TextUtils.isEmpty(tx[0])) {
                    tx[0] = HCUtils.getResString(R.string.hc_reserve_succ);
                    if (diffVehicleEntity != null) {
                      HCSensorsUtil.appointmentSuccess("对比详情页", diffVehicleEntity, phone);
                    }
                  }
                }
              }

              DialogUtils.dismissProgress();
              if (!TextUtils.isEmpty(tx[0])) {
                HCUtils.showToast(tx[0]);
              }
              sDialog.dismiss();
            }
          }));
        }
      }
    });

    return sDialog;
  }
}
