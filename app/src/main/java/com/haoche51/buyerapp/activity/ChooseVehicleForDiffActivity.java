package com.haoche51.buyerapp.activity;

import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.ChooseVehicleForDiffAdapter;
import com.haoche51.buyerapp.adapter.ChooseVehicleForDiffAdapter.OnDiffVehicleChangedListener;
import com.haoche51.buyerapp.entity.ScanHistoryEntity;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCFormatUtil;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCPullToRefresh;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * 选择历史记录车辆进行对比
 */
public class ChooseVehicleForDiffActivity extends HCCommonTitleActivity {
  //对比按钮
  private Button mDiffBtn;
  //底部对比布局
  private LinearLayout mBottomLinear;
  private HCPullToRefresh mPullToRefresh;
  private ChooseVehicleForDiffAdapter mHistoryAdapter;
  private List<ScanHistoryEntity> mHistoryData = new ArrayList<>();
  private long last_update_time = System.currentTimeMillis() / 1000;

  /**
   * 从详情页传递过来的车
   */
  private ScanHistoryEntity mEntity;

  @Override void initViews() {
    setHeaderViews();
    mBottomLinear = (LinearLayout) findViewById(R.id.linear_diffvehicle_bottom);
    mDiffBtn = (Button) findViewById(R.id.btn_diffvehicle);
    mDiffBtn.setOnClickListener(mListener);
    mPullToRefresh = (HCPullToRefresh) findViewById(R.id.pdv_diffvehicle);
    View emptyView = findViewById(R.id.rel_history_empty);
    emptyView.findViewById(R.id.btn_history_empty).setVisibility(View.GONE);
    mPullToRefresh.setEmptyView(emptyView);
    mPullToRefresh.setCanPull(false);
    requestHistoryData();

    mPullToRefresh.setOnRefreshCallback(new HCPullToRefresh.OnRefreshCallback() {
      @Override public void onPullDownRefresh() {
        requestHistoryData();
      }

      @Override public void onLoadMoreRefresh() {
        requestHistoryData();
      }
    });

    int itemRes = R.layout.lvitem_diff_vehicle;
    mHistoryAdapter = new ChooseVehicleForDiffAdapter(this, mHistoryData, itemRes);
    mPullToRefresh.setAdapter(mHistoryAdapter);

    mHistoryAdapter.setOnDiffVehicleChangedListener(new OnDiffVehicleChangedListener() {
      @Override public void onVehicleChanged(ScanHistoryEntity changedEntity) {
        int textRes =
            changedEntity == null ? R.string.hc_choose_another_for_diff : R.string.hc_dofiff;
        int bg = changedEntity == null ? R.drawable.rect_diff_gray
            : R.drawable.selector_rect_red_no_raduis;
        int colorRes = changedEntity == null ? R.color.font_gray : R.color.promote_white;

        mDiffBtn.getPaint().setFakeBoldText(changedEntity != null);
        mDiffBtn.setTextColor(getResources().getColorStateList(colorRes));
        mDiffBtn.setBackgroundResource(bg);
        mDiffBtn.setText(textRes);
        mDiffBtn.setTag(changedEntity);
      }
    });
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    titleTv.setText(R.string.hc_diff);
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_choose_vehicle_for_diff;
  }

  private void setHeaderViews() {
    findViewById(R.id.diff_header).setBackgroundResource(R.color.diff_vehicle_bg);
    findViewById(R.id.frame_diffcar).setBackgroundResource(R.color.diff_vehicle_bg);
    findViewById(R.id.lvitem_line).setVisibility(View.GONE);

    ImageView mImageView = (ImageView) findViewById(R.id.cb_diffcar);
    mImageView.setImageResource(R.drawable.icon_sub_choosed);
    ImageView mPicIv = (ImageView) findViewById(R.id.iv_diffcar_image);
    TextView mNameTv = (TextView) findViewById(R.id.tv_diffcar_carname);
    TextView mDetailTv = (TextView) findViewById(R.id.tv_diffcar_car_detail);
    TextView mPriceTv = (TextView) findViewById(R.id.tv_diffcar_car_price);
    String key = HCConsts.INTENT_KEY_SCANENTITY;
    if (getIntent().getSerializableExtra(key) != null) {
      mEntity = (ScanHistoryEntity) getIntent().getSerializableExtra(key);
      String vehicle_name = mEntity.getVehicle_name();
      String time = mEntity.getRegister_time();
      String miles = mEntity.getMiles();
      String cover_image_url = mEntity.getCover_pic();
      float seller_price = HCUtils.str2Float(mEntity.getSeller_price());

      //设置图片水印
      String leftTopUrl = mEntity.getLeft_top();
      String leftTopRate = mEntity.getLeft_top_rate();
      ImageView leftTopIv = (ImageView) findViewById(R.id.iv_diffcar_left_top_image);
      if (!TextUtils.isEmpty(leftTopUrl) && !TextUtils.isEmpty(leftTopRate)) {
        leftTopIv.setVisibility(View.VISIBLE);
        int leftTopWh = HCUtils.dp2px(120F / HCUtils.str2Float(leftTopRate));
        leftTopIv.getLayoutParams().width = leftTopWh;
        leftTopIv.getLayoutParams().height = leftTopWh;
        ImageLoaderHelper.displayNoMemoryImage(leftTopUrl, leftTopIv);
      } else {
        HCUtils.hideViewIfNeed(leftTopIv);
      }
      String leftBottomUrl = mEntity.getLeft_bottom();
      String leftBottomRate = mEntity.getLeft_bottom_rate();
      ImageView leftBottomIv = (ImageView) findViewById(R.id.iv_diffcar_left_bottom_image);
      if (!TextUtils.isEmpty(leftBottomUrl) && !TextUtils.isEmpty(leftBottomRate)) {
        leftBottomIv.setVisibility(View.VISIBLE);
        int leftBottomWh = HCUtils.dp2px(120F / HCUtils.str2Float(leftBottomRate));
        leftBottomIv.getLayoutParams().width = leftBottomWh;
        leftBottomIv.getLayoutParams().height = leftBottomWh;
        ImageLoaderHelper.displayNoMemoryImage(leftBottomUrl, leftBottomIv);
      } else {
        HCUtils.hideViewIfNeed(leftBottomIv);
      }

      ImageLoaderHelper.displayNoMemoryImage(cover_image_url, mPicIv);
      mNameTv.setText(vehicle_name);
      String detail = HCFormatUtil.getSimpleVehicleDetail(time, miles);
      mDetailTv.setText(detail);
      Spanned SpannedPrice = Html.fromHtml(HCFormatUtil.getSoldPriceFormat(seller_price));
      mPriceTv.setText(SpannedPrice);
    }
  }

  private void requestHistoryData() {
    Map<String, Object> params = HCParamsUtil.getScanHistory(last_update_time);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleHistoryData(responseJsonString);
      }
    }));
  }

  private void handleHistoryData(String responseJsonString) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      List<ScanHistoryEntity> list = HCJSONParser.parseScanHistory(responseJsonString);
      list = removeValid(list);
      removeRepeat(mHistoryData, list);
      boolean isNoMore = list.size() < HCConsts.PAGESIZE;
      mPullToRefresh.setFooterStatus(isNoMore);
      int size = list.size();
      if (size > 0) {// 记录最后一条记录的时间
        last_update_time = list.get(size - 1).getCreate_time();
      }
    }

    if (!mHistoryData.isEmpty() && mBottomLinear.getVisibility() != View.VISIBLE) {
      mBottomLinear.setVisibility(View.VISIBLE);
    }
    mHistoryAdapter.notifyDataSetChanged();
    mPullToRefresh.finishRefresh();
  }

  private void removeRepeat(List<ScanHistoryEntity> allData, List<ScanHistoryEntity> subData) {
    for (ScanHistoryEntity entity : subData) {
      if (!allData.contains(entity)) {
        allData.add(entity);
      }
    }
    if (allData.contains(mEntity)) {
      allData.remove(mEntity);
    }
  }

  private View.OnClickListener mListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      switch (v.getId()) {
        case R.id.btn_diffvehicle:
          if (v.getTag() != null) {
            ScanHistoryEntity entity = (ScanHistoryEntity) v.getTag();
            go2DiffVehicle(HCUtils.str2Int(entity.getId()));
          }
          break;
      }
    }
  };

  private void go2DiffVehicle(int otherid) {
    Intent intent = new Intent(GlobalData.mContext, DiffVehiclesActivity.class);
    intent.putExtra(HCConsts.INTENT_KEY_MINE, HCUtils.str2Int(mEntity.getId()));
    intent.putExtra(HCConsts.INTENT_KEY_OTHER, otherid);
    startActivity(intent);
  }

  public List<ScanHistoryEntity> removeValid(List<ScanHistoryEntity> lists) {
    List<ScanHistoryEntity> results = new ArrayList<>();
    for (ScanHistoryEntity scan : lists) {
      if (!TextUtils.isEmpty(scan.getVehicle_name()) && HCUtils.str2Int(scan.getId()) > 0
          && HCUtils.str2Float(scan.getSeller_price()) > 0) {
        results.add(scan);
      }
    }
    return results;
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
