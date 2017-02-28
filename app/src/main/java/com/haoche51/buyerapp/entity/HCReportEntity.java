package com.haoche51.buyerapp.entity;

public class HCReportEntity {

    public HCReportEntity() {

    }

    private int vehicle_source_id;
    private float vehicle_appearance_score;
    private float vehicle_interior_score;
    private float vehicle_equipment_score;
    private int transfer_times;
    private float vehicle_machine_score;
    private HCSkylightEntity skylight;

    public int getVehicle_source_id() {
        return vehicle_source_id;
    }

    public void setVehicle_source_id(int vehicle_source_id) {
        this.vehicle_source_id = vehicle_source_id;
    }

    public float getVehicle_appearance_score() {
        return vehicle_appearance_score;
    }

    public void setVehicle_appearance_score(float vehicle_appearance_score) {
        this.vehicle_appearance_score = vehicle_appearance_score;
    }

    public float getVehicle_interior_score() {
        return vehicle_interior_score;
    }

    public void setVehicle_interior_score(float vehicle_interior_score) {
        this.vehicle_interior_score = vehicle_interior_score;
    }

    public float getVehicle_equipment_score() {
        return vehicle_equipment_score;
    }

    public void setVehicle_equipment_score(float vehicle_equipment_score) {
        this.vehicle_equipment_score = vehicle_equipment_score;
    }

    public int getTransfer_times() {
        return transfer_times;
    }

    public void setTransfer_times(int transfer_times) {
        this.transfer_times = transfer_times;
    }

    public float getVehicle_machine_score() {
        return vehicle_machine_score;
    }

    public void setVehicle_machine_score(float vehicle_machine_score) {
        this.vehicle_machine_score = vehicle_machine_score;
    }

    public HCSkylightEntity getSkylight() {
        return skylight;
    }

    public void setSkylight(HCSkylightEntity skylight) {
        this.skylight = skylight;
    }

}
