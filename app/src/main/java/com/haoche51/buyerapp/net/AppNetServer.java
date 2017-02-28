package com.haoche51.buyerapp.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haoche51.buyerapp.BuildConfig;
import com.haoche51.buyerapp.HCDebug;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;

public class AppNetServer {

  private static String TAG = "AppNetServer";

  private static final String SERVER_URL = HCDebug.APP_SERVER;

  private static final String APP_TOKEN = "haoche51@572";
  private static final String REQUEST_KEY = "req";
  private static final String ACTIOIN_KEY = "action";
  private static final String PARAMS_KEY = "params";
  private static final String MSG_KEY = "msg";
  private static final String TOKEN_KEY = "token";
  private static final String OTHER_KEY = "other";
  private static final int TIMEOUT_TIME = 5000;

  private static AsyncHttpClient mInstance;
  private static AppNetServer mAppNetServer;

  private AppNetServer() {
    if (mInstance == null) {
      synchronized (AsyncHttpClient.class) {
        if (mInstance == null) {
          mInstance = new AsyncHttpClient();
          // 设置连接超时和重连次数
          mInstance.setMaxRetriesAndTimeout(2, TIMEOUT_TIME);
          mInstance.setTimeout(TIMEOUT_TIME);
        }
      }
    }
  }

  public static AppNetServer getInstance() {
    if (mAppNetServer == null) {
      mAppNetServer = new AppNetServer();
    }
    return mAppNetServer;
  }

  public static AsyncHttpClient getHttpClient() {
    return mInstance;
  }

  /***
   * Map<String,Object> params --> RequestParams
   */
  private RequestParams generateRequestParam(Map<String, Object> inputParams) {
    if (!inputParams.containsKey(ACTIOIN_KEY)) {
      throw new RuntimeException("action not find ");
    }
    //取出action
    String actionValue = (String) inputParams.remove(ACTIOIN_KEY);

    //action params 层
    Map<String, Object> first = new HashMap<>();
    first.put(ACTIOIN_KEY, actionValue);
    first.put(PARAMS_KEY, inputParams);

    //第二层
    Map<String, Object> second = new HashMap<>();
    second.put(MSG_KEY, first);
    second.put(TOKEN_KEY, APP_TOKEN);

    //统计相关
    Map<String, Object> other = new HashMap<>();
    other.put("udid", HCUtils.getUserDeviceId());
    other.put("a_v", HCUtils.getAppVersionName());
    other.put("s_v", HCUtils.getOSVersion());
    other.put("r_w", HCUtils.getScreenWidthInPixels());
    other.put("r_h", HCUtils.getScreenHeightPixels());
    other.put("p", 1);

    if (HCUtils.isXiaoMiChannel()) {
      other.put("mi_client_id", HCSpUtils.getXMClientId());
    } else {
      other.put("bd_client_id", HCSpUtils.getBDChannelId());
      other.put("bd_user_id", HCSpUtils.getBDUserId());
    }
    other.put("promote_id", HCUtils.getCurrentChannel());
    other.put("net", HCUtils.getNetworkType());

    if (!BuildConfig.DEBUG) {
      second.put(OTHER_KEY, other);
    }

    GsonBuilder gb = new GsonBuilder().disableHtmlEscaping();
    Gson gson = gb.create();
    RequestParams loopjParams = new RequestParams();
    String loopReqStr = gson.toJson(second);
    loopjParams.put(REQUEST_KEY, loopReqStr);
    return loopjParams;
  }

  public void post(final HCRequest request) {
    final RequestParams params = generateRequestParam(request.params);
    try {

      mInstance.post(SERVER_URL, params, new HCBaseAsyncHttpResponseHandler() {
        @Override public void onStart() {
          super.onStart();
          request.callback.onStart();
        }

        @Override public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
          super.onSuccess(statusCode, headers, responseBody);
          try {
            if (responseBody != null) {
              request.callback.onSuccess(statusCode, headers, responseBody);
              String resp = new String(responseBody);
              setResponseJson(resp);
              String strReq = params.toString();
              strReq = HCDebug.APP_SERVER + "?" + strReq;
              HCLog.net(strReq + "\n\n\n\n");
            } else {
              HCLog.net("responseBody null \n\n" + params.toString() + "\n\n");
            }
          } catch (Throwable e) {
            request.callback.onFinish();
            request.callback.onHttpFinish(getResponseJson());
          }
        }

        @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
            Throwable error) {
          try {
            HCLog.net("onFailure  statusCode = " + statusCode + "\n" + params.toString() + "\n\n");
            request.callback.onFailure(statusCode, headers, responseBody, error);
          } catch (Throwable e) {
            request.callback.onFinish();
            request.callback.onHttpFinish(getResponseJson());
          }
        }

        @Override public void onFinish() {
          super.onFinish();
          request.callback.onFinish();
          request.callback.onHttpFinish(getResponseJson());
        }
      });
    } catch (Exception e) {
      HCLog.log("AyncHttpClient Exception now!!! " + e.getCause() + ",\n\n stackTrace:\n"
          + e.getStackTrace().toString());
      request.callback.onHttpFinish("");
    }
  }
}