package com.haoche51.buyerapp.entity;

public class BHCRecommandEntity {

  private int errno;
  private String errmsg;
  private HCRecommandEntity data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public void setData(HCRecommandEntity data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public HCRecommandEntity getData() {
    return data;
  }
}
