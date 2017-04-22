package com.mishwar.commun_ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import com.mishwar.R;
import com.mishwar.driver_ui.DriverHomeActivity;
import com.mishwar.passanger_ui.HomeActivity;
import com.mishwar.session.SessionManager;

import java.util.Locale;


public class SplashActivity extends AppCompatActivity {

    public static final String TAG = SplashActivity.class.getSimpleName();

    Context mContext;
    //GoogleCloudMessaging gcm;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(SplashActivity.this);

       /* try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:....", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }*/


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your Application's Main Activity
                if(sessionManager.isLoggedIn()){
                    String  socialStatus = "";
                    socialStatus = sessionManager.getSocialId().toString();

                    if (!socialStatus.equals("")){
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    }

                    if(sessionManager.getUserType().equals("driver")){

                        if(sessionManager.getLanguage().equalsIgnoreCase("ar")){
                            Locale locale = new Locale("ar");
                            Locale.setDefault(locale);
                            Resources resources = getResources();
                            Configuration configuration = resources.getConfiguration();
                            configuration.locale = locale;
                            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                        }else {
                            Locale locale = new Locale("en");
                            Locale.setDefault(locale);
                            Resources resources = getResources();
                            Configuration configuration = resources.getConfiguration();
                            configuration.locale = locale;
                            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                        }

                        Intent intent = new Intent(SplashActivity.this, DriverHomeActivity.class);
                        //Intent intent = new Intent(SplashActivity.this, PassengerPaymentActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();

                    }else {

                        if(sessionManager.getLanguage().equalsIgnoreCase("ar")){
                            Locale locale = new Locale("ar");
                            Locale.setDefault(locale);
                            Resources resources = getResources();
                            Configuration configuration = resources.getConfiguration();
                            configuration.locale = locale;
                            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                        }else {
                            Locale locale = new Locale("en");
                            Locale.setDefault(locale);
                            Resources resources = getResources();
                            Configuration configuration = resources.getConfiguration();
                            configuration.locale = locale;
                            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                        }

                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    }
                }else{
                    Intent intent = new Intent(SplashActivity.this, RoleActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }

            }
        }, 3000);

        mContext = getApplicationContext();

    }

    @Override
    public void onBackPressed() {

    }

}