package com.haoche51.buyerapp.entity;

public class BHCHomeDialogDataEntity {

  private int errno;
  private String errmsg;
  private HCHomeDialogDataEntity data;

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

  public HCHomeDialogDataEntity getData() {
    return data;
  }

  public void setData(HCHomeDialogDataEntity data) {
    this.data = data;
  }
}
