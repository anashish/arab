package com.mishwar.passanger_ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mishwar.R;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.model.AllTrips;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.PaymentClass;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mishwar.helper.Constant.REQUEST_CODE_PAYMENT;

public class ShowPassengerMonthlyTripActivity extends AppCompatActivity {
    private ArrayList<AllTrips> tmArraylist;
    TextView tvTm_bookingid,tvTm_Name,tvTm_From,tvTm_To,tvTm_Time,tvTm_Date,tvTm_Distance,tvTm_Cost,tvTm_Contact,tvMonthlyTripEmpty,driver_actionbarLayout_title;
    LinearLayout lyTm_Contact,lyTm_Name;
    RelativeLayout activity_show_passenger_monthly_trip;
    Button  btnTmMakePayment;
    PaymentClass paymentClass;
    String tripTotal,bookingId="",paypal_id="";
    private ImageView driver_actionbar_btton_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_passenger_monthly_trip);
        initializeViews();
    }

    private void initializeViews() {
        tmArraylist = new ArrayList<AllTrips>();
        paymentClass = new PaymentClass(ShowPassengerMonthlyTripActivity.this);

        tvTm_bookingid = (TextView) findViewById(R.id.tvTm_bookingid);
        tvTm_Name = (TextView) findViewById(R.id.tvTm_Name);
        tvTm_From = (TextView) findViewById(R.id.tvTm_From);
        tvTm_To = (TextView) findViewById(R.id.tvTm_To);
        tvTm_Time = (TextView) findViewById(R.id.tvTm_Time);
        tvTm_Date = (TextView) findViewById(R.id.tvTm_Date);
        tvTm_Distance = (TextView) findViewById(R.id.tvTm_Distance);
        tvTm_Cost = (TextView) findViewById(R.id.tvTm_Cost);
        tvTm_Contact = (TextView) findViewById(R.id.tvTm_Contact);
        tvMonthlyTripEmpty = (TextView) findViewById(R.id.tvMonthlyTripEmpty);
        driver_actionbarLayout_title = (TextView) findViewById(R.id.driver_actionbarLayout_title);
        driver_actionbar_btton_back = (ImageView) findViewById(R.id.driver_actionbar_btton_back);

        driver_actionbarLayout_title.setText(getString(R.string.text_trip_monthly));

        lyTm_Contact = (LinearLayout) findViewById(R.id.lyTm_Contact);
        lyTm_Name = (LinearLayout) findViewById(R.id.lyTm_Name);
        activity_show_passenger_monthly_trip = (RelativeLayout) findViewById(R.id.activity_show_passenger_monthly_trip);

        btnTmMakePayment = (Button) findViewById(R.id.btnTmMakePayment);

        if(AppUtility.isNetworkAvailable(this)) {

            GetPassMonthllyTripInfoTask();

        }else {
            Toast.makeText(this,getResources().getString(R.string.network_not_available), Toast.LENGTH_SHORT).show();
        }

        btnTmMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentClass.starProcessToPay(tripTotal, "USD", REQUEST_CODE_PAYMENT, "Payment for Mishwar taxi app!");
            }
        });

        driver_actionbar_btton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

    }

    /****************************************************************************************************************************************/

    public void GetPassMonthllyTripInfoTask() {
        final CustomDialog customDialog = new CustomDialog(this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_GET_TRIPMONTHLLY_BOOKING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("passenger monthlly trip response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {

                                tmArraylist.clear();

                                JSONArray jsonArray = jsonObj.getJSONArray("result");
                                if(jsonArray != null ) {
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        AllTrips item = new AllTrips();
                                        activity_show_passenger_monthly_trip.setVisibility(View.VISIBLE);

                                        item.setBookingId(jsonObject.getString("bookingId").toString().trim());
                                        item.setRideStatus(jsonObject.getString("rideStatus").toString().trim());
                                        item.setPickupLatLong(jsonObject.getString("pickupLatLong").toString().trim());
                                        item.setDestinationAddress(jsonObject.getString("destinationAddress").toString().trim());
                                        item.setDestinationLatLong(jsonObject.getString("destinationLatLong").toString().trim());
                                        item.setPickupAddress(jsonObject.getString("pickupAddress").toString().trim());

                                        item.setTripTotal(jsonObject.getString("tripTotal").toString().trim());
                                        item.setDistance(jsonObject.getString("distance").toString().trim());
                                        item.setRideStartTime(jsonObject.getString("rideStartTime").toString().trim());
                                        item.setRideStartDate(jsonObject.getString("rideStartDate").toString().trim());

                                        String rideStatus = jsonObject.getString("rideStatus").toString();
                                        if (rideStatus.equals("3")){
                                            JSONObject passengerDetail = jsonObject.getJSONObject("driverDetail");
                                            if (passengerDetail.has("fullName")){
                                                lyTm_Name.setVisibility(View.VISIBLE);
                                                lyTm_Contact.setVisibility(View.VISIBLE);
                                                btnTmMakePayment.setVisibility(View.GONE);
                                                item.setFullName(passengerDetail.getString("fullName").toString().trim());
                                                item.setMobileNumber(passengerDetail.getString("mobileNumber").toString().trim());
                                            }
                                        }else {
                                            lyTm_Name.setVisibility(View.GONE);
                                            lyTm_Contact.setVisibility(View.GONE);
                                        }

                                        tmArraylist.add(item);

                                    }

                                    if(tmArraylist.size()>0) {

                                        for (int i = 0; i < tmArraylist.size(); i++)
                                        {
                                            tripTotal = tmArraylist.get(i).getTripTotal().toString();
                                            bookingId = tmArraylist.get(i).getBookingId().toString();

                                            String rideStatus = tmArraylist.get(i).getRideStatus().toString();

                                            if (rideStatus.equals("1")){
                                                btnTmMakePayment.setVisibility(View.GONE);
                                            }else if (rideStatus.equals("2")){
                                                btnTmMakePayment.setVisibility(View.VISIBLE);
                                            }else if (rideStatus.equals("6")){
                                                btnTmMakePayment.setVisibility(View.VISIBLE);
                                            }

                                            tvTm_bookingid.setText(tmArraylist.get(i).getBookingId().toString());
                                            tvTm_From.setText(tmArraylist.get(i).getPickupAddress().toString());
                                            tvTm_To.setText(tmArraylist.get(i).getDestinationAddress().toString());
                                            tvTm_Cost.setText("$ "+tmArraylist.get(i).getTripTotal().toString());
                                            tvTm_Date.setText(tmArraylist.get(i).getRideStartDate().toString());
                                            tvTm_Distance.setText(tmArraylist.get(i).getDistance().toString()+" km");
                                            tvTm_Time.setText(tmArraylist.get(i).getRideStartTime().toString() +"");

                                            if (rideStatus.equals("3")){
                                                btnTmMakePayment.setVisibility(View.GONE);
                                                String fullname = tmArraylist.get(i).getFullName().toString();
                                                lyTm_Name.setVisibility(View.VISIBLE);
                                                lyTm_Contact.setVisibility(View.VISIBLE);
                                                tvTm_Name.setText(fullname);
                                                tvTm_Contact.setText(tmArraylist.get(i).getMobileNumber().toString());

                                            }else {
                                                lyTm_Name.setVisibility(View.GONE);
                                                lyTm_Contact.setVisibility(View.GONE);
                                            }
                                        }
                                    }

                                }else{
                                    activity_show_passenger_monthly_trip.setVisibility(View.GONE);
                                    AppUtility.showToast(ShowPassengerMonthlyTripActivity.this,message,0);
                                }

                            } else if(message.equals("Now you have no ride"))
                            {
                                tvMonthlyTripEmpty.setVisibility(View.VISIBLE);
                                activity_show_passenger_monthly_trip.setVisibility(View.GONE);
                                AppUtility.showToast(ShowPassengerMonthlyTripActivity.this,message,0);
                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(ShowPassengerMonthlyTripActivity.this,"Server error occured, please try again !","","Ok");
                            }
                            else if(status.equalsIgnoreCase("0")){
                                activity_show_passenger_monthly_trip.setVisibility(View.GONE);
                                tvMonthlyTripEmpty.setVisibility(View.VISIBLE);
                                AppUtility.showToast(ShowPassengerMonthlyTripActivity.this,message,0);

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
                        Toast.makeText(ShowPassengerMonthlyTripActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                SessionManager sessionManager = new SessionManager(ShowPassengerMonthlyTripActivity.this);

                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ShowPassengerMonthlyTripActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);


                if (confirm != null)
                {
                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject()
                                .toString(4));
                        String  state,create_time,platform,paypal_sdk_version,product_name,short_description,paypal_amount,currency_code;
                        JSONObject jsonObject = confirm.toJSONObject();

                        JSONObject jsonResponse = jsonObject.getJSONObject("response");

                        System.out.println("jsonResponse   =  "+jsonResponse);

                        state = jsonResponse.getString("state");
                        paypal_id = jsonResponse.getString("id");
                        create_time = jsonResponse.getString("create_time");

                        UpdateTmPaymentInfo();

                        JSONObject jsonClient = jsonObject.getJSONObject("client");

                        System.out.println("paypal_id   =  "+paypal_id);
                        System.out.println("jsonClient   =  "+jsonClient);

                        platform = jsonClient.getString("platform");
                        paypal_sdk_version = jsonClient.getString("paypal_sdk_version");
                        product_name = jsonClient.getString("product_name");

                        JSONObject jsonObjectPay = confirm.getPayment().toJSONObject();

                        System.out.println("jsonObjectPay   =  "+jsonObjectPay);

                        short_description = jsonObjectPay.getString("short_description");
                        paypal_amount = jsonObjectPay.getString("amount");
                        currency_code = jsonObjectPay.getString("currency_code");

                        // getPaymentFromServer();

                        // Toast.makeText(getApplicationContext(), "Payment succesfull",Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }else if(resultCode == Activity.RESULT_CANCELED)
            {
                System.out.println("The user canceled.");
            }
            else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
                System.out.println("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }

        }
    }
    private void sendAuthorizationToServer(PayPalAuthorization authorization) {


    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    //****************************************************************//
    public void UpdateTmPaymentInfo() {
        final CustomDialog customDialog = new CustomDialog(ShowPassengerMonthlyTripActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TRIPMONTHLLY_PATMENT_SUBMIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("PassengerPaymentActivity response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                AppUtility.showToast(ShowPassengerMonthlyTripActivity.this,message,1);
                                Intent i = new Intent(ShowPassengerMonthlyTripActivity.this, HomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                                startActivity(i);

                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(ShowPassengerMonthlyTripActivity.this,"server error occured, please try again later!","","Ok");
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(ShowPassengerMonthlyTripActivity.this,message,"","Ok");
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
                        Toast.makeText(ShowPassengerMonthlyTripActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("bookingId", bookingId);
                header.put("transactionId", paypal_id);
                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                SessionManager  sessionManager = new SessionManager(ShowPassengerMonthlyTripActivity.this);
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ShowPassengerMonthlyTripActivity.this);
        requestQueue.add(stringRequest);
    }
}
