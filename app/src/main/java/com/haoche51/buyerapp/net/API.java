package com.haoche51.buyerapp.net;

import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCUtils;
import org.apache.http.Header;

public class API {

  public static final String TAG = "tagForHCAPI";

  public static void post(final HCRequest request) {
    AppNetServer.getInstance().post(request);
  }

  public static void get(final String url) {

    if (!HCUtils.isNetAvailable()) {
      //会不会需要在这里存储所有没有发送的url.当网络恢复时重新发送
      return;
    }

    AppNetServer.getHttpClient().get(url, new HCBaseAsyncHttpResponseHandler() {
      @Override public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        super.onSuccess(statusCode, headers, responseBody);
        HCLog.d(TAG, url + " ,   onSuccess ");
      }

      @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
          Throwable error) {
        super.onFailure(statusCode, headers, responseBody, error);
        HCLog.d(TAG, url + " ,   onnFailure ");
      }

      @Override public void onRetry(int retryNo) {
        super.onRetry(retryNo);
        HCLog.d(TAG, "retry " + url + ",  times =  " + retryNo);
      }
    });
  }
}
