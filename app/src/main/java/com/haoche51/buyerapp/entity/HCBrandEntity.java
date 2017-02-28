package com.haoche51.buyerapp.entity;

import java.util.ArrayList;
import java.util.List;

public class HCBrandEntity extends BaseEntity {
    private int brand_id;
    private String brand_name;
    private String first_char;
    private List<String> series = new ArrayList<>();

    public HCBrandEntity() {

    }

    public int getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getFirst_char() {
        return first_char;
    }

    public void setFirst_char(String first_char) {
        this.first_char = first_char;
    }

    public List<String> getSeries() {
        return series;
    }

    public void setSeries(List<String> series) {
        this.series = series;
    }

    @Override
    public String toString() {
        return "HCBrandEntity [brand_id=" + brand_id + ", brand_name=" + brand_name + ", first_char=" + first_char + ", series=" + series + "]";
    }
}
