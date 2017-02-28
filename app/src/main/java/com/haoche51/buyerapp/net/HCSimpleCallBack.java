package com.haoche51.buyerapp.net;

import org.apache.http.Header;


public abstract class HCSimpleCallBack implements HCCallBack {

    @Override
    public void onStart() {
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
    }

    @Override
    public void onRetry(int retryNo) {
    }

    @Override
    public void onProgress(int bytesWritten, int totalSize) {
    }

    @Override
    public void onFinish() {

    }

    @Override
    public abstract void onHttpFinish(String responseJsonString);

}
