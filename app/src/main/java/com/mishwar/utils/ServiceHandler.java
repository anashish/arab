package com.mishwar.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Dharmraj on 3/12/2016.
 */
public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    public final static int GET_Param = 3;
    public final static int POST_ENTITY = 4;
    int responseCode;

    public ServiceHandler() {
    }

    public int getResponceCode()
    {
        return responseCode;
    }

    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    public String makeServiceCall(String url, int method, List<NameValuePair> params) {

        try {

            // set timeout
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.//
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 60000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 60000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method==POST_ENTITY){

                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                    httpPost.setHeader(params.get(0).getName(), params.get(0).getValue());
                }
                httpResponse = httpClient.execute(httpPost);

            }else if (method == GET) {
                // appending params to url
                HttpGet httpGet = new HttpGet(url);
                if (params != null)
                {
                    String paramString = URLEncodedUtils.format(params,  "UTF-8");
                    // url += "?" + paramString;
                    for (int i=0; i<params.size(); i++)
                    {
                        httpGet.setHeader(params.get(i).getName(), params.get(i).getValue());
                    }
                }
                httpResponse = httpClient.execute(httpGet);
            }
            else if (method==GET_Param) {
                // appending params to url
                // String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + (params.get(1).getName()+"="+ params.get(1).getValue());
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader(params.get(0).getName(), params.get(0).getValue());
                httpResponse = httpClient.execute(httpGet);
                Log.d("Url: ", "> " + url);
            }

            httpEntity = httpResponse.getEntity();
            responseCode = httpResponse.getStatusLine().getStatusCode();
            response = EntityUtils.toString(httpEntity, "UTF-8");
            Log.d("Responce:", "> " + response);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}