package com.haoche51.buyerapp.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import butterknife.InjectView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.WebBrowserActivity;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCViewClickListener;

public class ForumFragment extends HCBaseFragment {

  @InjectView(R.id.view_loading) View mLoadingView;
  @InjectView(R.id.wv_forum) WebView mWebView;
  @InjectView(R.id.linear_net_refresh) LinearLayout mNetErrLinear;

  private final String URL = "http://bbs.haoche51.com/?channel=android";
  private final static String HTTP = "http";
  private boolean isFirstToLoadUrl = true;

  private HCViewClickListener mClickListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {
      if (mWebView != null) {
        mLoadingView.setVisibility(View.VISIBLE);
        if (!HCUtils.isNetAvailable()) {
          HCUtils.hideViewIfNeed(mLoadingView);
          mNetErrLinear.setVisibility(View.VISIBLE);
          HCUtils.toastNetError();
          return;
        }
        HCUtils.hideViewIfNeed(mNetErrLinear);
        mWebView.loadUrl(URL);
      }
    }
  };

  @Override boolean isNeedBindEventBus() {
    return false;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_forum;
  }

  @Override void doInitViewOrData() {
    initWebView();
    initViews();
  }

  private void initViews() {
    mNetErrLinear.setOnClickListener(mClickListener);

    //加载前判断网络状态
    if (!HCUtils.isNetAvailable()) {
      HCUtils.hideViewIfNeed(mLoadingView);
      mNetErrLinear.setVisibility(View.VISIBLE);
      HCUtils.toastNetError();
      return;
    }
    HCUtils.hideViewIfNeed(mNetErrLinear);
    mWebView.loadUrl(URL);
  }

  private void initWebView() {
    mWebView.getSettings().setDomStorageEnabled(true);
    mWebView.getSettings().setAllowFileAccess(true);
    try {
      mWebView.getSettings().setJavaScriptEnabled(true);
    } catch (Exception e) {
    }
    mWebView.getSettings().setBuiltInZoomControls(true);
    mWebView.getSettings().setSupportZoom(true);
    mWebView.getSettings().setUseWideViewPort(true);
    mWebView.getSettings().setLoadWithOverviewMode(true);
    mWebView.setWebViewClient(new WebViewClient() {
      @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (isFirstToLoadUrl) {//第一次在本页面加载
          view.loadUrl(url);
        } else {//以后的连接跳到WebBrowserActivity中处理
          if (!TextUtils.isEmpty(url)) {
            url = url.trim();
            if (url.startsWith(HTTP)) {
              Intent mIntent = new Intent(GlobalData.mContext, WebBrowserActivity.class);
              mIntent.putExtra(HCConsts.INTENT_KEY_URL, url);
              mIntent.putExtra(HCConsts.INTENT_KEY_FORUM, url);
              getActivity().startActivity(mIntent);
            }
          }
        }
        return true;
      }
    });
    mWebView.setWebChromeClient(new WebChromeClient() {
      @Override public void onProgressChanged(WebView view, int newProgress) {
        if (mLoadingView == null) return;
        if (newProgress == 100) {
          HCUtils.hideViewIfNeed(mLoadingView);
          //加载完成置为false
          isFirstToLoadUrl = false;
        }
      }
    });
  }

  @Override public void onEvent(HCCommunicateEntity entity) {
  }

  @Override public void onResume() {
    super.onResume();
    ThirdPartInjector.onPageStart(this.getClass().getSimpleName());
  }

  @Override public void onPause() {
    super.onPause();
    ThirdPartInjector.onPageEnd(this.getClass().getSimpleName());
  }
}
