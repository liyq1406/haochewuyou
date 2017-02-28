package com.haoche51.buyerapp.entity;

public class HCSplashEntity {

  /**
   * id : 3
   * redirect : http://m.haoche51.com/bj/activity/temai?channel=app
   * image_url : http://image3.haoche51.com/promote_002.jpg
   * show_time : 5
   */

  private SplashDataEntity body;
  /**
   * id : 3
   * redirect : http://m.haoche51.com/bj/activity/temai?channel=app
   * image_url : http://image3.haoche51.com/promote_002.jpg
   * show_time : 5
   */

  private SplashDataEntity foot;

  public void setBody(SplashDataEntity body) {
    this.body = body;
  }

  public SplashDataEntity getBody() {
    return body;
  }

  public void setFoot(SplashDataEntity foot) {
    this.foot = foot;
  }

  public SplashDataEntity getFoot() {
    return foot;
  }
}
