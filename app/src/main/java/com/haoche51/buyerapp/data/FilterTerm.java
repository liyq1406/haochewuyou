package com.haoche51.buyerapp.data;

public class FilterTerm {

  /**
   * 排序类型
   * 默认排序 time
   * 车龄 register_time
   * 价格 price
   * 里程 miles
   */
  private String order = "time";

  /** 排序类型   升序 降序 */
  private int sort = 1;

  /** 记录排序描述信息 */
  private String descriptionSort = "综合排序";

  private int brand_id;

  private int class_id;

  private float highPrice = 0; // 价格上限 0 不限

  private float lowPrice = 0;// 价格下限 0 不限

  /** 变速箱类型 0:未知 1:手动 2:自动 3:双离合 4:手自一体 5:无级变速' */
  private int gearboxType = 0; // 变速箱类型 0 不限
  /** 车龄 */
  private int from_year;
  private int to_year;
  /** 里程区间 */
  private int from_miles;
  private int to_miles;
  /** 排量区间 */
  private float from_emission;
  private float to_emission;
  /** 车身结构 0:未知 1:两厢 2: 三厢 3: SUV' */
  private int structure;
  /** 排放标准 排放标准:0国二 1国三 2国四 3国五 */
  private int standard;
  /** 国别 */
  private int county;
  /** 颜色 */
  private int color;

  public int getCounty() {
    return county;
  }

  public void setCounty(int county) {
    this.county = county;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public String getDescriptionSort() {
    return descriptionSort;
  }

  public void setDescriptionSort(String descriptionSort) {
    this.descriptionSort = descriptionSort;
  }

  public int getSort() {
    return sort;
  }

  public void setSort(int sort) {
    this.sort = sort;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public int getFrom_year() {
    return from_year;
  }

  public void setFrom_year(int from_year) {
    this.from_year = from_year;
  }

  public int getTo_year() {
    return to_year;
  }

  public void setTo_year(int to_year) {
    this.to_year = to_year;
  }

  public int getFrom_miles() {
    return from_miles;
  }

  public void setFrom_miles(int from_miles) {
    this.from_miles = from_miles;
  }

  public int getTo_miles() {
    return to_miles;
  }

  public void setTo_miles(int to_miles) {
    this.to_miles = to_miles;
  }

  public float getFrom_emission() {
    return from_emission;
  }

  public void setFrom_emission(float from_emission) {
    this.from_emission = from_emission;
  }

  public float getTo_emission() {
    return to_emission;
  }

  public void setTo_emission(float to_emission) {
    this.to_emission = to_emission;
  }

  public int getStructure() {
    return structure;
  }

  public void setStructure(int structure) {
    this.structure = structure;
  }

  public int getStandard() {
    return standard;
  }

  public void setStandard(int standard) {
    this.standard = standard;
  }

  public float getHighPrice() {
    return highPrice;
  }

  public void setHighPrice(float highPrice) {
    this.highPrice = highPrice;
  }

  public float getLowPrice() {
    return lowPrice;
  }

  public void setLowPrice(float lowPrice) {
    this.lowPrice = lowPrice;
  }

  public int getGearboxType() {
    return gearboxType;
  }

  public void setGearboxType(int gearboxType) {
    this.gearboxType = gearboxType;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FilterTerm that = (FilterTerm) o;

    if (sort != that.sort) return false;
    if (Float.compare(that.highPrice, highPrice) != 0) return false;
    if (Float.compare(that.lowPrice, lowPrice) != 0) return false;
    if (gearboxType != that.gearboxType) return false;
    if (from_year != that.from_year) return false;
    if (to_year != that.to_year) return false;
    if (from_miles != that.from_miles) return false;
    if (to_miles != that.to_miles) return false;
    if (Float.compare(that.from_emission, from_emission) != 0) return false;
    if (Float.compare(that.to_emission, to_emission) != 0) return false;
    if (structure != that.structure) return false;
    if (standard != that.standard) return false;
    if (brand_id != that.brand_id) return false;
    if (class_id != that.class_id) return false;
    if (county != that.county) return false;
    if (color != that.color) return false;
    return !(order != null ? !order.equals(that.order) : that.order != null);
  }

  @Override public int hashCode() {
    int result = order != null ? order.hashCode() : 0;
    result = 31 * result + sort;
    result = 31 * result + (highPrice != +0.0f ? Float.floatToIntBits(highPrice) : 0);
    result = 31 * result + (lowPrice != +0.0f ? Float.floatToIntBits(lowPrice) : 0);
    result = 31 * result + gearboxType;
    result = 31 * result + from_year;
    result = 31 * result + to_year;
    result = 31 * result + from_miles;
    result = 31 * result + to_miles;
    result = 31 * result + (from_emission != +0.0f ? Float.floatToIntBits(from_emission) : 0);
    result = 31 * result + (to_emission != +0.0f ? Float.floatToIntBits(to_emission) : 0);
    result = 31 * result + structure;
    result = 31 * result + standard;
    result = 31 * result + brand_id;
    result = 31 * result + class_id;
    result = 31 * result + county;
    result = 31 * result + color;
    return result;
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
}
