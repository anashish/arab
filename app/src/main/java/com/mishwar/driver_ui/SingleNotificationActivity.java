package com.mishwar.driver_ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.mishwar.R;
import com.mishwar.driver_fregments.DriverCancelBookingsFragment;
import com.mishwar.driver_fregments.DriverCurrentMapFragment;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class SingleNotificationActivity extends AppCompatActivity {

    String destinationAddress="",pickupAddress="",rideStartDate="",rideStartTime="",bookingId="",fullName="",mobileNumber="",tripPrice="",distance="",paymentType;
    private ImageView driver_actionbar_btton_back;
    private  TextView driver_actionbarLayout_title;
    private SessionManager sessionManager;
    private String actionStatus="";
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_notification);

        sessionManager = new SessionManager(SingleNotificationActivity.this);
        driver_actionbarLayout_title = (TextView) findViewById(R.id.driver_actionbarLayout_title);
        driver_actionbar_btton_back = (ImageView) findViewById(R.id.driver_actionbar_btton_back);
        driver_actionbarLayout_title.setText(getString(R.string.text_new_req));
        driver_actionbar_btton_back.setVisibility(View.GONE);
        Intent intent = getIntent();
        bookingId= intent.getStringExtra("bookingId");

     /*   Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String notificationTitle = "";
            notificationTitle = extras.getString("notificationTitle");

        }*/
        Button accept_btn = (Button) findViewById(R.id.accept_btn);
        Button  cancel_btn = (Button) findViewById(R.id.cancel_btn);
        TextView passengerName = (TextView) findViewById(R.id.passengerName);
        //  TextView passMobileNo = (TextView) findViewById(R.id.passMobileNo);
        TextView  passengerPickUpAdddress = (TextView) findViewById(R.id.passengerPickUpAdddress);
        TextView  passengerDestinationAdddress = (TextView) findViewById(R.id.passengerDestinationAdddress);
        TextView  bookedTripTime = (TextView) findViewById(R.id.bookedTripTime);
        TextView  bookedTripDate = (TextView) findViewById(R.id.bookedTripDate);
        TextView  bookedTripPrice = (TextView) findViewById(R.id.bookedTripprice);
        TextView  bookedTripDistance = (TextView) findViewById(R.id.bookedTripDistance);
        TextView  tvPaymenType = (TextView) findViewById(R.id.tvPaymenType);


        if (!bookingId.equals("")){

            destinationAddress= intent.getStringExtra("destinationAddress");
            pickupAddress= intent.getStringExtra("pickupAddress");
            rideStartTime= intent.getStringExtra("rideStartTime");
            rideStartDate= intent.getStringExtra("rideStartDate");
            fullName= intent.getStringExtra("fullName");
            mobileNumber= intent.getStringExtra("mobileNumber");
            tripPrice = intent.getStringExtra("price");
            distance = intent.getStringExtra("distance");
            paymentType = intent.getStringExtra("paymentType");

            String[] time = rideStartTime.split(":");
            String hours = time[0];
            String min = time[1];
            String finalTime = hours+":"+min;

            double value = Double.parseDouble(tripPrice);

            String tripDate = "";
            SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat writeFormat = new SimpleDateFormat("dd-MMM-yyyy");
            java.util.Date date;
            try {
                date = readFormat.parse(rideStartDate);
                rideStartDate = writeFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            bookedTripPrice.setText("$ "+String.format("%.2f", value));
            passengerName.setText(fullName);
            passengerPickUpAdddress.setText(pickupAddress);
            passengerDestinationAdddress.setText(destinationAddress);
            bookedTripTime.setText(finalTime);
            bookedTripDate.setText(rideStartDate);
            bookedTripDistance.setText(distance+" km");
            tvPaymenType.setText(paymentType);
//        passMobileNo.setText("Contact :  "+mobileNumber);

        }

        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionStatus = "1";
                if (AppUtility.isNetworkAvailable(SingleNotificationActivity.this)) {
                    GetAcceptCancelReqTask();
                }else {
                    AppUtility.showAlertDialog_SingleButton(SingleNotificationActivity.this, getString(R.string.network_not_available), "", "Ok");
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionStatus = "2";
                if (AppUtility.isNetworkAvailable(SingleNotificationActivity.this)) {
                    GetAcceptCancelReqTask();
                }else {
                    AppUtility.showAlertDialog_SingleButton(SingleNotificationActivity.this, getString(R.string.network_not_available), "", "Ok");
                }
            }
        });
    }

    /****************************************************************************************************************************************/
    public void GetAcceptCancelReqTask() {

        customDialog = new CustomDialog(SingleNotificationActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_ACCEPT_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                DriverCurrentMapFragment.createView =0;
                                DriverCancelBookingsFragment.createCancelView =0;
                                // dialogReqNotification.dismiss();
                                if (actionStatus.equals("1")){
                                    Intent i = new Intent(SingleNotificationActivity.this, DriverMyBookingsActivity.class);
                                    // i.putExtra("mobileNumber", mobileNumber);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                                    startActivity(i);
                                }else if (message.equalsIgnoreCase("This ride is already transfer to another driver")){
                                    Intent i = new Intent(SingleNotificationActivity.this, DriverHomeActivity.class);
                                    // i.putExtra("mobileNumber", mobileNumber);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                                    startActivity(i);
                                }else {
                                    Intent i = new Intent(SingleNotificationActivity.this, DriverHomeActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                                    startActivity(i);
                                }

                                // startActivity(getIntent());


                            }else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(SingleNotificationActivity.this,"Error occured, please try again !","","Ok");
                            }
                            else {
                                AppUtility.showToast(SingleNotificationActivity.this, message, 1);
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
                        Toast.makeText(SingleNotificationActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("bookingId",bookingId);
                header.put("actionStatus",actionStatus);
                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SingleNotificationActivity.this, DriverHomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
        startActivity(i);
    }
}