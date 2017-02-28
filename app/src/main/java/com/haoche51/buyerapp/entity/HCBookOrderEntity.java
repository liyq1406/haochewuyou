package com.haoche51.buyerapp.entity;

public class HCBookOrderEntity {

    /**
     * id : 835
     * vehicle_source_id : 11268
     * vehicle_name : 宝马 5系GT 2011款 535i 典雅型
     * register_time : null
     * mile : null
     * trans_status : 3
     * geerbox : null
     * image : http://192.168.1.106:9999/76ba434446dabd8e7af6578c8d1c88636d7bfd21.jpg
     * time : 1425609300
     * type : 过户
     * place : bj
     * desc : 过户专员
     * name : 过户
     * phone : 13800138000
     * price : 10
     * status : 4
     * comment : 过户材料：机动车登记证书，机动车行驶证，原始购置发票或二手车过户票，身份证。看详细说明http://m.haoche51.com/zrbz
     * vehicle_online : 0
     * coupon_type: "3",
     * coupon_amount: "500",
     */
    private String id;
    private String vehicle_source_id;
    private String vehicle_name;
    private long register_time;
    private float mile;
    private String trans_status;
    private int geerbox;
    private String image;
    private long time;
    private String type;
    private String place;
    private String desc;
    private String name;
    private String phone;
    private String price;
    private int status;
    private String comment;
    private int vehicle_online;
    private String coupon_type;
    private String coupon_amount;
    private String brand_name;
    private String class_name;

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicle_source_id() {
        return vehicle_source_id;
    }

    public void setVehicle_source_id(String vehicle_source_id) {
        this.vehicle_source_id = vehicle_source_id;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public long getRegister_time() {
        return register_time;
    }

    public void setRegister_time(long register_time) {
        this.register_time = register_time;
    }

    public float getMile() {
        return mile;
    }

    public void setMile(float mile) {
        this.mile = mile;
    }

    public String getTrans_status() {
        return trans_status;
    }

    public void setTrans_status(String trans_status) {
        this.trans_status = trans_status;
    }

    public int getGeerbox() {
        return geerbox;
    }

    public void setGeerbox(int geerbox) {
        this.geerbox = geerbox;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getVehicle_online() {
        return vehicle_online;
    }

    public void setVehicle_online(int vehicle_online) {
        this.vehicle_online = vehicle_online;
    }

    public String getCoupon_type() {
        return coupon_type;
    }

    public void setCoupon_type(String coupon_type) {
        this.coupon_type = coupon_type;
    }

    public String getCoupon_amount() {
        return coupon_amount;
    }

    public void setCoupon_amount(String coupon_amount) {
        this.coupon_amount = coupon_amount;
    }
}