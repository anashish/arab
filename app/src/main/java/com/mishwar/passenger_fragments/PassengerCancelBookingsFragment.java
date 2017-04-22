package com.mishwar.passenger_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mishwar.adapter.CanceledTripsAdapter;
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


public class PassengerCancelBookingsFragment extends Fragment {
    private SessionManager sessionManager ;
    public static int createCancelView;
    private View rootView;
    private PullToRefreshListView cancelTripListview;
    private ArrayList<AllTrips> canceledTripArraylist;
    private CanceledTripsAdapter canceledTripsAdapter;
    private TextView tvEmptyList;
    private  boolean isEmpty = false,isViewCreated = false;
    CustomDialog customDialog;

    public PassengerCancelBookingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // createCancelView = 0;

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
        }
        catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        // Inflate the layout for this fragment
        if(createCancelView == 0){
            isViewCreated = true;
            canceledTripArraylist = new ArrayList<AllTrips>();
            canceledTripsAdapter = new CanceledTripsAdapter(getActivity(), canceledTripArraylist);
            cancelTripListview = (PullToRefreshListView) rootView.findViewById(R.id.cancelTripListview);
            tvEmptyList = (TextView) rootView.findViewById(R.id.tvEmptyList);

            if (isEmpty==true){
                cancelTripListview.setVisibility(View.GONE);
                tvEmptyList.setVisibility(View.VISIBLE);
            }
            // PassengerPreviousTripFragment.createPreviousTripView = 0;
            cancelTripListview.setAdapter(canceledTripsAdapter);
            if (AppUtility.isNetworkAvailable(getActivity())) {
                GetDriverCanceledTripInfoTask();
            } else {
                AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
            }

            cancelTripListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
                                                        @Override
                                                        public void onRefresh(PullToRefreshBase refreshView) {
                                                            if (AppUtility.isNetworkAvailable(getActivity())) {
                                                                GetDriverCanceledTripInfoTask();
                                                            } else {
                                                                AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
                                                            }
                                                        }
                                                    }



            );

            createCancelView = 1;

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
        if(!isVisibleToUser) {
            return;
        }
        if (isEmpty==true){
            cancelTripListview.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
        }

        if(AppUtility.isNetworkAvailable(getActivity())) {

            GetDriverCanceledTripInfoTask();

        }else {
            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.network_not_available), Toast.LENGTH_SHORT).show();
        }
    }


    /*-------------------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onResume()
    {
        super.onResume();
    }

/*---------------------------------------------------------------------------------------------------------------------------*/

    /****************************************************************************************************************************************/
    public void GetDriverCanceledTripInfoTask() {
        customDialog = new CustomDialog(getActivity());
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_PASSENGER_GET_TRIP_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        cancelTripListview.onRefreshComplete();
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                canceledTripArraylist.clear();
                                JSONArray jsonArray = jsonObj.getJSONArray("Request");
                                if(jsonArray != null ) {
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        isEmpty = false;
                                        tvEmptyList.setVisibility(View.GONE);
                                        cancelTripListview.setVisibility(View.VISIBLE);
                                        AllTrips item = new AllTrips();
                                        item.setBookingId(jsonObject.getString("bookingId").toString().trim());
                                        item.setRideStatus(jsonObject.getString("rideStatus").toString().trim());
                                        item.setPickupLatLong(jsonObject.getString("pickupLatLong").toString().trim());
                                        item.setDestinationAddress(jsonObject.getString("destinationAddress").toString().trim());
                                        item.setDestinationLatLong(jsonObject.getString("destinationLatLong").toString().trim());
                                        item.setPickupAddress(jsonObject.getString("pickupAddress").toString().trim());
                                        item.setTripTotal(jsonObject.getString("tripTotal").toString().trim());
                                        item.setDistance(jsonObject.getString("distance").toString().trim());
                                        item.setReason(jsonObject.getString("reason").toString().trim());
                                        item.setRideStartDate(jsonObject.getString("rideStartDate").toString().trim());
                                        item.setRideStartTime(jsonObject.getString("rideStartTime").toString().trim());
                                        item.setRideType(jsonObject.getString("rideType").toString().trim());

                                        JSONObject driverJsonObject = jsonObject.getJSONObject("driver");
                                        JSONObject driverDetailsJsonObject = driverJsonObject.getJSONObject("driverDetail");
                                        item.setFullName(driverDetailsJsonObject.getString("fullName").toString().trim());
                                        item.setMobileNumber(driverDetailsJsonObject.getString("mobileNumber").toString().trim());
                                        item.setPaymentStatus(jsonObject.getString("paymentStatus").toString().trim());


                                        canceledTripArraylist.add(item);

                                    }
                                    cancelTripListview.setVisibility(View.VISIBLE);
                                    canceledTripsAdapter.notifyDataSetChanged();
                                }else {
                                    isEmpty = true;
                                    tvEmptyList.setVisibility(View.VISIBLE);
                                    cancelTripListview.setVisibility(View.GONE);
                                    // AppUtility.showAlertDialog_SingleButton(getActivity(),"No result found","","Ok");
                                }
                            } else if(message.equals("No results found right now"))
                            {
                                isEmpty = true;
                                tvEmptyList.setVisibility(View.VISIBLE);
                                //  AppUtility.showAlertDialog_SingleButton(getActivity(),"No future booking found","","Ok");
                            }
                            else if(message.equals("Not Implemented"))
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"Already canceled","","Ok");
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
                        // Toast.makeText(getActivity(), "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String dataStatus = "4";
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
}
