package com.haoche51.buyerapp.entity;

public class HCMoreFilterEntity {

  private int errno;
  private String errmsg;
  private HCMoreFilterItemEntity data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public void setData(HCMoreFilterItemEntity data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public HCMoreFilterItemEntity getData() {
    return data;
  }
}
