package com.haoche51.buyerapp.entity;

public class HCSubcribeEntity {
    /**
     * sub_id : 61
     */
    private String sub_id = "";

    public void setSub_id(String sub_id) {
        this.sub_id = sub_id;
    }

    public String getSub_id() {
        return sub_id;
    }

    @Override
    public String toString() {
        return "HCSubcribeEntity{" +
                "sub_id='" + sub_id + '\'' +
                '}';
    }
}
