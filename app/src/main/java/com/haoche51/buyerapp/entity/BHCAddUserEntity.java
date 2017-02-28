package com.haoche51.buyerapp.entity;

public class BHCAddUserEntity {
    private int errno;
    private String errmsg;
    private HCAddUserEntity data;

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

    public HCAddUserEntity getData() {
        return data;
    }

    public void setData(HCAddUserEntity data) {
        this.data = data;
    }
}
