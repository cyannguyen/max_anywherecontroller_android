package de.cyberkatze.iroot;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;  
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.security.MessageDigest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Map;
import java.util.HashMap;

import java.lang.Exception;

import com.scottyab.rootbeer.RootBeer;

import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

/**
 * Detect weather device is rooted or not
 *
 * @author Ali Elderov
 */
public class IRoot extends CordovaPlugin {

    private final String LOG_TAG = "IRoot";

    private final boolean WITH = true;

    private enum Action {
        // Actions
        ACTION_IS_ROOTED("isRooted"),
        ACTION_IS_ROOTED_REDBEER("isRootedRedBeer"),
        ACTION_IS_ROOTED_REDBEER_WITHOUT_BUSYBOX("isRootedRedBeerWithoutBusyBox");

        private final String name;

        private static final Map<String, Action> lookup = new HashMap<String, Action>();

        static {
            for (Action a : Action.values()) {
                lookup.put(a.getName(), a);
            }
        }

        private Action(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Action get(final String name) {
            return lookup.get(name);
        }
    }

    /**
     * helper fn that logs the err and then calls the err callback
     */
    private PluginResult error(final String message, final Throwable e) {
        LOG.e(LOG_TAG, message, e);
        return new PluginResult(Status.ERROR, message);
    }

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        // throws JSONException
        Action act = Action.get(action);

        if (act == null) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    LOG.e(LOG_TAG, "unknown action");
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "unknown action"));
                }
            });

            return false;
        }

        switch (act) {
            case ACTION_IS_ROOTED:
                cordova.getThreadPool().execute(new Runnable() {

                    @Override
                    public void run() {
                        PluginResult result;

                        try {
                            result = checkIsRooted(args, callbackContext);
                        } catch (Exception e) {
                            result = new PluginResult(PluginResult.Status.ERROR, e.toString());
                        }

                        callbackContext.sendPluginResult(result);
                    }
                });

                return true;
            case ACTION_IS_ROOTED_REDBEER:
                cordova.getThreadPool().execute(new Runnable() {

                    @Override
                    public void run() {
                        PluginResult result;

                        try {
                            result = checkIsRootedRedBeer(args, callbackContext);
                        } catch (Exception e) {
                            result = new PluginResult(PluginResult.Status.ERROR, e.toString());
                        }

                        callbackContext.sendPluginResult(result);
                    }
                });

                return true;
            case ACTION_IS_ROOTED_REDBEER_WITHOUT_BUSYBOX:
                cordova.getThreadPool().execute(new Runnable() {

                    @Override
                    public void run() {
                        PluginResult result;

                        try {
                            result = checkIsRootedRedBeerWithoutBusyBox(args, callbackContext);
                        } catch (Exception e) {
                            result = new PluginResult(PluginResult.Status.ERROR, e.toString());
                        }

                        callbackContext.sendPluginResult(result);
                    }
                });

                return true;
            default:
                cordova.getActivity().runOnUiThread(new Runnable() {

                    public void run() {
                        LOG.e(LOG_TAG, "unknown action");
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "unknown action"));
                    }
                });
                return false;
        }
    }

    private boolean isDeviceRooted() {
        return checkBuildTags() || checkSuperUserApk() || checkFilePath();
    }

    private boolean checkBuildTags() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean checkSuperUserApk() {
        return new File("/system/app/Superuser.apk").exists();
    }

    private boolean checkFilePath() {
        String[] paths = {
                        "/sbin/su",
                        "/system/bin/su",
                        "/system/xbin/su",
                        "/data/local/xbin/su",
                        "/data/local/bin/su",
                        "/system/sd/xbin/su",
                        "/system/bin/failsafe/su",
                        "/data/local/su"
        };

        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }

        return false;
    }

    // ************************************************
    // ACTION HANDLERS
    // - return true:
    // - to indicate action was executed with correct arguments
    // - also if the action from sdk has failed.
    // - return false:
    // - arguments were wrong
    // ************************************************

    /**
     * Simple check with rootBeer
     */
    private PluginResult checkIsRootedRedBeer(final JSONArray args, final CallbackContext callbackContext) {
        try {
            RootBeer rootBeer = new RootBeer(this.cordova.getActivity().getApplicationContext());

            return new PluginResult(Status.OK, rootBeer.isRooted());
        } catch (Exception e) {
            return this.error("Error", e);
        }
    }

    /**
     * Simple check with rootBeer without BusyBox
     */
    private PluginResult checkIsRootedRedBeerWithoutBusyBox(final JSONArray args, final CallbackContext callbackContext) {
        try {
            RootBeer rootBeer = new RootBeer(this.cordova.getActivity().getApplicationContext());

            return new PluginResult(Status.OK, rootBeer.isRootedWithoutBusyBoxCheck());
        } catch (Exception e) {
            return this.error("Error", e);
        }
    }

    /**
     * Combine simple check and rootBeer. User can activate check with plugin option ENABLE_BUSYBOX_CHECK -> put argument with
     * TRUE/FALSE.
     */
    private PluginResult checkIsRooted(final JSONArray args, final CallbackContext callbackContext) {
        try {
            RootBeer rootBeer = new RootBeer(this.cordova.getActivity().getApplicationContext());

            boolean check = isDeviceRooted() || ((this.WITH) ? rootBeer.isRootedWithoutBusyBoxCheck() : rootBeer.isRooted());

            // MD5 checksum doesn't match
            boolean checkMD5 = !this.checkMD5();
            
            return new PluginResult(Status.OK, check || checkMD5);
        } catch (Exception e) {
            return this.error("Error", e);
        }
    }

    private boolean checkMD5() throws FileNotFoundException, IOException {
        InputStream in = cordova.getActivity().getAssets().open("www/js/appdownload.md5");
        byte[] buffer = new byte[1024];
        in.read(buffer);
        String appdownloadMD5File = new String(buffer);
        appdownloadMD5File = appdownloadMD5File.split("\u0000")[0];
        StringBuilder sb = new StringBuilder();
        try {
            String fileName = appdownloadMD5File.split(":")[1];

            InputStream infile = cordova.getActivity().getAssets().open("www/js/" + fileName);
            byte[] bufferFile = new byte[1048576];
            infile.read(bufferFile);
            String valueInFile = new String(bufferFile);
            valueInFile = valueInFile.split("\u0000")[0];
            MessageDigest mdigest = MessageDigest.getInstance("MD5");

            // read the data from file and update that data in
            // the message digest
            mdigest.update(valueInFile.getBytes());

            // store the bytes returned by the digest() method
            byte[] bytes = mdigest.digest();

            // this array of bytes has bytes in decimal format
            // so we need to convert it into hexadecimal format
            // for this we create an object of StringBuilder
            // since it allows us to update the string i.e. its
            // mutable
            sb = new StringBuilder();

            // loop through the bytes array
            for (int i = 0; i < bytes.length; i++) {
                // decimal into hexadecimal format
                sb.append(Integer
                        .toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
        } catch (Exception e) {
            if (e instanceof FileNotFoundException || e instanceof IOException)
                return true;
            return false;
        }
        String appdownloadMD5 = appdownloadMD5File.split(":")[2];
        return appdownloadMD5.contentEquals(sb);
    }
}
