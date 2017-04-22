package com.mishwar.commun_ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.mishwar.R;
import com.mishwar.helper.Constant;
import com.mishwar.session.SessionManager;

public class RoleActivity extends AppCompatActivity {


    public static String sRole;

    private ImageView iv_logo,iv_driver,iv_passenger;
    private TextView tv_driver,tv_passenger,tv_select;

    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        sessionManager = new SessionManager(RoleActivity.this);

        Typeface RobotoMedium = Typeface.createFromAsset(getAssets(),"fonts/Roboto_Medium.ttf");
        Typeface RobotoLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");

        tv_select = (TextView)findViewById(R.id.role_tv_select);
        tv_select.setTypeface(RobotoMedium);
        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent  = new Intent(RoleActivity.this,VerifyPhoneActivity.class);
                // startActivity(intent);
            }
        });

        tv_driver = (TextView)findViewById(R.id.role_tv_driver);
        tv_driver.setTypeface(RobotoMedium);
        tv_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sRole = Constant.DRIVER;
                sessionManager.setRole(sRole);
                System.out.println("sessionManager role = " + sessionManager.getRole());
                Intent intent  = new Intent(RoleActivity.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        tv_passenger = (TextView)findViewById(R.id.role_tv_passanger);
        tv_passenger.setTypeface(RobotoMedium);
        tv_passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sRole = Constant.PASSENGER;
                sessionManager.setRole(sRole);
                System.out.println("sessionManager role = " + sessionManager.getRole());
                Intent intent  = new Intent(RoleActivity.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

            }
        });

        iv_driver = (ImageView)findViewById(R.id.role_iv_driver);
        iv_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sRole = Constant.DRIVER;
                sessionManager.setRole(sRole);
                Intent intent  = new Intent(RoleActivity.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

            }
        });


        iv_passenger = (ImageView)findViewById(R.id.role_iv_passanger);
        iv_passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sRole = Constant.PASSENGER;
                sessionManager.setRole(sRole);
                Intent intent  = new Intent(RoleActivity.this,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

            }
        });


    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(RoleActivity.this);
        builder1.setMessage(getString(R.string.text_exit_app));
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

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
