package com.mishwar.model;

import java.util.List;

/**
 * Created by mindiii on 23/9/16.
 */
public class NearestDriversBean {

String userId;
    String mobileNumber;
    String vihicleNumber;
    String fullName;
    String latitude;
    String bookingId;

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    String profileImage;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    String longitude;
    String distance;
    String carId;
    String userName;
    List<CarFacility> carFacilities;

    public List<CarFacility> getCarFacilities() {
        return carFacilities;
    }

    public void setCarFacilities(List<CarFacility> carFacilities) {
        this.carFacilities = carFacilities;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    String carName;
    String singleFacilitiesName;
    String rating;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSingleFacilitiesName() {
        return singleFacilitiesName;
    }

    public void setSingleFacilitiesName(String singleFacilitiesName) {
        this.singleFacilitiesName = singleFacilitiesName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getVihicleNumber() {
        return vihicleNumber;
    }

    public void setVihicleNumber(String vihicleNumber) {
        this.vihicleNumber = vihicleNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public NearestDriversBean() {
    }


}

