package com.mishwar.driver_fregments;

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
import com.mishwar.adapter.DriverPriviousTripAdapter;
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


public class DriverPriviousTripFragment extends Fragment {
    public static int createPriviousTripView;
    private View rootView;
    private PullToRefreshListView priviousTripListview;
    private ArrayList<AllTrips> priviousTripArraylist;
    private DriverPriviousTripAdapter priviousTripsAdapter;
    private  TextView tvEmptyList;
    private  boolean isEmpty = false,isViewCreated = false;
    CustomDialog customDialog;

    public DriverPriviousTripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        createPriviousTripView = 0;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
        if(createPriviousTripView == 0){
            isViewCreated = true;

            priviousTripArraylist = new ArrayList<AllTrips>();
            priviousTripsAdapter = new DriverPriviousTripAdapter(getActivity(), priviousTripArraylist);
            priviousTripListview = (PullToRefreshListView) rootView.findViewById(R.id.cancelTripListview);
            tvEmptyList = (TextView) rootView.findViewById(R.id.tvEmptyList);

            DriverUpcomingTripFragment.createUpcomingTripView = 0;
            DriverCancelBookingsFragment.createCancelView = 0;

            //  if (isEmpty==true){
            priviousTripListview.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
            //}

            priviousTripListview.setAdapter(priviousTripsAdapter);

            if(AppUtility.isNetworkAvailable(getActivity())) {

                GetDriverPreviousTripInfoTask();

            }else {
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.network_not_available), Toast.LENGTH_SHORT).show();
            }
            createPriviousTripView = 1;

            priviousTripListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
                                                          @Override
                                                          public void onRefresh(PullToRefreshBase refreshView) {
                                                              if(AppUtility.isNetworkAvailable(getActivity())) {

                                                                  GetDriverPreviousTripInfoTask();

                                                              }else {
                                                                  Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.network_not_available), Toast.LENGTH_SHORT).show();
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
        if(!isVisibleToUser) {
            return;
        }
        if (isEmpty==true){
            priviousTripListview.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
        }

        if(AppUtility.isNetworkAvailable(getActivity())) {

            GetDriverPreviousTripInfoTask();

        }else {
            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.network_not_available), Toast.LENGTH_SHORT).show();
        }
    }
    /****************************************************************************************************************************************/
    @Override
    public void onResume()
    {
        super.onResume();

    }

    /****************************************************************************************************************************************/

    public void GetDriverPreviousTripInfoTask() {
        customDialog = new CustomDialog(getActivity());
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_GET_DRIVER_RIDES_INFO+"?rideType=2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        priviousTripListview.onRefreshComplete();
                        System.out.println("privious trip response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {

                                priviousTripArraylist.clear();

                                JSONArray jsonArray = jsonObj.getJSONArray("myRides");
                                if(jsonArray != null ) {
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        isEmpty = false;
                                        priviousTripListview.setVisibility(View.VISIBLE);
                                        tvEmptyList.setVisibility(View.GONE);

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
                                        item.setRideEndDate(jsonObject.getString("rideEndDate").toString().trim());
                                        item.setRideEndTime(jsonObject.getString("rideEndTime").toString().trim());
                                        item.setPaymentStatus(jsonObject.getString("paymentStatus").toString().trim());

                                        JSONObject vehicheInfo = jsonObject.getJSONObject("vehicheInfo");
                                        item.setCounterprice(vehicheInfo.getString("Counterprice").toString().trim());
                                        item.setPriceByDistence(vehicheInfo.getString("priceByDistence").toString().trim());
                                        item.setPriceByTime(vehicheInfo.getString("priceByTime").toString().trim());

                                        priviousTripArraylist.add(item);

                                    }
                                    priviousTripsAdapter.notifyDataSetChanged();
                                }
                                else {
                                    isEmpty = true;
                                    tvEmptyList.setVisibility(View.VISIBLE);
                                    priviousTripListview.setVisibility(View.GONE);
                                    //  AppUtility.showAlertDialog_SingleButton(getActivity(),"No result found","","Ok");
                                }
                            } else if(message.equals("No results found right now"))
                            {
                                isEmpty = true;
                                priviousTripListview.setVisibility(View.GONE);
                                tvEmptyList.setVisibility(View.VISIBLE);
                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"Server error occured, please try again !","","Ok");
                            }

                            else if(status.equalsIgnoreCase("0")){
                                isEmpty = true;
                                priviousTripListview.setVisibility(View.GONE);
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
                String rideType = "2";
                header.put("rideType", rideType);
                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                SessionManager sessionManager = new SessionManager(getContext());
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
