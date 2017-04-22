package com.mishwar.driver_ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
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
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.listner.CustomButtonListener;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.DatePickerFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentInfoActivity extends AppCompatActivity implements View.OnClickListener,CustomButtonListener {
    private TextView driver_actionbarLayout_title,tvPaymentInfoTotalEarn,tvPaymentInfoCash,tvPaymentInfoPaypal,tvPaymentInfoDatePicker1,tvPaymentInfoDatePicker2,tvAddAccount;
    private   ImageView actionbar_btton_back;
    private   Button btnPaymentInfoSubmit;
    private boolean btnDatePicker1Clicked = false,btnDatePicker2Clicked = false;
    CustomDialog customDialog;
    private String paypalId = "";
    boolean isPayPalId = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);
        initializeViews();
        if (AppUtility.isNetworkAvailable(PaymentInfoActivity.this))
        {
            GetPaymentInfoFromServer();
        }
        else {
            AppUtility.showAlertDialog_SingleButton(PaymentInfoActivity.this, getString(R.string.network_not_available), "", "Ok");
        }
    }

    private void initializeViews() {
        tvAddAccount = (TextView) findViewById(R.id.tvAddAccount);
        driver_actionbarLayout_title = (TextView) findViewById(R.id.driver_actionbarLayout_title);
        tvPaymentInfoCash = (TextView) findViewById(R.id.tvPaymentInfoCash);
        tvPaymentInfoPaypal = (TextView) findViewById(R.id.tvPaymentInfoPaypal);
        tvPaymentInfoDatePicker1 = (TextView) findViewById(R.id.tvPaymentInfoDatePicker1);
        tvPaymentInfoDatePicker2 = (TextView) findViewById(R.id.tvPaymentInfoDatePicker2);
        tvPaymentInfoTotalEarn = (TextView) findViewById(R.id.tvPaymentInfoTotalEarn);

        actionbar_btton_back = (ImageView) findViewById(R.id.driver_actionbar_btton_back);
        btnPaymentInfoSubmit = (Button) findViewById(R.id.btnPaymentInfoSubmit);
        String paypalId ="";
        SessionManager sessionManager = new SessionManager(PaymentInfoActivity.this);
        paypalId = sessionManager.getPayPalId();
        if (paypalId.equals("") || paypalId.isEmpty()){
            tvAddAccount.setVisibility(View.VISIBLE);
        }else {
            tvAddAccount.setVisibility(View.GONE);
        }

        driver_actionbarLayout_title.setText(getString(R.string.text_payment_info));

        tvAddAccount.setOnClickListener(this);
        tvPaymentInfoDatePicker1.setOnClickListener(this);
        tvPaymentInfoDatePicker2.setOnClickListener(this);
        actionbar_btton_back.setOnClickListener(this);
        btnPaymentInfoSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.driver_actionbar_btton_back:
                onBackPressed();
                break;

            case R.id.btnPaymentInfoSubmit:
                sDatePicker1date = tvPaymentInfoDatePicker1.getText().toString();
                sDatePicker2date = tvPaymentInfoDatePicker2.getText().toString();
                isPayPalId = false;
                if (!sDatePicker1date.equals("")&&!sDatePicker1date.equals(getString(R.string.date_hint)) ){

                    if (!sDatePicker2date.equals("") && !sDatePicker2date.equals(getString(R.string.date_hint))) {
                        if (AppUtility.isNetworkAvailable(PaymentInfoActivity.this))
                        {
                            GetPaymentInfoFromServer();
                        }
                        else {
                            AppUtility.showAlertDialog_SingleButton(PaymentInfoActivity.this, getString(R.string.network_not_available), "", "Ok");
                        }

                    }else {
                        AppUtility.showToast(PaymentInfoActivity.this, getString(R.string.progress_target_date), Toast.LENGTH_SHORT);
                    }
                }else {
                    AppUtility.showToast(PaymentInfoActivity.this, getString(R.string.text_select_date), Toast.LENGTH_SHORT);
                }

                break;

            case R.id.tvPaymentInfoDatePicker1:
                btnDatePicker1Clicked = true;
                btnDatePicker2Clicked = false;

                DatePickerFragment datePickerFragment1 = new DatePickerFragment(Constant.CALENDAR_DAY, false);
                datePickerFragment1.setDateListener(PaymentInfoActivity.this);
                datePickerFragment1.show(getSupportFragmentManager(), "");
                break;

            case R.id.tvPaymentInfoDatePicker2:
                btnDatePicker2Clicked = true;
                btnDatePicker1Clicked = false;
                DatePickerFragment datePickerFragment2 = new DatePickerFragment(Constant.CALENDAR_DAY, false);
                datePickerFragment2.setDateListener(PaymentInfoActivity.this);
                datePickerFragment2.show(getSupportFragmentManager(), "");
                break;

            case R.id.tvAddAccount:
                Intent addAccountInten = new Intent(PaymentInfoActivity.this,AddAccountActivity.class);
                startActivity(addAccountInten);
                overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
                finish();
                // showGetPaypalIdDialog();

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
    }


    public void showGetPaypalIdDialog(){

        final Dialog   dialogGetPaypalId = new Dialog(PaymentInfoActivity.this,android.R.style.Theme_Light);
        dialogGetPaypalId.setCanceledOnTouchOutside(true);
        dialogGetPaypalId.setCancelable(true);
        dialogGetPaypalId.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogGetPaypalId.setContentView(R.layout.activity_add_account);
        dialogGetPaypalId.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window=dialogGetPaypalId.getWindow();
        window.setGravity(Gravity.CENTER);


        dialogGetPaypalId.show();

    }


    private String sDatePicker1date = "",sDatePicker2date = "";
    @Override
    public void onDateSet(int year, int month, int day, int cal_type) {

        if (btnDatePicker1Clicked==true){
            tvPaymentInfoDatePicker1.setText(year + "-" + (month + 1) + "-" + day);
            sDatePicker1date = tvPaymentInfoDatePicker1.getText().toString();

        } else if (btnDatePicker2Clicked==true) {
            tvPaymentInfoDatePicker2.setText(year + "-" + (month + 1) + "-" + day);
            sDatePicker2date = tvPaymentInfoDatePicker2.getText().toString();
        }
    }

    public void GetPaymentInfoFromServer() {

        customDialog = new CustomDialog(PaymentInfoActivity.this);
        customDialog.show();
        String urlParam = "?startData="+sDatePicker1date+"&endDate="+sDatePicker2date+"&paypalId="+paypalId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_DRIVER_PAYMENTINFO+urlParam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("response paymentInfo" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("1")) {
                                JSONObject resultObj = jsonObj.getJSONObject("result");
                                String casePayment = resultObj.getString("casePayment");
                                String paypalPayment = resultObj.getString("paypalPayment");
                                String total = resultObj.getString("total");

                                tvPaymentInfoCash.setText("$ "+casePayment);
                                tvPaymentInfoPaypal.setText("$ "+paypalPayment);
                                tvPaymentInfoTotalEarn.setText( "$ "+total);

                                if (total.equals("0")){
                                    //  if (isPayPalId==false)
                                    AppUtility.showToast(PaymentInfoActivity.this,"No earning found.",1);
                                    tvPaymentInfoDatePicker1.setText(getString(R.string.date_hint));
                                    tvPaymentInfoDatePicker2.setText(getString(R.string.date_hint));
                                }
                            }
                            else
                            {
                                AppUtility.showAlertDialog_SingleButton(PaymentInfoActivity.this,message,"","Ok");
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
                        Toast.makeText(PaymentInfoActivity.this, getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }) {
            /*  @Override
              public Map<String, String> getParams() throws AuthFailureError {
                  Map<String, String> header = new HashMap<>();
                  header.put("startDate", DatePicker1date);
                  header.put("endDate", DatePicker2date);
                  return header;
              }*/
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                SessionManager sessionManager = new SessionManager(PaymentInfoActivity.this);
                String AuthToken = sessionManager.getAuthToken().toString();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }else {
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(PaymentInfoActivity.this,"Already logged in on another device","","Ok");
                    //Toast.makeText(HomeActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /****************************************************************************************************************************************/


}
