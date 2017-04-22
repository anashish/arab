package com.mishwar.passenger_fragments;

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
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.passanger_ui.PassengerMyBookingsActivity;
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
public class CurrentMapFragment extends Fragment implements com.google.android.gms.maps.GoogleMap.OnCameraChangeListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,LocationListener,View.OnClickListener {

    public static final String TAG = "PassCurrent";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1234;
    private GoogleMap pMap;
    private LatLng currentLatLng,pickupLatLng,destiLatLng;
    private GoogleApiClient GoogleApiClient;
    public static int createView;
    View rootView;
    private boolean gps_enabled = false,network_enabled = false, platlng = true,isCancelTripButtonClicked = false,isFregVisible = false;
    private String rideStatusValue,bookingId="",reasonForCancel="",rideType="",pickupAddress="",destinationAddress,sPickupLatLong,sDestinationLatLong,rideStartDate
            ,rideStartTime,fullName,mobileNumber="", dist;
    private  double dPickupLat,dPickupLong,dDstinationLat,dDstinationLong,tripCost;
    private SessionManager sessionManager ;
    private Marker passengerMarker,driverMarker,movingDriverMarker;
    Button btnPassengerTripInfo,btnPassengerCancelTrip;
    private Dialog dialogCancelRide;
    CustomDialog customDialog;
    private Timer timer;
    private TimerTask timerTask;;
    final Handler handler = new Handler();
    private  RequestQueue requestQueue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
            //view =
            rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_current_map, container, false);

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        // Inflate the layout for this fragment
        if(createView == 0){

            if (AppUtility.isNetworkAvailable(getActivity()))
            {
                GetPassengerCurrentTripInfoTask();
            }
            else {
                AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
            }

            PassengerCancelBookingsFragment.createCancelView = 0;
            PassengerPreviousTripFragment.createPreviousTripView = 0;
            PassengerUpcomingTripFragment.createUpcomingTripView = 0;

            btnPassengerTripInfo = (Button) rootView.findViewById(R.id.btnPassengerTripInfo);
            btnPassengerCancelTrip = (Button) rootView.findViewById(R.id.btnPassengerCancelTrip);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }

            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.myBookingMap);
            mapFragment.getMapAsync(this);
            btnPassengerTripInfo.setOnClickListener(this);
            btnPassengerCancelTrip.setOnClickListener(this);

            createView = 1;
        }
        return rootView;
    }

    /*----------------------------------------------------------------------------------------------------------------------------*/
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

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
    /*---------------------------------------------------------------------------------------------------------*/
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnPassengerTripInfo:
                if (!bookingId.equals(""))
                {
                    passengerTripInfoDialog();

                }else {
                    Toast.makeText(getActivity(), getString(R.string.text_currently_no_ride), Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btnPassengerCancelTrip:

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage(getString(R.string.text_alert_sure_cancel_ride));
                builder1.setCancelable(true);
                builder1.setPositiveButton(
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
                                                btnPassengerCancelTrip.setVisibility(View.GONE);
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
                        getString(R.string.text_btn_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                break;
        }
    }

    /*----------------------------------------------------------------------------------------------------------------------*/
    private void passengerTripInfoDialog() {

        final Dialog dialogCurrentRunningRide = new Dialog(getActivity(), android.R.style.Theme_Light);
        dialogCurrentRunningRide.setCanceledOnTouchOutside(true);
        dialogCurrentRunningRide.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCurrentRunningRide.setContentView(R.layout.dailog_current_running_ride_info);
        dialogCurrentRunningRide.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialogCurrentRunningRide.getWindow();
        window.setGravity(Gravity.CENTER);

        //  Button btnTrack = (Button) dialogCurrentRunningRide.findViewById(R.id.btnTrack);
        Button cancel_btn = (Button) dialogCurrentRunningRide.findViewById(R.id.cancel_btn);
        Button passenger_call_btn = (Button) dialogCurrentRunningRide.findViewById(R.id.passenger_call_btn);
        TextView tvCurrentDriverName = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvCurrentDriverName);
        TextView tvCurrentBookingId = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvCurrentBookingId);
        TextView tvCurrentToAddress = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvCurrentToAddress);
        TextView tvCurrentFromAddress = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvCurrentFromAddress);
        TextView tvCurrentDetails = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvCurrentDetails);
        TextView tvstatus = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvstatus);
        TextView tvdistance = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvdistance);
        TextView tvcost = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvcost);
        TextView tvRideType = (TextView) dialogCurrentRunningRide.findViewById(R.id.tvRideType);

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
        tvCurrentDriverName.setText(fullName);
        tvCurrentBookingId.setText(bookingId);
        tvCurrentToAddress.setText(destinationAddress);
        tvCurrentFromAddress.setText(pickupAddress);
        tvCurrentDetails.setText(Details);

        if (rideType.equals("TN")){
            tvRideType.setText("Trip now");
        }else  if (rideType.equals("TL")){
            tvRideType.setText("Trip later");
        }

        tvdistance.setText(dist+" Km");
        tvcost.setText("$ "+String.format("%.2f", tripCost));

        if(rideStatusValue.equals("2")){
            tvstatus.setText("Not start");
        }
        if(rideStatusValue.equals("3")){
            tvstatus.setText("Trip start");
        }
        if(rideStatusValue.equals("6")){
            tvstatus.setText("Driver on the way");
        }

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCurrentRunningRide.dismiss();
            }
        });
        passenger_call_btn.setOnClickListener(new View.OnClickListener() {
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

    /*----------------------------------------------------------------------------------------------------------------*/

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

                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
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

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
    /*-------------------------------------------------------------------------------------------------------------------*/

    public  void setCurrent(LatLng picmarker,String locType,String infoPickAddress){
        //  if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))
        BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_current_loc);
        pMap.addMarker(new MarkerOptions().position(picmarker).title("Your Current location")).setIcon(pickupIcon);

        pMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picmarker, 12));

    }
    /*-------------------------------------------------------------------------------------------------------------------*/

    public  void setPickUp(LatLng picmarker,String locType,String infoPickAddress){
        //  if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))

        BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.passenger_marker);
        passengerMarker = pMap.addMarker(new MarkerOptions().position(picmarker).title("pickup : "+infoPickAddress));
        passengerMarker.setIcon(pickupIcon);
    }

    public  void setPickUpUpdate(LatLng picmarker,String locType,String infoPickAddress){
        //  if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))

        BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.passenger_marker);
        passengerMarker = pMap.addMarker(new MarkerOptions().position(picmarker).title("pickup : "+infoPickAddress));
        passengerMarker.setIcon(pickupIcon);


    }
    /*-------------------------------------------------------------------------------------------------------------------*/
    public  void setDestination(LatLng marker,String locType,String infoAddress){
        if (AppUtility.isNetworkAvailable(getActivity()))
        {
            BitmapDescriptor destiIcon = BitmapDescriptorFactory.fromResource(R.drawable.driver_marker);
            driverMarker = pMap.addMarker(new MarkerOptions().position(marker).title("destination : "+infoAddress));
            driverMarker.setIcon(destiIcon);
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(), getString(R.string.network_not_available), "", "Ok");
        }
    }

    public  void setDriverChangedLocationUpdate(LatLng marker,String infoPickAddress){
        //  if (AppUtility.isNetworkAvailable(DriverHomeActivity.this))
        //   pMap.clear();
        BitmapDescriptor pickupIcon = BitmapDescriptorFactory.fromResource(R.drawable.car_icon);
        movingDriverMarker = pMap.addMarker(new MarkerOptions().position(marker).title(" "+infoPickAddress));
        movingDriverMarker.setIcon(pickupIcon);

    }
    /*---------------------------------------------------------------------------------------------------------------------------------*/

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
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
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
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(getResources().getColor(R.color.colorPrimary));

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

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

    /****************************************************************************************************************************************/ /****************************************************************************************************************************************/

    public void GetPassengerCurrentTripInfoTask() {

        customDialog = new CustomDialog(getActivity());
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_PASSENGER_GET_TRIP_INFO,
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

                                JSONObject requestObj = jsonObj.getJSONObject("Request");
                                JSONObject driverJsonObj = requestObj.getJSONObject("driver");
                                //  JSONObject distanceJsonObj = requestObj.getJSONObject("distance");
                                JSONObject driverDetailJsonObj = driverJsonObj.getJSONObject("driverDetail");

                                btnPassengerCancelTrip.setVisibility(View.VISIBLE);

                                rideStatusValue =  requestObj.getString("rideStatus");
                                bookingId = requestObj.getString("bookingId");
                                rideType = requestObj.getString("rideType");
                                pickupAddress = requestObj.getString("pickupAddress");
                                destinationAddress = requestObj.getString("destinationAddress");
                                sPickupLatLong = (requestObj.getString("pickupLatLong"));
                                sDestinationLatLong = (requestObj.getString("destinationLatLong"));
                                rideStartDate = requestObj.getString("rideStartDate");
                                rideStartTime = requestObj.getString("rideStartTime");
                                fullName = driverDetailJsonObj.getString("fullName");
                                mobileNumber = driverDetailJsonObj.getString("mobileNumber");
                                dist = requestObj.getString("distance");
                                tripCost= Double.parseDouble(requestObj.getString("tripTotal"));

                                if(rideStatusValue.equals("2")){
                                    btnPassengerTripInfo.setVisibility(View.VISIBLE);
                                    sessionManager.setBookingId(bookingId);
                                    sessionManager.setTripDate(rideStartDate);
                                    sessionManager.setTripType(rideType);

                                    // btnPassengerCancelTrip.setVisibility(View.GONE);
                                }
                                if(rideStatusValue.equals("3")){
                                    //  btnPassengerTripInfo.setVisibility(View.GONE);
                                    btnPassengerCancelTrip.setVisibility(View.GONE);
                                    stoptimertask();

                                }
                                if(rideStatusValue.equals("6")){
                                    btnPassengerTripInfo.setVisibility(View.VISIBLE);

                                    if (isCancelTripButtonClicked==false){
                                        if (isFregVisible==true)
                                        {
                                            startTimerToCallGetLocation();

                                        }/*else {
                                            AppUtility.showAlertDialog_SingleButton(getActivity(),"stay on this screen if you want to track driver","","Ok");
                                        }*/
                                    }
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

                                if (pickupLatLng!=null && destiLatLng!=null){
                                    setPickUp(pickupLatLng, "pickup", pickupAddress);
                                    setDestination(destiLatLng, "destination", destinationAddress);
                                    // Adding all the points in the route to LineOptions
                                    // Getting URL to the Google Directions API
                                    String url = getDirectionsUrl(pickupLatLng, destiLatLng);
                                    FetchUrl FetchUrl = new FetchUrl();
                                    // Start downloading json data from Google Directions API
                                    FetchUrl.execute(url);

                                }

                            }else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"server Error occured, please try again !","","Ok");
                            }
                            else {
                                // btnPassengerTripInfo.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), getString(R.string.text_currently_no_ride), Toast.LENGTH_LONG).show();
                                sessionManager.setBookingId("");
                                sessionManager.setTripDate("");
                                sessionManager.setTripType("");
                                //   AppUtility.showAlertDialog_SingleButton(getActivity(),"Currently no ride found","","Ok");
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
                header.put("dataStatus", "1");

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
// method for cancel ride from server
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
                                isCancelTripButtonClicked = true;
                                AppUtility.showToast(getActivity(), message, 1);
                                btnPassengerCancelTrip.setVisibility(View.GONE);
                                dialogCancelRide.dismiss();
                                // stop handler for calling asynchtsk of getting update location
                                stoptimertask();
                                sessionManager.setBookingId("");
                                sessionManager.setTripDate("");
                                sessionManager.setTripType("");
                                // getVisibleFragment();


                                Intent intentend = new Intent(getActivity(), PassengerMyBookingsActivity.class);
                                CurrentMapFragment.createView = 0;
                                PassengerUpcomingTripFragment.createUpcomingTripView = 0;
                                PassengerPreviousTripFragment.createPreviousTripView = 0;
                                PassengerCancelBookingsFragment.createCancelView = 0;
                                startActivity(intentend);
                                getActivity().finish();

                            }else if(message.equals("Now you can not cancel this ride"))
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),getString(R.string.text_can_not_cancel_ride),"",getString(R.string.text_btn_ok));

                            }else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),"server Error occured, please try again later !","","Ok");
                            }
                            else if(status.equalsIgnoreCase("0"))
                            {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),message,"","Ok");
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

        //customDialog = new CustomDialog(DriverHomeActivity.this);
        //customDialog.show();

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
                                //  LatLng  newPassengerLatLng = new LatLng(dPassengerLat,dPassengerLong);

                                System.out.println("passenger cur loc -:"+passengerLat+"\n"+" :"+passengerLng);

                                if (pickupLatLng!=null && destiLatLng!=null){
                                    setPickUpUpdate(pickupLatLng, "pickup", pickupAddress);
                                    setDestination(destiLatLng, "destination", destinationAddress);
                                    // Adding all the points in the route to LineOptions
                                    // Getting URL to the Google Directions API
                                    String url = getDirectionsUrl(pickupLatLng, destiLatLng);
                                    FetchUrl FetchUrl = new FetchUrl();
                                    // Start downloading json data from Google Directions API
                                    FetchUrl.execute(url);
                                    //move map camera
                                }
                                if (newDriverLatLng!=null){
                                    setDriverChangedLocationUpdate(newDriverLatLng,  "driver location");
                                    //move map camera

                                }

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
                       // Toast.makeText(getActivity(), "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
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

    public void startTimerToCallGetLocation() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 30000ms
        timer.schedule(timerTask, 1000, 10000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
            if (timerTask!=null){
                // pMap.clear();
            }
            timerTask.cancel();
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
                        if (isFregVisible == true){
                            if (getActivity() != null) {
                                // Code goes here.
                                if (AppUtility.isNetworkAvailable(getActivity()))
                                {
                                    GetLocationContinouslly();
                                }else {
                                    AppUtility.showAlertDialog_SingleButton(getActivity(),"Please on net connection to see pasenger location","","Ok");
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

}



