package com.mishwar.passanger_ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
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
import com.mishwar.R;
import com.mishwar.adapter.PlacesAutoCompleteAdapter;
import com.mishwar.adapter.VehichleTypeAdapter;

import com.mishwar.commun_ui.SettingActivity;
import com.mishwar.driver_ui.ShowMonthllyTripsActivity;
import com.mishwar.model.AllTrips;
import com.mishwar.model.CarFacility;
import com.mishwar.passenger_fragments.CurrentMapFragment;
import com.mishwar.passenger_fragments.PassengerCancelBookingsFragment;
import com.mishwar.helper.CircleTransform;
import com.mishwar.helper.ConnectionDetector;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.listner.CustomButtonListener;
import com.mishwar.model.NearestDriversBean;
import com.mishwar.model.VehichleTypeBean;
import com.mishwar.reciver.ReceiverPositioningAlarm;
import com.mishwar.scrol_listview.TwoWayAdapterView;
import com.mishwar.scrol_listview.TwoWayGridView;
import com.mishwar.service.GpsService;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.DatePickerFragment;
import com.mishwar.utils.DirectionsJSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity implements com.google.android.gms.maps.GoogleMap.OnCameraChangeListener,CustomButtonListener, OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,LocationListener,View.OnClickListener {

    public static final String TAG = HomeActivity.class.getSimpleName();

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private SessionManager sessionManager;
    PlacesAutoCompleteAdapter mPlacesAdapter;
    private LatLng pickup, destination,PickUpPoint,DestiNationPoint;
    private Marker pickupMarker,destinationMarker,driversMarker,secondPickupMarker;
    private boolean pickuplatlng = true,IsEditlatlng = false,IsVehichleSelected = false,destinationlatlng = false, gps_enabled = false,network_enabled = false;
    private Button pickupPin,destinationPin,b_logout;
    private TextView tv_pickup,tv_destination,home_tv_vehichle_type,tvPaymentType,home_tv_confrmBooking,
            tvPassNavProfilePic,tv_carTypeDetail_distance,tv_carTypeDetail_time,tv_date_picker,tv_time_picker;
    private ImageView iv_addPickup,iv_addDestn,iv_editPickup,iv_editDestn,ivPaymentType,home_b_confirmbooking,iv_tripNow,iv_tripLater,iv_tripMonthly,
            home_iv_vehicletype,ivPassNavProfilePic,ivPassNavForward;
    private CustomDialog customDialog;
    AutoCompleteTextView tv_destn_hd,tv_pickup_hd;
    private List<NearestDriversBean> nearestDriversItemList ;
    TwoWayGridView lv_vehicleType;
    private   RelativeLayout tripNowLayout,rl_pickup,rl_destn;
    private ArrayList<VehichleTypeBean> vehichleType_arraylist ;
    private ArrayList<AllTrips> homePassUpcomingTripArraylist;
    VehichleTypeAdapter vehichleTypeAdapter;
    private  double lat1,lat2,lng1,lng2,finalDistance = 0.0,timeDuration = 0.0;
    int counter = 1,carSeat,mHour, mMinute;
    private String trip_total="",vehicleId="",tripType="",currentDate,tripdate,tripTime,driverProfileImage, bookingId="", sPaymentType = "",driverId ="",paramDistance="",pickupAddress="",destinationAddress="", pickupLatLong="",destinationLatLong="";

    public static Dialog dialogTimmer;
    public static CountDownTimer Timmer;
    final Handler handler = new Handler();
    private Timer timer;
    private TimerTask timerTask;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    public static  boolean IsDriverFound;
    private Toolbar toolbar;
    LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        IsDriverFound = false;

        initializeView();


/*--------------------------------------------------------------------------------------------------------------------*/
        //location on from setting
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}
        if(!gps_enabled && !network_enabled) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
            builder1.setMessage(getString(R.string.text_alert_gps_not_on));
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    getString(R.string.text_btn_open_setting),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            HomeActivity.this.startActivity(myIntent);
                        }
                    });

            builder1.setNegativeButton(
                    getString(R.string.text_btn_no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }

        }
    }

/*-------------------------------------------------------------------------------------------------------------------*/

    private void initializeView() {

        sessionManager = new SessionManager(HomeActivity.this);
        vehichleType_arraylist = new ArrayList<VehichleTypeBean>();
        homePassUpcomingTripArraylist = new ArrayList<AllTrips>();
        nearestDriversItemList = new ArrayList<NearestDriversBean>();

        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.passenger_drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        tvPassNavProfilePic = (TextView) headerView.findViewById(R.id.tvPassNavProfilePic);
        home_tv_confrmBooking = (TextView) findViewById(R.id.home_tv_confrmBooking);

        tv_carTypeDetail_distance = (TextView) findViewById(R.id.tv_carTypeDetail_distance);
        tv_carTypeDetail_time = (TextView) findViewById(R.id.tv_carTypeDetail_time);
        TextView   tv_tripLater = (TextView)findViewById(R.id.home_tv_later);
        //  tv_tripMonthly = (TextView)findViewById(R.id.home_tv_monthly);
        tvPaymentType = (TextView)findViewById(R.id.tvPaymentType);
        home_tv_vehichle_type = (TextView)findViewById(R.id.home_tv_vehichle_type);
        tv_pickup = (TextView)findViewById(R.id.home_tv_setpickup);
        tv_destination = (TextView)findViewById(R.id.home_tv_setdestination);

        tv_pickup_hd = (AutoCompleteTextView)findViewById(R.id.tv_pick_hd);
        tv_destn_hd = (AutoCompleteTextView)findViewById(R.id.tv_destn_hd);

        rl_pickup = (RelativeLayout)findViewById(R.id.home_rl_pickup);
        rl_destn = (RelativeLayout)findViewById(R.id.home_rl_destn);
        tripNowLayout =(RelativeLayout) findViewById(R.id.layout_tripNow);

        iv_tripNow = (ImageView)findViewById(R.id.home_b_tripnow);
        ivPaymentType = (ImageView) findViewById(R.id.ivPaymentType);
        home_b_confirmbooking = (ImageView) findViewById(R.id.home_b_confirmbooking);
        iv_tripMonthly = (ImageView)findViewById(R.id.home_b_tripmothly);
        iv_editPickup = (ImageView)findViewById(R.id.home_iv_editpickup);
        iv_editDestn = (ImageView)findViewById(R.id.home_iv_editdestn);
        home_iv_vehicletype = (ImageView) findViewById(R.id.home_iv_vehicletype);
        iv_addPickup = (ImageView)findViewById(R.id.home_iv_addpickup);
        iv_addDestn = (ImageView) findViewById(R.id.home_iv_adddestination);
        iv_tripLater = (ImageView)findViewById(R.id.home_b_triplater);
        ivPassNavProfilePic = (ImageView)headerView.findViewById(R.id.ivPassNavProfilePic);
        ivPassNavForward = (ImageView) headerView.findViewById(R.id.ivPassNavForward);

        b_logout = (Button) findViewById(R.id.home_b_logout);
        assert b_logout != null;
        pickupPin = (Button)findViewById(R.id.home_b_pickup_pin);
        destinationPin = (Button)findViewById(R.id.home_b_destn_pin);


        Glide.with(this).load(sessionManager.getProfileImage()).placeholder(R.drawable.person).transform(new CircleTransform(this)).override(100, 100).into(ivPassNavProfilePic);
        tvPassNavProfilePic.setText(sessionManager.getFullName().toString());


        iv_tripMonthly.setOnClickListener(this);
        iv_tripLater.setOnClickListener(this);
        home_iv_vehicletype.setOnClickListener(this);
        ivPaymentType.setOnClickListener(this);
        iv_tripNow.setOnClickListener(this);
        ivPassNavForward.setOnClickListener(this);
        home_b_confirmbooking.setOnClickListener(this);
        iv_editDestn.setOnClickListener(this);
        iv_editPickup.setOnClickListener(this);
        iv_addPickup.setOnClickListener(this);
        iv_addDestn.setOnClickListener(this);
        b_logout.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            //  Intent intent = getIntent();
            String  notificationMsg = bundle.getString("notificationMsg");
            String  notificationTitle = bundle.getString("notificationTitle");

            if (!notificationMsg.equals("") && !notificationTitle.equals("")  ){
                //   Counter.cancel();
                if (notificationTitle.equalsIgnoreCase("Rejected"))
                {
                    Timmer.cancel();
                    AppUtility.showAlertDialog_SingleButton(HomeActivity.this, getString(R.string.text_req_has_rejected), "", getString(R.string.text_btn_ok));

                }else if (notificationTitle.equalsIgnoreCase("Accepted")){
                    AppUtility.showToast(HomeActivity.this, getString(R.string.text_req_has_accepted), 1);
                    dialogTimmer.dismiss();
                    Intent i = new Intent(HomeActivity.this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(i);
                }

            }

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // Create a new fragment and specify the planet to show based on
        // position

        switch (item.getItemId()) {
            case R.id.nav_my_profile:
                startActivity(new Intent(this, PassengerUpdateProfileActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                break;

            case R.id.nav_my_booking:
                CurrentMapFragment.createView =0;
                PassengerCancelBookingsFragment.createCancelView =0;
                startActivity(new Intent(this, PassengerMyBookingsActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                break;


            case R.id.nav_trip_monthlly:
                startActivity(new Intent(this, ShowPassengerMonthlyTripActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, SettingActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;

            case R.id.nav_logout:
                sessionManager.logout();
                ReceiverPositioningAlarm.stopLocationListener();
                stopService(new Intent(HomeActivity.this, GpsService.class));
                AppUtility.showToast(HomeActivity.this, getString(R.string.text_logout_msg), 0);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out);

                break;
            default:

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*-------------------------------------------------------------------------------------------------------------------*/

    private void getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        Date dt = new Date();
        int hours = dt.getHours();
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();
        String curTime = hours + ":" + minutes + ":" + seconds;

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        tripdate = formattedDate;
        tripTime = curTime;

        SimpleDateFormat readFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy/MM/dd");
        java.util.Date date;
        try {
            date = readFormat.parse(formattedDate);
            formattedDate = writeFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currentDate = formattedDate;
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    boolean VehicleTypeViewIsShowing = false, isVehicleAPICalled = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*Action perform for tripmothly*/
            case R.id.home_b_tripmothly:

                tripType = Constant.TRIP_TYPE_MONTHLY;
                pickupAddress = tv_pickup_hd.getText().toString().trim();
                destinationAddress = tv_destn_hd.getText().toString().trim();

                home_b_confirmbooking.setSelected(false);
                home_tv_confrmBooking.setTextColor(getResources().getColor(R.color.white));
                home_b_confirmbooking.setImageResource(R.drawable.icon_conform_booking);

                Intent tripMonthlyintent = new Intent(HomeActivity.this,TripMonthlyActivity.class);
                tripMonthlyintent.putExtra("pickupAddress",pickupAddress);
                tripMonthlyintent.putExtra("destinationAddress", destinationAddress);
                tripMonthlyintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
                startActivity(tripMonthlyintent);
                finish();

                break;

            case R.id.ivPassNavForward:
                drawer.closeDrawers();

                break;

            case R.id.home_b_logout:
                sessionManager.logout();
                ReceiverPositioningAlarm.stopLocationListener();
                stopService(new Intent(HomeActivity.this, GpsService.class));
                AppUtility.showToast(HomeActivity.this, getString(R.string.text_logout_msg), 0);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out);

                break;

            case R.id.home_iv_editdestn:

                pickupAddress = tv_pickup_hd.getText().toString().trim();
                destinationAddress = tv_destn_hd.getText().toString().trim();
                tv_pickup_hd.clearFocus();
                tv_destn_hd.setEnabled(true);
                tv_pickup_hd.setEnabled(true);

                if (!pickupAddress.equals(""))

                {
                    if (!destinationAddress.equals("")) {

                        pickuplatlng = true;
                        destinationlatlng = true;
                        pickupPin.setVisibility(View.GONE);
                        rl_pickup.setVisibility(View.GONE);
                        rl_destn.setVisibility(View.VISIBLE);
                        destinationPin.setVisibility(View.VISIBLE);
                        destinationAddress = tv_destn_hd.getText().toString().trim();
                        tv_destn_hd.setVisibility(View.VISIBLE);
                        iv_editDestn.setVisibility(View.VISIBLE);
                        tv_destination.setText(destinationAddress);

                        editDestination(pickup, destination, pickupAddress, destinationAddress);
                    } else {
                        AppUtility.showToast(HomeActivity.this, getString(R.string.text_select_destination), Toast.LENGTH_SHORT);

                    }
                } else {
                    AppUtility.showToast(HomeActivity.this, getString(R.string.text_select_pickup), Toast.LENGTH_SHORT);

                }

                break;

            case R.id.home_iv_editpickup:
                pickupAddress = tv_pickup_hd.getText().toString().trim();
                destinationAddress = tv_destn_hd.getText().toString().trim();
                tv_destn_hd.setEnabled(true);
                tv_pickup_hd.setEnabled(true);
                tv_destn_hd.clearFocus();
                ;

                if (!pickupAddress.equals("")) {
                    IsEditlatlng = true;
                    pickuplatlng = true;
                    rl_pickup.setVisibility(View.VISIBLE);
                    pickupPin.setVisibility(View.VISIBLE);
                    pickupAddress = tv_pickup_hd.getText().toString().trim();
                    tv_pickup_hd.setVisibility(View.VISIBLE);
                    iv_editPickup.setVisibility(View.VISIBLE);
                    tv_pickup.setText(pickupAddress);

                    editPickUp(pickup, destination, pickupAddress, destinationAddress);

                    rl_destn.setVisibility(View.GONE);

                } else {

                    AppUtility.showToast(HomeActivity.this, "please select location", Toast.LENGTH_SHORT);

                }

                break;

            case R.id.home_iv_adddestination:
                destinationlatlng = true;
                pickuplatlng = true;
                rl_pickup.setVisibility(View.GONE);
                rl_destn.setVisibility(View.GONE);
                pickupPin.setVisibility(View.GONE);
                destinationPin.setVisibility(View.GONE);
                destinationAddress = tv_destination.getText().toString().trim();
                tv_destn_hd.setVisibility(View.VISIBLE);
                iv_editDestn.setVisibility(View.VISIBLE);
                tv_destn_hd.setText(destinationAddress);

                drawMarker();
                //setDestination(destination, "destination", destinationAddress);
                mMap.setOnCameraChangeListener(null);

                break;

            case R.id.home_iv_addpickup:
                tv_destn_hd.clearFocus();
                tv_pickup_hd.clearFocus();

                if (pickup != null && !IsEditlatlng) {
                    pickuplatlng = false;
                    rl_pickup.setVisibility(View.GONE);
                    pickupPin.setVisibility(View.GONE);
                    rl_destn.setVisibility(View.VISIBLE);
                    destinationPin.setVisibility(View.VISIBLE);
                    pickupAddress = tv_pickup.getText().toString().trim();
                    tv_pickup_hd.setVisibility(View.VISIBLE);
                    iv_editPickup.setVisibility(View.VISIBLE);
                    tv_pickup_hd.setText(pickupAddress);
                    setPickUp(pickup, "pickup", pickupAddress);
                } else {
                    pickuplatlng = true;
                    rl_pickup.setVisibility(View.GONE);
                    pickupAddress = tv_pickup.getText().toString().trim();
                    tv_pickup_hd.setText(pickupAddress);

                    drawMarker(); // dharmraj
                }

                mMap.setOnCameraChangeListener(HomeActivity.this);

                break;

            case R.id.home_b_triplater:
                //getCurrentDateTime();
                tripType = Constant.TRIP_TYPE_LETER;
                home_b_confirmbooking.setSelected(false);
                home_tv_confrmBooking.setTextColor(getResources().getColor(R.color.white));
                home_b_confirmbooking.setImageResource(R.drawable.icon_conform_booking);
                home_tv_vehichle_type.setTextColor(getResources().getColor(R.color.white));
                home_iv_vehicletype.setImageResource(R.drawable.vehicle);
                tv_destn_hd.clearFocus();
                tv_pickup_hd.clearFocus();

                pickupAddress = tv_pickup_hd.getText().toString().trim();
                destinationAddress = tv_destn_hd.getText().toString().trim();

                if (pickup != null && !pickupAddress.equals("")) {
                    if (destination != null && !destinationAddress.equals("")) {
                        //  if (sessionManager.getBookingId().equals("") || sessionManager.getTripType().equals("TL")){

                        iv_tripLater.setSelected(true);

                        showTripLetterDialog();

                    }
                    else {
                        AppUtility.showToast(HomeActivity.this,  getString(R.string.text_select_destination), Toast.LENGTH_SHORT);
                    }
                } else {
                    AppUtility.showToast(HomeActivity.this, getString(R.string.text_select_pickup), Toast.LENGTH_SHORT);
                }

                break;

        /*Action perform for vehicle type*/
            case R.id.home_iv_vehicletype:

                pickupAddress = tv_pickup_hd.getText().toString().trim();
                destinationAddress = tv_destn_hd.getText().toString().trim();
                tv_destn_hd.clearFocus();
                tv_pickup_hd.clearFocus();

                if (!pickupAddress.equals("")) {

                    if (!destinationAddress.equals("")) {

                        RelativeLayout frame_vehicleLayout = (RelativeLayout) findViewById(R.id.vehicletype_options_list_view);
                        //    frame_vehicleLayout.setVisibility(View.VISIBLE);
                        RelativeLayout frame_cnfrmBooking = (RelativeLayout) findViewById(R.id.layout_confirm);
                        frame_cnfrmBooking.setVisibility(View.GONE);

                        RelativeLayout frame_tripNow = (RelativeLayout) findViewById(R.id.tripNow_RideOptions);
                        frame_tripNow.setVisibility(View.GONE);
                        //  mMap.getUiSettings().setScrollGesturesEnabled(true);
                        home_b_confirmbooking.setSelected(false);
                        home_tv_confrmBooking.setTextColor(getResources().getColor(R.color.white));
                        home_b_confirmbooking.setImageResource(R.drawable.icon_conform_booking);

                        home_tv_vehichle_type.setTextColor(getResources().getColor(R.color.colorBrightGreen));
                        home_iv_vehicletype.setImageResource(R.drawable.icon_vichletype);

                        lv_vehicleType = (TwoWayGridView) findViewById(R.id.lv_vehicleType);
                        if (isVehicleAPICalled==true){
                            if (VehicleTypeViewIsShowing == true){
                                frame_vehicleLayout.setVisibility(View.VISIBLE);
                                VehicleTypeViewIsShowing= false;
                            }else {
                                frame_vehicleLayout.setVisibility(View.GONE);
                                VehicleTypeViewIsShowing= true;
                            }
                        }
                        //getVehichleTypeFromServer();
                        if (ConnectionDetector.isNetworkAvailable(HomeActivity.this)) {
                            if (isVehicleAPICalled==false){
                                //showParamsDialog();
                                GetVehichleTypeTask();
                            }

                        } else {
                            AppUtility.showToast(HomeActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
                        }

                        if (pickup != null && destination != null) {
                            lat1 = pickup.latitude;
                            lng1 = pickup.longitude;
                            lat2 = destination.latitude;
                            lng2 = destination.longitude;
                            System.out.println("latlng :" + pickup + "  latlng :" + destination);
                            // calDistance(lat1,lng1,lat2,lng2,"M");

                            // Getting URL to the Google Directions API
                            String url = getDirectionsUrl(pickup, destination);

                            DownloadTask downloadTask = new DownloadTask();

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);
                        }

                        vehichleTypeAdapter = new VehichleTypeAdapter(HomeActivity.this, vehichleType_arraylist);
                        lv_vehicleType.setAdapter(vehichleTypeAdapter);

                        lv_vehicleType.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
                                tv_destn_hd.setEnabled(false);
                                tv_pickup_hd.setEnabled(false);
                                //  mMap.getUiSettings().setScrollGesturesEnabled(false);
                                iv_editPickup.setEnabled(false);
                                iv_editDestn.setEnabled(false);
                                VehichleTypeBean itemBean = vehichleType_arraylist.get(position);

                                RelativeLayout item_confirm_booking = (RelativeLayout) findViewById(R.id.layout_carTypeDetail);
                                item_confirm_booking.setVisibility(View.VISIBLE);
                                item_confirm_booking.setClickable(true);

                                IsVehichleSelected = true;
                                TextView tv_carTypeDetail_CarRateByDistance = (TextView) findViewById(R.id.tv_carTypeDetail_CarRateByDistance);
                                TextView tv_carTypeDetail_carName = (TextView) findViewById(R.id.tv_carTypeDetail_carName);
                                TextView tv_carTypeDetail_ok = (TextView) findViewById(R.id.tv_carTypeDetail_ok);
                                TextView tv_carTypeDetail_cancle = (TextView) findViewById(R.id.tv_carTypeDetail_cancle);
                                TextView tv_carTypeDetail_CarRateByTime = (TextView) findViewById(R.id.tv_carTypeDetail_CarRateByTime);
                                TextView tv_carTypeDetail_TotalTripPrice = (TextView) findViewById(R.id.tv_carTypeDetail_TotalTripPrice);
                                TextView tv_carTypeDetail_CarOpeningCounter = (TextView) findViewById(R.id.tv_carTypeDetail_CarOpeningCounter);

                                final TextView tv_carTypeDetail_counter = (TextView) findViewById(R.id.tv_carTypeDetail_counter);
                                counter = 1;
                                tv_carTypeDetail_counter.setText(""+counter);


                                ImageView tv_carTypeDetail_carImage = (ImageView) findViewById(R.id.tv_carTypeDetail_carImage);
                                ImageView iv_carTypeDetail_plus = (ImageView) findViewById(R.id.iv_carTypeDetail_plus);
                                ImageView iv_carTypeDetail_minus = (ImageView) findViewById(R.id.iv_carTypeDetail_minus);
                                carSeat = Integer.parseInt(itemBean.getCarSheet().toString());

                                iv_carTypeDetail_plus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String msg = getString(R.string.text_select_max_pass)+" " +carSeat +" "+getString(R.string.text_select_max_pass_for_car)  ;
                                        if (counter < carSeat) {
                                            counter++;
                                            tv_carTypeDetail_counter.setText(Integer.toString(counter));
                                        } else {
                                            AppUtility.showToast(HomeActivity.this, msg , Toast.LENGTH_SHORT);
                                        }
                                    }
                                });

                                iv_carTypeDetail_minus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (counter > 1) {
                                            counter--;
                                            tv_carTypeDetail_counter.setText(Integer.toString(counter));
                                        } else {
                                            AppUtility.showToast(HomeActivity.this, getString(R.string.text_select_pass_msg), Toast.LENGTH_SHORT);
                                        }
                                    }
                                });
                                tv_carTypeDetail_carName.setText(itemBean.getCarName().toString());
                                tv_carTypeDetail_CarRateByDistance.setText(" : " + " $ "+ itemBean.getPriceByDistence().toString()  + "/km");
                                tv_carTypeDetail_CarRateByTime.setText(" : "+ " $ " + itemBean.getPriceByTime().toString()  + " /min");
                                tv_carTypeDetail_CarOpeningCounter.setText(" : " + " $ "+ itemBean.getCounterprice().toString() );
                                Glide.with(HomeActivity.this).load(itemBean.getVehichleType_Url()).into(tv_carTypeDetail_carImage);


                                final double opening_counter, kilmeter_price, minute_price, trip_price, number_of_kilometere, number_of_minutes;

                                kilmeter_price = Double.parseDouble(itemBean.getPriceByDistence().toString());
                                minute_price = Double.parseDouble(itemBean.getPriceByTime().toString());
                                opening_counter = Double.parseDouble(itemBean.getCounterprice().toString());

                                number_of_kilometere = finalDistance;
                                number_of_minutes = timeDuration;

                                trip_price = opening_counter + (number_of_kilometere * kilmeter_price) + (number_of_minutes * minute_price);

                                tv_carTypeDetail_TotalTripPrice.setText(" : "+ " $ " + String.format("%.2f", trip_price) );

                                trip_total = String.valueOf(trip_price);
                                vehicleId = itemBean.getCarId().toString();

                                System.out.println("finalDistance : " + finalDistance);

                                tv_carTypeDetail_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        RelativeLayout item_confirm_booking = (RelativeLayout) findViewById(R.id.layout_carTypeDetail);
                                        item_confirm_booking.setVisibility(View.GONE);

                                        RelativeLayout frame_vehicleLayout = (RelativeLayout) findViewById(R.id.vehicletype_options_list_view);
                                        frame_vehicleLayout.setVisibility(View.GONE);
                                        //  mMap.getUiSettings().setScrollGesturesEnabled(true);

                                    }
                                });

                                tv_carTypeDetail_cancle.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        IsVehichleSelected = false;
                                        vehicleId = "";

                                        home_b_confirmbooking.setSelected(false);
                                        home_tv_confrmBooking.setTextColor(getResources().getColor(R.color.white));
                                        home_b_confirmbooking.setImageResource(R.drawable.icon_conform_booking);

                                        RelativeLayout item_confirm_booking = (RelativeLayout) findViewById(R.id.layout_carTypeDetail);
                                        item_confirm_booking.setVisibility(View.GONE);

                                        RelativeLayout frame_vehicleLayout = (RelativeLayout) findViewById(R.id.vehicletype_options_list_view);
                                        frame_vehicleLayout.setVisibility(View.GONE);

                                    }
                                });

                            }
                        });
                    } else {
                        AppUtility.showToast(HomeActivity.this,  getString(R.string.text_select_destination), Toast.LENGTH_SHORT);

                    }
                }else {
                    AppUtility.showToast(HomeActivity.this,  getString(R.string.text_select_pickup), Toast.LENGTH_SHORT);
                }

                break;

            case R.id.home_b_confirmbooking:

                pickupAddress = tv_pickup_hd.getText().toString().trim();
                destinationAddress = tv_destn_hd.getText().toString().trim();
                tv_destn_hd.setEnabled(false);
                tv_pickup_hd.setEnabled(false);
                iv_editPickup.setEnabled(false);
                iv_editDestn.setEnabled(false);
                iv_addDestn.setEnabled(false);
                tv_destn_hd.clearFocus();
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                tv_pickup_hd.clearFocus();
                rl_pickup.setVisibility(View.GONE);
                rl_destn.setVisibility(View.GONE);

                if (pickup != null && !pickupAddress.equals("")) {

                    if (destination != null && !destinationAddress.equals("")) {
                        final RelativeLayout item_confirm_booking = (RelativeLayout) findViewById(R.id.layout_confirm);

                        if (IsVehichleSelected == true && !vehicleId.equals("")) {

                            if (!sPaymentType.equals("")) {

                                if (IsDriverFound) {
                                    refreshBooking();

                                } else {

                                    RelativeLayout frame_vehicleLayout = (RelativeLayout) findViewById(R.id.vehicletype_options_list_view);
                                    frame_vehicleLayout.setVisibility(View.GONE);

                                    RelativeLayout frame_tripNow = (RelativeLayout) findViewById(R.id.tripNow_RideOptions);
                                    frame_tripNow.setVisibility(View.GONE);

                                    item_confirm_booking.setVisibility(View.VISIBLE);
                                    RelativeLayout item_cardetails = (RelativeLayout) findViewById(R.id.layout_carTypeDetail);
                                    item_cardetails.setVisibility(View.GONE);

                                    home_b_confirmbooking.setSelected(true);
                                    home_tv_confrmBooking.setTextColor(getResources().getColor(R.color.colorBrightGreen));
                                    home_b_confirmbooking.setImageResource(R.drawable.conformbooking_copy);

                                    Button btn_confirm_yes = (Button) findViewById(R.id.btn_confirm_yes);
                                    Button btn_confirm_no = (Button) findViewById(R.id.btn_confirm_no);

                                    btn_confirm_yes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            home_iv_vehicletype.setEnabled(false);
                                            if (ConnectionDetector.isNetworkAvailable(HomeActivity.this)) {
                                                GetConfirmBookingFromServer();
                                                //showParamsOfConfirmBookingDialog();

                                            } else {
                                                AppUtility.showToast(HomeActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);

                                            }
                                        }
                                    });

                                    btn_confirm_no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            item_confirm_booking.setVisibility(View.GONE);

                                        }
                                    });

                                }
                            } else {
                                AppUtility.showToast(HomeActivity.this, "Please select payment type before confirm booking.", Toast.LENGTH_SHORT);
                            }

                        } else {
                            AppUtility.showToast(HomeActivity.this, "Please select one vehicle type", Toast.LENGTH_SHORT);
                        }

                    } else {
                        AppUtility.showToast(HomeActivity.this,  getString(R.string.text_select_destination), Toast.LENGTH_SHORT);
                    }
                } else {
                    AppUtility.showToast(HomeActivity.this, getString(R.string.text_select_pickup), Toast.LENGTH_SHORT);
                }

                break;


            case R.id.ivPaymentType:

                RelativeLayout item_confirm_booking = (RelativeLayout) findViewById(R.id.layout_carTypeDetail);
                item_confirm_booking.setVisibility(View.GONE);

                final CharSequence[] options = {getString(R.string.text_cash), getString(R.string.text_paypal), getString(R.string.text_cancel)};
                ContextThemeWrapper ctw = new ContextThemeWrapper(HomeActivity.this, android.R.style.TextAppearance_Theme_Dialog);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
                builder.setTitle(getString(R.string.text_select_payment_type));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        tv_destn_hd.setEnabled(true);
                        tv_pickup_hd.setEnabled(true);

                        if (options[item].equals(getString(R.string.text_cash))) {
                            sPaymentType = "cash";
                            ivPaymentType.setImageResource(R.drawable.payment_type_green);
                            tvPaymentType.setTextColor(getResources().getColor(R.color.colorBrightGreen));

                        }
                        else if (options[item].equals(getString(R.string.text_paypal))) {
                            sPaymentType = "paypal";
                            ivPaymentType.setImageResource(R.drawable.payment_type_green);
                            tvPaymentType.setTextColor(getResources().getColor(R.color.colorBrightGreen));
                        }
                        else if (options[item].equals(getString(R.string.text_cancel))) {
                            sPaymentType = "";
                            tvPaymentType.setTextColor(getResources().getColor(R.color.white));
                            ivPaymentType.setImageResource(R.drawable.payment_type);

                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

                break;

            /*Action perform for trip now*/
            case R.id.home_b_tripnow:

                tripType = Constant.TRIP_TYPE_NOW;
                tv_destn_hd.clearFocus();
                tv_pickup_hd.clearFocus();

                getCurrentDateTime();

                pickupAddress = tv_pickup_hd.getText().toString().trim();
                destinationAddress = tv_destn_hd.getText().toString().trim();

                RelativeLayout frame_tripNow =(RelativeLayout) findViewById(R.id.tripNow_RideOptions);
                frame_tripNow.setVisibility(View.GONE);

                home_tv_vehichle_type.setTextColor(getResources().getColor(R.color.white));
                home_iv_vehicletype.setImageResource(R.drawable.vehicle);

                //  tvPaymentType.setText("Payment type");
                if (pickup != null && !pickupAddress.equals("")) {

                    if (destination != null && !destinationAddress.equals("")) {

                        if (sessionManager.getBookingId().equals("")){

                            iv_tripNow.setSelected(true);

                            if (iv_tripNow.isSelected()) {

                                tripNowLayout.setVisibility(View.GONE);
                                RelativeLayout frame2 = (RelativeLayout) findViewById(R.id.vehicletype_options);
                                frame2.setVisibility(View.VISIBLE);

                            } else {
                                tripNowLayout.setVisibility(View.VISIBLE);
                            }

                        }else if (sessionManager.getTripType().equals("TN")){
                            AppUtility.showAlertDialog_SingleButton(HomeActivity.this, "You have already created ride, you can not creat ride now.", "","Ok");
                        }else if (sessionManager.getTripType().equals("TL")){
                            AppUtility.showAlertDialog_SingleButton(HomeActivity.this, "You have already created ride for later, you can not creat ride now.", "","Ok");
                        }
                    }else {
                        AppUtility.showToast(HomeActivity.this, "Please select destination  address!", Toast.LENGTH_SHORT);
                    }
                } else {
                    AppUtility.showToast(HomeActivity.this, getString(R.string.text_select_pickup), Toast.LENGTH_SHORT);
                }

                break;

            case R.id.iv_timePrDay_tripMonthly:

                break;

            /*case R.id.iv_dayPerWeek_tripMonthly:

                break;*/
        }
    }

/*-------------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {

        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {

        }
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
            builder1.setMessage("Gps network not available !");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Open Location setting",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            HomeActivity.this.startActivity(myIntent);
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }else {
            if (pickup != null || destination!=null){
                if (pickuplatlng) {
                    pickup = cameraPosition.target;
                    setAddress(pickup, tv_pickup);
                } else {
                    destination = cameraPosition.target;
                    setAddress(destination, tv_destination);
                }

            }else {
                AppUtility.showAlertDialog_SingleButton(HomeActivity.this, "Gps network not available ! please try again.", "", "Ok");
            }

        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
        builder1.setMessage(getString(R.string.text_exit_app));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.text_btn_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
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

    /*-------------------------------------------------------------------------------------------------------------------*/
    //show params
    private void showParamsDialog() {
        final Dialog  dialogCurrentRunningRide = new Dialog(HomeActivity.this, android.R.style.Theme_Light);
        dialogCurrentRunningRide.setCanceledOnTouchOutside(false);
        dialogCurrentRunningRide.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCurrentRunningRide.setContentView(R.layout.dialog_show_data);
        dialogCurrentRunningRide.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialogCurrentRunningRide.getWindow();
        window.setGravity(Gravity.CENTER);

        Button  btnSendDialogParam = (Button) dialogCurrentRunningRide.findViewById(R.id.btnSendDialogParam);

        TextView tvDialogShowParam = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvDialogShowParam);

        String AuthToken = sessionManager.getAuthToken().toString();
        tvDialogShowParam.setText("AuthToken : "+AuthToken);

        btnSendDialogParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogCurrentRunningRide.dismiss();

                GetVehichleTypeTask();


            }
        });


        dialogCurrentRunningRide.show();
    }

    //show params
    private void showParamsOfConfirmBookingDialog() {
        final Dialog  dialogCurrentRunningRide = new Dialog(HomeActivity.this, android.R.style.Theme_Light);
        dialogCurrentRunningRide.setCanceledOnTouchOutside(false);
        dialogCurrentRunningRide.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCurrentRunningRide.setContentView(R.layout.dialog_show_data);
        dialogCurrentRunningRide.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialogCurrentRunningRide.getWindow();
        window.setGravity(Gravity.CENTER);

        Button  btnSendDialogParam = (Button) dialogCurrentRunningRide.findViewById(R.id.btnSendDialogParam);
        TextView tvDialogShowParam = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvDialogShowParam);

        if (pickup!=null && destination!=null){
            String pLat = String.valueOf(pickup.latitude);
            String pLng = String.valueOf(pickup.longitude);
            String dLat = String.valueOf(destination.latitude);
            String dLng = String.valueOf(destination.longitude);
            pickupLatLong = pLat +","+pLng;
            destinationLatLong = dLat +","+dLng;

        }else {
            AppUtility.showToast(HomeActivity.this, "please select valid pikup and destination address", Toast.LENGTH_SHORT);
        }

        String passCounter = String.valueOf(counter);

        String AuthToken = sessionManager.getAuthToken().toString();

        String params = "AuthToken : "+AuthToken+ "\n" + "pickupLatLong : "+pickupLatLong + "\n" + "destinationLatLong : " +destinationLatLong
                + "\n" + "pickupAddress : "+pickupAddress + "\n" + "destinationAddress : "+destinationAddress + "\n" + "trip total : "+trip_total + "\n" +
                "trip Type : "+tripType + "\n" + "trip date : "+tripdate + "\n" + "trip Time : "+tripTime + "\n" + "passenger Count : "+passCounter + "\n" + "distance : "+paramDistance
                + "\n" + "vehichle Id : "+vehicleId;

        tvDialogShowParam.setText(params);



        btnSendDialogParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogCurrentRunningRide.dismiss();
                GetConfirmBookingFromServer();
            }
        });


        dialogCurrentRunningRide.show();
    }
    private Dialog dialogTripLetter;
    boolean tripLater = false;
    /*Dialog For trip letter*/
    public  void showTripLetterDialog(){

        dialogTripLetter = new Dialog(HomeActivity.this, android.R.style.Theme_Light);
        dialogTripLetter.setCanceledOnTouchOutside(false);
        dialogTripLetter.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTripLetter.setContentView(R.layout.dialog_trip_letter);
        dialogTripLetter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialogTripLetter.getWindow();
        window.setGravity(Gravity.CENTER);

        RelativeLayout  ly_date_picker = (RelativeLayout) dialogTripLetter.findViewById(R.id.ly_date_picker);
        RelativeLayout  ly_time_picker = (RelativeLayout) dialogTripLetter.findViewById(R.id.ly_time_picker);
        Button  btn_cancel_tripLetter = (Button) dialogTripLetter.findViewById(R.id.btn_cancel_tripLetter);
        Button  btn_submit_tripLetter = (Button) dialogTripLetter.findViewById(R.id.btn_submit_tripLetter);
        tv_date_picker = (TextView) dialogTripLetter.findViewById(R.id.tv_date_picker);
        tv_time_picker = (TextView) dialogTripLetter.findViewById(R.id.tv_time_picker);

        ly_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment(Constant.CALENDAR_DAY, true);
                datePickerFragment.setDateListener(HomeActivity.this);
                datePickerFragment.show(getSupportFragmentManager(), "");

            }
        });

        ly_time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                tv_time_picker.setText(hourOfDay + ":" + minute);

                            }
                        }, mHour, mMinute, false);

                tripTime = tv_time_picker.getText().toString();
                timePickerDialog.show();
                timePickerDialog.setTitle(getString(R.string.text_select_time));
                // AppPreferences.getInstance().setUserAge(tv_signUpActivity_age.getText().toString());

            }
        });
        btn_cancel_tripLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTripLetter.dismiss();
            }
        });
        btn_submit_tripLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDate, selectedTime;
                selectedDate = tv_date_picker.getText().toString();
                selectedTime = tv_time_picker.getText().toString();
                tripdate = tv_date_picker.getText().toString();
                tripTime = tv_time_picker.getText().toString();

                if (!selectedDate.equals("")) {

                    if (!tripdate.equals(currentDate)){

                        if (!selectedTime.equals("")) {
                            tripLater = true;
                            tripNowLayout.setVisibility(View.GONE);
                            RelativeLayout frame2 = (RelativeLayout) findViewById(R.id.vehicletype_options);
                            frame2.setVisibility(View.VISIBLE);
                            dialogTripLetter.dismiss();

                        } else {
                            AppUtility.showToast(HomeActivity.this, getString(R.string.text_select_time), Toast.LENGTH_SHORT);
                        }
                    }  else {
                        AppUtility.showAlertDialog_SingleButton(HomeActivity.this, getString(R.string.text_create_another_typeof_trip), "",getString(R.string.text_btn_ok));
                    }
                }
                else {
                    AppUtility.showToast(HomeActivity.this, getString(R.string.text_select_date), Toast.LENGTH_SHORT);
                }

                dialogTripLetter.dismiss();
            }
        });

        dialogTripLetter.show();
    }

/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onDateSet(int year, int month, int day, int cal_type) {

        tv_date_picker.setText(year + "-" + (month + 1) + "-" + day);
        String tripLaterdate = tv_date_picker.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(tripLaterdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = 0; i < homePassUpcomingTripArraylist.size(); i++)
        {
            String priviousDate = homePassUpcomingTripArraylist.get(i).getRideStartDate();

            SimpleDateFormat priviousDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date priviousDateConvertedDate = new Date();
            try {
                priviousDateConvertedDate = priviousDateFormat.parse(priviousDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (convertedDate.equals(priviousDateConvertedDate)){
                AppUtility.showAlertDialogSingleButtonWithTextView(HomeActivity.this, getString(R.string.text_already_create_ride), "",getString(R.string.text_btn_ok),tv_date_picker);

            }
        }

        tripdate = tripLaterdate;
    }

/*-------------------------------------------------------------------------------------------------------------------*/

    public  void setPickUp(LatLng picmarker,String locType,String infoPickAddress){

        if (AppUtility.isNetworkAvailable(HomeActivity.this)) {
            if (locType.equals("current")) {
                BitmapDescriptor currentIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_current_loc);
                mMap.addMarker(new MarkerOptions().position(picmarker).title("Your Current location")).setIcon(currentIcon);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picmarker, 10));

                lat1 = picmarker.latitude;
                lng1 = picmarker.longitude;

            } else if (locType.equals("pickup")) {
                BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_green);
                pickupMarker = mMap.addMarker(new MarkerOptions().position(picmarker).title(infoPickAddress));
                pickupMarker.setIcon(pickupIcon);
                secondPickupMarker = pickupMarker;
                lat1 = picmarker.latitude;
                lng1 = picmarker.longitude;
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picmarker, 10));
        } else {
            AppUtility.showAlertDialog_SingleButton(HomeActivity.this, getString(R.string.network_not_available), "", getString(R.string.text_btn_ok));
        }
        //    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.5f));

    }
    /*-------------------------------------------------------------------------------------------------------------------*/

    public  void setDestination(LatLng marker,String locType,String infoAddress){
        pickuplatlng = false;
        if (AppUtility.isNetworkAvailable(HomeActivity.this))
        {
            if(locType.equals("current")){
                BitmapDescriptor currentIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_current_loc);
                mMap.addMarker(new MarkerOptions().position(marker).title("Your Current location")).setIcon(currentIcon);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 10));
                Log.d("current loc", "latitude =" + marker.latitude + "longitude =" + marker.longitude);
                lat2= marker.latitude;
                lng2 = marker.longitude;
            }
            else if(locType.equals("destination")){
                pickuplatlng = false;
                BitmapDescriptor destiIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red);
                destinationMarker = mMap.addMarker(new MarkerOptions().position(marker).title(infoAddress));
                destinationMarker.setIcon(destiIcon);
                lat2= marker.latitude;
                lng2 = marker.longitude;
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,10));
            //    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.5f));
        }
        else {
            AppUtility.showAlertDialog_SingleButton(HomeActivity.this, getString(R.string.network_not_available), "", getString(R.string.text_btn_ok));
        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    public  void editPickUp(LatLng picEditmarker,LatLng editDestimarker,String infopickupAddress,String valueOfAddress){
        mMap.clear();
        pickuplatlng = true;
        if(picEditmarker!=null && infopickupAddress.equals("pickup")) {
            BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_green);
            pickupMarker = mMap.addMarker(new MarkerOptions().position(picEditmarker).title(infopickupAddress));
            pickupMarker.setIcon(pickupIcon);
            pickupPin.setVisibility(View.GONE);
        }
        if(secondPickupMarker!=null){
            BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red);
            destinationMarker = mMap.addMarker(new MarkerOptions().position(editDestimarker).title(valueOfAddress));
            destinationMarker.setIcon(pickupIcon);
            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(editDestimarker, 10));
        }

        // Dharmraj
        // setDestination(destination, "destination", destinationAddress);
        if(!destinationAddress.equals("")){
            BitmapDescriptor destiIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red);
            destinationMarker = mMap.addMarker(new MarkerOptions().position(destination).title(destinationAddress));
            destinationMarker.setIcon(destiIcon);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picEditmarker, 10));
        //    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.5f));
        mMap.setOnCameraChangeListener(this);
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    public  void editDestination(LatLng picEditmarker,LatLng editDestimarker,String infopickupAddress,String valueOfAddress){
        mMap.clear();
        pickuplatlng = false;
        if(infopickupAddress.equals("destination")) {
            BitmapDescriptor destiIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red);
            destinationMarker = mMap.addMarker(new MarkerOptions().position(editDestimarker).title(valueOfAddress));
            destinationMarker.setIcon(destiIcon);
        }
        if(picEditmarker!=null){
            BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_green);
            pickupMarker = mMap.addMarker(new MarkerOptions().position(picEditmarker).title(infopickupAddress));
            pickupMarker.setIcon(pickupIcon);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picEditmarker, 10));

        }

        if(!pickupAddress.equals("")){
            BitmapDescriptor destiIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red);
            pickupMarker = mMap.addMarker(new MarkerOptions().position(destination).title(destinationAddress));
            pickupMarker.setIcon(destiIcon);
            pickupPin.setVisibility(View.GONE);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(editDestimarker, 10));
        mMap.setOnCameraChangeListener(this);
    }

/*-------------------------------------------------------------------------------------------------------------------------*/

    /*Listner for Places API*/
    private AdapterView.OnItemClickListener mAutocompleteClickListenerPickUp = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mPlacesAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackPickUp);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackPickUp = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            mMap.clear();

            if(lat2!=0.0&lng2!=0.0){
                setDestination(new LatLng(lat2, lng2), "destination", destinationAddress);
            }

            if (!places.getStatus().isSuccess()) {
                Log.e("place", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            PickUpPoint = place.getLatLng();
            DestiNationPoint = place.getLatLng();
            String geoAdrress = (String) place.getAddress().toString();
            pickup = PickUpPoint;
            pickupAddress = tv_pickup_hd.getText().toString().trim();
            setPickUp(pickup, "pickup", pickupAddress);

            rl_pickup.setVisibility(View.GONE);
            pickupPin.setVisibility(View.GONE);
            //      destination = DestiNationPoint;

        }
    };
    /*-------------------------------------------------------------------------------------------------------------------*/

    /*Listner for Places API*/
    private AdapterView.OnItemClickListener mAutocompleteClickListenerDestiNation = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mPlacesAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackDestiNation);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackDestiNation = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            mMap.clear();
            if(lat1!=0.0&lng1!=0.0){
                setPickUp(new LatLng(lat1,lng1), "pickup", pickupAddress);
            }

            if (!places.getStatus().isSuccess()) {
                Log.e("place", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            PickUpPoint = place.getLatLng();
            DestiNationPoint = place.getLatLng();
            String geoAdrress = (String) place.getAddress().toString();
            destination = DestiNationPoint;
            destinationAddress = tv_destn_hd.getText().toString().trim();
            setDestination(destination, "destination", destinationAddress);

            rl_destn.setVisibility(View.GONE);
            destinationPin.setVisibility(View.GONE);

        }
    };

    /**
     * Manipulates the map once available.
     * installed Google Play services and returned to the app.
     */
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkLocationPermission()){
                createGoogleApiClient();
            }
        } else {
            createGoogleApiClient();
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }


        if (pickuplatlng) {
            pickup = mMap.getCameraPosition().target;
        }else {
            destination = mMap.getCameraPosition().target;
        }
//start backgrtout service for update latlng
        startService(new Intent(this, GpsService.class));


        if (AppUtility.isNetworkAvailable(HomeActivity.this)) {
            GetPassengerUpcomingTripInfoTask();
        } else {
            AppUtility.showAlertDialog_SingleButton(HomeActivity.this, getString(R.string.network_not_available), "", getString(R.string.text_btn_ok));
        }

        mMap.setOnCameraChangeListener(this);

        mPlacesAdapter = new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1, mGoogleApiClient, null, null);
        tv_destn_hd.setOnItemClickListener(mAutocompleteClickListenerDestiNation);
        tv_pickup_hd.setOnItemClickListener(mAutocompleteClickListenerPickUp);

        tv_destn_hd.setAdapter(mPlacesAdapter);
        tv_pickup_hd.setAdapter(mPlacesAdapter);

    }
/*-------------------------------------------------------------------------------------------------------------------*/

    public boolean checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                            Constant.MY_PERMISSIONS_REQUEST_LOCATION);
                }

            } else {
                // No explanation needed, we can request the permission.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                            Constant.MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }
            return false;
        } else {

            return true;

        }
    }
    /*-------------------------------------------------------------------------------------------------------------------*/
    synchronized public void createGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();

            mGoogleApiClient.connect();

        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            createGoogleApiClient();
                        }
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "permission denied", Toast.LENGTH_LONG).show();

                }
            }
        }
    }
    /*-------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"inside onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "inside onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            if (pickuplatlng) {
                pickup = new LatLng(location.getLatitude(), location.getLongitude());
                setAddress(pickup, tv_pickup);
            } else {
                destination = new LatLng(location.getLatitude(), location.getLongitude());
                setAddress(destination, tv_destination);
            }
            // call server api om location change to update location change on server
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        setPickUp(latLng, "current", null);

        //stop location updates
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
        long FASTEST_INTERVAL = 2000; /* 2 sec */

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);


        mPlacesAdapter = new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1, mGoogleApiClient, null, null);
        tv_destn_hd.setOnItemClickListener(mAutocompleteClickListenerDestiNation);
        tv_pickup_hd.setOnItemClickListener(mAutocompleteClickListenerPickUp);

        tv_destn_hd.setAdapter(mPlacesAdapter);
        tv_pickup_hd.setAdapter(mPlacesAdapter);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }
    /*----------------------------------------------------------------------------------------------------------------------------------*/

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&units=metric&mode=driving&optimizeWaypoints=true";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downlrl", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    /*----------------------------------------------------------------------------------------------------------------------------------*/
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-commun_ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-commun_ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            //  PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if(result.size()<1){
                // Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }


            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                //   lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){	// Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                // lineOptions.addAll(points);
                //  lineOptions.width(2);
                //  lineOptions.color(Color.RED);

            }
            try {
                tv_carTypeDetail_distance.setText(" : "+distance);
                paramDistance = distance;
                String[] str = distance.split(" ");
                finalDistance = Double.parseDouble(str[0]);


                if(duration.contains("hour") || duration.contains("hours")){

                    if (duration.contains("hour")){
                        String[] TimeInHours = duration.split(" hour ");
                        String timeInHr = TimeInHours[0];
                        int hours = Integer.parseInt(timeInHr);
                        int minit = hours*60;
                        Log.i("======= hours"," :: "+minit+ "min" );

                        if(duration.contains("mins")){
                            String timeInMin = TimeInHours[1];
                            String[] splitMin = timeInMin.split(" mins");
                            String getMin = splitMin[0];

                            // String[] finalTimeMin = getMin.split(" ");
                            //String min = finalTimeMin[0];
                            int Min = Integer.parseInt(getMin);
                            minit = minit+Min;

                            if ( finalDistance>=10){
                                if (minit<=5){
                                    minit = 15;
                                }
                            }
                            Log.i("======= Min"," :: "+minit );
                        }
                        tv_carTypeDetail_time.setText(" : " + minit +" min");
                        timeDuration = (double) minit;
                    }else {
                        String[] TimeInHours = duration.split(" hours ");
                        String timeInHr = TimeInHours[0];
                        int hours = Integer.parseInt(timeInHr);
                        int minit = hours*60;
                        Log.i("======= hours"," :: "+minit+ "min" );
                        if(duration.contains("mins")){
                            String timeInMin = TimeInHours[1];
                            String[] splitMin = timeInMin.split(" mins");
                            String getMin = splitMin[0];

                            // String[] finalTimeMin = getMin.split(" ");
                            //String min = finalTimeMin[0];
                            int Min = Integer.parseInt(getMin);
                            minit = minit+Min;

                            if ( finalDistance>=10){
                                if (minit<=5){
                                    minit = 15;
                                }
                            }
                            Log.i("======= Min"," :: "+Min );
                        }
                        tv_carTypeDetail_time.setText(" : " + minit +" min");
                        timeDuration = (double) minit;
                    }

                }else {
                    String[] time = duration.split(" ");
                    timeDuration = Double.parseDouble(time[0]);
                    String  timeDurationMin = time[0];
                    tv_carTypeDetail_time.setText(" : " + timeDurationMin +" min");
                }

                /*
                    int days,hours,min;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    Date date1 = simpleDateFormat.parse(duration);

                    int minit = hours*60;
                    duration = minit+min;
                    Log.i("======= Hours"," :: "+hours +"======= min :: "+min+"======= duration :: "+duration);*/

            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println("Distance:" + distance + ", Duration:" + duration);

            // Drawing polyline in the Google Map for the i-th route
            // mMap.addPolyline(lineOptions);
        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    private  void drawMarker(){
        mMap.clear();

        if(!pickupAddress.equals("")){
            BitmapDescriptor destiIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_green);
            pickupMarker = mMap.addMarker(new MarkerOptions().position(pickup).title(pickupAddress));
            pickupMarker.setIcon(destiIcon);
            pickupPin.setVisibility(View.GONE);
        }

        if(!destinationAddress.equals("")){
            BitmapDescriptor destiIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red);
            destinationMarker = mMap.addMarker(new MarkerOptions().position(destination).title(destinationAddress));
            destinationMarker.setIcon(destiIcon);
            destinationPin.setVisibility(View.GONE);
        }

    }

/*-------------------------------------------------------------------------------------------------------------------*/

    public void setAddress(LatLng location,TextView textView) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        double latitude = location.latitude;
        double longitude = location.longitude;
        //  currentLat1 = latitude;
        // currentLng1 = longitude;
        Log.e("latitude", "latitude--" + latitude);
        try {
            Log.e("setAddress ", "inside latitude--" + latitude);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                if (address != null && !address.equals("null")) {
                    textView.setText(address);
                }
                if (address != null && !address.equals("null") && city != null && !city.equals("null")) {
                    textView.setText(address + " ," + city);
                }
                if (address != null && !address.equals("null") && city != null && !city.equals("null") && country != null && !country.equals("null")) {
                    textView.setText(address + " ," + city + " ," + country);

                }

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try{
                if(e instanceof SocketTimeoutException) {
                    throw new SocketTimeoutException();
                }
            } catch (SocketTimeoutException f){

                AppUtility.showAlertDialog_SingleButton(HomeActivity.this,"Time out !, please try again !","",getString(R.string.text_btn_ok));
            }
        }
    }


    /****************************************************************************************************************************************/

    public void GetVehichleTypeTask() {

        customDialog = new CustomDialog(HomeActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_VEHICHAL_TYPE,
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

                                vehichleType_arraylist.clear();

                                JSONArray jsonArray = jsonObj.getJSONArray("carDetail");

                                RelativeLayout frame_vehicleLayout = (RelativeLayout) findViewById(R.id.vehicletype_options_list_view);
                                frame_vehicleLayout.setVisibility(View.VISIBLE);
                                VehicleTypeViewIsShowing = true;
                                isVehicleAPICalled = true;
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    VehichleTypeBean item = new VehichleTypeBean();

                                    item.setCarId(jsonObject.getString("carId").toString().trim());
                                    item.setCarName(jsonObject.getString("carName").toString().trim());
                                    item.setCarSheet(jsonObject.getString("carSeat").toString().trim());
                                    item.setVehichleType_Url(jsonObject.getString("carImage").toString().trim());
                                    item.setPriceByDistence(jsonObject.getString("priceByDistence").toString().trim());
                                    item.setPriceByTime(jsonObject.getString("priceByTime").toString().trim());
                                    item.setCounterprice(jsonObject.getString("Counterprice").toString().trim());

                                    vehichleType_arraylist.add(item);

                                }
                                vehichleTypeAdapter.notifyDataSetChanged();

                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(HomeActivity.this,"Error occured, please try again !","",getString(R.string.text_btn_ok));
                            }

                            else {

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
                        Toast.makeText(HomeActivity.this, getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }) {

            /*   @Override
               public Map<String, String> getParams() throws AuthFailureError {
                   // SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd");
                   //  String todayDate = writeFormat.format(date);
                   Map<String, String> header = new HashMap<>();
                   //   header.put("mobileNumber", otpUrlParameter);
                   return header;
               }
   */            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }else {
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(HomeActivity.this,"Already logged in on another device","",getString(R.string.text_btn_ok));
                    //Toast.makeText(HomeActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/

    public void GetConfirmBookingFromServer() {

        customDialog = new CustomDialog(HomeActivity.this);
        customDialog.show();
        if (pickup!=null && destination!=null){
            String pLat = String.valueOf(pickup.latitude);
            String pLng = String.valueOf(pickup.longitude);
            String dLat = String.valueOf(destination.latitude);
            String dLng = String.valueOf(destination.longitude);
            pickupLatLong = pLat +","+pLng;
            destinationLatLong = dLat +","+dLng;

        }else {
            AppUtility.showToast(HomeActivity.this, "please select valid pikup and destination address", Toast.LENGTH_SHORT);
        }

        System.out.println("pickupLatLong : " + pickupLatLong + "\n" + "destinationLatLong : " + destinationLatLong + "\n" + "pickupAddress :" + pickupAddress + "\n" + "trip_total : " + trip_total + "\n" + "vehicleId :" + vehicleId);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_CONFIRM_BOOKING,
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

                            RelativeLayout item_confirm_booking = (RelativeLayout) findViewById(R.id.layout_confirm);
                            item_confirm_booking.setVisibility(View.GONE);

                            if(message.equals("Driver Not Aavailable")){

                                NoDriverAlert();
                                // AppUtility.showToast(HomeActivity.this, message, Toast.LENGTH_SHORT);
                            }

                            else {
                                nearestDriversItemList.clear();
                                mMap.clear();

                                if (status.equalsIgnoreCase("1")) {
                                    bookingId = jsonObj.getString("bookingId");
                                    JSONArray jsonArray = jsonObj.getJSONArray("driver");

                                    if (jsonArray.length() == 0) {
                                        AppUtility.showToast(HomeActivity.this, message, Toast.LENGTH_SHORT);
                                    } else {

                                        for (int i = 0; i < jsonArray.length(); i++) {

                                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                                            NearestDriversBean item = new NearestDriversBean();
                                            //  item.setBookingId(bookingId);
                                            item.setUserId(jsonObject.getString("userId").toString().trim());
                                            item.setMobileNumber(jsonObject.getString("mobileNumber").toString().trim());
                                            item.setVihicleNumber(jsonObject.getString("vihicleNumber").toString().trim());
                                            item.setFullName(jsonObject.getString("fullName").toString().trim());

                                            JSONArray carFacilityJsonArray = jsonObject.getJSONArray("carFacility");

                                            if(carFacilityJsonArray != null ) {
                                                List<CarFacility> carFacilities = new ArrayList<>();
                                                for (int j = 0; j < carFacilityJsonArray.length(); j++) {
                                                    JSONObject jsonObjCarFacility = carFacilityJsonArray.getJSONObject(j);
                                                    CarFacility carFacility = new CarFacility();
                                                    carFacility.setFacilityId(jsonObjCarFacility.getString("facilityId").toString().trim());
                                                    carFacility.setFacilityName(jsonObjCarFacility.getString("facilityName").toString().trim());
                                                    carFacilities.add(carFacility);
                                                }
                                                item.setCarFacilities(carFacilities);
                                            }

                                            JSONObject vihicleTypeJsonObject = jsonObject.getJSONObject("vihicleType");
                                            item.setCarId(vihicleTypeJsonObject.getString("carId").toString().trim());
                                            item.setCarName(vihicleTypeJsonObject.getString("carName").toString().trim());

                                            JSONObject userDataJsonObj = jsonObject.getJSONObject("userData");
                                            item.setUserName(userDataJsonObj.getString("userName").toString().trim());
                                            item.setProfileImage(userDataJsonObj.getString("profileImage").toString().trim());

                                            item.setLatitude(jsonObject.getString("latitude").toString().trim());
                                            item.setLongitude(jsonObject.getString("longitude").toString().trim());
                                            item.setRating(jsonObject.getString("rating").toString().trim());
                                            item.setDistance(jsonObject.getString("distance").toString().trim());
                                            nearestDriversItemList.add(item);
                                        }

                                        drawMarkers(nearestDriversItemList);

                                        RelativeLayout item_cardetails = (RelativeLayout) findViewById(R.id.layout_carTypeDetail);
                                        item_cardetails.setVisibility(View.GONE);
                                        AppUtility.showToast(HomeActivity.this, message, Toast.LENGTH_SHORT);
                                    }
                                } else if (status.equals("0")) {
                                    IsDriverFound =true;
                                    AppUtility.showToast(HomeActivity.this, message, Toast.LENGTH_SHORT);

                                }else if (response.isEmpty()) {
                                    AppUtility.showToast(HomeActivity.this, message, Toast.LENGTH_SHORT);
                                    //   AppUtility.showAlertDialog_SingleButton_finishActivity(HomeActivity.this, message, "", "Ok");
                                } else if (message.equalsIgnoreCase("Now you can select a driver")){
                                    IsDriverFound =true;
                                    AppUtility.showToast(HomeActivity.this, getString(R.string.text_now_select__driver), Toast.LENGTH_SHORT);

                                }else {
                                    AppUtility.showToast(HomeActivity.this, message, Toast.LENGTH_SHORT);

                                }
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
                        Toast.makeText(HomeActivity.this, getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                // SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd");
                //  String todayDate = writeFormat.format(date);
                Map<String, String> header = new HashMap<>();
                String passCounter = String.valueOf(counter);

                header.put("pickupLatLong", pickupLatLong);
                header.put("destinationLatLong", destinationLatLong);
                header.put("pickupAddress", pickupAddress);
                header.put("destinationAddress", destinationAddress);
                header.put("tripTotal", trip_total);
                header.put("rideType", tripType);
                header.put("rideStartDate", tripdate);
                if (tripLater==true){
                    header.put("rideStartTime", tripTime);
                }else {
                    header.put("rideStartTime", "");
                }

                header.put("passengerCount", passCounter);
                header.put("distance", paramDistance);
                header.put("paymentType", sPaymentType);

                if(!vehicleId.equals(""))
                {
                    header.put("vehichleId", vehicleId);
                }else {
                    AppUtility.showToast(HomeActivity.this,"please select vehicle type.",0);
                }

                return header;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                    System.out.println("saved authTokan in comfirm booking := " + sessionManager.getAuthToken());
                }else {
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(HomeActivity.this,"Already logged in on another device","","Ok");
                    //Toast.makeText(HomeActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/

    public void GetDriverAction() {

        customDialog = new CustomDialog(HomeActivity.this);
        //  customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_GET_DRIVER_ACTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // customDialog.cancel();
                        System.out.println("#" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                CurrentMapFragment.createView =0;
                                PassengerCancelBookingsFragment.createCancelView =0;
                                stoptimertask();
                                Timmer.cancel();
                                AppUtility.showToast(HomeActivity.this, "Your request has been accepted.", 1);
                                dialogTimmer.dismiss();
                                Intent i = new Intent(HomeActivity.this, PassengerMyBookingsActivity.class);
                                //  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(i);
                                overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                            }
                            else if (status.equalsIgnoreCase("2")) {
                                //  showTimmerDialog();
                                home_iv_vehicletype.setEnabled(true);
                                stoptimertask();
                                AppUtility.showAlertDialog_SingleButton(HomeActivity.this, "Your Request has been Rejected, Please Select another driver !", "", "Ok");
                                dialogTimmer.dismiss();
                                Timmer.cancel();
                            }
                            else if (status.equalsIgnoreCase("0")) {
                                home_iv_vehicletype.setEnabled(true);
                                //  AppUtility.showAlertDialog_SingleButton(HomeActivity.this, "Driver is not active at this time, Please Select another driver !", "", "Ok");
                                // dialogTimmer.dismiss();
                                // Timmer.cancel();
                            }
                            else if (message.equalsIgnoreCase("Invalid Auth Token"))
                            {
                                sessionManager.logout();
                                AppUtility.showAlertDialog_SingleButton(HomeActivity.this,getString(R.string.text_something_went_wrong),"",getString(R.string.text_btn_ok));
                            }
                            else if(status.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(HomeActivity.this,"Error occured, please try again !","","Ok");
                            }

                            else {
                                AppUtility.showToast(HomeActivity.this, message, 1);
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
                        Toast.makeText(HomeActivity.this, getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                // SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd");
                //  String todayDate = writeFormat.format(date);
                Map<String, String> header = new HashMap<>();
                header.put("bookingId", bookingId);
                header.put("driverId", driverId);

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
                    AppUtility.showAlertDialog_SingleButton_finishActivity(HomeActivity.this,"Already logged in on another device","","Ok");
                    //Toast.makeText(HomeActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/ /****************************************************************************************************************************************/

    public void SendRequetToDriver() {

        customDialog = new CustomDialog(HomeActivity.this);
        customDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_SEND_DRIVER_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("#" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                showTimmerDialog();
                            } else if(message.equals("Request already Send"))
                            {
                                AppUtility.showAlertDialog_SingleButton(HomeActivity.this,"Request already Send to this driver, select another driver if available!","","Ok");

                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(HomeActivity.this,"Error occured, please try again later !","","Ok");
                            }
                            else {
                                AppUtility.showToast(HomeActivity.this, message, 1);
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
                        Toast.makeText(HomeActivity.this, getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("bookingId", bookingId);
                header.put("driverId", driverId);

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
                    AppUtility.showAlertDialog_SingleButton_finishActivity(HomeActivity.this,"Already logged in on another device","","Ok");
                    //Toast.makeText(HomeActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/

    public void GetPassengerUpcomingTripInfoTask() {
        customDialog = new CustomDialog(HomeActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_PASSENGER_GET_TRIP_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("HomeActivity response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                JSONArray jsonArray = jsonObj.getJSONArray("Request");
                                if(jsonArray != null ) {
                                    homePassUpcomingTripArraylist.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        AllTrips item = new AllTrips();
                                        item.setBookingId(jsonObject.getString("bookingId").toString().trim());
                                        item.setRideType(jsonObject.getString("rideType").toString().trim());
                                        item.setRideStatus(jsonObject.getString("rideStatus").toString().trim());
                                        item.setPickupLatLong(jsonObject.getString("pickupLatLong").toString().trim());
                                        item.setDestinationAddress(jsonObject.getString("destinationAddress").toString().trim());
                                        item.setDestinationLatLong(jsonObject.getString("destinationLatLong").toString().trim());
                                        item.setPickupAddress(jsonObject.getString("pickupAddress").toString().trim());
                                        item.setTripTotal(jsonObject.getString("tripTotal").toString().trim());
                                        item.setDistance(jsonObject.getString("distance").toString().trim());
                                        item.setRideStartDate(jsonObject.getString("rideStartDate").toString().trim());
                                        item.setRideStartTime(jsonObject.getString("rideStartTime").toString().trim());

                                        JSONObject driverJsonObject = jsonObject.getJSONObject("driver");
                                        JSONObject driverDetailsJsonObject = driverJsonObject.getJSONObject("driverDetail");
                                        item.setFullName(driverDetailsJsonObject.getString("fullName").toString().trim());
                                        item.setMobileNumber(driverDetailsJsonObject.getString("mobileNumber").toString().trim());

                                        homePassUpcomingTripArraylist.add(item);
                                    }
                                }else {
                                    //   AppUtility.showAlertDialog_SingleButton(getActivity(),"No result found","","Ok");
                                }
                            } else if(message.equals("No results found right now"))
                            {

                            }
                            else if(message.equals("Not Implemented"))
                            {
                                AppUtility.showAlertDialog_SingleButton(HomeActivity.this,"Error occured, please try again later!","","Ok");
                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(HomeActivity.this,"server error occured, please try again later!","","Ok");
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(HomeActivity.this,message,"","Ok");
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
                        Toast.makeText(HomeActivity.this, getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
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
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/


    private void drawMarkers(final List<NearestDriversBean> nearestDriversItemList) {
        LatLng point = null;
        mMap.clear();
        double driverLat = 0.0,driverLng = 0.0;

        if(nearestDriversItemList==null) {
            return;
        }
        if(nearestDriversItemList.size()>0) {


            for (int i = 0; i < nearestDriversItemList.size(); i++)
            {

                point = new LatLng(Double.parseDouble(nearestDriversItemList.get(i).getLatitude().toString()), Double.parseDouble(nearestDriversItemList.get(i).getLongitude().toString()));
                // Creating an instance of MarkerOptions
                MarkerOptions markerOptions = new MarkerOptions();
                // Setting latitude and longitude for the marker
                markerOptions.position(point);
                markerOptions.title(nearestDriversItemList.get(i).getUserName());
                markerOptions.snippet("" + nearestDriversItemList.get(i).getCarName());
                // Adding marker on the Google
                BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.driver_marker);
                // mMap.addMarker(markerOptions);
                driversMarker =  mMap.addMarker(markerOptions.position(point).title(nearestDriversItemList.get(i).getFullName()));
                driversMarker.setIcon(markerIcon);
                driversMarker.setTag(nearestDriversItemList.get(i).getUserId());
                dropPinEffect(driversMarker);
                driversMarker.showInfoWindow();

            }
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, 10);
        mMap.animateCamera(cameraUpdate); // nearestDriversItemList.get(0).getLatitude(), nearestDriversItemList.get(0).getLongitude()
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            int pos,posision2;
            String facility="";
            @Override
            public boolean onMarkerClick(Marker marker) {
                NearestDriversBean driversBean = new NearestDriversBean();
                //showDriverRequestDialog();
                final Dialog dialogDriverReq;
                dialogDriverReq = new Dialog(HomeActivity.this, android.R.style.Theme_Light);
                dialogDriverReq.setCanceledOnTouchOutside(true);
                dialogDriverReq.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogDriverReq.setContentView(R.layout.dialog_request_driver);
                dialogDriverReq.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Window window = dialogDriverReq.getWindow();
                window.setGravity(Gravity.CENTER);

                String markerId = String.valueOf(marker.getTag());

                for (int i = 0; i < nearestDriversItemList.size(); i++) {
                    if (markerId.equals(nearestDriversItemList.get(i).getUserId())) {
                        pos = i;
                    }
                }
                Button btn_submit_request = (Button) dialogDriverReq.findViewById(R.id.btn_submit_request);
                Button btn_cancel_request = (Button) dialogDriverReq.findViewById(R.id.btn_cancel_request);
                ImageView iv_VI_profilePic = (ImageView) dialogDriverReq.findViewById(R.id.iv_VI_profilePic);
                TextView tv_name = (TextView) dialogDriverReq.findViewById(R.id.tv_name_req);
                TextView tv_carType = (TextView) dialogDriverReq.findViewById(R.id.tv_carType_req);
                TextView tv_carFacility_name = (TextView) dialogDriverReq.findViewById(R.id.tv_carFacility_name);
                TextView tv_driver_rating = (TextView) dialogDriverReq.findViewById(R.id.tv_driver_rating);
                // TextView  tvUserName = (TextView) dialogDriverReq.findViewById(R.id.tvUserName);
                driverProfileImage = nearestDriversItemList.get(pos).getProfileImage();
                Glide.with(HomeActivity.this).load(driverProfileImage).placeholder(R.drawable.person).transform(new CircleTransform(HomeActivity.this)).override(100, 100).into(iv_VI_profilePic);
                driverId = nearestDriversItemList.get(pos).getUserId();
                tv_name.setText(nearestDriversItemList.get(pos).getFullName());
                tv_carType.setText(nearestDriversItemList.get(pos).getCarName());
                tv_driver_rating.setText(nearestDriversItemList.get(pos).getRating());

                List<CarFacility> carFacility = new ArrayList<>();
                carFacility = nearestDriversItemList.get(pos).getCarFacilities();
                facility = "";
                for (int j = 0; j < carFacility.size(); j++){

                    if(facility.equals("")){

                        facility = carFacility.get(j).getFacilityName();

                    }else {

                        facility = facility + "," +carFacility.get(j).getFacilityName();
                    }

                    //driversBean.setSingleFacilitiesName(carFacility.get(j).getFacilityName());
                    //  facility = driversBean.getSingleFacilitiesName().toString();
                }
                if (!carFacility.isEmpty()){
                    tv_carFacility_name.setText(facility);
                }

                tv_driver_rating.setText("0");
                //tvUserName.setText(nearestDriversItemList.get(pos).getFullName());

                btn_submit_request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (AppUtility.isNetworkAvailable(HomeActivity.this)) {
                            SendRequetToDriver();
                            dialogDriverReq.dismiss();
                        }else {
                            AppUtility.showAlertDialog_SingleButton(HomeActivity.this, getString(R.string.network_not_available), "", "Ok");
                        }

                    }
                });
                btn_cancel_request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialogDriverReq.dismiss();

                    }
                });

                dialogDriverReq.show();

                return false;
            }
        })
        ;

    }

    /*-----------------------------------------------------------------------------------------------------*/

    private void dropPinEffect(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post again 15ms later.
                    handler.postDelayed(this, 14);
                } else {
                    marker.showInfoWindow();

                }
            }
        });
    }

    /*-----------------------------------------------------------------------------------------------------*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

/*-------------------------------------------------------------------------------------------------------*/

    public void showTimmerDialog(){

        dialogTimmer=new Dialog(HomeActivity.this,android.R.style.Theme_Light);
        dialogTimmer.setCanceledOnTouchOutside(false);
        dialogTimmer.setCancelable(false);
        dialogTimmer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTimmer.setContentView(R.layout.dialog_timmer);
        dialogTimmer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window=dialogTimmer.getWindow();
        window.setGravity(Gravity.CENTER);

        startTimer();

        final TextView  tvtimmer=(TextView)dialogTimmer.findViewById(R.id.tvtimmer);
        Timmer =  new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvtimmer.setText("" + millisUntilFinished / 1000 +"sec");

            }
            public void onFinish() {
                stoptimertask();
                tvtimmer.setText("Driver not available !");
                AppUtility.showToast(HomeActivity.this, "Driver is not active at this time, Please Select another driver !",0);
                home_iv_vehicletype.setEnabled(true);
                Timmer.cancel();
                // new  GetDriverAction().execute();
                dialogTimmer.dismiss();
            }

        }.start();

        dialogTimmer.show();

    }

    /*-------------------------------------------------------------------------------------------------------*/

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 3000, 10000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    /*-------------------------------------------------------------------------------------------------------*/

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        final String strDate = simpleDateFormat.format(calendar.getTime());
                        GetDriverAction();
                        //show the toast
                        int duration = Toast.LENGTH_SHORT;
                        // Toast toast = Toast.makeText(getApplicationContext(), strDate, duration);
                        //toast.show();
                    }
                });
            }
        };
    }

    /*-------------------------------------------------------------------------------------------------------*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                !getLocationOnScreen(tv_destn_hd).contains(x, y)) {
            InputMethodManager input = (InputMethodManager)
                    HomeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(tv_destn_hd.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    protected Rect getLocationOnScreen(TextView mEditText) {
        Rect mRect = new Rect();
        int[] location = new int[2];

        mEditText.getLocationOnScreen(location);

        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mEditText.getWidth();
        mRect.bottom = location[1] + mEditText.getHeight();

        return mRect;
    }
    /*-------------------------------------------------------------------------------------------------------*/
    public void NoDriverAlert(){
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.no_driver_found_alert);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        TextView CartypeAnother = (TextView)dialog.findViewById(R.id.car_type_btn);
        TextView RideAnother = (TextView)dialog.findViewById(R.id.trip_again_btn);
        TextView CancelDialog = (TextView)dialog.findViewById(R.id.cancel_btn);

        CancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        RideAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_destn_hd.setEnabled(true) ;
                tv_pickup_hd.setEnabled(true);
                iv_editPickup.setEnabled(true);
                iv_editDestn.setEnabled(true);
                dialog.cancel();
                Intent i = new Intent(HomeActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(i);
                overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                finish();
            }
        });

        CartypeAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                // home_iv_vehicletype.setEnabled(true);
                RelativeLayout frame_vehicleLayout = (RelativeLayout) findViewById(R.id.vehicletype_options_list_view);
                frame_vehicleLayout.setVisibility(View.VISIBLE);
                //  home_tv_vehichle_type.setTextColor(getResources().getColor(R.color.white));
                //  home_iv_vehicletype.setImageResource(R.drawable.vehicle);
            }
        });
        dialog.show();
    }
    /*-------------------------------------------------------------------------------------------------------*/
    public void refreshBooking(){
        final RelativeLayout item_confirm_booking = (RelativeLayout) findViewById(R.id.layout_confirm);
        item_confirm_booking.setVisibility(View.VISIBLE);
        TextView title = (TextView) findViewById(R.id.tv_msg_confirm);
        title.setText(getString(R.string.text_doyou_want_create_new_trip));

        Button btn_confirm_yes = (Button) findViewById(R.id.btn_confirm_yes);
        Button btn_confirm_no = (Button) findViewById(R.id.btn_confirm_no);

        btn_confirm_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_destn_hd.setEnabled(true) ;
                tv_pickup_hd.setEnabled(true);
                iv_editPickup.setEnabled(true);
                iv_editDestn.setEnabled(true);
                item_confirm_booking.setVisibility(View.GONE);
                Intent i = new Intent(HomeActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                startActivity(i);
                finish();
            }
        });
        btn_confirm_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item_confirm_booking.setVisibility(View.GONE);
            }
        });
    }

    /************************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReceiverPositioningAlarm.stopLocationListener();
        stopService(new Intent(HomeActivity.this, GpsService.class));
    }
}