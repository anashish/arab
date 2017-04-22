package com.mishwar.model;

/**
 * Created by gst-10096 on 30/5/16.
 */
public class VehichleTypeBean {
    String carId;
    String VehichleType_Url;
    String carName;
    String carSheet;
    String priceByDistence;
    String priceByTime;
    String Counterprice,vehicleFacilityId,vehicleFacilityName;

    public String getVehicleFacilityId() {
        return vehicleFacilityId;
    }

    public void setVehicleFacilityId(String vehicleFacilityId) {
        this.vehicleFacilityId = vehicleFacilityId;
    }

    public String getVehicleFacilityName() {
        return vehicleFacilityName;
    }

    public void setVehicleFacilityName(String vehicleFacilityName) {
        this.vehicleFacilityName = vehicleFacilityName;
    }

    double finalDistance,timeDuration;

    public String getPriceByDistence() {
        return priceByDistence;
    }

    public void setPriceByDistence(String priceByDistence) {
        this.priceByDistence = priceByDistence;
    }

    public String getPriceByTime() {
        return priceByTime;
    }

    public void setPriceByTime(String priceByTime) {
        this.priceByTime = priceByTime;
    }

    public String getCounterprice() {
        return Counterprice;
    }

    public void setCounterprice(String counterprice) {
        Counterprice = counterprice;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCarSheet() {
        return carSheet;
    }

    public void setCarSheet(String carSheet) {
        this.carSheet = carSheet;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    String price;

    public double getFinalDistance() {
        return finalDistance;
    }

    public void setFinalDistance(double finalDistance) {
        this.finalDistance = finalDistance;
    }

    public double getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(double timeDuration) {
        this.timeDuration = timeDuration;
    }



    public String getVehichleType_Url() {
        return VehichleType_Url;
    }

    public void setVehichleType_Url(String vehichleType_Url) {
        VehichleType_Url = vehichleType_Url;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }



}
