package com.mishwar.model;

/**
 * Created by mindiii on 28/11/16.
 */

public class paymentDetails {
    String carName;
    String Counterprice;
    String priceByDistenc;
    String priceByTime,rideEndDate,rideEndTime,rideStartDate,rideStartTime;

    public String getCarName() {
        return carName;
    }

    public String getRideEndDate() {
        return rideEndDate;
    }

    public void setRideEndDate(String rideEndDate) {
        this.rideEndDate = rideEndDate;
    }

    public String getRideEndTime() {
        return rideEndTime;
    }

    public void setRideEndTime(String rideEndTime) {
        this.rideEndTime = rideEndTime;
    }

    public String getRideStartDate() {
        return rideStartDate;
    }

    public void setRideStartDate(String rideStartDate) {
        this.rideStartDate = rideStartDate;
    }

    public String getRideStartTime() {
        return rideStartTime;
    }

    public void setRideStartTime(String rideStartTime) {
        this.rideStartTime = rideStartTime;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCounterprice() {
        return Counterprice;
    }

    public void setCounterprice(String counterprice) {
        Counterprice = counterprice;
    }

    public String getPriceByDistenc() {
        return priceByDistenc;
    }

    public void setPriceByDistenc(String priceByDistenc) {
        this.priceByDistenc = priceByDistenc;
    }

    public String getPriceByTime() {
        return priceByTime;
    }

    public void setPriceByTime(String priceByTime) {
        this.priceByTime = priceByTime;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTripCost() {
        return tripCost;
    }

    public void setTripCost(String tripCost) {
        this.tripCost = tripCost;
    }

    String fullName;
    String bookingId;
    String pickupAddress;
    String destinationAddress;
    String distance;
    String tripCost;

}
