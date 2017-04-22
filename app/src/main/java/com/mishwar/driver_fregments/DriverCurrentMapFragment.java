package com.mishwar.driver_fregments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mishwar.R;
import com.mishwar.driver_ui.DriverMyBookingsActivity;
import com.mishwar.driver_ui.SendPaymentDetailsToPassengerActivity;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.model.paymentDetails;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.DataParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin3 on 22/08/2016.
 */
public class DriverCurrentMapFragment extends Fragment implements View.OnClickListener, GoogleMap.OnCameraChangeListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,LocationListener {

    public static final String TAG = "Current";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1234;
    private GoogleMap pMap;
    private LatLng currentLatLng,pickupLatLng,destiLatLng;
    private GoogleApiClient GoogleApiClient;
    public static int createView;
    View rootView;
    private boolean gps_enabled = false,network_enabled = false, platlng = true,isStartTripButtonClicked = false,isOnTheWayButtonClicked = false,
            isCancelTripButtonClicked=false,isFregVisible = false;
    private String bookingId = "",reasonForCancel="",rideType,pickupAddress = "",destinationAddress = "",sPickupLatLong = "",sDestinationLatLong,rideStartDate
            ,rideStartTime,rideStatusValue="",fullName = "",TotalPayment,mobileNumber = "",
            dist,actionStatus = "";
    private  double dPickupLat,dPickupLong,dDstinationLat,dDstinationLong,tripCost;
    private SessionManager sessionManager ;
    private Marker passengerMarker,driverMarker,movingPassengerMarker;
    TextView tvRidestatus;
    private LinearLayout layout_footer1,layout_footer2;
    private  Button btnDriverStarttrip,btnDriverCanceltrip,btnDriverEndtrip,btnTripInfo,btnDriverOntheWay;
    private CustomDialog customDialog;
    private  Dialog dialogCancelRide;
    private Timer timer;
    final Handler handler = new Handler();
    private  TimerTask doAsynchronousTask;
    private   RequestQueue requestQueue;
    static  paymentDetails paymentDetails ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        paymentDetails = new paymentDetails();
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            //view =
            rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_driver_current_map, container, false);

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        // Inflate the layout for this fragment

        if(createView == 0){

            if (AppUtility.isNetworkAvailable(getActivity()))
            {
                GetDriverCurrentTripInfoTask();
            }
            else {
                AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
            }
            DriverCancelBookingsFragment.createCancelView =0;
            DriverPriviousTripFragment.createPriviousTripView =0;
            DriverUpcomingTripFragment.createUpcomingTripView =0;

            btnDriverStarttrip = (Button) rootView.findViewById(R.id.btnDriverStarttrip);
            btnDriverCanceltrip = (Button) rootView.findViewById(R.id.btnDriverCanceltrip);
            btnDriverEndtrip = (Button) rootView.findViewById(R.id.btnDriverEndtrip);
            btnDriverOntheWay = (Button) rootView.findViewById(R.id.btnDriverOntheWay);
            btnTripInfo = (Button) rootView.findViewById(R.id.btnTripInfo);

            layout_footer2 = (LinearLayout) rootView.findViewById(R.id.layout_footer2);
            layout_footer1 = (LinearLayout) rootView.findViewById(R.id.layout_footer1);

            btnTripInfo.setOnClickListener(this);
            btnDriverEndtrip.setOnClickListener(this);
            btnDriverOntheWay.setOnClickListener(this);
            btnDriverStarttrip.setOnClickListener(this);
            btnDriverCanceltrip.setOnClickListener(this);

            // ---------------find dialog's components ids-----------------//

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.BookingMap);
            mapFragment.getMapAsync(this);

            createView = 1;

        }

        return rootView;
    }
    /*----------------------------------------------------------------------------------------------------------------------------*/
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            String reason = extras.getString("reason");
            if (reason!=null || !reason.equals("")){
                String msg = getString(R.string.text_canceled_reason)+ ":"+reason;
                AppUtility.showAlertDialog_SingleButton(getActivity(), msg, "", getString(R.string.text_btn_ok));
            }
        }

        // Intent intent = getActivity().getIntent();
        //mobileNumber= intent.getStringExtra("mobileNumber");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == true) {
            isFregVisible = true;

        }else {
            isFregVisible = false;
        }

    }

    /*-------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            /*Action perform for tripmothly*/
            case R.id.btnTripInfo:
                if (!bookingId.equals("") && !pickupAddress.equals(""))
                {
                    tripInfoDialog();

                }else {
                    Toast.makeText(getActivity(), getString(R.string.text_currently_no_ride), Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.btnDriverEndtrip:
                actionStatus = "4";
                isStartTripButtonClicked = false;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage(getString(R.string.text_alert_sure));
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        getString(R.string.text_btn_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (AppUtility.isNetworkAvailable(getActivity()))
                                {
                                    if (AppUtility.isNetworkAvailable(getActivity()))
                                    {
                                        GetStartAndEndRideTask();
                                    }
                                    else {
                                        AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
                                    }
                                }
                                else {
                                    AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
                                }
                            }
                        });

                builder1.setNegativeButton(
                        getString(R.string.text_btn_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                break;

            case R.id.btnDriverOntheWay:
                if (!bookingId.equals("") && !pickupAddress.equals(""))
                {
                    actionStatus = "6";
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                    builder2.setMessage(getString(R.string.text_alert_sure_ontheway));
                    builder2.setCancelable(true);
                    builder2.setPositiveButton(
                            getString(R.string.text_btn_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    isOnTheWayButtonClicked = true;

                                    if (AppUtility.isNetworkAvailable(getActivity()))
                                    {
                                        GetStartAndEndRideTask();

                                    }
                                    else {
                                        AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
                                    }
                                }
                            });

                    builder2.setNegativeButton(
                            getString(R.string.text_btn_no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert12 = builder2.create();
                    alert12.show();
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.text_currently_no_ride), Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.btnDriverStarttrip:
                if (!bookingId.equals("") && !pickupAddress.equals(""))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.text_alert_sure_start_ride));
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            getString(R.string.text_btn_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    actionStatus = "3";
                                    isStartTripButtonClicked = true;

                                    if (AppUtility.isNetworkAvailable(getActivity()))
                                    {
                                        GetStartAndEndRideTask();
                                        stoptimertask();
                                    }
                                    else {
                                        AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
                                    }
                                    String[] pickup = sPickupLatLong.split(",");
                                    String pickupLat = pickup[0];
                                    String pickupLng = pickup[1];
                                    String[] destination = sDestinationLatLong.split(",");
                                    String destinationLat = destination[0];
                                    String destinationLng = destination[1];
                                    dPickupLat = Double.parseDouble(pickupLat);
                                    dPickupLong = Double.parseDouble(pickupLng);
                                    dDstinationLat = Double.parseDouble(destinationLat);
                                    dDstinationLong = Double.parseDouble(destinationLng);

                                    pickupLatLng = new LatLng(dPickupLat,dPickupLong);
                                    destiLatLng = new LatLng(dDstinationLat,dDstinationLong);
                                    System.out.println("pickupLat -:"+pickupLat+"\n"+"pickupLng -:"+pickupLng);
                                }
                            });

                    builder.setNegativeButton(
                            getString(R.string.text_btn_no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert13 = builder.create();
                    alert13.show();

                }else {
                    Toast.makeText(getActivity(), "You Have no ride to start !", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.btnDriverCanceltrip:
                isStartTripButtonClicked = false;
                if (!bookingId.equals("")) {
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
                    builder3.setMessage(getString(R.string.text_alert_sure_cancel_ride));
                    builder3.setCancelable(true);
                    builder3.setPositiveButton(
                            getString(R.string.text_btn_yes),
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
                                    final TextView countvalue = (TextView) dialogCancelRide.findViewById(R.id.count);
                                    Button btnCancelDialogCancelRide = (Button) dialogCancelRide.findViewById(R.id.btnCancelDialogCancelRide);

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
                                                AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.text_enter_reason_for_cancel), "", getString(R.string.text_btn_ok));

                                            }else {
                                                isCancelTripButtonClicked = true;
                                                isOnTheWayButtonClicked = false;
                                                isStartTripButtonClicked = false;
                                                if (AppUtility.isNetworkAvailable(getActivity())) {
                                                    GetCancelRideTask();

                                                } else {
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

                    builder3.setNegativeButton(
                            getString(R.string.text_btn_no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert13 = builder3.create();
                    alert13.show();
                }else {
                    Toast.makeText(getActivity(), getString(R.string.text_no_ride_for_cancel), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

/*-------------------------------------------------------------------------------------------------------------------*/

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constant.MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constant.MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    /*-------------------------------------------------------------------------------------------------------------------*/

    synchronized public void createGoogleApiClient() {
        if (GoogleApiClient == null) {
            GoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)

                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();

            GoogleApiClient.connect();

        }
    }
    /*-------------------------------------------------------------------------------------------------------------------*/
//handel run time permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (GoogleApiClient == null) {
                            createGoogleApiClient();
                        }
                        pMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CALL_PHONE :{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    executeCall();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "inside onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "inside onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        // call server api om location change to update location change on server
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        LatLng current;
        LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}


        if (AppUtility.isNetworkAvailable(getActivity()))
        {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location mylocation = LocationServices.FusedLocationApi.getLastLocation(GoogleApiClient);
                if(gps_enabled && network_enabled) {
                    current = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
                    setCurrent(current, "current", "your current location");
                    currentLatLng = current;
                }else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage(getString(R.string.text_alert_gps_not_on));
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            getString(R.string.text_btn_open_setting),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    getActivity().startActivity(myIntent);
                                }
                            });

                    builder1.setNegativeButton(
                            getString(R.string.text_btn_no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        currentLatLng = cameraPosition.target;
        //setAddress(pickup, tv_pickup);
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        pMap = googleMap;

        final UiSettings uiSettings = pMap.getUiSettings();
        // uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);
        if (platlng) {
            //  pickupLatLng = pMap.getCameraPosition().target;
        }else {
            currentLatLng = pMap.getCameraPosition().target;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                createGoogleApiClient();
                pMap.setMyLocationEnabled(true);
            }
        }
        else {
            createGoogleApiClient();
            pMap.setMyLocationEnabled(true);
        }
        pMap.setOnCameraChangeListener(this);

    }
// methods for set marksers
    /*-------------------------------------------------------------------------------------------------------------------*/

    public  void setCurrent(LatLng picmarker,String locType,String infoPickAddress){
        //  if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))

        System.out.println("setPassengerMarker pickup latlng :" + sPickupLatLong);
        BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_current_loc);
        pMap.addMarker(new MarkerOptions().position(picmarker).title("Your Current location")).setIcon(pickupIcon);

        pMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picmarker, 12));

    }

    /*-------------------------------------------------------------------------------------------------------------------*/
    public  void setPassengerMarker(LatLng picmarker,String infoPickAddress){
        //  if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))

        System.out.println("setPassengerMarker pickup latlng :" + sPickupLatLong);
        BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.passenger_marker);
        passengerMarker = pMap.addMarker(new MarkerOptions().position(picmarker).title("pickup : "+infoPickAddress));
        passengerMarker.setIcon(pickupIcon);
    }

    /*-------------------------------------------------------------------------------------------------------------------*/

    public  void setDestination(LatLng marker,String infoAddress){
        if (AppUtility.isNetworkAvailable(getActivity()))
        {
            BitmapDescriptor destiIcon = BitmapDescriptorFactory.fromResource(R.drawable.driver_marker);
            driverMarker = pMap.addMarker(new MarkerOptions().position(marker).title(infoAddress));
            driverMarker.setIcon(destiIcon);
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
        }
    }
    /*-------------------------------------------------------------------------------------------------------------------*/

    public  void setPassengerChangedLocation(LatLng marker,String infoPickAddress){
        //  if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))
        BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.passenger_tracking_marker);
        movingPassengerMarker = pMap.addMarker(new MarkerOptions().position(marker).title(" "+infoPickAddress));
        movingPassengerMarker.setIcon(pickupIcon);
        //  //move map camera
        pMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
        // pMap.animateCamera(CameraUpdateFactory.zoomTo(17));

    }
    /*---------------------------------------------------------------------------------------------------------------------------------*/

    private void tripInfoDialog() {
        final Dialog dialogCurrentRunningRide = new Dialog(getActivity(), android.R.style.Theme_Light);
        dialogCurrentRunningRide.setCanceledOnTouchOutside(false);
        dialogCurrentRunningRide.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCurrentRunningRide.setContentView(R.layout.dailog_driver_current_ride_info);
        dialogCurrentRunningRide.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialogCurrentRunningRide.getWindow();
        window.setGravity(Gravity.CENTER);

        //   btnDailogDriverTripStart = (Button) dialogCurrentRunningRide.findViewById(R.id.btnDailogDriverTripStart);
        // btnDailogDriverTripEnd = (Button) dialogCurrentRunningRide.findViewById(R.id.btnDailogDriverTripEnd);
        Button btnDailogDriveCancel = (Button) dialogCurrentRunningRide.findViewById(R.id.btnDailogDriveCancelTrip);
        Button btnDailogDriverCall = (Button) dialogCurrentRunningRide.findViewById(R.id.btnDailogDriverCall);

        TextView tvCurrentPassengerName = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvCurrentPassengerName);
        TextView tvCurrentPassBookingId = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvCurrentPassBookingId);
        TextView tvCurrentPassToAddress = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvCurrentPassToAddress);
        TextView tvCurrentPassFromAddress = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvCurrentPassFromAddress);
        TextView tvCurrentPassDetails = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvCurrentPassDetails);
        tvRidestatus = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvRidestatus);
        TextView tvPassDistance = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvPassDistance);
        TextView tvPassCost = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvPassCost);
        TextView tvRideType = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvRideType);


        // String[] time = StartTime.split(":");
        String[] time = rideStartTime.split(":");
        String hours = time[0];
        String min = time[1];
        String finalTime = hours+":"+min;

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

        String Details = "Trip time - "+finalTime+", "+" Date - "+rideStartDate;
        tvCurrentPassengerName.setText(fullName);
        tvCurrentPassBookingId.setText(bookingId);
        tvCurrentPassToAddress.setText(destinationAddress);
        tvCurrentPassFromAddress.setText(pickupAddress);

        if (rideType.equals("TN")){
            tvRideType.setText("Trip now");
        }else  if (rideType.equals("TL")){
            tvRideType.setText("Trip later");
        }

        tvCurrentPassDetails.setText(Details);
        tvPassDistance.setText(dist+" km");
        tvPassCost.setText("$ "+String.format("%.2f", tripCost));

        if(rideStatusValue.equals("2")){
            tvRidestatus.setText("Not start");
        }
        if(rideStatusValue.equals("3")){
            tvRidestatus.setText("Trip start");
        }
        if(rideStatusValue.equals("6")){
            tvRidestatus.setText("Driver on the way");
        }

        btnDailogDriveCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogCurrentRunningRide.dismiss();

            }
        });

        btnDailogDriverCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mobileNumber.equals("")){

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    } else {
                        executeCall();
                    }

                    dialogCurrentRunningRide.dismiss();
                }
            }
        });

        dialogCurrentRunningRide.show();
    }


    /*----------------------------------------------------------------------------------------------------------------*/

    private void executeCall() {
        // start your call here
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mobileNumber.equals("")){
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+"+mobileNumber));
                    startActivity(callIntent);
                }
            }
        });
    }


    /****************************************************************************************************************************************/

    public void GetDriverCurrentTripInfoTask() {
        customDialog = new CustomDialog(getActivity());
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_GET_DRIVER_RIDES_INFO+"?rideType=1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("response current info" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {

                                JSONObject myRidesJsonObj = jsonObj.getJSONObject("myRides");
                                fullName = myRidesJsonObj.getString("fullName").toString().trim();
                                bookingId = myRidesJsonObj.getString("bookingId").toString().trim();
                                rideType = myRidesJsonObj.getString("rideType").toString().trim();
                                pickupAddress = myRidesJsonObj.getString("pickupAddress").toString().trim();
                                destinationAddress = myRidesJsonObj.getString("destinationAddress").toString().trim();
                                sPickupLatLong = myRidesJsonObj.getString("pickupLatLong").toString().trim();
                                sDestinationLatLong = myRidesJsonObj.getString("destinationLatLong").toString().trim();
                                // createdAt = myRidesJsonObj.getString("createdAt").toString().trim();
                                rideStartDate = myRidesJsonObj.getString("rideStartDate").toString().trim();
                                rideStartTime = myRidesJsonObj.getString("rideStartTime").toString().trim();
                                dist = myRidesJsonObj.getString("distance").toString().trim();
                                mobileNumber = myRidesJsonObj.getString("mobileNumber").toString().trim();
                                TotalPayment = myRidesJsonObj.getString("tripTotal").toString().trim();
                                tripCost= Double.parseDouble(myRidesJsonObj.getString("tripTotal"));
                                rideStatusValue= myRidesJsonObj.getString("rideStatus");


                                // requestDialog();
                                if(rideStatusValue.equals("6")) {

                                    btnTripInfo.setVisibility(View.VISIBLE);
                                    btnDriverStarttrip.setVisibility(View.VISIBLE);
                                    btnDriverCanceltrip.setVisibility(View.VISIBLE);
                                    layout_footer1.setVisibility(View.VISIBLE);
                                    btnDriverEndtrip.setVisibility(View.GONE);
                                    btnDriverOntheWay.setVisibility(View.GONE);
                                    //  layout_footer3.setVisibility(View.GONE);
                                    layout_footer2.setVisibility(View.GONE);


                                    if (isFregVisible == true)
                                    {
                                        startTimerToCallGetLocation();
                                    }
                                }
                                else if(rideStatusValue.equals("2")) {
                                    btnTripInfo.setVisibility(View.VISIBLE);
                                    btnDriverOntheWay.setVisibility(View.VISIBLE);
                                    btnDriverStarttrip.setVisibility(View.GONE);
                                    btnDriverCanceltrip.setVisibility(View.VISIBLE);
                                    btnDriverEndtrip.setVisibility(View.GONE);
                                    layout_footer1.setVisibility(View.VISIBLE);
                                    layout_footer2.setVisibility(View.GONE);
                                }
                                else  if(rideStatusValue.equals("3")){
                                    btnDriverOntheWay.setVisibility(View.GONE);
                                    layout_footer2.setVisibility(View.VISIBLE);
                                    btnDriverStarttrip.setVisibility(View.GONE);
                                    btnDriverCanceltrip.setVisibility(View.GONE);
                                    btnDriverEndtrip.setVisibility(View.VISIBLE);

                                }
                                else  if(rideStatusValue.equals("4")){
                                    btnTripInfo.setVisibility(View.GONE);
                                    btnDriverOntheWay.setVisibility(View.GONE);
                                    btnDriverStarttrip.setVisibility(View.GONE);
                                    btnDriverCanceltrip.setVisibility(View.GONE);
                                    btnDriverEndtrip.setVisibility(View.GONE);
                                    layout_footer1.setVisibility(View.GONE);
                                    layout_footer2.setVisibility(View.GONE);

                                }

                     /*draw a path between two points*/
                                String[] pickup = sPickupLatLong.split(",");
                                String pickupLat = pickup[0];
                                String pickupLng = pickup[1];
                                String[] destination = sDestinationLatLong.split(",");
                                String destinationLat = destination[0];
                                String destinationLng = destination[1];
                                dPickupLat = Double.parseDouble(pickupLat);
                                dPickupLong = Double.parseDouble(pickupLng);
                                dDstinationLat = Double.parseDouble(destinationLat);
                                dDstinationLong = Double.parseDouble(destinationLng);

                                pickupLatLng = new LatLng(dPickupLat,dPickupLong);
                                destiLatLng = new LatLng(dDstinationLat,dDstinationLong);
                                System.out.println("pickupLat -:"+pickupLat+"\n"+"pickupLng -:"+pickupLng);

                                if (pickupLatLng!=null && destiLatLng!=null){
                                    setPassengerMarker(pickupLatLng, pickupAddress);
                                    setDestination(destiLatLng, destinationAddress);

                                    // Getting URL to the Google Directions API
                                    String url = getDirectionsUrl(pickupLatLng, destiLatLng);
                                    Log.d("onMapClick", url.toString());
                                    DriverCurrentMapFragment.FetchUrl FetchUrl = new DriverCurrentMapFragment.FetchUrl();

                                    // Start downloading json data from Google Directions API
                                    FetchUrl.execute(url);
                                    //move map camera
                                    pMap.moveCamera(CameraUpdateFactory.newLatLng(pickupLatLng));
                                }

                            }else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"server error occured, please try again later!","","Ok");
                            }
                            else if(status.equalsIgnoreCase("0")){
                                Toast.makeText(getActivity(), getString(R.string.text_currently_no_ride), Toast.LENGTH_LONG).show();

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
                        Toast.makeText(getActivity(), getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
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
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }else {
                    sessionManager.logout();
                }
                return header;
            }
        };

        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/
    public void GetStartAndEndRideTask() {
        customDialog = new CustomDialog(getActivity());
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_START_AND_END_RIDE,
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

                                JSONObject carDetail = jsonObj.getJSONObject("carDetail");

                                String carName = carDetail.getString("carName");
                                String carSheat = carDetail.getString("carSheet");
                                String Counterprice = carDetail.getString("Counterprice");
                                String priceByDistence = carDetail.getString("priceByDistence");
                                String priceByTime = carDetail.getString("priceByTime");
                                String bookingId = carDetail.getString("bookingId");
                                String rideEndDate = carDetail.getString("rideEndDate");
                                String rideEndTime = carDetail.getString("rideEndTime");
                                String rideStartTime = carDetail.getString("rideStartTime");


                                if (isStartTripButtonClicked==true || message.equalsIgnoreCase("Your trip has started by")){
                                    AppUtility.showToast(getActivity(), getString(R.string.text_ride_has_started), 1);
                                    stoptimertask();
                                    btnDriverStarttrip.setVisibility(View.GONE);
                                    btnDriverCanceltrip.setVisibility(View.GONE);
                                    btnDriverEndtrip.setVisibility(View.VISIBLE);
                                    btnDriverOntheWay.setVisibility(View.GONE);
                                    layout_footer1.setVisibility(View.GONE);
                                    //layout_footer3.setVisibility(View.GONE);
                                    layout_footer2.setVisibility(View.VISIBLE);
                                    rideStatusValue = "3";
                                    tvRidestatus.setText("Trip start");

                                }else if(actionStatus.equals("4") || message.equalsIgnoreCase("Your ride has been ended")){
                                    //    getVisibleFragment();
                                    AppUtility.showToast(getActivity(), getString(R.string.text_ride_has_ended), 1);
                                    btnDriverStarttrip.setVisibility(View.GONE);
                                    btnDriverCanceltrip.setVisibility(View.GONE);
                                    btnDriverOntheWay.setVisibility(View.GONE);
                                    layout_footer1.setVisibility(View.GONE);
                                    layout_footer2.setVisibility(View.GONE);
                                    stoptimertask();
                                    Intent intentend = new Intent(getActivity(), SendPaymentDetailsToPassengerActivity.class);
                                    intentend.putExtra("carName", carName);
                                    intentend.putExtra("Counterprice", Counterprice);
                                    intentend.putExtra("priceByDistence", priceByDistence);
                                    intentend.putExtra("priceByTime", priceByTime);
                                    intentend.putExtra("fullName", fullName);
                                    intentend.putExtra("bookingId", bookingId);
                                    intentend.putExtra("pickupAddress", pickupAddress);
                                    intentend.putExtra("destinationAddress", destinationAddress);
                                    intentend.putExtra("distance", dist);
                                    intentend.putExtra("tripCost", TotalPayment);
                                    intentend.putExtra("rideStartDate", rideStartDate);
                                    intentend.putExtra("rideEndDate", rideEndDate);
                                    intentend.putExtra("rideEndTime", rideEndTime);
                                    intentend.putExtra("rideStartTime", rideStartTime);

                                    DriverCurrentMapFragment.createView = 0;
                                    DriverUpcomingTripFragment.createUpcomingTripView = 0;
                                    DriverPriviousTripFragment.createPriviousTripView = 0;
                                    DriverCancelBookingsFragment.createCancelView = 0;
                                    startActivity(intentend);
                                    getActivity().finish();
                                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.fade_in);


                                }else if (isCancelTripButtonClicked==true){
                                    AppUtility.showToast(getActivity(), getString(R.string.text_ride_has_cancel), 1);

                                    btnDriverStarttrip.setVisibility(View.GONE);
                                    btnDriverCanceltrip.setVisibility(View.GONE);
                                    btnDriverOntheWay.setVisibility(View.GONE);
                                    layout_footer1.setVisibility(View.GONE);
                                    layout_footer2.setVisibility(View.GONE);
                                    //  layout_footer3.setVisibility(View.GONE);
                                    stoptimertask();

                                }else if (isOnTheWayButtonClicked==true){
                                    AppUtility.showToast(getActivity(), getString(R.string.text_u_r_ontheway), 1);
                                    btnDriverStarttrip.setVisibility(View.VISIBLE);
                                    btnDriverCanceltrip.setVisibility(View.VISIBLE);
                                    layout_footer1.setVisibility(View.VISIBLE);
                                    //    layout_footer3.setVisibility(View.GONE);
                                    layout_footer2.setVisibility(View.GONE);
                                    btnDriverOntheWay.setVisibility(View.GONE);
                                    rideStatusValue = "6";
                                    tvRidestatus.setText("Driver on the way");

                                    // put here timing for run this API
                                    if (isCancelTripButtonClicked==false || !actionStatus.equals("4")){
                                        if (AppUtility.isNetworkAvailable(getActivity()))
                                        {
                                            startTimerToCallGetLocation();

                                        }else {
                                            AppUtility.showAlertDialog_SingleButton(getActivity(),"Please on net connection to see passenger location","","Ok");
                                        }
                                    }

                                }

                                // requestDialog();

                            }else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"server error occured, please try again !","","Ok");
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
                        Toast.makeText(getActivity(), getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("bookingId", bookingId);
                header.put("status", actionStatus);
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

        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/

    public void GetCancelRideTask() {
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
                                dialogCancelRide.dismiss();
                                stoptimertask();

                                if (AppUtility.isNetworkAvailable(getActivity()))
                                {
                                    GetDriverCurrentTripInfoTask();
                                }
                                else {
                                    AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
                                }
                                //    btnDriverStarttrip.setEnabled(false);
                                btnDriverStarttrip.setVisibility(View.GONE);
                                btnDriverCanceltrip.setVisibility(View.GONE);
                                btnDriverOntheWay.setVisibility(View.GONE);
                                layout_footer1.setVisibility(View.GONE);
                                layout_footer2.setVisibility(View.GONE);
                                // layout_footer3.setVisibility(View.GONE);
                                AppUtility.showToast(getActivity(), message, 1);
                                //getVisibleFragment();

                                Intent intentend = new Intent(getActivity(), DriverMyBookingsActivity.class);
                                DriverCurrentMapFragment.createView = 0;
                                DriverUpcomingTripFragment.createUpcomingTripView = 0;
                                DriverPriviousTripFragment.createPriviousTripView = 0;
                                DriverCancelBookingsFragment.createCancelView = 0;
                                startActivity(intentend);
                                getActivity().finish();
                                // Reload current fragment

                            }else if(message.equals("Now you can not cancel this ride"))
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),message,"","Ok");
                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"Server error occured, please try again later!","","Ok");
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
                        Toast.makeText(getActivity(), getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("bookingId", bookingId);
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

        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }




    /****************************************************************************************************************************************/
    public void GetLocationContinouslly() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_GET_LOCATION_BOTH_SIDE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   customDialog.cancel();
                        System.out.println("GetLocationContinouslly response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {

                                pMap.clear();

                                JSONObject currentLocJsonObj = jsonObj.getJSONObject("location");
                                String updateDriverLocLatLng = currentLocJsonObj.getString("driverLatLong");
                                String updatePassengerLocLatLong = currentLocJsonObj.getString("passengerLatLong");
                                 /*draw a path between two points*/
                                String[] pickup = updateDriverLocLatLng.split(",");
                                String driverLat = pickup[0];
                                String driverLng = pickup[1];
                                String[] destination = updatePassengerLocLatLong.split(",");
                                String passengerLat = destination[0];
                                String passengerLng = destination[1];
                                double dDriverLat = Double.parseDouble(driverLat);
                                double dDriverLong = Double.parseDouble(driverLng);
                                double dPassengerLat = Double.parseDouble(passengerLat);
                                double dPassengerLong = Double.parseDouble(passengerLng);

                                LatLng  newDriverLatLng = new LatLng(dDriverLat,dDriverLong);
                                LatLng  newPassengerLatLng = new LatLng(dPassengerLat,dPassengerLong);

                                System.out.println("passenger cur loc -:"+passengerLat+"\n"+" :"+passengerLng);

                                if (pickupLatLng!=null && destiLatLng!=null){
                                    setPassengerMarker(pickupLatLng, pickupAddress);
                                    setDestination(destiLatLng, destinationAddress);

                                    // Getting URL to the Google Directions API
                                    String url = getDirectionsUrl(pickupLatLng, destiLatLng);
                                    Log.d("onMapClick", url.toString());
                                    DriverCurrentMapFragment.FetchUrl FetchUrl = new DriverCurrentMapFragment.FetchUrl();

                                    // Start downloading json data from Google Directions API
                                    FetchUrl.execute(url);
                                    //move map camera
                                }
                                if (newPassengerLatLng!=null){
                                    setPassengerChangedLocation(newPassengerLatLng,  "pasenger location");
                                }

                                //  AppUtility.showToast(getActivity(), message, 1);

                            }
                            else {
                                AppUtility.showToast(getActivity(), message, 1);
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
                        Toast.makeText(getActivity(), getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("bookingId",bookingId);
                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }else {

                    //Toast.makeText(HomeActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };

        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

     /*-------------------------------------------------------------------------------------------------------*/

    public void startTimerToCallGetLocation() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 30000ms
        timer.schedule(doAsynchronousTask, 1000, 10000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
            if (doAsynchronousTask!=null){
                doAsynchronousTask.cancel();
            }
        }
    }


    /*-------------------------------------------------------------------------------------------------------*/

    public void initializeTimerTask() {

        doAsynchronousTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        if (isFregVisible == true){
                            if (getActivity() != null) {
                                // Code goes here.
                                if (AppUtility.isNetworkAvailable(getActivity()))
                                {
                                    GetLocationContinouslly();
                                }else {
                                    AppUtility.showAlertDialog_SingleButton(getActivity(),"Please on net connection to see passenger location","","Ok");
                                }
                            }
                        }

                    }
                });
            }
        };
    }

    /*-------------------------------------------------------------------------------------------------------*/
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //do when hidden
            isFregVisible = true;
            stoptimertask();
        } else {
            isFregVisible = false;
            startTimerToCallGetLocation();
            //do when show
        }
    }
    /****************************************************************************************************************************************/

// Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            DriverCurrentMapFragment.ParserTask parserTask = new DriverCurrentMapFragment.ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                try {
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(getResources().getColor(R.color.colorPrimary));

                    Log.d("onPostExecute","onPostExecute lineoptions decoded");

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                pMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;
    }

/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
    }
}
