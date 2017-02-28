package com.haoche51.buyerapp.entity;

/**
 * action:collection_list
 */
public class BHCVehicleListEntity {

  /**
   * errno : 0
   * errmsg : ok
   * data : {"vehicle_list":[{"id":"8315","vehicle_name":"绅宝D70 2013款 2.0T
   * 舒适版","register_year":"2014","register_month":"06","gearbox":"4","seller_price":"11","miles":"0.45","online_time":"1416902689","cut_price":"0.2","city":"12","status":"3","class_name":"绅宝D70","brand_name":"绅宝","refresh_time":"1418280500","cover_image_url":"http://image1.haoche51.com/49de5c9d55a093eca0ab15400459c7000dfea56e.jpg","dealer_price":"9.23","quoted_price":"16.48","suitable":0},{"id":"7709","vehicle_name":"大众Eos
   * 2011款 2.0TSI","register_year":"2014","register_month":"06","gearbox":"3","seller_price":"30","miles":"0.65","online_time":"1415949017","cut_price":"0","city":"12","status":"3","class_name":"大众Eos","brand_name":"大众","refresh_time":"1415949017","cover_image_url":"http://image1.haoche51.com/045b239d518a4d355e950a1e748565ebebee4941.jpg","dealer_price":"27.06","quoted_price":"50.51","suitable":0},{"id":"9235","vehicle_name":"艾力绅
   * 2012款 2.4L VTi-S尊贵版","register_year":"2014","register_month":"07","gearbox":"2","seller_price":"29","miles":"2.7","online_time":"1418031896","cut_price":"0","city":"12","status":"3","class_name":"艾力绅","brand_name":"本田","refresh_time":"1418031896","cover_image_url":"http://image1.haoche51.com/67a7b0a1e3ab0d4c6e345f2d8f9f8d506b1e191b.jpg","dealer_price":"21.00","quoted_price":"35.18","suitable":0},{"id":"9174","vehicle_name":"雷克萨斯IS
   * 2011款 250 炫动版","register_year":"2014","register_month":"11","gearbox":"4","seller_price":"36","miles":"0.2","online_time":"1418028095","cut_price":"0","city":"12","status":"3","class_name":"雷克萨斯IS","brand_name":"雷克萨斯","refresh_time":"1418028095","cover_image_url":"http://image1.haoche51.com/cf8091e167ade131a550ba6d25019e5f6315d6f7.jpg","dealer_price":"28.06","quoted_price":"50.60","suitable":0}],"vehicle_count":4}
   */

  private int errno;
  private String errmsg;
  /**
   * vehicle_list : [{"id":"8315","vehicle_name":"绅宝D70 2013款 2.0T 舒适版","register_year":"2014","register_month":"06","gearbox":"4","seller_price":"11","miles":"0.45","online_time":"1416902689","cut_price":"0.2","city":"12","status":"3","class_name":"绅宝D70","brand_name":"绅宝","refresh_time":"1418280500","cover_image_url":"http://image1.haoche51.com/49de5c9d55a093eca0ab15400459c7000dfea56e.jpg","dealer_price":"9.23","quoted_price":"16.48","suitable":0},{"id":"7709","vehicle_name":"大众Eos
   * 2011款 2.0TSI","register_year":"2014","register_month":"06","gearbox":"3","seller_price":"30","miles":"0.65","online_time":"1415949017","cut_price":"0","city":"12","status":"3","class_name":"大众Eos","brand_name":"大众","refresh_time":"1415949017","cover_image_url":"http://image1.haoche51.com/045b239d518a4d355e950a1e748565ebebee4941.jpg","dealer_price":"27.06","quoted_price":"50.51","suitable":0},{"id":"9235","vehicle_name":"艾力绅
   * 2012款 2.4L VTi-S尊贵版","register_year":"2014","register_month":"07","gearbox":"2","seller_price":"29","miles":"2.7","online_time":"1418031896","cut_price":"0","city":"12","status":"3","class_name":"艾力绅","brand_name":"本田","refresh_time":"1418031896","cover_image_url":"http://image1.haoche51.com/67a7b0a1e3ab0d4c6e345f2d8f9f8d506b1e191b.jpg","dealer_price":"21.00","quoted_price":"35.18","suitable":0},{"id":"9174","vehicle_name":"雷克萨斯IS
   * 2011款 250 炫动版","register_year":"2014","register_month":"11","gearbox":"4","seller_price":"36","miles":"0.2","online_time":"1418028095","cut_price":"0","city":"12","status":"3","class_name":"雷克萨斯IS","brand_name":"雷克萨斯","refresh_time":"1418028095","cover_image_url":"http://image1.haoche51.com/cf8091e167ade131a550ba6d25019e5f6315d6f7.jpg","dealer_price":"28.06","quoted_price":"50.60","suitable":0}]
   * vehicle_count : 4
   */

  private HCVehicleListDataEntity data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public void setData(HCVehicleListDataEntity data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public HCVehicleListDataEntity getData() {
    return data;
  }
}
