package com.haoche51.buyerapp.entity;

import java.util.List;

/**
 * //同步服务器端的订阅实体
 */
public class HCSyncSubContionEntity {

    /**
     * errno : 0
     * data : [{"uid":"30000002","geerbox":"0","structure":"0","es_standard":"0","emission_low":"0","brand_id":"47","price_high":"0","sub_time":"2015-08-22 14:37:41","sign":"0e9cb2cd9758b8f57451a90ab1911c2a","emission_high":"0","id":"230","price_low":"0","class_id":"0","miles_high":"0","year_low":"0","del":"0","miles_low":"0","year_high":"0"},{"uid":"30000002","geerbox":"0","structure":"0","es_standard":"0","emission_low":"0","brand_id":"36","price_high":"15","sub_time":"2015-08-22 14:40:15","sign":"65de1b98912f045b78ff70b25cc089e2","emission_high":"0","id":"235","price_low":"12","class_id":"0","miles_high":"0","year_low":"0","del":"0","miles_low":"0","year_high":"0"},{"uid":"30000002","geerbox":"0","structure":"0","es_standard":"0","emission_low":"0","brand_id":"1","price_high":"12","sub_time":"2015-08-22 15:50:19","sign":"d367491b44b699a2d832632f856d726d","emission_high":"0","id":"244","price_low":"9","class_id":"0","miles_high":"0","year_low":"0","del":"0","miles_low":"0","year_high":"0"}]
     * errmsg : 操作成功
     */
    private int errno;
    private List<SubConditionDataEntity> data;
    private String errmsg;

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public void setData(List<SubConditionDataEntity> data) {
        this.data = data;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getErrno() {
        return errno;
    }

    public List<SubConditionDataEntity> getData() {
        return data;
    }

    public String getErrmsg() {
        return errmsg;
    }

    @Override
    public String toString() {
        return "HCSyncSubContionEntity{" +
                "errno=" + errno +
                ", data=" + data +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}
