package com.haoche51.buyerapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCCompareVehicleEntity;
import com.haoche51.buyerapp.entity.HCModelEntity;
import com.haoche51.buyerapp.entity.HCReportEntity;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCDecodeUtis;
import com.haoche51.buyerapp.util.HCFormatUtil;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCViewClickListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * 车俩对比详情页
 */
public class DiffVehiclesActivity extends HCCommonTitleActivity {

  private View mPreLoadingView;
  private LinearLayout mNetErrLinear;

  /**
   * 概况父控件
   */
  private LinearLayout mOverViewLinear;

  /**
   * 车检报告父控件
   */
  private LinearLayout mCheckReportLinear;

  /**
   * 主要参数父控件
   */
  private LinearLayout mMainParamLinear;

  /**
   * 评价父控件
   */
  private LinearLayout mEvaluateLinear;

  private TextView mLeftTitleName, mRightTitleName;
  private ImageView mdiffLeftIv, mdiffRightIv;
  private Button mLeftBtn, mRightBtn;

  private ImageView mTopLeftIv, mTopRightIv, mBottomLeftIv, mBottomRightIv;
  private HCCompareVehicleEntity lEntity, rEntity;
  private int myId = 0;
  private int otherId = 0;

  @Override void initViews() {

    mPreLoadingView = findViewById(R.id.view_loading);
    mNetErrLinear = (LinearLayout) findViewById(R.id.linear_net_refresh);
    mNetErrLinear.setOnClickListener(mListener);

    mLeftBtn = (Button) findViewById(R.id.btn_diff_left);
    mRightBtn = (Button) findViewById(R.id.btn_diff_right);

    mLeftBtn.getPaint().setFakeBoldText(true);
    mRightBtn.getPaint().setFakeBoldText(true);
    mLeftBtn.setOnClickListener(mListener);
    mRightBtn.setOnClickListener(mListener);

    initHeaderPics();
    mLeftTitleName = (TextView) findViewById(R.id.tv_diff_vehiclename_left);
    mRightTitleName = (TextView) findViewById(R.id.tv_diff_vehiclename_right);
    mLeftTitleName.setOnClickListener(mListener);
    mRightTitleName.setOnClickListener(mListener);

    mdiffLeftIv = (ImageView) findViewById(R.id.iv_diff_left);
    mdiffRightIv = (ImageView) findViewById(R.id.iv_diff_right);

    mOverViewLinear = (LinearLayout) findViewById(R.id.linear_diff_overview);
    mCheckReportLinear = (LinearLayout) findViewById(R.id.linear_diff_checkreport);
    mMainParamLinear = (LinearLayout) findViewById(R.id.linear_diff_mainparam);
    mEvaluateLinear = (LinearLayout) findViewById(R.id.linear_diff_evaluate);

    if (getIntent().hasExtra(HCConsts.INTENT_KEY_MINE)) {
      Intent intent = getIntent();
      myId = intent.getIntExtra(HCConsts.INTENT_KEY_MINE, 0);
      otherId = intent.getIntExtra(HCConsts.INTENT_KEY_OTHER, 0);
      requestDiffData(myId, otherId);
    } else {
      finish();
    }
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    titleTv.setText(R.string.hc_diff_detail);
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_diff_vehicles;
  }

  private HCViewClickListener mListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {

      int viewId = v.getId();
      switch (v.getId()) {
        case R.id.tv_diff_vehiclename_left:
        case R.id.tv_diff_vehiclename_right:
          if (lEntity == null || rEntity == null) return;
          HCCompareVehicleEntity compareVehicleEntity =
              viewId == R.id.tv_diff_vehiclename_left ? lEntity : rEntity;
          int vehicleSourceId = compareVehicleEntity.getId();
          VehicleDetailActivity.idToThis(GlobalData.mContext, String.valueOf(vehicleSourceId),
              "对比");
          break;

        case R.id.btn_diff_left:
        case R.id.btn_diff_right:
          if (lEntity == null || rEntity == null) return;
          String vehicle_name =
              viewId == R.id.btn_diff_left ? lEntity.getVehicle_name() : rEntity.getVehicle_name();
          int vehicle_source_id =
              viewId == R.id.btn_diff_left ? lEntity.getVehicle_id() : rEntity.getVehicle_id();
          HCCompareVehicleEntity diffVehicleEntity =
              viewId == R.id.btn_diff_left ? lEntity : rEntity;
          Dialog dialog =
              DialogUtils.showReserveCheckVehicleDialog(DiffVehiclesActivity.this, vehicle_name,
                  vehicle_source_id, diffVehicleEntity);
          if (dialog != null) {
            dialog.show();
          }
          break;

        case R.id.linear_net_refresh:
          requestDiffData(myId, otherId);
          break;
      }
    }
  };

  private void requestDiffData(final int mLeftId, final int mRightId) {

    //加载前判断网络状态
    if (!HCUtils.isNetAvailable()) {
      mPreLoadingView.setVisibility(View.GONE);
      mNetErrLinear.setVisibility(View.VISIBLE);
      HCUtils.toastNetError();
      return;
    }

    mPreLoadingView.setVisibility(View.VISIBLE);
    HCUtils.hideViewIfNeed(mNetErrLinear);

    Map<String, Object> params = HCParamsUtil.compareVehicles(mLeftId, mRightId);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleDiffData(responseJsonString, mLeftId);
      }
    }));
  }

  private void handleDiffData(String responseJsonString, int mLeftId) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      mPreLoadingView.setVisibility(View.GONE);
      responseJsonString = HCDecodeUtis.decodeUnicode(responseJsonString);

      responseJsonString = removeUnUseChars(responseJsonString);
      List<HCCompareVehicleEntity> mDiffData =
          HCJSONParser.parseCompareVehicles(responseJsonString);
      if (mDiffData != null && mDiffData.size() == 2) {
        if (mDiffData.get(0).getId() == mLeftId) {
          lEntity = mDiffData.get(0);
          rEntity = mDiffData.get(1);
        } else {
          lEntity = mDiffData.get(1);
          rEntity = mDiffData.get(0);
        }
        fillPicture();
        fillOverviewData();
        fillMainParam();
        fillcheckReport();
        fillEvalator();
      }
    }
  }

  private void fillPicture() {
    if (lEntity != null && rEntity != null) {

      List<String> lUrls = lEntity.getCover_image_urls();
      List<String> rUrls = rEntity.getCover_image_urls();

      if (!HCUtils.isListEmpty(lUrls) && lUrls.size() >= 2) {
        String lurl0 = lUrls.get(0);
        String lurl1 = lUrls.get(1);
        ImageLoaderHelper.displayNormalImage(lurl0, mTopLeftIv);
        ImageLoaderHelper.displayNormalImage(lurl1, mBottomLeftIv);
      }

      if (!HCUtils.isListEmpty(rUrls) && rUrls.size() >= 2) {
        String rurl0 = rUrls.get(0);
        String rurl1 = rUrls.get(1);
        ImageLoaderHelper.displayNormalImage(rurl0, mTopRightIv);
        ImageLoaderHelper.displayNormalImage(rurl1, mBottomRightIv);
      }

      String lName = lEntity.getVehicle_name();
      String rName = rEntity.getVehicle_name();
      lName = HCFormatUtil.formatVehicleName(lName);
      rName = HCFormatUtil.formatVehicleName(rName);
      mLeftTitleName.setText(lName);
      mRightTitleName.setText(rName);
      int lBrandId = lEntity.getBrand_id();
      int rBrandId = rEntity.getBrand_id();

      //if (BrandLogoHelper.BRAND_LOGO.containsKey(lBrandId)) {
      //  int res = BrandLogoHelper.BRAND_LOGO.get(lBrandId);
      //  mdiffLeftIv.setImageResource(res);
      //}

      //if (BrandLogoHelper.BRAND_LOGO.containsKey(rBrandId)) {
      //  int res = BrandLogoHelper.BRAND_LOGO.get(rBrandId);
      //  mdiffRightIv.setImageResource(res);
      //}

      HCViewUtils.setIconById(mdiffLeftIv, lBrandId);
      HCViewUtils.setIconById(mdiffRightIv, rBrandId);

      // 设置底部按钮,如果是已售出
      int lstatus = lEntity.getStatus();
      int rstatus = rEntity.getStatus();
      if (lstatus == HCConsts.STATUS_SOLD || lstatus == HCConsts.STATUS_OWNER_SOLD) {
        findViewById(R.id.rel_diff_left).setBackgroundResource(R.color.promote_gray);
        mLeftBtn.setBackgroundResource(R.color.promote_gray);
        mLeftBtn.setText(R.string.hc_vehicle_sold);
        mLeftBtn.setClickable(false);
      }
      if (rstatus == HCConsts.STATUS_SOLD || rstatus == HCConsts.STATUS_OWNER_SOLD) {
        findViewById(R.id.rel_diff_right).setBackgroundResource(R.color.promote_gray);
        mRightBtn.setBackgroundResource(R.color.promote_gray);
        mRightBtn.setText(R.string.hc_vehicle_sold);
        mRightBtn.setClickable(false);
      }

      findViewById(R.id.linear_diff_bottom).setVisibility(View.VISIBLE);
    }
  }

  private void fillOverviewData() {
    if (lEntity != null && rEntity != null) {
      String[] mArr = HCUtils.getResArray(R.array.hc_diff_overview);
      int len = mArr.length;
      List<String> leftList = fillOverviewText(lEntity, len);
      List<String> rightList = fillOverviewText(rEntity, len);
      for (int i = 0; i < len; i++) {
        View view =
            LayoutInflater.from(this).inflate(R.layout.diff_item_tvs, mOverViewLinear, false);
        TextView leftTv = (TextView) view.findViewById(R.id.tv_item_left);
        TextView middle = (TextView) view.findViewById(R.id.tv_item_middle);
        TextView rightTv = (TextView) view.findViewById(R.id.tv_item_right);
        middle.setText(mArr[i]);
        if (i == 0) {
          fillRedBigText(leftTv, leftList.get(i));
          fillRedBigText(rightTv, rightList.get(i));
        } else {
          leftTv.setText(leftList.get(i));
          rightTv.setText(rightList.get(i));
        }
        mOverViewLinear.addView(view);
      }
    }
  }

  private void fillRedBigText(TextView tv, String ltext) {
    Spanned lsp = Html.fromHtml(ltext);
    Spannable lsb = new SpannableString(lsp);
    lsb.setSpan(new RelativeSizeSpan(1.2f), 0, ltext.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    tv.setText(lsb);
    tv.setTextColor(HCUtils.getResColor(R.color.reminder_red));
    tv.getPaint().setFakeBoldText(true);
  }

  private void fillMainParam() {
    if (lEntity != null && rEntity != null) {
      String[] mArr = HCUtils.getResArray(R.array.hc_diff_mainparam);
      int len = mArr.length;
      List<String> llist = fillMainParamText(lEntity, len);
      List<String> rlist = fillMainParamText(rEntity, len);
      for (int i = 0; i < len; i++) {
        View view =
            LayoutInflater.from(this).inflate(R.layout.diff_item_tvs, mMainParamLinear, false);
        TextView leftTv = (TextView) view.findViewById(R.id.tv_item_left);
        TextView middleTv = (TextView) view.findViewById(R.id.tv_item_middle);
        TextView rightTv = (TextView) view.findViewById(R.id.tv_item_right);
        middleTv.setText(mArr[i]);
        mMainParamLinear.addView(view);
        leftTv.setText(llist.get(i));
        rightTv.setText(rlist.get(i));
      }
    }
  }

  private void fillcheckReport() {
    if (lEntity != null && rEntity != null) {
      String[] mArr = HCUtils.getResArray(R.array.hc_diff_checkreport);
      int len = mArr.length;
      for (int i = 0; i < len; i++) {
        View view = LayoutInflater.from(this)
            .inflate(R.layout.diff_item_ratingbar, mCheckReportLinear, false);
        TextView middleTv = (TextView) view.findViewById(R.id.tv_diffrb_middle);
        TextView leftTv = (TextView) view.findViewById(R.id.tv_diffrb_left);
        TextView rightTv = (TextView) view.findViewById(R.id.tv_diffrb_right);
        LinearLayout leftRbLinear = (LinearLayout) view.findViewById(R.id.linear_diffrb_left);
        LinearLayout rightRbLinear = (LinearLayout) view.findViewById(R.id.linear_diffrb_right);

        middleTv.setText(mArr[i]);
        float lScore = 0.0f, rScore = 0.0f;
        switch (i) {
          case 0:// 车辆外观评分
            lScore =
                (lEntity.getReport() != null ? lEntity.getReport().getVehicle_appearance_score()
                    : 0);
            rScore =
                (rEntity.getReport() != null ? rEntity.getReport().getVehicle_appearance_score()
                    : 0);
            break;

          case 1:// 车辆内饰评分
            lScore =
                (lEntity.getReport() != null ? lEntity.getReport().getVehicle_interior_score() : 0);
            rScore =
                (rEntity.getReport() != null ? rEntity.getReport().getVehicle_interior_score() : 0);
            break;

          case 2:// 车辆设备评分
            lScore = (lEntity.getReport() != null ? lEntity.getReport().getVehicle_equipment_score()
                : 0);
            rScore = (rEntity.getReport() != null ? rEntity.getReport().getVehicle_equipment_score()
                : 0);
            break;

          case 3:// 车辆机械评分
            lScore =
                (lEntity.getReport() != null ? lEntity.getReport().getVehicle_machine_score() : 0);
            rScore =
                (rEntity.getReport() != null ? rEntity.getReport().getVehicle_machine_score() : 0);
            break;
        }

        leftTv.setText(String.valueOf(lScore));
        setScore(leftRbLinear, lScore);
        setScore(rightRbLinear, rScore);
        rightTv.setText(String.valueOf(rScore));
        mCheckReportLinear.addView(view);
      }
    }
  }

  private void fillEvalator() {
    if (lEntity != null && rEntity != null) {
      String[] mArr = HCUtils.getResArray(R.array.hc_diff_evaluator);
      int len = mArr.length;
      String lText = "", rText = "";
      int pading = HCUtils.getDimenPixels(R.dimen.px_10dp);
      for (int i = 0; i < len; i++) {
        View view =
            LayoutInflater.from(this).inflate(R.layout.diff_item_tvs, mEvaluateLinear, false);
        view.getLayoutParams().height = -1;
        TextView leftTv = (TextView) view.findViewById(R.id.tv_item_left);
        TextView middle = (TextView) view.findViewById(R.id.tv_item_middle);
        TextView rightTv = (TextView) view.findViewById(R.id.tv_item_right);
        leftTv.setPadding(pading, pading, pading, pading);
        rightTv.setPadding(pading, pading, pading, pading);
        leftTv.setGravity(Gravity.TOP | Gravity.LEFT);
        rightTv.setGravity(Gravity.TOP | Gravity.LEFT);
        middle.setText(mArr[i]);
        switch (i) {
          case 0:// 车主说 seller_words
            lText = lEntity.getSeller_words();
            rText = rEntity.getSeller_words();
            break;
          case 1:// 评估师建议 checker_words
            lText = lEntity.getChecker_words();
            rText = rEntity.getChecker_words();
            break;
        }
        leftTv.setText(lText);
        rightTv.setText(rText);
        mEvaluateLinear.addView(view);
      }
    }
  }

  private void initHeaderPics() {
    mTopLeftIv = (ImageView) findViewById(R.id.iv_diffpic_top_left);
    mTopRightIv = (ImageView) findViewById(R.id.iv_diffpic_top_right);
    mBottomLeftIv = (ImageView) findViewById(R.id.iv_diffpic_bottom_left);
    mBottomRightIv = (ImageView) findViewById(R.id.iv_diffpic_bottom_right);

    int width = (int) (HCUtils.getScreenWidthInPixels() / 2.0f);
    int height = (int) (width * 3 / 4.0f);
    FrameLayout.LayoutParams ltp = (LayoutParams) mTopLeftIv.getLayoutParams();
    ltp.width = width - 1;
    ltp.height = height - 1;
    FrameLayout.LayoutParams rbp = (LayoutParams) mBottomRightIv.getLayoutParams();
    rbp.width = width - 1;
    rbp.height = height - 1;
    mTopRightIv.getLayoutParams().width = width - 1;
    mTopRightIv.getLayoutParams().height = height - 1;
    mBottomLeftIv.getLayoutParams().width = width - 1;
    mBottomLeftIv.getLayoutParams().height = height - 1;
  }

  private List<String> fillOverviewText(HCCompareVehicleEntity entity, int size) {
    ArrayList<String> list = new ArrayList<>(size);
    String text = "";
    for (int i = 0; i < size; i++) {
      switch (i) {
        case 0:// 售价
          text = "￥" + entity.getSeller_price() + "万";
          // text =
          // HCFormatUtil.formatSellerPrice(entity.getSeller_price());
          break;
        case 1:// 新车指导价
          text = HCFormatUtil.formatWanPrice(entity.getQuoted_price());
          break;
        //case 2:// 二手车商价
        //  text = HCFormatUtil.formatWanPrice(entity.getDealer_price());
        //  break;
        case 2:// 公里数
          text = HCFormatUtil.formatMiles(entity.getMiles());
          break;
        case 3:// 上牌时间
          text = HCFormatUtil.formatMonthYear(entity.getRegister_time());
          break;
        case 4:// 过户次数
          text = entity.getReport() != null ? entity.getReport().getTransfer_times() + "次" : "";
          break;
        case 5:// 标准油耗
          text = entity.getModel() != null ? entity.getModel().getOffical_oil_cost() : "";
          break;
      }
      list.add(text);
    }

    return list;
  }

  private List<String> fillMainParamText(HCCompareVehicleEntity entity, int size) {
    ArrayList<String> list = new ArrayList<>(size);
    String text = "";
    HCModelEntity model = entity.getModel();
    HCReportEntity report = entity.getReport();
    for (int i = 0; i < size; i++) {
      switch (i) {
        case 0:// 车身结构
          text = model != null ? model.getStructure_all() : "";
          break;
        case 1:// 变速箱
          text = entity.getGeerbox() == null ? "" : entity.getGeerbox();
          break;
        case 2:// 排放
          text = entity.getEmission_standard() + "";
          break;
        case 3:// 天窗
          text = report == null ? "无" : "有";
          break;
        case 4:// 座椅
          text = model != null ? model.getLeather_seat() : "";
          break;
        case 5:// 空调
          text = model != null ? model.getAir_conditioning_mode() : "";
          break;
        case 6:// 长宽高
          text = model != null ? model.getLwh() : "";
          break;
        case 7:// 发动机
          text = model != null ? model.getEngine() : "";
          break;
        case 8:// 燃油
          text = model != null ? model.getFuel_label() : "";
          break;
        case 9:// 最大马力
          text = model != null ? model.getHorsepower() + "" : "";
          break;
        case 10:// 最大扭矩
          text = model != null ? model.getMax_torque() + "" : "";
          break;
        case 11:// 进气形式
          text = model != null ? model.getIntake_form() : "";
          break;
        case 12:// 汽缸数
          text = model != null ? model.getCylinder_num() + "" : "";
          break;
        case 13:// 驱动方式
          text = model != null ? model.getDriving_mode() : "";
          break;
        case 14:// 轴距
          text = model != null ? model.getWheel_base() + "" : "";
          break;
      }
      list.add(text);
    }
    return list;
  }

  private void setScore(LinearLayout linear, float score) {

    for (int i = 0; i < (int) score; i++) {
      ImageView iv = new ImageView(this);
      iv.setImageResource(R.drawable.icon_ratingbar_selected);
      iv.setPadding(1, 1, 1, 1);
      linear.addView(iv);
    }
  }

  private String removeUnUseChars(String str) {
    return str.replace("\\", "").replace("\"{", "{").replace("}\"", "}");
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
