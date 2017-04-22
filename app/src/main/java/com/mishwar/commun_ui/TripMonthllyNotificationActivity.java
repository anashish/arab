package com.mishwar.commun_ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.passanger_ui.HomeActivity;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.PaymentClass;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.mishwar.helper.Constant.REQUEST_CODE_PAYMENT;

public class TripMonthllyNotificationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 123;
    private ImageView driver_actionbar_btton_back;
    private TextView driver_actionbarLayout_title, btnTmCall,tvExpireNote;
    private Button btnTmCancel,btnTmPayWithPaypal;
    private String mobileNumber,tripTotal="",bookingId,paypal_id="";
    RelativeLayout lyTmName,lyTmMobileNo;
    PaymentClass paymentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_monthlly_notification);
        initializeView();
    }

    private void initializeView(){
        paymentClass = new PaymentClass(TripMonthllyNotificationActivity.this);

        driver_actionbar_btton_back = (ImageView) findViewById(R.id.driver_actionbar_btton_back);

        lyTmName = (RelativeLayout) findViewById(R.id.lyTmName);
        lyTmMobileNo = (RelativeLayout) findViewById(R.id.lyTmMobileNo);

        driver_actionbarLayout_title = (TextView) findViewById(R.id.driver_actionbarLayout_title);
        TextView lyTmAddressText = (TextView) findViewById(R.id.lyTmAddressText);
        TextView tvTmNameText = (TextView) findViewById(R.id.tvTmNameText);
        TextView tvTmDestinationText = (TextView) findViewById(R.id.tvTmDestinationText);
        TextView tvTmTripTime = (TextView) findViewById(R.id.tvTmTripTime);
        TextView tvTmDateText = (TextView) findViewById(R.id.tvTmDateText);
        TextView lyTmCostText = (TextView) findViewById(R.id.lyTmCostText);
        TextView tvTmMobileNoText = (TextView) findViewById(R.id.tvTmMobileNoText);
        TextView tvTmName = (TextView) findViewById(R.id.tvTmName);
        TextView  tvTmMobileNo = (TextView) findViewById(R.id.tvTmMobileNo);
        btnTmCall = (TextView) findViewById(R.id.btnTmCall);
        tvExpireNote = (TextView) findViewById(R.id.tvExpireNote);
        TextView tvTextTmBookingId = (TextView) findViewById(R.id.tvTextTmBookingId);

        btnTmCancel = (Button) findViewById(R.id.btnTmCancel);
        btnTmPayWithPaypal = (Button) findViewById(R.id.btnTmPayWithPaypal);

        SessionManager sessionManager = new SessionManager(TripMonthllyNotificationActivity.this);
        if(sessionManager.getUserType().equals("driver")){
            btnTmPayWithPaypal.setVisibility(View.GONE);
            tvExpireNote.setVisibility(View.GONE);
        }else {
            btnTmPayWithPaypal.setVisibility(View.VISIBLE);
            tvExpireNote.setVisibility(View.VISIBLE);
        }

        driver_actionbarLayout_title.setText("");
        String pickupAddress, destinationAddress, fullName, rideStartTime, rideStartDate;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String  notificationType = "";
            notificationType = extras.getString("notificationType");

            if (!notificationType.equals("")) {
                destinationAddress = extras.getString("destinationAddress");
                pickupAddress = extras.getString("pickupAddress");
                tripTotal = extras.getString("tripTotal");
                rideStartTime = extras.getString("rideStartTime");
                rideStartDate = extras.getString("rideStartDate");
                bookingId = extras.getString("bookingId");

                if (notificationType.equals("tripMonthly")) {
                    fullName = extras.getString("fullName");
                    mobileNumber = extras.getString("mobileNumber");

                    tvTmNameText.setVisibility(View.VISIBLE);
                    tvTmMobileNoText.setVisibility(View.VISIBLE);
                    btnTmCall.setVisibility(View.VISIBLE);
                    lyTmMobileNo.setVisibility(View.VISIBLE);
                    lyTmName.setVisibility(View.VISIBLE);
                    btnTmCancel.setVisibility(View.VISIBLE);
                    tvTmName.setVisibility(View.VISIBLE);
                    tvTmMobileNo.setVisibility(View.VISIBLE);

                    tvTmNameText.setText(fullName);
                    tvTmMobileNoText.setText(mobileNumber);

                    btnTmPayWithPaypal.setVisibility(View.GONE);

                }
                else if (notificationType.equals("tmAccept")){
                    tvTmNameText.setVisibility(View.GONE);
                    tvTmMobileNoText.setVisibility(View.GONE);
                    btnTmCall.setVisibility(View.GONE);
                    lyTmMobileNo.setVisibility(View.GONE);
                    lyTmName.setVisibility(View.GONE);
                    btnTmCancel.setVisibility(View.GONE);
                    tvTmName.setVisibility(View.GONE);
                    tvTmMobileNo.setVisibility(View.GONE);
                }

                tvTextTmBookingId.setText(bookingId);
                lyTmAddressText.setText(pickupAddress);
                tvTmDestinationText.setText(destinationAddress);
                tvTmTripTime.setText(rideStartTime);
                tvTmDateText.setText(rideStartDate);
                lyTmCostText.setText("$ "+tripTotal);

            }

        }

        driver_actionbar_btton_back.setOnClickListener(this);
        btnTmCancel.setOnClickListener(this);
        btnTmCall.setOnClickListener(this);
        btnTmPayWithPaypal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())

        {
            case R.id.driver_actionbar_btton_back:
                onBackPressed();
                break;
            case R.id.btnTmPayWithPaypal:
                paymentClass.starProcessToPay(tripTotal, "USD", REQUEST_CODE_PAYMENT, "Payment for Mishwar taxi app!");
                break;

            case R.id.btnTmCancel:
                onBackPressed();
                break;

            case R.id.btnTmCall:
                if (!mobileNumber.equals("")) {
                    if (ContextCompat.checkSelfPermission(TripMonthllyNotificationActivity.this,
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(TripMonthllyNotificationActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    } else {
                        executeCall();
                    }
                }
                break;
        }
    }

     /*----------------------------------------------------------------------------------------------------------------*/

    private void executeCall() {
        // start your call here
        TripMonthllyNotificationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mobileNumber.equals("")) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+" + mobileNumber));
                    if (ActivityCompat.checkSelfPermission(TripMonthllyNotificationActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE :{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    executeCall();

                } else {

                    Toast.makeText(TripMonthllyNotificationActivity.this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
    /*----------------------------------------------------------------------------------------------------------------*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Intent intent = new Intent(TripMonthllyNotificationActivity.this,DriverHomeActivity.class);
        //  startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.slide_out);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);


                if (confirm != null)
                {
                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject()
                                .toString(4));
                        String  state,create_time,platform,paypal_sdk_version,product_name,short_description,paypal_amount,currency_code;
                        JSONObject jsonObject = confirm.toJSONObject();

                        JSONObject jsonResponse = jsonObject.getJSONObject("response");

                        System.out.println("jsonResponse   =  "+jsonResponse);

                        state = jsonResponse.getString("state");
                        paypal_id = jsonResponse.getString("id");
                        create_time = jsonResponse.getString("create_time");

                        UpdateTmPaymentInfo();

                        JSONObject jsonClient = jsonObject.getJSONObject("client");

                        System.out.println("paypal_id   =  "+paypal_id);
                        System.out.println("jsonClient   =  "+jsonClient);

                        platform = jsonClient.getString("platform");
                        paypal_sdk_version = jsonClient.getString("paypal_sdk_version");
                        product_name = jsonClient.getString("product_name");

                        JSONObject jsonObjectPay = confirm.getPayment().toJSONObject();

                        System.out.println("jsonObjectPay   =  "+jsonObjectPay);

                        short_description = jsonObjectPay.getString("short_description");
                        paypal_amount = jsonObjectPay.getString("amount");
                        currency_code = jsonObjectPay.getString("currency_code");

                        // getPaymentFromServer();

                        // Toast.makeText(getApplicationContext(), "Payment succesfull",Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }else if(resultCode == Activity.RESULT_CANCELED)
            {
                System.out.println("The user canceled.");
            }
            else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
                System.out.println("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }

        }
    }
    private void sendAuthorizationToServer(PayPalAuthorization authorization) {


    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    //****************************************************************//
    public void UpdateTmPaymentInfo() {
        final CustomDialog customDialog = new CustomDialog(TripMonthllyNotificationActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TRIPMONTHLLY_PATMENT_SUBMIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("PassengerPaymentActivity response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                AppUtility.showToast(TripMonthllyNotificationActivity.this,message,1);
                                Intent i = new Intent(TripMonthllyNotificationActivity.this, HomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                                startActivity(i);

                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(TripMonthllyNotificationActivity.this,"server error occured, please try again later!","","Ok");
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(TripMonthllyNotificationActivity.this,message,"","Ok");
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
                        Toast.makeText(TripMonthllyNotificationActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("bookingId", bookingId);
                header.put("transactionId", paypal_id);
                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                SessionManager  sessionManager = new SessionManager(TripMonthllyNotificationActivity.this);
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(TripMonthllyNotificationActivity.this);
        requestQueue.add(stringRequest);
    }

}
