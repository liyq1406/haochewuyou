package com.haoche51.buyerapp.data;

import java.util.List;

public class Brand {
  private String sortLetter = "";
  private int brandId;// Id for brand
  private String brandName;
  private List<String> series_ids;

  public Brand() {
  }

  public List<String> getSeries_ids() {
    return series_ids;
  }

  public void setSeries_ids(List<String> series_ids) {
    this.series_ids = series_ids;
  }

  public int getBrandId() {
    return brandId;
  }

  public void setBrandId(int brandId) {
    this.brandId = brandId;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getSortLetter() {
    return sortLetter;
  }

  public void setSortLetter(String sortLetter) {
    this.sortLetter = sortLetter;
  }
}
