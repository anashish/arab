package com.mishwar.commun_ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mishwar.R;
import com.mishwar.driver_ui.DriverHomeActivity;
import com.mishwar.passanger_ui.HomeActivity;
import com.mishwar.session.SessionManager;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    Button btnSettingEnglish,btnSettingArabic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnSettingEnglish = (Button) findViewById(R.id.btnSettingEnglish);
        btnSettingArabic = (Button) findViewById(R.id.btnSettingArabic);

        btnSettingEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Locale locale = new Locale("en");
                Locale.setDefault(locale);
                Resources resources = getResources();
                Configuration configuration = resources.getConfiguration();
                configuration.locale = locale;
                resources.updateConfiguration(configuration, resources.getDisplayMetrics());

                SessionManager sessionManager = new SessionManager(SettingActivity.this);
                sessionManager.setLanguage("en");

                if(sessionManager.getUserType().equals("driver")){
                    Intent i = new Intent(SettingActivity.this, DriverHomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }else {
                    Intent i = new Intent(SettingActivity.this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }

            }
        });

        btnSettingArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Locale locale = new Locale("ar");
                Locale.setDefault(locale);
                Resources resources = getResources();
                Configuration configuration = resources.getConfiguration();
                configuration.locale = locale;
                resources.updateConfiguration(configuration, resources.getDisplayMetrics());

                SessionManager sessionManager = new SessionManager(SettingActivity.this);
                sessionManager.setLanguage("ar");

                if(sessionManager.getUserType().equals("driver")){
                    Intent i = new Intent(SettingActivity.this, DriverHomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }else {
                    Intent i = new Intent(SettingActivity.this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }

            }
        });

    }
}
