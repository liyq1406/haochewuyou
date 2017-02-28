package com.haoche51.buyerapp.entity;

public class HCCityEntity extends BaseEntity{
  private int city_id;
  private String city_name;
  private String first_char;
  private String domain;

  public HCCityEntity() {

  }

  public HCCityEntity(int city_id, String city_name) {
    this.city_id = city_id;
    this.city_name = city_name;
  }

  public int getCity_id() {
    return city_id;
  }

  public void setCity_id(int city_id) {
    this.city_id = city_id;
  }

  public String getCity_name() {
    return city_name;
  }

  public void setCity_name(String city_name) {
    this.city_name = city_name;
  }

  public String getFirst_char() {
    return first_char;
  }

  public void setFirst_char(String first_char) {
    this.first_char = first_char;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  @Override public String toString() {
    return "HCCityEntity [city_id=" + city_id + ", city_name=" + city_name + ", first_char="
        + first_char + ", domain=" + domain + "]";
  }
}
