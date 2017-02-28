package com.haoche51.buyerapp.net;

import android.text.TextUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

public abstract class HCBaseAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

    private String responseJson = "" ;

    public String getResponseJson() {
        return TextUtils.isEmpty(responseJson) ? "" : responseJson;
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }

    public HCBaseAsyncHttpResponseHandler() {
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

    }
}
