package com.mishwar.driver_fregments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.mishwar.adapter.AllTripsAdapter;
import com.mishwar.driver_ui.DriverUpcomingMapActivity;
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


public class DriverUpcomingTripFragment extends Fragment {
    private SessionManager sessionManager ;
    public static int createUpcomingTripView;
    private View rootView;
    private PullToRefreshListView upcomingTripListview;
    private ArrayList<AllTrips> upcomingTripArraylist;
    private AllTripsAdapter upcomingTripsAdapter;
    private  TextView tvEmptyList;
    private  boolean isEmpty = false,isViewCreated = false;
    CustomDialog customDialog;

    public DriverUpcomingTripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createUpcomingTripView = 0;

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
            //view =
            rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_cancel_trips, container, false);

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        // Inflate the layout for this fragment
        if(createUpcomingTripView == 0){
            isViewCreated = true;
            upcomingTripArraylist = new ArrayList<AllTrips>();
            upcomingTripsAdapter = new AllTripsAdapter(getActivity(), upcomingTripArraylist);
            upcomingTripListview = (PullToRefreshListView) rootView.findViewById(R.id.cancelTripListview);
            tvEmptyList = (TextView) rootView.findViewById(R.id.tvEmptyList);

            DriverPriviousTripFragment.createPriviousTripView = 0;
            DriverCancelBookingsFragment.createCancelView = 0;

            // if (isEmpty==true){
            upcomingTripListview.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
            //  }

            upcomingTripListview.setAdapter(upcomingTripsAdapter);

            if (AppUtility.isNetworkAvailable(getActivity()))
            {
                GetDriverUpcomingTripInfoTask();
            }
            else {
                AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
            }


            upcomingTripListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
                                                          @Override
                                                          public void onRefresh(PullToRefreshBase refreshView) {
                                                              if (AppUtility.isNetworkAvailable(getActivity()))
                                                              {
                                                                  GetDriverUpcomingTripInfoTask();
                                                              }
                                                              else {
                                                                  AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
                                                              }

                                                          }
                                                      }



            );

            upcomingTripListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    int positionv = position-1;

                    AllTrips item = upcomingTripArraylist.get(positionv);

                    Intent upcomingIntent = new Intent(getActivity(), DriverUpcomingMapActivity.class);
                    upcomingIntent.putExtra("from","UpacomingFreg");
                    upcomingIntent.putExtra("bookingId",item.getBookingId());
                    upcomingIntent.putExtra("rideStatus", item.getRideStatus());
                    upcomingIntent.putExtra("pickupLatLong", item.getPickupLatLong());
                    upcomingIntent.putExtra("destinationLatLong",  item.getDestinationLatLong());
                    upcomingIntent.putExtra("pickupAddress", item.getPickupAddress());
                    upcomingIntent.putExtra("destinationAddress", item.getDestinationAddress());
                    upcomingIntent.putExtra("fullName", item.getFullName());
                    upcomingIntent.putExtra("mobileNumber", item.getMobileNumber());
                    upcomingIntent.putExtra("tripTotal", item.getTripTotal());
                    upcomingIntent.putExtra("distance", item.getDistance());
                    upcomingIntent.putExtra("rideStartDate", item.getRideStartDate());
                    upcomingIntent.putExtra("rideStartTime", item.getRideStartTime());
                    upcomingIntent.putExtra("rideType", item.getRideType());
                    getActivity().startActivity(upcomingIntent);
                    getActivity().overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                    getActivity().finish();

                }
            });

            createUpcomingTripView = 1;

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
        if (isVisibleToUser==true || isResumed() ){

            if (isEmpty==true){
                upcomingTripListview.setVisibility(View.GONE);
                tvEmptyList.setVisibility(View.VISIBLE);
            }
            onResume();
        }
    }
    /****************************************************************************************************************************************/
    @Override
    public void onResume()
    {
        super.onResume();

    }

    /****************************************************************************************************************************************/
    public void GetDriverUpcomingTripInfoTask() {
        customDialog = new CustomDialog(getActivity());
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_GET_DRIVER_RIDES_INFO+"?rideType=3",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        upcomingTripListview.onRefreshComplete();
                        System.out.println("upcoming response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {

                                upcomingTripArraylist.clear();

                                JSONArray jsonArray = jsonObj.getJSONArray("myRides");
                                if(jsonArray != null ) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        upcomingTripListview.setVisibility(View.VISIBLE);
                                        tvEmptyList.setVisibility(View.GONE);
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        isEmpty = false;
                                        AllTrips item = new AllTrips();
                                        item.setBookingId(jsonObject.getString("bookingId").toString().trim());
                                        item.setRideStatus(jsonObject.getString("rideStatus").toString().trim());
                                        item.setPickupLatLong(jsonObject.getString("pickupLatLong").toString().trim());
                                        item.setDestinationAddress(jsonObject.getString("destinationAddress").toString().trim());
                                        item.setDestinationLatLong(jsonObject.getString("destinationLatLong").toString().trim());
                                        item.setPickupAddress(jsonObject.getString("pickupAddress").toString().trim());
                                        item.setFullName(jsonObject.getString("fullName").toString().trim());
                                        item.setMobileNumber(jsonObject.getString("mobileNumber").toString().trim());
                                        item.setTripTotal(jsonObject.getString("tripTotal").toString().trim());
                                        item.setDistance(jsonObject.getString("distance").toString().trim());
                                        item.setDate(jsonObject.getString("createdAt").toString().trim());
                                        item.setRideType(jsonObject.getString("rideType").toString().trim());
                                        item.setRideStartTime(jsonObject.getString("rideStartTime").toString().trim());
                                        item.setRideStartDate(jsonObject.getString("rideStartDate").toString().trim());
                                        item.setPaymentStatus(jsonObject.getString("paymentStatus").toString().trim());

                                        upcomingTripArraylist.add(item);

                                        System.out.println("Vehichle type" + upcomingTripArraylist);

                                    }
                                    upcomingTripsAdapter.notifyDataSetChanged();
                                }
                                else {
                                    isEmpty = true;
                                    tvEmptyList.setVisibility(View.VISIBLE);
                                    upcomingTripListview.setVisibility(View.GONE);
                                    AppUtility.showAlertDialog_SingleButton(getActivity(),"No result found","","Ok");
                                }
                            } else if(message.equals("No results found right now"))
                            {
                                tvEmptyList.setVisibility(View.VISIBLE);
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"No Canceled booking found","","Ok");
                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"Server error occured, please try again !","","Ok");
                            }
                            else if (message.equalsIgnoreCase("Invalid Auth Token"))
                            {
                                sessionManager.logout();
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"Something went wrong, please try again!","","Ok");
                            }
                            else if(status.equalsIgnoreCase("0")){
                                isEmpty = true;
                                upcomingTripListview.setVisibility(View.GONE);
                                tvEmptyList.setVisibility(View.VISIBLE);
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
                        Toast.makeText(getActivity(), "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String rideType = "3";
                header.put("rideType", rideType);
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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/
}
