package com.mishwar.passanger_ui;

import android.Manifest;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mishwar.R;
import com.mishwar.helper.ConnectionDetector;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import vollyemultipart.AppHelper;
import vollyemultipart.VolleyMultipartRequest;
import vollyemultipart.VolleySingleton;

public class PassengerRegistrationActivity extends AppCompatActivity {

    public static final String TAG = PassengerRegistrationActivity.class.getSimpleName();
    private ImageView iv_back,iv_editImage,iv_checkBox;
    CircleImageView iv_profilePic;
    private EditText ed_fullName,ed_email,ed_password,ed_confirmPass,ed_userName;
    private Button b_signUp;
    private Spinner spn_gender;
    private boolean termsAccepted = false;

    private String sAuthToken,sUserName,sFullName,sEmail,sPassword,sConfirmPass,sGender,sSocialId,mDeviceToken="",mDeviceType="1",
            mobileNumber="",sUserType= "passenger",sProfileImage,sLoginSatus,sCarFacility;
    private int userId;
    CustomDialog customDialog;
    private SessionManager sessionManager;
    private Bitmap profileImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_registration);

        sessionManager = new SessionManager(PassengerRegistrationActivity.this);
        mDeviceToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("mDeviceToken == "+mDeviceToken);

        Typeface RobotoLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");

        iv_back = (ImageView)findViewById(R.id.p_reg_iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iv_profilePic = (CircleImageView)findViewById(R.id.p_reg_iv_passenger);
        iv_profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        iv_editImage = (ImageView)findViewById(R.id.p_reg_iv_cam);
        iv_editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });
        iv_checkBox = (ImageView)findViewById(R.id.p_reg_term_accepted);
        iv_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!termsAccepted){
                    termsAccepted = true;
                    //iv_checkBox.setSelected(true);
                    iv_checkBox.setImageDrawable(getResources().getDrawable(R.drawable.icon_with_tick));
                }
                else {
                    termsAccepted = false;
                    // iv_checkBox.setSelected(false);
                    iv_checkBox.setImageDrawable(getResources().getDrawable(R.drawable.icon_without_tick));
                }
            }
        });

        mobileNumber= getIntent().getStringExtra("pMobileNo");

        ed_userName = (EditText)findViewById(R.id.p_reg_ed_username);
        ed_userName.setTypeface(RobotoLight);

        ed_fullName = (EditText) findViewById(R.id.p_reg_ed_fullname);
        ed_fullName.setTypeface(RobotoLight);

        ed_email = (EditText)findViewById(R.id.p_reg_ed_email);
        ed_email.setTypeface(RobotoLight);

        ed_password = (EditText)findViewById(R.id.p_reg_ed_password);
        ed_password.setTypeface(RobotoLight);

        ed_confirmPass = (EditText)findViewById(R.id.p_reg_confirm_pass);
        ed_confirmPass.setTypeface(RobotoLight);


        TextView tv_terms = (TextView)findViewById(R.id.p_reg_tv_terms);

        spn_gender = (Spinner)findViewById(R.id.p_reg_spn_gender);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(PassengerRegistrationActivity.this,R.layout.item_country_spinner,getResources().getStringArray(R.array.genders));
        spn_gender.setAdapter(genderAdapter);
        spn_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sGender = getResources().getStringArray(R.array.genders)[position] ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        b_signUp = (Button)findViewById(R.id.p_reg_b_signup);
        b_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean cancel = false;

                sUserName = ed_userName.getText().toString().trim();
                sFullName = ed_fullName.getText().toString().trim();
                sEmail = ed_email.getText().toString().trim();
                sPassword = ed_password.getText().toString().trim();
                sConfirmPass = ed_confirmPass.getText().toString().trim();

               /* if(mImagePath==null){
                    cancel = true;
                    AppUtility.showToast(PassengerRegistrationActivity.this, "Please select profile image", Toast.LENGTH_SHORT);

                }*/
                if(sUserName.isEmpty()){
                    cancel =true;
                    ed_userName.setError("Please enter user name");
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

                } else if (sPassword.length()<=5){
                    cancel = true;
                    ed_password.setError("Please enter minimum 6 digit password");
                }
                else if (sConfirmPass.isEmpty()){
                    cancel = true;
                    ed_confirmPass.setError(getString(R.string.please_confirm_password));
                }
                else if(!sConfirmPass.equals(sPassword)){
                    cancel =true;
                    ed_confirmPass.setError(getString(R.string.password_does_not_match));
                }
                else if(sGender.isEmpty()){
                    AppUtility.showToast(PassengerRegistrationActivity.this, getString(R.string.please_select_gender), Toast.LENGTH_SHORT);

                }
                else if(!termsAccepted){
                    cancel =true;
                    AppUtility.showToast(PassengerRegistrationActivity.this, getString(R.string.please_accept_terms_and_cond), Toast.LENGTH_SHORT);

                }

                if(!cancel){
                    if(ConnectionDetector.isNetworkAvailable(PassengerRegistrationActivity.this)){
                        //  mAuthTask = new UserSignUpTask();
                        // mAuthTask.execute(Constant.URL_SIGNUP_PASSENGER);
                        GetPassengerRegistrationTask();
                    }
                    else {
                        AppUtility.showToast(PassengerRegistrationActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);

                    }

                }
            }

        });

    }

    /*---------------------------------------------------------------------------------------------------------*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == Constant.REQUEST_CAMERA) {

                profileImageBitmap = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                profileImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                iv_profilePic.setImageBitmap(profileImageBitmap);

            } else if (requestCode == Constant.SELECT_FILE) {

                Uri selectedImageUri = data.getData();

                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);

                profileImageBitmap = ImageUtil.decodeFile(selectedImagePath);
                try {
                    profileImageBitmap = ImageUtil.modifyOrientation(profileImageBitmap, selectedImagePath);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    profileImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    iv_profilePic.setImageBitmap(profileImageBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    /*-----------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.SELECT_FILE);
                } else {
                    Toast.makeText(PassengerRegistrationActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }break;

            case  Constant.MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    Toast.makeText(PassengerRegistrationActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            } break;
        }
    }
    /*-------------------------------------------------------------------------------------------------------------------------*/
    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from gallery", "Cancel"};
        AlertDialog.Builder alert = new AlertDialog.Builder(PassengerRegistrationActivity.this);
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
    public void onBackPressed() {
        android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(PassengerRegistrationActivity.this);
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
                        overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
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

    //**************************************************************************************************************************************
    public void GetPassengerRegistrationTask() {

        customDialog = new CustomDialog(PassengerRegistrationActivity.this);
        customDialog.show();
        if(mDeviceToken.equals("")){
            mDeviceToken = FirebaseInstanceId.getInstance().getToken();
            System.out.println("mDeviceToken == "+mDeviceToken);
        }
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,  Constant.URL_SIGNUP_PASSENGER, new Response.Listener<NetworkResponse>() {
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

                        AppUtility.showToast(PassengerRegistrationActivity.this, message, Toast.LENGTH_SHORT);

                        JSONObject usedetailobject = result.getJSONObject(Constant.USERDETAILS);
                        userId = usedetailobject.getInt(Constant.USERID);
                        //  sFullName = usedetailobject.getString(Constant.FULLNAME);
                        sEmail = usedetailobject.getString(Constant.EMAIL);
                        sPassword = usedetailobject.getString("password");
                        sAuthToken = usedetailobject.getString(Constant.AUTHTOKEN);
                        sGender = usedetailobject.getString(Constant.GENDER);
                        sSocialId = usedetailobject.getString(Constant.SOCIALID);
                        //    sSocialType  = usedetailobject.getString(Constant.SOCIALTYPE);
                        sUserName = usedetailobject.getString(Constant.USERNAME);
                        sLoginSatus = usedetailobject.getString("status");
                        sUserType = usedetailobject.getString(Constant.USERTYPE);
                        sProfileImage = usedetailobject.getString(Constant.PROFILEIMAGE);
                        //  sCarFacility = usedetailobject.getString("carFacility");
                        if(sUserType.equals("passenger")) {
                            JSONObject passengerDetailobject = usedetailobject.getJSONObject("passengerDetail");
                            sFullName = passengerDetailobject.getString("fullName");
                            mobileNumber = passengerDetailobject.getString("mobileNumber");
                        }


                        sessionManager.createSession(userId,sUserName,sFullName,sSocialId,sEmail,sUserType,sGender,sProfileImage,sLoginSatus,mobileNumber,sAuthToken);
                        sessionManager.setPassword(sPassword);
                        Intent enterApp = new Intent(PassengerRegistrationActivity.this,HomeActivity.class);
                        enterApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        enterApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(enterApp);
                        overridePendingTransition(R.anim.fade_in, R.anim.slide_in);

                        finish();

                    } else {
                        AppUtility.showToast(PassengerRegistrationActivity.this, message, Toast.LENGTH_SHORT);

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
                        AppUtility.showToast(PassengerRegistrationActivity.this, message, Toast.LENGTH_SHORT);

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
                params.put("userType", "passenger");

                if(mDeviceToken.equals("")){
                    mDeviceToken = FirebaseInstanceId.getInstance().getToken();
                    System.out.println("mDeviceToken == "+mDeviceToken);
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

                }
                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

    }

    /****************************************************************************************************************************************/


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                !getLocationOnScreen(ed_confirmPass).contains(x, y)) {
            InputMethodManager input = (InputMethodManager)
                    PassengerRegistrationActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(ed_confirmPass.getWindowToken(), 0);
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
