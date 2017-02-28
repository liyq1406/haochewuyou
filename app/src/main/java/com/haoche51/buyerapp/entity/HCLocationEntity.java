package com.haoche51.buyerapp.entity;

/**
 * 坐标位置实体
 */
public class HCLocationEntity {
    private double latitude;
    private double longitude;
    private String city_name;

    public HCLocationEntity() {

    }

    public HCLocationEntity(double latitude, double longitude, String city_name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city_name = city_name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getlongitude() {
        return longitude;
    }

    public void setlongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    @Override
    public String toString() {
        return "HCLocationEntity [latitude=" + latitude + ", longitude=" + longitude + ", city_name=" + city_name + "]";
    }
}
