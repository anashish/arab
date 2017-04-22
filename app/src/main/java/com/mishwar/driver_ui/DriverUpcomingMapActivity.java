package com.mishwar.driver_ui;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
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
import com.mishwar.driver_fregments.DriverCancelBookingsFragment;
import com.mishwar.driver_fregments.DriverCurrentMapFragment;
import com.mishwar.driver_fregments.DriverPriviousTripFragment;
import com.mishwar.driver_fregments.DriverUpcomingTripFragment;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vollyemultipart.VolleySingleton;

public class DriverUpcomingMapActivity extends AppCompatActivity implements View.OnClickListener, GoogleMap.OnCameraChangeListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    public static final String TAG = "Upcoming Booking";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1234;

    private GoogleMap pMap;
    private LatLng currentUpcomingLatLng, upcomingPickupLatLng, upcomingDestiLatLng;
    private com.google.android.gms.common.api.GoogleApiClient GoogleApiClient;
    private ImageView actionbar_btton_back;
    private TextView actionbarLayout_title;
    View rootView;
    private boolean gps_enabled = false, network_enabled = false, platlng = true, isStartTripButtonClicked = false, isOnTheWayButtonClicked = false, isEndTripButtonClicked = false, isCancelTripButtonClicked = false;
    private double dPickupLat, dPickupLong, dDstinationLat, dDstinationLong, tripTotal;
    private SessionManager sessionManager;
    private Marker passengerMarker, driverMarker;
    private PolylineOptions lineOptions = null;
    private ArrayList<LatLng> points = null;
    private LinearLayout layout_footer1, layout_footer2, layout_footer3;
    private Button btnDriverUpcomingStarttrip, btnDriverUpcomingCanceltrip, btnDriverUpcomingEndtrip, btnUpcomingTripInfo, btnDriverUpcomingOntheWay;
    private String from, bookingId = "", pickupAddress, destinationAddress, fullName, tripTime, mobileNumber,
            distance, sPickupLatLong = "", sDestinationLatLong, rideStatusValue = "", rideType = "", rideStartDate = "",rideStartTime="", dist, actionStatus = "", reasonForCancel = "";
    private Dialog dialogCancelUpcomingRide;
    private CustomDialog customDialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_upcoming_map);
        sessionManager = new SessionManager(DriverUpcomingMapActivity.this);
        initializeViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.UpcomingBookingMap);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initializeViews() {
        DriverCurrentMapFragment.createView = 0;
        DriverCancelBookingsFragment.createCancelView = 0;
        DriverPriviousTripFragment.createPriviousTripView = 0;
        DriverUpcomingTripFragment.createUpcomingTripView = 0;

        btnDriverUpcomingStarttrip = (Button) findViewById(R.id.btnDriverUpcomingStarttrip);
        btnDriverUpcomingCanceltrip = (Button) findViewById(R.id.btnDriverUpcomingCanceltrip);
        btnDriverUpcomingEndtrip = (Button) findViewById(R.id.btnDriverUpcomingEndtrip);
        btnDriverUpcomingOntheWay = (Button) findViewById(R.id.btnDriverUpcomingOntheWay);
        btnUpcomingTripInfo = (Button) findViewById(R.id.btnUpcomingTripInfo);
        actionbarLayout_title = (TextView) findViewById(R.id.driver_actionbarLayout_title);
        actionbar_btton_back = (ImageView) findViewById(R.id.driver_actionbar_btton_back);

        layout_footer2 = (LinearLayout) findViewById(R.id.layout_footer2);
        layout_footer1 = (LinearLayout) findViewById(R.id.layout_footer1);
        //layout_footer3 = (LinearLayout) findViewById(R.id.layout_footer3);

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        bookingId = intent.getStringExtra("bookingId");
        rideStatusValue = intent.getStringExtra("rideStatus");
        sPickupLatLong = intent.getStringExtra("pickupLatLong");
        sDestinationLatLong = intent.getStringExtra("destinationLatLong");
        pickupAddress = intent.getStringExtra("pickupAddress");
        destinationAddress = intent.getStringExtra("destinationAddress");
        fullName = intent.getStringExtra("fullName");
        mobileNumber = intent.getStringExtra("mobileNumber");
        tripTotal = Double.parseDouble(intent.getStringExtra("tripTotal"));
        dist = intent.getStringExtra("distance");
        rideStartDate = intent.getStringExtra("rideStartDate");
        rideStartTime = intent.getStringExtra("rideStartTime");
        rideType = intent.getStringExtra("rideType");

      /*  //  tripInfoDialog();
        String[] sCreatedAt = createdAt.split(" ");
        String StartDate = sCreatedAt[0];
        String StartTime = sCreatedAt[1];

        String[] time = StartTime.split(":");
        String hours = time[0];
        String min = time[1];
        String finalTime = hours + ":" + min;

        tripTime = finalTime;

        SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat writeFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date;
        try {
            date = readFormat.parse(StartDate);
            StartDate = writeFormat.format(date);
            createdAt = StartDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        actionbarLayout_title.setText(getString(R.string.text_upcoming_booking));
        btnUpcomingTripInfo.setOnClickListener(this);
        btnDriverUpcomingEndtrip.setOnClickListener(this);
        btnDriverUpcomingOntheWay.setOnClickListener(this);
        btnDriverUpcomingStarttrip.setOnClickListener(this);
        btnDriverUpcomingCanceltrip.setOnClickListener(this);
        actionbar_btton_back.setOnClickListener(this);

    }

    /*-------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            /*Action perform for tripmothly*/
            case R.id.driver_actionbar_btton_back:
                DriverCurrentMapFragment.createView = 0;
                DriverCancelBookingsFragment.createCancelView = 0;
                DriverPriviousTripFragment.createPriviousTripView = 0;
                DriverUpcomingTripFragment.createUpcomingTripView = 0;
                overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                startActivity(new Intent(DriverUpcomingMapActivity.this, DriverMyBookingsActivity.class));
                finish();
                break;

            case R.id.btnUpcomingTripInfo:
                if (!bookingId.equals("") && !pickupAddress.equals("")) {

                    tripInfoDialog();

                } else {
                    Toast.makeText(DriverUpcomingMapActivity.this, "Currently you Have no any request", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.btnDriverUpcomingEndtrip:
                actionStatus = "4";
                isStartTripButtonClicked = false;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DriverUpcomingMapActivity.this);
                builder1.setMessage("Do you want to end this ride ?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (AppUtility.isNetworkAvailable(DriverUpcomingMapActivity.this)) {
                                    isEndTripButtonClicked = true;

                                    if (AppUtility.isNetworkAvailable(DriverUpcomingMapActivity.this)) {
                                        GetStartAndEndRideTask();

                                        btnDriverUpcomingStarttrip.setVisibility(View.GONE);
                                        btnDriverUpcomingCanceltrip.setVisibility(View.GONE);
                                        btnDriverUpcomingOntheWay.setVisibility(View.GONE);
                                        layout_footer1.setVisibility(View.GONE);
                                        layout_footer2.setVisibility(View.GONE);
                                        // layout_footer3.setVisibility(View.GONE);
                                    } else {
                                        AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, getString(R.string.network_not_available), "", "Ok");
                                    }
                                } else {
                                    AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, getString(R.string.network_not_available), "", "Ok");
                                }
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
                break;

            case R.id.btnDriverUpcomingOntheWay :

                String currentDate = "",currentTime;
                Calendar c = Calendar.getInstance();
                Date dt = new Date();
                int hours = dt.getHours();
                int minutes = dt.getMinutes();
                int seconds = dt.getSeconds();
                String curTime = hours + ":" + minutes + ":" + seconds;

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c.getTime());
                // currentDate = formattedDate;
                SimpleDateFormat readFormat = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date;
                try {
                    date = readFormat.parse(formattedDate);
                    formattedDate = writeFormat.format(date);
                    currentDate = formattedDate;
                    currentTime = curTime;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(currentDate.equals(rideStartDate)){
                    if (!bookingId.equals("") && !pickupAddress.equals("")) {
                        actionStatus = "6";
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(DriverUpcomingMapActivity.this);
                        builder2.setMessage("Are you sure, you are on the way ?");
                        builder2.setCancelable(true);
                        builder2.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        isOnTheWayButtonClicked = true;

                                        if (AppUtility.isNetworkAvailable(DriverUpcomingMapActivity.this)) {
                                            GetStartAndEndRideTask();

                                        } else {
                                            AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, getString(R.string.network_not_available), "", "Ok");
                                        }
                                    }
                                });

                        builder2.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert12 = builder2.create();
                        alert12.show();
                    } else {
                        Toast.makeText(DriverUpcomingMapActivity.this, "You Have no ride !", Toast.LENGTH_LONG).show();
                    }
                }else {
                    AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, getString(R.string.text_can_not_start_upcoming_ride),"",getString(R.string.text_btn_ok) );
                }

                break;

            case R.id.btnDriverUpcomingStarttrip:
                if (!bookingId.equals("") && !pickupAddress.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DriverUpcomingMapActivity.this);
                    builder.setMessage("Do you want to start this ride ?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    actionStatus = "3";
                                    isStartTripButtonClicked = true;

                                    if (AppUtility.isNetworkAvailable(DriverUpcomingMapActivity.this)) {
                                        GetStartAndEndRideTask();

                                    } else {
                                        AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, getString(R.string.network_not_available), "", "Ok");
                                    }
                                }
                            });

                    builder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert13 = builder.create();
                    alert13.show();

                } else {
                    Toast.makeText(DriverUpcomingMapActivity.this, "You have no ride to start !", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.btnDriverUpcomingCanceltrip:
                isStartTripButtonClicked = false;
                if (!bookingId.equals("")) {
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(DriverUpcomingMapActivity.this);
                    builder3.setMessage(getString(R.string.text_alert_sure_cancel_ride));
                    builder3.setCancelable(true);
                    builder3.setPositiveButton(
                            getString(R.string.text_btn_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialogCancelUpcomingRide = new Dialog(DriverUpcomingMapActivity.this, android.R.style.Theme_Light);
                                    dialogCancelUpcomingRide.setCanceledOnTouchOutside(false);
                                    dialogCancelUpcomingRide.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialogCancelUpcomingRide.setContentView(R.layout.dialog_cancel_trip);
                                    dialogCancelUpcomingRide.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    Window window = dialogCancelUpcomingRide.getWindow();
                                    window.setGravity(Gravity.CENTER);

                                    final EditText etDialogCancelRide = (EditText) dialogCancelUpcomingRide.findViewById(R.id.etDialogCancelRide);
                                    Button btnSendDialogCancelRide = (Button) dialogCancelUpcomingRide.findViewById(R.id.btnSendDialogCancelRide);
                                    final TextView countvalue = (TextView) dialogCancelUpcomingRide.findViewById(R.id.count);
                                    Button btnCancelDialogCancelRide = (Button) dialogCancelUpcomingRide.findViewById(R.id.btnCancelDialogCancelRide);

                                    etDialogCancelRide.addTextChangedListener(new TextWatcher() {
                                        String sCountValue;

                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                            countvalue.setText("" + s.length() + "/100");
                                            //  sCountValue = countvalue.getText().toString();

                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });

                                    btnSendDialogCancelRide.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            isCancelTripButtonClicked = true;
                                            isOnTheWayButtonClicked = false;
                                            isStartTripButtonClicked = false;
                                            reasonForCancel = etDialogCancelRide.getText().toString();
                                            if (AppUtility.isNetworkAvailable(DriverUpcomingMapActivity.this)) {
                                                GetCancelRideTask();

                                            } else {
                                                AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, getString(R.string.network_not_available), "", "Ok");
                                            }
                                        }
                                    });
                                    btnCancelDialogCancelRide.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogCancelUpcomingRide.dismiss();
                                        }
                                    });

                                    dialogCancelUpcomingRide.show();
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
                } else {
                    Toast.makeText(DriverUpcomingMapActivity.this, "You Have no ride to Cancel !", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /*-------------------------------------------------------------------------------------------------------------------*/
    private void drawMarkerAndPath(){
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

        upcomingPickupLatLng = new LatLng(dPickupLat, dPickupLong);
        upcomingDestiLatLng = new LatLng(dDstinationLat, dDstinationLong);
        System.out.println("pickupLat -:" + pickupLat + "\n" + "pickupLng -:" + pickupLng);

        if (upcomingPickupLatLng != null && upcomingDestiLatLng != null) {

            setPassengerMarker(upcomingPickupLatLng, "pickup", pickupAddress);
            setDestination(upcomingDestiLatLng, "destination", destinationAddress);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(upcomingPickupLatLng, upcomingDestiLatLng);
            Log.d("onMapClick", url.toString());
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            //move map camera
            pMap.moveCamera(CameraUpdateFactory.newLatLng(upcomingPickupLatLng));
            pMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        } else {

        }
    }
    /*-------------------------------------------------------------------------------------------------------------------*/
/*    private void getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        Date dt = new Date();
        int hours = dt.getHours();
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();
        String curTime = hours + ":" + minutes + ":" + seconds;

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        // currentDate = formattedDate;
        SimpleDateFormat readFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat writeFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date;
        try {
            date = readFormat.parse(formattedDate);
            formattedDate = writeFormat.format(date);
            currentDate = formattedDate;
            currentTime = curTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }*/
/*-------------------------------------------------------------------------------------------------------------------*/

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(DriverUpcomingMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DriverUpcomingMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(DriverUpcomingMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(DriverUpcomingMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    /*-------------------------------------------------------------------------------------------------------------------*/

    synchronized public void createGoogleApiClient() {
        if (GoogleApiClient == null) {
            GoogleApiClient = new GoogleApiClient.Builder(DriverUpcomingMapActivity.this)
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(DriverUpcomingMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (GoogleApiClient == null) {
                            createGoogleApiClient();
                        }
                        pMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(DriverUpcomingMapActivity.this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
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
    public void onConnected(@Nullable Bundle bundle) {
        LatLng current;
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }


        if (AppUtility.isNetworkAvailable(DriverUpcomingMapActivity.this)) {
            if (ContextCompat.checkSelfPermission(DriverUpcomingMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location mylocation = LocationServices.FusedLocationApi.getLastLocation(GoogleApiClient);
                if (gps_enabled && network_enabled) {
                    current = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
                    setCurrent(current, "current", "your current location");
                    currentUpcomingLatLng = current;
                } /*else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(DriverUpcomingMapActivity.this);
                    builder1.setMessage("Gps network not available !");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Open Location setting",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    DriverUpcomingMapActivity.this.startActivity(myIntent);
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
                }*/
            }
        } else {
            AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, getString(R.string.network_not_available), "", "Ok");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        currentUpcomingLatLng = cameraPosition.target;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        pMap = googleMap;

        final UiSettings uiSettings = pMap.getUiSettings();
        // uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);
        if (platlng) {
            //  pickupLatLng = pMap.getCameraPosition().target;
        } else {
            currentUpcomingLatLng = pMap.getCameraPosition().target;
        }

        drawMarkerAndPath();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(DriverUpcomingMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                createGoogleApiClient();
                pMap.setMyLocationEnabled(true);
            }
        } else {
            createGoogleApiClient();
            pMap.setMyLocationEnabled(true);
        }
        pMap.setOnCameraChangeListener(this);
    }

    /*-------------------------------------------------------------------------------------------------------------------*/

    public  void setPassengerMarker(LatLng picmarker,String locType,String infoPickAddress){
        //  if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))

        BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.passenger_marker);
        passengerMarker = pMap.addMarker(new MarkerOptions().position(picmarker).title("pickup : "+infoPickAddress));
        passengerMarker.setIcon(pickupIcon);

        pMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picmarker, 15));

    }
    /*-------------------------------------------------------------------------------------------------------------------*/

    public  void setCurrent(LatLng picmarker,String locType,String infoPickAddress){
        BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_current_loc);
        pMap.addMarker(new MarkerOptions().position(picmarker).title("Your Current location")).setIcon(pickupIcon);

        pMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picmarker, 15));

    }
    /*-------------------------------------------------------------------------------------------------------------------*/
    public  void setDestination(LatLng marker,String locType,String infoAddress){
        if (AppUtility.isNetworkAvailable(DriverUpcomingMapActivity.this))
        {
            BitmapDescriptor destiIcon = BitmapDescriptorFactory.fromResource(R.drawable.driver_marker);
            driverMarker = pMap.addMarker(new MarkerOptions().position(marker).title("destination : "+infoAddress));
            driverMarker.setIcon(destiIcon);

            pMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,14));
            //    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.5f));
        }
        else {
            AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, getString(R.string.network_not_available), "", "Ok");
        }
    }

    /*---------------------------------------------------------------------------------------------------------------------------------*/

    private void tripInfoDialog() {
        final Dialog dialogUpcomingRide = new Dialog(DriverUpcomingMapActivity.this, android.R.style.Theme_Light);
        dialogUpcomingRide.setCanceledOnTouchOutside(false);
        dialogUpcomingRide.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpcomingRide.setContentView(R.layout.dailog_driver_current_ride_info);
        dialogUpcomingRide.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialogUpcomingRide.getWindow();
        window.setGravity(Gravity.CENTER);

        //   btnDailogDriverTripStart = (Button) dialogCurrentRunningRide.findViewById(R.id.btnDailogDriverTripStart);
        // btnDailogDriverTripEnd = (Button) dialogCurrentRunningRide.findViewById(R.id.btnDailogDriverTripEnd);
        Button btnDailogDriveCancel = (Button) dialogUpcomingRide.findViewById(R.id.btnDailogDriveCancelTrip);
        Button btnDailogDriverCall = (Button) dialogUpcomingRide.findViewById(R.id.btnDailogDriverCall);

        TextView tvCurrentPassengerName = (TextView) dialogUpcomingRide.findViewById(R.id.tvCurrentPassengerName);
        TextView tvCurrentPassBookingId = (TextView) dialogUpcomingRide.findViewById(R.id.tvCurrentPassBookingId);
        TextView tvCurrentPassToAddress = (TextView) dialogUpcomingRide.findViewById(R.id.tvCurrentPassToAddress);
        TextView tvCurrentPassFromAddress = (TextView) dialogUpcomingRide.findViewById(R.id.tvCurrentPassFromAddress);
        TextView tvCurrentPassDetails = (TextView) dialogUpcomingRide.findViewById(R.id.tvCurrentPassDetails);
        TextView tvRidestatus = (TextView) dialogUpcomingRide.findViewById(R.id.tvRidestatus);
        TextView tvRideType = (TextView) dialogUpcomingRide.findViewById(R.id.tvRideType);
        TextView tvPassDistance = (TextView) dialogUpcomingRide.findViewById(R.id.tvPassDistance);
        TextView tvPassCost = (TextView) dialogUpcomingRide.findViewById(R.id.tvPassCost);
        TextView tvRideStatusAndType = (TextView) dialogUpcomingRide.findViewById(R.id.tvRideStatusAndType);

        // String Details = "Trip time - " + finalTime + ", " + " Date - " + StartDate;
        tvCurrentPassengerName.setText(fullName);
        if (rideType.equals("TL")){
            rideType = "Trip later";
        }
        tvCurrentPassBookingId.setText(bookingId);
        tvCurrentPassToAddress.setText(pickupAddress);
        tvCurrentPassFromAddress.setText(destinationAddress);
        tvCurrentPassDetails.setText("Date : "+rideStartDate +" "+"Time : "+rideStartTime);
        tvPassDistance.setText(dist + " km");
        tvRideType.setText(rideType);
        tvPassCost.setText("$ " + String.format("%.2f", tripTotal));

        if (rideStatusValue.equals("2")) {
            tvRidestatus.setText("Not start ");
        }
        if (rideStatusValue.equals("3")) {
            tvRidestatus.setText("Trip start ");
        }
        if (rideStatusValue.equals("6")) {
            tvRidestatus.setText("Driver on the way ");
        }

        btnDailogDriveCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogUpcomingRide.dismiss();

            }
        });

        btnDailogDriverCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mobileNumber.equals("")) {

                    if (ContextCompat.checkSelfPermission(DriverUpcomingMapActivity.this,
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(DriverUpcomingMapActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    } else {
                        executeCall();
                    }

                    dialogUpcomingRide.dismiss();
                }
            }
        });

        dialogUpcomingRide.show();
    }

       /*----------------------------------------------------------------------------------------------------------------*/

    private void executeCall() {
        // start your call here
        DriverUpcomingMapActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mobileNumber.equals("")) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+" + mobileNumber));
                    if (ActivityCompat.checkSelfPermission(DriverUpcomingMapActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                }
            }
        });
    }

    /****************************************************************************************************************************************/

    public void GetStartAndEndRideTask() {

        customDialog = new CustomDialog(DriverUpcomingMapActivity.this);
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

                            //   driverRequestItemList.clear();

                            if (status.equalsIgnoreCase("1")) {

                                AppUtility.showToast(DriverUpcomingMapActivity.this, message, 1);

                                if (isStartTripButtonClicked == true) {

                                    btnDriverUpcomingStarttrip.setVisibility(View.GONE);
                                    btnDriverUpcomingCanceltrip.setVisibility(View.GONE);
                                    btnDriverUpcomingEndtrip.setVisibility(View.VISIBLE);
                                    btnDriverUpcomingOntheWay.setVisibility(View.GONE);
                                    layout_footer1.setVisibility(View.GONE);
                                    //layout_footer3.setVisibility(View.GONE);
                                    layout_footer2.setVisibility(View.VISIBLE);

                                }else if(actionStatus.equals("4") || message.equalsIgnoreCase("Your ride has been ended")){
                                    sendTripRegistrationInfo_To_Manasah_Wasl_API();
                                    //    getVisibleFragment();
                                    btnDriverUpcomingStarttrip.setVisibility(View.GONE);
                                    btnDriverUpcomingCanceltrip.setVisibility(View.GONE);
                                    btnDriverUpcomingOntheWay.setVisibility(View.GONE);
                                    layout_footer1.setVisibility(View.GONE);
                                    layout_footer2.setVisibility(View.GONE);
                                    //   layout_footer3.setVisibility(View.GONE);

                                    /*if (AppUtility.isNetworkAvailable(DriverUpcomingMapActivity.this)) {
                                        //  GetDriverCurrentTripInfoTask();
                                    } else {
                                        AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, getString(R.string.network_not_available), "", "Ok");
                                    }*/
                                    Intent i = new Intent(DriverUpcomingMapActivity.this, DriverMyBookingsActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    startActivity(i);


                                } else if (isCancelTripButtonClicked == true) {
                                    btnDriverUpcomingStarttrip.setVisibility(View.GONE);
                                    btnDriverUpcomingCanceltrip.setVisibility(View.GONE);
                                    btnDriverUpcomingOntheWay.setVisibility(View.GONE);
                                    layout_footer1.setVisibility(View.GONE);
                                    layout_footer2.setVisibility(View.GONE);
                                    //  layout_footer3.setVisibility(View.GONE);
                                    DriverCurrentMapFragment.createView = 0;
                                    DriverUpcomingTripFragment.createUpcomingTripView = 0;
                                    DriverPriviousTripFragment.createPriviousTripView = 0;
                                    DriverCancelBookingsFragment.createCancelView = 0;

                                    Intent i = new Intent(DriverUpcomingMapActivity.this, DriverMyBookingsActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    startActivity(i);
                                    /*if (AppUtility.isNetworkAvailable(DriverUpcomingMapActivity.this)) {
                                        // GetDriverCurrentTripInfoTask();
                                    } else {
                                        AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, getString(R.string.network_not_available), "", "Ok");
                                    }*/

                                } else if (isOnTheWayButtonClicked == true) {
                                    btnDriverUpcomingStarttrip.setVisibility(View.VISIBLE);
                                    btnDriverUpcomingCanceltrip.setVisibility(View.VISIBLE);
                                    layout_footer1.setVisibility(View.VISIBLE);
                                    //  layout_footer3.setVisibility(View.GONE);
                                    //    layout_footer3.setVisibility(View.GONE);
                                    layout_footer2.setVisibility(View.GONE);
                                    btnDriverUpcomingOntheWay.setVisibility(View.GONE);
                                    Intent intentOntheWay = new Intent(DriverUpcomingMapActivity.this, DriverMyBookingsActivity.class);

                                    DriverCurrentMapFragment.createView = 0;
                                    DriverUpcomingTripFragment.createUpcomingTripView = 0;
                                    DriverPriviousTripFragment.createPriviousTripView = 0;
                                    DriverCancelBookingsFragment.createCancelView = 0;
                                    finish();
                                    startActivity(intentOntheWay);
                                    overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                                }

                                // requestDialog();

                            } else if (response.isEmpty()) {
                                AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, "Server error occured, please try again later!", "", "Ok");
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, message, "", "Ok");

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
                        Toast.makeText(DriverUpcomingMapActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
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
                if (!AuthToken.equals("")) {
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(DriverUpcomingMapActivity.this);
        requestQueue.add(stringRequest);
    }


    /**
     * This method used to send location update to manasah api
     */
    private void sendTripRegistrationInfo_To_Manasah_Wasl_API() {

        StringRequest sendInfo = new StringRequest(Request.Method.POST,  Constant.URL_TRIP_REG_INFO_MANASAH_WASL_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppUtility.log_DEBUG("Response ",response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppUtility.log_DEBUG("Response ",error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("apiKey", Constant.MANASAH_WASL_API_KEY);
                params.put("vehicleReferenceNumber", sessionManager.getVehicleNo());
                params.put("captainReferenceNumber", sessionManager.getDriverCaptionIdentityNumber());
                params.put("distanceInMeters", dist);
                params.put("customerRating", "5.0");
                params.put("customerWaitingTimeInSeconds", "");
                params.put("originCityNameInArabic", pickupAddress);
                params.put("destinationCityNameInArabic", destinationAddress);
                params.put("originLatitude", String.valueOf(dPickupLat));
                params.put("originLongitude", String.valueOf(dPickupLong));
                params.put("destinationLatitude", String.valueOf(dDstinationLat));
                params.put("destinationLongitude", String.valueOf(dDstinationLong));
                params.put("pickupTimestamp",rideStartTime );
                params.put("dropoffTimestamp", String.valueOf(System.currentTimeMillis()));
                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(sendInfo);

    }

    /****************************************************************************************************************************************/
    public void GetCancelRideTask() {
        customDialog = new CustomDialog(DriverUpcomingMapActivity.this);
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
                                dialogCancelUpcomingRide.dismiss();
                                //    btnDriverStarttrip.setEnabled(false);
                                btnDriverUpcomingStarttrip.setVisibility(View.GONE);
                                btnDriverUpcomingCanceltrip.setVisibility(View.GONE);
                                btnDriverUpcomingOntheWay.setVisibility(View.GONE);
                                layout_footer1.setVisibility(View.GONE);
                                layout_footer2.setVisibility(View.GONE);
                                layout_footer3.setVisibility(View.GONE);
                                AppUtility.showToast(DriverUpcomingMapActivity.this, message, 1);

                               /* if (AppUtility.isNetworkAvailable(DriverUpcomingMapActivity.this)) {
                                    //GetDriverCurrentTripInfoTask();
                                } else {
                                    AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, getString(R.string.network_not_available), "", "Ok");
                                }*/
                                dialogCancelUpcomingRide.dismiss();
                                Intent i = new Intent(DriverUpcomingMapActivity.this, DriverHomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(i);
                                // Reload current fragment

                            } else if (response.isEmpty()) {
                                AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, "Server error occured, please try again !", "", "Ok");
                            }  else {
                                AppUtility.showAlertDialog_SingleButton(DriverUpcomingMapActivity.this, message, "", "Ok");

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
                        Toast.makeText(DriverUpcomingMapActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
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
                if (!AuthToken.equals("")) {
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(DriverUpcomingMapActivity.this);
        requestQueue.add(stringRequest);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("DriverUpcomingMap Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
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

            ParserTask parserTask = new ParserTask();

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
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
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
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(getResources().getColor(R.color.colorPrimary));

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                pMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }
    /*----------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onBackPressed() {
        DriverCurrentMapFragment.createView = 0;
        DriverCancelBookingsFragment.createCancelView = 0;
        DriverPriviousTripFragment.createPriviousTripView = 0;
        DriverUpcomingTripFragment.createUpcomingTripView = 0;
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
        startActivity(new Intent(DriverUpcomingMapActivity.this, DriverMyBookingsActivity.class));
        finish();
    }
}
