package com.haoche51.buyerapp.entity;

import java.util.List;

public class HCSoldVehicleEntity {
  /**
   * vehicle_source_id : 11477
   * vehicle_name : 大众 速腾 2009款 1.6L 自动舒适型
   * cover_image : http://192.168.1.106:9999/66fd0f26c427184cf1866680998b25314e1e1e37.jpg
   * correct_text : 如您发现车款不符，可联系我们进行纠正>>
   * correct_phone : 010-53605923
   * seller_price : 0
   * eval_price : 0
   * adjust_text :
   * adjust_phone :
   * buyer_count : 2
   * buyer_list : [{"text":"151****7204的客户其他","create_time":""},{"text":"185****3693的客户想了解您的低价是多少","create_time":""}]
   * offline_phone : 010-53605923
   * offline_text : 已自己出售，申请下线。
   * online_text : 2015/07/14您的爱车成功上线
   * sell_text :
   * sell_vehicle_text : 我还有车要出售>>
   * suggest_text : 按照好车无忧估价，您的报价合理。
   * status : 3
   * status_text : 在售
   */

  private List<SoldVehicleInfoEntity> vehicle_info;

  public void setVehicle_info(List<SoldVehicleInfoEntity> vehicle_info) {
    this.vehicle_info = vehicle_info;
  }

  public List<SoldVehicleInfoEntity> getVehicle_info() {
    return vehicle_info;
  }
}
