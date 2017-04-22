package com.mishwar.passanger_ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.paypal.android.sdk.payments.PayPalService;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import static com.mishwar.R.id.rb_paypal;

public class PassengerPaymentActivity extends AppCompatActivity {
    TextView tvPassBookingNo,tvBookingPickupAddress,tvBookingDestinationAddress,tvBookingTotalPrice,tvDistance,tvTime,tvBookingStartDate,tvPassPaymentType;
    Button btn_paymentDone;
    String  paymentAmount="100",paypalId= "",bookingId = "",driverId="",paymenType,paymentId,payKey="",RatingCount="0",Review="";
    private CustomDialog customDialog;
     Dialog dialogRating;
    RelativeLayout activity_payment;
    RadioGroup radioGroup;
    WebView webdisplayView;
    private RatingBar ratingBar;
    ScrollView scrollView;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recive_payment);
        // paymentClass = new PaymentClass(PassengerPaymentActivity.this);
        initializeViews();


        btn_paymentDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txt = btn_paymentDone.getText().toString();
                if(btn_paymentDone.getText().toString().equals("Link Paypal Account")){

                }else if(btn_paymentDone.getText().toString().equalsIgnoreCase(getString(R.string.text_pay_cash))){
                    String dPaypalId = "";
                    SessionManager  sessionManager = new SessionManager(PassengerPaymentActivity.this);
                    dPaypalId = sessionManager.getPayPalId().toString();

                    paymenType = "paypal";
                    if (!dPaypalId.equals("") || !dPaypalId.equals(null))
                    {
                        initWebView();

                    }else {
                        AppUtility.showToast(PassengerPaymentActivity.this,getString(R.string.text_add_paypalid),1);
                    }

                }else if(btn_paymentDone.getText().toString().equalsIgnoreCase(getString(R.string.text_done))){
                    paymenType = "cash";
                    UpdatePaymentInfo();
                }

            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {

                if(id == rb_paypal){
                    paymenType = "paypal";
                    btn_paymentDone.setText(getString(R.string.text_pay_cash));

                }else {
                    //tv_paymentMode.setText("Cash");
                    //paypal_layout.setVisibility(View.GONE);
                    btn_paymentDone.setText(getString(R.string.text_done));
                    btn_paymentDone.setVisibility(View.VISIBLE);

                }
            }
        });


    }

    private void initializeViews() {
        String  fullName,pickupAddress,destinationAddress,totalCost,rideStartDate,rideEndDate,rideEndTime,rideStartTime,distance,rideDuration;

        tvPassBookingNo = (TextView) findViewById(R.id.tvPassBookingNo);
        tvBookingPickupAddress = (TextView) findViewById(R.id.tvBookingPickupAddress);
        tvBookingDestinationAddress = (TextView) findViewById(R.id.tvBookingDestinationAddress);
        tvBookingTotalPrice = (TextView) findViewById(R.id.tvBookingTotalPrice);
        tvBookingStartDate = (TextView) findViewById(R.id.tvBookingStartDate);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvPassPaymentType = (TextView) findViewById(R.id.tvPassPaymentType);
        btn_paymentDone = (Button) findViewById(R.id.btn_paymentDone);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.check(R.id.rb_paypal);


        activity_payment = (RelativeLayout) findViewById(R.id.activity_payment);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String  paymenType = extras.getString("paymenType");
            if (paymenType != null || !paymenType.equals("")) {

                bookingId = extras.getString("bookingId");
                paymentAmount = extras.getString("paymentAmount");
                pickupAddress = extras.getString("pickupAddress");
                destinationAddress = extras.getString("destinationAddress");
                distance = extras.getString("rideDistance");
                rideStartDate = extras.getString("startDate");
                rideEndDate = extras.getString("endDate");
                rideEndTime = extras.getString("endTime");
                rideStartTime = extras.getString("startTime");
                rideDuration = extras.getString("rideTime");
                paymentId = extras.getString("paymentId");
                driverId = extras.getString("driver");

                tvPassPaymentType.setText(" "+paymenType);
                tvPassBookingNo.setText(" "+bookingId);
                tvBookingPickupAddress.setText(" "+pickupAddress);
                tvBookingDestinationAddress.setText(" "+destinationAddress);
                tvTime.setText(rideDuration+" min");
                tvDistance.setText(distance+" km");
                tvBookingTotalPrice.setText(( paymentAmount));
                //  tvPassCost.setText("$ "+String.format("%.2f", tripCost));
                tvBookingStartDate.setText(" : "+rideStartDate);
            }
        }

    }




    public void initWebView() {
        webdisplayView =(WebView) findViewById(R.id.load_webview);
        webdisplayView.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        webdisplayView.setWebViewClient(new MyWebViewClient());
        webdisplayView.loadUrl("http://mashaueer.com/mishwar/index.php/home/buy?id="+bookingId);
        webdisplayView.getSettings().setBuiltInZoomControls(true);
        webdisplayView.getSettings().setDisplayZoomControls(false);
        webdisplayView.getSettings().setSupportZoom(true);
        webdisplayView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webdisplayView.getSettings().setJavaScriptEnabled(true);
        webdisplayView.getSettings().setAllowFileAccess(true);
        webdisplayView.getSettings().setDomStorageEnabled(true);
        webdisplayView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        progress = ProgressDialog.show(this, getString(R.string.progress_loading_title), getString(R.string.progress_loading_msg));
    }



    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }



        @Override
        public void onPageFinished(WebView view, String url) {

            if (progress != null && progress.isShowing()) {
                progress.dismiss();
                progress = null;
            }

            if(url.contains("http://mashaueer.com/mishwar/index.php/home/success")){
                Toast.makeText(PassengerPaymentActivity.this, "Payment has done.", Toast.LENGTH_LONG).show();
                scrollView.setVisibility(View.VISIBLE);
                webdisplayView.setVisibility(View.GONE);

                showRatingDialog();

            }else if(url.contains("http://mashaueer.com/mishwar/index.php/home/cancel")){
                Toast.makeText(PassengerPaymentActivity.this, "Payment has not done. due to network issue.", Toast.LENGTH_LONG).show();
                scrollView.setVisibility(View.VISIBLE);
                webdisplayView.setVisibility(View.GONE);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //PoemDisplayFragment.this.progress.setProgress(0);
            webdisplayView.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            super.onPageStarted(view, url, favicon);
        }
    }

    public void showRatingDialog(){

        dialogRating = new Dialog(PassengerPaymentActivity.this,android.R.style.Theme_Light);
        dialogRating.setCanceledOnTouchOutside(true);
        dialogRating.setCancelable(true);
        dialogRating.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRating.setContentView(R.layout.dialog_layout_rating);
        dialogRating.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window=dialogRating.getWindow();
        window.setGravity(Gravity.CENTER);
        RatingCount = "0";
        final Button  btnRatingSubmit = (Button) dialogRating.findViewById(R.id.btnRatingSubmit);
        final EditText etReview = (EditText) dialogRating.findViewById(R.id.etReview);

        ratingBar = (RatingBar) dialogRating.findViewById(R.id.ratingBar);

        //if rating is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                RatingCount = String.valueOf(rating);
            }
        });

        btnRatingSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Review = etReview.getText().toString();
                if (!RatingCount.equals("")){
                    if (AppUtility.isNetworkAvailable(PassengerPaymentActivity.this)) {
                        sendRatingToServer();
                    } else {
                        AppUtility.showAlertDialog_SingleButton(PassengerPaymentActivity.this, getString(R.string.network_not_available), "", "Ok");
                    }
                }
            }
        });
        dialogRating.show();

    }

    /*********** onBackpress *******************/

    @Override
    public void onBackPressed() {
        dialogRating.cancel();
        Intent i2 = new Intent(PassengerPaymentActivity.this, HomeActivity.class);
        i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(i2);
    }
    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    //**************************************************************************************************************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constant.PERMISSION_READ_PHONE_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                } else {
                    Toast.makeText(this,"permission denied !",Toast.LENGTH_SHORT).show();                }
                return;
            }

        }
    }
    /****************************************************************************************************************************************/

    public void UpdatePaymentInfo() {
        customDialog = new CustomDialog(PassengerPaymentActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_PASSENGER_UPDATE_PAYMENT_ON_SERVER,
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
                                AppUtility.showToast(PassengerPaymentActivity.this,message,1);

                                showRatingDialog();
                               /* Intent i = new Intent(PassengerPaymentActivity.this, HomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                                startActivity(i);*/

                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(PassengerPaymentActivity.this,"server error occured, please try again later!","","Ok");
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(PassengerPaymentActivity.this,message,"","Ok");
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
                        Toast.makeText(PassengerPaymentActivity.this, getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("paymentId", paymentId);
                header.put("paymenType", paymenType);
                // header.put("paypalId", payKey);
                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                SessionManager  sessionManager = new SessionManager(PassengerPaymentActivity.this);
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(PassengerPaymentActivity.this);
        requestQueue.add(stringRequest);
    }

    public void sendRatingToServer() {
        customDialog = new CustomDialog(PassengerPaymentActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_DRIVER_RATING,
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
                                dialogRating.cancel();
                                AppUtility.showToast(PassengerPaymentActivity.this,message,1);
                                Intent i = new Intent(PassengerPaymentActivity.this, HomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                                startActivity(i);

                            }
                            else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton(PassengerPaymentActivity.this,"server error occured, please try again later!","","Ok");
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(PassengerPaymentActivity.this,message,"","Ok");
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
                        Toast.makeText(PassengerPaymentActivity.this, getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("rate",RatingCount );
                header.put("review", Review);
                header.put("driverId", driverId);
                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                SessionManager  sessionManager = new SessionManager(PassengerPaymentActivity.this);
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(PassengerPaymentActivity.this);
        requestQueue.add(stringRequest);
    }

}
