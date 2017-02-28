package com.haoche51.buyerapp.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import butterknife.InjectView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.BHCLoginEntity;
import com.haoche51.buyerapp.entity.HCDataIntEntity;
import com.haoche51.buyerapp.entity.HCSyncSubContionEntity;
import com.haoche51.buyerapp.entity.response.RHCCommonEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCEditText;
import com.haoche51.custom.HCViewClickListener;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * 登录
 */
public class LoginActivity extends HCCommonTitleActivity {

  /**
   * 语音请求间隔
   */
  public static final long VOICE_LIMIT = 30 * 1000;

  @InjectView(R.id.et_login_phone) HCEditText mPhoneEt;

  @InjectView(R.id.et_login_verify) HCEditText mVerifyEt;

  @InjectView(R.id.tv_login_verifycode) TextView mGetVcodeTv;

  @InjectView(R.id.tv_login_description) TextView mDescTv;

  @InjectView(R.id.tv_receive_voice) TextView mReceiveVoiceTv;

  private int countTotal = HCConsts.VERIFY_TIME;
  private final static int COUNTWHAT = 0x1021;

  private boolean isForSubscribe = false;

  private int mVehicleSourceId;

  private LoginHandler mHandler = new LoginHandler(this);

  private static class LoginHandler extends Handler {
    private WeakReference<LoginActivity> mWeakAct;

    LoginHandler(LoginActivity mAct) {
      this.mWeakAct = new WeakReference<>(mAct);
    }

    @Override public void handleMessage(Message msg) {
      LoginActivity mAct = mWeakAct.get();
      if (mAct != null && mAct.mGetVcodeTv != null) {
        if (msg.what == COUNTWHAT) {
          mAct.countTotal--;
          if (mAct.countTotal <= 0) {
            mAct.countTotal = HCConsts.VERIFY_TIME;
            mAct.mGetVcodeTv.setText(R.string.hc_get_verify_code);
            mAct.mGetVcodeTv.setTextColor(HCUtils.getResColor(R.color.font_black));
            mAct.mGetVcodeTv.setBackgroundResource(R.drawable.selector_gray_stroke_btn);
            mAct.mGetVcodeTv.setClickable(true);
          } else {
            String str = String.format("重新发送(%s)", mAct.countTotal);
            mAct.mGetVcodeTv.setText(str);
            mAct.mGetVcodeTv.setTextColor(HCUtils.getResColor(R.color.promote_white));
            mAct.mGetVcodeTv.setBackgroundResource(R.drawable.bg_rect_gray);
            mAct.mGetVcodeTv.setClickable(false);
            mAct.countTime();
          }
        }
      }
    }
  }

  @Override void initViews() {
    Class destCls = (Class) getIntent().getSerializableExtra(HCConsts.INTENT_KEY_LOGIN_DEST);

    String hintStr = getIntent().getStringExtra(HCConsts.INTENT_KEY_LOGIN_HINT);
    if (!TextUtils.isEmpty(hintStr)) {
      mDescTv.setText(hintStr);
    }

    if (destCls != null && destCls.equals(MySoldVehiclesActivity.class)) {
      mDescTv.setText(R.string.hc_action_from_sale);
    }

    if (getIntent().hasExtra(HCConsts.INTENT_KEY_LOGIN_TITLE)) {
      int titleRes =
          getIntent().getIntExtra(HCConsts.INTENT_KEY_LOGIN_TITLE, R.string.hc_noregist_tologin);
      mDescTv.setText(titleRes);
    }
    if (getIntent().hasExtra(HCConsts.INTENT_KEY_IS_FOR_LOGIN)) {
      isForSubscribe = true;
    }

    if (getIntent().hasExtra(HCConsts.INTENT_KEY_VEHICLEID)) {
      mVehicleSourceId = getIntent().getIntExtra(HCConsts.INTENT_KEY_VEHICLEID, 0);
    }

    initClickListener();
  }

  private void initClickListener() {
    findViewById(R.id.tv_login_verifycode).setOnClickListener(mClickListener);
    findViewById(R.id.btn_login_login).setOnClickListener(mClickListener);
    findViewById(R.id.tv_receive_voice).setOnClickListener(mClickListener);
  }

  private HCViewClickListener mClickListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {

      switch (v.getId()) {
        case R.id.tv_login_verifycode:// 发送验证码
          if (isInputValid(true)) {

            if (!HCUtils.isNetAvailable()) {
              HCUtils.toastNetError();
              return;
            }
            countTime();
            String phone = mPhoneEt.getText().toString();
            requestVerifyCode(phone);
          }
          break;

        case R.id.btn_login_login:
          if (isInputValid(false)) {

            if (!HCUtils.isNetAvailable()) {
              HCUtils.toastNetError();
              return;
            }

            String phone = mPhoneEt.getText().toString();
            String vcode = mVerifyEt.getText().toString();

            if (!HCUtils.isNetAvailable()) {
              HCUtils.toastNetError();
              return;
            }

            requestLogin(phone, vcode);
          }
          break;

        case R.id.tv_receive_voice: {
          if (isInputValid(true)) {

            if (!HCUtils.isNetAvailable()) {
              HCUtils.toastNetError();
              return;
            }

            String phone = mPhoneEt.getText().toString();

            if (!HCUtils.isNetAvailable()) {
              HCUtils.toastNetError();
              return;
            }

            requestVoiceCode(phone);
          }
          break;
        }
      }
    }
  };

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    titleTv.setText(R.string.hc_login);
  }

  protected void requestVerifyCode(String phone) {
    Map<String, Object> params = HCParamsUtil.sendVerifyCode(phone);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleVerifyCode(responseJsonString);
      }
    }));
  }

  private void handleVerifyCode(String responseJsonString) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      HCDataIntEntity entity = HCJSONParser.parseSendVerify(responseJsonString);
      if (entity != null && mVerifyEt != null) {
        if (0 == entity.getErrno()) {
          HCUtils.showToast(R.string.hc_send_verify_succ);
          mVerifyEt.requestFocus();
          delayVoice();
        } else {
          HCUtils.showToast(entity.getErrmsg());
        }
      }
    }
  }

  private void delayVoice() {
    if (mHandler != null) {
      mHandler.postDelayed(new Runnable() {
        @Override public void run() {
          if (mReceiveVoiceTv != null) {
            HCViewUtils.formatReceiveCode(mReceiveVoiceTv, true);// 验证码请求发送后展示语音验证码区域
            enableReqVoiceCode(true);
          }
        }
      }, VOICE_LIMIT);
    }
  }

  private void requestLogin(final String phone, String vcode) {
    int user_id = HCUtils.getUserDeviceId();

    Map<String, Object> params = HCParamsUtil.userLogin(phone, vcode, user_id);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleLogin(responseJsonString, phone);
      }
    }));

    DialogUtils.showProgress(this);
  }

  private void handleLogin(String responseJsonString, String phone) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      BHCLoginEntity entity = HCJSONParser.parseLogin(responseJsonString);
      if (entity == null) return;
      if (0 == entity.getErrno()) {
        HCSpUtils.setUserPhone(phone);
        //HCSensorsUtil.setUserPhoneOnceProperties();
        //HCSensorsUtil.setUserPhoneProperties(phone);
        HCSensorsUtil.login();

        if (entity.getData() != null) {
          String uid = entity.getData().getUid();
          HCSpUtils.setUserUid(uid);
        }
        if (mVehicleSourceId > 0) {
          //统计从收藏过来，并且登录的成功数
          HCStatistic.loginForCollectClick();
          HCEvent.postEvent(HCEvent.ACTION_LOGINSTATUS_CHANGED);
          HCUtils.hideKeyboard(mVerifyEt);
          DialogUtils.dismissProgress();
          finish();
        } else {
          requestSubscribe();
        }
      } else {
        HCUtils.showToast(entity.getErrmsg());
        DialogUtils.dismissProgress();
      }
    } else {
      HCUtils.toastNetError();
      DialogUtils.dismissProgress();
    }
  }

  private void doFinish() {
    if (getIntent().hasExtra(HCConsts.INTENT_KEY_URL)) {
      // 表示从登陆过来
      Intent intent = getIntent();
      intent.setClass(GlobalData.mContext, WebBrowserActivity.class);
      String url = intent.getStringExtra(HCConsts.INTENT_KEY_URL);
      url += "&phone=" + HCSpUtils.getUserPhone();
      url += "&uid=" + HCSpUtils.getUserUid();
      intent.putExtra(HCConsts.INTENT_KEY_URL, url);
      startActivity(intent);
    } else if (getIntent().hasExtra(HCConsts.INTENT_KEY_LOGIN_DEST)) {//其他需要登陆后再到目的地
      Class destCls = (Class) getIntent().getSerializableExtra(HCConsts.INTENT_KEY_LOGIN_DEST);
      Intent intent = new Intent(this, destCls);
      startActivity(intent);
    } else if (getIntent().hasExtra(HCConsts.INTENT_LOGIN_FOR_COLL_OR_SUB)) {
      String action = getIntent().getStringExtra(HCConsts.INTENT_LOGIN_FOR_COLL_OR_SUB);
      HCEvent.postEvent(action);
    } else {
      setResult(RESULT_OK);
    }

    HCUtils.hideKeyboard(mVerifyEt);

    finish();
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_login;
  }

  // 校验输入合法性
  private boolean isInputValid(boolean justMobile) {
    boolean result = false;
    Animation shakeAnim = HCViewUtils.getShakeAnim();
    String phone = mPhoneEt.getText().toString();
    if (!HCUtils.isPhoneNumberValid(phone)) {
      findViewById(R.id.rel_login_phone).startAnimation(shakeAnim);
      mPhoneEt.requestFocus();
      return false;
    }
    if (justMobile) {
      return true;
    }
    String pass = mVerifyEt.getText().toString();
    if (TextUtils.isEmpty(pass)) {
      findViewById(R.id.linear_login_verify).startAnimation(shakeAnim);
      mVerifyEt.requestFocus();
    } else {
      result = true;
    }
    return result;
  }

  private void countTime() {
    int delay = countTotal == HCConsts.VERIFY_TIME ? 0 : 1000;
    mHandler.sendMessageDelayed(mHandler.obtainMessage(COUNTWHAT), delay);
  }

  @Override protected void onDestroy() {
    mHandler.removeCallbacksAndMessages(null);
    mHandler = null;
    super.onDestroy();
  }

  private void requestSubscribe() {
    Map<String, Object> params = HCParamsUtil.getUserAllSubscribesCondition();
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleSubscribe(responseJsonString);
      }
    }));
  }

  private void handleSubscribe(String responseJsonString) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      HCSyncSubContionEntity entity = HCJSONParser.parseSyncSubCondition(responseJsonString);
      if (entity != null) {
        HCSpUtils.setAllSubscribe(entity.getData());
        HCEvent.postEvent(HCEvent.ACTION_LOGINSTATUS_CHANGED);
        if (isForSubscribe) {
          startActivity(new Intent(this, MySubscribeVehiclesActivity.class));
        }
      }
      DialogUtils.dismissProgress();
      doFinish();
    }
  }

  protected void requestVoiceCode(String phone) {
    Map<String, Object> params = HCParamsUtil.getVoiceCode(phone);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleVoiceCode(responseJsonString);
      }
    }));
  }

  private void handleVoiceCode(String responseJsonString) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      RHCCommonEntity entity = HCJSONParser.parseCommon(responseJsonString);
      if (entity != null && mVerifyEt != null) {
        if (0 == entity.getErrno()) {
          HCUtils.showToast(R.string.hc_send_voice_req_succ);
          mVerifyEt.requestFocus();
          enableReqVoiceCode(false);
        } else {
          HCUtils.showToast(entity.getErrmsg());
        }
      }
    }
  }

  @Override protected void onPause() {
    super.onPause();
    doThirdPartyPause();
    if (mVerifyEt != null) {
      HCUtils.hideKeyboard(mVerifyEt);
    }
  }

  @Override protected void onResume() {
    super.onResume();
    doThirdPartyResume();
  }

  /**
   * 设置语音验证码请求
   */
  protected void enableReqVoiceCode(boolean enable) {
    if (mReceiveVoiceTv != null) {
      mReceiveVoiceTv.setVisibility(View.VISIBLE);
      HCViewUtils.formatReceiveCode(mReceiveVoiceTv, enable);
      mReceiveVoiceTv.setEnabled(enable);
    }
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
