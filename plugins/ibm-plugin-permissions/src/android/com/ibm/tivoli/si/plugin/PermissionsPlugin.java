/*Copyright (C) 2016 Jason Yang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.ibm.tivoli.si.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

/**
 * Created by JasonYang on 2016/3/11.
 */
public class PermissionsPlugin extends CordovaPlugin {

    private static final String ACTION_HAS_PERMISSION = "hasPermission";
    private static final String ACTION_REQUEST_PERMISSION = "requestPermission";
    private static final String ACTION_REQUEST_PERMISSIONS = "requestPermissions";

    private static final int REQUEST_CODE_ENABLE_PERMISSION = 55433;

    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";
    private static final String TAG = "PermissionPlugin";

    private CallbackContext permissionsCallback;

    public static final int SEARCH_REQ_CODE = 0;


    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

        Log.i(TAG,"Executing action " + action);

        if (ACTION_HAS_PERMISSION.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    hasPermissionAction(callbackContext, args);
                }
            });
            return true;
        } else if (ACTION_REQUEST_PERMISSION.equals(action) || ACTION_REQUEST_PERMISSIONS.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        requestPermissionAction(callbackContext, args);
                    } catch (Exception e) {
                        Log.e(TAG,"Error executing action " + action);
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
                        addProperty(returnObj, KEY_MESSAGE, "Request permission has been denied.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        }
        return false;
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {

        Log.i(TAG,"Received call for onRequestPermissionResult ");

        if (permissionsCallback == null) {
            return;
        }

        JSONObject returnObj = new JSONObject();
        if (permissions != null && permissions.length > 0) {
            Log.i(TAG,"Received call for onRequestPermissionResult for on success callback ");
            //Call hasPermission again to verify
            boolean hasAllPermissions = hasAllPermissions(permissions);
            addProperty(returnObj, ACTION_HAS_PERMISSION, hasAllPermissions);
            permissionsCallback.success(returnObj);
        } else {
            Log.i(TAG,"Received call for onRequestPermissionResult for on failure callback ");
            addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
            addProperty(returnObj, KEY_MESSAGE, "Unknown error.");
            permissionsCallback.error(returnObj);
        }
        permissionsCallback = null;
    }

    private void hasPermissionAction(CallbackContext callbackContext, JSONArray permission) {
        Log.i(TAG,"Received call for hasPermissionAction ");

        if (permission == null || permission.length() == 0 || permission.length() > 1) {
            Log.i(TAG,"Received call for hasPermissionAction with no permissions ");
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_ERROR, ACTION_HAS_PERMISSION);
            addProperty(returnObj, KEY_MESSAGE, "One time one permission only.");
            callbackContext.error(returnObj);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(TAG,"Received call for hasPermissionAction for SDK less than Android M" + Build.VERSION.SDK_INT);
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, ACTION_HAS_PERMISSION, true);
            try {
                addProperty(returnObj, ACTION_REQUEST_PERMISSION, permission.getString(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callbackContext.success(returnObj);
        } else {
            try {

                Log.i(TAG,"Received call for hasPermissionAction for SDK " + Build.VERSION.SDK_INT);

                JSONObject returnObj = new JSONObject();
                addProperty(returnObj, ACTION_HAS_PERMISSION, cordova.getActivity().checkSelfPermission(permission.getString(0)));
                addProperty(returnObj, ACTION_REQUEST_PERMISSION, permission.getString(0));
                callbackContext.success(returnObj);
            } catch (JSONException e) {
                Log.i(TAG,"Received call for hasPermissionAction error for SDK " + Build.VERSION.SDK_INT);
                e.printStackTrace();
            }
        }
    }

    private void requestPermissionAction(CallbackContext callbackContext, JSONArray permissions) throws Exception {
        Log.i(TAG,"Received call for requestPermissionAction ");
        if (permissions == null || permissions.length() == 0) {
            Log.i(TAG,"Received call for requestPermissionAction with no permissions ");
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
            addProperty(returnObj, KEY_MESSAGE, "At least one permission.");
            callbackContext.error(returnObj);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(TAG,"Received call for requestPermissionAction for SDK below Android M " + Build.VERSION.SDK_INT);
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, ACTION_HAS_PERMISSION, true);
            callbackContext.success(returnObj);
        } else if (hasAllPermissions(permissions)) {
            Log.i(TAG,"Received call for requestPermissionAction but has all permissions ");
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, ACTION_HAS_PERMISSION, true);
            callbackContext.success(returnObj);
        } else {
            Log.i(TAG,"Received call for requestPermissionAction and requesting permission from user ");
            permissionsCallback = callbackContext;
            String[] permissionArray = getPermissions(permissions);
            //cordova.getActivity().requestPermissions(permissionArray,REQUEST_CODE_ENABLE_PERMISSION);

            cordova.requestPermissions(this, SEARCH_REQ_CODE, permissionArray);
        }
    }

    private String[] getPermissions(JSONArray permissions) {
        String[] stringArray = new String[permissions.length()];
        for (int i = 0; i < permissions.length(); i++) {
            try {
                stringArray[i] = permissions.getString(i);
            } catch (JSONException ignored) {
                //Believe exception only occurs when adding duplicate keys, so just ignore it
            }
        }
        return stringArray;
    }

    private boolean hasAllPermissions(JSONArray permissions) throws JSONException {
        Log.i(TAG,"Received call for hasAllPermissions ");
        return hasAllPermissions(getPermissions(permissions));
    }

    private boolean hasAllPermissions(String[] permissions) throws JSONException {

        Log.i(TAG,"Received call for hasAllPermissions for build SDK " + Build.VERSION.SDK_INT);

        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(!(cordova.getActivity().checkSelfPermission(permission)== PackageManager.PERMISSION_GRANTED)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        Log.i(TAG,"Received call for hasAllPermissions and returning true ");

        return true;
    }

    private void addProperty(JSONObject obj, String key, Object value) {
        try {
            if (value == null) {
                obj.put(key, JSONObject.NULL);
            } else {
                obj.put(key, value);
            }
        } catch (JSONException ignored) {
            //Believe exception only occurs when adding duplicate keys, so just ignore it
        }
    }
}
