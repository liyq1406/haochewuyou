package com.haoche51.buyerapp.entity;

public class HCBSubscribeIdEntity {

    /**
     * errno : 0
     * data : {"sub_id":294}
     * errmsg : 操作成功
     */
    private int errno;
    private HCSubcribeEntity data;
    private String errmsg;

    public HCSubcribeEntity getData() {
        return data;
    }

    public void setData(HCSubcribeEntity data) {
        this.data = data;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }
}
