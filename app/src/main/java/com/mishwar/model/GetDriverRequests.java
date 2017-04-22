package com.mishwar.model;

/**
 * Created by mindiii on 13/10/16.
 */

public class GetDriverRequests {
    String destinationLatLong;
    String destinationAddress;
    String pickupAddress;
    String pickupLatLong;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    String rideStartDate;
    String bookingId;
    String createdAt;
    String distance;
    String id;

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

    String driverId;
    String fullName;
    String mobileNumber;

    public String getRideStartTime() {
        return rideStartTime;
    }

    public void setRideStartTime(String rideStartTime) {
        this.rideStartTime = rideStartTime;
    }

    String rideStartTime;

    String driverStatus;
    String userId;

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

    public String getRideStartDate() {
        return rideStartDate;
    }

    public void setRideStartDate(String rideStartDate) {
        this.rideStartDate = rideStartDate;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(String driverStatus) {
        this.driverStatus = driverStatus;
    }

    public String getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(String passengerCount) {
        this.passengerCount = passengerCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRideType() {
        return rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }

    public String getTripTotal() {
        return tripTotal;
    }

    public void setTripTotal(String tripTotal) {
        this.tripTotal = tripTotal;
    }

    public String getVehichleId() {
        return vehichleId;
    }

    public void setVehichleId(String vehichleId) {
        this.vehichleId = vehichleId;
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

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getAcceptBy() {
        return acceptBy;
    }

    public void setAcceptBy(String acceptBy) {
        this.acceptBy = acceptBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String rideType;
    String passengerCount;
    String tripTotal;
    String vehichleId;
    String rideEndDate;
    String rideEndTime;
    String rideStatus;
    String acceptBy;
    String status;


    public GetDriverRequests() {
    }


}
