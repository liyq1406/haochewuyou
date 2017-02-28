package com.haoche51.buyerapp.entity;

/**
 * action: my_data
 */
public class BHCProfileCountsEntity {
  private int errno;
  private String errmsg;
  private HCProfileCountsEntity data;

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

  public HCProfileCountsEntity getData() {
    return data;
  }

  public void setData(HCProfileCountsEntity data) {
    this.data = data;
  }
}
