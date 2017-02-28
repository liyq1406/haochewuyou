package com.haoche51.buyerapp.entity;

import java.util.List;

/**
 * 优惠券列表实体
 */
public class BHCCouponEntity {
    private int errno;
    private String errmsg;
    private List<HCCouponEntity> data;

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

    public List<HCCouponEntity> getData() {
        return data;
    }

    public void setData(List<HCCouponEntity> data) {
        this.data = data;
    }
}
