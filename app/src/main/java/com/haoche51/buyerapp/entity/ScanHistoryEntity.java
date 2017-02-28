package com.haoche51.buyerapp.entity;

import java.io.Serializable;

/***
 * 浏览记录实体
 */
public class ScanHistoryEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String city_name;
  private String vehicle_name;
  private String seller_price;
  private String shoufu_price;
  private String register_time;
  private String miles;
  private String geerbox_type;
  private String cover_pic;
  private String suitable;
  private String zhiyingdian;
  private String zhibao;
  private String status;
  private int promote;
  private long create_time;

  //图片四角的水印
  private String left_top;
  private String left_top_rate;
  private String left_bottom;
  private String left_bottom_rate;

  public ScanHistoryEntity() {
  }

  public ScanHistoryEntity(String id, String cover_pic, String vehicle_name, String register_time,
      String miles, String seller_price, String left_top, String left_top_rate, String left_bottom,
      String left_bottom_rate) {
    this.id = id;
    this.vehicle_name = vehicle_name;
    this.seller_price = seller_price;
    this.register_time = register_time;
    this.miles = miles;
    this.cover_pic = cover_pic;
    this.left_top = left_top;
    this.left_top_rate = left_top_rate;
    this.left_bottom = left_bottom;
    this.left_bottom_rate = left_bottom_rate;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCity_name() {
    return city_name;
  }

  public void setCity_name(String city_name) {
    this.city_name = city_name;
  }

  public String getVehicle_name() {
    return vehicle_name;
  }

  public void setVehicle_name(String vehicle_name) {
    this.vehicle_name = vehicle_name;
  }

  public String getSeller_price() {
    return seller_price;
  }

  public void setSeller_price(String seller_price) {
    this.seller_price = seller_price;
  }

  public String getShoufu_price() {
    return shoufu_price;
  }

  public void setShoufu_price(String shoufu_price) {
    this.shoufu_price = shoufu_price;
  }

  public String getRegister_time() {
    return register_time;
  }

  public void setRegister_time(String register_time) {
    this.register_time = register_time;
  }

  public String getMiles() {
    return miles;
  }

  public void setMiles(String miles) {
    this.miles = miles;
  }

  public String getGeerbox_type() {
    return geerbox_type;
  }

  public void setGeerbox_type(String geerbox_type) {
    this.geerbox_type = geerbox_type;
  }

  public String getCover_pic() {
    return cover_pic;
  }

  public void setCover_pic(String cover_pic) {
    this.cover_pic = cover_pic;
  }

  public String getSuitable() {
    return suitable;
  }

  public void setSuitable(String suitable) {
    this.suitable = suitable;
  }

  public String getZhiyingdian() {
    return zhiyingdian;
  }

  public void setZhiyingdian(String zhiyingdian) {
    this.zhiyingdian = zhiyingdian;
  }

  public String getZhibao() {
    return zhibao;
  }

  public void setZhibao(String zhibao) {
    this.zhibao = zhibao;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getPromote() {
    return promote;
  }

  public void setPromote(int promote) {
    this.promote = promote;
  }

  public long getCreate_time() {
    return create_time;
  }

  public void setCreate_time(long create_time) {
    this.create_time = create_time;
  }

  public String getLeft_top() {
    return left_top;
  }

  public void setLeft_top(String left_top) {
    this.left_top = left_top;
  }

  public String getLeft_top_rate() {
    return left_top_rate;
  }

  public void setLeft_top_rate(String left_top_rate) {
    this.left_top_rate = left_top_rate;
  }

  public String getLeft_bottom() {
    return left_bottom;
  }

  public void setLeft_bottom(String left_bottom) {
    this.left_bottom = left_bottom;
  }

  public String getLeft_bottom_rate() {
    return left_bottom_rate;
  }

  public void setLeft_bottom_rate(String left_bottom_rate) {
    this.left_bottom_rate = left_bottom_rate;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ScanHistoryEntity that = (ScanHistoryEntity) o;

    if (id != null ? !id.equals(that.id) : that.id != null) return false;
    if (vehicle_name != null ? !vehicle_name.equals(that.vehicle_name)
        : that.vehicle_name != null) {
      return false;
    }
    return seller_price != null ? seller_price.equals(that.seller_price)
        : that.seller_price == null;
  }

  @Override public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (vehicle_name != null ? vehicle_name.hashCode() : 0);
    result = 31 * result + (seller_price != null ? seller_price.hashCode() : 0);
    return result;
  }
}
