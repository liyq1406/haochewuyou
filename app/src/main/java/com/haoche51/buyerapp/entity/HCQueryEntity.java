package com.haoche51.buyerapp.entity;

import java.util.List;

public class HCQueryEntity {

  /**
   * structure : 1
   * es : [2,3]
   * gear : [1,1]
   * brand_id : 8
   * class_id : 364
   * color : 白色
   * price : ["5","15"]
   * register_time : [1346428800,1409500800]
   * emission : [1.6,1.6]
   * city : 12
   * type : [1,1]
   */

  private String structure;
  private String brand_id;
  private String class_id;
  private String color;
  private String city;
  private List<Integer> es;
  private List<Integer> gear;
  private List<String> price;
  private List<Integer> register_time;
  private List<Double> emission;
  private List<Integer> type;

  public void setStructure(String structure) {
    this.structure = structure;
  }

  public void setBrand_id(String brand_id) {
    this.brand_id = brand_id;
  }

  public void setClass_id(String class_id) {
    this.class_id = class_id;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setEs(List<Integer> es) {
    this.es = es;
  }

  public void setGear(List<Integer> gear) {
    this.gear = gear;
  }

  public void setPrice(List<String> price) {
    this.price = price;
  }

  public void setRegister_time(List<Integer> register_time) {
    this.register_time = register_time;
  }

  public void setEmission(List<Double> emission) {
    this.emission = emission;
  }

  public void setType(List<Integer> type) {
    this.type = type;
  }

  public String getStructure() {
    return structure;
  }

  public String getBrand_id() {
    return brand_id;
  }

  public String getClass_id() {
    return class_id;
  }

  public String getColor() {
    return color;
  }

  public String getCity() {
    return city;
  }

  public List<Integer> getEs() {
    return es;
  }

  public List<Integer> getGear() {
    return gear;
  }

  public List<String> getPrice() {
    return price;
  }

  public List<Integer> getRegister_time() {
    return register_time;
  }

  public List<Double> getEmission() {
    return emission;
  }

  public List<Integer> getType() {
    return type;
  }
}