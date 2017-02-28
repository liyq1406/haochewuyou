package com.haoche51.buyerapp.entity;

import org.json.JSONObject;

/**
 * 车系实体
 */
public class SeriesEntity extends BaseEntity {

  public SeriesEntity() {

  }

  public SeriesEntity(int brand_id, int id) {
    this.brand_id = brand_id;
    this.id = id;
    this.name = "不限";
  }

  public SeriesEntity(JSONObject json) {
    this.id = json.optInt("id");
    this.name = json.optString("name");
    this.short_name = json.optString("short_name");
    this.pinyin = json.optString("pinyin");
    this.brand_id = json.optInt("brand_id");
    this.brand_name = json.optString("brand_name");
  }

  private int id;
  private String name;
  private String short_name;
  private String pinyin;
  private int brand_id;
  private String brand_name;

  public SeriesEntity(int id, String name, String short_name, String pinyin, int brand_id,
      String brand_name) {
    this.id = id;
    this.name = name;
    this.short_name = short_name;
    this.pinyin = pinyin;
    this.brand_id = brand_id;
    this.brand_name = brand_name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getShort_name() {
    return short_name;
  }

  public void setShort_name(String short_name) {
    this.short_name = short_name;
  }

  public String getPinyin() {
    return pinyin;
  }

  public void setPinyin(String pinyin) {
    this.pinyin = pinyin;
  }

  public int getBrand_id() {
    return brand_id;
  }

  public void setBrand_id(int brand_id) {
    this.brand_id = brand_id;
  }

  public String getBrand_name() {
    return brand_name;
  }

  public void setBrand_name(String brand_name) {
    this.brand_name = brand_name;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SeriesEntity)) return false;
    SeriesEntity that = (SeriesEntity) o;
    if (getId() != that.getId()) return false;
    return getBrand_id() == that.getBrand_id();
  }

  @Override public int hashCode() {
    int result = getId();
    result = 31 * result + (getName() != null ? getName().hashCode() : 0);
    result = 31 * result + (getShort_name() != null ? getShort_name().hashCode() : 0);
    result = 31 * result + (getPinyin() != null ? getPinyin().hashCode() : 0);
    result = 31 * result + getBrand_id();
    result = 31 * result + (getBrand_name() != null ? getBrand_name().hashCode() : 0);
    return result;
  }
}
