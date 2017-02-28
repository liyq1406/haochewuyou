package com.haoche51.buyerapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.custom.HCViewClickListener;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.HCDetailEntity;
import com.haoche51.buyerapp.entity.ScanHistoryEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ShareUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 车辆详情页 跳转url附加参数udid=uid 便于服务器统计用户浏览记录
 * 附加参数src=noclose src=hasclose  src="" 统计弹窗用户提交线索的类型
 */
@SuppressWarnings("unused") public class VehicleDetailActivity extends Activity {

  /** 已收藏 */
  public static final int COLLECTED = 1;

  public static final int CANCEL_COLLECTION = 0;

  private final static String TAG = "hcVehicleDetail";

  private static final String INTENT_KEY_ID = "keyForId";

  private static String vehicleChannel = "";

  private static final int STATUS_OFFLINE = -1;

  private VehicleDetailActivity mAct;

  @InjectView(R.id.view_loading) View mLoadingView;
  @InjectView(R.id.web_browser) WebView mWebView;
  @InjectView(R.id.view_for_progress) View mProgressView;

  @InjectView(R.id.linear_net_refresh) LinearLayout mNetErrLinear;
  @InjectView(R.id.linear_bottom_parent) LinearLayout mBottomParentLinear;
  @InjectView(R.id.rel_detail_similar) RelativeLayout mSimilarRel;
  @InjectView(R.id.rel_detail_reverse) RelativeLayout mReverseRel;
  @InjectView(R.id.rel_detail_advisory) RelativeLayout mAdvisoryRel;

  @InjectView(R.id.iv_detail_share) ImageView mShareIv;
  @InjectView(R.id.iv_detail_collect) ImageView mCollectIv;
  @InjectView(R.id.iv_detail_diff) ImageView mDiffIv;

  private LinearLayout.LayoutParams mProgParams;

  private LinearLayout mToastView;

  private boolean hasCollected;

  /** 要load的URL链接 */
  private String mLoadURL;
  private String mAskPhone;
  private String vehicleName;

  private HCDetailEntity mDetailEntity;

  private ScanHistoryEntity mHistoryEntity;

  private int mVehicleSourceId;

  private boolean canHandleClick = true;

  /** 分享所需要的四个值 */
  private String share_title;
  private String share_desc;
  private String share_link;
  private String share_image;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_vehicle_detail);
    ButterKnife.inject(this);
    HCEvent.register(this);
    mAct = this;
    initViews();
    initClickListener();
    requestVehicleData();
  }

  private void initClickListener() {
    findViewById(R.id.tv_detail_back).setOnClickListener(mClickListener);
    findViewById(R.id.iv_detail_share).setOnClickListener(mClickListener);
    findViewById(R.id.iv_detail_diff).setOnClickListener(mClickListener);
    findViewById(R.id.iv_detail_collect).setOnClickListener(mClickListener);
    findViewById(R.id.linear_net_refresh).setOnClickListener(mClickListener);
    findViewById(R.id.rel_detail_advisory).setOnClickListener(mClickListener);
    findViewById(R.id.rel_detail_similar).setOnClickListener(mClickListener);
    findViewById(R.id.rel_detail_reverse).setOnClickListener(mClickListener);
  }

  @SuppressLint("SetJavaScriptEnabled") private void initViews() {
    Intent intent = getIntent();
    if (intent.hasExtra(INTENT_KEY_ID)) {
      mVehicleSourceId = intent.getIntExtra(INTENT_KEY_ID, -1);
    }
    mLoadURL = HCUtils.getCarDetailURL(mVehicleSourceId);

    initWebView();

    if (!HCUtils.isNetAvailable()) {
      mNetErrLinear.setVisibility(View.VISIBLE);
      return;
    }
    HCUtils.hideViewIfNeed(mNetErrLinear);
    setupWebProgress();
    mWebView.loadUrl(mLoadURL);
  }

  private void setupWebProgress() {

    if (mWebView == null) return;

    final int sWidth = HCUtils.getScreenWidthInPixels();
    WebChromeClient chromeClient = new WebChromeClient() {
      public void onProgressChanged(WebView view, int progress) {

        //这里怎么会有mProgressView为null的情况
        if (mProgressView == null) return;

        final int width = sWidth * progress / 100;
        if (mProgParams == null) {
          mProgParams = (LayoutParams) mProgressView.getLayoutParams();
          mProgParams.height = HCUtils.getDimenPixels(R.dimen.px_2dp);
          mProgressView.setVisibility(View.VISIBLE);
        }

        if (mProgParams != null && mProgressView != null) {
          mProgParams.width = width;
          mProgressView.setLayoutParams(mProgParams);
          if (progress == 100) {
            mProgressView.setVisibility(View.GONE);
          }
        }
      }
    };
    mWebView.setWebChromeClient(chromeClient);
  }

  private void initWebView() {
    mWebView.getSettings().setDomStorageEnabled(true);
    try {
      mWebView.getSettings().setJavaScriptEnabled(true);
    } catch (Exception e) {
      HCLog.d(TAG, "VehicleDetailActivity setJavaScriptEnabled is crash");
    }
    mWebView.addJavascriptInterface(new GetShareDataInterface(), "getShareData");
    mWebView.setWebViewClient(new WebViewClient() {
      // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("tel:")) {
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
          startActivity(intent);
        } else {
          mLoadURL = url;
          //当前页面跳转时先判断状态。
          if (!HCUtils.isNetAvailable()) {
            mNetErrLinear.setVisibility(View.VISIBLE);
          } else {
            HCUtils.hideViewIfNeed(mNetErrLinear);
            view.loadUrl(url);
          }
        }
        return true;
      }

      @Override public void onPageFinished(WebView view, String url) {
        view.loadUrl("javascript:window.getShareData.OnGetShareData("
            + "document.querySelector('meta[name=\"share_info\"]').getAttribute('content')" + ");");
        super.onPageFinished(view, url);
      }
    });
  }

  private class GetShareDataInterface {
    @JavascriptInterface public void OnGetShareData(String shareData) {
      if (null != shareData) {
        try {
          JSONObject shareJson = new JSONObject(shareData);
          share_desc = shareJson.optString("share_desc");
          share_link = shareJson.optString("share_link");
          share_image = shareJson.optString("share_image");
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private HCViewClickListener mClickListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {
      switch (v.getId()) {
        case R.id.tv_detail_back:// 顶部返回
          if (mWebView.canGoBack()) {
            mWebView.goBack();
          } else {
            finish();
          }
          break;

        case R.id.iv_detail_share: // 顶部分享按钮
          if (mDetailEntity != null) {
            //ShareUtils.share(mAct, share_title, share_desc, share_link, share_image);
            ShareUtils.shareVehicle(mAct, share_title, share_desc, share_image, share_link,
                mDetailEntity, vehicleChannel);
          }
          break;

        case R.id.iv_detail_diff:// 顶部对比
          if (mDetailEntity != null) {
            HCSensorsUtil.vehicleCompare(vehicleChannel, mDetailEntity);
          }
          go2DiffVehicle();
          break;

        case R.id.iv_detail_collect://收藏
          if (mDetailEntity != null) {
            HCSensorsUtil.vehicleCollect(vehicleChannel, mDetailEntity);
          }
          if (!HCUtils.isUserLogined()) {
            Intent intent = new Intent(GlobalData.mContext, LoginActivity.class);
            intent.putExtra(HCConsts.INTENT_KEY_LOGIN_TITLE,
                R.string.hc_action_from_detail_collect);
            intent.putExtra(HCConsts.INTENT_KEY_VEHICLEID, mVehicleSourceId);
            startActivity(intent);
            //统计未登录状态从收藏跳到登录的数据
            HCStatistic.collectToLoginClick();
            return;
          }

          if (HCUtils.isNetAvailable()) {

            if (canHandleClick) {

              if (hasCollected) {
                requestCancel();
              } else {
                requestCollect();
              }

              canHandleClick = false;

              mCollectIv.postDelayed(new Runnable() {
                @Override public void run() {
                  canHandleClick = true;
                }
              }, 1500);
            }
          }
          break;
        case R.id.linear_net_refresh://网络连接异常，刷新
          if (!HCUtils.isNetAvailable()) {
            mNetErrLinear.setVisibility(View.VISIBLE);
            HCUtils.toastNetError();
          } else {
            HCUtils.hideViewIfNeed(mNetErrLinear);
            mLoadingView.setVisibility(View.VISIBLE);
            mWebView.loadUrl(mLoadURL);
            setupWebProgress();
            requestVehicleData();
          }
          break;
        case R.id.rel_detail_advisory:
        case R.id.rel_detail_similar:
          //咨询电话点击事件
          HCUtils.diaPhone(mAskPhone);
          break;
        case R.id.rel_detail_reverse:
          //预约咨询点击事件
          if (!TextUtils.isEmpty(vehicleName) && mVehicleSourceId > 0) {
            Dialog dialog =
                DialogUtils.showReserveCheckVehicleDialog(VehicleDetailActivity.this, vehicleName,
                    mVehicleSourceId);
            if (dialog != null) {
              dialog.show();
            }
          }
          break;
      }
    }
  };

  private void go2DiffVehicle() {
    Intent mIntent = new Intent(GlobalData.mContext, ChooseVehicleForDiffActivity.class);
    Bundle extras = new Bundle();
    extras.putSerializable(HCConsts.INTENT_KEY_SCANENTITY, mHistoryEntity);
    mIntent.putExtras(extras);
    startActivity(mIntent);
  }

  private void requestCollect() {
    if (mCollectIv == null) return;
    mCollectIv.setClickable(false);
    Map<String, Object> params = HCParamsUtil.collectVehicle(mVehicleSourceId);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleCollect(responseJsonString);
      }
    }));
  }

  private void handleCollect(String responseJsonString) {

    if (mCollectIv == null) return;

    mCollectIv.setClickable(true);
    if (!TextUtils.isEmpty(responseJsonString)) {
      boolean isSucc = HCJSONParser.parseCollectResult(responseJsonString);
      if (isSucc) {
        showHcToast(COLLECTED);
        hasCollected = true;
        mCollectIv.setImageResource(R.drawable.icon_collected);
        HCEvent.postEvent(HCEvent.ACTION_COLLECTION_CHANGED);
      }
    }
  }

  private void requestCancel() {
    if (mCollectIv == null) return;
    mCollectIv.setClickable(false);
    Map<String, Object> params = HCParamsUtil.cancelCollectVehicle(mVehicleSourceId);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleCancel(responseJsonString);
      }
    }));
  }

  private void handleCancel(String responseJsonString) {
    if (mCollectIv == null) return;
    mCollectIv.setClickable(true);
    if (!TextUtils.isEmpty(responseJsonString)) {
      boolean isSucc = HCJSONParser.parseCollectResult(responseJsonString);
      if (isSucc) {
        hasCollected = false;
        showHcToast(CANCEL_COLLECTION);
        mCollectIv.setImageResource(R.drawable.icon_un_collected);
        HCEvent.postEvent(HCEvent.ACTION_COLLECTION_CHANGED);
      }
    }
  }

  private void showHcToast(int code) {
    Toast toast = new Toast(GlobalData.mContext);
    toast.setGravity(Gravity.CENTER, 0, 0);

    int res = R.layout.layout_for_collect_toast;
    View toastView = LayoutInflater.from(this).inflate(res, null);

    TextView tv = (TextView) toastView.findViewById(R.id.tv_toast_msg);
    int textRes = code == COLLECTED ? R.string.hc_has_collected : R.string.hc_cancel_collect;
    tv.setText(textRes);

    ImageView iv = (ImageView) toastView.findViewById(R.id.iv_toast_icon);
    int ivRes = code == COLLECTED ? R.drawable.icon_toast_collect : R.drawable.icon_toast_uncollect;
    iv.setImageResource(ivRes);

    toast.setDuration(Toast.LENGTH_SHORT);
    toast.setView(toastView);
    toast.show();
  }

  private void requestVehicleData() {
    Map<String, Object> params = HCParamsUtil.getVehicleStatus(mVehicleSourceId);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String resp) {
        handleVehicleData(resp);
      }
    }));
  }

  private void handleVehicleData(String resp) {
    if (mLoadingView == null) return;

    mLoadingView.setVisibility(View.GONE);

    if (!TextUtils.isEmpty(resp)) {
      HCDetailEntity entity = HCJSONParser.parseVehicleDetail(resp);
      //统计车辆详情页的信息
      HCSensorsUtil.vehicleDetailInfo(vehicleChannel, entity);
      mLoadURL = HCUtils.getCarDetailURL(mVehicleSourceId);
      HCLog.d(TAG, "mLoadURL..." + mLoadURL);
      vehicleName = entity.getVehicle_name();
      mAskPhone = entity.getAsk_phone();
      int status = entity.getStatus();
      if (!TextUtils.isEmpty(mAskPhone)) {
        if (status != STATUS_OFFLINE) {
          //mBottomParentLinear.setVisibility(View.VISIBLE);
          if (HCUtils.isVehicleSold(status)) {
            mSimilarRel.setVisibility(View.VISIBLE);
            mReverseRel.setVisibility(View.GONE);
            mAdvisoryRel.setVisibility(View.GONE);
          } else {
            mSimilarRel.setVisibility(View.GONE);
            mReverseRel.setVisibility(View.VISIBLE);
            mAdvisoryRel.setVisibility(View.VISIBLE);
          }
        }
      }

      mDetailEntity = entity;
      mHistoryEntity = convertToScanHistory(entity);
      mCollectIv.setVisibility(View.VISIBLE);
      mDiffIv.setVisibility(View.VISIBLE);
      share_title = mDetailEntity.getShare_title();
      share_desc = mDetailEntity.getShare_desc();
      share_image = mDetailEntity.getShare_image();
      share_link = mDetailEntity.getShare_link();
      seeIfNeedShowShareBtn();

      int collection_status = entity.getCollection_status();
      if (COLLECTED == collection_status) {
        hasCollected = true;
        //已收藏
        mCollectIv.setImageResource(R.drawable.icon_collected);
      } else {
        //未收藏
        mCollectIv.setImageResource(R.drawable.icon_un_collected);
        hasCollected = false;
      }
    }
  }

  private ScanHistoryEntity convertToScanHistory(HCDetailEntity entity) {
    String id = entity.getId();
    String vehicle_name = entity.getVehicle_name();
    String time = entity.getRegister_time();
    String miles = entity.getMiles();
    String seller_price = entity.getSeller_price();
    String cover_pic = entity.getCover_pic();
    String left_top = entity.getLeft_top();
    String left_top_rate = entity.getLeft_top_rate();
    String left_bottom = entity.getLeft_bottom();
    String left_bottom_rate = entity.getLeft_bottom_rate();

    return new ScanHistoryEntity(id, cover_pic, vehicle_name, time, miles, seller_price, left_top,
        left_top_rate, left_bottom, left_bottom_rate);
  }

  public void onEvent(HCCommunicateEntity entity) {
    if (entity != null && entity.getAction().equals(HCEvent.ACTION_LOGINSTATUS_CHANGED)) {
      if (HCUtils.isUserLogined() && !isFinishing()) {
        requestCollect();
      }
    }
  }

  public static void idToThis(Context context, String curId, String from) {

    if (TextUtils.isEmpty(curId) || !TextUtils.isDigitsOnly(curId)) return;
    vehicleChannel = from;

    Intent mIntent = new Intent(GlobalData.mContext, VehicleDetailActivity.class);
    mIntent.putExtra(INTENT_KEY_ID, Integer.parseInt(curId));
    if (context instanceof Activity) {
      context.startActivity(mIntent);
    } else {
      mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(mIntent);
    }
  }

  @Override protected void onResume() {
    super.onResume();
    ThirdPartInjector.onPageStart(this.getClass().getSimpleName());
    ThirdPartInjector.onResume(this);
    if (mWebView != null) {
      mWebView.onResume();
    }
  }

  @Override protected void onPause() {
    super.onPause();
    ThirdPartInjector.onPageEnd(this.getClass().getSimpleName());
    ThirdPartInjector.onPause(this);
    if (mWebView != null) {
      mWebView.onPause();
    }
  }

  @Override protected void onDestroy() {
    HCEvent.unRegister(this);
    super.onDestroy();
  }

  private void seeIfNeedShowShareBtn() {
    if (mShareIv != null && mDetailEntity != null && !TextUtils.isEmpty(share_title)
        && !TextUtils.isEmpty(share_desc) && !TextUtils.isEmpty(share_image) && !TextUtils.isEmpty(
        share_link)) {
      mShareIv.setVisibility(View.VISIBLE);
    }
  }
}

