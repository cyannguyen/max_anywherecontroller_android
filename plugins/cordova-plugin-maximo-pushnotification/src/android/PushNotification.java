package com.ibm.maximo.pushnotification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationManagerCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;
import java.util.ArrayList;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import android.app.Activity;


public class PushNotification extends CordovaPlugin {

	private static CallbackContext cct;

	private static PushNotification instance;
	private static Context context;
	Activity activity;
	private static final String TAG = "MaxPush-PushNotification-";
	ArrayList<CallbackContext> cctList = new ArrayList<CallbackContext>();

	@Override
	protected void pluginInitialize() {
		PushNotification.instance = this;
		context = cordova.getActivity().getApplicationContext();
		activity = cordova.getActivity();
	}

	@Override
	public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

		Log.i(TAG, "Execute Action: "+ action);
		String methodName = "";
		//CallbackContext callbackContext = instance.cct;

		if (action.equals("subscribe")) {
			Log.i(TAG, "subscribe");
			instance.cct = callbackContext;

			String maxurl = data.getString(0);
			String username = data.getString(1);
			String password = data.getString(2);

			subscribe(activity, maxurl, username, password, callbackContext);

			return true;
		} if (action.equals("subscribewithApiKey")) {
			Log.i(TAG, "subscribewithApiKey");

			instance.cct = callbackContext;

			String maxurl = data.getString(0);
			String apikey = data.getString(1);

			subscribewithAPIKey(activity, maxurl, apikey, callbackContext);

			return true;
		} if (action.equals("subscribeEvent")) {
			Log.i(TAG, "subscribeEvent");
			instance.cct = callbackContext;

			String maxurl = data.getString(0);
			String username = data.getString(1);
			String password = data.getString(2);
			String eventname = data.getString(3);

			subscribeEvent(activity, maxurl, username, password, eventname, callbackContext);

			return true;
		} if (action.equals("subscribeEventwithApiKey")) {
			Log.i(TAG, "subscribeEventwithApiKey");

			instance.cct = callbackContext;

			String maxurl = data.getString(0);
			String apikey = data.getString(1);
			String eventname = data.getString(2);

			subscribeEventwithAPIKey(activity, maxurl, apikey, eventname, callbackContext);

			return true;
		} else  if (action.equals("unsubscribe")) {
			Log.i(TAG, "unsubscribe");

			instance.cct = callbackContext;

			String maxurl = data.getString(0);
			String username = data.getString(1);
			String password = data.getString(2);
			unsubscribe(activity, maxurl, username, password, callbackContext);

			return true;
		} else if (action.equals("unsubscribewithApiKey")) {
			Log.i(TAG, "unsubscribewithApiKey");
			instance.cct = callbackContext;

			String maxurl = data.getString(0);
			String apikey = data.getString(1);

			unsubscribewithAPIKey(activity, maxurl, apikey, callbackContext);

			return true;
		} else  if (action.equals("listen")) {
			Log.i(TAG, "listen");
			instance.cct = callbackContext;
			boolean viewNotification = data.getBoolean(0);
			MaximoPush.viewNotification = viewNotification;

			MaximoPush.listen(new MyMaximoPushMsgListener(callbackContext), activity);
			return true;
		}  else  if (action.equals("getBackgroundMessages")) {
			Log.i(TAG, "getBackgroundMessages");
			instance.cct = callbackContext;

			JSONObject jo = new JSONObject();
			//	jo.put("backgroundmsgs", MaximoPush.msgList.getJSONObject("notificationData"));
			//	jo.put("backgroundmsgs", MaximoPush.msgArray.getJSONObject("notificationData"));

			JSONArray jaMsg = MaximoPush.msgArray;
			ArrayList msgList = new ArrayList();

			System.out.println(TAG + "background msgs : " + jaMsg);

			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jaMsg.toString());
			pluginResult.setKeepCallback(true);
			instance.cct.sendPluginResult(pluginResult);
			MaximoPush.msgList.clear();
			return true;
		}
		else {
			return false;
		}
	}

	public void subscribe(Activity activity, String maxurl, String username, String password, CallbackContext callbackContext) {
		MaximoPush.subscribe(activity, maxurl, username, password, new IMaximoPushListener<String>() {
			@Override
			public void success(String msg, JSONObject joResult) {
				System.out.println("PushNotification: MaximoPush.subscribe: success");
				Log.i(TAG, "subscribe - success");
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.OK));

			}

			@Override
			public void failure(String msg) {
				Log.i(TAG, "subscribe - failure"+ msg);
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.ERROR));
			}
		});
	}

	public void subscribewithAPIKey(Activity activity, String maxurl, String apikey, CallbackContext callbackContext) {
		MaximoPush.subscribewithAPIKey(activity, maxurl, apikey, new IMaximoPushListener<String>() {
			@Override
			public void success(String msg, JSONObject joResult) {
				Log.i(TAG, "MaximoPush.subscribewithAPIKey - success");
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.OK));
			}

			@Override
			public void failure(String msg) {
				Log.i(TAG, "MaximoPush.subscribewithAPIKey - failure -" + msg);
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.ERROR));
			}
		});
	}


	public void subscribeEvent(Activity activity, String maxurl, String username, String password, String eventname, CallbackContext callbackContext) {
		MaximoPush.subscribe(activity, maxurl, username, password, eventname, new IMaximoPushListener<String>() {
			@Override
			public void success(String msg, JSONObject joResult) {
				System.out.println("PushNotification: MaximoPush.subscribeEvent: success");
				Log.i(TAG, "subscribeEvent - success");
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.OK));

			}

			@Override
			public void failure(String msg) {
				Log.i(TAG, "subscribe - failure"+ msg);
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.ERROR));
			}
		});
	}

	public void subscribeEventwithAPIKey(Activity activity, String maxurl, String apikey, String eventname, CallbackContext callbackContext) {
		MaximoPush.subscribewithAPIKey(activity, maxurl, apikey, eventname, new IMaximoPushListener<String>() {
			@Override
			public void success(String msg, JSONObject joResult) {
				Log.i(TAG, "MaximoPush.subscribeEventwithAPIKey - success");
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.OK));
			}

			@Override
			public void failure(String msg) {
				Log.i(TAG, "MaximoPush.subscribewithAPIKey - failure -" + msg);
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.ERROR));
			}
		});
	}

	public void unsubscribe(Activity activity, String maxurl, String username, String password, CallbackContext callbackContext) {

		MaximoPush.unsubscribe(activity, maxurl, username, password, new IMaximoPushListener<String>() {
			@Override
			public void success(String msg, JSONObject joResult) {
				Log.i(TAG, "MaximoPush.subscribewithAPIKey - success");
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.OK));


			}

			@Override
			public void failure(String msg) {
				System.out.println("PushNotification: unsubscribe: failure"+ msg);
				Log.i(TAG, "maximoPush.subscribewithAPIKey - failure - "+ msg);
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.ERROR));
			}
		});
	}


	public void unsubscribewithAPIKey(Activity activity, String maxurl, String apikey, CallbackContext callbackContext) {
		MaximoPush.unsubscribewithAPIKey(activity, maxurl, apikey, new IMaximoPushListener<String>() {
			@Override
			public void success(String msg, JSONObject joResult) {
				Log.i(TAG, "MaximoPush.unsubscribewithAPIKey - success");
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.OK));


			}

			@Override
			public void failure(String msg) {
				System.out.println("PushNotication: unsubscribe: failure"+ msg);
				Log.i(TAG, "MaximoPush.unsubscribewithAPIKey - failure - "+ msg);
				callbackContext.sendPluginResult(
						new PluginResult(PluginResult.Status.ERROR));
			}
		});
	}

	class MyMaximoPushMsgListener implements IMaximoPushMsgListener {

		CallbackContext mycct;
		MyMaximoPushMsgListener (CallbackContext cct) {
			mycct = cct;
		}

		@Override
		public void onReceive(IMaximoPushMessage maximoPushMessage) {
			Log.i(TAG, "onReceive called");
			try {
				JSONObject jo = new JSONObject();
				String extraDataStr = maximoPushMessage.getExtraData();
				if ( extraDataStr == null || extraDataStr.length() == 0) {
				    if ( maximoPushMessage.getTitle().length() > 0)
					    jo.put("title", maximoPushMessage.getTitle().toString());
					jo.put("message", maximoPushMessage.getMessage().toString());
					Log.i(TAG, "On Receive - message - " + maximoPushMessage.getMessage().toString());
				}
				else {
					jo = new JSONObject(maximoPushMessage.getExtraData());
					Log.i(TAG, "On Receive - extraData - " + maximoPushMessage.getExtraData());
				}

				PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jo);
				pluginResult.setKeepCallback(true);
				mycct.sendPluginResult(pluginResult);

			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

