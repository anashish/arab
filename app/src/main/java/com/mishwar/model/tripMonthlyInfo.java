package com.mishwar.model;

/**
 * Created by mindiii on 8/12/16.
 */

public class tripMonthlyInfo {
    String placeId,placeName,placeLatLong,studentPlaceId,studentPlaceName,studentPlaceLatLong;

    public String getPlaceLatLong() {
        return placeLatLong;
    }

    public void setPlaceLatLong(String placeLatLong) {
        this.placeLatLong = placeLatLong;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getStudentPlaceId() {
        return studentPlaceId;
    }

    public void setStudentPlaceId(String studentPlaceId) {
        this.studentPlaceId = studentPlaceId;
    }

    public String getStudentPlaceName() {
        return studentPlaceName;
    }

    public void setStudentPlaceName(String studentPlaceName) {
        this.studentPlaceName = studentPlaceName;
    }

    public String getStudentPlaceLatLong() {
        return studentPlaceLatLong;
    }

    public void setStudentPlaceLatLong(String studentPlaceLatLong) {
        this.studentPlaceLatLong = studentPlaceLatLong;
    }

    public void setPlaceId(String placeId) {

        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
