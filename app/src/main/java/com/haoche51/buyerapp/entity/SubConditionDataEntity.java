package com.haoche51.buyerapp.entity;

public class SubConditionDataEntity {

  private String city_id;
  private String uid = "";
  private String geerbox = "";
  private String structure = "";
  private String es_standard = "";
  private String emission_low = "";
  private String brand_id = "";
  private String price_high = "";
  private String emission_high = "";
  private String id = "";
  private String price_low = "";
  private String class_id = "";
  private String miles_high = "";
  private String year_low = "";
  private String miles_low = "";
  private String year_high = "";
  private String country = "";
  private String color = "";

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getGeerbox() {
    return geerbox;
  }

  public void setGeerbox(String geerbox) {
    this.geerbox = geerbox;
  }

  public String getStructure() {
    return structure;
  }

  public void setStructure(String structure) {
    this.structure = structure;
  }

  public String getEs_standard() {
    return es_standard;
  }

  public void setEs_standard(String es_standard) {
    this.es_standard = es_standard;
  }

  public String getEmission_low() {
    return emission_low;
  }

  public void setEmission_low(String emission_low) {
    this.emission_low = emission_low;
  }

  public String getBrand_id() {
    return brand_id;
  }

  public void setBrand_id(String brand_id) {
    this.brand_id = brand_id;
  }

  public String getPrice_high() {
    return price_high;
  }

  public void setPrice_high(String price_high) {
    this.price_high = price_high;
  }

  public String getEmission_high() {
    return emission_high;
  }

  public void setEmission_high(String emission_high) {
    this.emission_high = emission_high;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPrice_low() {
    return price_low;
  }

  public void setPrice_low(String price_low) {
    this.price_low = price_low;
  }

  public String getClass_id() {
    return class_id;
  }

  public void setClass_id(String class_id) {
    this.class_id = class_id;
  }

  public String getMiles_high() {
    return miles_high;
  }

  public void setMiles_high(String miles_high) {
    this.miles_high = miles_high;
  }

  public String getYear_low() {
    return year_low;
  }

  public void setYear_low(String year_low) {
    this.year_low = year_low;
  }

  public String getMiles_low() {
    return miles_low;
  }

  public void setMiles_low(String miles_low) {
    this.miles_low = miles_low;
  }

  public String getYear_high() {
    return year_high;
  }

  public void setYear_high(String year_high) {
    this.year_high = year_high;
  }

  public String getCity_id() {
    return city_id;
  }

  public void setCity_id(String city_id) {
    this.city_id = city_id;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SubConditionDataEntity entity = (SubConditionDataEntity) o;

    if (geerbox != null ? !geerbox.equals(entity.geerbox) : entity.geerbox != null) return false;
    if (structure != null ? !structure.equals(entity.structure) : entity.structure != null) {
      return false;
    }
    if (es_standard != null ? !es_standard.equals(entity.es_standard)
        : entity.es_standard != null) {
      return false;
    }
    if (emission_low != null ? !emission_low.equals(entity.emission_low)
        : entity.emission_low != null) {
      return false;
    }
    if (brand_id != null ? !brand_id.equals(entity.brand_id) : entity.brand_id != null) {
      return false;
    }
    if (price_high != null ? !price_high.equals(entity.price_high) : entity.price_high != null) {
      return false;
    }
    if (emission_high != null ? !emission_high.equals(entity.emission_high)
        : entity.emission_high != null) {
      return false;
    }
    if (price_low != null ? !price_low.equals(entity.price_low) : entity.price_low != null) {
      return false;
    }
    if (class_id != null ? !class_id.equals(entity.class_id) : entity.class_id != null) {
      return false;
    }
    if (miles_high != null ? !miles_high.equals(entity.miles_high) : entity.miles_high != null) {
      return false;
    }
    if (year_low != null ? !year_low.equals(entity.year_low) : entity.year_low != null) {
      return false;
    }
    if (miles_low != null ? !miles_low.equals(entity.miles_low) : entity.miles_low != null) {
      return false;
    }
    if (year_high != null ? !year_high.equals(entity.year_high) : entity.year_high != null) {
      return false;
    }
    if (country != null ? !country.equals(entity.country) : entity.country != null) return false;
    return color != null ? color.equals(entity.color) : entity.color == null;
  }

  @Override public int hashCode() {
    int result = geerbox != null ? geerbox.hashCode() : 0;
    result = 31 * result + (structure != null ? structure.hashCode() : 0);
    result = 31 * result + (es_standard != null ? es_standard.hashCode() : 0);
    result = 31 * result + (emission_low != null ? emission_low.hashCode() : 0);
    result = 31 * result + (brand_id != null ? brand_id.hashCode() : 0);
    result = 31 * result + (price_high != null ? price_high.hashCode() : 0);
    result = 31 * result + (emission_high != null ? emission_high.hashCode() : 0);
    result = 31 * result + (price_low != null ? price_low.hashCode() : 0);
    result = 31 * result + (class_id != null ? class_id.hashCode() : 0);
    result = 31 * result + (miles_high != null ? miles_high.hashCode() : 0);
    result = 31 * result + (year_low != null ? year_low.hashCode() : 0);
    result = 31 * result + (miles_low != null ? miles_low.hashCode() : 0);
    result = 31 * result + (year_high != null ? year_high.hashCode() : 0);
    result = 31 * result + (country != null ? country.hashCode() : 0);
    result = 31 * result + (color != null ? color.hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    return "SubConditionDataEntity{" +
        "city_id='" + city_id + '\'' +
        ", uid='" + uid + '\'' +
        ", geerbox='" + geerbox + '\'' +
        ", structure='" + structure + '\'' +
        ", es_standard='" + es_standard + '\'' +
        ", emission_low='" + emission_low + '\'' +
        ", brand_id='" + brand_id + '\'' +
        ", price_high='" + price_high + '\'' +
        ", emission_high='" + emission_high + '\'' +
        ", id='" + id + '\'' +
        ", price_low='" + price_low + '\'' +
        ", class_id='" + class_id + '\'' +
        ", miles_high='" + miles_high + '\'' +
        ", year_low='" + year_low + '\'' +
        ", miles_low='" + miles_low + '\'' +
        ", year_high='" + year_high + '\'' +
        ", country='" + country + '\'' +
        ", color='" + color + '\'' +
        '}';
  }
}
