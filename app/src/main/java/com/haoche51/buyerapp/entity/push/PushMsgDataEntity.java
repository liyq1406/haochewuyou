package com.haoche51.buyerapp.entity.push;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuhaibo on 15/11/7
 */
public class PushMsgDataEntity implements Serializable {

  private int vehicle_source_id;

  private String params;

  private String redirect;

  /** 城市 */
  private int city;
  /** 品牌车系 */
  private int brand_id;
  private int class_id;
  /** 价格 */
  private List<Double> price;

  public int getCity() {
    return city;
  }

  public void setCity(int city) {
    this.city = city;
  }

  public int getBrand_id() {
    return brand_id;
  }

  public void setBrand_id(int brand_id) {
    this.brand_id = brand_id;
  }

  public int getClass_id() {
    return class_id;
  }

  public void setClass_id(int class_id) {
    this.class_id = class_id;
  }

  public List<Double> getPrice() {
    return price;
  }

  public void setPrice(List<Double> price) {
    this.price = price;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public String getRedirect() {
    return redirect;
  }

  public void setRedirect(String redirect) {
    this.redirect = redirect;
  }

  public void setVehicle_source_id(int vehicle_source_id) {
    this.vehicle_source_id = vehicle_source_id;
  }

  public int getVehicle_source_id() {
    return vehicle_source_id;
  }
}
