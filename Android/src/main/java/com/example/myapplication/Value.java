package com.example.myapplication;
import java.io.Serializable;

public class Value implements Serializable {


    public String lineNumber;
    public String routeCode;
    public String vehicleID;
    public String lineName;
    public String busLineID;
    public String info;
    public double lat;
    public double lon;

    public Value(String lineNumber , String routeCode , String vehicleID , String lineName , String busLineID , String info , double lat , double lon){  // Constractor
        this.lineNumber=lineNumber;
        this.routeCode=routeCode;
        this.vehicleID=vehicleID;
        this.lineName=lineName;
        this.busLineID=busLineID;
        this.info=info;
        this.lat=lat;
        this.lon=lon;
    }

    public Value(){
        this.lineNumber=null;
        this.routeCode=null;
        this.vehicleID=null;
        this.lineName=null;
        this.busLineID=null;
        this.info=null;
        this.lat=0;
        this.lon=0;
    }


        //Getters and Setters





    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


}
