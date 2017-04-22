package com.mishwar.passanger_ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.mishwar.R;
import com.mishwar.helper.CircleTransform;
import com.mishwar.helper.ConnectionDetector;
import com.mishwar.helper.Constant;
import com.mishwar.helper.CustomDialog;
import com.mishwar.session.SessionManager;
import com.mishwar.utils.AppUtility;
import com.mishwar.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import vollyemultipart.AppHelper;
import vollyemultipart.VolleyMultipartRequest;
import vollyemultipart.VolleySingleton;
import static com.mishwar.helper.Constant.PERMISSION_REQUEST_CONTACT;
import static com.mishwar.helper.Constant.REQUEST_CAMERA;
import static com.mishwar.helper.Constant.REQUEST_CODE_PICK_CONTACTS;
import static com.mishwar.helper.Constant.SELECT_FILE;

public class PassengerUpdateProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private Uri uriContact;
    private String contactID,sNewPassword="";
    private TextView tvPassEmergencyCantact,tvPassFullName,tvPassEmail,tvPassContactNo;
    private  ImageView ivBack;
    private EditText etPassUpdateProfileUserName,etPassChangePass;
    private Button btnUpdatePassProfile;
    private CircleImageView ivPassProfilePic;
    private Bitmap profileImageBitmap;
    private SessionManager sessionManager;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_update_profile);
        sessionManager = new SessionManager(PassengerUpdateProfileActivity.this);
        initializeViews();
    }

    private void  initializeViews(){
        tvPassEmergencyCantact = (TextView) findViewById(R.id.tvPassEmergencyCantact);
        tvPassFullName = (TextView) findViewById(R.id.tvPassFullName);
        tvPassEmail = (TextView) findViewById(R.id.tvPassEmail);
        tvPassContactNo = (TextView) findViewById(R.id.tvPassContactNo);

        etPassChangePass = (EditText) findViewById(R.id.etPassChangePass);
        etPassUpdateProfileUserName = (EditText) findViewById(R.id.etPassUpdateProfileUserName);

        btnUpdatePassProfile = (Button) findViewById(R.id.btnUpdatePassProfile);
        ivPassProfilePic = (CircleImageView) findViewById(R.id.ivPassProfilePic);
        ivBack = (ImageView) findViewById(R.id.ivBack);

        Glide.with(this).load(sessionManager.getProfileImage()).placeholder(R.drawable.person).transform(new CircleTransform(this)).override(100, 100).into(ivPassProfilePic);
        etPassUpdateProfileUserName.setText(sessionManager.getUserName().toString());
        tvPassFullName.setText(sessionManager.getFullName().toString());
        tvPassContactNo.setText(sessionManager.getMobileNo().toString());
        tvPassEmail.setText(sessionManager.getUserEmail().toString());

        askForContactPermission();

        tvPassEmergencyCantact.setOnClickListener(this);
        btnUpdatePassProfile.setOnClickListener(this);
        ivPassProfilePic.setOnClickListener(this);
        etPassChangePass.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvPassEmergencyCantact :

                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);

                break;

            case R.id.ivBack :

                onBackPressed();
                break;

            case R.id.ivPassProfilePic :
                selectImage();
                break;

            case R.id.etPassChangePass :
                showDialog();

                break;
            case R.id.btnUpdatePassProfile :
                boolean cancel = false;
                String changePass = etPassChangePass.getText().toString().trim();
                String emmergencyContactNo = tvPassEmergencyCantact.getText().toString().trim();
                String  sUserName = etPassUpdateProfileUserName.getText().toString().trim();

                if(sUserName.isEmpty()){
                    cancel =true;
                    etPassUpdateProfileUserName.setError("Please enter user name");
                }
                if(!cancel){

                    if(ConnectionDetector.isNetworkAvailable(PassengerUpdateProfileActivity.this)){

                        GetPassenegrUpdateProfileTask();
                    }
                    else {
                        AppUtility.showToast(PassengerUpdateProfileActivity.this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);

                    }
                }
                break;
        }
    }

    /*-------------------------------------------------------------------------------------------------------------------------*/
    private void selectImage() {

        final CharSequence[] items = {getString(R.string.text_take_photo), getString(R.string.text_chose_gellery), getString(R.string.text_cancel)};
        AlertDialog.Builder alert = new AlertDialog.Builder(PassengerUpdateProfileActivity.this);
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

    //**************************************************************************************************************************************
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                } else {
                    Toast.makeText(PassengerUpdateProfileActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }break;

            case  Constant.MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else {
                    Toast.makeText(PassengerUpdateProfileActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            }
            break;
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                return;
            }
        }
    }
    /*-------------------------------------------------------------------------------------------------------*/

    public void showDialog(){

        final Dialog dialogChangePass = new Dialog(PassengerUpdateProfileActivity.this,android.R.style.Theme_Black);
        dialogChangePass.setCanceledOnTouchOutside(true);
        dialogChangePass.setCancelable(true);
        dialogChangePass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangePass.setContentView(R.layout.layout_pass_change_password);
        dialogChangePass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window=dialogChangePass.getWindow();
        window.setGravity(Gravity.CENTER);


        final EditText  etOldPass=(EditText)dialogChangePass.findViewById(R.id.etOldPass);
        final EditText  etNewPass=(EditText)dialogChangePass.findViewById(R.id.etNewPass);
        final EditText  etConfirmNewPass=(EditText)dialogChangePass.findViewById(R.id.etConfirmNewPass);

        Button  btnChangePassword=(Button)dialogChangePass.findViewById(R.id.btnChangePasswordSubmit);
        Button  btnChangePasswordCancel=(Button)dialogChangePass.findViewById(R.id.btnChangePasswordCancel);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = false;
                String  sOldPassword = etOldPass.getText().toString();
                String NewPass = etNewPass.getText().toString();
                String   sConfirmPassword = etConfirmNewPass.getText().toString();
                String oldPass = sessionManager.getUserPassword();

                if(sOldPassword.isEmpty()){
                    cancel =true;
                    etOldPass.requestFocus();
                    etOldPass.setError(getString(R.string.text_enter_pass));
                }
                else if(NewPass.isEmpty()){
                    cancel =true;
                    etNewPass.requestFocus();
                    etNewPass.setError(getString(R.string.text_enter_new_pass));
                }else if (NewPass.length()<=5){
                    cancel = true;
                    etNewPass.requestFocus();
                    etNewPass.setError(getString(R.string.text_enter_six_digit_pass));
                }
                else if (sConfirmPassword.isEmpty()){
                    cancel =true;
                    etConfirmNewPass.requestFocus();
                    etConfirmNewPass.setError(getString(R.string.text_enter_confirm_pass));

                } else if (!sConfirmPassword.equals(NewPass)){
                    cancel =true;
                    etConfirmNewPass.requestFocus();
                    etConfirmNewPass.setError(getString(R.string.text_enter_correct_old_pass));
                }
                else if (!sOldPassword.equals(oldPass)){
                    cancel =true;
                    etConfirmNewPass.requestFocus();
                    etConfirmNewPass.setError(getString(R.string.text_enter_pass));
                }
                if(!cancel){
                    sNewPassword = etNewPass.getText().toString().trim();
                    etPassChangePass.setText(NewPass);
                    sNewPassword = NewPass;
                    dialogChangePass.dismiss();
                }
            }
        });
        btnChangePasswordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChangePass.dismiss();
                String oldPass = sessionManager.getUserPassword();
            }
        });
        dialogChangePass.show();

    }

    //**************************************************************************************************************************************


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == Constant.REQUEST_CAMERA) {

                profileImageBitmap = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                profileImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
                ivPassProfilePic.setImageBitmap(profileImageBitmap);

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
                    profileImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
                    ivPassProfilePic.setImageBitmap(profileImageBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == REQUEST_CODE_PICK_CONTACTS ) {
                Log.d("contact", "contact Response: " + data.toString());
                uriContact = data.getData();

                //  retrieveContactName();
                retrieveContactNumber();
                // retrieveContactPhoto();
            }
        }

    }
    private void retrieveContactPhoto() {

        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);

            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void retrieveContactNumber() {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d("Contact", "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();
        tvPassEmergencyCantact.setText(contactNumber);
        Log.d("Contact", "Contact Phone Number: " + contactNumber);
    }

    private void retrieveContactName() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        Log.d("Contact", "Contact Name: " + contactName);

    }

    public void askForContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(PassengerUpdateProfileActivity.this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(PassengerUpdateProfileActivity.this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PassengerUpdateProfileActivity.this);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(PassengerUpdateProfileActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
    }
    //**************************************************************************************************************************************
    public void GetPassenegrUpdateProfileTask() {

        customDialog = new CustomDialog(PassengerUpdateProfileActivity.this);
        customDialog.show();

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,  Constant.URL_UPDATE_PASSENGER_PROFILE, new Response.Listener<NetworkResponse>() {
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
                    int userId;
                    if(status.equals(Constant.ONE)){
                        String sAuthToken,sUserName,sFullName,sEmail,sPassword,sGender,sSocialId,
                                mobileNumber="",sUserType,sProfileImage,sLoginSatus,sCarFacility,sSocialType="";

                        JSONObject usedetailobject = responseData.getJSONObject("result");
                        userId = usedetailobject.getInt(Constant.USERID);
                        sAuthToken = usedetailobject.getString(Constant.AUTHTOKEN);
                        sGender = usedetailobject.getString(Constant.GENDER);
                        sPassword = usedetailobject.getString("password");
                        sEmail = usedetailobject.getString("email");
                        sSocialId = usedetailobject.getString(Constant.SOCIALID);
                        sSocialType = usedetailobject.getString(Constant.SOCIALTYPE);
                        sUserName = usedetailobject.getString(Constant.USERNAME);
                        sUserType = usedetailobject.getString(Constant.USERTYPE);
                        sProfileImage = usedetailobject.getString("profileImage");
                        sLoginSatus = usedetailobject.getString("status");

                        if (sUserType.equals("passenger")) {
                            JSONObject passengerDetailobject = usedetailobject.getJSONObject("passengerDetail");
                            sFullName = passengerDetailobject.getString("fullName");
                            mobileNumber = passengerDetailobject.getString("mobileNumber");
                            sessionManager.createSession(userId,sUserName,sFullName,sSocialId,sEmail,sUserType,sGender,sProfileImage,sLoginSatus,mobileNumber,sAuthToken);
                            sessionManager.setPassword(sPassword);

                        }
                        AppUtility.showToast(PassengerUpdateProfileActivity.this, message, Toast.LENGTH_SHORT);
                        Intent intent = new Intent(PassengerUpdateProfileActivity.this,HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.slide_in);


                    } else {
                        AppUtility.showToast(PassengerUpdateProfileActivity.this, message, Toast.LENGTH_SHORT);
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
                            errorMessage = message+ "Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }else if (networkResponse.statusCode == 300) {
                            errorMessage = message+" Something is getting wrong";
                        }
                        AppUtility.showToast(PassengerUpdateProfileActivity.this, message, Toast.LENGTH_SHORT);

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

                String sNewPassword = etPassChangePass.getText().toString().trim();
                String emmergencyContactNo = tvPassEmergencyCantact.getText().toString().trim();
                String  sUserName = etPassUpdateProfileUserName.getText().toString().trim();

                params.put("userName", sUserName);
                params.put("password", sNewPassword);
                params.put("emergencyContact", emmergencyContactNo);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
    }
}
