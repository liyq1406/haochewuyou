package com.haoche51.buyerapp.entity;

import java.util.List;

public class BHCVehicleItemEntity {

  /**
   * count : 26
   * vehicles : [{"quoted_price":"0.00","register_time":"1138723200","vehicle_structure":"1","emission":"1.3","class_name":"雨燕","brand_name":"铃木","offline":"0","online_time":"1442299565","city_id":"12","id":"52843","vehicle_year":"2005","emission_standard":"0","show_status":"0","che300_high_price":"1.98","refresh_time":"1442299565","register_month":"2","geerbox_type":"1","status":"3","register_year":"2006","brand_id":"53","miles":"14.8","vehicle_name":"铃木
   * 雨燕 2005款 1.3L 手动豪华型","class_id":"362","seller_price":"1.7","dealer_price":"1.46","cover_image_url":""},{"quoted_price":"0.00","register_time":"1157040000","vehicle_structure":"1","emission":"1.3","class_name":"雨燕","brand_name":"铃木","offline":"0","online_time":"1440654163","city_id":"12","id":"48194","vehicle_year":"2005","emission_standard":"0","show_status":"0","che300_high_price":"2.01","refresh_time":"1440654163","register_month":"9","geerbox_type":"1","status":"3","register_year":"2006","brand_id":"53","miles":"13.1","vehicle_name":"铃木
   * 雨燕 2005款 1.3L 手动豪华型","class_id":"362","seller_price":"1.9","dealer_price":"1.48","cover_image_url":""}]
   */

  private String count;
  private String show_count;
  private List<HCVehicleItemEntity> vehicles;
  private List<HCVehicleItemEntity> recommend;

  public String getShow_count() {
    return show_count;
  }

  public void setShow_count(String show_count) {
    this.show_count = show_count;
  }

  public void setCount(String count) {
    this.count = count;
  }

  public String getCount() {
    return count;
  }

  public List<HCVehicleItemEntity> getRecommend() {
    return recommend;
  }

  public void setRecommend(List<HCVehicleItemEntity> recommend) {
    this.recommend = recommend;
  }

  public List<HCVehicleItemEntity> getVehicles() {
    return vehicles;
  }

  public void setVehicles(List<HCVehicleItemEntity> vehicles) {
    this.vehicles = vehicles;
  }
}

