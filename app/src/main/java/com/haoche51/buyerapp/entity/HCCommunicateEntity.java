package com.haoche51.buyerapp.entity;

public class HCCommunicateEntity {
  private String action;
  private int intValue;
  private String strValue;
  private boolean booleanValue;
  private Object objValue;

  public Object getObjValue() {
    return objValue;
  }

  public void setObjValue(Object objValue) {
    this.objValue = objValue;
  }

  public boolean isBooleanValue() {
    return booleanValue;
  }

  public void setBooleanValue(boolean booleanValue) {
    this.booleanValue = booleanValue;
  }

  public int getIntValue() {
    return intValue;
  }

  public void setIntValue(int intValue) {
    this.intValue = intValue;
  }

  public String getStrValue() {
    return strValue;
  }

  public void setStrValue(String strValue) {
    this.strValue = strValue;
  }

  private HCCommunicateEntity() {

  }

  public HCCommunicateEntity(String action) {
    this.action = action;
  }

  public HCCommunicateEntity(String action, int intValue) {
    this.action = action;
    this.intValue = intValue;
  }

  public HCCommunicateEntity(String action, boolean booleanValue) {
    this.action = action;
    this.booleanValue = booleanValue;
  }

  public HCCommunicateEntity(String action, Object objValue) {
    this.action = action;
    this.objValue = objValue;
  }

  public HCCommunicateEntity(String action, String strValue) {
    this.action = action;
    this.strValue = strValue;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
