package com.mishwar.driver_ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mishwar.R;
import com.mishwar.adapter.TripMonthllyAdapter;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.model.AllTrips;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowMonthllyTripsActivity extends AppCompatActivity {
    private PullToRefreshListView tmListview;
    private ArrayList<AllTrips> tmArraylist;
    private TextView tmTvEmptyList;
    private  boolean isEmpty = false;
    TripMonthllyAdapter monthllyAdapter;
    private ImageView driver_actionbar_btton_back;
    private  TextView driver_actionbarLayout_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_monthlly_trips);

        tmArraylist = new ArrayList<AllTrips>();
        monthllyAdapter = new TripMonthllyAdapter(ShowMonthllyTripsActivity.this,tmArraylist);
        tmListview = (PullToRefreshListView) findViewById(R.id.tmListview);
        tmTvEmptyList = (TextView) findViewById(R.id.tmTvEmptyList);
        driver_actionbar_btton_back = (ImageView) findViewById(R.id.driver_actionbar_btton_back);
        driver_actionbarLayout_title = (TextView) findViewById(R.id.driver_actionbarLayout_title);

        if(AppUtility.isNetworkAvailable(this)) {

            GetMonthllyTripInfoTask();

        }else {
            Toast.makeText(this,getResources().getString(R.string.network_not_available), Toast.LENGTH_SHORT).show();
        }
        tmListview.setAdapter(monthllyAdapter);
        driver_actionbarLayout_title.setText(getString(R.string.text_trip_monthly));
        driver_actionbar_btton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        tmListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
                                            @Override
                                            public void onRefresh(PullToRefreshBase refreshView) {
                                                if(AppUtility.isNetworkAvailable(ShowMonthllyTripsActivity.this)) {

                                                    GetMonthllyTripInfoTask();

                                                }else {
                                                    Toast.makeText(ShowMonthllyTripsActivity.this,getResources().getString(R.string.network_not_available), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }



        );

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
    }

    /****************************************************************************************************************************************/

    public void GetMonthllyTripInfoTask() {
        final CustomDialog  customDialog = new CustomDialog(this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_GET_TRIPMONTHLLY_BOOKING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        tmListview.onRefreshComplete();
                        System.out.println("monthlly trip response" + response);
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
                                        isEmpty = false;
                                        tmListview.setVisibility(View.VISIBLE);
                                        tmTvEmptyList.setVisibility(View.GONE);

                                        AllTrips item = new AllTrips();

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

                                        JSONObject passengerDetail = jsonObject.getJSONObject("passengerDetail");
                                        if (passengerDetail.has("fullName")){
                                            item.setFullName(passengerDetail.getString("fullName").toString().trim());
                                            item.setMobileNumber(passengerDetail.getString("mobileNumber").toString().trim());
                                        }
                                        tmArraylist.add(item);

                                    }
                                    tmListview.setVisibility(View.VISIBLE);
                                    monthllyAdapter.notifyDataSetChanged();
                                }
                                else {
                                    isEmpty = true;
                                    tmTvEmptyList.setVisibility(View.VISIBLE);
                                    tmListview.setVisibility(View.GONE);
                                    //  AppUtility.showAlertDialog_SingleButton(getActivity(),"No result found","","Ok");
                                }
                            } else if(message.equals("No results found right now"))
                            {
                                isEmpty = true;
                                tmListview.setVisibility(View.GONE);
                                tmTvEmptyList.setVisibility(View.VISIBLE);
                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(ShowMonthllyTripsActivity.this,"Server error occured, please try again !","","Ok");
                            }
                            else if(status.equalsIgnoreCase("0")){
                                isEmpty = true;
                                tmListview.setVisibility(View.GONE);
                                tmTvEmptyList.setVisibility(View.VISIBLE);
                                // AppUtility.showAlertDialog_SingleButton(getActivity(),"No ride found !","","Ok");

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
                        Toast.makeText(ShowMonthllyTripsActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                SessionManager sessionManager = new SessionManager(ShowMonthllyTripsActivity.this);

                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ShowMonthllyTripsActivity.this);
        requestQueue.add(stringRequest);
    }
}
