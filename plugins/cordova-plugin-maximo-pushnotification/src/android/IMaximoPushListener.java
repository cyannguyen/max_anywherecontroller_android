package com.ibm.maximo.pushnotification;

import org.json.JSONObject;

public interface IMaximoPushListener<String> {

    public void success(String msg, JSONObject jo);
    public void failure(String msg);
}
