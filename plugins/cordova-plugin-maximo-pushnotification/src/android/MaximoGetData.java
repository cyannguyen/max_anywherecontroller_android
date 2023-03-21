package com.ibm.maximo.pushnotification;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import java.util.logging.Logger;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.google.firebase.iid.FirebaseInstanceId;

import android.util.Log;


/**
 * Get from Maximo
 */
public class MaximoGetData extends AsyncTask<String,Void, JSONObject> {

    Activity myactivity;

    private static final String TAG = "MaximoPush-MaximoGetData";
    private static final String className = MaximoGetData.class.getName();
    private static final Logger logger = Logger.getLogger(className);
    private String maxUrl="";
    private String username ="";
    private String password = "";
    private String deviceId = "";
    private int connectionTimeout=10000;
    private String apikey = "";
    private boolean authAPIKeyBased = false;
    protected String message = null;
    IMaximoPushListener maximoPushListener;


    public MaximoGetData(Activity m, String maxUrl, String username, String password, IMaximoPushListener maximoPushListener)
    {
        this.myactivity = m;
        this.maxUrl = maxUrl;
        this.username = username;
        this.password = password;
        this.maximoPushListener = maximoPushListener;
    }

    public MaximoGetData(Activity m, String maxUrl, String apikey, IMaximoPushListener maximoPushListener)
    {
        this.myactivity = m;
        this.maxUrl = maxUrl;
        this.apikey = apikey;
        authAPIKeyBased = true;
        this.maximoPushListener = maximoPushListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
     }

    @Override
    protected JSONObject doInBackground(String... params) {

        JSONObject jo = new JSONObject();
        try {
            URL url = new URL(maxUrl);
            Log.i(TAG, "Maximo URL : "+ maxUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod("GET");
                //urlConnection.setConnectTimeout(connectionTimeout);
                urlConnection.addRequestProperty("Content-Type", "application/json");
                urlConnection.setDoInput(true);

                if (authAPIKeyBased) {
                    urlConnection.setRequestProperty("apikey", apikey);
                } else {
                    Log.i(TAG, "Username:" + username);
                    String encodedMaxAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
                    urlConnection.setRequestProperty("maxauth", encodedMaxAuth);
                }

                urlConnection.connect();
                Log.i(TAG, "Maximo Connection Successful");
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                byte[] responseBody = MaximoUtil.getByteArrayFromInputStream(in);

                if (responseBody.length > 0)
                    jo = new JSONObject(new String(responseBody));

                Log.i(TAG, "Maximo Result :"+ new String(responseBody));

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return jo;
    }


    protected void onPostExecute(JSONObject result) {
        // dismiss progress dialog and update ui
        super.onPostExecute(result);

        try {
            Log.d(TAG, "Get Result :"+ result );
            String providerName = result.get("providername").toString();
            MaximoPush.setProviderName(result.get("providername").toString());
            MaximoPush.setUsername(result.get("username").toString());

            if ( providerName.equalsIgnoreCase("IPN")) {
                //check if provider is IBM
                String clientSecret = result.get("clientsecret").toString();
                String appGUID = result.get("appguid").toString();
                String region = result.get("bluemixregion").toString();
                initPush(clientSecret, appGUID, region);
            }

            if ( apikey == null || apikey.equals(""))
                MaximoPush.register(myactivity, maxUrl, username, password, maximoPushListener);
            else
                MaximoPush.registerwithAPIKey(myactivity, maxUrl, apikey, maximoPushListener);

        } catch (Exception e) {
            e.printStackTrace();
            if( this.maximoPushListener != null) {
                this.maximoPushListener.failure("provider service not initialized");
            }
        }
        if( this.maximoPushListener != null) {
            this.maximoPushListener.success("success", result);
        }
    }


    protected void initPush(String clientSecret, String appGUID, String region) {
        BMSClient.getInstance().initialize(myactivity, region);
        MFPPush.getInstance().initialize(myactivity, appGUID, clientSecret);

        Log.i(TAG, "MFP Initialized");
    }

}
