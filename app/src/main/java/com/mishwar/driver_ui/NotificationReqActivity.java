package com.mishwar.driver_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.mishwar.adapter.DriverCurrentNotificationAdapter;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.listner.CustomAdapterButtonListener;
import com.mishwar.model.GetDriverRequests;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationReqActivity extends AppCompatActivity implements View.OnClickListener,CustomAdapterButtonListener{
    //   String destinationAddress="",pickupAddress="",rideStartDate="",rideStartTime="",bookingId="";
    private ImageView driver_actionbar_btton_back;
    private  TextView driver_actionbarLayout_title;
    private SessionManager sessionManager;
    private ListView DriverReqlistview;
    private ArrayList<GetDriverRequests> driverCurrentReqArraylist = new ArrayList<GetDriverRequests>();
    public static String pos;
    private DriverCurrentNotificationAdapter driverCurrentNotificationAdapter;
    private String actionStatus="";
    String BookingId = "";
    CustomDialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_req);
        sessionManager = new SessionManager(NotificationReqActivity.this);
        initializeViews();

    }
    /*------------------------------------------------------------------------------------------------*/
    private void initializeViews() {

        driverCurrentNotificationAdapter = new DriverCurrentNotificationAdapter(NotificationReqActivity.this, driverCurrentReqArraylist);

        driver_actionbarLayout_title = (TextView) findViewById(R.id.driver_actionbarLayout_title);

        DriverReqlistview = (ListView) findViewById(R.id.DriverReqlistview);

        driver_actionbar_btton_back = (ImageView) findViewById(R.id.driver_actionbar_btton_back);

        driver_actionbarLayout_title.setText(getString(R.string.text_pending_req));

        if (AppUtility.isNetworkAvailable(NotificationReqActivity.this))
        {
            GetDriverCurrentRequestsTask();
        }
        else {
            AppUtility.showAlertDialog_SingleButton(NotificationReqActivity.this, getString(R.string.network_not_available), "", "Ok");
        }

        Intent intent = getIntent();
        String from= intent.getStringExtra("HomeNotification");

        //  if (from.equals("1")){
        // DriverReqlistview.setVisibility(View.VISIBLE);
       /* }else {
            requestDialog();
        }*/

        DriverReqlistview.setAdapter(driverCurrentNotificationAdapter);
        driverCurrentNotificationAdapter.setCustomListener(NotificationReqActivity.this);

        DriverReqlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = String.valueOf(position);
                //   for (int i = 0; i < driverCurrentReqArraylist.size(); i++){
                BookingId =  driverCurrentReqArraylist.get(position).getBookingId().toString();
                // }

            }
        });

        driver_actionbar_btton_back.setOnClickListener(this);
    }

    /*-------------------------------------------------------------------------------------------------*/

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            /*Action perform for tripmothly*/
            case R.id.driver_actionbar_btton_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onButtonClick(int position, String buttonText) {
        pos = String.valueOf(position);

        BookingId =  driverCurrentReqArraylist.get(position).getBookingId().toString();

        if (buttonText.equals("1")){
            actionStatus = "1";
            if (AppUtility.isNetworkAvailable(NotificationReqActivity.this)) {
                GetAcceptCancelReqTask();
            }else {
                AppUtility.showAlertDialog_SingleButton(NotificationReqActivity.this, getString(R.string.network_not_available), "", "Ok");
            }
        }else {
            actionStatus = "2";
            if (AppUtility.isNetworkAvailable(NotificationReqActivity.this)) {
                GetAcceptCancelReqTask();
            }else {
                AppUtility.showAlertDialog_SingleButton(NotificationReqActivity.this, getString(R.string.network_not_available), "", "Ok");
            }
        }

    }

    /*-------------------------------------------------------------------------------------------------------------------*/
    /****************************************************************************************************************************************/
    public void GetAcceptCancelReqTask() {

        customDialog = new CustomDialog(NotificationReqActivity.this);
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

                                Intent i = new Intent(NotificationReqActivity.this, DriverHomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(i);
                                // startActivity(getIntent());


                            }else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton_finishActivity(NotificationReqActivity.this,"Error occured, please try again !","","Ok");
                            }
                            else if (message.equalsIgnoreCase("Invalid Auth Token"))
                            {
                                sessionManager.logout();
                                AppUtility.showAlertDialog_SingleButton_finishActivity(NotificationReqActivity.this,"Something went wrong, please try again!","","Ok");

                            }

                            else {
                                AppUtility.showToast(NotificationReqActivity.this, message, 1);
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
                        Toast.makeText(NotificationReqActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("bookingId",BookingId);
                header.put("actionStatus",actionStatus);
                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }else {
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(NotificationReqActivity.this,"Already logged in on another device","","Ok");
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/
    public void GetDriverCurrentRequestsTask() {

        customDialog = new CustomDialog(NotificationReqActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_ALL_REQUESTS,
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

                                driverCurrentReqArraylist.clear();

                                JSONArray jsonArray = jsonObj.getJSONArray("Request");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    GetDriverRequests item = new GetDriverRequests();

                                    item.setId(jsonObject.getString("id").toString().trim());
                                    item.setBookingId(jsonObject.getString("bookingId").toString().trim());
                                    item.setRideStatus(jsonObject.getString("rideStatus").toString().trim());
                                    item.setRideStartDate(jsonObject.getString("rideStartDate").toString().trim());
                                    item.setRideStartTime(jsonObject.getString("rideStartTime").toString().trim());
                                    item.setPickupLatLong(jsonObject.getString("pickupLatLong").toString().trim());
                                    item.setAcceptBy(jsonObject.getString("acceptBy").toString().trim());
                                    item.setDestinationAddress(jsonObject.getString("destinationAddress").toString().trim());
                                    item.setDestinationLatLong(jsonObject.getString("destinationLatLong").toString().trim());
                                    item.setDriverStatus(jsonObject.getString("driverStatus").toString().trim());
                                    item.setDriverId(jsonObject.getString("driverId").toString().trim());
                                    item.setPickupAddress(jsonObject.getString("pickupAddress").toString().trim());
                                    item.setPassengerCount(jsonObject.getString("passengerCount").toString().trim());
                                    item.setRideType(jsonObject.getString("rideType").toString().trim());
                                    item.setVehichleId(jsonObject.getString("vehichleId").toString().trim());
                                    item.setRideEndTime(jsonObject.getString("rideEndTime").toString().trim());
                                    item.setRideEndDate(jsonObject.getString("rideEndDate").toString().trim());
                                    item.setStatus(jsonObject.getString("status").toString().trim());
                                    item.setFullName(jsonObject.getString("fullName").toString().trim());
                                    item.setMobileNumber(jsonObject.getString("mobileNumber").toString().trim());

                                    item.setUserId(jsonObject.getString("userId").toString().trim());
                                    item.setTripTotal(jsonObject.getString("tripTotal").toString().trim());


                                    driverCurrentReqArraylist.add(item);

                                }
                                driverCurrentNotificationAdapter.notifyDataSetChanged();

                            }else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton_finishActivity(NotificationReqActivity.this,"Error occured, please try again !","","Ok");
                            }
                            else if (message.equalsIgnoreCase("Invalid Auth Token"))
                            {
                                sessionManager.logout();
                                AppUtility.showAlertDialog_SingleButton_finishActivity(NotificationReqActivity.this,"Something went wrong, please try again!","","Ok");

                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton_finishActivity(NotificationReqActivity.this,message,"","Ok");
                                //startActivity(new Intent(NotificationReqActivity.this,DriverHomeActivity.class));
                              //  overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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
                        Toast.makeText(NotificationReqActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("driverStatus","0");

                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }else {
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(NotificationReqActivity.this,"Already logged in on another device","","Ok");
                    //Toast.makeText(HomeActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/
    /*--------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), DriverHomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
    }

}
