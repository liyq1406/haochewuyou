package com.haoche51.buyerapp.entity;


import java.util.List;

public class BHCBrandEntity {
  private int errno;
  private String errmsg;
  private List<HCBrandEntity> data;

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

  public List<HCBrandEntity> getData() {
    return data;
  }

  public void setData(List<HCBrandEntity> data) {
    this.data = data;
  }
}
