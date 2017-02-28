package com.haoche51.buyerapp.activity;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;

import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCViewClickListener;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.SoldVehicleAdapter;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.ScanHistoryEntity;
import com.haoche51.buyerapp.entity.SoldBuyerListEntity;
import com.haoche51.buyerapp.entity.SoldVehicleInfoEntity;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCFormatUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import com.haoche51.custom.ConstListView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 我出售的车
 */
public class MySoldVehiclesActivity extends HCCommonTitleActivity {

  @InjectView(R.id.view_loading) View viewLoading;
  @InjectView(R.id.linear_net_refresh) LinearLayout mNetErrLinear;

  /** 咨询销售 */
  @InjectView(R.id.tv_sold_consultative) TextView mConsultativeTv;

  @InjectView(R.id.rel_title_descption) RelativeLayout mTitleRel;
  @InjectView(R.id.iv_sold_brand) ImageView mBrandIv;
  @InjectView(R.id.tv_sold_vehicle_name) TextView mVehicleNameTv;
  @InjectView(R.id.view_sold_line) View titleLine;

  //--------------------normal-----------------------//

  @InjectView(R.id.linear_sold_inner) LinearLayout mNormalParent;

  @InjectView(R.id.iv_sold_vehicle_pic) ImageView mVehiclePicIv;

  /** 车源编号 */
  @InjectView(R.id.tv_sold_vehicle_number) TextView mVehicleIdTv;

  @InjectView(R.id.tv_sold_correct_number) TextView mCorrectNumberTv;

  /** 价格描述 */
  @InjectView(R.id.tv_sold_price_title) TextView mPriceTitleTv;

  /** 显示价格 */
  @InjectView(R.id.tv_sold_quote_price) TextView mPriceTv;

  @InjectView(R.id.iv_sold_soldicon) ImageView mSoldIconIv;

  @InjectView(R.id.linear_sold_suggestion) LinearLayout mSuggestionLinear;

  /** 建议价格 */
  @InjectView(R.id.tv_sold_suggestion_price_title) TextView mSuggestionContentTv;

  @InjectView(R.id.tv_sold_suggestion_price) TextView mSuggetionPriceTv;

  /** 调整价格按钮 */
  @InjectView(R.id.tv_sold_adjust_price) TextView mAdujstPriceTv;

  /** 出售进度emptyView */
  @InjectView(R.id.tv_sold_empty) TextView mProcessEmptyTv;

  /** 出售进度红线 */
  @InjectView(R.id.view_sold_redline) View mRedLine;

  @InjectView(R.id.lv_sold_main) ConstListView mProcessLv;
  private SoldVehicleAdapter mProcessAdapter;
  private List<SoldBuyerListEntity> mProcessData = new ArrayList<>();

  @InjectView(R.id.tv_sold_status) TextView mSoldStatusTv;

  @InjectView(R.id.tv_sold_apply_offline) TextView mApplyOfflineTv;

  //--------------------normal-----------------------//

  //--------------------offline-----------------------//
  @InjectView(R.id.rel_sold_offline) RelativeLayout mOfflineParent;
  @InjectView(R.id.tv_sold_offline_owner) TextView moffLineOwnerName;
  @InjectView(R.id.tv_sold_offline_desc) TextView moffLineDesc;
  @InjectView(R.id.tv_sold_offline_service_phone) TextView moffLineServiceTv;
  //--------------------offline-----------------------//

  //--------------------not online-----------------------//
  @InjectView(R.id.rel_sold_not_online) RelativeLayout mNotOnlineParent;

  @InjectView(R.id.tv_sold_not_online_owner) TextView mNotOnlineOwnerNameTv;

  @InjectView(R.id.tv_sold_not_online_hint) TextView mNotOnlineHintTv;

  @InjectView(R.id.tv_notonline_phone) TextView mNotOnlinePhoneTv;

  //--------------------not online-----------------------//

  @InjectView(R.id.rel_sold_not_submit) RelativeLayout mNotSubmitParent;
  @InjectView(R.id.tv_sold_not_submit_ownner) TextView mNotSubmitTv;
  @InjectView(R.id.tv_sold_not_submit_phone) TextView mNotSubmitPhoneTv;

  View footerView;

  /** 记住上一次选择的postion */
  private int mLastPos = 0;

  private List<SoldVehicleInfoEntity> allMyVehicles = new ArrayList<>();

  @Override void initViews() {
    DialogUtils.showProgress(this);
    HCEvent.register(this, HCEvent.PRIORITY_HIGH);
    mProcessAdapter = new SoldVehicleAdapter(this, mProcessData, R.layout.lvitem_my_sold_vehicles);
    mProcessLv.setAdapter(mProcessAdapter);
    mProcessLv.setEmptyView(mProcessEmptyTv);

    initClickListener();

    int resLayout = R.layout.activity_my_sold_vehicles_footerview;
    footerView = LayoutInflater.from(this).inflate(resLayout, null);

    requestSoldVehicles(HCSpUtils.getUserPhone());
  }

  private void initClickListener() {
    findViewById(R.id.iv_sold_dropdown).setOnClickListener(mClickListener);
    findViewById(R.id.rel_title_descption).setOnClickListener(mClickListener);
    findViewById(R.id.tv_sold_adjust_price).setOnClickListener(mClickListener);
    findViewById(R.id.tv_sold_apply_offline).setOnClickListener(mClickListener);
    findViewById(R.id.tv_sold_consultative).setOnClickListener(mClickListener);
    findViewById(R.id.tv_sold_correct_number).setOnClickListener(mClickListener);
    findViewById(R.id.tv_sold_offline_service_phone).setOnClickListener(mClickListener);
    findViewById(R.id.tv_notonline_phone).setOnClickListener(mClickListener);
    findViewById(R.id.tv_sold_not_submit_phone).setOnClickListener(mClickListener);
    findViewById(R.id.iv_sold_vehicle_pic).setOnClickListener(mClickListener);
    findViewById(R.id.tv_sold_not_submit_sold_vehicle).setOnClickListener(mClickListener);
    findViewById(R.id.linear_net_refresh).setOnClickListener(mClickListener);
  }

  private HCViewClickListener mClickListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {
      switch (v.getId()) {
        //切换车辆按钮 R.id.rel_title_descption
        case R.id.iv_sold_dropdown:
        case R.id.rel_title_descption:
          if (allMyVehicles == null || allMyVehicles.size() <= 1) {
            return;
          }
          DialogUtils.showAllSoldVehicles(MySoldVehiclesActivity.this, allMyVehicles, mLastPos);
          break;
        //调整价格 申请下线
        case R.id.tv_sold_adjust_price:
        case R.id.tv_sold_apply_offline:
        case R.id.tv_sold_consultative:
        case R.id.tv_sold_correct_number:
        case R.id.tv_sold_offline_service_phone:
        case R.id.tv_notonline_phone:
        case R.id.tv_sold_not_submit_phone:
          if (v.getTag() != null && v.getTag() instanceof String) {
            HCUtils.diaPhone((String) v.getTag());
          }
          break;
        case R.id.iv_sold_vehicle_pic:
          if (v.getTag() != null && v.getTag() instanceof ScanHistoryEntity) {
            ScanHistoryEntity entity = (ScanHistoryEntity) v.getTag();
            int id = HCUtils.str2Int(entity.getId());
            if (id > 0) {
              VehicleDetailActivity.idToThis(GlobalData.mContext, String.valueOf(id), "出售");
            }
          }
          break;
        case R.id.tv_sold_not_submit_sold_vehicle:
          MySoldVehiclesActivity.this.finish();
          break;
        case R.id.linear_net_refresh:
          if (HCUtils.isNetAvailable()) {
            requestSoldVehicles(HCSpUtils.getUserPhone());
          } else {
            HCUtils.toastNetError();
          }
          break;
      }
    }
  };

  private void requestSoldVehicles(String phone) {
    Map<String, Object> params = HCParamsUtil.getMySoldVehicles(phone, HCDbUtil.getSavedCityId());
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleSoldVehicles(responseJsonString);
      }
    }));
  }

  private void handleSoldVehicles(String responseJsonString) {
    DialogUtils.dismissProgress();
    HCUtils.hideViewIfNeed(viewLoading);
    HCUtils.hideViewIfNeed(mNetErrLinear);
    if (!TextUtils.isEmpty(responseJsonString)) {
      List<SoldVehicleInfoEntity> datas = HCJSONParser.parseMySoldVehicles(responseJsonString);
      allMyVehicles.addAll(datas);
      if (allMyVehicles != null && !allMyVehicles.isEmpty()) {
        fillEachView(allMyVehicles.get(0));
      }
    } else {
      mNetErrLinear.setVisibility(View.VISIBLE);
    }
  }

  private void fillEachView(SoldVehicleInfoEntity entity) {

    if (entity == null) return;

    //重置view的状态为初始状态
    mTitleRel.setVisibility(View.VISIBLE);
    mBrandIv.setVisibility(View.VISIBLE);
    titleLine.setVisibility(View.VISIBLE);

    mConsultativeTv.setVisibility(View.VISIBLE);
    mNormalParent.setVisibility(View.VISIBLE);
    mOfflineParent.setVisibility(View.GONE);
    mNotOnlineParent.setVisibility(View.GONE);

    //车源状态
    //车源状态编码(-1，下线处理；1，没有提交爱车出售信息；2,暂未上线；3,在售；4，已售)
    int status = entity.getStatus();

    int viewStatus = allMyVehicles.size() <= 1 ? View.GONE : View.VISIBLE;
    mTitleRel.setVisibility(viewStatus);

    if (status == -1) {
      //下线
      //隐藏normal布局
      mNormalParent.setVisibility(View.GONE);
      mConsultativeTv.setVisibility(View.GONE);
      mNotOnlineParent.setVisibility(View.GONE);
      mNotSubmitParent.setVisibility(View.GONE);
      mOfflineParent.setVisibility(View.VISIBLE);

      mBrandIv.setVisibility(View.GONE);
      titleLine.setVisibility(View.INVISIBLE);

      moffLineDesc.setText(entity.getStatus_text());

      int resId = R.string.hc_hi_ownner;
      moffLineOwnerName.setText(HCUtils.getResString(resId, HCSpUtils.getUserHintPhone()));

      preDialTv(moffLineServiceTv);
    }

    if (status == 1) {
      //没有提交
      mNormalParent.setVisibility(View.GONE);
      mConsultativeTv.setVisibility(View.GONE);
      mNotSubmitParent.setVisibility(View.VISIBLE);
      //hc_hi_ownner
      String phone = HCUtils.getResString(R.string.hc_hi_ownner, HCSpUtils.getUserHintPhone());
      mNotSubmitTv.setText(phone);
      preDialTv(mNotSubmitPhoneTv);
    }

    if (status == 2) {
      //暂未上线
      if (mTitleRel.getVisibility() == View.GONE) {
        mNotOnlineHintTv.setVisibility(View.VISIBLE);
      }
      if (mNotOnlineHintTv.getVisibility() == View.GONE) {
        mBrandIv.setVisibility(View.GONE);
        titleLine.setVisibility(View.INVISIBLE);
      }

      String notHintText = entity.getStatus_text();
      if (!TextUtils.isEmpty(notHintText)) {
        mNotOnlineHintTv.setText(notHintText);
      }

      String hintPhone = HCSpUtils.getUserHintPhone();
      int res = R.string.hc_hi_owner_not_online;
      mNotOnlineOwnerNameTv.setText(HCUtils.getResString(res, hintPhone));

      mNotOnlineParent.setVisibility(View.VISIBLE);
      mNormalParent.setVisibility(View.GONE);
      mConsultativeTv.setVisibility(View.GONE);
      mOfflineParent.setVisibility(View.GONE);

      preDialTv(mNotOnlinePhoneTv);
    }

    mConsultativeTv.setTag(entity.getAsk_seller_phone());

    String sell_text = entity.getask_seller_text();
    if (!TextUtils.isEmpty(sell_text)) {
      mConsultativeTv.setText(sell_text);
    }

    //设置品牌
    int brand_id = entity.getBrand_id();
    HCViewUtils.setIconById(mBrandIv, brand_id, R.drawable.icon_brand_unlimit);

    String vehicle_name = entity.getVehicle_name();
    vehicle_name = HCUtils.toHCString(vehicle_name);
    mVehicleNameTv.setText(vehicle_name);

    if (TextUtils.isEmpty(vehicle_name) && status == 2) {
      mVehicleNameTv.setGravity(Gravity.CENTER);
      mVehicleNameTv.setText(HCUtils.getResString(R.string.hc_zanweishangxian));
      mVehicleNameTv.setTextColor(HCUtils.getResColor(R.color.font_gray));
    } else if (TextUtils.isEmpty(vehicle_name) && status == -1) {
      //下线
      mVehicleNameTv.setGravity(Gravity.CENTER);
      mVehicleNameTv.setText(entity.getStatus_text());
      mVehicleNameTv.setTextColor(HCUtils.getResColor(R.color.font_gray));
    } else {
      mVehicleNameTv.setGravity(Gravity.LEFT);
      mVehicleNameTv.setTextColor(HCUtils.getResColor(R.color.font_black));
    }

    //设置图片
    String cover_image = entity.getCover_image();
    cover_image = HCUtils.toHCString(cover_image);
    int picW = HCUtils.getScreenWidthInPixels();
    int picH = (int) ((picW * 480F) / 640);
    mVehiclePicIv.getLayoutParams().width = picW;
    mVehiclePicIv.getLayoutParams().height = picH;
    if (!TextUtils.isEmpty(cover_image)) {
      ImageLoaderHelper.displayNoMemoryImage(cover_image, mVehiclePicIv);
    } else {
      mVehiclePicIv.setImageResource(R.drawable.default_template);
    }

    ScanHistoryEntity se = convert2History(entity);
    mVehiclePicIv.setTag(se);

    //车源编号
    String str = HCUtils.getResString(R.string.hc_vehicle_number, entity.getVehicle_source_id());
    mVehicleIdTv.setText(str);

    //纠正电话
    String correctText = entity.getCorrect_text();
    String correctPhone = entity.getCorrect_phone();
    correctText = HCUtils.toHCString(correctText);
    correctPhone = HCUtils.toHCString(correctPhone);
    String text = correctText + " " + correctPhone;
    if (!TextUtils.isEmpty(correctText) && !TextUtils.isEmpty(correctPhone)) {
      mCorrectNumberTv.setText(text);
      mCorrectNumberTv.setTag(correctPhone);
      int start = text.indexOf(correctPhone);
      int end = text.length();
      int color = HCUtils.getResColor(R.color.reminder_red);
      HCViewUtils.changeTextViewColor(mCorrectNumberTv, color, start, end);
    }

    if (status == 4) {
      mPriceTitleTv.setText(R.string.hc_deal_price);
    } else {
      mPriceTitleTv.setText(R.string.hc_baojia);
    }

    String priceStr =
        HCFormatUtil.getSoldPriceFormat(HCUtils.str2Float(entity.getSeller_price()), 2);
    Spanned spannedPrice = Html.fromHtml(priceStr);
    mPriceTv.setText(spannedPrice);

    if (status == 4) {
      mSoldIconIv.setVisibility(View.VISIBLE);
    } else {
      mSoldIconIv.setVisibility(View.GONE);
    }

    if (status == 3) {//建议价格布局
      mSuggestionLinear.setVisibility(View.VISIBLE);
      int suggest_status = entity.getSuggest_status();
      //1,估价合理；2价格偏高
      if (suggest_status == 2) {
        if (TextUtils.isEmpty(entity.getEval_price())) {
          mSuggetionPriceTv.setText(entity.getEval_price());
        }
      }
      mSuggestionContentTv.setText(entity.getSuggest_text());
    } else {
      mSuggestionLinear.setVisibility(View.GONE);
    }

    mAdujstPriceTv.setTag(entity.getAdjust_phone());
    String adjust_text = entity.getAdjust_text();
    if (!TextUtils.isEmpty(adjust_text)) {
      mAdujstPriceTv.setText(adjust_text);
    }

    mProcessData.clear();
    //设置出售进度
    List<SoldBuyerListEntity> process = entity.getBuyer_list();
    process = process == null ? new ArrayList<SoldBuyerListEntity>() : process;
    process = process.size() > 20 ? process.subList(0, 20) : process;
    mProcessData.addAll(process);
    mProcessAdapter.notifyDataSetChanged();
    if (process.isEmpty()) {
      mRedLine.setVisibility(View.GONE);
    } else {
      mRedLine.setVisibility(View.VISIBLE);
    }

    controlFooterView(process);

    mApplyOfflineTv.setTag(entity.getOffline_phone());
    String offlineText = entity.getOffline_text();
    if (!TextUtils.isEmpty(offlineText)) {
      mSoldStatusTv.setText(offlineText);
    }
  }

  private void controlFooterView(List<SoldBuyerListEntity> process) {

    if (process.isEmpty()) {
      if (mProcessLv.getFooterViewsCount() != 0) {
        mProcessLv.removeFooterView(footerView);
      }
    } else {
      if (mProcessLv.getFooterViewsCount() == 0) {
        mProcessLv.addFooterView(footerView);
      }
    }
  }

  public void onEvent(HCCommunicateEntity entity) {
    if (entity == null) return;

    String action = entity.getAction();
    if (HCEvent.ACTION_SOLD_DIALOG_CLICK.equals(action)) {
      mLastPos = entity.getIntValue();
      if (mLastPos <= allMyVehicles.size()) {
        SoldVehicleInfoEntity en = allMyVehicles.get(mLastPos);
        fillEachView(en);
      }
    }
  }

  private ScanHistoryEntity convert2History(SoldVehicleInfoEntity info) {
    ScanHistoryEntity se = new ScanHistoryEntity();
    se.setId(info.getVehicle_source_id() + "");
    return se;
  }

  private void preDialTv(TextView tv) {
    String phone = HCSpUtils.getKefuPhone();
    if (!TextUtils.isEmpty(phone)) {
      String formatStr = HCUtils.getResString(R.string.hc_buyvehicle_advisory, phone);
      tv.setText(formatStr);
      tv.setTag(phone);
      int start = formatStr.indexOf(phone);
      int end = formatStr.length();
      int color = HCUtils.getResColor(R.color.reminder_red);
      HCViewUtils.changeTextViewColor(tv, color, start, end);
    }
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    titleTv.setText(R.string.hc_my_vehicle);
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_my_sold_vehicles;
  }

  @Override protected void onDestroy() {
    HCEvent.unRegister(this);
    super.onDestroy();
  }

  @Override protected void onResume() {
    super.onResume();

    doThirdPartyResume();
  }

  @Override protected void onPause() {
    super.onPause();

    doThirdPartyPause();
  }

  private void doThirdPartyResume() {
    String name = this.getClass().getSimpleName();
    ThirdPartInjector.onPageStart(name);
    ThirdPartInjector.onResume(this);
  }

  private void doThirdPartyPause() {
    String name = this.getClass().getSimpleName();
    ThirdPartInjector.onPageEnd(name);
    ThirdPartInjector.onPause(this);
  }
}
