package com.haoche51.buyerapp.entity;

import java.util.List;

public class BHCCompareVehicleEntity {
    private int errno;
    private String errmsg;
    private List<HCCompareVehicleEntity> data;

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

    public List<HCCompareVehicleEntity> getData() {
        return data;
    }

    public void setData(List<HCCompareVehicleEntity> data) {
        this.data = data;
    }
}
