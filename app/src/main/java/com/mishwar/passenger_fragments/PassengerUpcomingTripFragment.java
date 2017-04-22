package com.mishwar.passenger_fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.mishwar.adapter.PassengerUpcomingTripAdapter;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.listner.CustomAdapterButtonListener;
import com.mishwar.model.AllTrips;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PassengerUpcomingTripFragment extends Fragment implements CustomAdapterButtonListener {
    private SessionManager sessionManager ;
    public static int createUpcomingTripView;
    private View rootView;
    private PullToRefreshListView upcomingTripListview;
    private ArrayList<AllTrips> upcomingTripArraylist;
    private PassengerUpcomingTripAdapter upcomingTripsAdapter;
    private TextView tvEmptyList;
    private  boolean isEmpty = false,isViewCreated = false;
    CustomDialog customDialog;
    public String pos;
    Dialog  dialogCancelRide;
    private  String BookingId ="",reasonForCancel = "";

    public PassengerUpcomingTripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_cancel_trips, container, false);

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        // Inflate the layout for this fragment
        if(createUpcomingTripView == 0){

            isViewCreated = true;
            upcomingTripArraylist = new ArrayList<AllTrips>();
            upcomingTripsAdapter = new PassengerUpcomingTripAdapter(getActivity(), upcomingTripArraylist);
            upcomingTripListview = (PullToRefreshListView) rootView.findViewById(R.id.cancelTripListview);
            tvEmptyList = (TextView) rootView.findViewById(R.id.tvEmptyList);

            PassengerPreviousTripFragment.createPreviousTripView = 0;
            PassengerCancelBookingsFragment.createCancelView = 0;
            //   CurrentMapFragment.createView = 0;

            //  if (isEmpty==true){
            upcomingTripListview.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
            // }
            upcomingTripListview.setAdapter(upcomingTripsAdapter);
            upcomingTripsAdapter.setCustomListener(PassengerUpcomingTripFragment.this);


            upcomingTripListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pos = String.valueOf(position);
                    //   for (int i = 0; i < driverCurrentReqArraylist.size(); i++){
                    BookingId =  upcomingTripArraylist.get(position).getBookingId().toString();
                    // }

                }
            });

            if (AppUtility.isNetworkAvailable(getActivity()))
            {
                GetPassengerUpcomingTripInfoTask();
            }
            else {
                AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
            }
            createUpcomingTripView = 1;

            upcomingTripListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
                                                          @Override
                                                          public void onRefresh(PullToRefreshBase refreshView) {
                                                              if (AppUtility.isNetworkAvailable(getActivity()))
                                                              {
                                                                  GetPassengerUpcomingTripInfoTask();
                                                              }
                                                              else {
                                                                  AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
                                                              }


                                                          }
                                                      }



            );
        }
        return rootView;
    }
    /*----------------------------------------------------------------------------------------------------------------------------*/
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser==true ){
            if (isEmpty==true){
                upcomingTripListview.setVisibility(View.GONE);
                tvEmptyList.setVisibility(View.VISIBLE);
            }
            // }
            //  onResume();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    /****************************************************************************************************************************************/
    public void GetPassengerUpcomingTripInfoTask() {
        customDialog = new CustomDialog(getActivity());
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_PASSENGER_GET_TRIP_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        upcomingTripListview.onRefreshComplete();
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                upcomingTripArraylist.clear();
                                JSONArray jsonArray = jsonObj.getJSONArray("Request");
                                if(jsonArray != null ) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        upcomingTripListview.setVisibility(View.VISIBLE);
                                        isEmpty = false;
                                        tvEmptyList.setVisibility(View.GONE);
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        AllTrips item = new AllTrips();
                                        item.setBookingId(jsonObject.getString("bookingId").toString().trim());
                                        item.setRideStatus(jsonObject.getString("rideStatus").toString().trim());
                                        item.setPickupLatLong(jsonObject.getString("pickupLatLong").toString().trim());
                                        item.setDestinationAddress(jsonObject.getString("destinationAddress").toString().trim());
                                        item.setDestinationLatLong(jsonObject.getString("destinationLatLong").toString().trim());
                                        item.setPickupAddress(jsonObject.getString("pickupAddress").toString().trim());
                                        item.setTripTotal(jsonObject.getString("tripTotal").toString().trim());
                                        item.setDistance(jsonObject.getString("distance").toString().trim());
                                        item.setRideStartDate(jsonObject.getString("rideStartDate").toString().trim());
                                        item.setRideStartTime(jsonObject.getString("rideStartTime").toString().trim());
                                        item.setRideType(jsonObject.getString("rideType").toString().trim());

                                        JSONObject driverJsonObject = jsonObject.getJSONObject("driver");
                                        JSONObject driverDetailsJsonObject = driverJsonObject.getJSONObject("driverDetail");
                                        item.setFullName(driverDetailsJsonObject.getString("fullName").toString().trim());
                                        item.setMobileNumber(driverDetailsJsonObject.getString("mobileNumber").toString().trim());
                                        item.setPaymentStatus(jsonObject.getString("paymentStatus").toString().trim());

                                        upcomingTripArraylist.add(item);

                                    }
                                    upcomingTripsAdapter.notifyDataSetChanged();
                                }else {
                                    isEmpty = true;
                                    upcomingTripListview.setVisibility(View.GONE);
                                    tvEmptyList.setVisibility(View.VISIBLE);
                                    //   AppUtility.showAlertDialog_SingleButton(getActivity(),"No result found","","Ok");
                                }
                            } else if(message.equals("No results found right now"))
                            {
                                isEmpty = true;
                                tvEmptyList.setVisibility(View.VISIBLE);
                                // AppUtility.showAlertDialog_SingleButton(getActivity(),"No future booking found","","Ok");
                            }
                            else if(message.equals("Not Implemented"))
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"Error occured, please try again later!","","Ok");
                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"server error occured, please try again later!","","Ok");
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),message,"","Ok");
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
                        Toast.makeText(getActivity(), "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String dataStatus = "3";
                header.put("dataStatus", dataStatus);
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
                    AppUtility.showAlertDialog_SingleButton(getActivity(),"Already logged in on another device","","Ok");
                    //Toast.makeText(HomeActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/


    @Override
    public void onButtonClick(int position, String buttonText) {

        if (buttonText.equals("1")){
            BookingId =  upcomingTripArraylist.get(position).getBookingId().toString();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage(getString(R.string.text_alert_sure_cancel_ride));
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialogCancelRide = new Dialog(getActivity(), android.R.style.Theme_Light);
                            dialogCancelRide.setCanceledOnTouchOutside(false);
                            dialogCancelRide.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogCancelRide.setContentView(R.layout.dialog_cancel_trip);
                            dialogCancelRide.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            Window window = dialogCancelRide.getWindow();
                            window.setGravity(Gravity.CENTER);

                            final EditText etDialogCancelRide = (EditText) dialogCancelRide.findViewById(R.id.etDialogCancelRide);
                            Button btnSendDialogCancelRide = (Button) dialogCancelRide.findViewById(R.id.btnSendDialogCancelRide);
                            Button btnCancelDialogCancelRide = (Button) dialogCancelRide.findViewById(R.id.btnCancelDialogCancelRide);
                            final TextView countvalue = (TextView) dialogCancelRide.findViewById(R.id.count);

                            etDialogCancelRide.addTextChangedListener(new TextWatcher() {
                                String sCountValue;
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    countvalue.setText(""+s.length()+"/100");
                                    //  sCountValue = countvalue.getText().toString();
                                }
                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });
                            btnSendDialogCancelRide.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reasonForCancel = etDialogCancelRide.getText().toString();

                                    if (reasonForCancel.equals("")){
                                        AppUtility.showAlertDialog_SingleButton(getActivity(), "please enter the reason for cancel booking.", "", "Ok");

                                    }else {
                                        if (AppUtility.isNetworkAvailable(getActivity()))
                                        {
                                            GetPassengerCancelRideTask();
                                        }
                                        else {
                                            AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
                                        }
                                    }

                                }
                            });
                            btnCancelDialogCancelRide.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogCancelRide.dismiss();
                                }
                            });

                            dialogCancelRide.show();
                        }
                    });
            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }
    }

    /****************************************************************************************************************************************/

    public void GetPassengerCancelRideTask() {

        customDialog = new CustomDialog(getActivity());
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_CANCEL_TRIP_FROM_BOTH,
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
                                AppUtility.showToast(getActivity(), message, 1);
                                dialogCancelRide.cancel();

                                //upcomingTripsAdapter.notifyDataSetChanged();
                                GetPassengerUpcomingTripInfoTask();

                            }else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"server error occured, please try again !","","Ok");
                            }
                            else if(status.equalsIgnoreCase("0"))
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"Something went wrong, please try again!","","Ok");
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),message,"","Ok");

                            }


                            if (AppUtility.isNetworkAvailable(getActivity()))
                            {
                                GetPassengerUpcomingTripInfoTask();
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
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
                        Toast.makeText(getActivity(), "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("bookingId", BookingId);
                header.put("reason", reasonForCancel);

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
                    AppUtility.showAlertDialog_SingleButton(getActivity(),"Already logged in on another device","","Ok");
                    //Toast.makeText(HomeActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/
}
