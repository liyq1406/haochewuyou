package com.haoche51.buyerapp.entity;

public class BHCSplashEntity {

  /**
   * errno : 0
   * errmsg : ok
   * data : {"id":3,"redirect":"http://m.haoche51.com/bj/activity/temai?channel=app","image_url":"http://image1.haoche51.com/255d8fd476f-95a0-4712-94af-cb8381dbb42c.jpg","show_time":5}
   */

  private int errno;
  private String errmsg;
  /**
   * id : 3
   * redirect : http://m.haoche51.com/bj/activity/temai?channel=app
   * image_url : http://image1.haoche51.com/255d8fd476f-95a0-4712-94af-cb8381dbb42c.jpg
   * show_time : 5
   */

  private HCSplashEntity data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public void setData(HCSplashEntity data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public HCSplashEntity getData() {
    return data;
  }
}
