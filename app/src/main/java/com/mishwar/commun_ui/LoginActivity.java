package com.mishwar.commun_ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mishwar.R;
import com.mishwar.driver_ui.DriverHomeActivity;
import com.mishwar.helper.ConnectionDetector;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.model.CarFacility;
import com.mishwar.model.NearestDriversBean;
import com.mishwar.passanger_ui.HomeActivity;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextView tv_or, tv_createAccount, tv_forgetPass;
    private ImageView iv_fLogin;
    private EditText ed_email, ed_password;
    private Button b_signIn;

    private int userId;
    private String sEmail, sPassword, sSocialId = "", sUserImageUrl, sFullName, sGender, mobileNumber = "", sAuthToken, mDeviceToken = "", mDeviceType = "1", urlParameters, sUserName, sUserProfilePic, sSocialType,
            sUserType, sVihicleTypeId, sLoginSatus, sVehicleNo, sVehicleFeatures="",sLicenceNo, VehicleTypeName, sIdentityProof, sIdentityProofStatus, sLicenceNumberStatus, sLicenceExDate;

    private CallbackManager callbackManager;

    CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface RobotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Medium.ttf");
        Typeface RobotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

       mDeviceToken = FirebaseInstanceId.getInstance().getToken();
/*-------------------------------------------------------------------------------------------------------------------*/

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            if (object.has("email")) {
                                sEmail = object.getString("email");
                            }

                            String firstname = object.getString("first_name");
                            String lastname = object.getString("last_name");
                            String gender = object.getString("gender");
                            sGender = gender;
                            sSocialId = object.getString("id");
                            sUserName = firstname.concat(lastname);
                            sFullName = firstname + " " + lastname;
                            mDeviceToken = FirebaseInstanceId.getInstance().getToken();

                            //   sUserImageUrl = object.getString("picture");
                            sUserImageUrl = "http://graph.facebook.com/" + sSocialId + "/picture?type=large";
                            urlParameters = "userName=" + sUserName + "&fullName=" + sFullName + "&gender=" + gender + "&fullName=" + sFullName + "&email=" + sEmail + "&socialId=" + sSocialId + "&deviceType=" + mDeviceType + "&socialType=facebook&deviceToken=" + mDeviceToken + "&password=" + "&profileImage=" + sUserImageUrl;
                            Log.d(TAG, "social parameter =" + urlParameters);

                            if (AppUtility.isNetworkAvailable(LoginActivity.this)) {
                                GetCheckRegistrationTask();
                            } else {
                                AppUtility.showAlertDialog_SingleButton(LoginActivity.this, getString(R.string.network_not_available), "", "Ok");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name,gender, email,picture");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

                AppUtility.showToast(LoginActivity.this, "Facebook login cancelled", Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(FacebookException exception) {
                AppUtility.showToast(LoginActivity.this, "Error in Facebook login", Toast.LENGTH_SHORT);
            }

        });
/*-------------------------------------------------------------------------------------------------------------------*/


        tv_or = (TextView) findViewById(R.id.login_tv_or);
        tv_or.setTypeface(RobotoLight);

        tv_createAccount = (TextView) findViewById(R.id.login_tv_createAcc);
        tv_createAccount.setTypeface(RobotoMedium);
        tv_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call registration activity
                Intent intent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        tv_forgetPass = (TextView) findViewById(R.id.login_tv_forgetPass);
        tv_forgetPass.setTypeface(RobotoMedium);
        tv_forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call forget password activity
                Intent intent = new Intent(LoginActivity.this, ForgetPassActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
                // AppUtility.showToast(LoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT);
            }
        });
        ed_email = (EditText) findViewById(R.id.login_ed_email);
        ed_email.setTypeface(RobotoLight);
        ed_password = (EditText) findViewById(R.id.login_ed_password);
        ed_password.setTypeface(RobotoLight);

        iv_fLogin = (ImageView) findViewById(R.id.login_iv_flogo);
        iv_fLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isNetworkAvailable(LoginActivity.this)) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
                } else {
                    AppUtility.showToast(LoginActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT);
                }
            }
        });

        b_signIn = (Button) findViewById(R.id.login_b_login);
        b_signIn.setTypeface(RobotoMedium);

        b_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = false;
                sEmail = ed_email.getText().toString().trim();
                sPassword = ed_password.getText().toString().trim();

                if (sEmail.isEmpty()) {
                    cancel = true;
                    ed_email.setError(getString(R.string.please_enter_your_email));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    cancel = true;
                    ed_email.setError(getString(R.string.please_enter_a_valid_email));
                } else if (sPassword.isEmpty()) {
                    cancel = true;
                    ed_password.setError(getString(R.string.please_enter_your_password));
                }
                if (!cancel) {
                    // call login api

                    if (ConnectionDetector.isNetworkAvailable(LoginActivity.this)) {
                        mDeviceToken = FirebaseInstanceId.getInstance().getToken();
                        urlParameters = "email=" + sEmail + "&deviceType=" + mDeviceType + "&deviceToken=" + mDeviceToken + "&password=" + sPassword;
                        GetUserLoginTask();

                    } else {
                        AppUtility.showToast(LoginActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT);
                    }
                }
            }
        });

        if (RoleActivity.sRole.equals(Constant.DRIVER)) {
            tv_or.setVisibility(View.GONE);
            iv_fLogin.setVisibility(View.GONE);
        } else {
            tv_or.setVisibility(View.VISIBLE);
            iv_fLogin.setVisibility(View.VISIBLE);
        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


/*--------------------------------------------------------------------------------------------------------------------*/

    public void GetCheckRegistrationTask() {

        customDialog = new CustomDialog(LoginActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_PASSENGER_CHECK_SOCIAL_REGISTRATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("#" + response);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            Log.d(TAG, "response =" + response);

                            if (status.equalsIgnoreCase("1")) {

                                GetSocialLoginTask();

                            } else if (response.equals("")) {
                                AppUtility.showAlertDialog_SingleButton_finishActivity(LoginActivity.this, "something went wrong !", "", "Ok");
                            } else {
                                Intent verifyNum = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                                verifyNum.putExtra(Constant.PARAMETERS, urlParameters);
                                verifyNum.putExtra(Constant.LOGINTYPE, "fb");
                                verifyNum.putExtra(Constant.FULLNAME, sFullName);
                                verifyNum.putExtra(Constant.SOCIALID, sSocialId);
                                verifyNum.putExtra(Constant.PROFILEIMAGE, "");
                                verifyNum.putExtra(Constant.EMAIL, sEmail);
                                verifyNum.putExtra(Constant.USERNAME, sUserName);
                                verifyNum.putExtra("gender", sGender);
                                startActivity(verifyNum);
                                //   AppUtility.showToast(LoginActivity.this, message, Toast.LENGTH_SHORT);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customDialog.cancel();
                        Toast.makeText(LoginActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                // SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd");
                //  String todayDate = writeFormat.format(date);
                Map<String, String> header = new HashMap<>();
                header.put("socialId", sSocialId);
                return header;
            }


        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
/*-----------------------------------------------------------------------------------------------------------------------------------*/

    public void GetSocialLoginTask() {

        customDialog = new CustomDialog(LoginActivity.this);
        customDialog.show();
        mDeviceToken = FirebaseInstanceId.getInstance().getToken();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_SIGNUP_PASSENGER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("#" + response);
                        JSONObject jsonObject;
                        customDialog.dismiss();
                        try {
                            jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            Log.d(TAG, "authToken =" + response);

                            if (status.equalsIgnoreCase("1")) {
                                SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                JSONObject usedetailobject = jsonObject.getJSONObject(Constant.USERDETAILS);
                                userId = usedetailobject.getInt(Constant.USERID);
                                sAuthToken = usedetailobject.getString(Constant.AUTHTOKEN);
                                sGender = usedetailobject.getString(Constant.GENDER);
                                sSocialId = usedetailobject.getString(Constant.SOCIALID);
                                sSocialType = usedetailobject.getString(Constant.SOCIALTYPE);
                                sUserName = usedetailobject.getString(Constant.USERNAME);
                                //sPassword = usedetailobject.getString("password");
                                sEmail = usedetailobject.getString("email");
                                //   sUserProfilePic = usedetailobject.getString(Constant.PROFILEIMAGE);
                                sUserType = usedetailobject.getString(Constant.USERTYPE);
                                sUserProfilePic = usedetailobject.getString(Constant.PROFILEIMAGE);
                                sLoginSatus = usedetailobject.getString("status");
                                // mobileNumber = usedetailobject.getString("mobileNumber");
                                //socialId
                                if (sUserType.equals("passenger")) {
                                    JSONObject passengerDetailobject = usedetailobject.getJSONObject("passengerDetail");
                                    sFullName = passengerDetailobject.getString("fullName");
                                    mobileNumber = passengerDetailobject.getString("mobileNumber");
                                }

                                sessionManager.createSession(userId, sUserName, sFullName, sSocialId, sEmail, sUserType, sGender, sUserProfilePic, sLoginSatus,mobileNumber, sAuthToken);

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                finish();

                            } else if (response.isEmpty()) {
                                AppUtility.showAlertDialog_SingleButton_finishActivity(LoginActivity.this, message, "", "Ok");
                            } else {
                                AppUtility.showToast(LoginActivity.this, message, Toast.LENGTH_SHORT);

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customDialog.cancel();
                        Toast.makeText(LoginActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                // SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd");
                //  String todayDate = writeFormat.format(date);
                Map<String, String> header = new HashMap<>();

                header.put("fullName", sFullName);
                header.put("userName", sUserName);
                header.put("email", sEmail);
                header.put("socialId", sSocialId);
                header.put("deviceType", "1");
                header.put("deviceToken", mDeviceToken);

                header.put("userType", "passenger");
                header.put("mobileNumber", mobileNumber);
                header.put("socialtype", sSocialType);
                header.put("gender", sGender);
                if (!sUserImageUrl.equals("")) {
                    header.put("profileImage", sUserImageUrl);
                }

                return header;
            }

         /*   @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("authToken", sessionManager.getAuthToken());
                return header;
            }*/
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    /*----------------------------------------------------------------------------------------------------------------------------*/
    //**************************************************************************************************************************************
    public void GetUserLoginTask() {

        customDialog = new CustomDialog(LoginActivity.this);
        customDialog.show();
        mDeviceToken = FirebaseInstanceId.getInstance().getToken();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("#" + response);
                        JSONObject jsonObject;
                        try {

                            jsonObject = new JSONObject(response);
                            String status = jsonObject.getString(Constant.STATUS);
                            String   message = jsonObject.getString(Constant.MESSAGE);

                            SessionManager sessionManager = new SessionManager(LoginActivity.this);
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                            if (status.equals(Constant.ONE)) {
                                JSONObject usedetailobject = jsonObject.getJSONObject(Constant.USERDETAILS);
                                userId = usedetailobject.getInt(Constant.USERID);
                                sAuthToken = usedetailobject.getString(Constant.AUTHTOKEN);
                                sGender = usedetailobject.getString(Constant.GENDER);
                                sPassword = usedetailobject.getString("password");
                                sEmail = usedetailobject.getString("email");
                                sSocialId = usedetailobject.getString(Constant.SOCIALID);
                                sSocialType = usedetailobject.getString(Constant.SOCIALTYPE);
                                sUserName = usedetailobject.getString(Constant.USERNAME);
                                sUserType = usedetailobject.getString(Constant.USERTYPE);
                                sUserProfilePic = usedetailobject.getString(Constant.PROFILEIMAGE);
                                sLoginSatus = usedetailobject.getString("status");

                                if (sUserType.equals("passenger")) {
                                    JSONObject passengerDetailobject = usedetailobject.getJSONObject("passengerDetail");
                                    sFullName = passengerDetailobject.getString("fullName");
                                    mobileNumber = passengerDetailobject.getString("mobileNumber");

                                    sessionManager.createSession(userId, sUserName, sFullName, sSocialId, sEmail, sUserType, sGender, sUserProfilePic, sLoginSatus,mobileNumber, sAuthToken);
                                    sessionManager.setPassword(sPassword);
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                    finish();

                                }

                                if (sUserType.equals("driver")) {
                                    String paypalId="";
                                    JSONObject driverDetailObject = usedetailobject.getJSONObject("driverDetail");
                                    sVehicleNo = driverDetailObject.getString("vihicleNumber");
                                    sLicenceNo = driverDetailObject.getString("licenceNumber");
                                    sFullName = driverDetailObject.getString("fullName");
                                    VehicleTypeName = driverDetailObject.getString("carName");
                                    sVihicleTypeId = driverDetailObject.getString("vihicleType");
                                    sIdentityProofStatus = driverDetailObject.getString("identityProofStatus");
                                    sLicenceNumberStatus = driverDetailObject.getString("licenceNumberStatus");
                                    sLicenceExDate = driverDetailObject.getString("licenceExDate");
                                    sIdentityProof = driverDetailObject.getString("identityProof");
                                    paypalId = driverDetailObject.getString("paypalId");

                                    JSONArray carFacilityJsonArray = driverDetailObject.getJSONArray("carFacility");
                                    NearestDriversBean item = new NearestDriversBean();

                                    if(carFacilityJsonArray != null ) {
                                        List<CarFacility> carFacilities = new ArrayList<>();
                                        for (int j = 0; j < carFacilityJsonArray.length(); j++) {
                                            JSONObject jsonObjCarFacility = carFacilityJsonArray.getJSONObject(j);
                                            CarFacility carFacility = new CarFacility();
                                            carFacility.setFacilityId(jsonObjCarFacility.getString("facilityId").toString().trim());
                                            carFacility.setFacilityName(jsonObjCarFacility.getString("facilityName").toString().trim());
                                            carFacilities.add(carFacility);

                                        }
                                        item.setCarFacilities(carFacilities);

                                        List<CarFacility> carFacility = new ArrayList<>();
                                        carFacility = item.getCarFacilities();
                                        for (int k = 0; k < carFacility.size(); k++){

                                            if(sVehicleFeatures.equals("")){
                                                sVehicleFeatures = carFacility.get(k).getFacilityName();

                                            }else {
                                                sVehicleFeatures = sVehicleFeatures + "," +carFacility.get(k).getFacilityName();
                                            }

                                            //driversBean.setSingleFacilitiesName(carFacility.get(j).getFacilityName());
                                            //  facility = driversBean.getSingleFacilitiesName().toString();
                                        }
                                    }

                                    sessionManager.createDriverSession(userId, sUserName, sFullName, sEmail, sUserType, sGender, sUserProfilePic, sVehicleNo, sLicenceNo,
                                            sIdentityProof, sLicenceExDate, sAuthToken);

                                    sessionManager.setDriverStatus(sLoginSatus);
                                    sessionManager.setLicenceNumberStatus(sLicenceNumberStatus);
                                    sessionManager.setIdentityProofStatus(sIdentityProofStatus);
                                    sessionManager.setVihicleTypeId(sVihicleTypeId);
                                    sessionManager.setVehicleTypeName(VehicleTypeName);
                                    sessionManager.setVihicleFeatures(sVehicleFeatures);
                                    sessionManager.setPaypalId(paypalId);


                                    Intent intent = new Intent(LoginActivity.this, DriverHomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                    finish();
                                    //  }

                                }
                                else{
                                    System.out.println("sUserType : " + sUserType);

                                }

                            } else {
                                AppUtility.showToast(LoginActivity.this,message,1);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customDialog.cancel();
                        Toast.makeText(LoginActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                //   urlParameters = "email=" + sEmail + "&deviceType=" + mDeviceType + "&deviceToken=" + mDeviceToken + "&password=" + sPassword;

                header.put("email", sEmail);
                header.put("deviceType", "1");
                header.put("deviceToken", mDeviceToken);
                header.put("password", sPassword);

                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }

}