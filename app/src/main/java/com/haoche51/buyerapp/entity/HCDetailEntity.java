package com.haoche51.buyerapp.entity;

/**
 * * 对应action: get_vehicle_source_by_id_v2
 */
public class HCDetailEntity {

  private String id;
  private int collection_status;
  private String vehicle_name;
  private String cover_pic;
  private String brand_name;
  private String class_name;
  private String miles;
  private String geerbox_type;
  private String register_time;
  private String seller_price;
  private String ask_phone;
  private int status;

  private String share_title;
  private String share_desc;
  private String share_link;
  private String share_image;

  //图片四角的水印
  private String left_top;
  private String left_top_rate;
  private String left_bottom;
  private String left_bottom_rate;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCover_pic() {
    return cover_pic;
  }

  public void setCover_pic(String cover_pic) {
    this.cover_pic = cover_pic;
  }

  public String getRegister_time() {
    return register_time;
  }

  public void setRegister_time(String register_time) {
    this.register_time = register_time;
  }

  public String getShare_desc() {
    return share_desc;
  }

  public void setShare_desc(String share_desc) {
    this.share_desc = share_desc;
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

  public String getLeft_bottom_rate() {
    return left_bottom_rate;
  }

  public void setLeft_bottom_rate(String left_bottom_rate) {
    this.left_bottom_rate = left_bottom_rate;
  }

  public String getLeft_bottom() {
    return left_bottom;
  }

  public void setLeft_bottom(String left_bottom) {
    this.left_bottom = left_bottom;
  }

  public String getShare_title() {
    return share_title;
  }

  public void setShare_title(String share_title) {
    this.share_title = share_title;
  }

  public String getshare_desc() {
    return share_desc;
  }

  public void setshare_desc(String share_desc) {
    this.share_desc = share_desc;
  }

  public String getShare_link() {
    return share_link;
  }

  public void setShare_link(String share_link) {
    this.share_link = share_link;
  }

  public String getShare_image() {
    return share_image;
  }

  public void setShare_image(String share_image) {
    this.share_image = share_image;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getAsk_phone() {
    return ask_phone;
  }

  public void setAsk_phone(String ask_phone) {
    this.ask_phone = ask_phone;
  }

  public int getCollection_status() {
    return collection_status;
  }

  public void setCollection_status(int collection_status) {
    this.collection_status = collection_status;
  }

  public String getVehicle_name() {
    return vehicle_name;
  }

  public void setVehicle_name(String vehicle_name) {
    this.vehicle_name = vehicle_name;
  }

  public String getBrand_name() {
    return brand_name;
  }

  public void setBrand_name(String brand_name) {
    this.brand_name = brand_name;
  }

  public String getClass_name() {
    return class_name;
  }

  public void setClass_name(String class_name) {
    this.class_name = class_name;
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

  public String getSeller_price() {
    return seller_price;
  }

  public void setSeller_price(String seller_price) {
    this.seller_price = seller_price;
  }
}
