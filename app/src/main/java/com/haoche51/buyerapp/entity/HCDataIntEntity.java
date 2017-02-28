package com.haoche51.buyerapp.entity;

/***
 * 解析这种格式的json
 * {"errno":0,"errmsg":"ok","data":0}
 */
public class HCDataIntEntity {
    private int errno;
    private String errmsg;
    private int data;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
