package com.mishwar.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by mindiii on 10/8/16.
 */
public class ConnectionDetector {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager onGoingCM = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = onGoingCM.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
