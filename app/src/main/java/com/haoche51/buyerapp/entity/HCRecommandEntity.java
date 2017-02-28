package com.haoche51.buyerapp.entity;

import java.util.List;

public class HCRecommandEntity {
  private int count;

  private List<HCVehicleItemEntity> vehicles;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void setVehicles(List<HCVehicleItemEntity> vehicles) {
    this.vehicles = vehicles;
  }

  public List<HCVehicleItemEntity> getVehicles() {
    return vehicles;
  }
}
