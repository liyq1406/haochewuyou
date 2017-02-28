package com.haoche51.buyerapp.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;

/***
 * 设置
 */
public class SettingActivity extends HCCommonTitleActivity {

  //public static final String QIHOO_KEY = "UpdateInfo";
  private RelativeLayout mLogoutRel;
  private TextView mVersionTv;
  private ProgressBar mProgressBar;

  private View.OnClickListener mClickListener = new View.OnClickListener() {

    @Override public void onClick(View v) {
      switch (v.getId()) {
        case R.id.rel_setting_logout:// 退出登录
          HCUtils.logout();
          mLogoutRel.setVisibility(View.GONE);
          HCEvent.postEvent(HCEvent.ACTION_LOGINSTATUS_CHANGED);
          finish();
          break;

        case R.id.rel_setting_update:// 系统更新
          mProgressBar.setVisibility(View.VISIBLE);
          mVersionTv.setVisibility(View.GONE);
          doAppUpdata();
          break;

        case R.id.rel_setting_feedback://意见反馈
          HCSpUtils.setProfileFeedbackReminder(0);
          startActivity(new Intent(GlobalData.mContext, FeedBackActivity.class));
          break;

        case R.id.rel_setting_evaluate://评价
          DialogUtils.showEvaluateDialog(SettingActivity.this);
          break;
      }
    }
  };

  private void doAppUpdata() {
    doBaiDuUpdate();
    //doXiaoMiUpdate();
    //do360Update();
  }

  private void doBaiDuUpdate() {
    BDAutoUpdateSDK.cpUpdateCheck(SettingActivity.this, new CPCheckUpdateCallback() {
      @Override public void onCheckUpdateCallback(AppUpdateInfo appUpdateInfo,
          AppUpdateInfoForInstall appUpdateInfoForInstall) {
        if (appUpdateInfo == null) {
          setVersionName();
          HCUtils.showToast(R.string.hc_already_newest);
        } else {
          BDAutoUpdateSDK.uiUpdateAction(SettingActivity.this, new UICheckUpdateCallback() {
            @Override public void onCheckComplete() {
            }
          });
        }
        mProgressBar.setVisibility(View.GONE);
        mVersionTv.setVisibility(View.VISIBLE);
      }
    });
  }

  //private void doXiaoMiUpdate() {
  //  XiaomiUpdateAgent.setUpdateAutoPopup(false);
  //  XiaomiUpdateAgent.setUpdateListener(new XiaomiUpdateListener() {
  //    @Override public void onUpdateReturned(int i, UpdateResponse updateResponse) {
  //      mProgressBar.setVisibility(View.GONE);
  //      mVersionTv.setVisibility(View.VISIBLE);
  //      if (i == UpdateStatus.STATUS_UPDATE) {
  //        XiaomiUpdateAgent.arrange();
  //      } else if (i == UpdateStatus.STATUS_NO_UPDATE) {
  //        setVersionName();
  //        HCUtils.showToast(R.string.hc_already_newest);
  //      } else {
  //        HCUtils.showToast(R.string.hc_check_update_error);
  //      }
  //    }
  //  });
  //  XiaomiUpdateAgent.update(this);
  //}

  //private void do360Update() {
  //  UpdateManager.checkUpdate(SettingActivity.this, true, true, HCUtils.getPackageName(),
  //      new UpdateManager.CheckUpdateListener() {
  //        @Override public void onResult(boolean b, Bundle bundle) {
  //
  //          if (mProgressBar != null) {
  //            SettingActivity.this.runOnUiThread(new Runnable() {
  //              @Override public void run() {
  //                mProgressBar.setVisibility(View.GONE);
  //                mVersionTv.setVisibility(View.VISIBLE);
  //              }
  //            });
  //          }
  //
  //          if (!b) {
  //            setVersionName();
  //            HCUtils.showToast(R.string.hc_already_newest);
  //          } else {
  //            if (bundle != null) {
  //              Parcelable p = bundle.getParcelable(QIHOO_KEY);
  //              if (p != null) {
  //
  //                if (p instanceof AppInfo) {
  //                  AppInfo info = (AppInfo) p;
  //                  long vc = info.versionCode;
  //                  if (vc <= HCUtils.getAppVersionCode()) {
  //                    setVersionName();
  //                    HCUtils.showToast(R.string.hc_already_newest);
  //                    return;
  //                  }
  //                }
  //
  //                Intent mIntent = new Intent(SettingActivity.this, CheckUpdateAcitivty.class);
  //                Bundle extras = new Bundle();
  //                extras.putParcelable(QIHOO_KEY, p);
  //                mIntent.putExtras(extras);
  //                startActivity(mIntent);
  //              }
  //            }
  //          }
  //        }
  //      });
  //}

  @Override void initViews() {
    mLogoutRel = (RelativeLayout) findViewById(R.id.rel_setting_logout);
    if (HCUtils.isUserLogined()) {
      mLogoutRel.setVisibility(View.VISIBLE);
      mLogoutRel.setOnClickListener(mClickListener);
    }

    mVersionTv = (TextView) findViewById(R.id.tv_setting_version);
    mProgressBar = (ProgressBar) findViewById(R.id.pb_setting);
    findViewById(R.id.rel_setting_update).setOnClickListener(mClickListener);
    findViewById(R.id.rel_setting_feedback).setOnClickListener(mClickListener);
    findViewById(R.id.rel_setting_evaluate).setOnClickListener(mClickListener);

    requestUpdate();
  }

  private void requestUpdate() {
    requestBaiDuUpdate();
    //requestXiaoMiUpdate();
    //request360Update();
  }

  private void requestBaiDuUpdate() {
    BDAutoUpdateSDK.cpUpdateCheck(SettingActivity.this, new CPCheckUpdateCallback() {
      @Override public void onCheckUpdateCallback(AppUpdateInfo appUpdateInfo,
          AppUpdateInfoForInstall appUpdateInfoForInstall) {
        if (appUpdateInfo == null) {
          setVersionName();
        }
      }
    });
  }

  //private void requestXiaoMiUpdate() {
  //  XiaomiUpdateAgent.setUpdateAutoPopup(false);
  //  XiaomiUpdateAgent.setUpdateListener(new XiaomiUpdateListener() {
  //    @Override public void onUpdateReturned(int i, UpdateResponse updateResponse) {
  //      if (i != UpdateStatus.STATUS_UPDATE) {
  //        setVersionName();
  //      }
  //    }
  //  });
  //  XiaomiUpdateAgent.update(this);
  //}

  //private void request360Update() {
  //  UpdateManager.checkUpdate(this, false, false, HCUtils.getPackageName(),
  //      new UpdateManager.CheckUpdateListener() {
  //        @Override public void onResult(boolean b, Bundle bundle) {
  //          if (!b) {
  //            setVersionName();
  //          }
  //        }
  //      });
  //}

  private void setVersionName() {
    if (mVersionTv != null) {
      mVersionTv.post(new Runnable() {
        @Override public void run() {
          String vname = HCUtils.getAppVersionName();
          if (!TextUtils.isEmpty(vname)) {
            mVersionTv.setText(vname);
          }
        }
      });
    }
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    titleTv.setText(R.string.hc_setting);
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_setting;
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
