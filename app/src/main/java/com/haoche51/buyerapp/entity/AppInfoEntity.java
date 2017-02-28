package com.haoche51.buyerapp.entity;

public class AppInfoEntity {
  private String n;
  private String p;
  private String v;

  public AppInfoEntity(String n, String p, String v) {
    this.n = n;
    this.p = p;
    this.v = v;
  }

  public String getN() {
    return n;
  }

  public void setN(String n) {
    this.n = n;
  }

  public String getP() {
    return p;
  }

  public void setP(String p) {
    this.p = p;
  }

  public String getV() {
    return v;
  }

  public void setV(String v) {
    this.v = v;
  }
}
