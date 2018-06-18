package com.perryfaro.android2;

import org.json.JSONException;
import org.json.JSONObject;

public interface AsyncResponse {
    void processFinish(JSONObject jsonObject) throws JSONException;
}
