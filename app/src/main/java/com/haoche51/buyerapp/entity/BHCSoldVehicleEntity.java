package com.haoche51.buyerapp.entity;

/**
 * 我出售的车
 * action: get_my_vehicle
 */
public class BHCSoldVehicleEntity {

  /**
   * errno : 0
   * errmsg : ok
   * data : {"vehicle_info":[{"vehicle_source_id":11477,"vehicle_name":"大众 速腾 2009款 1.6L 自动舒适型","cover_image":"http://192.168.1.106:9999/66fd0f26c427184cf1866680998b25314e1e1e37.jpg","correct_text":"如您发现车款不符，可联系我们进行纠正>>","correct_phone":"010-53605923","seller_price":"0","eval_price":"0","adjust_text":"","adjust_phone":"","buyer_count":2,"buyer_list":[{"text":"151****7204的客户其他","create_time":""},{"text":"185****3693的客户想了解您的低价是多少","create_time":""}],"offline_phone":"010-53605923","offline_text":"已自己出售，申请下线。","online_text":"2015/07/14您的爱车成功上线","sell_text":"","sell_vehicle_text":"我还有车要出售>>","suggest_text":"按照好车无忧估价，您的报价合理。","status":3,"status_text":"在售"},{"vehicle_source_id":11105,"vehicle_name":"2013款 英菲尼迪QX50进口 3.7L 四驱版","cover_image":"http://192.168.1.106:9999/2093e9ee45f8036cd005924029c90c43a626df15.jpg","correct_text":"如您发现车款不符，可联系我们进行纠正>>","correct_phone":"010-53605923","seller_price":"20","eval_price":"0","adjust_text":"","adjust_phone":"","buyer_count":7,"buyer_list":[{"text":"138****1143的客户其他","create_time":""},{"text":"138****1134的客户想现场去看下您的车","create_time":""},{"text":"138****1154的客户想现场去看下您的车","create_time":""},{"text":"138****1145的客户想现场去看下您的车","create_time":""},{"text":"152****7670的客户想了解您的低价是多少","create_time":""},{"text":"188****3062的客户电话给销售询问了您的爱车","create_time":""},{"text":"139****3234的客户电话给销售询问了您的爱车","create_time":""}],"offline_phone":"010-53605923","offline_text":"已自己出售，申请下线。","online_text":"2015/02/03您的爱车成功上线","sell_text":"","sell_vehicle_text":"我还有车要出售>>","suggest_text":"按照好车无忧估价，您的报价合理。","status":3,"status_text":"在售"}]}
   */

  private int errno;
  private String errmsg;
  private HCSoldVehicleEntity data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public void setData(HCSoldVehicleEntity data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public HCSoldVehicleEntity getData() {
    return data;
  }
}
