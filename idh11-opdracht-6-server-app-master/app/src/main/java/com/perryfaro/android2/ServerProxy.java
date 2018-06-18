package com.perryfaro.android2;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ServerProxy extends AsyncTask<ServerProxyParams, Void, String> {

    private AsyncResponse delegate = null;

    public void setDelegate(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(ServerProxyParams... params) {
        String urlString = params[0].urlString;
        String action = params[0].action;
        String requestJson = params[0].requestJson;
        String requestMethod = params[0].requestMethod;
        String response;

        if (requestMethod == "PUT" || requestMethod == "POST") {
            response = this.putTaskData(urlString, action, requestJson, requestMethod);
        } else {
            response = this.getTaskData(urlString, action, requestMethod);
        }
        return response;
    }

    protected void onProgressUpdate(Integer... progress) {
        Log.i("TAG", progress.toString());
    }

    protected void onPostExecute(String response) {
        // parse JSON and inform caller
        JSONObject jsonObject;


        try {
            jsonObject = new JSONObject(response);
            delegate.processFinish(jsonObject);
        } catch (JSONException ex) {
            Log.e("TAG", ex.getLocalizedMessage());
        }

    }

    public String getTaskData(String urlString, String action, String requestMethod) {
        // HttpClient client = new DefaultHttpClient();
        InputStream inputStream = null;
        int responsCode = -1;
        String response = "";
        try {
            System.out.println("Get 1");
            URL url = new URL(urlString + action);
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                // Url
                System.out.println("Get 2");
                //return null;
            }

            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setRequestMethod(requestMethod);
            httpConnection.connect();

            responsCode = httpConnection.getResponseCode();

            if (responsCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
            }
        } catch (MalformedURLException e) {
            System.out.println("Exception1");
            Log.e("TAG", e.getLocalizedMessage());
            //return null;
        } catch (IOException e) {
            System.out.println("Exception2");
            Log.e("TAG", e.getLocalizedMessage());
            //return null;
        }

        return response;
    }

    public String putTaskData(String urlString, String action, String requestJson, String requestMethod) {
        InputStream inputStream = null;
        int responsCode = -1;
        String response = "";
        try {
            URL url = new URL(urlString + action);
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                Log.i("TAG", "Geen http connectie");
            }

            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setRequestMethod(requestMethod);
            httpConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(
                    httpConnection.getOutputStream());
            out.write(requestJson);
            out.close();

            httpConnection.getInputStream();
            httpConnection.connect();

            System.out.println("response is oke?");
            inputStream = httpConnection.getInputStream();
            response = getStringFromInputStream(inputStream);


        } catch (MalformedURLException e) {
            Log.e("TAG", e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e("TAG", e.getLocalizedMessage());
        }

        return response;
    }

    //
    // convert InputStream to String
    //
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }
}



