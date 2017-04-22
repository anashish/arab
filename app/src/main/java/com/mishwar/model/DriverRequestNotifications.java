package com.mishwar.model;

import java.io.Serializable;

/**
 * Created by mindiii on 13/10/16.
 */

public class DriverRequestNotifications implements Serializable {
    String destinationLatLong;
    String destinationAddress;
    String pickupAddress,fullName,mobileNumber,price,distance,paymentType;

    public DriverRequestNotifications() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    String rideStatus;

    public String getRideStartTime() {
        return rideStartTime;
    }

    public DriverRequestNotifications(String destinationLatLong, String destinationAddress, String pickupAddress, String rideStatus, String rideStartTime,
                                      String pickupLatLong, String rideStartDate, String bookingId) {
        this.destinationLatLong = destinationLatLong;
        this.destinationAddress = destinationAddress;
        this.pickupAddress = pickupAddress;
        this.rideStatus = rideStatus;
        this.rideStartTime = rideStartTime;
        this.pickupLatLong = pickupLatLong;
        this.rideStartDate = rideStartDate;
        this.bookingId = bookingId;
    }

    public void setRideStartTime(String rideStartTime) {
        this.rideStartTime = rideStartTime;
    }

    public String getDestinationLatLong() {
        return destinationLatLong;
    }

    public void setDestinationLatLong(String destinationLatLong) {
        this.destinationLatLong = destinationLatLong;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getPickupLatLong() {
        return pickupLatLong;
    }

    public void setPickupLatLong(String pickupLatLong) {
        this.pickupLatLong = pickupLatLong;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getRideStartDate() {
        return rideStartDate;
    }

    public void setRideStartDate(String rideStartDate) {
        this.rideStartDate = rideStartDate;
    }

    String rideStartTime;
    String pickupLatLong;
    String rideStartDate;
    String bookingId;

}
