package com.haoche51.buyerapp.entity;

import com.haoche51.buyerapp.util.HCConsts;

public class KeyValueEntity  {
    public String key;
    public String value = HCConsts.UNLIMITED;

    public KeyValueEntity(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "KeyValueEntity [key=" + key + ", value=" + value + "]";
    }
}
