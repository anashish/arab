package com.mishwar.reciver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.mishwar.helper.Constant;
import com.mishwar.listner.OnNewLocationListener;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mindiii on 30/8/16.
 */
public class ReceiverPositioningAlarm extends BroadcastReceiver {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 91;
    public static final String COMMAND = "SENDER";
    public static final int SENDER_ACT_DOCUMENT = 0;
    public static final int SENDER_SRV_POSITIONING = 1;
    public static final int MIN_TIME_REQUEST = 2 * 1000;
    public static final String ACTION_REFRESH_SCHEDULE_ALARM =
            "com.mishwar.ACTION_REFRESH_SCHEDULE_ALARM";
    private static Location currentLocation;
    private static Location prevLocation;
    private static Context _context;
    private String provider = LocationManager.GPS_PROVIDER;
    private static Intent _intent;
    private static LocationManager locationManager;
    private boolean canGetLocation;
    private static SessionManager sessionManager;

    private static LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            try {
                String strStatus = "";
                switch (status) {
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        strStatus = "GPS_EVENT_FIRST_FIX";
                        break;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        strStatus = "GPS_EVENT_SATELLITE_STATUS";
                        break;
                    case GpsStatus.GPS_EVENT_STARTED:
                        strStatus = "GPS_EVENT_STARTED";
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        strStatus = "GPS_EVENT_STOPPED";
                        break;
                    default:
                        strStatus = String.valueOf(status);
                        break;
                }
                Toast.makeText(_context, "Status: " + strStatus,
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            try {
               /* Toast.makeText(_context, "***new location***",
                        Toast.LENGTH_SHORT).show();*/
                gotLocation(location);
            } catch (Exception e) {
            }
        }
    };

    // received request from the calling service
    @Override
    public void onReceive(final Context context, Intent intent) {
      /*  Toast.makeText(context, "new request received by receiver",
                Toast.LENGTH_SHORT).show();*/
        sessionManager = new SessionManager(context);

        _context = context;
        _intent = intent;
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(provider)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(provider,
                    MIN_TIME_REQUEST, 5, locationListener);
            Location gotLoc = locationManager
                    .getLastKnownLocation(provider);
            gotLocation(getLocation(context));
        } else {
          /*  Toast t = Toast.makeText(context, "please turn on GPS",
                    Toast.LENGTH_LONG);*/
            //  t.setGravity(Gravity.CENTER, 0, 0);
            // t.show();
        /*    Intent settinsIntent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            settinsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(settinsIntent);*/
        }
    }

    private static void gotLocation(Location location) {
        prevLocation = currentLocation == null ? null : new Location(
                currentLocation);
        currentLocation = location;
        if (isLocationNew()) {
            OnNewLocationReceived(location);
            /*Toast.makeText(_context, "new location saved",
                    Toast.LENGTH_SHORT).show();*/
            if (AppUtility.isNetworkAvailable(_context)) {
                // UpdateDriverCurrentLatLngTask task = new UpdateDriverCurrentLatLngTask(location.getLatitude(),location.getLongitude());
                // task.execute();
                UpdateCurrentLatLngTask(location.getLatitude(),location.getLongitude());
                sendLocationRegistration_To_Manasah_Wasl_API(location.getLatitude(),location.getLongitude());

            } else {
                //  AppUtility.showAlertDialog_SingleButton(DriverHomeActivity.this, getString(R.string.network_not_available), "", "Ok");
                Toast.makeText(_context, "Network not available. Please check network connection", Toast.LENGTH_LONG).show();
            }
           /* if (ConnectionDetector.isConnectingToInternet(_context)) {
                UpdateLatLongTask task = new UpdateLatLongTask("" + location.getLatitude(), "" + location.getLongitude());
                task.execute();
            }*/

            stopLocationListener();
        }
    }



    private static boolean isLocationNew() {
        if (currentLocation == null) {
            return false;
        } else if (prevLocation == null) {
            return true;
        } else if (currentLocation.getTime() == prevLocation.getTime()) {
            return false;
        } else {
            return true;
        }
    }

    public static void stopLocationListener() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(locationListener);

        }
        /*Toast.makeText(_context, "provider stoped", Toast.LENGTH_SHORT)
                .show();*/
    }

    // listener ----------------------------------------------------
    static ArrayList<OnNewLocationListener> arrOnNewLocationListener =
            new ArrayList<OnNewLocationListener>();

    // Allows the user to set a OnNewLocationListener outside of this class
    // and react to the event.
    // A sample is provided in ActDocument.java in method: startStopTryGetPoint
    public static void setOnNewLocationListener(
            OnNewLocationListener listener) {
        arrOnNewLocationListener.add(listener);
    }

    public static void clearOnNewLocationListener(
            OnNewLocationListener listener) {
        arrOnNewLocationListener.remove(listener);
    }

    // This function is called after the new point received
    private static void OnNewLocationReceived(Location location) {
        // Check if the Listener was set, otherwise we'll get an Exception
        // when we try to call it
        if (arrOnNewLocationListener != null) {
            // Only trigger the event, when we have any listener
            for (int i = arrOnNewLocationListener.size() - 1; i >= 0; i--) {
                arrOnNewLocationListener.get(i).onNewLocationReceived(
                        location);
            }
        }
    }
    double latitude;
    double longitude;
    //***********get user current location *************
    public Location getLocation(Context ct) {
        Location location = null;
        try {
            locationManager = (LocationManager) ct
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;

                if (ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                100,
                                1, locationListener);
                        Log.d("Network", "Network Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    100,
                                    1, locationListener);
                            Log.d("GPS", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

     /*updating data frm server for confirmbooking*/
/*

    public static class UpdateDriverCurrentLatLngTask extends AsyncTask<Void, Void, Boolean> {
        JSONObject jsonObj;
        String rStatus;
        double latitude,longitude;

        public  UpdateDriverCurrentLatLngTask(double lat,double lng){
            // TODO Auto-generated constructor stub

            this.latitude = lat;
            this.longitude = lng;
        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //  pickupAddress = tv_pickup_hd.getText().toString().trim();

            super.onPreExecute();
            //  customDialog = new CustomDialog(DriverHomeActivity.this);
            // if (isShown)
            //  customDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String jsonStr = null;
            try {
                //System.out.println("Driver LatLng : " + CurrentLatlng);
                // String pLat = String.valueOf(CurrentLatlng.latitude);
                //  String pLng = String.valueOf(CurrentLatlng.longitude);
                //   String CurrentLatLong = pLat +","+pLng;
                System.out.println("saved authTokan in comfirm booking := " + sessionManager.getAuthToken());
                String AuthToken = sessionManager.getAuthToken().toString();
                LatLng latLng = new LatLng(latitude,longitude);
                String CurrentLatLong = latitude +","+longitude;
                System.out.println("currrrreeennntt @@@ Lat : " + latitude + "\n" + "currrrreeennntt @@@ Lat Long : " + longitude);

                ServiceHandler sh = new ServiceHandler();
                List<NameValuePair> list = new ArrayList<>();

                list.add(new BasicNameValuePair(Constant.AUTHTOKEN, AuthToken));
                if (latLng!=null){
                    list.add(new BasicNameValuePair("latLong", CurrentLatLong));
                }
                jsonStr = sh.makeServiceCall(Constant.URL_UPDATE_DRIVER_LATLNG, ServiceHandler.POST_ENTITY, list);

                if (sh.getResponceCode() == 200) {

                    if (jsonStr != null) {
                        try {
                            jsonObj = new JSONObject(jsonStr);
                            rStatus = jsonObj.getString("status");
                            return true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("ServiceHandler", "Couldn't get any data from the url");
                    }
                }else if (sh.getResponceCode()==300){
                    sessionManager.logout();
                }

            } catch (Exception e) {
                e.printStackTrace();
                try{
                    if(e instanceof SocketTimeoutException) {
                        throw new SocketTimeoutException();
                    }
                } catch (SocketTimeoutException f){
                    Toast.makeText(_context, "Time out !, please try again !", Toast.LENGTH_LONG).show();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // updateCurrentLatLngAuthTask=null;
            try {
                //  arraylist.clear();
                String response = jsonObj.getString("status");
                String message = jsonObj.getString("message");

                if (response.equalsIgnoreCase("1")) {
                    Log.e("Service is running ******** ",message);
                    // Toast.makeText(_context, message, Toast.LENGTH_LONG).show();

                }
                else  {
                    Toast.makeText(_context, message, Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }
    }
*/

    /****************************************************************************************************************************************/
    public static void UpdateCurrentLatLngTask(final double latitude, final double longitude) {

        //   customDialog = new CustomDialog(_context);
        //  customDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_UPDATE_DRIVER_LATLNG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //    customDialog.cancel();
                        System.out.println("response update driver latlng" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                Log.d("Service is runing *****",message);
                                //   Toast.makeText(_context, message, Toast.LENGTH_LONG).show();

                            }
                            else  {
                                Toast.makeText(_context, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   customDialog.cancel();
                        //Toast.makeText(_context, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                //System.out.println("Driver LatLng : " + CurrentLatlng);
                // String pLat = String.valueOf(CurrentLatlng.latitude);
                //  String pLng = String.valueOf(CurrentLatlng.longitude);
                //   String CurrentLatLong = pLat +","+pLng;
                LatLng latLng = new LatLng(latitude,longitude);

                String CurrentLatLong = latitude +","+longitude;

                // System.out.println("Driver Lat : " + latitude + "\n" + "pickup Long : " + longitude);

                Map<String, String> header = new HashMap<>();
                if (latLng!=null){
                    header.put("latLong", CurrentLatLong);
                   /* header.put("vehicleReferenceNumber", sessionManager.getVehicleNo());*/
                }else {
                    // AppUtility.showAlertDialog_SingleButton_finishActivity(DriverHomeActivity.this,"Already logged in on another device","","Ok");
                    Toast.makeText(_context, "Your location is not updated", Toast.LENGTH_SHORT).show();
                }

                //  header.put("driverId", "");

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
                    //AppUtility.showAlertDialog_SingleButton_finishActivity(DriverHomeActivity.this,"Already logged in on another device","","Ok");
                    Toast.makeText(_context, "Already logged in on another device", Toast.LENGTH_SHORT).show();
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(_context);
        requestQueue.add(stringRequest);
    }
    /****************************************************************************************************************************************/


    /**
     * this method used to send location update to manasah API
     * @param latitude
     * @param longitude
     */
    private static void sendLocationRegistration_To_Manasah_Wasl_API(final double latitude, double longitude) {

        StringRequest sendInfo = new StringRequest(Request.Method.POST,  Constant.URL_LOCATION_UPDATE_MANASAH_WASL_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("apiKey", Constant.MANASAH_WASL_API_KEY);
                params.put("vehicleReferenceNumber", sessionManager.getVehicleNo());
                params.put("currentLatitude", String.valueOf(latitude));
                params.put("currentLongitude", String.valueOf(latitude));
                params.put("hasCustomer", "1");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(_context);
        requestQueue.add(sendInfo);
    }
}
