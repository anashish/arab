package com.mishwar.listner;

import org.json.JSONObject;

/**
 * Created by DELL on 3/11/2016.
 */
public interface ResultListener {

    void onPostExecute(JSONObject jsonObject, String requestCode);
}
