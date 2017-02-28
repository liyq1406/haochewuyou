package com.haoche51.buyerapp.entity;

public class HCProfileCountsEntity {
  private int collection_count;
  private int subscribe_count;
  private int subscribe_new_count;
  private int buyer_order_count;

  public int getCollection_count() {
    return collection_count;
  }

  public void setCollection_count(int collection_count) {
    this.collection_count = collection_count;
  }

  public int getSubscribe_count() {
    return subscribe_count;
  }

  public void setSubscribe_count(int subscribe_count) {
    this.subscribe_count = subscribe_count;
  }

  public int getSubscribe_new_count() {
    return subscribe_new_count;
  }

  public void setSubscribe_new_count(int subscribe_new_count) {
    this.subscribe_new_count = subscribe_new_count;
  }

  public int getBuyer_order_count() {
    return buyer_order_count;
  }

  public void setBuyer_order_count(int buyer_order_count) {
    this.buyer_order_count = buyer_order_count;
  }
}
