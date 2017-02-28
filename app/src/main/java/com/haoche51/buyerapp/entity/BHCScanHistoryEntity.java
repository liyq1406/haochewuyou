package com.haoche51.buyerapp.entity;

public class BHCScanHistoryEntity {

  private int errno;
  private String errmsg;
  private HCScanHistoryEntity data;

  public void setErrno(int errno) {
    this.errno = errno;
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

  public HCScanHistoryEntity getData() {
    return data;
  }

  public void setData(HCScanHistoryEntity data) {
    this.data = data;
  }
}
