package com.haoche51.buyerapp.entity;

import java.util.List;

public class HCBSubcribeEntity {

    /**
     * errno : 0 data : {"sub_id":61} errmsg : 操作成功
     */
    private int errno;
    private List<HCSubcribeEntity> data;
    private String errmsg;

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public List<HCSubcribeEntity> getData() {
        return data;
    }

    public void setData(List<HCSubcribeEntity> data) {
        this.data = data;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getErrno() {
        return errno;
    }

    public String getErrmsg() {
        return errmsg;
    }
}