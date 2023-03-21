package com.ibm.maximo.pushnotification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ParseException;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.app.PendingIntent;
import android.content.res.Resources;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.RemoteMessage;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.io.File;
import java.io.InputStreamReader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.logging.Logger;
import java.util.Base64;
import java.util.ArrayList;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import android.media.RingtoneManager;
import android.net.Uri;

public class MaximoPush {
    private static final String TAG = "MaxPush-MaximoPush-";
    private static final String className = MaximoPush.class.getName();
    private static final Logger logger = Logger.getLogger(className);

    private static boolean authAPIKeyBased = false;
    private static String providerName = "";
    private static String userName = "";
    private static final String MAXIMOPUSHSURL ="/api/pushnotif/android";
    private static IMaximoPushListener curMaximoPushListener;
    public static int defaultNotificationIcon ;
    public static String defaultChannel ;

    private static String fcmGoogleServicesFolder;
    public static String fcmGoogleServicesFile="google-services.json" ;
    public static ArrayList msgList = new ArrayList();

    public static JSONArray msgArray = new JSONArray();

    static MFPPush push; // Push client
    private MFPPushNotificationListener notificationListener; // Notification listener to handle a push sent to the phone

    public static IMaximoPushMsgListener mylistener ;
    public static ArrayList <IMaximoPushMsgListener> mylistenerlist = new ArrayList<IMaximoPushMsgListener>();

    public static boolean viewNotification = true;
    public static String NOTIFICATION_ICON_KEY = "com.google.firebase.messaging.default_notification_icon";

    public static Activity activity = null;
    public static boolean isBackground = false;

    public static String eventName = "";
       /**
     * This method register the device to Maximo Push Notification service.
     *
     * @param activity
     * @param maxurl
     * @param username
     * @param password
     * @param maximoPushListener
     */
    public static void subscribe(Activity activity, String maxurl, String username, String password, IMaximoPushListener maximoPushListener)
    {
        Log.i(TAG, "subscribe");

        boolean isDataValid = validateData(maxurl, username, password);
        if ( isDataValid == false)
            return;

        String url = maxurl + MAXIMOPUSHSURL;
        curMaximoPushListener = maximoPushListener;

        MaximoGetData mxData = null;
        mxData = new MaximoGetData(activity, url, username, password, maximoPushListener);
        mxData.execute();
    }

    /**
     * This method register the device to Maximo Push Notification service.
     *
     * @param activity
     * @param maxurl
     * @param apikey
     * @param maximoPushListener
     */
    public static void subscribewithAPIKey(Activity activity, String maxurl, String apikey, IMaximoPushListener maximoPushListener)
    {
        Log.d(TAG, "subscribewithAPIKey");

        authAPIKeyBased = true;
        boolean isDataValid = validateData(maxurl,apikey);
        if (isDataValid == false)
            return;

        String url = maxurl + MAXIMOPUSHSURL;
        curMaximoPushListener = maximoPushListener;
        MaximoGetData mxData = null;
        mxData = new MaximoGetData(activity, url, apikey, maximoPushListener);
        mxData.execute();
    }


    /**
     * This method register the device to Maximo Push Notification service.
     *
     * @param activity
     * @param maxurl
     * @param username
     * @param password
     * @param maximoPushListener
     */
    public static void subscribe(Activity activity, String maxurl, String username, String password, String eventname, IMaximoPushListener maximoPushListener)
    {
        Log.i(TAG, "subscribe");
        eventName = eventname;
        MaximoPush.subscribe(activity, maxurl, username, password, maximoPushListener);
    }

    /**
     * This method register the device to Maximo Push Notification service.
     *
     * @param activity
     * @param maxurl
     * @param apikey
     * @param maximoPushListener
     */
    public static void subscribewithAPIKey(Activity activity, String maxurl, String apikey, String eventname, IMaximoPushListener maximoPushListener)
    {
        Log.i(TAG, "subscribewithAPIKey");
        eventName = eventname;
        subscribewithAPIKey(activity, maxurl, apikey, maximoPushListener);
    }

    /**
     * This method register the device
     * @param activity
     * @param maxurl
     * @param username
     * @param password
     * @param maximoPushListener
     */
    public static void register(Activity activity, String maxurl, String username, String password, IMaximoPushListener maximoPushListener)
    {
        Log.i(TAG, "register");

        boolean isDataValid = validateData(maxurl, username, password);
        if ( isDataValid == false)
            return;

        if ( getProviderName().equalsIgnoreCase("FCM"))
            // registerDeviceFCM(activity, url, username, password, maximoPushListener);
            generateDeviceIdFCM(activity, maxurl, username, password, maximoPushListener);
        else
            generateDeviceIdIBM(activity, maxurl, username, password, maximoPushListener);

    }


    public static void registerwithAPIKey(Activity activity, String maxurl, String apikey, IMaximoPushListener maximoPushListener)
    {
        Log.d(TAG, "registerwithAPIKey");

        boolean isDataValid = validateData(maxurl, apikey);
        if ( isDataValid == false)
            return;

        if ( getProviderName().equalsIgnoreCase("FCM"))
            generateDeviceIdFCM(activity, maxurl, apikey, maximoPushListener);
        else
            generateDeviceIdIBM(activity, maxurl, apikey, maximoPushListener);
    }


    /**
     * This method adds the listener to receive a callback onmessage received.
     *
     * @param maximoPushMsgListener
     * @param activity
     */
    public static void listen(final IMaximoPushMsgListener maximoPushMsgListener, Activity activity){

        mylistenerlist.add(maximoPushMsgListener);
        Log.i(TAG, "Maximo Push Listerner added to listener list");
    }

    /**
     * This method notifies the listeners on message received.
     *
     * @param maximoPushMessageImpl
     */
    public static void notifyListerners( MaximoPushMessageImpl maximoPushMessageImpl) {
        Log.i(TAG, "notifyListerners");
        for (IMaximoPushMsgListener mylistner: MaximoPush.mylistenerlist)
        {
            mylistner.onReceive(maximoPushMessageImpl);
        }
    }

    /**
     * This method generates the device id for the client app instance for FCM.
     *
     * @param activity
     * @param maxurl
     * @param username
     * @param password
     * @param maximoPushListener
     */
    public static void generateDeviceIdFCM(final Activity activity, final String maxurl, final String username, final String password, final IMaximoPushListener maximoPushListener) {

        Log.i(TAG, "generateDeviceIdFCM");
        MaximoPostData mxData = null;
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i(TAG, "Firebase getInstanceId failed " + task.getException() );
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        //Log.i(TAG, "registerDeviceFCM token: " + token );
                        setDeviceId(token);
                        sendDeviceIdtoServer(activity, maxurl, username, password, maximoPushListener);
                    }
                });

    }

    /**
     * This method generates the device id for the client app instance for FCM.
     *
     * @param activity
     * @param maxurl
     * @param apikey
     * @param maximoPushListener
     */
    public static void generateDeviceIdFCM(final Activity activity, final String maxurl, final String apikey, final IMaximoPushListener maximoPushListener) {

        Log.i(TAG, "generateDeviceIdFCMAPIKey");
        MaximoPostData mxData = null;
        // String url = maxurl + MAXIMOPUSHSURL;
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i(TAG, "Firebase getInstanceId failed " + task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        setDeviceId(token);
                        sendDeviceIdtoServer(activity, maxurl, apikey, maximoPushListener);
                    }
                });

    }

    /**
     * This method registers the device to the IBM push notification service.
     *
     * @param activity
     * @param maxurl
     * @param username
     * @param password
     * @param maximoPushListener
     */
    public static void generateDeviceIdIBM(final Activity activity, final String maxurl, final String username, final String password, final IMaximoPushListener maximoPushListener)  {

        Log.i(TAG, "generateDeviceIdIBM");
        // String url = maxurl + MAXIMOPUSHSURL;
        MFPPush.getInstance().registerDeviceWithUserId(getUsername(), new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String response) {
                // Split response and convert to JSON object to display User ID confirmation from the backend
                String[] resp = response.split("Text: ");
                try {
                    JSONObject responseJSON = new JSONObject(resp[1]);
                    String deviceId = responseJSON.get("deviceId").toString();

                    MaximoPush.setDeviceId(deviceId);
                    Log.i(TAG, deviceId);
                    MaximoPush.curMaximoPushListener.success("Device Registered", null);

                    sendDeviceIdtoServer(activity, maxurl, username, password, maximoPushListener);

                } catch (JSONException e) {
                    Log.e(TAG,"unexpected JSON Exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(MFPPushException exception) {
                String errLog = "Error registering for push notifications: ";
                String errMessage = exception.getErrorMessage();
                int statusCode = exception.getStatusCode();

                // Set error log based on response code and error message
                if (statusCode == 401) {
                    errLog += "Cannot authenticate successfully with Bluemix Push instance, ensure your CLIENT SECRET was set correctly.";
                } else if (statusCode == 404 && errMessage.contains("Push GCM Configuration")) {
                    errLog += "Push GCM Configuration does not exist, ensure you have configured GCM Push credentials on your Bluemix Push dashboard correctly.";
                } else if (statusCode == 404 && errMessage.contains("PushApplication")) {
                    errLog += "Cannot find Bluemix Push instance, ensure your APPLICATION ID was set correctly and your phone can successfully connect to the internet.";
                } else if (statusCode >= 500) {
                    errLog += "Bluemix and/or your Push instance seem to be having problems, please try again later.";
                }
                Log.e(TAG, errLog, exception);
                MaximoPush.curMaximoPushListener.failure(errLog);
            }
        });
    }

    /**
     * This method registers the device to the IBM push notification service.
     *
     * @param activity
     * @param maxurl
     * @param apikey
     * @param maximoPushListener
     */
    public static void generateDeviceIdIBM(final Activity activity, final String maxurl, final String apikey, final IMaximoPushListener maximoPushListener)  {

        Log.i(TAG, "generateDeviceIdIBMAPIKey");
        // String url = maxurl + MAXIMOPUSHSURL;
        String username = getUsername();
        MFPPush.getInstance().registerDeviceWithUserId(username, new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String response) {
                // Split response and convert to JSON object to display User ID confirmation from the backend
                String[] resp = response.split("Text: ");
                try {
                    JSONObject responseJSON = new JSONObject(resp[1]);
                    String deviceId = responseJSON.get("deviceId").toString();

                    MaximoPush.setDeviceId(deviceId);
                    MaximoPush.curMaximoPushListener.success("Device Registered", null);
                    sendDeviceIdtoServer(activity, maxurl, apikey, maximoPushListener);

                } catch (JSONException e) {
                    Log.e(TAG,"unexpected JSON Exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(MFPPushException exception) {
                String errLog = "Error registering for push notifications: ";
                String errMessage = exception.getErrorMessage();
                int statusCode = exception.getStatusCode();

                // Set error log based on response code and error message
                if (statusCode == 401) {
                    errLog += "Cannot authenticate successfully with Bluemix Push instance, ensure your CLIENT SECRET was set correctly.";
                } else if (statusCode == 404 && errMessage.contains("Push GCM Configuration")) {
                    errLog += "Push GCM Configuration does not exist, ensure you have configured GCM Push credentials on your Bluemix Push dashboard correctly.";
                } else if (statusCode == 404 && errMessage.contains("PushApplication")) {
                    errLog += "Cannot find Bluemix Push instance, ensure your APPLICATION ID was set correctly and your phone can successfully connect to the internet.";
                } else if (statusCode >= 500) {
                    errLog += "Bluemix and/or your Push instance seem to be having problems, please try again later.";
                }

                Log.e(TAG, errLog, exception);
                MaximoPush.curMaximoPushListener.failure(errLog);
            }
        });
    }

    /**
     * This method unsubscribes the device to the push notification service.
     *
     * @param activity
     * @param maxurl
     * @param username
     * @param password
     * @param maximoPushListener
     */
    public static void unsubscribe(Activity activity, String maxurl, String username, String password, IMaximoPushListener maximoPushListener)
    {
        Log.i(TAG, "unsubscribe");
        boolean isDataValid = validateData(maxurl, username, password);

        if (isDataValid == false)
            return;
        String url = maxurl + MAXIMOPUSHSURL;
        setUsertoInactive(activity, url, username, password, maximoPushListener);
    }

    /**
     * This method unsubscribes the device to the push notification service.
     *
     * @param activity
     * @param maxurl
     * @param apikey
     * @param maximoPushListener
     */
    public static void unsubscribewithAPIKey(Activity activity, String maxurl, String apikey, IMaximoPushListener maximoPushListener)
    {
        Log.i(TAG, "unsubscribewithAPIKey");
        String url = maxurl + MAXIMOPUSHSURL;
        validateData(maxurl, apikey);
        setUsertoInactive(activity, url, apikey, maximoPushListener);
    }

    public static String deviceId = "";
    public static void setDeviceId(String deviceid) {
        deviceId = deviceid;
        Log.i(TAG, "DeviceId:" +deviceid);
    }

    /**
     * This method sends the deviceid to maximo server.
     *
     * @param activity
     * @param maxurl
     * @param username
     * @param password
     * @param maximoPushListener
     */
    public static void sendDeviceIdtoServer(Activity activity, String maxurl, String username, String password, IMaximoPushListener maximoPushListener) {
        MaximoPostData postTask = new MaximoPostData(activity, maxurl, username, password, maximoPushListener);
        postTask.execute("api", "subscribe", "deviceid", deviceId, "eventname", eventName);
    }

    /**
     * This method sends the deviceid to maximo server.
     *
     * @param activity
     * @param maxurl
     * @param apikey
     * @param maximoPushListener
     */

    public static void sendDeviceIdtoServer(Activity activity, String maxurl, String apikey, IMaximoPushListener maximoPushListener) {

        MaximoPostData postTask = new MaximoPostData(activity, maxurl, apikey, maximoPushListener);
        postTask.execute("api", "subscribe", "deviceid", deviceId, "eventname", eventName);
    }


    /**
     * This method sets the active flag to false. The maximo server will not send the message to the device.
     *
     * @param activity
     * @param maxurl
     * @param username
     * @param password
     * @param maximoPushListener
     */
    public static void setUsertoInactive(Activity activity, String maxurl, String username, String password, IMaximoPushListener maximoPushListener) {
        Log.i("TAG", "setUsertoInactive");

        MaximoPostData postTask = new MaximoPostData(activity, maxurl, username, password, maximoPushListener);
        postTask.execute("api", "unsubscribe", "deviceid", deviceId, "eventname", eventName);
    }

    /**
     * This method sets the active flag to false. The maximo server will not send the message to the device.
     *
     * @param activity
     * @param maxurl
     * @param apikey
     * @param maximoPushListener
     */
    public static void setUsertoInactive(Activity activity, String maxurl, String apikey, IMaximoPushListener maximoPushListener) {

        Log.i("TAG", "setUsertoInactive(ApiKey)");
        MaximoPostData postTask = new MaximoPostData(activity, maxurl, apikey, maximoPushListener);
        postTask.execute("api", "unsubscribe", "deviceid", deviceId, "eventname", eventName);
    }

    /**
     * This method returns the push notification service provider name.
     *
     * @return providername
     */
    public static String getProviderName() {
        return MaximoPush.providerName;
    }

    /**
     * This method sets the push notification service provider name.
     *
     * @param provider
     */
    public static void setProviderName (String provider) {
        if (! (("").equals(provider)))
            MaximoPush.providerName = provider;
    }

    /**
     * This method set sthe username.
     *
     * @param username
     */
    public static void setUsername(String username) {
        MaximoPush.userName = username;
    }

    /**
     * This method returns the username.
     *
     * @return username
     */
    public static String getUsername() {
        return MaximoPush.userName;
    }

    /**
     * Get Google-Service json folder.
     *
     * @return
     */
    public static String getFCMGoogleServicesFolder() {

        Log.i("TAG", "getFCMGoogleServicesFolder");
        return fcmGoogleServicesFolder;
    }

    /**
     * This method sets the folder for google-services.json file.
     *
     * @param servicesFolderolder
     */
    public static void setFCMGoogleServicesFolder(String servicesFolderolder) {

        Log.i("TAG", "setFCMGoogleServicesFolder");
        try {
            fcmGoogleServicesFolder = servicesFolderolder;
        }catch ( Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method validates data.
     *
     * @param maxurl
     * @param username
     * @param password
     */

    private static boolean validateData(String maxurl, String username, String password){

        Log.i("TAG", "validateData");

        boolean isDataValid = true;
        if ( maxurl.equals("")) {
            Log.e("TAG", "Maximo url is not set");
            isDataValid = false;
        }

        if (username.equals("")) {
            Log.e("TAG", "Maximo username is not set");
            isDataValid = false;
        }

        if (password.equals(""))  {
            Log.e("TAG", "Maximo password is not set");
            isDataValid = false;
        }
        return isDataValid ;
    }

    /**
     * This method validates data.
     *
     * @param maxurl
     * @param apikey
     */

    private static boolean validateData(String maxurl, String apikey){

        Log.i("TAG", "validateData");
        boolean isDataValid = true;

        if ( maxurl.equals("")) {
            Log.e("TAG", "Maximo url is not set");
            isDataValid = false;
        }

        if (authAPIKeyBased && apikey.equals("")) {
            Log.e("TAG", "Maximo API key is not set");
            isDataValid = false;
        }

        return isDataValid;
    }
}


