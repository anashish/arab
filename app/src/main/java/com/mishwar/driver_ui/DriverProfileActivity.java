package com.mishwar.driver_ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mishwar.R;
import com.mishwar.helper.CircleTransform;
import com.mishwar.helper.ConnectionDetector;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.listner.CustomButtonListener;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.DatePickerFragment;
import com.mishwar.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import vollyemultipart.AppHelper;
import vollyemultipart.VolleyMultipartRequest;
import vollyemultipart.VolleySingleton;

public class DriverProfileActivity extends AppCompatActivity implements View.OnClickListener,CustomButtonListener {

    private static final String TAG = DriverProfileActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    static final int REQUEST_CAMERA = 0;
    static final int SELECT_FILE = 1;
    private   ImageView iv_backward;
    CircleImageView iv_my_profilePic;
    private  TextView driver_actionbarLayout_title,tvProfileName,editProfile_edittext_attachRC;
    private EditText etDrivereditProfileFullName,etDrivereditProfileUserName,etDrivereditProfilePass,editProfile_edittext_changeVehicleNo,editProfile_edittext_changeLisenceNo,
            editProfile_edittext_licenceExDate;
    Button editProfile_imageBtn_updateProfile;
    private SessionManager sessionManager;
    private boolean isIdentityProof = false;
    Bitmap profileImageBitmap,IdProofImageBitmap,ImageBitmap;
    private int userId;
    private String  sIdentityProofStatus,sLicenceNumberStatus;
    private String sFullName,sEmail,sPassword,sVehicleNo,sLicenceNo,sIdentityProof,sLicenceExDate,sLicenceExpDate,sGender,VehicleTypeName,sVehicleTypeId,sLoginSatus,sSocialId,sUserProfile,sAuthToken,sUserType= "driver",sUserName,mDeviceToken="";
    RequestQueue requestQueue;
    CustomDialog customDialog;
    private EditText mEditTextplateLetterRight;
    private EditText mEditTextplateLetterMiddle;
    private EditText mEditTextplateLetterLeft;
    private EditText mEditTextplateNumber;
    private EditText mEditTextplateType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        sessionManager = new SessionManager(DriverProfileActivity.this);

        initializeViews();
    }

    /*-------------------------------------------------------------------------------------------------------------------*/
    private void initializeViews() {

        /*tvAccountEdit = (TextView) findViewById(R.id.tvAccountEdit);
        */
        // driver_actionbarLayout_title = (TextView) findViewById(R.id.driver_actionbarLayout_title);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);

        iv_backward = (ImageView) findViewById(R.id.iv_backward);
        iv_my_profilePic = (CircleImageView) findViewById(R.id.iv_my_profilePic);

        editProfile_imageBtn_updateProfile = (Button) findViewById(R.id.editProfile_imageBtn_updateProfile);

        etDrivereditProfileFullName = (EditText) findViewById(R.id.etDrivereditProfileFullName);
        etDrivereditProfileUserName = (EditText) findViewById(R.id.etDrivereditProfileUserName);
        //etDrivereditProfilePass = (EditText) findViewById(R.id.etDrivereditProfilePass);
        editProfile_edittext_changeVehicleNo = (EditText) findViewById(R.id.editProfile_edittext_changeVehicleNo);
        editProfile_edittext_changeLisenceNo = (EditText) findViewById(R.id.editProfile_edittext_changeLisenceNo);
        editProfile_edittext_licenceExDate = (EditText) findViewById(R.id.editProfile_edittext_licenceExDate);
        editProfile_edittext_attachRC = (TextView) findViewById(R.id.editProfile_edittext_attachRC);

         mEditTextplateLetterRight=(EditText)findViewById(R.id.editProfile_edittext_plateLetterRight);
         mEditTextplateLetterMiddle=(EditText)findViewById(R.id.editProfile_edittext_plateLetterMiddle);
         mEditTextplateLetterLeft=(EditText)findViewById(R.id.editProfile_edittext_plateLetterLeft);
         mEditTextplateNumber=(EditText)findViewById(R.id.editProfile_edittext_plateNumber);
         mEditTextplateType=(EditText)findViewById(R.id.editProfile_edittext_plateType);



        if(ConnectionDetector.isNetworkAvailable(DriverProfileActivity.this)){

            GetRefereshTask();
        }
        else {
            AppUtility.showToast(DriverProfileActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);

        }

        //driver_actionbarLayout_title.setText("Edit Profile");
        Glide.with(this).load(sessionManager.getProfileImage()).placeholder(R.drawable.person).transform(new CircleTransform(this)).override(100, 100).into(iv_my_profilePic);

        tvProfileName.setText(sessionManager.getFullName().toString());
        editProfile_edittext_changeVehicleNo.setText(sessionManager.getVehicleNo().toString());
        editProfile_edittext_changeLisenceNo.setText(sessionManager.getLicenceNo().toString());
        editProfile_edittext_licenceExDate.setText(sessionManager.getLicenceExDate().toString());
        etDrivereditProfileFullName.setText(sessionManager.getFullName().toString());
        etDrivereditProfileUserName.setText(sessionManager.getUserName().toString());


        if (sessionManager.getLicenceNumberStatus().toString().equals("0") && sessionManager.getIdentityProofStatus().toString().equals("0")){

            AppUtility.showToast(DriverProfileActivity.this,"your Licence and Vehicle id Proof are not approved yet !",0);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("1") && sessionManager.getLicenceNumberStatus().toString().equals("1"))
        {
            // AppUtility.showToast(DriverProfileActivity.this,"your Licence and Vehicle ID Proof have been Approved !",1);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("2") && sessionManager.getLicenceNumberStatus().toString().equals("2"))
        {
            AppUtility.showToast(DriverProfileActivity.this,"your Licence & Vehicle ID Proof heve been Rejected !, please upload valid Licencde number !",0);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("2") && sessionManager.getLicenceNumberStatus().toString().equals("1"))
        {
            AppUtility.showToast(DriverProfileActivity.this,"your Vehicle ID Proof has been Rejected !, please upload valid Vehicle id proof !",0);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("1") && sessionManager.getLicenceNumberStatus().toString().equals("2"))
        {
            AppUtility.showToast(DriverProfileActivity.this,"your Licence number has been Rejected !, please upload valid Licence number!",0);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("0") && sessionManager.getLicenceNumberStatus().toString().equals("1"))
        {
            AppUtility.showToast(DriverProfileActivity.this,"your Vehicle ID Proof is not approved yet !",0);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("1") && sessionManager.getLicenceNumberStatus().toString().equals("0"))
        {
            AppUtility.showToast(DriverProfileActivity.this,"your Licence number is not approved yet !",0);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("0") && sessionManager.getLicenceNumberStatus().toString().equals("2"))
        {
            AppUtility.showToast(DriverProfileActivity.this,"your Licence number has been Rejected !",0);
        }
        else if (sessionManager.getIdentityProofStatus().toString().equals("2") && sessionManager.getLicenceNumberStatus().toString().equals("0"))
        {
            AppUtility.showToast(DriverProfileActivity.this,"your Vehicle ID Proof has been Rejected !",0);
        }

        iv_backward.setOnClickListener(this);
        editProfile_imageBtn_updateProfile.setOnClickListener(this);
        editProfile_edittext_attachRC.setOnClickListener(this);
        editProfile_edittext_licenceExDate.setOnClickListener(this);

        iv_my_profilePic.setOnClickListener(this);
    }

    /*-------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.iv_backward:
                onBackPressed();
                finish();

                break;

            case R.id.editProfile_imageBtn_updateProfile:
                boolean cancel = false;
                sUserName = etDrivereditProfileUserName.getText().toString().trim();
                sFullName = etDrivereditProfileFullName.getText().toString().trim();
                sVehicleNo = editProfile_edittext_changeVehicleNo.getText().toString().trim();
                sLicenceNo = editProfile_edittext_changeLisenceNo.getText().toString().trim();
                sLicenceExpDate = editProfile_edittext_licenceExDate.getText().toString().trim();


                if(sUserName.isEmpty()){
                    cancel =true;
                    etDrivereditProfileUserName.setError("Please enter user name");
                }
                else if(sFullName.isEmpty()){
                    cancel =true;
                    etDrivereditProfileFullName.setError(getString(R.string.please_enter_fullname));
                }
                else if (sVehicleNo.isEmpty()){
                    cancel =true;
                    editProfile_edittext_changeVehicleNo.setError("Please enter your vehicle number");
                }
               /* else if(sLicenceNo.isEmpty()){
                    cancel =true;
                    editProfile_edittext_changeLisenceNo.setError("Please enter your Licence number");
                }*/
                else if (sLicenceExpDate.isEmpty()){
                    cancel =true;
                    editProfile_edittext_licenceExDate.setError("Please enter your Licence expiry date");
                } else if (mEditTextplateLetterRight.getText().toString().isEmpty()){
                    cancel =true;
                    mEditTextplateLetterRight.setError("Please enter");
                } else if (mEditTextplateLetterLeft.getText().toString().isEmpty()){
                    cancel =true;
                    mEditTextplateLetterLeft.setError("Please enter");
                } else if (mEditTextplateLetterMiddle.getText().toString().isEmpty()){
                    cancel =true;
                    mEditTextplateLetterMiddle.setError("Please enter");
                }else if (mEditTextplateNumber.getText().toString().isEmpty()){
                    cancel =true;
                    mEditTextplateNumber.setError("Please enter");
                }else if (mEditTextplateType.getText().toString().isEmpty()){
                    cancel =true;
                    mEditTextplateType.setError("Please enter");
                }



                if(!cancel){
                    //   AppUtility.showToast(DriverProfileActivity.this, "Work In Progress...", Toast.LENGTH_SHORT);

                    // call signup api
                    //     AppUtility.showToast(DriverRegistrationActivity.this, "sign up clicked", Toast.LENGTH_SHORT);
                    if(ConnectionDetector.isNetworkAvailable(DriverProfileActivity.this)){


                        GetDriverEditProfileTask();
                    }
                    else {
                        AppUtility.showToast(DriverProfileActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);

                    }
                }

                break;

            case R.id.editProfile_edittext_attachRC:

                if (!sessionManager.getIdentityProofStatus().toString().equals("2")) {
                    AppUtility.showToast(DriverProfileActivity.this, "you can not update vehicle identity !", 0);
                    // editProfile_edittext_attachRC.setEnabled(false);
                }else {
                    isIdentityProof = true;
                    editProfile_edittext_attachRC.setEnabled(true);

                    selectImage();

                }
                break;

            case R.id.editProfile_edittext_licenceExDate:
                if (!sessionManager.getLicenceNumberStatus().toString().equals("2")) {
                    AppUtility.showToast(DriverProfileActivity.this, "you can not update vehicle identity !", 0);
                    // editProfile_edittext_licenceExDate.setEnabled(false);
                }else {
                    editProfile_edittext_licenceExDate.setEnabled(true);
                    editProfile_edittext_changeLisenceNo.setEnabled(true);
                    DatePickerFragment datePickerFragment = new DatePickerFragment(Constant.CALENDAR_DAY, true);
                    datePickerFragment.setDateListener(DriverProfileActivity.this);
                    datePickerFragment.show(getSupportFragmentManager(), "");

                    String date = editProfile_edittext_licenceExDate.getText().toString();
                }
                break;

            case R.id.iv_my_profilePic:
                isIdentityProof = false;

                selectImage();

                break;
        }
    }

    /**
     * This method used to send vehicle info to manasah api
     */
    private void sendVehicleRegistrationInfo_To_Manasah_Wasl_API() {

        StringRequest sendInfo = new StringRequest(Request.Method.POST,  Constant.URL_DRIVER_VEHICLE_REG_INFO_MANASAH_WASL_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                AppUtility.log_DEBUG("RESPONSE ",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppUtility.log_DEBUG("RESPONSE ",error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("apiKey", Constant.MANASAH_WASL_API_KEY);
                params.put("vehicleSequenceNumber", sVehicleNo);
                params.put("plateLetterRight", mEditTextplateLetterRight.getText().toString());
                params.put("plateLetterMiddle",mEditTextplateLetterMiddle.getText().toString());
                params.put("plateLetterLeft", mEditTextplateLetterLeft.getText().toString());
                params.put("plateType", mEditTextplateType.getText().toString());
                params.put("plateNumber", mEditTextplateNumber.getText().toString());
                return params;
            }
        };


        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(sendInfo);
    }


    /*-------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onBackPressed() {
        Intent enterApp = new Intent(DriverProfileActivity.this,DriverHomeActivity.class);
        enterApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        enterApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(enterApp);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
        super.onBackPressed();
    }


    /*-------------------------------------------------------------------------------------------------------------------*/
    private void selectImage() {

        final CharSequence[] items = {getString(R.string.text_take_photo), getString(R.string.text_chose_gellery), getString(R.string.text_cancel)};
        AlertDialog.Builder alert = new AlertDialog.Builder(DriverProfileActivity.this);
        alert.setTitle(getString(R.string.text_add_photo));
        alert.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.text_take_photo))) {

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


                } else if (items[item].equals(getString(R.string.text_chose_gellery))) {

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


                } else if (items[item].equals(getString(R.string.text_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        alert.show();
    }
    /****************************************************************************************************************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.SELECT_FILE);
                } else {
                    Toast.makeText(DriverProfileActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }break;

            case  Constant.MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    Toast.makeText(DriverProfileActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            } break;
        }
    }
    /****************************************************************************************************************************************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                ImageBitmap = (Bitmap) data.getExtras().get("data");
                //profileImageBitmap = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                if (isIdentityProof == true) {
                    IdProofImageBitmap = ImageBitmap;
                    editProfile_edittext_attachRC.setText(getString(R.string.text_attached));

                } else {
                    profileImageBitmap = ImageBitmap;
                }

                if (isIdentityProof == false) {
                    iv_my_profilePic.setImageBitmap(profileImageBitmap);
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

                    if (isIdentityProof == true) {
                        IdProofImageBitmap = ImageBitmap;
                        editProfile_edittext_attachRC.setText(getString(R.string.text_attached));

                    } else {
                        profileImageBitmap = ImageBitmap;
                    }

                    if (isIdentityProof == false) {
                        iv_my_profilePic.setImageBitmap(profileImageBitmap);
                    }
                    // iv_profilePic.setImageBitmap(profileImageBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //**************************************************************************************************************************************
    public void GetDriverEditProfileTask() {

        customDialog = new CustomDialog(DriverProfileActivity.this);
        customDialog.show();
        if(mDeviceToken.equals("")){
            mDeviceToken = FirebaseInstanceId.getInstance().getToken();
        }
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,  Constant.URL_UPDATE_DRIVER_PROFILE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String status , message;
                String resultResponse = new String(response.data);
                customDialog.cancel();
                System.out.println("resultResponse==="+resultResponse);
                try {
                    JSONObject responseData = new JSONObject(resultResponse);
                    status = responseData.getString(Constant.STATUS);
                    message = responseData.getString(Constant.MESSAGE);

                    if(status.equals(Constant.ONE)){
                        sendVehicleRegistrationInfo_To_Manasah_Wasl_API();
                        JSONObject usedetailobject = responseData.getJSONObject("result");
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

                        sFullName = driverDetailObject.getString("fullName");
                        sVehicleNo = driverDetailObject.getString("vihicleNumber");
                        sLicenceNo = driverDetailObject.getString("licenceNumber");
                        VehicleTypeName = driverDetailObject.getString("carName");
                        sVehicleTypeId = driverDetailObject.getString("vihicleType");
                        sIdentityProof = driverDetailObject.getString("identityProof");
                        sLicenceExDate = driverDetailObject.getString("licenceExDate");
                        sIdentityProofStatus = driverDetailObject.getString("identityProofStatus");
                        sLicenceNumberStatus = driverDetailObject.getString("licenceNumberStatus");
                        AppUtility.showToast(DriverProfileActivity.this, message, Toast.LENGTH_SHORT);

                        SessionManager sessionManager = new SessionManager(DriverProfileActivity.this);

                        sessionManager.createDriverSession(userId, sUserName, sFullName,sEmail, sUserType, sGender, sUserProfile, sVehicleNo, sLicenceNo,
                                sIdentityProof,sLicenceExDate, sAuthToken);

                        sessionManager.setDriverStatus(sLoginSatus);
                        sessionManager.setVihicleTypeId(sVehicleTypeId);
                        sessionManager.setVehicleTypeName(VehicleTypeName);
                        sessionManager.setIdentityProofStatus(sIdentityProofStatus);
                        sessionManager.setLicenceNumberStatus(sLicenceNumberStatus);
                        //     AppUtility.showToast(DriverProfileActivity.this, refereshMsg, Toast.LENGTH_LONG);
                        Intent enterApp = new Intent(DriverProfileActivity.this,DriverHomeActivity.class);
                        enterApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        enterApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(enterApp);
                        overridePendingTransition(R.anim.fade_in, R.anim.slide_in);
                        finish();

                    } else {
                        AppUtility.showToast(DriverProfileActivity.this, message, Toast.LENGTH_SHORT);
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
                    String resultResponse = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(resultResponse);
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
                        AppUtility.showToast(DriverProfileActivity.this, message, Toast.LENGTH_SHORT);

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
                params.put("fullName", sFullName);
                params.put("vihicleNumber", sVehicleNo);
                params.put("licenceNumber", sLicenceNo);
                params.put("licenceExDate", sLicenceExpDate);
                params.put("plateLetterRight", mEditTextplateLetterRight.getText().toString());
                params.put("plateLetterMiddle",mEditTextplateLetterMiddle.getText().toString());
                params.put("plateLetterLeft", mEditTextplateLetterLeft.getText().toString());
                params.put("plateType", mEditTextplateType.getText().toString());
                params.put("plateNumber", mEditTextplateNumber.getText().toString());

                return params;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (profileImageBitmap!=null){

                    params.put("profileImage", new DataPart("profileImage.jpg", AppHelper.getFileDataFromDrawable(profileImageBitmap), "image/jpeg"));

                }if (IdProofImageBitmap!=null){

                    params.put("identityProof", new DataPart("identityProof.jpg", AppHelper.getFileDataFromDrawable(IdProofImageBitmap), "image/jpeg"));

                }
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("authToken", sessionManager.getAuthToken());
                return header;
            }

        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

    }
    /****************************************************************************************************************************************/
    public void GetRefereshTask() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_REFERESH_DRIVER_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");
                            sIdentityProofStatus = jsonObj.getString("identityProofStatus");
                            sLicenceNumberStatus = jsonObj.getString("licenceNumberStatus");

                            if (status.equalsIgnoreCase("1")) {
                                sLoginSatus = response;
                                sessionManager.setDriverStatus(sLoginSatus);
                                sessionManager.setIdentityProofStatus(sIdentityProofStatus);
                                sessionManager.setLicenceNumberStatus(sLicenceNumberStatus);
                                editProfile_edittext_attachRC.setEnabled(false);
                                editProfile_edittext_licenceExDate.setEnabled(false);
                                editProfile_edittext_changeLisenceNo.setEnabled(false);

                            }else if(sIdentityProofStatus.equals("0") && sIdentityProofStatus.equals("2"))
                            {
                                editProfile_edittext_attachRC.setEnabled(true);
                                AppUtility.showToast(DriverProfileActivity.this, message, 0);

                            }else if(sLicenceNumberStatus.equals("0") && sLicenceNumberStatus.equals("2"))
                            {
                                editProfile_edittext_licenceExDate.setEnabled(true);
                                AppUtility.showToast(DriverProfileActivity.this, message, 0);
                            }
                            /*else {
                                AppUtility.showToast(DriverProfileActivity.this, message, 1);

                            }*/
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(DriverProfileActivity.this, "Something went wrong, please check after some time.", Toast.LENGTH_LONG).show();
                    }
                }) {

            /* @Override
             public Map<String, String> getParams() throws AuthFailureError {
                 Map<String, String> header = new HashMap<>();
                 header.put("driverStatus",driverStatus);
                 return header;
             }*/
            @Override
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

/*-------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onDateSet(int year, int month, int day, int cal_type) {
        // tv_date_picker.setText(year + "/" + (month + 1) + "/" + day);
        editProfile_edittext_licenceExDate.setText(year + "/" + (month + 1) + "/" + day);
    }
    /*-------------------------------------------------------------------------------------------------------------------*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    /*-------------------------------------------------------------------------------------------------------------------*/
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                !getLocationOnScreen(etDrivereditProfileFullName).contains(x, y)) {
            InputMethodManager input = (InputMethodManager)
                    DriverProfileActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(etDrivereditProfileFullName.getWindowToken(), 0);
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
}
