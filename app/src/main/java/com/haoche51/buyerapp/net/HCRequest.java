package com.haoche51.buyerapp.net;

import java.util.Map;


public class HCRequest {
    public HCCallBack callback;
    public Map<String, Object> params;


    public HCRequest(Map<String, Object> params, HCCallBack callback) {
        this.callback = callback;
        this.params = params;
    }
}
