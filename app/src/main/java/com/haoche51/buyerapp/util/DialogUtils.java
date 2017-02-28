package com.haoche51.buyerapp.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.ShowSoldAdapter;
import com.haoche51.buyerapp.dialog.BangMaiCityChangeDialog;
import com.haoche51.buyerapp.dialog.CityDialog;
import com.haoche51.buyerapp.dialog.CouponCountDialog;
import com.haoche51.buyerapp.dialog.CouponIntroDialog;
import com.haoche51.buyerapp.dialog.CouponUsageDialog;
import com.haoche51.buyerapp.dialog.EvaluateDialog;
import com.haoche51.buyerapp.dialog.HomeQuizDialog;
import com.haoche51.buyerapp.dialog.LocationChangeDialog;
import com.haoche51.buyerapp.dialog.PromoteDialog;
import com.haoche51.buyerapp.dialog.ReserveCheckVehicleDialog;
import com.haoche51.buyerapp.dialog.SubmitSellVehicleDialog;
import com.haoche51.buyerapp.dialog.SubscribeConditionDialog;
import com.haoche51.buyerapp.entity.HCCityEntity;
import com.haoche51.buyerapp.entity.HCCompareVehicleEntity;
import com.haoche51.buyerapp.entity.HCHomeDialogDataEntity;
import com.haoche51.buyerapp.entity.HCPromoteEntity;
import com.haoche51.buyerapp.entity.SoldVehicleInfoEntity;
import com.haoche51.buyerapp.entity.SubConditionDataEntity;
import java.util.ArrayList;
import java.util.List;

public class DialogUtils {

  public static Dialog mProcessDialog;

  public static int PROMOTE_PIC_WIDTH = (int) (HCUtils.getScreenWidthInPixels() * 580 / 640F);
  public static int PROMOTE_PIC_HEIGHT = (int) (HCUtils.getScreenHeightPixels() * 770 / 1136F);

  /**
   * 这个context必须是Activity
   */
  public static void showProgress(Activity mAct) {
    if (mAct != null && !mAct.isFinishing()) {

      View myView = View.inflate(mAct, R.layout.layout_progress_loading, null);
      mProcessDialog = new Dialog(mAct, R.style.dialog_loading_noDim);
      mProcessDialog.setContentView(myView);
      mProcessDialog.setCanceledOnTouchOutside(false);
      mProcessDialog.show();
    }
  }

  public static void dismissProgress() {
    if (mProcessDialog != null && mProcessDialog.isShowing()) {
      mProcessDialog.dismiss();
      mProcessDialog = null;
    }
  }

  public static void showCouponIntro(final Activity mAct, int status) {

    if (mAct == null || mAct.isFinishing()) return;

    CouponIntroDialog dialog = new CouponIntroDialog(mAct, status);
    dialog.showCouponIntro();
  }

  public static void showCouponUsage(Activity mAct) {

    if (mAct == null || mAct.isFinishing()) return;

    CouponUsageDialog dialog = new CouponUsageDialog(mAct);
    dialog.showCouponUsage();
  }

  /**
   * 预约看车弹层
   */
  public static Dialog showReserveCheckVehicleDialog(final Activity mAct, String vehicle_name,
      final int vehicle_source_id, HCCompareVehicleEntity entity) {

    if (mAct == null || mAct.isFinishing()) return null;

    ReserveCheckVehicleDialog dialog =
        new ReserveCheckVehicleDialog(mAct, vehicle_name, vehicle_source_id, entity);
    return dialog.showReserveCheckVehicleDialog();
  }

  public static Dialog showReserveCheckVehicleDialog(final Activity mAct, String vehicle_name,
      final int vehicle_source_id) {

    if (mAct == null || mAct.isFinishing()) return null;

    ReserveCheckVehicleDialog dialog =
        new ReserveCheckVehicleDialog(mAct, vehicle_name, vehicle_source_id);
    return dialog.showReserveCheckVehicleDialog();
  }

  public static void showCouponCountDialog(Activity activity) {

    int counts = HCSpUtils.getProfileCouponReminder();
    if (activity == null || activity.isFinishing() || counts <= 0) return;

    CouponCountDialog dialog = new CouponCountDialog(activity, counts);
    dialog.showCouponCountDialog();
  }

  /**
   * 展示当前订阅条件
   */
  public static void showSubscribeConditionDialog(Activity activity,
      final SubConditionDataEntity entity) {

    if (activity == null || activity.isFinishing()) return;

    SubscribeConditionDialog dialog = new SubscribeConditionDialog(activity, entity);
    dialog.showSubscribeConditionDialog();
  }

  /**
   * 评价弹层
   */
  public static void showEvaluateDialog(Activity activity) {
    if (activity == null || activity.isFinishing()) return;

    EvaluateDialog dialog = new EvaluateDialog(activity);
    dialog.showEvaluateDialog();
  }

  /**
   * 提交线索成功弹层
   */
  public static void showSubmitSellVehicle(final Activity activity, final String title,
      final String desc) {
    if (activity == null || activity.isFinishing()) return;

    SubmitSellVehicleDialog dialog = new SubmitSellVehicleDialog(activity, title, desc);
    dialog.showSubmitSellVehicleDialog();
  }

  public static void showAllSoldVehicles(Activity activity,
      List<SoldVehicleInfoEntity> allMyVehicles, int pos) {

    if (activity == null || activity.isFinishing()) return;

    final Activity[] mAct = new Activity[1];
    mAct[0] = activity;

    int layoutId = R.layout.dialog_my_sold_vehicles;
    View contentView = LayoutInflater.from(mAct[0]).inflate(layoutId, null);

    final Dialog dialog = new Dialog(mAct[0], R.style.sub_condition_dialog);

    final List<String> mData = new ArrayList<>();
    for (SoldVehicleInfoEntity en : allMyVehicles) {
      String name = en.getVehicle_name();

      if (TextUtils.isEmpty(name)) {
        //这个时候读取status_text字段
        name = en.getStatus_text();
      }

      mData.add(name);
    }

    ListView mLv = (ListView) contentView.findViewById(R.id.lv_dialg_sold);
    int resId = R.layout.lvitem_show_sold_vehicles;
    ShowSoldAdapter mAdapter = new ShowSoldAdapter(mAct[0], mData, resId);
    mLv.setAdapter(mAdapter);
    mAdapter.setLastPos(pos);

    mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (mData.size() > 1) {
          HCEvent.postEvent(HCEvent.ACTION_SOLD_DIALOG_CLICK, position);
        }
        dialog.dismiss();
      }
    });

    dialog.setContentView(contentView);

    Window window = dialog.getWindow();
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.width = HCUtils.getScreenWidthInPixels();
    lp.gravity = Gravity.BOTTOM;

    dialog.show();

    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override public void onDismiss(DialogInterface dialog) {
        mAct[0] = null;
      }
    });
  }

  /**
   * 第一次加载时，homepage弹层
   */
  public static void showPromoteDialog(Activity activity, final HCPromoteEntity entity) {

    if (activity == null || activity.isFinishing()) return;
    if (entity == null) return;

    PromoteDialog dialog = new PromoteDialog(activity, entity);
    dialog.showPromoteDialog();
  }

  /**
   * 选择城市弹层
   */
  public static void showCityDialog(Activity activity, List<HCCityEntity> mCityData) {
    if (activity == null || activity.isFinishing() || HCUtils.isListEmpty(mCityData)) return;
    CityDialog dialog = new CityDialog(activity, mCityData);
    dialog.showCityDialog();
  }

  /**
   * 刚打开APP时定位变化弹层
   */
  public static void showLocationChangeDialog(Activity activity) {
    if (activity == null || activity.isFinishing()) return;
    LocationChangeDialog dialog = new LocationChangeDialog(activity);
    dialog.showLocationChangeDialog();
  }

  /**
   * 帮买城市变化的弹层
   */
  public static void showBangMaiCityChangeDialog(Activity activity, String cityName, int type) {
    if (activity == null || activity.isFinishing() || TextUtils.isEmpty(cityName)) return;
    BangMaiCityChangeDialog dialog = new BangMaiCityChangeDialog(activity, cityName, type);
    dialog.showBangMaiDialog();
  }

  /**
   * 首页问答的弹层
   */
  public static void showHomeQuizDialog(Activity activity, HCHomeDialogDataEntity entity) {
    if (activity == null || activity.isFinishing() || entity.getType() == -1) return;
    HomeQuizDialog dialog = new HomeQuizDialog(activity, entity);
    dialog.showHomeQuizDialog();
  }
}
