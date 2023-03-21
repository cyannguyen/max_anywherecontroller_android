package com.ibm.maximo.pushnotification;

import org.json.JSONObject;

public interface IMaximoPushMessage {

    public String getMessage();
    public String getTitle();
    public String getExtraData();
}
