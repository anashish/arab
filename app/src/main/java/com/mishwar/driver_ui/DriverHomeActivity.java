package com.mishwar.driver_ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
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
import com.mishwar.R;
import com.mishwar.adapter.PlacesAutoCompleteAdapter;
import com.mishwar.commun_ui.SettingActivity;
import com.mishwar.helper.CircleTransform;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.passenger_fragments.CurrentMapFragment;
import com.mishwar.reciver.ReceiverPositioningAlarm;
import com.mishwar.service.GpsService;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DriverHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener,com.google.android.gms.maps.GoogleMap.OnCameraChangeListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,LocationListener {

    public static final String TAG = "Driver Home";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleMap map;
    private LatLng pickupLatLng;
    private GoogleApiClient GoogleApiClient;
    private TextView tvDriverName;
    private SessionManager sessionManager;
    NavigationView navigationView;
    DrawerLayout drawer;
    private View headerView;
    private ImageView iv_nav_forward,toggle,ivDrawerToggleBack,ivDriverProfileImage;
    TextView btnDriverOnlineStatus,btnDriverOfLineStatus;
    private LatLng DriverCurrentLatlng;
    private Marker pDestinationMarker;
    private boolean pDestinationlatlng = true,gps_enabled = false,network_enabled = false;
    private AutoCompleteTextView tv_address;
    private PlacesAutoCompleteAdapter mPlacesAdapter;
    private  String pDestinationAddress, sIdentityProofStatus,sLicenceNumberStatus,driverStatus = "";
    CustomDialog customDialog;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        sessionManager = new SessionManager(DriverHomeActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.driver_nav_view);
        initializeViews();

        navigationView.setNavigationItemSelectedListener(this);
        setMyActionbarForAllScreen(getString(R.string.text_home));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driverMap);
        mapFragment.getMapAsync(this);
    }

    /*-------------------------------------------------------------------------------------------------------------------*/

    private void initializeViews() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        headerView = navigationView.getHeaderView(0);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String notificationTitle = "";
            notificationTitle = extras.getString("notificationTitle");

        }

        if (sessionManager.getLicenceNumberStatus().toString().equals("0") && sessionManager.getIdentityProofStatus().toString().equals("0")){

            AppUtility.showToast(DriverHomeActivity.this,"Your licence and vehicle id proof are not approved yet !",1);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("1") && sessionManager.getLicenceNumberStatus().toString().equals("1"))
        {
            // AppUtility.showToast(DriverHomeActivity.this,"your Licence and Vehicle ID Proof have been Approved !",1);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("2") && sessionManager.getLicenceNumberStatus().toString().equals("2"))
        {
            AppUtility.showToast(DriverHomeActivity.this,"Your licence & vehicle id proof have been rejected ! Please upload valid licencde number !",1);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("2") && sessionManager.getLicenceNumberStatus().toString().equals("1"))
        {
            AppUtility.showToast(DriverHomeActivity.this,"Your vehicle id proof has been rejected ! Please upload valid vehicle id proof !",1);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("1") && sessionManager.getLicenceNumberStatus().toString().equals("2"))
        {
            AppUtility.showToast(DriverHomeActivity.this,"Your licence number has been rejected ! Please upload valid licencde number!",1);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("0") && sessionManager.getLicenceNumberStatus().toString().equals("1"))
        {
            AppUtility.showToast(DriverHomeActivity.this,"Your vehicle id proof is not approved yet !",1);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("1") && sessionManager.getLicenceNumberStatus().toString().equals("0"))
        {
            AppUtility.showToast(DriverHomeActivity.this,"Your licence number is not approved yet !",1);
        }

        btnDriverOnlineStatus = (TextView) headerView.findViewById(R.id.btnDriverOnlineStatus);
        btnDriverOfLineStatus = (TextView) headerView.findViewById(R.id.btnDriverOfLineStatus);

        ivDriverProfileImage = (ImageView)headerView.findViewById(R.id.ivDriverProfileImage);
        iv_nav_forward = (ImageView) headerView.findViewById(R.id.iv_nav_forward);


        tvDriverName = (TextView) headerView.findViewById(R.id.tvDriverName);
        tv_address = (AutoCompleteTextView)findViewById(R.id.tv_pickup_driver);
        // Glide.with(DriverHomeActivity.this).load(sessionManager.getProfileImage()).into(ivDriverProfileImage);
        Glide.with(this).load(sessionManager.getProfileImage()).placeholder(R.drawable.person).transform(new CircleTransform(this)).override(100, 100).into(ivDriverProfileImage);

        tvDriverName.setText(sessionManager.getFullName().toString());

        iv_nav_forward.setOnClickListener(this);
        btnDriverOnlineStatus.setOnClickListener(this);
        btnDriverOfLineStatus.setOnClickListener(this);

        if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))
        {
            mPlacesAdapter = new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1, GoogleApiClient, null, null);
            tv_address.setOnItemClickListener(mAutocompleteClickListenerDestiNation);
            tv_address.setAdapter(mPlacesAdapter);
        }else {
            checkLocationPermission();
        }
}

/*-------------------------------------------------------------------------------------------------------------------*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;
        Class fragmentClass = null;

        switch (item.getItemId()) {
            case R.id.driver_profile:
                //setMyActionbarForAllScreen("Home");
                startActivity(new Intent(this, DriverProfileActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                break;

            case R.id.driver_nav_vehicle_info:
                startActivity(new Intent(this, VehicleInfoActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                break;

            case R.id.nav_setting:
                startActivity(new Intent(this, SettingActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                break;

            case R.id.driver_nav_logout:
                if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))
                {
                    sessionManager.logout();
                    ReceiverPositioningAlarm.stopLocationListener();
                    stopService(new Intent(DriverHomeActivity.this, GpsService.class));
                    AppUtility.showToast(DriverHomeActivity.this, getString(R.string.text_logout_msg), 0);
                    finish();
                    overridePendingTransition(R.anim.slide_out, R.anim.slide_in);

                }
                else {
                    AppUtility.showAlertDialog_SingleButton(DriverHomeActivity.this, getString(R.string.network_not_available), "", "Ok");
                }

                break;

            case R.id.driver_my_booking:
                startActivity(new Intent(this, DriverMyBookingsActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                //  CallFregments(fragment,DriverMyBookingsActivity.class);
                //  setMyActionbarForAllScreen("Privious Trips");
                break;

            case R.id.driver_monthlly_booking:
                startActivity(new Intent(this, ShowMonthllyTripsActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                //  CallFregments(fragment,DriverMyBookingsActivity.class);
                //  setMyActionbarForAllScreen("Privious Trips");
                break;

            case R.id.driver_nav_payment:
                startActivity(new Intent(this, PaymentInfoActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                break;
            default:
                fragmentClass = CurrentMapFragment.class;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    /*Calling and replacing fregment classes*/
    public void CallFregments(Fragment fragment,Class fragmentClass){
        try {
            // Insert the fragment by replacing any existing fragment
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(DriverHomeActivity.this);
            builder1.setMessage(getString(R.string.text_exit_app));
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
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

/*-------------------------------------------------------------------------------------------------------------------*/

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    /*-------------------------------------------------------------------------------------------------------------------*/

    synchronized public void createGoogleApiClient() {
        if (GoogleApiClient == null) {
            GoogleApiClient = new GoogleApiClient.Builder(this)
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
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (GoogleApiClient == null) {
                            createGoogleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();

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
        if(location!=null) {
            DriverCurrentLatlng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        // call server api om location change to update location change on server
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LatLng current;
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location mylocation = LocationServices.FusedLocationApi.getLastLocation(GoogleApiClient);
                if(gps_enabled && network_enabled) {
                    if(mylocation!=null) {
                        current = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
                        DriverCurrentLatlng = current;
                        setPickUp(current, "current", null);

                    }
                }

                mPlacesAdapter = new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1, GoogleApiClient, null, null);
                tv_address.setOnItemClickListener(mAutocompleteClickListenerDestiNation);
                tv_address.setAdapter(mPlacesAdapter);
            }else {
                checkLocationPermission();
            }
        }
        else {
            AppUtility.showAlertDialog_SingleButton(DriverHomeActivity.this, getString(R.string.network_not_available), "", "Ok");
        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (DriverCurrentLatlng!=null){
            DriverCurrentLatlng = cameraPosition.target;
        }else {
            AppUtility.showToast(DriverHomeActivity.this,"Gps network not available ! please try again.",1);
        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        final UiSettings uiSettings = map.getUiSettings();
        // uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))
        {
            GetRefereshTask();

        }
        else {
            AppUtility.showAlertDialog_SingleButton(DriverHomeActivity.this, getString(R.string.network_not_available), "", "Ok");
        }
        //start backgrtout service for update latlng
        startService(new Intent(this, GpsService.class));

        if (pDestinationlatlng) {
            pickupLatLng = map.getCameraPosition().target;
        }else {
            DriverCurrentLatlng = map.getCameraPosition().target;
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                createGoogleApiClient();
                map.setMyLocationEnabled(true);
            }
        }
        else {
            createGoogleApiClient();
            map.setMyLocationEnabled(true);
        }
        map.setOnCameraChangeListener(this);

    }
/*-------------------------------------------------------------------------------------------------------------------*/

    public  void setPickUp(LatLng driverCurrentLatLng,String locType,String infoPickAddress){
        //  if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))
        //  {
        if(locType.equals("current")){
            //  mMap.clear();
            DriverCurrentLatlng = driverCurrentLatLng;
            System.out.println("current latlng :"+driverCurrentLatLng);
            BitmapDescriptor currentIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_current_loc);
            map.addMarker(new MarkerOptions().position(driverCurrentLatLng).title("Your Current location")).setIcon(currentIcon);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(driverCurrentLatLng, 15));

            //showParamsDialog();
            // UpdateDriverCurrentLatLngTask();

            Log.d("current loc", "latitude =" + driverCurrentLatLng.latitude + "longitude =" + driverCurrentLatLng.longitude);

        } else if(locType.equals("destination")){
            BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red);
            pDestinationMarker = map.addMarker(new MarkerOptions().position(driverCurrentLatLng).title(infoPickAddress));
            pDestinationMarker.setIcon(pickupIcon);

            DriverCurrentLatlng = driverCurrentLatLng;
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(driverCurrentLatLng, 15));
        //  }
       /* else {
            AppUtility.showAlertDialog_SingleButton(DriverHomeActivity.this, getString(R.string.network_not_available), "", "Ok");
        }*/
    }

    private void showParamsDialog() {
        final Dialog  dialogCurrentRunningRide = new Dialog(DriverHomeActivity.this, android.R.style.Theme_Light);
        dialogCurrentRunningRide.setCanceledOnTouchOutside(false);
        dialogCurrentRunningRide.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCurrentRunningRide.setContentView(R.layout.dialog_show_data);
        dialogCurrentRunningRide.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialogCurrentRunningRide.getWindow();
        window.setGravity(Gravity.CENTER);

        Button  btnSendDialogParam = (Button) dialogCurrentRunningRide.findViewById(R.id.btnSendDialogParam);

        TextView tvDialogShowParam = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvDialogShowParam);

        String AuthToken = sessionManager.getAuthToken().toString();

        String pLat = String.valueOf(DriverCurrentLatlng.latitude);
        String pLng = String.valueOf(DriverCurrentLatlng.longitude);
        String CurrentLatLong = pLat +","+pLng;

        tvDialogShowParam.setText("AuthToken : "+AuthToken+ "\n" + "LatLng : "+CurrentLatLong);

        btnSendDialogParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogCurrentRunningRide.dismiss();

                //UpdateDriverCurrentLatLngTask();


            }
        });


        dialogCurrentRunningRide.show();
    }


/*-------------------------------------------------------------------------------------------------------------------*/

    //* methods for adding custom action bar*//*
    public void setMyActionbarForAllScreen(String title)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        View actionBarView = getLayoutInflater().inflate(R.layout.actionbar_driver_layout, null);
        TextView titleTV = (TextView) actionBarView.findViewById(R.id.TvDriverStatus);

        final ImageView ivDriverNotification = (ImageView) actionBarView.findViewById(R.id.ivDriverNotification);
        toggle = (ImageView) actionBarView.findViewById(R.id.ivDrawerToggle);
        //  ivDrawerToggleBack = (ImageView) actionBarView.findViewById(R.id.ivDrawerToggleBack);

        titleTV.setText(title);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                    // Glide.with(DriverHomeActivity.this).load(R.drawable.icon_menu).into(toggle);
                    //  toggle.setVisibility(View.VISIBLE);
                    //  ivDrawerToggleBack.setVisibility(View.GONE);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                    //   Glide.with(DriverHomeActivity.this).load(R.drawable.icon_back).into(toggle);
                    //  toggle.setVisibility(View.GONE);
                    //  ivDrawerToggleBack.setVisibility(View.VISIBLE);
                }
            }
        });
        ivDriverNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestDialog();
                String notify = "1";
                Intent intent = new Intent(DriverHomeActivity.this,NotificationReqActivity.class);
                intent.putExtra("HomeNotification", notify);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        actionBar.setCustomView(actionBarView);
        Toolbar parent =(Toolbar) actionBarView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

    }
/*-------------------------------------------------------------------------------------------------------------------*/

    /*Listner for Places API*/
    private AdapterView.OnItemClickListener mAutocompleteClickListenerDestiNation = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mPlacesAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(GoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackDestiNation);
        }
    };
/*-------------------------------------------------------------------------------------------------------------------*/

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackDestiNation = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            map.clear();

            if (!places.getStatus().isSuccess()) {
                Log.e("place", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            pickupLatLng = place.getLatLng();
            String geoAdrress = (String) place.getAddress().toString();
            pDestinationAddress = tv_address.getText().toString().trim();
            setPickUp(pickupLatLng, "destination", pDestinationAddress);

        }
    };
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {

            /*Action perform for tripmothly*/
            case R.id.iv_nav_forward:

                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.btnDriverOfLineStatus:
                driverStatus = "2";

                if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))
                {
                    btnDriverOfLineStatus.setBackgroundResource(R.drawable.offline_btn_active_new);
                    btnDriverOnlineStatus.setBackgroundResource(R.drawable.online_btn_inactive_new);
                    btnDriverOfLineStatus.setText(getString(R.string.text_inactive));
                    btnDriverOnlineStatus.setText(getString(R.string.text_active));
                    btnDriverOnlineStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btnDriverOfLineStatus.setTextColor(getResources().getColor(R.color.white));

                    DriverOnlineOffline();

                }
                else {
                    AppUtility.showAlertDialog_SingleButton(DriverHomeActivity.this, getString(R.string.network_not_available), "", "Ok");
                }
                break;

            case R.id.btnDriverOnlineStatus:
                driverStatus = "1";

                if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))
                {
                    btnDriverOfLineStatus.setBackgroundResource(R.drawable.offline_btn_inactive_new);
                    btnDriverOnlineStatus.setBackgroundResource(R.drawable.online_btn_active_new);
                    btnDriverOfLineStatus.setText(getString(R.string.text_inactive));
                    btnDriverOnlineStatus.setText(getString(R.string.text_active));
                    btnDriverOnlineStatus.setTextColor(getResources().getColor(R.color.white));
                    btnDriverOfLineStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                    DriverOnlineOffline();
                }
                else {
                    AppUtility.showAlertDialog_SingleButton(DriverHomeActivity.this, getString(R.string.network_not_available), "", "Ok");
                }
                break;
        }
    }

    /****************************************************************************************************************************************/

    public void LogOutTask() {

        customDialog = new CustomDialog(DriverHomeActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_LOGOUT,
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
                                sessionManager.logout();
                                ReceiverPositioningAlarm.stopLocationListener();
                                stopService(new Intent(DriverHomeActivity.this, GpsService.class));
                                AppUtility.showToast(DriverHomeActivity.this, message, 1);
                                overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
                                finish();

                            }else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(DriverHomeActivity.this,"Error occured, please try again !","","Ok");
                            }
                            else if(status.equalsIgnoreCase("0")){
                                AppUtility.showToast(DriverHomeActivity.this, message, 1);
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
                        Toast.makeText(DriverHomeActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            /*@Override
            public Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> header = new HashMap<>();

                header.put("driverId", "");

                return header;
            }*/
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

    public void DriverOnlineOffline() {

        customDialog = new CustomDialog(DriverHomeActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_DRIVER_ONLINE_OFFLINE_STATUS,
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
                                AppUtility.showToast(DriverHomeActivity.this, message, 1);

                            }else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(DriverHomeActivity.this,"Error occured, please try again !","","Ok");
                            }

                            else {
                                AppUtility.showToast(DriverHomeActivity.this, message, 1);
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
                        Toast.makeText(DriverHomeActivity.this, "Something went wrong, please try after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("driverStatus",driverStatus);
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
    public void GetRefereshTask() {

        //customDialog = new CustomDialog(DriverHomeActivity.this);
        //customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_REFERESH_DRIVER_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   customDialog.cancel();
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");
                            sIdentityProofStatus = jsonObj.getString("identityProofStatus");
                            sLicenceNumberStatus = jsonObj.getString("licenceNumberStatus");

                            if (status.equalsIgnoreCase("1")) {
                                //  AppUtility.showToast(DriverHomeActivity.this, message, 1);

                                sessionManager.setIdentityProofStatus(sIdentityProofStatus);
                                sessionManager.setLicenceNumberStatus(sLicenceNumberStatus);

                            }
                            else
                            {
                                AppUtility.showToast(DriverHomeActivity.this, message, 1);

                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(DriverHomeActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            /* @Override
             public Map<String, String> getParams() throws AuthFailureError {
                 Map<String, String> header = new HashMap<>();
                 header.put("driverStatus",driverStatus);
                 return header;
             }*/
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

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(DriverHomeActivity.this, GpsService.class));
    }

   /*--------------------------------------------------------------------------------------------------------*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                !getLocationOnScreen(tv_address).contains(x, y)) {
            InputMethodManager input = (InputMethodManager)
                    DriverHomeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(tv_address.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    protected Rect getLocationOnScreen(EditText mEditText) {
        Rect mRect = new Rect();
        int[] location = new int[2];

        mEditText.getLocationOnScreen(location);

        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mEditText.getWidth();
        mRect.bottom = location[1] + mEditText.getHeight();

        return mRect;
    }
}

