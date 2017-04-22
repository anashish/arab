package com.mishwar.listner;

import android.location.Location;

/**
 * Created by mindiii on 30/8/16.
 */
public interface OnNewLocationListener {
    public abstract void onNewLocationReceived(Location location);
}
