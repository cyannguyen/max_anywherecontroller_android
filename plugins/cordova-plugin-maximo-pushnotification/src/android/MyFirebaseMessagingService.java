package com.ibm.maximo.pushnotification;

import android.app.Activity;
import android.app.PendingIntent;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import android.support.v4.app.TaskStackBuilder;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONObject;

import java.util.Random;
import java.util.Map;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.media.RingtoneManager;
import android.net.Uri;

import org.json.JSONObject;
import org.json.JSONException;
import android.app.ActivityManager;
import java.util.List;
import android.app.ActivityManager.RunningTaskInfo;

/**
 * Service called when firebase cloud message received
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MaxPush-MyFirebaseMsgService-";
    private static final String className = MyFirebaseMessagingService.class.getName();
    private NotificationManager notificationManager;
    public final static String NOTIFICATION_ICON_KEY = "com.google.firebase.messaging.default_notification_icon";
    public final static String NOTIFICATION_CHANNEL_KEY = "com.google.firebase.messaging.default_notification_channel_id";
    private int defaultNotificationIcon;
    private String defaultNotificationChannel;
    public Context activity;
    String msg = "";
    String title = "";
    String[] msgPropertyList ;


    @Override
    public void onCreate() {
        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            this.defaultNotificationIcon = ai.metaData.getInt(NOTIFICATION_ICON_KEY, ai.icon);
            this.defaultNotificationChannel = ai.metaData.getString(NOTIFICATION_CHANNEL_KEY, "CHANNEL_1");
            activity = getApplicationContext();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data", e);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Failed to load notification color", e);
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived");
        super.onMessageReceived(remoteMessage);

        try {
            String providerName = MaximoPush.getProviderName();
            MaximoPushMessageImpl maximoPushMessageImpl = new MaximoPushMessageImpl(getNotificationData(remoteMessage));
            if (providerName.equalsIgnoreCase("IPN")) {
                processIPNMessages(maximoPushMessageImpl);
            } else {
                processFCMMessages(maximoPushMessageImpl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processIPNMessages( MaximoPushMessageImpl maximoPushMessageImpl) {
        try {
            Log.i(TAG, "processIPNMessages");

            String msg = maximoPushMessageImpl.getMessage();
            String title = maximoPushMessageImpl.getTitle();

            Log.i(TAG+"IPN-msg: ", msg);
            Log.i(TAG+"IPN-title: ", title);

            if (isRunning(activity) && !MaximoPush.isBackground) {
                MaximoPush.notifyListerners(maximoPushMessageImpl);
                System.out.println(TAG+"Maximo messages: " + MaximoPush.mylistenerlist);
            } else { //background message
                MaximoPush.msgArray.put(msg);
                System.out.println(TAG+"Maximo background messages: " + MaximoPush.msgArray);
            }
            if (MaximoPush.viewNotification) {
                //MaximoPush.sendNotification(msg, title, this.getApplicationContext());
                sendNotification(msg, title);
            }
        } catch ( Exception e){
            e.printStackTrace();
        }
    }

    public void processFCMMessages( MaximoPushMessageImpl maximoPushMessageImpl) {
        Log.i(TAG, "processFCMMessages");
         msg = maximoPushMessageImpl.getMessage();
        title = maximoPushMessageImpl.getTitle();

        Log.i(TAG + "FCM-msg: ", msg);
        Log.i(TAG + "FCM-title: ", title);

        if (isRunning(activity) && !MaximoPush.isBackground) {
            MaximoPush.notifyListerners(maximoPushMessageImpl);
            System.out.println(TAG+"Maximo messages: " + MaximoPush.mylistenerlist);
        } else { //background message
            // MaximoPush.msgArray.put(notificationData);
            MaximoPush.msgArray.put(msg);
            System.out.println(TAG+"Maximo background messages: " + MaximoPush.msgArray);
        }

        if (MaximoPush.viewNotification) {
            sendNotification(msg, title);
        }
    }

    static JSONObject getNotificationData(RemoteMessage remoteMessage) {
        JSONObject notificationData = new JSONObject(remoteMessage.getData());
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        try {
            if (notification != null) {
                Log.i(TAG+"data1:", notification.getBody());
                JSONObject jsonNotification = new JSONObject();
                jsonNotification.put("body", notification.getBody());
                jsonNotification.put("title", notification.getTitle());
                jsonNotification.put("sound", notification.getSound());
                jsonNotification.put("icon", notification.getIcon());
                jsonNotification.put("tag", notification.getTag());
                jsonNotification.put("color", notification.getColor());
                jsonNotification.put("clickAction", notification.getClickAction());
                notificationData.put("gcm", jsonNotification);
            }
            notificationData.put("google.message_id", remoteMessage.getMessageId());
            notificationData.put("google.sent_time", remoteMessage.getSentTime());
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationData.put("sound", defaultSoundUri);

            System.out.println("MyFirebaseMessaging - Notification data: "+ notificationData);
        } catch (JSONException e) {
            Log.e(TAG, "sendNotification", e);
        }
        return notificationData;
    }

    /**
     * Create and show a simple notification containing the received FCM/IPN message.
     *
     * @param title
     */
    public void sendNotification(String msg, String title) {
        activity = this.getApplicationContext();
        try {
            // Create an Intent for the activity you want to start
            String packageName = activity.getPackageName();
            Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
            String pkgClassName = launchIntent.getComponent().getClassName();

            Intent resultIntent = new Intent(activity, Class.forName(pkgClassName));
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            resultIntent.setAction("android.intent.action.MAIN");
            resultIntent.addCategory("android.intent.category.LAUNCHER");

            // Get the PendingIntent containing the entire back stack
            PendingIntent resultPendingIntent =PendingIntent.getActivity(activity, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Log.i(TAG + "Channel Id: ", defaultNotificationChannel);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            CharSequence channelName = "Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(defaultNotificationChannel, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(activity, defaultNotificationChannel)
                            //.setSmallIcon(R.drawable.ic_launcher)
                            .setSmallIcon(this.defaultNotificationIcon)
                            .setChannelId(defaultNotificationChannel)
                            .setContentTitle(title)
                            .setContentText(msg)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(resultPendingIntent);

            if ( isRunning(activity)) {
                notificationManager.notify(getRequestCode(), notificationBuilder.build());
            }
            else {
                startForeground(getRequestCode(), notificationBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getRequestCode() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(900000);
    }

    /**
     * Checks if application is running.
     *
     * @param context ctx
     * @return true or false
     */
    public boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }
        return false;
    }
}
