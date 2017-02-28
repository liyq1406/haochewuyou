package com.haoche51.buyerapp.entity;

public class HCSearchEntity {

  /**
   * errno : 0
   * data : {"count":"26","vehicles":[{"quoted_price":"0.00","register_time":"1138723200","vehicle_structure":"1","emission":"1.3","class_name":"雨燕","brand_name":"铃木","offline":"0","online_time":"1442299565","city_id":"12","id":"52843","vehicle_year":"2005","emission_standard":"0","show_status":"0","che300_high_price":"1.98","refresh_time":"1442299565","register_month":"2","geerbox_type":"1","status":"3","register_year":"2006","brand_id":"53","miles":"14.8","vehicle_name":"铃木
   * 雨燕 2005款 1.3L 手动豪华型","class_id":"362","seller_price":"1.7","dealer_price":"1.46","cover_image_url":""},{"quoted_price":"0.00","register_time":"1157040000","vehicle_structure":"1","emission":"1.3","class_name":"雨燕","brand_name":"铃木","offline":"0","online_time":"1440654163","city_id":"12","id":"48194","vehicle_year":"2005","emission_standard":"0","show_status":"0","che300_high_price":"2.01","refresh_time":"1440654163","register_month":"9","geerbox_type":"1","status":"3","register_year":"2006","brand_id":"53","miles":"13.1","vehicle_name":"铃木
   * 雨燕 2005款 1.3L 手动豪华型","class_id":"362","seller_price":"1.9","dealer_price":"1.48","cover_image_url":""}]}
   * query : {"brand_id":"53","class_id":"362","city":"12","type":[1,1]}
   */

  private int errno;
  private BHCVehicleItemEntity data;
  private HCQueryEntity query;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setData(BHCVehicleItemEntity data) {
    this.data = data;
  }

  public void setQuery(HCQueryEntity query) {
    this.query = query;
  }

  public int getErrno() {
    return errno;
  }

  public BHCVehicleItemEntity getData() {
    return data;
  }

  public HCQueryEntity getQuery() {
    return query;
  }
}
