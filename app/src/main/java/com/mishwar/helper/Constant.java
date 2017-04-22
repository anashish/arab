package com.mishwar.helper;

import com.paypal.android.sdk.payments.PayPalConfiguration;

/**
 * Created by mindiii on 10/8/16.
 */
public class Constant {
    // key for run time permissions
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int REQUEST_CODE_PICK_CONTACTS = 100;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    public static final int PERMISSION_REQUEST_CONTACT = 103;
    public static final int PERMISSION_READ_PHONE_STATE = 104;
    public static final int REQUEST_CAMERA = 0;
    public static final int SELECT_FILE = 1;

    public static final int REQUEST_CODE_PAYMENT = 1;
    public static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    public static int CALENDAR_DAY = 0;
    public static final String AUTHTOKEN = "authToken";
    public static final String CANCEL = "cancel";
    public static final String DRIVER = "driver";
    public static final String PASSENGER = "passenger";
    public static final String DEVICETOKEN = "deviceToken";
    public static final String DEVICETYPE = "deviceType";
    public static final String EMAIL = "email";

    public static final String VEHICLENO = "vihicleNumber";
    public static final String LICENCENO = "licenceNumber";
    public static final String VEHICLE_TYPE = "vihicleType";
    public static final String VEHICLE_TYPE_ID = "vihicleType";
    public static final String DRIVER_IDENTITY = "identityProof";
    public static final String DRIVER_IDENTITY_PROFF_STATUS = "identityProofStatus";
    public static final String DRIVER_LICENCE_NO_STATUS = "licenceNumberStatus";
    public static final String DRIVER_LICENCE_EX_DATE = "licenceExDate";
    public static final String DRIVER_CAPTION_IDENTITY_NUMBER = "captionidentitynumber";
    public static final String DRIVER_DATE_OF_BIRTH = "driverdob";

    public static final String TRIP_TYPE_NOW="TN";
    public static final String TRIP_TYPE_LETER="TL";
    public static final String TRIP_TYPE_MONTHLY="TM";

    public static final String FULLNAME = "fullName" ;
    public static final String FACEBOOKID = "facebookId" ;
    public static final String GENDER = "gender";

    public static final String LOGIN = "login";
    public static final String MESSAGE = "message";
    public static final String NOTIFICATIONTYPE = "notificationType" ;
    public static final String OK = "OK";
    public static final String ONE = "1";
    public static final String ROLE = "role" ;
    public static final String STATUS = "status";
    public static final String SOCIALID = "socialId";
    public static final String SOCIALTYPE = "socialType";

    public static final String USERTYPE = "userType";
    public static final String USERID = "userId";
    public static final String USEREMAIL = "userEmail";
    public static final String USERGENDER = "userGender";
    public static final String USERDETAILS = "userDetail";

    public static final String ID = "id";

    public static final String USERNAME = "userName" ;

    public static final String PROFILEIMAGE = "profileImage";

    public static final String LOGINTYPE = "loginType" ;
    public static final String PARAMETERS = "parameters";

    public static final String BASE_URL = "http://menupluss.com/mishwar/index.php";

    // Driver side url
  /*  public static final String URL_DRIVER_REG_VEHICHAL_TYPE = "http://menupluss.com/mishwar/index.php/service/getCarList";
    public static final String URL_SIGNUP_DRIVER = "http://menupluss.com/mishwar/index.php/service/service/driverRagistration";
    public static final String URL_REFERESH_DRIVER_STATUS = "http://menupluss.com/mishwar/index.php/service/user/driverStatus";
    public static final String URL_UPDATE_DRIVER_PROFILE = "http://menupluss.com/mishwar/index.php/service/user/updateProfile";
    public static final String URL_VEHICLE_FEATURES = "http://menupluss.com/mishwar/index.php/service/user/vehicleFacility";
    public static final String URL_UPDATE_VEHICLE_INFO = "http://menupluss.com/mishwar/index.php/service/user/updateVehicle";
    public static final String URL_UPDATE_DRIVER_LATLNG = "http://menupluss.com/mishwar/index.php/service/user/updateDriverLatLong";
    public static final String URL_VEHICHAL_TYPE = "http://menupluss.com/mishwar/index.php/service/user/getCarList";
    public static final String URL_ACCEPT_REQUEST = "http://menupluss.com/mishwar/index.php/service/user/acceptRequest";
    public static final String URL_ALL_REQUESTS = "http://menupluss.com/mishwar/index.php/service/user/getAllRequest";
    public static final String URL_GET_DRIVER_RIDES_INFO = "http://menupluss.com/mishwar/index.php/service/ride/getMyRide";
    public static final String URL_START_AND_END_RIDE = "http://menupluss.com/mishwar/index.php/service/user/startEndRide";
    public static final String URL_DRIVER_ONLINE_OFFLINE_STATUS = "http://menupluss.com/mishwar/index.php/service/user/changeStatus";
    public static final String URL_DRIVER_PAYMENTINFO = "http://menupluss.com/mishwar/index.php/service/ride/getPaymentList";
    public static final String URL_DRIVER_SEND_PAYMENT_DETAILS_TO_PASS = "http://menupluss.com/mishwar/index.php/service/ride/endRidePayment";
    public static final String URL_DRIVER_SEND_AUTHDATA = "http://menupluss.com/mishwar/index.php/service/user/getPaypalAuthData";


    // Passanger side url
    public static final String URL_SIGNUP_PASSENGER = "http://menupluss.com/mishwar/index.php/service/service/socialLogin";
    public static final String URL_CONFIRM_BOOKING = "http://menupluss.com/mishwar/index.php/service/user/rideNowBooking";
    public static final String URL_NEAREST_DRIVER = "http://menupluss.com/mishwar/index.php/service/user/nearestDriver";
    public static final String URL_SEND_DRIVER_REQUEST = "http://menupluss.com/mishwar/index.php/service/user/selectDriverNotify";
    public static final String URL_GET_DRIVER_ACTION = "http://menupluss.com/mishwar/index.php/service/user/acceptOrReject";
    public static final String URL_PASSENGER_GET_TRIP_INFO = "http://menupluss.com/mishwar/index.php/service/user/getPassengerBooking";
    public static final String URL_PASSENGER_CHECK_SOCIAL_REGISTRATION = "http://menupluss.com/mishwar/index.php/service/chekRagistration";
    public static final String URL_PASSENGER_UPDATE_PAYMENT_ON_SERVER = "http://menupluss.com/mishwar/index.php/service/ride/givePayment";
    public static final String URL_UPDATE_PASSENGER_PROFILE = "http://menupluss.com/mishwar/index.php/service/user/updatePassenger";
    public static final String URL_TRIPMONTHLLY_GET_PLACES = "http://menupluss.com/mishwar/index.php/service/ride/getPlace";
    public static final String URL_TRIPMONTHLLY_GET_ROUTE = "http://menupluss.com/mishwar/index.php/service/ride/getRoute";
    public static final String URL_TRIPMONTHLLY_SUBMIT = "http://menupluss.com/mishwar/index.php/service/ride/tripMonthly";

    // Commun Url
    public static final String URL_LOGIN = "http://menupluss.com/mishwar/index.php/service/service/userLogin";
    public static final String URL_VERIFY_NO = "http://menupluss.com/mishwar/index.php/service/service/verifyNo";
    public static final String URL_FORGET_PASS = "http://menupluss.com/mishwar/index.php/service/forgerPassword";
    public static final String URL_LOGOUT = "http://menupluss.com/mishwar/index.php/service/user/userLogout";
    public static final String URL_CANCEL_TRIP_FROM_BOTH = "http://menupluss.com/mishwar/index.php/service/user/cancelBooking";
    public static final String URL_GET_LOCATION_BOTH_SIDE = "http://menupluss.com/mishwar/index.php/service/user/getLocation";*/


    /*----------------------------------------------------------------------------------------------------------------------------*/

    //Driver side url*//*
    public static final String URL_DRIVER_REG_VEHICHAL_TYPE = "http://mashaueer.com/mishwar/index.php/service/getCarList";
    public static final String URL_SIGNUP_DRIVER = "http://mashaueer.com/mishwar/index.php/service/service/driverRagistration";
    public static final String URL_REFERESH_DRIVER_STATUS = "http://mashaueer.com/mishwar/index.php/service/user/driverStatus";
    public static final String URL_UPDATE_DRIVER_PROFILE = "http://mashaueer.com/mishwar/index.php/service/user/updateProfile";
    public static final String URL_VEHICLE_FEATURES = "http://mashaueer.com/mishwar/index.php/service/user/vehicleFacility";
    public static final String URL_UPDATE_VEHICLE_INFO = "http://mashaueer.com/mishwar/index.php/service/user/updateVehicle";
    public static final String URL_UPDATE_DRIVER_LATLNG = "http://mashaueer.com/mishwar/index.php/service/user/updateDriverLatLong";
    public static final String URL_VEHICHAL_TYPE = "http://mashaueer.com/mishwar/index.php/service/user/getCarList";
    public static final String URL_ACCEPT_REQUEST = "http://mashaueer.com/mishwar/index.php/service/user/acceptRequest";
    public static final String URL_ALL_REQUESTS = "http://mashaueer.com/mishwar/index.php/service/user/getAllRequest";
    public static final String URL_GET_DRIVER_RIDES_INFO = "http://mashaueer.com/mishwar/index.php/service/ride/getMyRide";
    public static final String URL_START_AND_END_RIDE = "http://mashaueer.com/mishwar/index.php/service/user/startEndRide";
    public static final String URL_DRIVER_ONLINE_OFFLINE_STATUS = "http://mashaueer.com/mishwar/index.php/service/user/changeStatus";
    public static final String URL_DRIVER_PAYMENTINFO = "http://mashaueer.com/mishwar/index.php/service/ride/getPaymentList";
    public static final String URL_DRIVER_SEND_PAYMENT_DETAILS_TO_PASS = "http://mashaueer.com/mishwar/index.php/service/ride/endRidePayment";
    public static final String URL_DRIVER_SEND_AUTHDATA = "http://mashaueer.com/mishwar/index.php/service/user/getPaypalAuthData";


    //*Passanger side url*//*
    public static final String URL_SIGNUP_PASSENGER = "http://mashaueer.com/mishwar/index.php/service/service/socialLogin";
    public static final String URL_CONFIRM_BOOKING = "http://mashaueer.com/mishwar/index.php/service/user/rideNowBooking";
    public static final String URL_SEND_DRIVER_REQUEST = "http://mashaueer.com/mishwar/index.php/service/user/selectDriverNotify";
    public static final String URL_GET_DRIVER_ACTION = "http://mashaueer.com/mishwar/index.php/service/user/acceptOrReject";
    public static final String URL_PASSENGER_GET_TRIP_INFO = "http://mashaueer.com/mishwar/index.php/service/user/getPassengerBooking";
    public static final String URL_PASSENGER_CHECK_SOCIAL_REGISTRATION = "http://mashaueer.com/mishwar/index.php/service/chekRagistration";
    public static final String URL_PASSENGER_UPDATE_PAYMENT_ON_SERVER = "http://mashaueer.com/mishwar/index.php/service/ride/givePayment";
    public static final String URL_UPDATE_PASSENGER_PROFILE = "http://mashaueer.com/mishwar/index.php/service/user/updatePassenger";
    public static final String URL_TRIPMONTHLLY_GET_PLACES = "http://mashaueer.com/mishwar/index.php/service/ride/getPlace";
    public static final String URL_TRIPMONTHLLY_GET_ROUTE = "http://mashaueer.com/mishwar/index.php/service/ride/getRoute";
    public static final String URL_TRIPMONTHLLY_SUBMIT = "http://mashaueer.com/mishwar/index.php/service/ride/tripMonthly";
    public static final String URL_TRIPMONTHLLY_PATMENT_SUBMIT = "http://mashaueer.com/mishwar/index.php/service/ride/monthlyBookingPayment"; //post
    public static final String URL_DRIVER_RATING = "http://mashaueer.com/mishwar/index.php/service/ride/driverRating"; //post

    //*Commun Url*//*
    public static final String URL_LOGIN = "http://mashaueer.com/mishwar/index.php/service/service/userLogin";
    public static final String URL_VERIFY_NO = "http://mashaueer.com/mishwar/index.php/service/service/verifyNo";
    public static final String URL_FORGET_PASS = "http://mashaueer.com/mishwar/index.php/service/forgerPassword";
    public static final String URL_LOGOUT = "http://mashaueer.com/mishwar/index.php/service/user/userLogout";
    public static final String URL_CANCEL_TRIP_FROM_BOTH = "http://mashaueer.com/mishwar/index.php/service/user/cancelBooking";
    public static final String URL_GET_LOCATION_BOTH_SIDE = "http://mashaueer.com/mishwar/index.php/service/user/getLocation";
    public static final String URL_GET_TRIPMONTHLLY_BOOKING = "http://mashaueer.com/mishwar/index.php/service/ride/getMonthlyBooking";//get


    /*----------------------------------------------------------------------------------------------------------------------------*/


   /* public static final String URL_VEHICHAL_TYPE = "http://192.232.235.253/~aakaizen/taxiapp/index.php/service/user/getCarList";
    public static final String URL_LOGIN = "http://192.232.235.253/~aakaizen/taxiapp/index.php/service/service/userLogin";
    public static final String URL_SIGNUP_DRIVER = "http://192.232.235.253/~aakaizen/taxiapp/index.php/service/service/driverRagistration";
    public static final String URL_SIGNUP_PASSENGER = "http://192.232.235.253/~aakaizen/taxiapp/index.php/service/service/socialLogin";
    public static final String URL_VERIFY_NO = "http://192.232.235.253/~aakaizen/taxiapp/index.php/service/service/verifyNo";*/

    // public static final String HTTP_URL_COMMUN = "http://192.232.235.253/~aakaizen/taxiapp/index.php/service/user/";

    public static final String URL_VEHICHAL_TYPE_CODE = "getQuoteCode";


    /**
     * Manasah Wasl API
     */

    public static final String URL_DRIVER_REG_INFO_MANASAH_WASL_API = "https://wasl.elm.sa/WaslPortalWeb/rest/DriverRegistration/send";
    public static final String URL_DRIVER_VEHICLE_REG_INFO_MANASAH_WASL_API = "https://wasl.elm.sa/WaslPortalWeb/rest/VehicleRegistration/send";
    public static final String URL_TRIP_REG_INFO_MANASAH_WASL_API = "https://wasl.elm.sa/WaslPortalWeb/rest/TripRegistration/send";
    public static final String URL_LOCATION_UPDATE_MANASAH_WASL_API = "https://wasl.elm.sa/WaslPortalWeb/rest/LocationRegistration/send";
    public static final String MANASAH_WASL_API_KEY = "8EF7AA53-3860-4242-955F-7AC218FDE3C1";

}
