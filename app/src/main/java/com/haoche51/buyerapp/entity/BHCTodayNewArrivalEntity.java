package com.haoche51.buyerapp.entity;

public class BHCTodayNewArrivalEntity {

  private int errno;
  private String errmsg;

  private HCTodayNewArrivalEntity data;

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

  public HCTodayNewArrivalEntity getData() {
    return data;
  }

  public void setData(HCTodayNewArrivalEntity data) {
    this.data = data;
  }
}
