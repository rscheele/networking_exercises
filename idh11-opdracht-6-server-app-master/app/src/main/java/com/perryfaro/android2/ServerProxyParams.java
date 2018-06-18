package com.perryfaro.android2;

/**
 * Created by Frans on 16-3-2016.
 */
public class ServerProxyParams {

    String urlString;
    String action;
    String requestJson;
    String requestMethod;

    ServerProxyParams(String urlString, String action, String requestJson, String requestMethod) {
        this.urlString = urlString;
        this.action = action;
        this.requestJson = requestJson;
        this.requestMethod = requestMethod;
    }
}