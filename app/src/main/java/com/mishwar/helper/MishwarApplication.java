package com.mishwar.helper;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by GST-10018 on 3/7/2016.
 */
public class MishwarApplication extends MultiDexApplication {

    public static MishwarApplication instance = null;
    public static String gcmRegId;

    public Activity currentActiveActivity;

    public int tapCount = 0;

    public int tapLimit = 4;


    public static final String TAG = MishwarApplication.class.getSimpleName();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public static MishwarApplication getInstance()
    {
        if (instance != null) {
            return instance;
        }
        return new MishwarApplication();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
    }


}

