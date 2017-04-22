package com.mishwar.model;

/**
 * Created by mindiii on 5/11/16.
 */

public class AllTrips {
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

    public String getTripTotal() {
        return tripTotal;
    }

    public void setTripTotal(String tripTotal) {
        this.tripTotal = tripTotal;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
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

    public String getPickupLatLong() {
        return pickupLatLong;
    }

    public void setPickupLatLong(String pickupLatLong) {
        this.pickupLatLong = pickupLatLong;
    }

    public String getDestinationLatLong() {
        return destinationLatLong;
    }

    public void setDestinationLatLong(String destinationLatLong) {
        this.destinationLatLong = destinationLatLong;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    String fullName;
    String mobileNumber;
    String tripTotal;
    String bookingId;
    String rideStatus;
    String pickupAddress;
    String destinationAddress;
    String pickupLatLong;

    public String getCounterprice() {
        return Counterprice;
    }

    public void setCounterprice(String counterprice) {
        Counterprice = counterprice;
    }

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

    String reason,Counterprice,priceByDistence,priceByTime;

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    String paymentStatus;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    String destinationLatLong;
    String distance;
    String rideStartDate,rideStartTime,rideEndDate,rideEndTime;

    public String getDate() {
        return date;
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

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    String date;
    String time,rideType;

    public String getRideType() {
        return rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }
}
