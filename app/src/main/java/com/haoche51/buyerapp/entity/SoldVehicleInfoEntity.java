package com.haoche51.buyerapp.entity;

import java.util.List;

public class SoldVehicleInfoEntity {
  private int brand_id;

  public int getBrand_id() {
    return brand_id;
  }

  public void setBrand_id(int brand_id) {
    this.brand_id = brand_id;
  }

  private int vehicle_source_id;
  private String vehicle_name;
  private String cover_image;
  private String correct_text;
  private String correct_phone;
  private String seller_price;
  private String eval_price;
  private String adjust_text;
  private String adjust_phone;
  private int buyer_count;
  private String offline_phone;
  private String offline_text;
  private String online_text;
  private String sell_text;
  private String ask_seller_text;
  private String suggest_text;
  private int suggest_status;
  private String brand_name;
  private String class_name;
  private String register_year;
  private String register_month;
  private String miles;
  private String geerbox_type;
  private String ask_seller_phone;

  public String getAsk_seller_phone() {
    return ask_seller_phone;
  }

  public void setAsk_seller_phone(String ask_seller_phone) {
    this.ask_seller_phone = ask_seller_phone;
  }

  public String getGeerbox_type() {
    return geerbox_type;
  }

  public void setGeerbox_type(String geerbox_type) {
    this.geerbox_type = geerbox_type;
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

  public String getRegister_year() {
    return register_year;
  }

  public void setRegister_year(String register_year) {
    this.register_year = register_year;
  }

  public String getRegister_month() {
    return register_month;
  }

  public void setRegister_month(String register_month) {
    this.register_month = register_month;
  }

  public String getMiles() {
    return miles;
  }

  public void setMiles(String miles) {
    this.miles = miles;
  }

  public int getSuggest_status() {
    return suggest_status;
  }

  public void setSuggest_status(int suggest_status) {
    this.suggest_status = suggest_status;
  }

  private int status;
  private String status_text;
  /**
   * text : 151****7204的客户其他
   * create_time :
   */

  private List<SoldBuyerListEntity> buyer_list;

  public void setVehicle_source_id(int vehicle_source_id) {
    this.vehicle_source_id = vehicle_source_id;
  }

  public void setVehicle_name(String vehicle_name) {
    this.vehicle_name = vehicle_name;
  }

  public void setCover_image(String cover_image) {
    this.cover_image = cover_image;
  }

  public void setCorrect_text(String correct_text) {
    this.correct_text = correct_text;
  }

  public void setCorrect_phone(String correct_phone) {
    this.correct_phone = correct_phone;
  }

  public void setSeller_price(String seller_price) {
    this.seller_price = seller_price;
  }

  public void setEval_price(String eval_price) {
    this.eval_price = eval_price;
  }

  public void setAdjust_text(String adjust_text) {
    this.adjust_text = adjust_text;
  }

  public void setAdjust_phone(String adjust_phone) {
    this.adjust_phone = adjust_phone;
  }

  public void setBuyer_count(int buyer_count) {
    this.buyer_count = buyer_count;
  }

  public void setOffline_phone(String offline_phone) {
    this.offline_phone = offline_phone;
  }

  public void setOffline_text(String offline_text) {
    this.offline_text = offline_text;
  }

  public void setOnline_text(String online_text) {
    this.online_text = online_text;
  }

  public void setSell_text(String sell_text) {
    this.sell_text = sell_text;
  }

  public void setask_seller_text(String ask_seller_text) {
    this.ask_seller_text = ask_seller_text;
  }

  public void setSuggest_text(String suggest_text) {
    this.suggest_text = suggest_text;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setStatus_text(String status_text) {
    this.status_text = status_text;
  }

  public void setBuyer_list(List<SoldBuyerListEntity> buyer_list) {
    this.buyer_list = buyer_list;
  }

  public int getVehicle_source_id() {
    return vehicle_source_id;
  }

  public String getVehicle_name() {
    return vehicle_name;
  }

  public String getCover_image() {
    return cover_image;
  }

  public String getCorrect_text() {
    return correct_text;
  }

  public String getCorrect_phone() {
    return correct_phone;
  }

  public String getSeller_price() {
    return seller_price;
  }

  public String getEval_price() {
    return eval_price;
  }

  public String getAdjust_text() {
    return adjust_text;
  }

  public String getAdjust_phone() {
    return adjust_phone;
  }

  public int getBuyer_count() {
    return buyer_count;
  }

  public String getOffline_phone() {
    return offline_phone;
  }

  public String getOffline_text() {
    return offline_text;
  }

  public String getOnline_text() {
    return online_text;
  }

  public String getSell_text() {
    return sell_text;
  }

  public String getask_seller_text() {
    return ask_seller_text;
  }

  public String getSuggest_text() {
    return suggest_text;
  }

  public int getStatus() {
    return status;
  }

  public String getStatus_text() {
    return status_text;
  }

  public List<SoldBuyerListEntity> getBuyer_list() {
    return buyer_list;
  }
}
