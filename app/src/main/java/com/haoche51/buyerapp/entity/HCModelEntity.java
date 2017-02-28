package com.haoche51.buyerapp.entity;

//"model": {
//    "id": "3455", 
//    "structure_all": "4门5座三厢车", 
//    "leather_seat": "●", 
//    "offical_oil_cost": "7.6", 
//    "real_oil_cost": "-", 
//    "lwh": "4500*1735*1470", 
//    "engine": "1.8L 128马力 L4", 
//    "fuel_label": "93号(京92号)", 
//    "horsepower": "128", 
//    "max_torque": "161", 
//    "intake_form": "自然吸气", 
//    "cylinder_num": "4", 
//    "wheel_base": "2610", 
//    "driving_mode": "前置前驱", 
//    "front_suspension": "麦弗逊式独立悬架"
//}, 

public class HCModelEntity {

    public HCModelEntity() {
    }

    private int id;
    private String structure_all;
    private String leather_seat;
    private String offical_oil_cost;
    private String real_oil_cost;
    private String lwh;
    private String engine;
    private String fuel_label;
    private int horsepower;
    private int max_torque;
    private String intake_form;
    private int cylinder_num;
    private int wheel_base;
    private String driving_mode;
    private String front_suspension;
    private String air_conditioning_mode;
    private String emissions_l;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStructure_all() {
        return structure_all;
    }

    public void setStructure_all(String structure_all) {
        this.structure_all = structure_all;
    }

    public String getLeather_seat() {
        return leather_seat;
    }

    public void setLeather_seat(String leather_seat) {
        this.leather_seat = leather_seat;
    }

    public String getOffical_oil_cost() {
        return offical_oil_cost;
    }

    public void setOffical_oil_cost(String offical_oil_cost) {
        this.offical_oil_cost = offical_oil_cost;
    }

    public String getReal_oil_cost() {
        return real_oil_cost;
    }

    public void setReal_oil_cost(String real_oil_cost) {
        this.real_oil_cost = real_oil_cost;
    }

    public String getLwh() {
        return lwh;
    }

    public void setLwh(String lwh) {
        this.lwh = lwh;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getFuel_label() {
        return fuel_label;
    }

    public void setFuel_label(String fuel_label) {
        this.fuel_label = fuel_label;
    }

    public int getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(int horsepower) {
        this.horsepower = horsepower;
    }

    public int getMax_torque() {
        return max_torque;
    }

    public void setMax_torque(int max_torque) {
        this.max_torque = max_torque;
    }

    public String getIntake_form() {
        return intake_form;
    }

    public void setIntake_form(String intake_form) {
        this.intake_form = intake_form;
    }

    public int getCylinder_num() {
        return cylinder_num;
    }

    public void setCylinder_num(int cylinder_num) {
        this.cylinder_num = cylinder_num;
    }

    public int getWheel_base() {
        return wheel_base;
    }

    public void setWheel_base(int wheel_base) {
        this.wheel_base = wheel_base;
    }

    public String getDriving_mode() {
        return driving_mode;
    }

    public void setDriving_mode(String driving_mode) {
        this.driving_mode = driving_mode;
    }

    public String getFront_suspension() {
        return front_suspension;
    }

    public void setFront_suspension(String front_suspension) {
        this.front_suspension = front_suspension;
    }

    public String getAir_conditioning_mode() {
        return air_conditioning_mode;
    }

    public void setAir_conditioning_mode(String air_conditioning_mode) {
        this.air_conditioning_mode = air_conditioning_mode;
    }

    public String getEmissions_l() {
        return emissions_l;
    }

    public void setEmissions_l(String emissions_l) {
        this.emissions_l = emissions_l;
    }
}
