package com.example.myapplication;

import java.io.Serializable;

public class Bus implements Serializable {

    public String lineNumber;
    public String routeCode;
    public String vehicleID;
    public String lineName;
    public String busLineID;
    public String info;


    public Bus(String lineNumber ,String routeCode, String vehicleID, String lineName, String busLineID, String info) {             //constractors
        this.lineNumber = lineNumber;
        this.routeCode = routeCode;
        this.vehicleID = vehicleID;
        this.lineName = lineName;
        this.busLineID = busLineID;
        this.info = info;
    }

    public Bus(String lineNumber , String lineName , String busLineID){
        this.lineNumber = lineNumber;
        this.routeCode = null;
        this.vehicleID = null;
        this.lineName = lineName;
        this.busLineID = busLineID;
        this.info = null;

    }


    //Getters and Setters

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getBusLineID() {
        return busLineID;
    }

    public void setBusLineID(String busLineID) {
        this.busLineID = busLineID;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }



}
