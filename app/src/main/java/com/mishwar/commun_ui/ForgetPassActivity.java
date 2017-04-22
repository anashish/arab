package com.mishwar.commun_ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mishwar.R;
import com.mishwar.helper.ConnectionDetector;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class ForgetPassActivity extends AppCompatActivity {
    EditText etForgetPass;
    Button btn_submit_forgetpass;
    private ImageView actionbar_btton_back;
    private TextView actionbarLayout_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        etForgetPass = (EditText)findViewById(R.id.etForgetPass);
        btn_submit_forgetpass = (Button)findViewById(R.id.btn_submit_forgetpass);
        actionbarLayout_title = (TextView) findViewById(R.id.driver_actionbarLayout_title);

        actionbar_btton_back = (ImageView) findViewById(R.id.driver_actionbar_btton_back);

        actionbarLayout_title.setText(getString(R.string.text_forgot_pass));

        actionbar_btton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_submit_forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sEmail = etForgetPass.getText().toString().trim();
                if(!sEmail.equals("")&& Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()){

                    if(ConnectionDetector.isNetworkAvailable(ForgetPassActivity.this)){
                        new GetForgotPassTask().execute();
                    }
                    else {
                        AppUtility.showToast(ForgetPassActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
                    }

                }else {
                    AppUtility.showToast(ForgetPassActivity.this,"Please nnter valid email address !",1);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    /*-------------------------------------------------------------------------------------------------------------------*/
    public class GetForgotPassTask extends AsyncTask<Void, Void, Boolean> {
        JSONObject jsonObj;
        String rStatus;
        String sEmail = etForgetPass.getText().toString().trim();
        CustomDialog customDialog;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            customDialog = new CustomDialog(ForgetPassActivity.this);
            customDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            String jsonStr=null;
            try{
                ServiceHandler sh = new ServiceHandler();
                List<NameValuePair> list=new ArrayList<>();
                //  System.out.println("saved authTokan := "+sessionManager.getAuthToken());

                list.add(new BasicNameValuePair("email", sEmail));
                jsonStr = sh.makeServiceCall(Constant.URL_FORGET_PASS, ServiceHandler.POST_ENTITY, list);

                if (sh.getResponceCode()==200) {

                    if (jsonStr != null) {
                        try {
                            jsonObj = new JSONObject(jsonStr);
                            rStatus = jsonObj.getString("message");
                            return true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.e("ServiceHandler", "Couldn't get any data from the url");
                    }
                }


            }catch (Exception e){
                e.printStackTrace();
                try{
                    if(e instanceof SocketTimeoutException) {
                        throw new SocketTimeoutException();
                    }
                } catch (SocketTimeoutException f){

                    AppUtility.showAlertDialog_SingleButton(ForgetPassActivity.this,"Time out !, please try again !","","Ok");
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            customDialog.dismiss();

            try {
                //  arraylist.clear();
                String response = jsonObj.getString("status");
                String message = jsonObj.getString("message");

                if (response.equalsIgnoreCase("1")) {

                    AppUtility.showAlertDialog_SingleButton_finishActivity(ForgetPassActivity.this,message,"","Ok");


                }else if(response.isEmpty())
                {
                    AppUtility.showAlertDialog_SingleButton_finishActivity(ForgetPassActivity.this,message,"","Ok");
                }
                else {
                    AppUtility.showToast(ForgetPassActivity.this,message,0);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                try{
                    if(ex instanceof SocketTimeoutException) {
                        throw new SocketTimeoutException();
                    }
                } catch (SocketTimeoutException f){

                    AppUtility.showAlertDialog_SingleButton(ForgetPassActivity.this,"Time out !, please try again !","","Ok");
                }

            }
        }

        @Override
        protected void onCancelled() {
            customDialog = null;
            customDialog.dismiss();
        }
    }

    /*----------------------------------------------------------------------------------*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}
