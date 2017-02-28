package com.haoche51.buyerapp.entity;

/**
 * 对应action: get_vehicle_source_by_id
 */
public class BHCDetailEntity {

  /**
   * errno : 0
   * errmsg : ok
   * data : {"id":"9174","brand_name":"雷克萨斯","class_name":"雷克萨斯IS","miles":"0.2","geerbox_type":"4","register_month":"11","register_year":"2014","collection_status":0}
   */

  private int errno;
  private String errmsg;
  /**
   * id : 9174
   * brand_name : 雷克萨斯
   * class_name : 雷克萨斯IS
   * miles : 0.2
   * geerbox_type : 4
   * register_month : 11
   * register_year : 2014
   * collection_status : 0
   */

  private HCDetailEntity data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public void setData(HCDetailEntity data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public HCDetailEntity getData() {
    return data;
  }
}
