package com.haoche51.buyerapp.entity;

public class BHCNearestCityEntity {
    private int errno;
    private String errmsg;
    private HCCityEntity data;

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

    public HCCityEntity getData() {
        return data;
    }

    public void setData(HCCityEntity data) {
        this.data = data;
    }
}
