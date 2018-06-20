package com.groepc.opdracht_6_client.http;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.groepc.opdracht_6_client.R;

import org.json.JSONArray;

/**
 * Created by perryfaro on 27-03-16.
 */
public class Http {


    private static Http instance = null;
    public RequestQueue requestQueue;
    private Context context = null;

    private Http(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.context = context;
    }

    public static synchronized Http getInstance(Context context) {

        if (null == instance) {
            instance = new Http(context);
        }

        return instance;
    }

    //this is so we don't need to pass context each time
    public static synchronized Http getInstance() {
        if (null == instance) {
            throw new IllegalStateException(Http.class.getSimpleName() + " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public void getJson(String url, final JsonResponseListener<JSONArray> listener) {

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        listener.getResult(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, R.string.http_no_connection, Toast.LENGTH_LONG).show();
                        //throw new RuntimeException("Connection to RandomUserAPI failed. Try again");
                    }
                });

        requestQueue.add(jsObjRequest);
    }

    public interface JsonResponseListener<T> {
        void getResult(JSONArray json);
    }

}
