package com.mishwar.commun_ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mishwar.R;
import com.mishwar.driver_ui.DriverRegistrationActivity;
import com.mishwar.helper.ConnectionDetector;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.passanger_ui.HomeActivity;
import com.mishwar.passanger_ui.PassengerRegistrationActivity;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class VerifyPhoneActivity extends AppCompatActivity {

    public static final String TAG = VerifyPhoneActivity.class.getSimpleName();

    private TextView tv_one,tv_two,tv_three,tv_countryCode,tv_title;
    private Spinner spn_country;
    private EditText ed_otp,ed_phoneNumber;
    private Button b_ok,b_next;
    private ImageView iv_back;
    String countryCode, countryCodeSingal;
    private String sEmail,sCuntryCode,sSocialId,sUserImageUrl,sFullName,sGender,sAuthToken,mDeviceToken,mobileNumber,urlParameters,
            sUserName,sSocialType,sUserType,sUserProfilePic,sLoginSatus,otpUrlParameter="",sCountryCode,sPhoneNumber,sOtp="",loginType="normal";

    private int userId;
    CustomDialog customDialog;
    private String splitedcountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.vpa_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface RobotoMedium = Typeface.createFromAsset(getAssets(),"fonts/Roboto_Medium.ttf");
        Typeface RobotoLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");

        Intent intent = getIntent();


        if(intent.hasExtra(Constant.LOGINTYPE)){
            loginType = intent.getStringExtra(Constant.LOGINTYPE);
            if(loginType.equals("fb")){
                urlParameters = intent.getStringExtra(Constant.PARAMETERS);
                sFullName = intent.getStringExtra(Constant.FULLNAME);
                sUserName = intent.getStringExtra(Constant.USERNAME);
                sEmail  = intent.getStringExtra(Constant.EMAIL);
                sSocialId = intent.getStringExtra(Constant.SOCIALID);
                //  sFullName = intent.getStringExtra(Constant.FULLNAME);
                sUserImageUrl = intent.getStringExtra(Constant.PROFILEIMAGE);
                sGender = intent.getStringExtra("gender");
                loginType = intent.getStringExtra(Constant.LOGINTYPE);

                System.out.println("facebook res : ==="+urlParameters);
            }
        }

        tv_title = (TextView)findViewById(R.id.vpa_tv_title);
        tv_title.setTypeface(RobotoMedium);
        tv_one = (TextView)findViewById(R.id.vpa_tv_one);
        tv_one.setTypeface(RobotoLight);
        tv_two = (TextView)findViewById(R.id.vpa_tv_two);
        tv_two.setTypeface(RobotoLight);
        tv_three = (TextView)findViewById(R.id.vpa_tv_three);
        tv_three.setTypeface(RobotoMedium);
        tv_countryCode = (TextView)findViewById(R.id.vap_tv_countrycode);
        tv_countryCode.setTypeface(RobotoLight);

        spn_country = (Spinner)findViewById(R.id.vpa_spinner_country);

        spn_country.setEnabled(true);
        spn_country.setClickable(true);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(VerifyPhoneActivity.this,R.layout.item_country_spinner,getResources().getStringArray(R.array.CountryCodes));
        spn_country.setAdapter(spinnerAdapter);
        spn_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String   sCountry = getResources().getStringArray(R.array.CountryCodes)[position] ;
                String code = getResources().getStringArray(R.array.CountryCodes)[position];
                // sCountryCode = trimFirstWord(code);
                sCountryCode = code;
                String splitedCode[] = code.split(" ");
                countryCodeSingal = splitedCode[0];
                countryCode = splitedCode[1];
                tv_countryCode.setText(sCountryCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //*******************************************************************
        String countrycodewithName =   GetCountryZipCode();

        if(countrycodewithName!=null&&!countrycodewithName.equals("")){
            String[] sp = countrycodewithName.split(" ");
            if(sp!=null&&sp.length>0){
                if(sp[1]!=null&&!sp[1].equals("")){
                    // textcodevalue.setText(sp[1]);
                    countryCode = sp[1];
                    countryCodeSingal =sp[1];

                    int index =   getIndex(spn_country, countryCodeSingal);
                    spn_country.setSelection(index);
                }
                if(sp[0]!=null&&!sp[0].equals("")){
                    //  textcodevalue.setText(sp[0] + "," + "+" + sp[1]);
                    countryCodeSingal =sp[1];
                    countryCode = countryCode +" , "+"+"+sp[0];

                    int index =   getIndex(spn_country, countryCodeSingal);
                    spn_country.setSelection(index);

                }
            }

        }

//*******************************************************************
        iv_back = (ImageView)findViewById(R.id.vpa_iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ed_otp = (EditText)findViewById(R.id.vpa_otp);
        ed_phoneNumber = (EditText)findViewById(R.id.vpa_ed_phonenumber);

        b_ok = (Button)findViewById(R.id.vpa_btn_ok);
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = false;
                sPhoneNumber = ed_phoneNumber.getText().toString().trim();

                if(sPhoneNumber.isEmpty()){
                    cancel =true;
                    ed_phoneNumber.setError(getString(R.string.please_enter_mobile_number));
                }
                else if (!isValidNo( countryCodeSingal,sPhoneNumber)) {
                    cancel =true;
                    ed_phoneNumber.requestFocus();
                }

                // call twilio to send otp
                if(!cancel){

                    sCuntryCode = tv_countryCode.getText().toString();
                    String splitedCode[] = sCuntryCode.split(" ");
                    String firstCode = splitedCode[0];
                    String countryCode = splitedCode[1];
                    splitedcountryCode = splitedCode[1];
                    tv_countryCode.setText(sCountryCode);

                    otpUrlParameter = countryCode+""+sPhoneNumber;

                    if (AppUtility.isNetworkAvailable(VerifyPhoneActivity.this))
                    {
                        GetOtpTask();
                    }
                    else {
                        AppUtility.showAlertDialog_SingleButton(VerifyPhoneActivity.this, getString(R.string.network_not_available), "", "Ok");
                    }

                }

            }
        });
//*******************************************************************
        b_next = (Button)findViewById(R.id.vpa_btn_next);
        b_next.setTypeface(RobotoMedium);
        b_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = false;
                sPhoneNumber = ed_phoneNumber.getText().toString().trim();
                String otp = ed_otp.getText().toString().trim();
                System.out.println("postData no.============" + otpUrlParameter);
                if (sPhoneNumber.isEmpty()) {
                    cancel = true;
                    ed_phoneNumber.setError(getString(R.string.please_enter_mobile_number));
                }

                else if (otp.isEmpty()) {
                    cancel = true;
                    ed_otp.setError(getString(R.string.please_enter_otp));
                }

                if (!cancel) {
                    if (!sOtp.equals(""))
                    {
                        if (sOtp.equals(otp)) {
                            if (loginType.equals("fb")) {
                                if (ConnectionDetector.isNetworkAvailable(VerifyPhoneActivity.this)) {
                                    //   urlParameters = "mobileNumber"+urlParameters1;
                                    mobileNumber = "" + ed_phoneNumber.getText().toString().trim();

                                    GetSocialLoginTask();
                                } else {
                                    AppUtility.showToast(VerifyPhoneActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
                                }
                            } else {
                                if (RoleActivity.sRole.equals(Constant.DRIVER)) {
                                    String pMobileNo = splitedcountryCode + sPhoneNumber;
                                    Intent intent = new Intent(VerifyPhoneActivity.this, DriverRegistrationActivity.class);
                                    intent.putExtra("dMobileNo", pMobileNo);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                    finish();
                                } else {
                                    String dMobileNo = splitedcountryCode + sPhoneNumber;
                                    Intent intent = new Intent(VerifyPhoneActivity.this, PassengerRegistrationActivity.class);
                                    intent.putExtra("pMobileNo", dMobileNo);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                    finish();
                                }
                            }
                        } else {
                            AppUtility.showToast(VerifyPhoneActivity.this, "Wrong OTP! Please verify again", Toast.LENGTH_SHORT);

                        }
                    }
                }

            }
        });

    }
    /*-------------------------------------------------------------------------------------------------------------------*/
    String trimFirstWord(String s) {
        return s.contains(" ") ? s.substring(s.indexOf(' ')).trim() : "";
    }
    /*-------------------------------------------------------------------------------------------------------------------*/
    // Contact No validation by cuntry code.
    private boolean isValidNo(String countryCodeSingal, String contactNo){
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(VerifyPhoneActivity.this);
            Phonenumber.PhoneNumber swissNumberProto = null;
            try {
                String formattedNumber = PhoneNumberUtils.formatNumber(contactNo);

                swissNumberProto = phoneUtil.parse(formattedNumber, countryCodeSingal);
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
            Log.v("Isnumber", "" + swissNumberProto);
            boolean isValid = phoneUtil.isValidNumber(swissNumberProto);

            if(!isValid){
                ed_phoneNumber.requestFocus();
                AppUtility.showToast(VerifyPhoneActivity.this, "Enter your valid contact number with country code.", Toast.LENGTH_SHORT);
                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    /*-------------------------------------------------------------------------------------------------------------------*/
    public String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(" ");
            if(g[0].trim().equals(CountryID.trim())){
                CountryZipCode=g[1];
                break;
            }
        }
        return CountryZipCode+" "+CountryID;
    }
    /*-------------------------------------------------------------------------------------------------------------------*/
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().contains(myString)){
                index = i;
                return index;
            }
        }
        return index;
    }

    /****************************************************************************************************************************************/

    public void GetSocialLoginTask() {

        customDialog = new CustomDialog(VerifyPhoneActivity.this);
        customDialog.show();
        mDeviceToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("mDeviceToken == " + mDeviceToken);

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
                                SessionManager sessionManager = new SessionManager(VerifyPhoneActivity.this);
                                JSONObject usedetailobject = jsonObject.getJSONObject(Constant.USERDETAILS);
                                userId = usedetailobject.getInt(Constant.USERID);
                                sAuthToken = usedetailobject.getString(Constant.AUTHTOKEN);
                                sGender = usedetailobject.getString(Constant.GENDER);
                                sSocialId = usedetailobject.getString(Constant.SOCIALID);
                                sSocialType  = usedetailobject.getString(Constant.SOCIALTYPE);
                                sUserName = usedetailobject.getString(Constant.USERNAME);
                                //   sUserProfilePic = usedetailobject.getString(Constant.PROFILEIMAGE);
                                sUserType = usedetailobject.getString(Constant.USERTYPE);
                                sUserProfilePic = usedetailobject.getString(Constant.PROFILEIMAGE);
                                sLoginSatus = usedetailobject.getString("status");
                                // mobileNumber = usedetailobject.getString("mobileNumber");
                                //socialId
                                if(sUserType.equals("passenger")) {
                                    JSONObject passengerDetailobject = usedetailobject.getJSONObject("passengerDetail");
                                    sFullName = passengerDetailobject.getString("fullName");
                                    mobileNumber = passengerDetailobject.getString("mobileNumber");
                                }

                                sessionManager.createSession(userId,sUserName,sFullName,sSocialId,sEmail,sUserType,sGender,sUserProfilePic,sLoginSatus,mobileNumber,sAuthToken);

                                Intent intent = new Intent(VerifyPhoneActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                finish();


                            } else if (response.isEmpty()) {
                                AppUtility.showAlertDialog_SingleButton_finishActivity(VerifyPhoneActivity.this, message, "", "Ok");
                            } else {
                                AppUtility.showToast(VerifyPhoneActivity.this, message, Toast.LENGTH_SHORT);

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
                        Toast.makeText(VerifyPhoneActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                // SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd");
                //  String todayDate = writeFormat.format(date);
                Map<String, String> header = new HashMap<>();
                sSocialType = "fb";
                sUserImageUrl = "http://graph.facebook.com/" + sSocialId + "/picture?type=large";

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

    /****************************************************************************************************************************************/

    public void GetOtpTask() {

        customDialog = new CustomDialog(VerifyPhoneActivity.this);
        customDialog.show();
        mDeviceToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("mDeviceToken == " + mDeviceToken);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_VERIFY_NO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("#" + response);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            Log.d(TAG,"response ="+response);

                            if (status.equalsIgnoreCase("1")) {
                              //  sOtp = message;

                                sOtp = "456789";

                                // JSONObject usedetailobject = jsonObj.getJSONObject(Constant.USERDETAILS);
                                // sAuthToken = usedetailobject.getString(Constant.AUTHTOKEN);
                                AppUtility.showToast(VerifyPhoneActivity.this, getString(R.string.otp_sent), Toast.LENGTH_SHORT);


                            } else if (response.isEmpty()) {
                                AppUtility.showAlertDialog_SingleButton_finishActivity(VerifyPhoneActivity.this, message, "", "Ok");
                            }
                            else {
                                AppUtility.showToast(VerifyPhoneActivity.this, message, Toast.LENGTH_SHORT);

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
                        Toast.makeText(VerifyPhoneActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                // SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd");
                //  String todayDate = writeFormat.format(date);
                Map<String, String> header = new HashMap<>();
                header.put("mobileNumber", otpUrlParameter);
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


    /****************************************************************************************************************************************/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    /******************************************************************************************************************************************/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                !getLocationOnScreen(ed_phoneNumber).contains(x, y)) {
            InputMethodManager input = (InputMethodManager)
                    VerifyPhoneActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(ed_phoneNumber.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    protected Rect getLocationOnScreen(EditText mEditText) {
        Rect mRect = new Rect();
        int[] location = new int[2];

        mEditText.getLocationOnScreen(location);

        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mEditText.getWidth();
        mRect.bottom = location[1] + mEditText.getHeight();

        return mRect;
    }
}