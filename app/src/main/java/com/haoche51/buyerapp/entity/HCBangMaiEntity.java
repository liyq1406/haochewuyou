package com.haoche51.buyerapp.entity;

public class HCBangMaiEntity {

  private int errno;
  private String errmsg;
  private DataEntity data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public void setData(DataEntity data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public DataEntity getData() {
    return data;
  }

  public static class DataEntity {
    private String id;

    public void setId(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }
  }
}
