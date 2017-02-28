package com.haoche51.buyerapp.entity;

import java.util.List;

/**
 * {
 * "errno": 0,
 * "errmsg": "ok",
 * "data": [
 * {
 * "id": "36848",
 * "brand_id": "15",
 * "vehicle_name": "宝马 5系 2011款 535Li 豪华型",
 * "vehicle_id": "7436",
 * "register_time": "1304179200",
 * "brand_name": "宝马",
 * "class_name": "宝马5系",
 * "status": "3",
 * "seller_price": "38",
 * "quoted_price": "76.63",
 * "miles": "6.2",
 * "emission_standard": "2",
 * "geerbox": "自动",
 * "seller_words": "4s店购买，外观时尚大气，车身曲线流畅，车身硬朗，底盘牢固高速行驶稳定性好，座椅驾乘舒适，代步使用，按时保养，百公里11L，喜欢的朋友随时联系我。",
 * "checker_words": "车况非常好，女士用车，非常爱惜，个人一手车，外观几乎没有划痕，内饰干净，公里数合适，发动机无异响，动力强劲，整体车况良好。",
 * "report": {},
 * "model": {},
 * "cover_image_urls": [
 * "http://image1.haoche51.com/20a016fe4e8ef9ef9a46536f2bdd69b460b44e32.jpg",
 * "http://image1.haoche51.com/4304f512934aebe0af47a30051c714d17580ac9b.jpg"
 * ],
 * "dealer_price": "34.30"
 * },
 * {
 * "id": "36988",
 * "brand_id": "95",
 * "vehicle_name": "奔腾 B70 2006款 2.0L 自动豪华型",
 * "vehicle_id": "2457",
 * "register_time": "1183219200",
 * "brand_name": "奔腾",
 * "class_name": "奔腾B70",
 * "status": "3",
 * "seller_price": "5.2",
 * "quoted_price": "18.24",
 * "miles": "13.3",
 * "emission_standard": "1",
 * "geerbox": "自动",
 * "seller_words": "车是我07年在4s店购买的，i因为当时租的指标，所以有过过户记录，我平时都是代步用，车都在修理厂保养，百公里油耗9个，喜欢此车的朋友可以电联我。",
 * "checker_words": "车辆车况一般，全车喷过漆，发动机变速箱工作正常，气门室盖漏油，（需要换气门室盖垫）内饰保持干净整洁，功能配置工作正常，不错的车子，适合新手练手使用。",
 * "report": {
 * "vehicle_source_id": "36988",
 * "vehicle_appearance_score": "4",
 * "vehicle_interior_score": "5",
 * "vehicle_equipment_score": "4.9",
 * "transfer_times": "4",
 * "vehicle_machine_score": "4.6",
 * "skylight": "{\"desc\":\"\",\"extra\":true,\"has\":1,\"status\":0}"
 * },
 * "model": {
 * "id": "2457",
 * "structure_all": "4门5座三厢车",
 * "leather_seat": "●",
 * "offical_oil_cost": "-",
 * "real_oil_cost": "-",
 * "lwh": "4705*1782*1465",
 * "engine": "2.0L 146马力 L4",
 * "fuel_label": "93号(京92号)",
 * "horsepower": "146",
 * "max_torque": "183",
 * "intake_form": "自然吸气",
 * "air_conditioning_mode": "自动●",
 * "emissions_l": "2.0",
 * "cylinder_num": "4",
 * "wheel_base": "2675",
 * "driving_mode": "前置前驱",
 * "front_suspension": "双横臂式独立悬架"
 * },
 * "cover_image_urls": [
 * "http://image1.haoche51.com/da7fd025c8843753e8f8c2294a35a5a7e547dfe6.jpg",
 * "http://image1.haoche51.com/0b08ae778a962d49048e30e0fb961fc0bbf9486b.jpg"
 * ],
 * "dealer_price": "4.58"
 * }
 * ]
 * }
 */

public class HCCompareVehicleEntity {

    public HCCompareVehicleEntity() {

    }

    private int id;
    private int brand_id;
    private String vehicle_name;
    private int vehicle_id;
    private long register_time;
    private double seller_price;
    private double quoted_price;
    private double miles;
    private int emission_standard;
    private String geerbox;
    private String seller_words;
    private String checker_words;
    private HCReportEntity report;
    private HCModelEntity model;
    private List<String> cover_image_urls;
    private double dealer_price;
    private String brand_name;
    private String class_name;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public long getRegister_time() {
        return register_time;
    }

    public void setRegister_time(long register_time) {
        this.register_time = register_time;
    }

    public double getSeller_price() {
        return seller_price;
    }

    public void setSeller_price(double seller_price) {
        this.seller_price = seller_price;
    }

    public double getQuoted_price() {
        return quoted_price;
    }

    public void setQuoted_price(double quoted_price) {
        this.quoted_price = quoted_price;
    }

    public double getMiles() {
        return miles;
    }

    public void setMiles(double miles) {
        this.miles = miles;
    }

    public int getEmission_standard() {
        return emission_standard;
    }

    public void setEmission_standard(int emission_standard) {
        this.emission_standard = emission_standard;
    }

    public String getGeerbox() {
        return geerbox;
    }

    public void setGeerbox(String geerbox) {
        this.geerbox = geerbox;
    }

    public String getSeller_words() {
        return seller_words;
    }

    public void setSeller_words(String seller_words) {
        this.seller_words = seller_words;
    }

    public String getChecker_words() {
        return checker_words;
    }

    public void setChecker_words(String checker_words) {
        this.checker_words = checker_words;
    }

    public HCReportEntity getReport() {
        return report;
    }

    public void setReport(HCReportEntity report) {
        this.report = report;
    }

    public HCModelEntity getModel() {
        return model;
    }

    public void setModel(HCModelEntity model) {
        this.model = model;
    }

    public List<String> getCover_image_urls() {
        return cover_image_urls;
    }

    public void setCover_image_urls(List<String> cover_image_urls) {
        this.cover_image_urls = cover_image_urls;
    }

    public double getDealer_price() {
        return dealer_price;
    }

    public void setDealer_price(double dealer_price) {
        this.dealer_price = dealer_price;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
