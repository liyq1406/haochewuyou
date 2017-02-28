package com.haoche51.buyerapp.entity;

public class BHCSubListEntity {

  /**
   * errno : 0
   * errmsg : ok
   * data : [{"id":"10141","vehicle_name":"马自达3 2007款 1.6L 手动标准型","register_time":"1199116800","seller_words":"在山东买的，哥哥给买的，特别好开，操控性很好。我平时接送孩子，在修理厂保养，车况不错。现在，想换新车了。此车适合一般家庭使用，经济实惠。","seller_name":"窦女士","emission_standard":"1","register_year":"2008","register_month":"01","gearbox":"1","seller_price":"4.2","miles":"7.4","online_time":"1419060467","cut_price":"0","city":"12","status":"5","class_name":"马自达3","brand_name":"马自达","refresh_time":"1419060471","quoted_price":"12.98","eval_price":"0","suitable":1,"cover_image_url":"http://image1.haoche51.com/d0bc08730558d3f144966d89fa46bd03d8463351.jpg","dealer_price":"5.10","label":["女车主"]},{"id":"8775","vehicle_name":"标致
   * 307(进口) 2007款 SW 2.0 豪华版","register_time":"1193846400","seller_words":"13年在帅车购买。喜欢此车的空间大，座椅拆卸方便，空间也可以随着自己的喜欢自由组合。玻璃顶，视野开阔，通风良好，也非常的美观。车载蓝牙电话，加装四轮胎压检测，加装前雷达，整体车的配置非常的豪华，进口的车，质量很好。","seller_name":"李先生","emission_standard":"2","register_year":"2007","register_month":"11","gearbox":"4","seller_price":"7.2","miles":"15.25","online_time":"1417606899","cut_price":"0","city":"12","status":"7","class_name":"标致307(进口)","brand_name":"标致","refresh_time":"1417606902","quoted_price":"29.15","eval_price":"0","suitable":1,"cover_image_url":"http://image1.haoche51.com/865a66401aba3312d5d0d042a23abebc0764cb37.jpg","dealer_price":"8.96","label":[]}]
   */

  private int errno;
  private String errmsg;
  /**
   * id : 10141
   * vehicle_name : 马自达3 2007款 1.6L 手动标准型
   * register_time : 1199116800
   * seller_words : 在山东买的，哥哥给买的，特别好开，操控性很好。我平时接送孩子，在修理厂保养，车况不错。现在，想换新车了。此车适合一般家庭使用，经济实惠。
   * seller_name : 窦女士
   * emission_standard : 1
   * register_year : 2008
   * register_month : 01
   * gearbox : 1
   * seller_price : 4.2
   * miles : 7.4
   * online_time : 1419060467
   * cut_price : 0
   * city : 12
   * status : 5
   * class_name : 马自达3
   * brand_name : 马自达
   * refresh_time : 1419060471
   * quoted_price : 12.98
   * eval_price : 0
   * suitable : 1
   * cover_image_url : http://image1.haoche51.com/d0bc08730558d3f144966d89fa46bd03d8463351.jpg
   * dealer_price : 5.10
   * label : ["女车主"]
   */

  private HCSubListEntity data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public void setData(HCSubListEntity data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public HCSubListEntity getData() {
    return data;
  }
}
