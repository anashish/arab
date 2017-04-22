package com.mishwar.driver_ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mishwar.R;
import com.mishwar.adapter.DriverVehicleTypeAdapter;
import com.mishwar.adapter.VehicleFeaturesAdapter;
import com.mishwar.helper.CircleTransform;
import com.mishwar.helper.ConnectionDetector;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.model.VehichleTypeBean;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VehicleInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private SessionManager sessionManager;
    private  TextView driver_actionbarLayout_title,tvVIProfileName,VI_tvVehicleType,VI_tvVehicleFeatures;
    private   ImageView driver_actionbar_btton_back,iv_VI_profilePic;
    private Button btnUpdateVF;
    private ArrayList<VehichleTypeBean> vehichleFeaturesArraylist = new ArrayList<VehichleTypeBean>();
    private ArrayList<VehichleTypeBean> vehichleTypeArraylist = new ArrayList<VehichleTypeBean>();
    private Set<String> features = new HashSet<>();
    private Set<String> featuresId = new HashSet<>();
    VehicleFeaturesAdapter vehichleFeaturesAdapter;
    DriverVehicleTypeAdapter vehichleTypeAdapter;
    private ArrayList<String> arraySelected= new ArrayList();
    private ArrayList<String> arraySelectedid= new ArrayList();
    String  selectedOptions="",selectedOptionsId="",sVehicleType;
    private GetVehichleTypeTask mAuthGetVehichleTypeTask;
    private GetVehichleInfoTask mAuthGetVehichleInfoTask;
    private GetVehichleFeaturesTask mAuthGetVehichleFeaturesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info);
        sessionManager = new SessionManager(VehicleInfoActivity.this);
        initializeViews();
        if(ConnectionDetector.isNetworkAvailable(VehicleInfoActivity.this)){

            if(mAuthGetVehichleTypeTask==null){
                mAuthGetVehichleTypeTask = new GetVehichleTypeTask();
                mAuthGetVehichleTypeTask.execute();
            }
        }
        else {
            AppUtility.showToast(VehicleInfoActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
        }
    }
    /*-----------------------------------------------------------------------------------------------------------------------*/
    private void initializeViews() {
        vehichleFeaturesAdapter = new VehicleFeaturesAdapter(VehicleInfoActivity.this, vehichleFeaturesArraylist);
        vehichleTypeAdapter = new DriverVehicleTypeAdapter(VehicleInfoActivity.this, vehichleTypeArraylist);

        driver_actionbar_btton_back = (ImageView) findViewById(R.id.driver_actionbar_btton_back);
        iv_VI_profilePic = (ImageView) findViewById(R.id.iv_VI_profilePic);

        btnUpdateVF = (Button) findViewById(R.id.btnUpdateVF);

        driver_actionbarLayout_title = (TextView) findViewById(R.id.driver_actionbarLayout_title);
        tvVIProfileName = (TextView) findViewById(R.id.tvVIProfileName);
        VI_tvVehicleType = (TextView) findViewById(R.id.VI_tvVehicleType);
        VI_tvVehicleFeatures = (TextView) findViewById(R.id.VI_tvVehicleFeatures);

        Glide.with(this).load(sessionManager.getProfileImage()).placeholder(R.drawable.person).transform(new CircleTransform(this)).override(100, 100).into(iv_VI_profilePic);
        tvVIProfileName.setText(sessionManager.getFullName().toString());
        VI_tvVehicleType.setText(sessionManager.getVehicleTypeName().toString());

        String vehicleFeatureName = sessionManager.getVehicleFeatures().toString();

        if (vehicleFeatureName.equals("")){
            VI_tvVehicleFeatures.setText("Vehicle Features");
        }else {
            VI_tvVehicleFeatures.setText(vehicleFeatureName);
        }

        driver_actionbarLayout_title.setText(getString(R.string.text_vehicle_info));

        driver_actionbar_btton_back.setOnClickListener(this);
        VI_tvVehicleType.setOnClickListener(this);
        VI_tvVehicleFeatures.setOnClickListener(this);
        btnUpdateVF.setOnClickListener(this);

        if(ConnectionDetector.isNetworkAvailable(VehicleInfoActivity.this)){
            // new GetVehichleFeaturesTask().execute();
            if(mAuthGetVehichleFeaturesTask==null){
                mAuthGetVehichleFeaturesTask = new GetVehichleFeaturesTask();
                mAuthGetVehichleFeaturesTask.execute();
            }
        }
        else {
            AppUtility.showToast(VehicleInfoActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
        }
    }
    /*-------------------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*Action perform for tripmothly*/
            case R.id.driver_actionbar_btton_back:
                onBackPressed();
                break;

            case R.id.VI_tvVehicleType:

                View vehicleType_dialogView = View.inflate(VehicleInfoActivity.this, R.layout.list_dialog_layout, null);

                final Dialog vehicleTypeDialog = new Dialog(VehicleInfoActivity.this, android.R.style.Theme_Light);
                vehicleTypeDialog.setCanceledOnTouchOutside(true);
                vehicleTypeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                vehicleTypeDialog.setContentView(vehicleType_dialogView);
                vehicleTypeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // final String[] carListItemArray = {"Other","Micro","Mini","Sedan","VIP Car"};
                // ArrayAdapter VTadapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, carListItemArray);

                ListView dialogCarlistview = (ListView) vehicleType_dialogView.findViewById(R.id.dialog_listview);

                dialogCarlistview.setAdapter(vehichleTypeAdapter);

                dialogCarlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        VehichleTypeBean vehichleType = vehichleTypeArraylist.get(position);

                        vehicleTypeDialog.dismiss();
                        //  sVehicleType = String.valueOf(position);
                        sVehicleType = vehichleType.getCarName().toString();
                        String sVehicleTypeId = vehichleType.getCarId().toString();
                        VI_tvVehicleType.setText(sVehicleType);

                        sessionManager.setVehicleTypeName(sVehicleType);
                        sessionManager.setVihicleTypeId(sVehicleTypeId);

                    }
                });

                vehicleTypeDialog.show();
                break;

            case R.id.VI_tvVehicleFeatures:
                final RelativeLayout lyVehicleFeatures = (RelativeLayout)findViewById(R.id.lyVehicleFeatures);

                if (!vehichleFeaturesAdapter.isEmpty()){
                    lyVehicleFeatures.setVisibility(View.VISIBLE);
                    btnUpdateVF.setVisibility(View.VISIBLE);
                    selectedOptions="";selectedOptionsId="";

                    arraySelectedid.clear();
                    arraySelected.clear();

                }else {
                    AppUtility.showToast(VehicleInfoActivity.this, "No features available.", Toast.LENGTH_SHORT);
                    lyVehicleFeatures.setVisibility(View.GONE);
                }
                ListView dialogVFlistview = (ListView) findViewById(R.id.dialog_listview);
                Button btnDone = (Button) findViewById(R.id.btnDone);

                dialogVFlistview.setAdapter(vehichleFeaturesAdapter);

                dialogVFlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        VehichleTypeBean vehichleTypeBean = vehichleFeaturesArraylist.get(position);
/*
                        for (int i = 0; i < vehichleFeaturesArraylist.size(); i++) {
                            arraySelected.add(vehichleFeaturesArraylist.get(i).getVehicleFacilityName());
                            arraySelectedid.add(vehichleFeaturesArraylist.get(i).getVehicleFacilityId());
                        }*/
                        arraySelected.add(vehichleTypeBean.getVehicleFacilityName());

                        arraySelectedid.add(vehichleTypeBean.getVehicleFacilityId());

                        arraySelected.removeAll(Arrays.asList(null, ""));
                        selectedOptions = arraySelected.toString().replace("[","");
                        selectedOptions = selectedOptions.replace("]", "");
                        VI_tvVehicleFeatures.setText(selectedOptions);

                        arraySelectedid.removeAll(Arrays.asList(null, ""));
                        selectedOptionsId = arraySelectedid.toString().replace("[", "");
                        selectedOptionsId = selectedOptionsId.replace("]", "");

                        //   features.addAll(arraySelected);
                        // selectedOptionsId =  featuresId.addAll(arraySelectedid);
                        System.out.println("selectedOptions : "+selectedOptions +"selectedOptionsId : "+selectedOptionsId);
                        vehichleFeaturesArraylist.remove(position);
                        vehichleFeaturesAdapter.notifyDataSetChanged();
                    }
                });
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lyVehicleFeatures.setVisibility(View.GONE);
                    }
                });

                break;
            case R.id.btnUpdateVF:
                if(ConnectionDetector.isNetworkAvailable(VehicleInfoActivity.this)){
                    // new GetVehichleInfoTask().execute();
                    // if(mAuthGetVehichleInfoTask==null){
                    mAuthGetVehichleInfoTask = new GetVehichleInfoTask();
                    mAuthGetVehichleInfoTask.execute();

                    String features = VI_tvVehicleFeatures.getText().toString();
                    sessionManager.setVihicleFeatures(features);

                    // }
                }
                else {
                    AppUtility.showToast(VehicleInfoActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
                }
                break;
        }
    }
    /*---------------------------------------------------------------------------------------------------*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_out, R.anim.slide_out);
        finish();
    }

    /*-------------------------------------------------------------------------------------------------------------------*/
    public class GetVehichleFeaturesTask extends AsyncTask<Void, Void, Boolean> {
        JSONObject jsonObj;
        String rStatus;
        CustomDialog customDialog;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            customDialog = new CustomDialog(VehicleInfoActivity.this);
            //     if(isShown)
            customDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            String jsonStr=null;
            try{
                ServiceHandler sh = new ServiceHandler();
                List<NameValuePair> list=new ArrayList<>();

                list.add(new BasicNameValuePair(Constant.AUTHTOKEN, sessionManager.getAuthToken()));
                jsonStr = sh.makeServiceCall(Constant.URL_VEHICLE_FEATURES, ServiceHandler.GET, list);

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
                }else if (sh.getResponceCode()==300){
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(VehicleInfoActivity.this,"Something went wrong, please try again !","","Ok");
                }


            }catch (Exception e){
                e.printStackTrace();
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

                    vehichleFeaturesArraylist.clear();

                    JSONArray jsonArray = jsonObj.getJSONArray("facility");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        VehichleTypeBean item = new VehichleTypeBean();

                        item.setVehicleFacilityId(jsonObject.getString("facilityId").toString().trim());
                        item.setVehicleFacilityName(jsonObject.getString("facilityName").toString().trim());


                        vehichleFeaturesArraylist.add(item);
                    }
                    vehichleFeaturesAdapter.notifyDataSetChanged();

                    //     mainLayout.setVisibility(View.VISIBLE);
                    // relativeLayout_emptyScreen.setVisibility(View.GONE);

                }else if(response.isEmpty())
                {
                    AppUtility.showAlertDialog_SingleButton_finishActivity(VehicleInfoActivity.this,"Error occured, please try again !","","Ok");
                }
                else if (message.equalsIgnoreCase("Invalid Auth Token"))
                {
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(VehicleInfoActivity.this,"Something went wrong, please try again!","","Ok");
                }
                else {
//                AppUtility.showAlertDialog_SingleButton(Ho.this,"There is no any item related to..","","Ok");

                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }

        @Override
        protected void onCancelled() {
            customDialog = null;
            customDialog.dismiss();
        }
    }
    /*-------------------------------------------------------------------------------------------------------------------*/
    public class GetVehichleInfoTask extends AsyncTask<Void, Void, Boolean> {
        JSONObject jsonObj;
        String rStatus;
        CustomDialog customDialog;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            customDialog = new CustomDialog(VehicleInfoActivity.this);
            //    if(isShown)
            customDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            String jsonStr=null;
            try{
                ServiceHandler sh = new ServiceHandler();
                List<NameValuePair> list=new ArrayList<>();
                String sVehicleId = sessionManager.getVihicleTypeID().toString();

                list.add(new BasicNameValuePair(Constant.AUTHTOKEN, sessionManager.getAuthToken()));
                list.add(new BasicNameValuePair("vihicleType", sVehicleId));
                //   list.add(new BasicNameValuePair("facilityName", selectedOptions));
                list.add(new BasicNameValuePair("carFacility", selectedOptionsId));

                jsonStr = sh.makeServiceCall(Constant.URL_UPDATE_VEHICLE_INFO, ServiceHandler.POST_ENTITY, list);

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
                }else if (sh.getResponceCode()==300){
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(VehicleInfoActivity.this,"Something went wrong, please try again !","","Ok");
                }


            }catch (Exception e){
                e.printStackTrace();
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
                    AppUtility.showToast(VehicleInfoActivity.this, message, 0);
                    String  sVType = VI_tvVehicleType.getText().toString();
                    sessionManager.setVihicleFeatures(selectedOptions);
                    sessionManager.setVehicleTypeName(sVType);
                    sessionManager.setVihicleTypeId(selectedOptionsId);

                    //   VI_tvVehicleFeatures.setText(sessionManager.getVehicleFeatures().toString());
                    //     mainLayout.setVisibility(View.VISIBLE);
                    // relativeLayout_emptyScreen.setVisibility(View.GONE);

                }else if(response.isEmpty())
                {
                    AppUtility.showAlertDialog_SingleButton_finishActivity(VehicleInfoActivity.this,"Error occured, please try again !","","Ok");
                }
                else if (message.equalsIgnoreCase("Invalid Auth Token"))
                {
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(VehicleInfoActivity.this,"Something went wrong, please try again!","","Ok");
                }
                else {
//                AppUtility.showAlertDialog_SingleButton(Ho.this,"There is no any item related to..","","Ok");

                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }

        @Override
        protected void onCancelled() {
            customDialog = null;
            customDialog.dismiss();
        }
    }
    /*---------------------------------------------------------------------------------------------------------------------------------*/

    public class GetVehichleTypeTask extends AsyncTask<Void, Void, Boolean> {
        JSONObject jsonObj;
        String rStatus;

        CustomDialog customDialog;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            customDialog = new CustomDialog(VehicleInfoActivity.this);
            //        if(isShown)
            customDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            String jsonStr=null;
            try{
                ServiceHandler sh = new ServiceHandler();
                List<NameValuePair> list=new ArrayList<>();
                System.out.println("saved authTokan := " + sessionManager.getAuthToken());

                list.add(new BasicNameValuePair(Constant.AUTHTOKEN, sessionManager.getAuthToken()));
                jsonStr = sh.makeServiceCall(Constant.URL_VEHICHAL_TYPE, ServiceHandler.GET, list);

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
                }else if (sh.getResponceCode()==300){
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(VehicleInfoActivity.this,"Something went wrong, please try again !","","Ok");
                }


            }catch (Exception e){
                e.printStackTrace();
                try{
                    if(e instanceof SocketTimeoutException) {
                        throw new SocketTimeoutException();
                    }
                } catch (SocketTimeoutException f){

                    AppUtility.showAlertDialog_SingleButton(VehicleInfoActivity.this,"Time out !, please try again !","","Ok");
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

                    vehichleTypeArraylist.clear();

                    JSONArray jsonArray = jsonObj.getJSONArray("carDetail");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        VehichleTypeBean item = new VehichleTypeBean();

                        item.setCarId(jsonObject.getString("carId").toString().trim());
                        item.setCarName(jsonObject.getString("carName").toString().trim());

                        vehichleTypeArraylist.add(item);

                        System.out.println("Vehichle type" + vehichleTypeArraylist);

                    }
                    vehichleTypeAdapter.notifyDataSetChanged();



                }else if(response.isEmpty())
                {
                    AppUtility.showAlertDialog_SingleButton_finishActivity(VehicleInfoActivity.this,"Error occured, please try again !","","Ok");
                }
                else if (message.equalsIgnoreCase("Invalid Auth Token"))
                {
                    sessionManager.logout();
                    AppUtility.showAlertDialog_SingleButton_finishActivity(VehicleInfoActivity.this,"Something went wrong, please try again!","","Ok");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                try{
                    if(ex instanceof SocketTimeoutException) {
                        throw new SocketTimeoutException();
                    }
                } catch (SocketTimeoutException f){

                    AppUtility.showAlertDialog_SingleButton(VehicleInfoActivity.this,"Time out !, please try again !","","Ok");
                }

            }
        }

        @Override
        protected void onCancelled() {
            customDialog = null;
            customDialog.dismiss();
        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    protected void onResume() {
        super.onResume();

    }
}