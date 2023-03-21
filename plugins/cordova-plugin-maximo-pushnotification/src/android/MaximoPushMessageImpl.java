package com.ibm.maximo.pushnotification;

import com.google.firebase.messaging.RemoteMessage;
//import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;
import java.util.HashMap;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONObject;
import java.util.Map;
import android.util.Log;

import org.json.JSONException;

public class MaximoPushMessageImpl implements IMaximoPushMessage {
    RemoteMessage remoteMessage ;

    String providerName;
    JSONObject msgData = null;
    String extraDataStr = null;
    private static final String TAG = "MaxPush-MaximoPushMessageImpl";

    MaximoPushMessageImpl(RemoteMessage message) {
        remoteMessage = message;
    }

    MaximoPushMessageImpl(JSONObject messageData) {
        msgData = messageData;
    }

    public String getMessage() {

        String msg = "";
        String providerName = MaximoPush.getProviderName();
        Log.i(TAG + "Provider Name: ", providerName);


        try {
            if (msgData != null) {
                if (msgData.has("body")) {
                    if ("".equals(providerName)) {
                        MaximoPush.setProviderName("FCM");
                    }
                    msg = msgData.get("body").toString();
                    if ( msgData.has("extradata")) {
                        Log.i(TAG + "extradata", msgData.get("extradata").toString());
                        extraDataStr = msgData.get("extradata").toString();
                    }
                } else if (msgData.has("alert")) {
                    if ("".equals("providerName")) {
                        MaximoPush.setProviderName("IPN");
                    }
                    msg = msgData.get("alert").toString();

                    if ( msgData.has("payload")) {
                        JSONObject payloadObj =  new JSONObject(msgData.get("payload").toString());
                        System.out.println("***********payload: " + payloadObj);
                        if ( payloadObj != null && payloadObj.length() > 0) {
                            if (payloadObj.has("extradata")) {
                                Log.i(TAG + "extradata", payloadObj.get("extradata").toString());
                                extraDataStr = payloadObj.get("extradata").toString();
                            }
                        }
                    }
                }
            }
        } catch (org.json.JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return msg;
    }

      /*  if ( providerName.equalsIgnoreCase("FCM")) {
           // return remoteMessage.getNotification().getBody();
           // String msg = remoteMessage.getNotification().getBody();
            try {
                if (msgData != null) {
                    msg = msgData.get("body").toString();
                }
            } catch ( JSONException je) {
                je.printStackTrace();
                Log.e(TAG, "getMessage", je);
             }

            return msg;
        }
        else {
            Log.i(TAG+"getMessage()","IPN" );
            //return mfpMessage.getAlert().toString();
            if (remoteMessage != null) {
                Map<String, String> messageMap = remoteMessage.getData();
                try {
                    JSONObject joMsg = new JSONObject(messageMap);
                    msg = joMsg.get("alert").toString();
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                return msg;
            }
            else {
                Log.i(TAG, "Remote Message is null");
            }
            return "";
        }*/
    // }

    public String getTitle() {
        String title = "";
      //  if ( MaximoPush.getProviderName().equalsIgnoreCase("FCM")) {
            // return remoteMessage.getNotification().getTitle();
            try {
                if (msgData != null && msgData.has("title")) {
                    title = msgData.get("title").toString();
                }
            } catch (JSONException je) {
                je.printStackTrace();
                Log.e(TAG, "getMessage", je);
            }

        //}
        return title;
    }


    public String getExtraData() {
        if ( extraDataStr == null || extraDataStr.length() == 0)
            return "";
        Log.i(TAG+  " extradata",extraDataStr);
        return extraDataStr;
    }


}
