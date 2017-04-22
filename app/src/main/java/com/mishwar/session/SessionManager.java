package com.mishwar.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.mishwar.commun_ui.RoleActivity;
import com.mishwar.helper.Constant;
import com.mishwar.helper.MishwarApplication;

/**
 * Created by mindiii on 10/8/16.
 */

public class SessionManager {

    private static final String TAG = SessionManager.class.getSimpleName();
    public static SessionManager instance = null;
    private Context _context;
    SharedPreferences mypref ;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor2;
    private static final String PREF_NAME = "TaxiApp";
    private static final String IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context){
        this._context = context;
        mypref = _context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor.apply();

        editor2 = mypref.edit();
        editor2.apply();
    }

    public static SessionManager getInstance() {
        if ((instance instanceof SessionManager)) {
            return instance;
        }
        instance = new SessionManager(MishwarApplication.getInstance().getApplicationContext());
        return instance;
    }

    public void setRole(String userRole){
        editor.putString(Constant.USERTYPE, userRole);
        editor.commit();

    }
    public String getRole(){
        return mypref.getString(Constant.USERTYPE, null);
    }

    public void createSession(int userId,String sUserName ,String sFullName, String sSocialId, String sEmail,String sUserType, String sGender,String sUserProfilePic,
                              String sLoginSatus,String mobileNumber, String pAuthToken) {
        String id = String.valueOf(userId);
        editor.putString(Constant.USERID, id);
        editor.putString(Constant.USERNAME, sUserName);
        editor.putString(Constant.FULLNAME, sFullName);
        editor.putString(Constant.FACEBOOKID,sSocialId);
        editor.putString(Constant.USEREMAIL, sEmail);
        editor.putString(Constant.USERGENDER, sGender);
        editor.putString(Constant.USERTYPE, sUserType);
        editor.putString(Constant.PROFILEIMAGE, sUserProfilePic);
        editor.putString(Constant.STATUS, sLoginSatus);
        editor.putString("mobileNumber",mobileNumber);
        editor.putString(Constant.AUTHTOKEN,pAuthToken);
        editor.putBoolean(IS_LOGGEDIN, true);
        editor.commit();
        //    Log.d(TAG, userImage);
    }
    public void createDriverSession(int userId, String sUserName,String sFullName,String sEmail, String sUserType,String sGender,String sUserProfilePic,
                                    String sVehicleNo,String sLicenceNo ,String sIdentityProof,String sLicenceExDate,String dAuthToken) {

        String id = String.valueOf(userId);
        editor.putString(Constant.USERID, id);
        editor.putString(Constant.USERNAME, sUserName);
        editor.putString(Constant.FULLNAME, sFullName);
        editor.putString(Constant.EMAIL, sEmail);
        editor.putString(Constant.USERTYPE, sUserType);
        editor.putString(Constant.USERGENDER, sGender);
        editor.putString(Constant.PROFILEIMAGE, sUserProfilePic);
        editor.putString(Constant.VEHICLENO,sVehicleNo);
        editor.putString(Constant.LICENCENO, sLicenceNo);
        // editor.putString(Constant.VEHICLE_TYPE, sVihicleType);
        editor.putString(Constant.DRIVER_IDENTITY, sIdentityProof);
        editor.putString(Constant.DRIVER_LICENCE_EX_DATE, sLicenceExDate);
        // editor.putString(Constant.STATUS, sLoginSatus);
        editor.putString(Constant.AUTHTOKEN,dAuthToken);
        editor.putBoolean(IS_LOGGEDIN, true);
        editor.commit();
        //    Log.d(TAG, userImage);
    }



    public String getAuthToken(){

        return mypref.getString(Constant.AUTHTOKEN, "");
    }
    public String getMobileNo(){

        return mypref.getString("mobileNumber", "");
    }

    public String getLicenceNo(){

        return mypref.getString(Constant.LICENCENO, "");
    }

    public String getLicenceExDate(){

        return mypref.getString(Constant.DRIVER_LICENCE_EX_DATE, "");
    }

    public String getVehicleNo(){

        return mypref.getString(Constant.VEHICLENO, "");
    }

    public String getUserType(){

        return mypref.getString(Constant.USERTYPE, "");
    }
    public String getUserName(){

        return mypref.getString(Constant.USERNAME, "");
    }
    public String getUserEmail(){

        return mypref.getString(Constant.USEREMAIL, "");
    }
    public String getUserPassword(){

        return mypref.getString("password", "");
    }
    public String getFullName(){

        return mypref.getString(Constant.FULLNAME, "");
    }

    public String getSocialId(){
        return mypref.getString(Constant.SOCIALID, "");
    }


    public String getProfileImage(){

        return mypref.getString(Constant.PROFILEIMAGE, "");
    }
    public String getIdentityProofStatus(){

        return mypref.getString(Constant.DRIVER_IDENTITY_PROFF_STATUS, "");
    }
    public String getLicenceNumberStatus(){

        return mypref.getString(Constant.DRIVER_LICENCE_NO_STATUS, "");
    }
    public String getVehicleTypeName(){

        return mypref.getString("carName", "");
    }
    public String getVihicleTypeID(){

        return mypref.getString(Constant.VEHICLE_TYPE_ID, "");
    }
    public String getVehicleFeatures(){

        return mypref.getString("carFacility", "");
    }

    public String getTripDate(){

        return mypref.getString("rideStartDate", "");
    }
    public String getBookingId(){

        return mypref.getString("bookingId", "");
    }
    public String getTripType(){

        return mypref.getString("rideType", "");
    }
    public String getPaymentType() {

        return mypref.getString("paymentType", "");
    }

    public String getPayPalId(){

        return mypref.getString("paypalId", "");
    }
    public String getLanguage(){

        return mypref.getString("language", "");
    }

    /**************************************************************************************/

    public void setLanguage(String language){
        editor2.putString("language", language);
        editor2.commit();
    }

    public void setPaymentType(String paymentType){
        editor.putString("paymentType", paymentType);
        editor.commit();
    }
    public void setTripDate(String tripDate){
        editor.putString("rideStartDate", tripDate);
        editor.commit();
    }
    public void setBookingId(String bookingId){
        editor.putString("bookingId", bookingId);
        editor.commit();
    }
    public void setTripType(String tripType){
        editor.putString("rideType", tripType);
        editor.commit();
    }
    public void setIdentityProofStatus(String sIdentityProofStatus){
        editor.putString(Constant.DRIVER_IDENTITY_PROFF_STATUS, sIdentityProofStatus);
        editor.commit();
    }
    public void setLicenceNumberStatus(String sLicenceNumberStatus){
        editor.putString(Constant.DRIVER_LICENCE_NO_STATUS, sLicenceNumberStatus);
        editor.commit();
    }

    public void setDriverStatus(String sLoginSatus){
        editor.putString(Constant.STATUS, sLoginSatus);
        editor.commit();
    }
    public void setVehicleTypeName(String sVehicleType){
        editor.putString("carName", sVehicleType);
        editor.commit();
    }

    public void setPaypalId(String sPaypalId){
        editor.putString("paypalId", sPaypalId);
        editor.commit();
    }

    public void setVihicleFeatures(String sVehicleFeatures){
        editor.putString("carFacility", sVehicleFeatures);
        editor.commit();
    }
    public void setVihicleTypeId(String sVihicleTypeId){
        editor.putString(Constant.VEHICLE_TYPE_ID, sVihicleTypeId);
        editor.commit();
    }
    public String getLoginStatus(){
        return mypref.getString(Constant.STATUS, "");
    }

    public String getDeviceToken(){
        return mypref.getString("deviceToken", "");
    }

    public void setDeviceToken(String paramString){
        editor.putString("deviceToken", paramString);
        this.editor.commit();
    }
    public void setPassword(String sPassword){
        editor.putString("password",sPassword);
        this.editor.commit();
    }

    public boolean isPushNotificationEnable(){
        return mypref.getBoolean("setPushNotificationEnable", true);
    }

    public void logout(){
        editor.clear();
        editor.commit();
        FacebookSdk.sdkInitialize(this._context);
        LoginManager.getInstance().logOut();
        Intent showLogin = new Intent(_context,RoleActivity.class);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(showLogin);
    }

    public boolean isLoggedIn(){
        return mypref.getBoolean(IS_LOGGEDIN,false);
    }
    public void setDriverCaptionIdentityNumber(String number){
        editor.putString(Constant.DRIVER_CAPTION_IDENTITY_NUMBER, number);
        editor.commit();
    }
    public void setDriverDateOfBirth(String number){
        editor.putString(Constant.DRIVER_DATE_OF_BIRTH, number);
        editor.commit();
    }
    public String getDriverCaptionIdentityNumber(){
        return mypref.getString(Constant.DRIVER_CAPTION_IDENTITY_NUMBER, "");
    }
    public String getDriverDateOfBirth(){
        return mypref.getString(Constant.DRIVER_DATE_OF_BIRTH, "");
    }
}
