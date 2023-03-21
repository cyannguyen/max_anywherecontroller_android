package com.ibm.graphite.maximomobile;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.Environment;
import android.view.WindowManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LoggingPlugin extends CordovaPlugin {

    private static final String SAVE_LOGS = "savelogs";
    private static String filename = "MaximoMobile_Logs.txt";
    public static boolean initialized = false;
    public static long fileSize = 51200; //50 MB
    public static int fileRotate = 5;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
        if(!initialized){
            startLogging();
        }

        initialized = true;
    }


    public void startLogging() {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                File outputFile = new File(Environment.getExternalStorageDirectory() + "/" + "Android" + "/" + "data" + "/" + cordova.getActivity().getPackageName() + "/" + "cache",
                        filename);

                try {
                    String command = "logcat -r " + fileSize + " -n " + fileRotate + " -f " + outputFile.getAbsolutePath();
                    Runtime.getRuntime().exec(command);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {

            if (SAVE_LOGS.equals(action)) {


                callbackContext.success(SAVE_LOGS);
                return true;

            } else {
                callbackContext.error("invalid action");
                return false;
            }
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }
    }
}