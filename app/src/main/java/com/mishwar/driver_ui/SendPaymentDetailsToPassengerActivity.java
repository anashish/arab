package com.mishwar.driver_ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.mishwar.R;
import com.mishwar.driver_fregments.DriverCancelBookingsFragment;
import com.mishwar.driver_fregments.DriverCurrentMapFragment;
import com.mishwar.driver_fregments.DriverPriviousTripFragment;
import com.mishwar.driver_fregments.DriverUpcomingTripFragment;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;

import org.json.JSONObject;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SendPaymentDetailsToPassengerActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvBookingNo,tvBookingPickupAddress,tvBookingDestinationAddress,tvBookingTotalPrice,tvPaymentType,tvBookingStartDate,
            tvEditableDistance,tvEditableTime;
    ImageView iveditDistance,ivEditTime,ivEditDistancePlus,ivEditDistanceMinus,ivEditTimePlus,ivEditTimeMinus;
    Button btnSendReciept;
    private   String Counterprice="2",priceByDistence="2",priceByTime="2",bookingId="2",sDist="1";
    private int counterDist ,counterTime , duration=2,distance=4;
    private CustomDialog customDialog;
    private SessionManager sessionManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_payment_riciept);
        sessionManager = new SessionManager(SendPaymentDetailsToPassengerActivity.this);
        initializeViews();
    }

    /***********Initializing ids of view*******************/
    private void initializeViews() {

        tvBookingNo = (TextView) findViewById(R.id.tvBookingNo);
        tvBookingPickupAddress = (TextView) findViewById(R.id.tvBookingPickupAddress);
        tvBookingDestinationAddress = (TextView) findViewById(R.id.tvBookingDestinationAddress);
        tvBookingTotalPrice = (TextView) findViewById(R.id.tvBookingTotalPrice);
        tvPaymentType = (TextView) findViewById(R.id.tvPaymentType);
        tvBookingStartDate = (TextView) findViewById(R.id.tvBookingStartDate);
        tvEditableDistance = (TextView) findViewById(R.id.tvEditableDistance);
        tvEditableTime = (TextView) findViewById(R.id.tvEditableTime);

        btnSendReciept = (Button) findViewById(R.id.btnSendReciept);

        ivEditDistancePlus = (ImageView) findViewById(R.id.ivEditDistancePlus);
        ivEditDistanceMinus = (ImageView) findViewById(R.id.ivEditDistanceMinus);
        ivEditTimePlus = (ImageView) findViewById(R.id.ivEditTimePlus);
        ivEditTimeMinus = (ImageView) findViewById(R.id.ivEditTimeMinus);

        String  fullName ="",pickupAddress,destinationAddress,totalCost,rideStartDate,rideEndDate,rideEndTime,rideStartTime;

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            fullName = extras.getString("fullName");

            if (fullName!=null || !fullName.equals("")){

                Counterprice = extras.getString("Counterprice");
                priceByDistence = extras.getString("priceByDistence");
                priceByTime = extras.getString("priceByTime");
                bookingId = extras.getString("bookingId");
                pickupAddress = extras.getString("pickupAddress");
                destinationAddress = extras.getString("destinationAddress");
                sDist = extras.getString("distance");
                totalCost = extras.getString("tripCost");
                rideStartDate = extras.getString("rideStartDate");
                rideEndDate = extras.getString("rideEndDate");
                rideEndTime = extras.getString("rideEndTime");
                rideStartTime = extras.getString("rideStartTime");

                //convert distance string to int
                String sDist1 = sDist.trim();

                if(sDist.contains(".")){
                    //  String[] dist2 = sDist1.split(".");
                    String sDist3 =  sDist.substring(0, sDist.indexOf("."));
                    //  String sDist3 = dist2[0];
                    sDist = sDist3;
                    distance = Integer.parseInt(sDist3);
                }else {
                    distance = Integer.parseInt(sDist1);
                }


                try {
                    int days,hours,min;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    Date date1 = simpleDateFormat.parse(rideStartTime);
                    Date date2 = simpleDateFormat.parse(rideEndTime);

                    long difference = date2.getTime() - date1.getTime();
                    days = (int) (difference / (1000*60*60*24));
                    hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                    min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
                    hours = (hours < 0 ? -hours : hours);
                    int minit = hours*60;
                    duration = minit+min;
                    Log.i("======= Hours"," :: "+hours +"======= min :: "+min+"======= duration :: "+duration);
                }catch (Exception e){
                    e.printStackTrace();
                }

                counterDist = distance;
                counterTime =  duration;
                /****set texts of textview***/
                String sDuration = duration +"";

                tvBookingNo.setText(": "+bookingId);
                tvBookingPickupAddress.setText(" "+pickupAddress);
                tvBookingDestinationAddress.setText(" "+destinationAddress);

                tvEditableDistance.setText(distance+"");
                if (sDuration.equals("0min"))
                {
                    tvEditableTime.setText("1min");
                }else {
                    tvEditableTime.setText(sDuration);
                }

                tvBookingTotalPrice.setText(" "+totalCost);
                tvPaymentType.setText((""+sessionManager.getPaymentType().toString()));
                tvBookingStartDate.setText(" "+rideStartDate);
            }
        }

        btnSendReciept.setOnClickListener(this);
        ivEditDistancePlus.setOnClickListener(this);
        ivEditDistanceMinus.setOnClickListener(this);
        ivEditTimePlus.setOnClickListener(this);
        ivEditTimeMinus.setOnClickListener(this);
    }


    /*********** onclick of view's components *******************/
    double opening_counter=2.1, kilmeter_price=2.1, minute_price=2.1, trip_price=2.1, number_of_kilometere =2.1, number_of_minutes= 1.0;

    @Override
    public void onClick(View v) {
        switch (v.getId())

        {
            case R.id.btnSendReciept:
                if (AppUtility.isNetworkAvailable(SendPaymentDetailsToPassengerActivity.this))
                {
                    SendPaymentRecieptTOPassenger();
                }
                else {
                    AppUtility.showAlertDialog_SingleButton(SendPaymentDetailsToPassengerActivity.this, getString(R.string.network_not_available), "", "Ok");
                }
                break;

            case R.id.ivEditDistancePlus:
                String lang =  sessionManager.getLanguage();
                if (lang.equalsIgnoreCase("ar")){
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Resources resources = getResources();
                    Configuration configuration = resources.getConfiguration();
                    configuration.locale = locale;
                    resources.updateConfiguration(configuration, resources.getDisplayMetrics());

                }

                counterDist++;
                tvEditableDistance.setText(String.valueOf(counterDist));

                String noOfKilometer = tvEditableDistance.getText().toString();
                String noOfMinits = tvEditableTime.getText().toString();

                String[] time = noOfMinits.split(" ");
                String time1MIn =  time[0];


                number_of_kilometere = Double.valueOf(noOfKilometer.trim()).doubleValue();
                number_of_minutes = Double.valueOf(time1MIn.trim()).doubleValue();

                opening_counter = Double.parseDouble(Counterprice);
                kilmeter_price = Double.parseDouble(priceByDistence);
                minute_price = Double.parseDouble(priceByTime);
                //number_of_kilometere = valueDistance;y
                trip_price = opening_counter + (number_of_kilometere * kilmeter_price) + (number_of_minutes * minute_price);
                tvBookingTotalPrice.setText(String.format("%.2f", trip_price));

                break;

            case R.id.ivEditDistanceMinus:
                String lang2 =  sessionManager.getLanguage();
                if (lang2.equalsIgnoreCase("ar")){
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Resources resources = getResources();
                    Configuration configuration = resources.getConfiguration();
                    configuration.locale = locale;
                    resources.updateConfiguration(configuration, resources.getDisplayMetrics());

                }

                if (counterDist > 1) {
                    counterDist--;
                    tvEditableDistance.setText((String.valueOf(counterDist)));

                    String noOfKilometerMinus = tvEditableDistance.getText().toString();
                    String noOfMinitsMinus = tvEditableTime.getText().toString();

                    String[] time2 = noOfMinitsMinus.split(" ");
                    String timeMinus =  time2[0];

                    number_of_kilometere = Double.valueOf(noOfKilometerMinus.trim()).doubleValue();
                    number_of_minutes = Double.valueOf(timeMinus.trim()).doubleValue();

                    opening_counter = Double.parseDouble(Counterprice);
                    kilmeter_price = Double.parseDouble(priceByDistence);
                    minute_price = Double.parseDouble(priceByTime);
                    trip_price = opening_counter + (number_of_kilometere * kilmeter_price) + (number_of_minutes * minute_price);
                    tvBookingTotalPrice.setText(String.format("%.2f", trip_price));


                } else {
                    String noOfKilometerMinus2 = tvEditableDistance.getText().toString();
                    String noOfMinitsMinus = tvEditableTime.getText().toString();

                    number_of_kilometere = Double.valueOf(noOfKilometerMinus2.trim()).doubleValue();
                    number_of_minutes = Double.valueOf(noOfMinitsMinus.trim()).doubleValue();

                    opening_counter = Double.parseDouble(Counterprice);
                    kilmeter_price = Double.parseDouble(priceByDistence);
                    minute_price = Double.parseDouble(priceByTime);
                    //number_of_kilometere = valueDistance;y
                    trip_price = opening_counter + (number_of_kilometere * kilmeter_price) + (number_of_minutes * minute_price);
                    tvBookingTotalPrice.setText(String.format("%.2f", trip_price));

                    AppUtility.showToast(SendPaymentDetailsToPassengerActivity.this, "minimum distance is 1 km.", Toast.LENGTH_SHORT);
                }

                break;

            case R.id.ivEditTimePlus:

                String lang3 =  sessionManager.getLanguage();
                if (lang3.equalsIgnoreCase("ar")){
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Resources resources = getResources();
                    Configuration configuration = resources.getConfiguration();
                    configuration.locale = locale;
                    resources.updateConfiguration(configuration, resources.getDisplayMetrics());

                }
                counterTime++;
                tvEditableTime.setText(String.valueOf(counterTime));
                // ivEditTime.setEnabled(true);

                String inTimeNoOfKilometer = tvEditableDistance.getText().toString();
                String inTimeNoOfMinits = tvEditableTime.getText().toString();

                String[] time2 = inTimeNoOfMinits.split(" ");
                String timeMinus =  time2[0];

                number_of_kilometere = Double.valueOf(inTimeNoOfKilometer.trim()).doubleValue();
                number_of_minutes = Double.valueOf(timeMinus.trim()).doubleValue();

                opening_counter = Double.parseDouble(Counterprice);
                kilmeter_price = Double.parseDouble(priceByDistence);
                minute_price = Double.parseDouble(priceByTime);
                //number_of_kilometere = valueDistance;y
                trip_price = opening_counter + (number_of_kilometere * kilmeter_price) + (number_of_minutes * minute_price);
                tvBookingTotalPrice.setText(String.format("%.2f", trip_price));

                break;

            case R.id.ivEditTimeMinus:
                String lang4 =  sessionManager.getLanguage();
                if (lang4.equalsIgnoreCase("ar")){
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Resources resources = getResources();
                    Configuration configuration = resources.getConfiguration();
                    configuration.locale = locale;
                    resources.updateConfiguration(configuration, resources.getDisplayMetrics());

                }

                if (counterTime > 1) {
                    counterTime--;
                    tvEditableTime.setText(String.valueOf(counterTime));

                    String inTimeNoOfKilometer2 = tvEditableDistance.getText().toString();
                    String inTimeNoOfMinits2 = tvEditableTime.getText().toString();

                    String[] time3 = inTimeNoOfMinits2.split(" ");
                    String timeMinus2 =  time3[0];

                    number_of_kilometere = Double.valueOf(inTimeNoOfKilometer2.trim()).doubleValue();
                    number_of_minutes = Double.valueOf(timeMinus2.trim()).doubleValue();

                    opening_counter = Double.parseDouble(Counterprice);
                    kilmeter_price = Double.parseDouble(priceByDistence);
                    minute_price = Double.parseDouble(priceByTime);
                    trip_price = opening_counter + (number_of_kilometere * kilmeter_price) + (number_of_minutes * minute_price);
                    tvBookingTotalPrice.setText(String.format("%.2f", trip_price));
                }
                else {

                    String lang5 =  sessionManager.getLanguage();
                    if (lang5.equalsIgnoreCase("ar")){
                        Locale locale = new Locale("en");
                        Locale.setDefault(locale);
                        Resources resources = getResources();
                        Configuration configuration = resources.getConfiguration();
                        configuration.locale = locale;
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

                    }

                    String inTimeNoOfKilometer3 = tvEditableDistance.getText().toString();
                    String inTimeNoOfMinits3 = tvEditableTime.getText().toString();

                    String[] time4 = inTimeNoOfMinits3.split(" ");
                    String timeMinus2 =  time4[0];

                    number_of_kilometere = Double.valueOf(inTimeNoOfKilometer3.trim()).doubleValue();
                    number_of_minutes = Double.valueOf(timeMinus2.trim()).doubleValue();

                    opening_counter = Double.parseDouble(Counterprice);
                    kilmeter_price = Double.parseDouble(priceByDistence);
                    minute_price = Double.parseDouble(priceByTime);
                    trip_price = opening_counter + (number_of_kilometere * kilmeter_price) + (number_of_minutes * minute_price);
                    tvBookingTotalPrice.setText(String.format("%.2f", trip_price));
                    AppUtility.showToast(SendPaymentDetailsToPassengerActivity.this, "minimum time is 1 min.", Toast.LENGTH_SHORT);
                }

                break;


        }
    }

    /*********** onBackpress *******************/

    @Override
    public void onBackPressed() {

    }

    /****************************************************************************************************************************************/
    public void SendPaymentRecieptTOPassenger() {

        customDialog = new CustomDialog(SendPaymentDetailsToPassengerActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_DRIVER_SEND_PAYMENT_DETAILS_TO_PASS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   customDialog.cancel();
                        System.out.println("SendPaymentDetailsToPassengerActivity response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            String lang =  sessionManager.getLanguage();
                            if (lang.equalsIgnoreCase("ar")){
                                Locale locale = new Locale("ar");
                                Locale.setDefault(locale);
                                Resources resources = getResources();
                                Configuration configuration = resources.getConfiguration();
                                configuration.locale = locale;
                                resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                                sessionManager.setLanguage("ar");
                            }

                            if (status.equalsIgnoreCase("1")) {
                                AppUtility.showToast(SendPaymentDetailsToPassengerActivity.this, message, 1);

                                startActivity(new Intent(SendPaymentDetailsToPassengerActivity.this, DriverMyBookingsActivity.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                                DriverCurrentMapFragment.createView =0;
                                DriverCancelBookingsFragment.createCancelView =0;
                                DriverPriviousTripFragment.createPriviousTripView =0;
                                DriverUpcomingTripFragment.createUpcomingTripView =0;
                               /* JSONObject currentLocJsonObj = jsonObj.getJSONObject("location");
                                String updateDriverLocLatLng = currentLocJsonObj.getString("driverLatLong");
                                String updatePassengerLocLatLong = currentLocJsonObj.getString("passengerLatLong");
*/
                            }
                            else {
                                AppUtility.showToast(SendPaymentDetailsToPassengerActivity.this, message, 1);
                                startActivity(new Intent(SendPaymentDetailsToPassengerActivity.this, DriverMyBookingsActivity.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                                DriverCurrentMapFragment.createView =0;
                                DriverCancelBookingsFragment.createCancelView =0;
                                DriverPriviousTripFragment.createPriviousTripView =0;
                                DriverUpcomingTripFragment.createUpcomingTripView =0;
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
                        Toast.makeText(SendPaymentDetailsToPassengerActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                String rideTime = tvEditableTime.getText().toString();
                String rideDistance = tvEditableDistance.getText().toString();
                String totalPayment =   tvBookingTotalPrice.getText().toString();

                Charset.forName("UTF-8").encode(totalPayment);

                header.put("bookingId",bookingId);
                header.put("totalPayment",totalPayment);
                header.put("paymenType",sessionManager.getPaymentType().toString());
                header.put("rideTime",rideTime);
                header.put("rideDistance",rideDistance);
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

        RequestQueue requestQueue = Volley.newRequestQueue(SendPaymentDetailsToPassengerActivity.this);
        requestQueue.add(stringRequest);
    }


    /****************************************************************************************************************************************/

}
