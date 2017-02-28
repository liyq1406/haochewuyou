package com.haoche51.buyerapp.entity;

public class BHCLoginEntity {
    private int errno;
    private String errmsg;
    private HCLoginEntity data;

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

    public HCLoginEntity getData() {
        return data;
    }

    public void setData(HCLoginEntity data) {
        this.data = data;
    }
}
