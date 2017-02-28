package com.haoche51.buyerapp.entity;

public class SplashDataEntity {
  private int id;
  private String redirect;
  private String image_url;
  private int show_time;
  private int jump;

  public int getJump() {
    return jump;
  }

  public void setJump(int jump) {
    this.jump = jump;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setRedirect(String redirect) {
    this.redirect = redirect;
  }

  public void setImage_url(String image_url) {
    this.image_url = image_url;
  }

  public void setShow_time(int show_time) {
    this.show_time = show_time;
  }

  public int getId() {
    return id;
  }

  public String getRedirect() {
    return redirect;
  }

  public String getImage_url() {
    return image_url;
  }

  public int getShow_time() {
    return show_time;
  }
}
