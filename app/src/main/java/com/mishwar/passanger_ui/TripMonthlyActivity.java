package com.mishwar.passanger_ui;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.mishwar.R;
import com.mishwar.adapter.TripMonthllyPlacesAdapter;
import com.mishwar.helper.ConnectionDetector;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.listner.CustomButtonListener;
import com.mishwar.model.tripMonthlyInfo;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.DatePickerFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TripMonthlyActivity extends AppCompatActivity implements View.OnClickListener ,CustomButtonListener{

    private   TextView actionbarLayout_title,tvTmPickupTime,tvTmStartDate,tvTmPrice,tvTmVehicleType;
    private   Button btn_next_tripMonthly;
    private   ImageView actionbar_btton_back;
    private Spinner tvTmSource,tvTmDestination,tvTmCustType;
    private RelativeLayout lyTmStartDate,lyTmPickupTime,lyTmPrice;

    private ArrayList<tripMonthlyInfo> getPlacesArraylist;
    private ArrayList<tripMonthlyInfo> getStudentPlacesArraylist;
    private ArrayList<tripMonthlyInfo> tmpPlaceList;

    int  mHour, mMinute;
    CustomDialog customDialog;
    SessionManager sessionManager;
    String  spTmSource="",spTmDestination="";
    private TripMonthllyPlacesAdapter placesAdapter,sourcePlaceadapter;
    private String  sourcePlaceId = "",destinationPlaceId = "",price="",destinationLatLong,pickupLatLong,distance,destinationAddress,sourceAddress, TmStatus;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_trip_monthly);
        sessionManager = new SessionManager(TripMonthlyActivity.this);
        setViewIds();

        if(ConnectionDetector.isNetworkAvailable(TripMonthlyActivity.this)){
            GetPlaceseTask();
        }
        else {
            AppUtility.showToast(TripMonthlyActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
        }

        tvTmSource.setAdapter(sourcePlaceadapter);
        tvTmSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spTmSource = parent.getItemAtPosition(position).toString();
                spTmSource =  tvTmSource.getSelectedItem().toString();
                // Get selected row data to show on screen
                tripMonthlyInfo monthlyInfo = getPlacesArraylist.get(position);

                //  sVehicleType = String.valueOf(position);
                sourceAddress = monthlyInfo.getPlaceName().toString();
                sourcePlaceId = monthlyInfo.getPlaceId().toString();
                pickupLatLong = monthlyInfo.getPlaceLatLong().toString();

                if(sourcePlaceId.equals("-1")){
                    return;
                }

                if (!sourcePlaceId.equals("-1") && !destinationPlaceId.equals("-1")){
                    if(ConnectionDetector.isNetworkAvailable(TripMonthlyActivity.this)){
                        GetRouteTask(sourcePlaceId,destinationPlaceId);
                    }
                    else {
                        AppUtility.showToast(TripMonthlyActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvTmDestination.setAdapter(placesAdapter);
        tvTmDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spTmDestination = parent.getItemAtPosition(position).toString();
                // Get selected row data to show on screen
                spTmDestination =  tvTmDestination.getSelectedItem().toString();
                tripMonthlyInfo monthlyInfo = tmpPlaceList.get(position);

                destinationAddress = monthlyInfo.getPlaceName().toString();
                destinationPlaceId = monthlyInfo.getPlaceId().toString();
                destinationLatLong = monthlyInfo.getPlaceLatLong().toString();

                if(destinationPlaceId.equals("-1")){
                    return;
                }

                if (!sourcePlaceId.equals("-1") && !destinationPlaceId.equals("-1")){
                    if(ConnectionDetector.isNetworkAvailable(TripMonthlyActivity.this)){
                        GetRouteTask(sourcePlaceId,destinationPlaceId);
                    }
                    else {
                        AppUtility.showToast(TripMonthlyActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setViewIds(){
        getPlacesArraylist = new ArrayList<>();
        getStudentPlacesArraylist = new ArrayList<>();
        tmpPlaceList = new ArrayList<>();

        placesAdapter = new TripMonthllyPlacesAdapter(TripMonthlyActivity.this, tmpPlaceList);
        sourcePlaceadapter = new TripMonthllyPlacesAdapter(TripMonthlyActivity.this, getPlacesArraylist);

        actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);
        actionbarLayout_title = (TextView) findViewById(R.id.actionbarLayout_title);
        tvTmPickupTime = (TextView) findViewById(R.id.tvTmPickupTime);
        tvTmCustType = (Spinner) findViewById(R.id.tvTmCustType);
        tvTmSource = (Spinner) findViewById(R.id.tvTmSource);
        tvTmDestination = (Spinner) findViewById(R.id.tvTmDestination);
        tvTmPickupTime = (TextView) findViewById(R.id.tvTmPickupTime);
        tvTmStartDate = (TextView) findViewById(R.id.tvTmStartDate);
        tvTmPrice = (TextView) findViewById(R.id.tvTmPrice);
        tvTmVehicleType = (TextView) findViewById(R.id.tvTmVehicleType);

        lyTmStartDate = (RelativeLayout) findViewById(R.id.lyTmStartDate);
        lyTmPickupTime = (RelativeLayout) findViewById(R.id.lyTmPickupTime);
        lyTmPrice = (RelativeLayout) findViewById(R.id.lyTmPrice);

        btn_next_tripMonthly = (Button) findViewById(R.id.btn_next_tripMonthly);

        actionbarLayout_title.setText(getString(R.string.text_trip_monthly));


        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(TripMonthlyActivity.this,R.layout.item_country_spinner,getResources().getStringArray(R.array.customer_type));
        tvTmCustType.setAdapter(genderAdapter);
        tvTmCustType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String  sCustType = getResources().getStringArray(R.array.customer_type)[position] ;
                sCustType =  tvTmCustType.getSelectedItem().toString();
                tmpPlaceList.clear();
                lyTmPrice.setVisibility(View.GONE);
                if (sCustType.equalsIgnoreCase("Student")){
                    tmpPlaceList.addAll(getStudentPlacesArraylist);

                }else{
                    tmpPlaceList.addAll(getPlacesArraylist);
                }
                placesAdapter.notifyDataSetChanged();
                sourcePlaceadapter.notifyDataSetChanged();

                tvTmSource.setSelection(0);
                tvTmDestination.setSelection(0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        actionbar_btton_back.setOnClickListener(this);
        btn_next_tripMonthly.setOnClickListener(this);
        lyTmStartDate.setOnClickListener(this);
        lyTmPickupTime.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.actionbar_btton_back:
                onBackPressed();
                break;

            case R.id.lyTmStartDate:
                DatePickerFragment datePickerFragment = new DatePickerFragment(Constant.CALENDAR_DAY, true);
                datePickerFragment.setDateListener(TripMonthlyActivity.this);
                datePickerFragment.show(getSupportFragmentManager(), "");

                break;

            case R.id.lyTmPickupTime:

                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog comeTimePickerDialog = new TimePickerDialog(TripMonthlyActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                tvTmPickupTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                comeTimePickerDialog.show();

                break;


            case R.id.btn_next_tripMonthly:
                String tripMonthllyDate = "";
                String tripMonthllyTime = "";
                tripMonthllyDate = tvTmStartDate.getText().toString();
                tripMonthllyTime = tvTmPickupTime.getText().toString();
                String sprice = tvTmPrice.getText().toString();

                if (TmStatus.equals("0")) {
                    if (!spTmSource.equalsIgnoreCase("Select place") && !sourcePlaceId.equals("-1")) {

                        if (!spTmDestination.equalsIgnoreCase("Select place") && !spTmDestination.equalsIgnoreCase("Select university") && !destinationPlaceId.equals("-1")) {

                            if (!tripMonthllyDate.equals("")) {

                                if (!tripMonthllyTime.equals("")) {

                                    if (!price.equals("")) {
                                        if (ConnectionDetector.isNetworkAvailable(TripMonthlyActivity.this)) {

                                            TripMonthllyBooking(price, tripMonthllyDate, tripMonthllyTime);

                                        } else {
                                            AppUtility.showToast(TripMonthlyActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
                                        }
                                    } else {
                                        AppUtility.showToast(TripMonthlyActivity.this, getString(R.string.text_route_not_available), 1);
                                    }
                                } else {
                                    AppUtility.showToast(TripMonthlyActivity.this, getString(R.string.text_select_pickup_time), 1);
                                }
                            } else {
                                AppUtility.showToast(TripMonthlyActivity.this, getString(R.string.text_select_pickup_date), 1);
                            }
                        } else {
                            AppUtility.showToast(TripMonthlyActivity.this, getString(R.string.text_select_destination), 1);

                        }
                    } else {
                        AppUtility.showToast(TripMonthlyActivity.this, getString(R.string.text_select_pickup), 1);

                    }
                }else {
                    AppUtility.showToast(TripMonthlyActivity.this, getString(R.string.text_already_have_tm_trip), 1);

                }

                break;
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tripMonthlyintent = new Intent(TripMonthlyActivity.this,HomeActivity.class);
        tripMonthlyintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
        startActivity(tripMonthlyintent);
        finish();
    }

    // set date on clicke on date picker's date
    @Override
    public void onDateSet(int year, int month, int day, int cal_type) {
        tvTmStartDate.setText(year + "-" + (month + 1) + "-" + day);
    }

    /****************************************************************************************************************************************/

    public void GetPlaceseTask() {

        customDialog = new CustomDialog(TripMonthlyActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_TRIPMONTHLLY_GET_PLACES,
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
                            TmStatus = jsonObj.getString("TmStatus");

                            if (status.equalsIgnoreCase("1")) {

                                getPlacesArraylist.clear();
                                getStudentPlacesArraylist.clear();

                                tripMonthlyInfo defaultItem = new tripMonthlyInfo();
                                defaultItem.setPlaceName("Select place");
                                defaultItem.setPlaceId("-1");
                                defaultItem.setPlaceLatLong("0.0,0.0");

                                getPlacesArraylist.add(defaultItem);

                                JSONArray jsonArray = jsonObj.getJSONArray("result");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    tripMonthlyInfo item = new tripMonthlyInfo();

                                    item.setPlaceId(jsonObject.getString("placeId").toString().trim());
                                    item.setPlaceName(jsonObject.getString("place").toString().trim());
                                    item.setPlaceLatLong(jsonObject.getString("placeLatLong").toString().trim());

                                    getPlacesArraylist.add(item);

                                }

                                JSONArray studentArray = jsonObj.getJSONArray("student");

                                tripMonthlyInfo studentPlace = new tripMonthlyInfo();
                                studentPlace.setPlaceName("Select university");
                                studentPlace.setStudentPlaceName("Select university");
                                studentPlace.setPlaceId("-1");
                                studentPlace.setPlaceLatLong("0.0,0.0");
                                getStudentPlacesArraylist.add(studentPlace);


                                for (int i = 0; i < studentArray.length(); i++) {

                                    JSONObject studentObject = studentArray.getJSONObject(i);

                                    tripMonthlyInfo item = new tripMonthlyInfo();

                                    item.setPlaceId(studentObject.getString("placeId").toString().trim());
                                    item.setPlaceName(studentObject.getString("place").toString().trim());
                                    item.setPlaceLatLong(studentObject.getString("placeLatLong").toString().trim());

                                    getStudentPlacesArraylist.add(item);
                                    System.out.println("student array"+getStudentPlacesArraylist.size());

                                }
                                tmpPlaceList.addAll(getStudentPlacesArraylist);
                                placesAdapter.notifyDataSetChanged();
                                sourcePlaceadapter.notifyDataSetChanged();

                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(TripMonthlyActivity.this,"Error occured, please try again !","","Ok");
                            }

                            else {
                                AppUtility.showToast(TripMonthlyActivity.this,message,0);
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
                        Toast.makeText(TripMonthlyActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
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
                    AppUtility.showAlertDialog_SingleButton_finishActivity(TripMonthlyActivity.this,"Already logged in on another device","","Ok");
                    //Toast.makeText(HomeActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };


        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/
    public  void GetRouteTask(final String source, final String destination) {

        customDialog = new CustomDialog(TripMonthlyActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TRIPMONTHLLY_GET_ROUTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("tripmonthlly response " + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                JSONObject resultObj = jsonObj.getJSONObject("result");
                                price = resultObj.getString("price");
                                String dropUpPlace = resultObj.getString("dropUpPlace");
                                String pickUpPlace = resultObj.getString("pickUpPlace");
                                distance = resultObj.getString("distance");
                                String time = resultObj.getString("time");

                                tvTmPrice.setVisibility(View.VISIBLE);
                                lyTmPrice.setVisibility(View.VISIBLE);
                                tvTmPrice.setText("$ "+price);
                            }
                            else  {
                                tvTmPrice.setVisibility(View.GONE);
                                lyTmPrice.setVisibility(View.GONE);
                                Toast.makeText(TripMonthlyActivity.this, "There is no route available.", Toast.LENGTH_LONG).show();

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
                        Toast.makeText(TripMonthlyActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("sourceId", source);
                header.put("destinationId", destination);

                return header;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }else {
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(TripMonthlyActivity.this,"Already logged in on another device","","Ok");
                }
                return header;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    /****************************************************************************************************************************************/
    public  void TripMonthllyBooking(final String price,final String date,final String time) {

        customDialog = new CustomDialog(TripMonthlyActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TRIPMONTHLLY_SUBMIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("response " + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                //  JSONObject resultObj = jsonObj.getJSONObject("result");
                                // String price = resultObj.getString("price");
                                AppUtility.showToast(TripMonthlyActivity.this,message,0);
                                Intent i = new Intent(TripMonthlyActivity.this, HomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                                startActivity(i);
                                finish();
                            }
                            else  {
                                Toast.makeText(TripMonthlyActivity.this, message, Toast.LENGTH_LONG).show();

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
                        Toast.makeText(TripMonthlyActivity.this, getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("pickupAddress", sourceAddress);
                header.put("destinationAddress", destinationAddress);
                header.put("pickupLatLong", pickupLatLong);
                header.put("destinationLatLong", destinationLatLong);
                header.put("distance", distance);
                header.put("tripTotal", price);
                header.put("rideStartDate", date);
                header.put("rideStartTime", time);

                return header;
            }
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

}
