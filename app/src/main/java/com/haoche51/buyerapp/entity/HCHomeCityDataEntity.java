package com.haoche51.buyerapp.entity;

import java.util.List;

/**
 * action:  home_city_data_v6
 */
public class HCHomeCityDataEntity {

  private List<HCBannerEntity> mid_banner;

  private List<HCBannerEntity> top_slider;

  private List<HCPromoteEntity> pop_images;

  private String today_count;

  private String vehicle_count;

  private String accident_check_count;

  private String has_zhiyingdian;

  private String activity_start;

  private HCHomeLiveEntity zhibo_btn;

  private List<HomeBrandEntity> brand_list;

  private List<HCCityEntity> all_city;

  private List<HomeForumEntity> btm_posts;

  public HCHomeLiveEntity getZhibo_btn() {
    return zhibo_btn;
  }

  public void setZhibo_btn(HCHomeLiveEntity zhibo_btn) {
    this.zhibo_btn = zhibo_btn;
  }

  public String getActivity_start() {
    return activity_start;
  }

  public void setActivity_start(String activity_start) {
    this.activity_start = activity_start;
  }

  public String getHas_zhiyingdian() {
    return has_zhiyingdian;
  }

  public void setHas_zhiyingdian(String has_zhiyingdian) {
    this.has_zhiyingdian = has_zhiyingdian;
  }

  public List<HCBannerEntity> getTop_slider() {
    return top_slider;
  }

  public void setTop_slider(List<HCBannerEntity> top_slider) {
    this.top_slider = top_slider;
  }

  public String getAccident_check_count() {
    return accident_check_count;
  }

  public void setAccident_check_count(String accident_check_count) {
    this.accident_check_count = accident_check_count;
  }

  public String getVehicle_count() {
    return vehicle_count;
  }

  public void setVehicle_count(String vehicle_count) {
    this.vehicle_count = vehicle_count;
  }

  public List<HCPromoteEntity> getPop_images() {
    return pop_images;
  }

  public List<HCCityEntity> getAll_city() {
    return all_city;
  }

  public List<HomeForumEntity> getBtm_posts() {
    return btm_posts;
  }

  public void setBtm_posts(List<HomeForumEntity> btm_posts) {
    this.btm_posts = btm_posts;
  }

  public void setAll_city(List<HCCityEntity> all_city) {
    this.all_city = all_city;
  }

  public void setPop_images(List<HCPromoteEntity> pop_images) {
    this.pop_images = pop_images;
  }

  public String getToday_count() {
    return today_count;
  }

  public void setToday_count(String today_count) {
    this.today_count = today_count;
  }

  public void setMid_banner(List<HCBannerEntity> mid_banner) {
    this.mid_banner = mid_banner;
  }

  public void setBrand_list(List<HomeBrandEntity> brand_list) {
    this.brand_list = brand_list;
  }

  public List<HCBannerEntity> getMid_banner() {
    return mid_banner;
  }

  public List<HomeBrandEntity> getBrand_list() {
    return brand_list;
  }
}
