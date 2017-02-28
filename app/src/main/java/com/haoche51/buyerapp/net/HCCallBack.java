package com.haoche51.buyerapp.net;

import org.apache.http.Header;

public interface HCCallBack {
    void onStart();

    void onSuccess(int statusCode, Header[] headers, byte[] responseBody);

    void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error);

    void onRetry(int retryNo);

    void onProgress(int bytesWritten, int totalSize);

    void onFinish();

    void onHttpFinish(String responseJsonString);
}
