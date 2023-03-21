package com.ibm.maximo.pushnotification;

import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Logger;
import android.util.Log;

public class MaximoPostData extends AsyncTask<String,Void,Void> {

    private static final String TAG = "MaximoPush-MaximoPostData";
    private static final String className = MaximoPostData.class.getName();
    private static final Logger logger = Logger.getLogger(className);

    private Activity myactivity = null;
    private String maxUrl = "";
    private String username = "";
    private String password = "";
    private String deviceId = "";
    private String apikey = "";
    private int connectionTimeout = 10000;
    private boolean authAPIKeyBased = false;
    private IMaximoPushListener curMaximoPushListener;

    public MaximoPostData(Activity m, String maxUrl, String username, String password, IMaximoPushListener maximoPushListener) {
        myactivity = m;
        this.maxUrl = maxUrl;
        this.username = username;
        this.password = password;
        this.curMaximoPushListener = maximoPushListener;
    }

    public MaximoPostData(Activity m, String maxUrl, String apikey, IMaximoPushListener maximoPushListener) {
        myactivity = m;
        this.maxUrl = maxUrl;
        this.apikey = apikey;
        this.curMaximoPushListener = maximoPushListener;
        authAPIKeyBased = true;
    }

    @Override
    protected void onPreExecute() {
        //display progress dialog.
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        try {

            URL url = new URL(maxUrl);
            Log.i(TAG, "POST Request : "+ maxUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
                if (authAPIKeyBased){
                    urlConnection.setRequestProperty("apikey", apikey);
                }
                else {
                    String encodedMaxAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
                    urlConnection.setRequestProperty("maxauth", encodedMaxAuth);
                }
                urlConnection.addRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject jo = new JSONObject();
                int len = params.length;

                int i = 0;
                while ( i < len ) {
                    jo.put(params[i++], params[i++]);
                }
                Log.i(TAG, "POST Request : "+ jo.toString());

                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(jo.toString());

                writer.flush();
                writer.close();

                StringBuilder sb = new StringBuilder();
                int HttpResult = urlConnection.getResponseCode();
                Log.i(TAG, "******POST Result "+ HttpResult );
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.i(TAG, "POST Success" + sb.toString());
                }
                else{
                    String msg = Integer.toString(HttpResult) + ":"+ urlConnection.getResponseMessage();
                    curMaximoPushListener.failure( msg);
                    Log.i(TAG, "Http response for posting deviceid to Maximo: " + HttpResult + " " + urlConnection.getResponseMessage());
                }
            }
            finally {
                urlConnection.disconnect();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        // dismiss progress dialog and update ui
        super.onPostExecute(result);
        String msg = Integer.toString(HttpURLConnection.HTTP_OK);
        curMaximoPushListener.success(msg, null);
    }

}
