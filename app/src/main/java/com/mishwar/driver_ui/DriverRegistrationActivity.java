package com.mishwar.driver_ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mishwar.R;
import com.mishwar.adapter.DriverVehicleTypeAdapter;
import com.mishwar.helper.ConnectionDetector;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.listner.CustomButtonListener;
import com.mishwar.model.VehichleTypeBean;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.DatePickerFragment;
import com.mishwar.utils.ImageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import vollyemultipart.AppHelper;
import vollyemultipart.VolleyMultipartRequest;
import vollyemultipart.VolleySingleton;

public class DriverRegistrationActivity extends AppCompatActivity implements CustomButtonListener {

    private static final String TAG = DriverRegistrationActivity.class.getSimpleName();
    static final int REQUEST_CAMERA = 0;
    static final int SELECT_FILE = 1;
    CustomDialog customDialog;
    private ImageView iv_back,iv_editImage,iv_checkBox;
    private EditText ed_userName,ed_fullName,ed_email,ed_password,ed_vehicleNo,ed_LicenceNo,ed_LicenceExpDate,reg_ed_add_identity;
    private Button b_signUp;
    private Spinner spn_gender,spn_vehicleType;
    private SessionManager sessionManager;
    private CircleImageView iv_profilePic;

    int mYear,mMonth,mDay,userId;
    private String sFullName,sEmail,sPassword,sVehicleNo,sLicenceNo,sIdentityProof,sLicenceExpDate,sGender="male",VehicleTypeName,sLoginSatus,sSocialId,sUserProfile,sAuthToken,sUserType= "driver",
            sUserName,mDeviceToken="", mDeviceType="1",mobileNumber="",sIdentityProofStatus,sLicenceNumberStatus,sLicenceExDate,sVehicleTypeId;
    private boolean termsAccepted = false,isIdentityProof = false;
    private ArrayList<VehichleTypeBean> vehichleTypeArraylist = new ArrayList<VehichleTypeBean>();
    private DriverVehicleTypeAdapter vehichleTypeAdapter;
    Bitmap profileImageBitmap,IdProofImageBitmap,ImageBitmap;
    private EditText mEditTextCaptionNumber;
    private EditText mEditTextDateOfBirth;
    private String mCaptionIdentityNumber;
    private String mDriverDateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        sessionManager = new SessionManager(DriverRegistrationActivity.this);
        mDeviceToken = FirebaseInstanceId.getInstance().getToken();

/*-----------------------------------------------------------------------------------------------------------------------------------*/

        if(ConnectionDetector.isNetworkAvailable(DriverRegistrationActivity.this)){
            //  new GetVehichleTypeTask().execute();
            GetVehicleType();
        }
        else {
            AppUtility.showToast(DriverRegistrationActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
        }

/*-----------------------------------------------------------------------------------------------------------------------------------*/
        Typeface RobotoLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        vehichleTypeAdapter = new DriverVehicleTypeAdapter(DriverRegistrationActivity.this, vehichleTypeArraylist);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        iv_back = (ImageView)findViewById(R.id.reg_iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
            }
        });

        iv_profilePic = (CircleImageView)findViewById(R.id.reg_iv_driver);
        iv_profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIdentityProof = false;

                    selectImage();

            }
        });

        iv_editImage = (ImageView)findViewById(R.id.reg_iv_cam);
        iv_editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIdentityProof = false;
                    selectImage();

            }
        });
        reg_ed_add_identity = (EditText)findViewById(R.id.reg_ed_add_identity);

        reg_ed_add_identity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIdentityProof = true;
                    selectImage();

            }
        });


        iv_checkBox = (ImageView)findViewById(R.id.reg_term_accepted);
        iv_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!termsAccepted){
                    termsAccepted = true;
                    // iv_checkBox.setSelected(true);
                    iv_checkBox.setImageDrawable(getResources().getDrawable(R.drawable.icon_with_tick));
                    Log.d(TAG,"terms true");
                }
                else {
                    termsAccepted = false;
                    //iv_checkBox.setSelected(false);
                    iv_checkBox.setImageDrawable(getResources().getDrawable(R.drawable.icon_without_tick));
                    Log.d(TAG,"terms false");
                }
            }
        });

        mobileNumber= getIntent().getStringExtra("dMobileNo");

        ed_userName = (EditText)findViewById(R.id.reg_ed_username);
        ed_userName.setTypeface(RobotoLight);
        ed_fullName = (EditText) findViewById(R.id.reg_ed_fullname);
        ed_fullName.setTypeface(RobotoLight);
        ed_email = (EditText)findViewById(R.id.reg_ed_email);
        ed_email.setTypeface(RobotoLight);
        ed_password = (EditText)findViewById(R.id.reg_ed_password);
        ed_password.setTypeface(RobotoLight);
        ed_vehicleNo = (EditText)findViewById(R.id.reg_ed_vehicle_no);
        ed_vehicleNo.setTypeface(RobotoLight);
        ed_LicenceNo = (EditText)findViewById(R.id.reg_ed_licence_no);
        ed_LicenceNo.setTypeface(RobotoLight);
        ed_LicenceExpDate = (EditText)findViewById(R.id.reg_ed_licence_exp_date);
        ed_LicenceExpDate.setTypeface(RobotoLight);

        mEditTextDateOfBirth = (EditText)findViewById(R.id.reg_ed_dob);
        mEditTextDateOfBirth.setTypeface(RobotoLight);
        mEditTextCaptionNumber= (EditText)findViewById(R.id.reg_ed_caption_identitiy_number);
        mEditTextCaptionNumber.setTypeface(RobotoLight);

        ed_LicenceExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerFragment datePickerFragment = new DatePickerFragment(Constant.CALENDAR_DAY, true);
                datePickerFragment.setDateListener(DriverRegistrationActivity.this);
                datePickerFragment.show(getSupportFragmentManager(), "");

                String date = ed_LicenceExpDate.getText().toString();

            }
        });
        mEditTextDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(DriverRegistrationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        mEditTextDateOfBirth.setText(ImageUtil.timeFormatter(newDate.getTimeInMillis(), "dd-MM-yyyy"));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        TextView tv_terms = (TextView)findViewById(R.id.reg_tv_terms);

        spn_gender = (Spinner)findViewById(R.id.reg_spn_gender);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(DriverRegistrationActivity.this,R.layout.item_country_spinner,getResources().getStringArray(R.array.genders));
        spn_gender.setAdapter(genderAdapter);
        spn_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sGender = getResources().getStringArray(R.array.genders)[position] ;
                sGender =  spn_gender.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spn_vehicleType = (Spinner)findViewById(R.id.reg_spn_vehicle_type);
        ArrayAdapter<String> vehicleAdapter = new ArrayAdapter<String>(DriverRegistrationActivity.this,R.layout.item_country_spinner,getResources().getStringArray(R.array.vehicle_type));
        spn_vehicleType.setAdapter(vehichleTypeAdapter);

        // Listener called when spinner item selected
        spn_vehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                // Get selected row data to show on screen
                VehichleTypeBean vehichleType = vehichleTypeArraylist.get(position);
                //  sVehicleType = String.valueOf(position);
                VehicleTypeName = vehichleType.getCarName().toString();
                sVehicleTypeId = vehichleType.getCarId().toString();

                sessionManager.setVehicleTypeName(VehicleTypeName);
                sessionManager.setVihicleTypeId(sVehicleTypeId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        b_signUp = (Button)findViewById(R.id.reg_b_signup);
        b_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = false;
                sUserName = ed_userName.getText().toString().trim();
                sFullName = ed_fullName.getText().toString().trim();
                sEmail = ed_email.getText().toString().trim();
                sPassword = ed_password.getText().toString().trim();
                sVehicleNo = ed_vehicleNo.getText().toString().trim();
                sLicenceNo = ed_LicenceNo.getText().toString().trim();
                sLicenceExpDate = ed_LicenceExpDate.getText().toString().trim();
                mCaptionIdentityNumber = mEditTextCaptionNumber.getText().toString().trim();
                mDriverDateOfBirth= mEditTextDateOfBirth.getText().toString().trim();
                mDeviceToken = sessionManager.getDeviceToken();

             /*   if(mImagePath==null){
                    cancel = true;
                    AppUtility.showToast(DriverRegistrationActivity.this, "Please select profile image", Toast.LENGTH_SHORT);

                }*/
                if(IdProofImageBitmap==null){
                    cancel = true;
                    AppUtility.showToast(DriverRegistrationActivity.this, "Please select vehicle identity proof image", Toast.LENGTH_SHORT);

                }
                else if(sUserName.isEmpty()){
                    cancel =true;
                    ed_userName.setError(getString(R.string.please_enter_fullname));
                }
                else if(sFullName.isEmpty()){
                    cancel =true;
                    ed_fullName.setError(getString(R.string.please_enter_fullname));
                }
                else if(sEmail.isEmpty()){
                    cancel =true;
                    ed_email.setError(getString(R.string.please_enter_your_email));
                }

                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()){
                    cancel = true;
                    ed_email.setError(getString(R.string.please_enter_a_valid_email));
                }
                else if (sPassword.isEmpty()){
                    cancel = true;
                    ed_password.setError(getString(R.string.please_enter_your_password));
                }
                else if (sPassword.length()<=5){
                    cancel = true;
                    ed_password.setError("Please enter minimum 6 digit password");
                }
                else if (sVehicleNo.isEmpty()){
                    cancel =true;
                    ed_vehicleNo.setError("Please enter your vehicle number");
                }
                else if(sLicenceNo.isEmpty()){
                    cancel =true;
                    ed_LicenceNo.setError("Please enter your Licence number");
                }
                else if (sLicenceExpDate.isEmpty()){
                    cancel =true;
                    ed_LicenceExpDate.setError("Please enter your Licence expiry date");
                }
                else if(sGender.isEmpty()){
                    cancel =true;
                    AppUtility.showToast(DriverRegistrationActivity.this, "Please select a gender type", Toast.LENGTH_SHORT);
                }
                else if(VehicleTypeName.isEmpty()){
                    cancel = true;
                    AppUtility.showToast(DriverRegistrationActivity.this, getString(R.string.please_select_vehicle_type), Toast.LENGTH_SHORT);
                }else if(mCaptionIdentityNumber.isEmpty()){
                    cancel = true;
                    AppUtility.showToast(DriverRegistrationActivity.this, getString(R.string.caption_identity_msg), Toast.LENGTH_SHORT);
                }
                else if(mDriverDateOfBirth.isEmpty()){
                    cancel = true;
                    AppUtility.showToast(DriverRegistrationActivity.this, getString(R.string.dob_msg), Toast.LENGTH_SHORT);
                }
                else if(!termsAccepted){
                    cancel =true;
                    AppUtility.showToast(DriverRegistrationActivity.this, getString(R.string.please_accept_terms_and_cond), Toast.LENGTH_SHORT);
                }
                if(!cancel){
                    // call signup api
                    //     AppUtility.showToast(DriverRegistrationActivity.this, "sign up clicked", Toast.LENGTH_SHORT);
                    if(ConnectionDetector.isNetworkAvailable(DriverRegistrationActivity.this)){
                        //  mAuthTask = nsPaymentTypeew UserSignUpTask();
                        //mAuthTask.execute(Constant.URL_SIGNUP_DRIVER);

                        GetDriverRegistrationTask();
                    }
                    else {
                        AppUtility.showToast(DriverRegistrationActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);

                    }
                }
            }
        });

    }


    /**
     * This API used to send driver info to manasah wasl api
     */

    private void sendDriverRegistrationInfo_To_Manasah_Wasl_API() {

            StringRequest sendInfo = new StringRequest(Request.Method.POST,  Constant.URL_DRIVER_REG_INFO_MANASAH_WASL_API, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    AppUtility.log_DEBUG("RESPONSE ",response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AppUtility.log_DEBUG("Response ",error.getMessage());

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("apiKey", Constant.MANASAH_WASL_API_KEY);
                    params.put("emailAddress", sEmail);
                    params.put("mobileNumber", mobileNumber);

                    return params;
                }
            };

            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(sendInfo);


    }

    /*-------------------------------------------------------------------------------------------------------------------*/

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from gallery", "Cancel"};
        AlertDialog.Builder alert = new AlertDialog.Builder(DriverRegistrationActivity.this);
        alert.setTitle("Add Photo");
        alert.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    if(Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(
                                    new String[]{Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    Constant.MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                        else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, Constant.REQUEST_CAMERA);
                        }
                    }else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, Constant.REQUEST_CAMERA);
                    }


                } else if (items[item].equals("Choose from gallery")) {

                    if(Build.VERSION.SDK_INT >= 23){

                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, Constant.SELECT_FILE);
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, Constant.SELECT_FILE);
                    }


                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        alert.show();
    }
/*-----------------------------------------------------------------------------------------------------------------*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                ImageBitmap = (Bitmap) data.getExtras().get("data");
                //profileImageBitmap = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                if(isIdentityProof == true){
                    IdProofImageBitmap = ImageBitmap;
                    reg_ed_add_identity.setText("Attached");

                }else {
                    profileImageBitmap = ImageBitmap;
                }

                if(isIdentityProof == false) {
                    iv_profilePic.setImageBitmap(profileImageBitmap);
                }


            } else if (requestCode == SELECT_FILE) {

                Uri selectedImageUri = data.getData();

                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);

                ImageBitmap = ImageUtil.decodeFile(selectedImagePath);
                try {
                    ImageBitmap = ImageUtil.modifyOrientation(ImageBitmap, selectedImagePath);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    ImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    if(isIdentityProof == true){
                        IdProofImageBitmap = ImageBitmap;
                        reg_ed_add_identity.setText("Attached");

                    }else {
                        profileImageBitmap = ImageBitmap;
                    }

                    if(isIdentityProof == false) {
                        iv_profilePic.setImageBitmap(profileImageBitmap);
                    }
                    // iv_profilePic.setImageBitmap(profileImageBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onBackPressed() {
        android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(DriverRegistrationActivity.this);
        builder1.setMessage("Do you want to exit ?");
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
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        android.support.v7.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }
/*-------------------------------------------------------------------------------------------------------------------*/
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                       @NonNull int[] grantResults) {
    switch (requestCode) {

        case Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constant.SELECT_FILE);
            } else {
                Toast.makeText(DriverRegistrationActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
            }
        }break;

        case  Constant.MY_PERMISSIONS_REQUEST_CAMERA:
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, Constant.REQUEST_CAMERA);
            } else {
                Toast.makeText(DriverRegistrationActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
            }
        } break;
    }
}

    @Override
    public void onDateSet(int year, int month, int day, int cal_type) {
        // tv_date_picker.setText(year + "/" + (month + 1) + "/" + day);
        ed_LicenceExpDate.setText(year + "/" + (month + 1) + "/" + day);
    }

    //**************************************************************************************************************************************
    public void GetDriverRegistrationTask() {

        customDialog = new CustomDialog(DriverRegistrationActivity.this);
        customDialog.show();
        if(mDeviceToken.equals("")){
            mDeviceToken = FirebaseInstanceId.getInstance().getToken();
        }
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,  Constant.URL_SIGNUP_DRIVER, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                customDialog.cancel();
                System.out.println("resultResponse==="+resultResponse);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                    if(status.equals(Constant.ONE)){

                        SessionManager sessionManager = new SessionManager(DriverRegistrationActivity.this);
                        JSONObject usedetailobject = result.getJSONObject(Constant.USERDETAILS);
                        AppUtility.showToast(DriverRegistrationActivity.this, message, Toast.LENGTH_SHORT);

                        userId = usedetailobject.getInt(Constant.USERID);
                        sEmail = usedetailobject.getString(Constant.EMAIL);
                        sAuthToken = usedetailobject.getString(Constant.AUTHTOKEN);
                        sGender = usedetailobject.getString(Constant.GENDER);
                        sUserType = usedetailobject.getString("userType");
                        sLoginSatus = usedetailobject.getString("status");
                        //   sSocialType  = usedetailobject.getString(Constant.SOCIALTYPE);
                        sUserName = usedetailobject.getString(Constant.USERNAME);
                        sUserProfile = usedetailobject.getString(Constant.PROFILEIMAGE);

                        JSONObject driverDetailObject = usedetailobject.getJSONObject("driverDetail");

                        sVehicleNo = driverDetailObject.getString("vihicleNumber");
                        sLicenceNo = driverDetailObject.getString("licenceNumber");
                        VehicleTypeName = driverDetailObject.getString("carName");
                        sVehicleTypeId = driverDetailObject.getString("vihicleType");
                        sIdentityProof = driverDetailObject.getString("identityProof");
                        sFullName = driverDetailObject.getString("fullName");
                        sIdentityProofStatus = driverDetailObject.getString("identityProofStatus");
                        sLicenceNumberStatus = driverDetailObject.getString("licenceNumberStatus");
                        sLicenceExDate = driverDetailObject.getString("licenceExDate");

                        sessionManager.createDriverSession(userId, sUserName, sFullName,sEmail, sUserType, sGender, sUserProfile, sVehicleNo, sLicenceNo, sIdentityProof, sLicenceExDate, sAuthToken);

                        sessionManager.setDriverStatus(sLoginSatus);

                        sessionManager.setLicenceNumberStatus(sLicenceNumberStatus);
                        sessionManager.setIdentityProofStatus(sIdentityProofStatus);
                        sessionManager.setVihicleTypeId(sVehicleTypeId);
                        sessionManager.setVehicleTypeName(VehicleTypeName);
                        sessionManager.setDriverCaptionIdentityNumber(mCaptionIdentityNumber);
                        sessionManager.setDriverDateOfBirth(mDriverDateOfBirth);

                        sendDriverRegistrationInfo_To_Manasah_Wasl_API();
                        Intent enterApp = new Intent(DriverRegistrationActivity.this,DriverHomeActivity.class);
                        //enterApp.putExtra("refereshMsg",refereshMsg);
                        enterApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        enterApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(enterApp);
                        overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                        finish();

                    }
                    else {
                        AppUtility.showToast(DriverRegistrationActivity.this, message, Toast.LENGTH_SHORT);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                customDialog.cancel();
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", ""+status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }else if (networkResponse.statusCode == 300) {
                            errorMessage = message+" Something is getting wrong";
                        }
                        AppUtility.showToast(DriverRegistrationActivity.this, message, Toast.LENGTH_SHORT);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userName", sUserName);
                params.put("UserType", sUserType);
                params.put("fullName", sFullName);
                params.put("email", sEmail);
                params.put("password", sPassword);
                params.put("deviceType", mDeviceType);
                params.put("mobileNumber", mobileNumber);
                params.put("gender", sGender);
                params.put("socialtype", "");
                params.put("userType", "driver");
                params.put("vihicleNumber", sVehicleNo);
                params.put("licenceNumber", sLicenceNo);
                params.put("licenceExDate", sLicenceExpDate);
                params.put("vihicleType", sVehicleTypeId);
                params.put("captainIdentityNumber", mCaptionIdentityNumber);
                params.put("dateOfBirth", mDriverDateOfBirth);
                if(mDeviceToken.equals("")){
                    mDeviceToken = FirebaseInstanceId.getInstance().getToken();
                    params.put("deviceToken", mDeviceToken);
                }else {
                    params.put("deviceToken", mDeviceToken);
                }

                return params;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (profileImageBitmap!=null){
                    params.put("profileImage", new DataPart("profileImage.jpg", AppHelper.getFileDataFromDrawable(profileImageBitmap), "image/jpeg"));
                }if (IdProofImageBitmap!=null){
                    params.put("identityProof", new DataPart("identityProof.jpg", AppHelper.getFileDataFromDrawable(IdProofImageBitmap), "image/jpeg"));
                }else {
                    Toast.makeText(DriverRegistrationActivity.this, "Please select vehicle identity proof image", Toast.LENGTH_LONG).show();

                }
                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

    }

    /****************************************************************************************************************************************/

    public void GetVehicleType() {

        customDialog = new CustomDialog(DriverRegistrationActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_DRIVER_REG_VEHICHAL_TYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("#" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");
                            Log.d(TAG, "response =" + response);

                            if (status.equalsIgnoreCase("1")) {

                                vehichleTypeArraylist.clear();

                                JSONArray jsonArray = jsonObj.getJSONArray("carDetail");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    VehichleTypeBean item = new VehichleTypeBean();

                                    item.setCarId(jsonObject.getString("carId").toString().trim());
                                    item.setCarName(jsonObject.getString("carName").toString().trim());

                                    vehichleTypeArraylist.add(item);

                                }
                                vehichleTypeAdapter.notifyDataSetChanged();

                            }else if(response.isEmpty())
                            {
                                AppUtility.showAlertDialog_SingleButton_finishActivity(DriverRegistrationActivity.this,"Error occured, please try again !","","Ok");
                            }

                            else {
//                AppUtility.showAlertDialog_SingleButton(Ho.this,"There is no any item related to..","","Ok");

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
                        Toast.makeText(DriverRegistrationActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

          /*  @Override
            public Map<String, String> getParams() throws AuthFailureError {
                // SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd");
                //  String todayDate = writeFormat.format(date);
                Map<String, String> header = new HashMap<>();
                //   header.put("mobileNumber", otpUrlParameter);
                return header;
            }
*/
         /*   @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("authToken", sessionManager.getAuthToken());
                return header;
            }*/
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //**************************************************************************************************************************************
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                !getLocationOnScreen(ed_LicenceNo).contains(x, y)) {
            InputMethodManager input = (InputMethodManager)
                    DriverRegistrationActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(ed_LicenceNo.getWindowToken(), 0);
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
    /*--------------------------------------------------------------------------------------------------*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    /*--------------------------------------------------------------------------------------------------------*/

}