package com.haoche51.buyerapp.entity;

import java.io.Serializable;

/**
 * 首页banner实体
 */
public class HCBannerEntity implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  /**
   * 跳转显示标题
   **/
  private String title;
  /**
   * 显示图片url
   */
  private String pic_url;
  /**
   * 点击图片跳转URL
   */
  private String link_url;
  /**
   * 城市id()
   */
  private int city_id;
  /**
   * 是否需要检查登陆
   */
  private int login_check;

  private String share_title;
  private String share_image;
  private String share_des;
  private String share_link;

  private String pic_rate;

  public String getPic_rate() {
    return pic_rate;
  }

  public void setPic_rate(String pic_rate) {
    this.pic_rate = pic_rate;
  }

  public String getShare_link() {
    return share_link;
  }

  public void setShare_link(String share_link) {
    this.share_link = share_link;
  }

  public String getShare_title() {
    return share_title;
  }

  public void setShare_title(String share_title) {
    this.share_title = share_title;
  }

  public String getShare_image() {
    return share_image;
  }

  public void setShare_image(String share_image) {
    this.share_image = share_image;
  }

  public String getShare_des() {
    return share_des;
  }

  public void setShare_des(String share_des) {
    this.share_des = share_des;
  }

  public int getLogin_check() {
    return login_check;
  }

  public void setLogin_check(int login_check) {
    this.login_check = login_check;
  }

  public HCBannerEntity() {
  }

  public HCBannerEntity(String title, String pic_url, String link_url, int city_id) {
    super();
    this.title = title;
    this.pic_url = pic_url;
    this.link_url = link_url;
    this.city_id = city_id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPic_url() {
    return pic_url;
  }

  public void setPic_url(String pic_url) {
    this.pic_url = pic_url;
  }

  public String getLink_url() {
    return link_url;
  }

  public void setLink_url(String link_url) {
    this.link_url = link_url;
  }

  public int getCity_id() {
    return city_id;
  }

  public void setCity_id(int city_id) {
    this.city_id = city_id;
  }

  @Override public String toString() {
    return "HCBannerEntity [title=" + title + ", pic_url=" + pic_url + ", link_url="
        + link_url + ", city_id=" + city_id + ", login_check=" + login_check + ", share_title="
        + share_title + ", share_image=" + share_image + ", share_des=" + share_des
        + ", share_link=" + share_link + "]";
  }
}
