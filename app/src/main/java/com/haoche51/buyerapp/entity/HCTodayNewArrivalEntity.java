package com.haoche51.buyerapp.entity;

import java.util.List;

public class HCTodayNewArrivalEntity {
  private String count;

  private List<HCVehicleItemEntity> vehicles;

  public String getCount() {
    return count;
  }

  public void setCount(String count) {
    this.count = count;
  }

  public List<HCVehicleItemEntity> getVehicles() {
    return vehicles;
  }

  public void setVehicles(List<HCVehicleItemEntity> vehicles) {
    this.vehicles = vehicles;
  }
}
